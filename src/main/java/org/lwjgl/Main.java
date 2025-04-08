package org.lwjgl;

import org.lwjgl.UI.HexEditor;
import org.lwjgl.UI.ImGuiManager;
import org.lwjgl.UI.TestWindow;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;

import java.io.File;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;


import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

public class Main {
    private long window;
    private int width;
    private int height;
    private long oldTime;
    private int shaderProgram;
    private InputHandler inputHandler;
    private Scene scene;
    private FloatBuffer matrixBuffer;
    private ImGuiManager imGuiManager;
    private TestWindow testWindow;
    private HexEditor hexEditor;
    private Map<String, Integer> shaderMap;

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public Main() {
        // Constructor for initialization if needed (currently empty)
    }

    public void run() {
        init();
        loop();
        cleanup();
    }

    private void init() {
        // Initialize GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        //Monitor size
        glfwWindowHint(GLFW_MAXIMIZED, GLFW_TRUE);
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

        width = vidmode.width();
        height = vidmode.height();

        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        window = glfwCreateWindow(width, height, "LWJGL Camera Demo", NULL, NULL);
        if (window == NULL) {
            glfwTerminate();
            throw new RuntimeException("Failed to create GLFW window");
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        int[] arrWidth = new int[1];
        int[] arrHeight = new int[1];
        glfwGetFramebufferSize(window, arrWidth, arrHeight);
        width = arrWidth[0];
        height = arrHeight[0];

        // Set up OpenGL
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        glViewport(0, 0, width, height);

        // Initialize camera and scene
        inputHandler = new InputHandler(window, width, height);

        scene = new Scene(width, height, inputHandler);

        //UI
        try {
            imGuiManager = new ImGuiManager(window);

            testWindow = new TestWindow(imGuiManager, scene, inputHandler);
            hexEditor = new HexEditor(imGuiManager, scene, inputHandler);
            imGuiManager.addWindow(testWindow);
            imGuiManager.addWindow(hexEditor);
        }
        catch (Exception e) {
            System.err.println("Failed to load imGuiManager: " + e.getMessage());
            e.printStackTrace();
        }

        // Create and compile shaders
        shaderMap = new ShaderProgramCache().getShaderMap();
        shaderProgram = shaderMap.get("default");

        matrixBuffer = BufferUtils.createFloatBuffer(16);

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            if (width > 0 && height > 0) {
                glViewport(0, 0, width, height);
                scene.getCamera().updateProjection(width, height);
                if (imGuiManager != null) imGuiManager.resize(width, height);
            }
        });
    }

    private void loop() {
        IntBuffer widthBuf = BufferUtils.createIntBuffer(1);
        IntBuffer heightBuf = BufferUtils.createIntBuffer(1);

        while (!glfwWindowShouldClose(window)) {


            glfwGetFramebufferSize(window, widthBuf, heightBuf);
            int width = widthBuf.get(0);
            int height = heightBuf.get(0);

            if (width <= 0 || height <= 0) {
                // Skip rendering if size is invalid (e.g., minimized)
                glfwPollEvents();
                continue;
            }

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            long time = System.currentTimeMillis();
            float deltaTime = (time - oldTime) / 1000f;
            oldTime = time;

            // Render scene
            scene.render(shaderProgram);

            // Update camera and scene
            inputHandler.update(width, height);
            scene.getCamera().update(inputHandler);
            scene.update(deltaTime);



            // Set shader uniforms
            glUseProgram(shaderProgram);
            int projLoc = glGetUniformLocation(shaderProgram, "projection");
            glUniformMatrix4fv(projLoc, false, scene.getCamera().getProjectionMatrix().get(matrixBuffer)); matrixBuffer.rewind();
            int viewLoc = glGetUniformLocation(shaderProgram, "view");
            glUniformMatrix4fv(viewLoc, false, scene.getCamera().getViewMatrix().get(matrixBuffer)); matrixBuffer.rewind();



            //UI
            if (testWindow != null) {
            }

            if (hexEditor != null) {

            }

            if (imGuiManager != null) {
                imGuiManager.update(deltaTime, scene);
            }

            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void cleanup() {
        scene.cleanup();
        glDeleteProgram(shaderProgram);
        glfwDestroyWindow(window);
        glfwTerminate();
    }


}