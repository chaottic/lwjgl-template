package com.chaottic.template;

import static org.lwjgl.glfw.GLFW.*;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {

        if (!glfwInit()) {
            throw new RuntimeException();
        }

        var window = new Window();

        while (!window.shouldClose()) {
            window.swapBuffers();
            glfwPollEvents();
        }

        window.destroy();

        glfwTerminate();
    }
}
