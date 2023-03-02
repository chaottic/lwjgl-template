package com.chaottic.template;

import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkInstance;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.GLFWVulkan.glfwCreateWindowSurface;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;
import static org.lwjgl.vulkan.KHRSurface.vkDestroySurfaceKHR;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public final class Window {
    private final VkInstance instance;

    private final long window;
    private final long surface;

    public Window(Vulkan vulkan) {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API);

        var monitor = glfwGetPrimaryMonitor();
        var vidMode = glfwGetVideoMode(monitor);

        if (vidMode == null) {
            vulkan.destroy();
            glfwTerminate();
            throw new RuntimeException("Failed to get Video mode for primary monitor");
        }

        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            var buffer = memoryStack.callocInt(4);

            nglfwGetMonitorWorkarea(monitor,
                    memAddress(buffer),
                    memAddress(buffer) + 4,
                    memAddress(buffer) + 8,
                    memAddress(buffer) + 12);

            var width = buffer.get(2) / 2;
            var height = buffer.get(3) / 2;

            if ((window = glfwCreateWindow(width, height, "Template", NULL, NULL)) == NULL) {
                vulkan.destroy();
                glfwTerminate();
                throw new RuntimeException("Failed to create a GLFW Window");
            }

            glfwSetWindowPos(window, (buffer.get(2) - width) / 2, (buffer.get(3) - height) / 2);

            var surfaceBuffer = memoryStack.callocLong(1);

            if (glfwCreateWindowSurface(instance = vulkan.getInstance(), window, null, surfaceBuffer) != VK_SUCCESS) {
                glfwDestroyWindow(window);
                vulkan.destroy();
                glfwTerminate();
                throw new RuntimeException("Failed to create a Vulkan surface");
            }

            surface = surfaceBuffer.get();
        }
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void destroy() {
        vkDestroySurfaceKHR(instance, surface, null);
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }
}
