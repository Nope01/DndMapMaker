package org.lwjgl.shaders;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glDeleteShader;

public class ShaderProgramCache {
    Map<String, Integer> shaderMap;
    private static String DEFAULT_PATH = "shaders/";

    public ShaderProgramCache() {
        shaderMap = new HashMap<>();
        shaderMap.put("default", createShaderProgram("default"));
        shaderMap.put("continentHex", createShaderProgram("continentHex"));
        shaderMap.put("cityHex", createShaderProgram("cityHex"));
        shaderMap.put("trap", createShaderProgram("trap"));
        shaderMap.put("creature", createShaderProgram("creature"));
    }

    public Map<String, Integer> getShaderMap() {
        return shaderMap;
    }

    public int getShader(String shaderName) {
        if (!shaderMap.containsKey(shaderName)) {
            return shaderMap.get("default");
        }
        return shaderMap.get(shaderName);
    }

    public int createShaderProgram(String name) {
        String vertexPath = DEFAULT_PATH + name + "/vertex.glsl";
        String fragmentPath = DEFAULT_PATH + name + "/fragment.glsl";

//        String vertexSource = loadShaderSource(vertexPath);
//        String fragmentSource = loadShaderSource(fragmentPath);
//
//        // Debug: Print shader sources
//        System.out.println("=== Vertex Shader Source (" + name + ") ===");
//        System.out.println(vertexSource);
//        System.out.println("=== Fragment Shader Source (" + name + ") ===");
//        System.out.println(fragmentSource);

        int vertexShader = glCreateShader(GL_VERTEX_SHADER);
        glShaderSource(vertexShader, loadShaderSource(vertexPath));
        glCompileShader(vertexShader);
        if (glGetShaderi(vertexShader, GL_COMPILE_STATUS) == GL_FALSE) {
            System.err.println("Vertex shader compilation failed: " + glGetShaderInfoLog(vertexShader));
        }

        int fragmentShader = glCreateShader(GL_FRAGMENT_SHADER);
        glShaderSource(fragmentShader, loadShaderSource(fragmentPath));
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

        glValidateProgram(program);
        if (glGetProgrami(program, GL_VALIDATE_STATUS) == GL_FALSE) {
            System.err.println("Shader program validation failed: " + glGetProgramInfoLog(program));
        }

        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
        return program;
    }

    private String loadShaderSource(String path){
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream(path)){

//            File file = new File("src/main/resources/" + path);
//            System.out.println("Loading shader file: " + file.getAbsolutePath());
//            byte[] bytes = Files.readAllBytes(file.toPath());
//            System.out.println("Loading shader file: " + bytes.toString());
            //return new String(bytes, StandardCharsets.UTF_8);

//            System.out.println("");
//            System.out.println(new String(stream.readAllBytes(), StandardCharsets.UTF_8));
//            System.out.println("");
            if (stream == null) {
                throw new Exception("Cannot find file " + path);
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);

        } catch (Exception e) {
            throw new RuntimeException("Failed to load shader: " + path, e);
        }
    }

    public static void printShaderSource(int shaderID) {
        int[] length = new int[1];
        glGetShaderiv(shaderID, GL_SHADER_SOURCE_LENGTH, length);
        if (length[0] > 0) {
            String source = glGetShaderSource(shaderID);
            System.out.println("Shader Source (ID: " + shaderID + "):");
            System.out.println(source);
        } else {
            System.out.println("Shader source not available (may have been optimized out by the driver).");
        }
    }

    public static void inspectProgramShaders(int programID) {
        int[] count = new int[1];
        glGetProgramiv(programID, GL_ATTACHED_SHADERS, count);

        if (count[0] == 0) {
            System.out.println("No shaders attached to program " + programID);
            return;
        }

        int[] shaders = new int[count[0]];
        glGetAttachedShaders(programID, count, shaders);

        System.out.println("Shaders attached to program " + programID + ":");
        for (int shader : shaders) {
            int shaderType = glGetShaderi(shader, GL_SHADER_TYPE);
            String typeName = (shaderType == GL_VERTEX_SHADER) ? "Vertex" : "Fragment";
            System.out.println("- " + typeName + " Shader (ID: " + shader + ")");
            printShaderSource(shader); // Attempt to print source (if available)
        }
    }
}
