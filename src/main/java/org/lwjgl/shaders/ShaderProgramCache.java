package org.lwjgl.shaders;

import java.io.InputStream;
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
    }

    public Map<String, Integer> getShaderMap() {
        return shaderMap;
    }

    public int getShader(String shaderName) {
        int shader = -1;
        if (shaderName != null) {
            shader = shaderMap.get(shaderName);
        }
        if (shader == -1) {
            shader = shaderMap.get("default");
        }
        return shader;
    }

    public int createShaderProgram(String name) {
        String vertexPath = DEFAULT_PATH + name + "/vertex.glsl";
        String fragmentPath = DEFAULT_PATH + name + "/fragment.glsl";
        System.out.println("Creating shader program: " + vertexPath + "\n" + fragmentPath);

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
}
