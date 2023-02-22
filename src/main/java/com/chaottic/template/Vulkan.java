package com.chaottic.template;

import lombok.Getter;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkApplicationInfo;
import org.lwjgl.vulkan.VkInstance;
import org.lwjgl.vulkan.VkInstanceCreateInfo;
import org.lwjgl.vulkan.VkPhysicalDevice;

import static org.lwjgl.glfw.GLFW.glfwTerminate;
import static org.lwjgl.glfw.GLFWVulkan.glfwGetRequiredInstanceExtensions;
import static org.lwjgl.vulkan.VK.getInstanceVersionSupported;
import static org.lwjgl.vulkan.VK10.*;

public final class Vulkan {
    @Getter
    private final VkInstance instance;

    private final VkPhysicalDevice device;

    {
        try (MemoryStack memoryStack = MemoryStack.stackPush()) {
            instance = createInstance(memoryStack);
            device = createPhysicalDevice(memoryStack, instance);
        }
    }

    private VkInstance createInstance(MemoryStack memoryStack) {
        var buffer = memoryStack.callocPointer(1);

        var appInfo = VkApplicationInfo.calloc(memoryStack)
                .sType$Default()
                .pApplicationName(memoryStack.UTF8("Template"))
                .applicationVersion(VK_MAKE_VERSION(1, 0, 0))
                .pEngineName(memoryStack.UTF8("Template Engine"))
                .engineVersion(VK_MAKE_VERSION(1, 0, 0))
                .apiVersion(getInstanceVersionSupported());

        var requiredInstanceExtensions = glfwGetRequiredInstanceExtensions();
        if (requiredInstanceExtensions == null) {
            glfwTerminate();
            throw new RuntimeException("Failed to get required GLFW instance extensions");
        }

        var createInfo = VkInstanceCreateInfo.calloc(memoryStack)
                .sType$Default()
                .pApplicationInfo(appInfo)
                .ppEnabledLayerNames(null)
                .ppEnabledExtensionNames(requiredInstanceExtensions);

        var error = vkCreateInstance(createInfo, null, buffer);
        if (error != VK_SUCCESS) {
            var reason = switch (error) {
                case VK_ERROR_OUT_OF_HOST_MEMORY -> "Out of host memory";
                case VK_ERROR_OUT_OF_DEVICE_MEMORY -> "Out of device memory";
                case VK_ERROR_INITIALIZATION_FAILED -> "Initialization failed";
                case VK_ERROR_LAYER_NOT_PRESENT -> "Layer not present";
                case VK_ERROR_EXTENSION_NOT_PRESENT -> "Extension not present";
                case VK_ERROR_INCOMPATIBLE_DRIVER -> "Incompatible driver";

                default -> "Unspecified";
            };
            glfwTerminate();
            throw new RuntimeException("Failed to create a Vulkan instance: %s".formatted(reason));
        }

        return new VkInstance(buffer.get(0), createInfo);
    }

    private VkPhysicalDevice createPhysicalDevice(MemoryStack memoryStack, VkInstance instance) {
        // TODO
        return null;
    }

    public void destroy() {
        vkDestroyInstance(instance, null);
    }
}
