package com.chaottic.template;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwVulkanSupported;

public final class Main {

    private Main() {
    }

    public static void main(String[] args) {

        if (!glfwInit()) {
            throw new RuntimeException("Failed to initialize GLFW");
        }

        if (!glfwVulkanSupported()) {
            glfwTerminate();
            throw new RuntimeException("Vulkan isn't supported");
        }

        var vulkan = new Vulkan();
        var window = new Window(vulkan);

        while (!window.shouldClose()) {
            glfwPollEvents();
        }

        window.destroy();
        vulkan.destroy();

        glfwTerminate();
    }
}
