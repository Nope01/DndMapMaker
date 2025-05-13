package org.lwjgl.data;

import imgui.ImGui;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.lwjgl.glfw.GLFW.GLFW_ARROW_CURSOR;
import static org.lwjgl.glfw.GLFW.glfwSetCursor;

public class ApiCalls {

    public static String getRandomName() {
//        JFrame frame = new JFrame();
//        frame.setUndecorated(true);
//        frame.setSize(640, 480);
//        frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
//        frame.setBackground(new java.awt.Color(0, 0, 0, 0));
//        frame.setVisible(true);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://fantasyname.lukewh.com"))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;

        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

//        frame.dispose();
        return response.body();
    }
}
