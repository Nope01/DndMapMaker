package org.lwjgl;

import org.lwjgl.UI.ImGuiManager;
import org.lwjgl.UI.TestWindow;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.system.MemoryUtil.NULL;


import java.nio.charset.StandardCharsets;

public class Main {
    private long window;
    private int width;
    private int height;
    private long oldTime;
    private int shaderProgram;
    private InputHandler inputHandler;
    private Camera camera;
    private Scene scene;
    private FloatBuffer matrixBuffer;
    private ImGuiManager imGuiManager;
    private TestWindow testWindow;

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

        // Set up OpenGL
        glEnable(GL_DEPTH_TEST);
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        glViewport(0, 0, width, height);

        // Initialize camera and scene
        inputHandler = new InputHandler(window);
        camera = new Camera((float) width / (float) height);
        camera.setPosition(4, 5, 10);
        scene = new Scene();

        //UI
        try {
            imGuiManager = new ImGuiManager(window);
            testWindow = new TestWindow(imGuiManager, camera, scene);
            imGuiManager.addWindow(testWindow);
        }
        catch (Exception e) {
            System.err.println("Failed to load imGuiManager: " + e.getMessage());
            e.printStackTrace();
        }

        // Create and compile shaders
        shaderProgram = createShaderProgram();

        matrixBuffer = BufferUtils.createFloatBuffer(16);

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            if (width > 0 && height > 0) {
                glViewport(0, 0, width, height);
                camera.updateProjection((float) width / height);
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

            // Update camera and scene
            inputHandler.update();
            camera.update(inputHandler);
            scene.update(deltaTime);

            if (testWindow != null) {

            }

            if (imGuiManager != null) {
                imGuiManager.update(deltaTime, scene);
            }

            // Set shader uniforms
            glUseProgram(shaderProgram);
            int projLoc = glGetUniformLocation(shaderProgram, "projection");
            glUniformMatrix4fv(projLoc, false, camera.getProjectionMatrix().get(matrixBuffer)); matrixBuffer.rewind();
            int viewLoc = glGetUniformLocation(shaderProgram, "view");
            glUniformMatrix4fv(viewLoc, false, camera.getViewMatrix().get(matrixBuffer)); matrixBuffer.rewind();

            // Render scene
            scene.render(shaderProgram);

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

    private int createShaderProgram() {
        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, loadShaderSource("shaders/vertex.glsl"));
        glCompileShader(vertexShader);
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Vertex shader compilation failed: " + glGetShaderInfoLog(vertexShader));
        }

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, loadShaderSource("shaders/fragment.glsl"));
        glCompileShader(fragmentShader);
        if (glGetShaderi(fragmentShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Fragment shader compilation failed: " + glGetShaderInfoLog(fragmentShader));
        }

        int program = glCreateProgram();
        glAttachShader(program, vertexShader);
        glAttachShader(program, fragmentShader);
        glLinkProgram(program);
        if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
            System.err.println("Shader program linking failed: " + glGetProgramInfoLog(program));
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        return program;
    }

    private String loadShaderSource(String path) {
        try {
            java.nio.file.Path filePath = java.nio.file.Paths.get(ClassLoader.getSystemResource(path).toURI());
            byte[] bytes = java.nio.file.Files.readAllBytes(filePath);
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader: " + path, e);
        }
    }
}