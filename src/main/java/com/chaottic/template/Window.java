package com.chaottic.template;

import org.lwjgl.system.MemoryStack;

import java.util.Objects;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAddress;

public final class Window {
    private final long window;

    {
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 4);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 6);
        glfwWindowHint(GLFW_CONTEXT_RELEASE_BEHAVIOR, GLFW_RELEASE_BEHAVIOR_FLUSH);

        var monitor = glfwGetPrimaryMonitor();
        var vidMode = Objects.requireNonNull(glfwGetVideoMode(monitor));

        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            var buffer = memoryStack.callocInt(4);

            nglfwGetMonitorWorkarea(monitor, memAddress(buffer), memAddress(buffer) + 4, memAddress(buffer) + 8, memAddress(buffer) + 12);

            var width = buffer.get(2) / 2;
            var height = buffer.get(3) / 2;

            if ((window = glfwCreateWindow(width, height, "Template", NULL, NULL)) == NULL) {
                throw new RuntimeException();
            }

            glfwSetWindowPos(window, (buffer.get(2) - width) / 2, (buffer.get(3) - height) / 2);

            glfwMakeContextCurrent(window);
        }
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public void destroy() {
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
    }
}
