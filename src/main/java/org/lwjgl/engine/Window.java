package org.lwjgl.engine;

import org.lwjgl.BufferUtils;
import org.lwjgl.Scene;
import org.lwjgl.UI.ImGuiManager;
import org.lwjgl.engine.input.InputHandler;
import org.lwjgl.opengl.GL;
import org.lwjgl.shaders.ShaderProgramCache;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFW.glfwGetFramebufferSize;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Window {

    public long handle;
    public int width;
    public int height;
    public ShaderProgramCache shaderCache;
    public String title;
    public InputHandler inputHandler;
    public Scene scene;
    public ImGuiManager imGuiManager;
    private FloatBuffer matrixBuffer;
    public float oldTime = System.currentTimeMillis();

    public Window(int width, int height, String title) {
        this.width = width;
        this.height = height;
        this.title = title;

        handle = glfwCreateWindow(width, height, title, NULL, NULL);
        if (handle == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW window");
        }
    }

    public void initMainWindow(Engine engine) {
        init();
        glClearColor(0.5f, 0.1f, 0.1f, 1.0f);

        //Init scene
        setupInputHandler();
        setupScene(shaderCache, engine);
        setupUI();

    }

    public void initSecondaryWindow(Engine engine) {
        init();
        glClearColor(0.1f, 0.1f, 0.5f, 1.0f);

        setupInputHandler();
        setupScene(shaderCache, engine);
        scene.initCombatScene();
    }

    public void init() {
        glfwMakeContextCurrent(handle);
        GL.createCapabilities();

        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetFramebufferSize(handle, arrWidth, arrHeight);
        width = arrWidth[0];
        height = arrHeight[0];

        glEnable(GL_DEPTH_TEST);
        glViewport(0, 0, width, height);

        shaderCache = new ShaderProgramCache();
        matrixBuffer = BufferUtils.createFloatBuffer(16);
    }


    public void setupInputHandler() {
        inputHandler = new InputHandler(handle, width, height);
    }
    public void setupScene(ShaderProgramCache shaderCache, Engine engine) {
        scene = new Scene(width, height, inputHandler, shaderCache, this, engine);
    }

    public void setupUI() {
        //Init UI
        try {
            imGuiManager = new ImGuiManager(handle, width, height);
            imGuiManager.initMainMenu(imGuiManager, scene, inputHandler);
        }
        catch (Exception e) {
            System.err.println("Failed to load imGuiManager: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void loop() {
        IntBuffer widthBuf = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuf = BufferUtils.createIntBuffer(1);

        glfwMakeContextCurrent(handle);
        GL.createCapabilities();

        glfwGetFramebufferSize(handle, widthBuf, heightBuf);
        int width = widthBuf.get(0);
        int height = heightBuf.get(0);

        if (width <= 0 || height <= 0) {
            // Skip rendering if size is invalid (e.g., minimized)
            glfwPollEvents();
            return;
        }

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
        long time = System.currentTimeMillis();
        float deltaTime = (time - oldTime) / 1000f;
        oldTime = time;

        // Update camera and scene
        if (inputHandler != null) {
            inputHandler.update(width, height);
        }

        if (scene != null) {
            scene.getCamera().update(inputHandler);
            scene.update(deltaTime);
            scene.render();

            //For each shader, set it as active then set uniforms
            //Either do it once here or for each sceneObject
            shaderCache.getShaderMap().values().forEach(shader -> {
                // Set shader uniforms
                glUseProgram(shader);
                int projLoc = glGetUniformLocation(shader, "projection");
                glUniformMatrix4fv(projLoc, false, scene.getCamera().getProjectionMatrix().get(matrixBuffer)); matrixBuffer.rewind();
                int viewLoc = glGetUniformLocation(shader, "view");
                glUniformMatrix4fv(viewLoc, false, scene.getCamera().getViewMatrix().get(matrixBuffer)); matrixBuffer.rewind();
            });
        }

        if (imGuiManager != null) {
            imGuiManager.update(deltaTime, scene, inputHandler);
        }

        glfwSwapBuffers(handle);
        glfwPollEvents();

    }
}
