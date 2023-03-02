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
            var requiredInstanceExtensions = glfwGetRequiredInstanceExtensions();
            if (requiredInstanceExtensions == null) {
                glfwTerminate();
                throw new RuntimeException("Failed to get required GLFW instance extensions");
            }

            var buffer = memoryStack.callocPointer(1);

            var appInfo = VkApplicationInfo.calloc(memoryStack)
                    .sType$Default()
                    .pApplicationName(memoryStack.UTF8("Template"))
                    .applicationVersion(VK_MAKE_VERSION(1, 0, 0))
                    .pEngineName(memoryStack.UTF8("Template Engine"))
                    .engineVersion(VK_MAKE_VERSION(1, 0, 0))
                    .apiVersion(getInstanceVersionSupported());

            var createInfo = VkInstanceCreateInfo.calloc(memoryStack)
                    .sType$Default()
                    .pApplicationInfo(appInfo)
                    .ppEnabledLayerNames(null)
                    .ppEnabledExtensionNames(requiredInstanceExtensions);

            if (vkCreateInstance(createInfo, null, buffer) != VK_SUCCESS) {
                // TODO:: proper error handeling.
                glfwTerminate();
                throw new RuntimeException("Failed to create a Vulkan instance");
            }

            instance = new VkInstance(buffer.get(0), createInfo);

            device = null;
        }
    }

    public void destroy() {
        vkDestroyInstance(instance, null);
    }
}
