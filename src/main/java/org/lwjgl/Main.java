package org.lwjgl;

import org.lwjgl.UI.ImGuiManager;
import org.lwjgl.engine.Engine;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.engine.input.InputHandler;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLDebugMessageCallback;
import org.lwjgl.shaders.ShaderProgramCache;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL43.glDebugMessageCallback;
import static org.lwjgl.opengl.GL43C.GL_DEBUG_OUTPUT;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.utils.Debugging.*;

public class Main {
    public Engine engine;

    public static void main(String[] args) {
        Main app = new Main();
        app.run();
    }

    public Main() {
        // Constructor for initialization if needed (currently empty)
        engine = new Engine();
    }

    public void run() {
        engine.loop();
        engine.cleanup();
        glfwTerminate();
        System.out.println("Exiting...");
        System.exit(0);
    }
}