package org.lwjgl;

import org.lwjgl.UI.ImGuiManager;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.input.InputHandler;
import org.lwjgl.opengl.GL;
import org.lwjgl.shaders.ShaderProgramCache;

import java.nio.FloatBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Main {
    private long window;
    private long dmWindow;
    private int mainWidth;
    private int mainHeight;
    private int dmWidth;
    private int dmHeight;
    private long oldTime;
    private InputHandler inputHandler;
    private InputHandler dmInputHandler;
    private Scene scene;
    private Scene dmScene;
    private FloatBuffer matrixBuffer;
    private FloatBuffer dmMatrixBuffer;
    private ImGuiManager imGuiManager;
    private ImGuiManager dmImGuiManager;
    public ShaderProgramCache shaderCache;

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
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

        if (!glfwInit()) {
            throw new IllegalStateException("Failed to initialize GLFW");
        }

        matrixBuffer = BufferUtils.createFloatBuffer(16);  // For main window
        dmMatrixBuffer = BufferUtils.createFloatBuffer(16); // For DM window

        // Get monitor resolution
        GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        mainWidth = vidmode.width();
        mainHeight = vidmode.height();
        dmWidth = vidmode.width();
        dmHeight = vidmode.height();

        // --- MAIN WINDOW SETUP ---
        window = glfwCreateWindow(mainWidth, mainHeight, "DND Map Maker", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create main window");

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        System.out.println("Main window GL: " + glGetString(GL_VERSION));

        // OpenGL state for main window
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        // Initialize main scene
        inputHandler = new InputHandler(window, mainWidth, mainHeight);
        shaderCache = new ShaderProgramCache();
        scene = new Scene(mainWidth, mainHeight, inputHandler, shaderCache, window);

        // Initialize main ImGui
        imGuiManager = new ImGuiManager(window, mainWidth, mainHeight);
        imGuiManager.initMainMenu(imGuiManager, scene, inputHandler);

        // --- DM WINDOW SETUP ---
        dmWindow = glfwCreateWindow(dmWidth, dmHeight, "DM View", NULL, window); // Share OpenGL resources
        if (dmWindow == NULL) throw new RuntimeException("Failed to create DM window");

        glfwMakeContextCurrent(dmWindow);
        GL.createCapabilities(); // Must call for new context
        System.out.println("DM window GL: " + glGetString(GL_VERSION));

        // OpenGL state for DM window
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.2f, 0.2f, 0.25f, 1.0f); // Different background

        // Initialize DM scene
        dmInputHandler = new InputHandler(dmWindow, dmWidth, dmHeight);
        dmScene = new Scene(dmWidth, dmHeight, dmInputHandler, shaderCache, dmWindow);
        //dmScene.initContinentScene();

        // Initialize DM ImGui (separate context)
        dmImGuiManager = new ImGuiManager(dmWindow, dmWidth, dmHeight);
        dmImGuiManager.initMainMenu(dmImGuiManager, dmScene, dmInputHandler); // Custom DM UI

        // Switch back to main window
        glfwMakeContextCurrent(window);

        // Resize callbacks
        glfwSetFramebufferSizeCallback(window, (w, wWidth, wHeight) -> {
            if (wWidth > 0 && wHeight > 0) {
                glViewport(0, 0, wWidth, wHeight);
                scene.getCamera().updateProjection(wWidth, wHeight);
                imGuiManager.resize(wWidth, wHeight);
                mainWidth = wWidth;
                mainHeight = wHeight;
            }
        });

        glfwSetFramebufferSizeCallback(dmWindow, (w, wWidth, wHeight) -> {
            if (wWidth > 0 && wHeight > 0) {
                glViewport(0, 0, wWidth, wHeight);
                dmScene.getCamera().updateProjection(wWidth, wHeight);
                dmImGuiManager.resize(wWidth, wHeight);
                dmWidth = wWidth;
                dmHeight = wHeight;
            }
        });
    }

    private void loop() {
        while (!glfwWindowShouldClose(window)) {
            // Update time
            long time = System.currentTimeMillis();
            float deltaTime = (time - oldTime) / 1000f;
            oldTime = time;

            glfwPollEvents();

            // --- MAIN WINDOW RENDER ---
            glfwMakeContextCurrent(window);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            inputHandler.update(mainWidth, mainHeight);
            imGuiManager.update(deltaTime, scene, inputHandler);

            //TODO: Move this to below update, requires change to highlighting timing
            scene.render();

            // Update main scene

            scene.getCamera().update(inputHandler);
            scene.update(deltaTime);

            // Set shader uniforms for main window
            shaderCache.getShaderMap().values().forEach(shader -> {
                glUseProgram(shader);
                int projLoc = glGetUniformLocation(shader, "projection");
                glUniformMatrix4fv(projLoc, false, scene.getCamera().getProjectionMatrix().get(matrixBuffer));
                matrixBuffer.rewind();
                int viewLoc = glGetUniformLocation(shader, "view");
                glUniformMatrix4fv(viewLoc, false, scene.getCamera().getViewMatrix().get(matrixBuffer));
                matrixBuffer.rewind();
            });

            // Render main ImGui

            glfwSwapBuffers(window);

            // --- DM WINDOW RENDER ---
            glfwMakeContextCurrent(dmWindow);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            dmInputHandler.update(mainWidth, mainHeight);
            dmImGuiManager.update(deltaTime, dmScene, dmInputHandler);

            // Render DM scene
            dmScene.render();

            // Update DM scene

            dmScene.getCamera().update(dmInputHandler);
            dmScene.update(deltaTime);

            // Set shader uniforms for DM window (same shaders, but DM camera)
            shaderCache.getShaderMap().values().forEach(shader -> {
                glUseProgram(shader);
                int projLoc = glGetUniformLocation(shader, "projection");
                glUniformMatrix4fv(projLoc, false, dmScene.getCamera().getProjectionMatrix().get(matrixBuffer));
                matrixBuffer.rewind();
                int viewLoc = glGetUniformLocation(shader, "view");
                glUniformMatrix4fv(viewLoc, false, dmScene.getCamera().getViewMatrix().get(matrixBuffer));
                matrixBuffer.rewind();
            });

            // Render DM ImGui

            glfwSwapBuffers(dmWindow);

            // Poll events last
            glfwPollEvents();
        }
    }

    private void cleanup() {
        scene.cleanup();
        glfwDestroyWindow(window);
        glfwTerminate();
    }
}