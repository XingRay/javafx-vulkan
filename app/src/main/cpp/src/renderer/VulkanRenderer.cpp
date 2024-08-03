//
// Created by leixing on 2024/7/31.
//

#include "VulkanRenderer.h"
#include "CommonLog.h"

#include "vulkan/vulkan.hpp"

#include <set>
#include <string>
#include <unordered_map>

#include "vulkan/QueueFamilyIndices.h"
#include "FileUtil.h"

// https://github.com/nothings/stb/issues/917
// #define STB_IMAGE_IMPLEMENTATION 不能放在头文件中， 必须放在源文件中
#define STB_IMAGE_IMPLEMENTATION

#include <stb_image.h>

//#define STB_IMAGE_WRITE_IMPLEMENTATION
//#include "stb_image_write.h"

//#define STB_IMAGE_RESIZE_IMPLEMENTATION
//#include "stb_image_resize.h"

#define TINYOBJLOADER_IMPLEMENTATION

#include <tiny_obj_loader.h>

VulkanRenderer::VulkanRenderer() {
    LOG_D("renderer construct");
    initVulkan();
}

VulkanRenderer::~VulkanRenderer() {
    LOG_D("renderer deconstruct");
    cleanUp();
}

void VulkanRenderer::initVulkan() {
    LOG_D("createInstance();");
    createInstance();

    LOG_D("setupDebugMessenger();");
    setupDebugMessenger();

    LOG_D("pickPhysicalDevice();");
    pickPhysicalDevice();

    LOG_D("createLogicalDevice();");
    createLogicalDevice();

    LOG_D("createMappedImage();");
    createMappedImage();

    LOG_D("createRenderPass();");
    createRenderPass();

    LOG_D("createDescriptorSetLayout();");
//    createDescriptorSetLayout();

    LOG_D("createGraphicsPipeline();");
    createGraphicsPipeline();

    LOG_D("createCommandPool();");
    createCommandPool();

    LOG_D("createColorResources();");
//    createColorResources();

    LOG_D("createDepthResources();");
//    createDepthResources();

    LOG_D("createMappingFrameBuffer();");
    createMappingFrameBuffer();

    LOG_D("createTransferBuffer();");
    createTransferBuffer();

    LOG_D("loadTriangleModel();");
    loadTriangleModel();

    LOG_D("createTriangleVertexBuffer();");
    createTriangleVertexBuffer();

    LOG_D("createTriangleIndexBuffer();");
    createTriangleIndexBuffer();

//    LOG_D("createDescriptorSets();");
//    createDescriptorSets();

    LOG_D("createCommandBuffers();");
    createCommandBuffers();

    LOG_D("createSyncObjects();");
    createSyncObjects();
}

void VulkanRenderer::cleanUp() {
//    mDevice.destroy(mTextureSampler);
//    mDevice.destroy(mTextureImageView);

//    mDevice.destroy(mTextureImage);
//    mDevice.freeMemory(mTextureImageMemory);

    mDevice.destroy(mDescriptorPool);
    mDevice.destroy(mDescriptorSetLayout);

    mDevice.destroy(mVertexBuffer);
    mDevice.free(mVertexBufferMemory);

    mDevice.destroy(mIndexBuffer);
    mDevice.free(mIndexBufferMemory);

    mDevice.destroy(mGraphicsPipeline);
    mDevice.destroy(mPipelineLayout);

    mDevice.destroy(mRenderPass);

    cleanSyncObjects();
    mDevice.destroy(mCommandPool);

    mDevice.destroy();

    if (mEnableValidationLayer) {
        destroyDebugUtilsMessengerExt(nullptr);
    }

    mInstance.destroy();
}

void VulkanRenderer::createInstance() {
    vk::ApplicationInfo appInfo;
    appInfo.setPApplicationName("javafx-vulkan-demo");
    appInfo.setApplicationVersion(VK_MAKE_VERSION(1, 0, 0));
    appInfo.setPEngineName("vulkan renderer");
    appInfo.setEngineVersion(VK_MAKE_VERSION(1, 0, 0));
    appInfo.setApiVersion(VK_API_VERSION_1_3);

    vk::InstanceCreateInfo createInfo;
    createInfo.setFlags(createInfo.flags | vk::InstanceCreateFlagBits::eEnumeratePortabilityKHR);
    createInfo.setPApplicationInfo(&appInfo);

    std::vector<const char *> extensions = getRequiredExtensions();
    createInfo.setPEnabledExtensionNames(extensions);

    vk::DebugUtilsMessengerCreateInfoEXT debugCreateInfo{};
    if (mEnableValidationLayer) {
        if (!checkValidationLayerSupported()) {
            throw std::runtime_error("validation layers required, but not available !");
        }
        createInfo.setPEnabledLayerNames(mValidationLayers);

        populateDebugMessengerCreateInfo(debugCreateInfo);
        createInfo.pNext = &debugCreateInfo;
    } else {
        createInfo.enabledLayerCount = 0;
        createInfo.pNext = nullptr;
    }


    vk::Result result = vk::createInstance(&createInfo, nullptr, &mInstance);
    if (result != vk::Result::eSuccess) {
        throw std::runtime_error("failed to create mInstance");
    }

    // 展示所有的 vk 扩展
    uint32_t allExtensionCount = 0;
    result = vk::enumerateInstanceExtensionProperties(nullptr, &allExtensionCount, nullptr);
    if (result != vk::Result::eSuccess) {
        throw std::runtime_error("failed to get extension count");
    }
    std::vector<vk::ExtensionProperties> allExtensions(allExtensionCount);
    result = vk::enumerateInstanceExtensionProperties(nullptr, &allExtensionCount, allExtensions.data());
    if (result != vk::Result::eSuccess) {
        throw std::runtime_error("failed to get extensions");
    }
}

bool VulkanRenderer::checkValidationLayerSupported() {
    uint32_t availableLayerCount;
    vk::Result result = vk::enumerateInstanceLayerProperties(&availableLayerCount, nullptr);
    if (result != vk::Result::eSuccess) {
        throw std::runtime_error("get validation availableLayers failed !");
    }

    std::vector<vk::LayerProperties> availableLayers(availableLayerCount);
    result = vk::enumerateInstanceLayerProperties(&availableLayerCount, availableLayers.data());
    if (result != vk::Result::eSuccess) {
        throw std::runtime_error("get validation availableLayers failed !");
    }

//    for (const char *layerName: mValidationLayers) {
//        bool found = false;
//        for (const auto &layerProperty: availableLayers) {
//            if (strcmp(layerName, layerProperty.layerName) == 0) {
//                found = true;
//                break;
//            }
//        }
//
//        if (!found) {
//            return false;
//        }
//    }
//
//    return true;
    return std::all_of(mValidationLayers.begin(), mValidationLayers.end(), [&availableLayers](const char *layerName) {
        return std::find_if(availableLayers.begin(), availableLayers.end(), [layerName](const vk::LayerProperties &property) {
            return strcmp(property.layerName, layerName) == 0;
        }) != availableLayers.end();
    });
}

std::vector<const char *> VulkanRenderer::getRequiredExtensions() {
    std::vector<const char *> extensions(0);
    if (mEnableValidationLayer) {
        extensions.push_back(VK_EXT_DEBUG_UTILS_EXTENSION_NAME);
    }

    return extensions;
}

static VKAPI_ATTR VkBool32 VKAPI_CALL debugCallback(
        VkDebugUtilsMessageSeverityFlagBitsEXT messageSeverity,
        VkDebugUtilsMessageTypeFlagsEXT messageType,
        const VkDebugUtilsMessengerCallbackDataEXT *pCallbackData,
        void *pUserData) {
    return VK_FALSE;
}

void VulkanRenderer::destroyDebugUtilsMessengerExt(const vk::AllocationCallbacks *pAllocator) {
    DestroyDebugUtilsMessengerEXT(mInstance, mDebugMessenger, pAllocator);
}

void VulkanRenderer::setupDebugMessenger() {
    if (!mEnableValidationLayer) {
        return;
    }

    vk::DebugUtilsMessengerCreateInfoEXT createInfo;
    populateDebugMessengerCreateInfo(createInfo);

    if (CreateDebugUtilsMessengerEXT(mInstance, &createInfo, nullptr, &mDebugMessenger) != vk::Result::eSuccess) {
        throw std::runtime_error("failed to set up debug messenger!");
    }
}

void VulkanRenderer::pickPhysicalDevice() {
    auto devices = mInstance.enumeratePhysicalDevices();
    for (const auto &device: devices) {
        vk::PhysicalDeviceProperties properties = device.getProperties();
    }

    // 简单查找需要的设备
    for (const auto &device: devices) {
        if (isDeviceSuitable(device)) {
            mPhysicalDevice = device;
            mMsaaSamples = getMaxUsableSampleCount();
            break;
        }
    }

    if (mPhysicalDevice == VK_NULL_HANDLE) {
        throw std::runtime_error("failed to find GPUs with vulkan support !");
    }
}

bool VulkanRenderer::isDeviceSuitable(vk::PhysicalDevice device) {
    vk::PhysicalDeviceProperties deviceProperties = device.getProperties();
    vk::PhysicalDeviceFeatures deviceFeatures = device.getFeatures();

    QueueFamilyIndices indices = findQueueFamilies(device);
    if (!indices.isComplete()) {
        return false;
    }

    if (!isDeviceSupportedRequiredExtensions(device)) {
        return false;
    }

    return deviceProperties.deviceType == vk::PhysicalDeviceType::eDiscreteGpu && deviceFeatures.geometryShader && deviceFeatures.samplerAnisotropy;
}

void VulkanRenderer::createLogicalDevice() {
    QueueFamilyIndices indices = findQueueFamilies(mPhysicalDevice);
    if (!indices.isComplete()) {
        throw std::runtime_error("findQueueFamilies failed");
    }
    uint32_t graphicFamilyIndex = indices.graphicQueueFamily.value();
    uint32_t presentFamilyIndex = indices.graphicQueueFamily.value();

    std::vector<vk::DeviceQueueCreateInfo> queueCreateInfos;
    float queuePriority = 1.0f;
    if (graphicFamilyIndex == presentFamilyIndex) {
        vk::DeviceQueueCreateInfo queueCreateInfo;
        queueCreateInfo.queueFamilyIndex = graphicFamilyIndex;
        queueCreateInfo.queueCount = 1;
        queueCreateInfo.pQueuePriorities = &queuePriority;

        queueCreateInfos.push_back(queueCreateInfo);
    } else {
        std::array<uint32_t, 2> queueFamilyIndices = {graphicFamilyIndex, presentFamilyIndex};
        for (uint32_t queueFamilyIndex: queueFamilyIndices) {
            vk::DeviceQueueCreateInfo queueCreateInfo;
            queueCreateInfo.queueFamilyIndex = queueFamilyIndex;
            queueCreateInfo.queueCount = 1;
            queueCreateInfo.pQueuePriorities = &queuePriority;
            queueCreateInfos.push_back(queueCreateInfo);
        }
    }
    vk::PhysicalDeviceFeatures deviceFeatures{};
    deviceFeatures.setSamplerAnisotropy(vk::True)
            .setSampleRateShading(vk::True);

    vk::DeviceCreateInfo deviceCreateInfo;
    deviceCreateInfo.setPQueueCreateInfos(queueCreateInfos.data())
            .setQueueCreateInfoCount(queueCreateInfos.size())
            .setPEnabledFeatures(&deviceFeatures)
            .setEnabledExtensionCount(mRequiredExtensions.size())
            .setPpEnabledExtensionNames(mRequiredExtensions.data());

    if (mEnableValidationLayer) {
        deviceCreateInfo.enabledLayerCount = mValidationLayers.size();
        deviceCreateInfo.setPpEnabledLayerNames(mValidationLayers.data());
    } else {
        deviceCreateInfo.enabledLayerCount = 0;
    }

    // 创建逻辑设备的同时会根据 deviceCreateInfo.pQueueCreateInfo 创建任务队列
    mDevice = mPhysicalDevice.createDevice(deviceCreateInfo);

    // 从逻辑设备中取出任务队列, 第二个参数为下标, 总共创建了一个队列,所以这里下标为 0
    mGraphicsQueue = mDevice.getQueue(graphicFamilyIndex, 0);
}

QueueFamilyIndices VulkanRenderer::findQueueFamilies(vk::PhysicalDevice &device) {
    QueueFamilyIndices indices;
    auto queueFamilyProperties = device.getQueueFamilyProperties();


    for (int i = 0; i < queueFamilyProperties.size(); i++) {
        const auto &queueFamilyProperty = queueFamilyProperties[i];
        const vk::QueueFlags &queueFlags = queueFamilyProperty.queueFlags;

        if (queueFlags & vk::QueueFlagBits::eGraphics) {
            indices.graphicQueueFamily = i;
            // 不使用 swapChain 没有 Surface, 不测试是否支持显示到指定的 Surface 上
            indices.presentQueueFamily = i;
        }

        if (indices.isComplete()) {
            break;
        }
    }

    return indices;
}

bool VulkanRenderer::isDeviceSupportedRequiredExtensions(vk::PhysicalDevice device) {
    auto properties = device.enumerateDeviceExtensionProperties();
    std::set<std::string> requiredExtensions(mRequiredExtensions.begin(), mRequiredExtensions.end());
    for (const auto &property: properties) {
        requiredExtensions.erase(property.extensionName);
    }
    return requiredExtensions.empty();
}

void VulkanRenderer::createRenderPass() {
    vk::AttachmentDescription colorAttachmentDescription{};
    colorAttachmentDescription.setFormat(mMappingImageFormat.format)
            .setSamples(mMsaaSamples)
                    //载入图像前将帧缓冲清0
            .setLoadOp(vk::AttachmentLoadOp::eClear)
                    // 渲染图像之后将图像数据保存
            .setStoreOp(vk::AttachmentStoreOp::eStore)
                    // 模版缓冲, 这里不关注
            .setStencilLoadOp(vk::AttachmentLoadOp::eDontCare)
            .setStencilStoreOp(vk::AttachmentStoreOp::eDontCare)
                    // 常见的布局
                    //
                    //VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL：用作彩色附件的图像
                    //VK_IMAGE_LAYOUT_PRESENT_SRC_KHR：要在交换链中呈现的图像
                    //VK_IMAGE_LAYOUT_TRANSFER_DST_OPTIMAL：用作内存复制操作目标的图像
                    //
                    // initialLayout 渲染通道开始之前图像将具有的布局
                    // finalLayout 渲染通道完成时自动转换到的布局
                    //
                    // 使用 VK_IMAGE_LAYOUT_UNDEFINED 意味着我们不关心图像以前的布局
                    // 这个特殊值的警告是图像的内容不能保证被保留，但这并不重要，因为我们无论如何要清除
            .setInitialLayout(vk::ImageLayout::eUndefined)
                    // 我们希望图像在渲染后准备好使用交换链进行呈现，所以我们设置finalLayout 为 VK_IMAGE_LAYOUT_PRESENT_SRC_KHRas
//            .setFinalLayout(vk::ImageLayout::eColorAttachmentOptimal);
            .setFinalLayout(vk::ImageLayout::eTransferSrcOptimal);

    vk::AttachmentReference colorAttachmentReference{};
    // 多个 colorAttachmentDescription 组成数组, 上面只有一个 colorAttachmentDescription, 那么下标为 0
    colorAttachmentReference.setAttachment(0)
            .setLayout(vk::ImageLayout::eColorAttachmentOptimal);


//    vk::AttachmentDescription depthAttachmentDescription{};
//    depthAttachmentDescription.setFormat(findDepthFormat())
//            .setSamples(mMsaaSamples)
//            .setLoadOp(vk::AttachmentLoadOp::eClear)
//            .setStoreOp(vk::AttachmentStoreOp::eDontCare)
//            .setStencilLoadOp(vk::AttachmentLoadOp::eDontCare)
//            .setStencilStoreOp(vk::AttachmentStoreOp::eDontCare)
//            .setInitialLayout(vk::ImageLayout::eUndefined)
//            .setFinalLayout(vk::ImageLayout::eDepthStencilAttachmentOptimal);
//
//    vk::AttachmentReference depthAttachmentReference{};
//    depthAttachmentReference.setAttachment(1)
//            .setLayout(vk::ImageLayout::eDepthStencilAttachmentOptimal);

//    vk::AttachmentDescription colorAttachmentResolveDescription{};
//    colorAttachmentResolveDescription.setFormat(mMappingImageFormat.format)
//            .setSamples(vk::SampleCountFlagBits::e1)
//            .setLoadOp(vk::AttachmentLoadOp::eDontCare)
//            .setStoreOp(vk::AttachmentStoreOp::eStore)
//            .setStencilLoadOp(vk::AttachmentLoadOp::eDontCare)
//            .setStencilStoreOp(vk::AttachmentStoreOp::eDontCare)
//            .setInitialLayout(vk::ImageLayout::eUndefined)
//            .setFinalLayout(vk::ImageLayout::ePresentSrcKHR);
//
//    vk::AttachmentReference colorAttachmentResolveReference{};
//    colorAttachmentResolveReference.setAttachment(2)
//            .setLayout(vk::ImageLayout::eColorAttachmentOptimal);

    vk::SubpassDescription subpassDescription{};
    subpassDescription.setPipelineBindPoint(vk::PipelineBindPoint::eGraphics)
            .setColorAttachmentCount(1)
            .setPColorAttachments(&colorAttachmentReference);
//            .setPDepthStencilAttachment(&depthAttachmentReference)
//            .setPResolveAttachments(&colorAttachmentResolveReference);


//    vk::SubpassDependency subpassDependency{};
//    subpassDependency.setSrcSubpass(vk::SubpassExternal);
//    subpassDependency.setDstSubpass(0);
//    subpassDependency.setSrcStageMask(vk::PipelineStageFlagBits::eColorAttachmentOutput | vk::PipelineStageFlagBits::eEarlyFragmentTests);
//    subpassDependency.setSrcAccessMask(vk::AccessFlagBits::eNone);
//    subpassDependency.setDstStageMask(vk::PipelineStageFlagBits::eColorAttachmentOutput | vk::PipelineStageFlagBits::eEarlyFragmentTests);
//    subpassDependency.setDstAccessMask(vk::AccessFlagBits::eColorAttachmentWrite | vk::AccessFlagBits::eDepthStencilAttachmentWrite);

//    std::array<vk::AttachmentDescription, 3> attachments = {colorAttachmentDescription, depthAttachmentDescription, colorAttachmentResolveDescription};
    std::array<vk::AttachmentDescription, 1> attachments = {colorAttachmentDescription};//, depthAttachmentDescription, colorAttachmentResolveDescription};
    vk::RenderPassCreateInfo renderPassCreateInfo{};
    renderPassCreateInfo
//            .setAttachmentCount(attachments.size())
            .setAttachments(attachments)
            .setSubpassCount(1)
            .setPSubpasses(&subpassDescription);
//            .setDependencyCount(1)
//            .setPDependencies(&subpassDependency);

    mRenderPass = mDevice.createRenderPass(renderPassCreateInfo);
}

void VulkanRenderer::createGraphicsPipeline() {
    auto vertexShaderCode = FileUtil::readFile(VERTEX_SHADER_PATH);
    auto fragmentShaderCode = FileUtil::readFile(FRAGMENT_SHADER_PATH);

    vk::ShaderModule vertexModule = createShaderModule(vertexShaderCode);
    vk::ShaderModule fragmentModule = createShaderModule(fragmentShaderCode);

    // input assembler
    vk::PipelineInputAssemblyStateCreateInfo inputAssemblyStateCreateInfo;
    inputAssemblyStateCreateInfo.setTopology(vk::PrimitiveTopology::eTriangleList)
            .setPrimitiveRestartEnable(vk::False);

    std::vector<vk::DynamicState> dynamicStages = {
            vk::DynamicState::eViewport,
            vk::DynamicState::eScissor
    };

    vk::PipelineDynamicStateCreateInfo dynamicStateCreateInfo;
    dynamicStateCreateInfo.setDynamicStateCount(dynamicStages.size())
            .setPDynamicStates(dynamicStages.data());

    vk::Viewport viewport;
    viewport.setX(0.0f)
            .setY(0.0f)
            .setWidth((float) mMappingExtent.width)
            .setHeight((float) mMappingExtent.height)
            .setMinDepth(0.0f)
            .setMaxDepth(1.0f);

    vk::Rect2D scissor{};
    scissor.setOffset(vk::Offset2D{0, 0})
            .setExtent(mMappingExtent);

    vk::PipelineViewportStateCreateInfo viewportStateCreateInfo;
    viewportStateCreateInfo.setViewportCount(1)
            .setPViewports(&viewport)
            .setScissorCount(1)
            .setPScissors(&scissor);

    // vertex shader
    vk::VertexInputBindingDescription bindingDescription = TriangleVertex::getBindingDescription();
    std::array<vk::VertexInputAttributeDescription, 2> attributeDescriptions = TriangleVertex::getAttributeDescriptions();
    vk::PipelineVertexInputStateCreateInfo vertexInputStateCreateInfo;
    vertexInputStateCreateInfo.setPVertexBindingDescriptions(&bindingDescription)
            .setVertexBindingDescriptionCount(1)
            .setPVertexAttributeDescriptions(attributeDescriptions.data())
            .setVertexAttributeDescriptionCount(attributeDescriptions.size());
//            .setVertexAttributeDescriptions(attributeDescriptions);

    vk::PipelineShaderStageCreateInfo vertexShaderStageCreateInfo;
    vertexShaderStageCreateInfo.setStage(vk::ShaderStageFlagBits::eVertex)
            .setModule(vertexModule)
            .setPName("main")
            .setPSpecializationInfo(nullptr);

    // tessellation

    // geometry shader

    // rasterization
    vk::PipelineRasterizationStateCreateInfo rasterizationStateCreateInfo;

    // 如果depthClampEnable设置为VK_TRUE，则超出近平面和远平面的片段将被夹紧，而不是丢弃它们。这在某些特殊情况下很有用，例如阴影贴图。使用此功能需要启用 GPU 功能。
    rasterizationStateCreateInfo.setDepthClampEnable(vk::False)
                    // 如果rasterizerDiscardEnable设置为VK_TRUE，则几何图形永远不会通过光栅化阶段。这基本上禁用了帧缓冲区的任何输出。
            .setRasterizerDiscardEnable(vk::False)
                    //确定polygonMode如何为几何体生成片段。可以使用以下模式：
                    //VK_POLYGON_MODE_FILL：用碎片填充多边形区域
                    //VK_POLYGON_MODE_LINE：多边形边缘绘制为线
                    //VK_POLYGON_MODE_POINT：多边形顶点绘制为点
            .setPolygonMode(vk::PolygonMode::eFill)
                    // 使用填充以外的任何模式都需要设置 lineWidth :
            .setLineWidth(1.0f)
                    // 设置面剔除策略, 这里设置为反面被剔除
            .setCullMode(vk::CullModeFlagBits::eBack)
                    // 设置正面的方向, 这设置为顺时针为正面
            .setFrontFace(vk::FrontFace::eCounterClockwise)
            .setDepthBiasEnable(vk::False)
            .setDepthBiasConstantFactor(0.0f)
            .setDepthBiasClamp(0.0f)
            .setDepthBiasSlopeFactor(0.0f);

    // depth & stencil testing
    vk::PipelineDepthStencilStateCreateInfo depthStencilStateCreateInfo;
    depthStencilStateCreateInfo.setDepthTestEnable(vk::True)
            .setDepthWriteEnable(vk::True)
            .setDepthCompareOp(vk::CompareOp::eLess)
            .setDepthBoundsTestEnable(vk::False)
            .setMinDepthBounds(0.0f)
            .setMaxDepthBounds(1.0f)
            .setStencilTestEnable(vk::False)
            .setFront({})
            .setBack({});


    // Multisampling
    vk::PipelineMultisampleStateCreateInfo multisampleStateCreateInfo;
    multisampleStateCreateInfo.setSampleShadingEnable(vk::True)
            .setRasterizationSamples(mMsaaSamples)
            .setMinSampleShading(0.2f)
            .setPSampleMask(nullptr)
            .setAlphaToCoverageEnable(vk::False)
            .setAlphaToOneEnable(vk::False);

    // fragment shader
    vk::PipelineShaderStageCreateInfo fragmentShaderStageCreateInfo;
    fragmentShaderStageCreateInfo.setStage(vk::ShaderStageFlagBits::eFragment)
            .setModule(fragmentModule)
            .setPName("main")
            .setPSpecializationInfo(nullptr);

    // color blending
    vk::PipelineColorBlendAttachmentState colorBlendAttachmentState{};
    colorBlendAttachmentState.setColorWriteMask(vk::ColorComponentFlagBits::eR
                                                | vk::ColorComponentFlagBits::eG
                                                | vk::ColorComponentFlagBits::eB
                                                | vk::ColorComponentFlagBits::eA);
//    colorBlendAttachmentState.setBlendEnable(vk::False);
//
//    // rgb = src.rgb*1 + dst.rgb*0
//    colorBlendAttachmentState.setSrcColorBlendFactor(vk::BlendFactor::eOne);
//    colorBlendAttachmentState.setDstColorBlendFactor(vk::BlendFactor::eZero);
//    colorBlendAttachmentState.setColorBlendOp(vk::BlendOp::eAdd);
//
//    // a = src.a*1+dst.a*1
//    colorBlendAttachmentState.setSrcAlphaBlendFactor(vk::BlendFactor::eOne);
//    colorBlendAttachmentState.setDstAlphaBlendFactor(vk::BlendFactor::eZero);
//    colorBlendAttachmentState.setAlphaBlendOp(vk::BlendOp::eAdd);

    // 伪代码:
    // if (blendEnable) {
    //    finalColor.rgb = (srcColorBlendFactor * newColor.rgb) <colorBlendOp> (dstColorBlendFactor * oldColor.rgb);
    //    finalColor.a = (srcAlphaBlendFactor * newColor.a) <alphaBlendOp> (dstAlphaBlendFactor * oldColor.a);
    //} else {
    //    finalColor = newColor;
    //}
    //
    //finalColor = finalColor & colorWriteMask;

    // 常用的混合模式是透明混合
    // finalColor.rgb = newAlpha * newColor + (1 - newAlpha) * oldColor;
    // finalColor.a = newAlpha.a;
    colorBlendAttachmentState.setBlendEnable(vk::True)
            .setSrcColorBlendFactor(vk::BlendFactor::eSrcAlpha)
            .setDstColorBlendFactor(vk::BlendFactor::eOneMinusSrcAlpha)
            .setColorBlendOp(vk::BlendOp::eAdd)
            .setSrcAlphaBlendFactor(vk::BlendFactor::eOne)
            .setDstAlphaBlendFactor(vk::BlendFactor::eZero)
            .setAlphaBlendOp(vk::BlendOp::eAdd);

    vk::PipelineColorBlendStateCreateInfo colorBlendStateCreateInfo;
    colorBlendStateCreateInfo.setLogicOpEnable(vk::False)
            .setLogicOp(vk::LogicOp::eCopy)
            .setAttachmentCount(1)
            .setPAttachments(&colorBlendAttachmentState)
            .setBlendConstants(std::array<float, 4>{0.0f, 0.0f, 0.0f, 0.0f});


    vk::PipelineLayoutCreateInfo pipelineLayoutCreateInfo{};
    pipelineLayoutCreateInfo.setSetLayoutCount(1)
            .setPSetLayouts(&mDescriptorSetLayout)
            .setPushConstantRangeCount(0)
            .setPPushConstantRanges(nullptr);

    mPipelineLayout = mDevice.createPipelineLayout(pipelineLayoutCreateInfo);

    vk::PipelineShaderStageCreateInfo shaderStageCreateInfos[] = {vertexShaderStageCreateInfo, fragmentShaderStageCreateInfo};
    vk::GraphicsPipelineCreateInfo graphicsPipelineCreateInfo;
    graphicsPipelineCreateInfo.setStageCount(2)
            .setPStages(shaderStageCreateInfos)
            .setPVertexInputState(&vertexInputStateCreateInfo)
            .setPInputAssemblyState(&inputAssemblyStateCreateInfo)
            .setPViewportState(&viewportStateCreateInfo)
            .setPRasterizationState(&rasterizationStateCreateInfo)
            .setPMultisampleState(&multisampleStateCreateInfo)
            .setPDepthStencilState(&depthStencilStateCreateInfo)
            .setPColorBlendState(&colorBlendStateCreateInfo)
            .setPDynamicState(&dynamicStateCreateInfo)
            .setLayout(mPipelineLayout)
            .setRenderPass(mRenderPass)
            .setSubpass(0)
            .setBasePipelineHandle(VK_NULL_HANDLE)
            .setBasePipelineIndex(-1);

    auto [result, pipeline] = mDevice.createGraphicsPipeline(VK_NULL_HANDLE, graphicsPipelineCreateInfo);
    if (result != vk::Result::eSuccess) {
        throw std::runtime_error("createGraphicsPipelines failed");
    }
    mGraphicsPipeline = pipeline;

    mDevice.destroy(vertexModule);
    mDevice.destroy(fragmentModule);
}

vk::ShaderModule VulkanRenderer::createShaderModule(const std::vector<char> &code) {
    vk::ShaderModuleCreateInfo createInfo;
    createInfo.setCodeSize(code.size())
            .setPCode(reinterpret_cast<const uint32_t *>(code.data()));

    return mDevice.createShaderModule(createInfo);
}

void VulkanRenderer::createCommandPool() {
    QueueFamilyIndices queueFamilyIndices = findQueueFamilies(mPhysicalDevice);

    vk::CommandPoolCreateInfo commandPoolCreateInfo{};
    commandPoolCreateInfo.setFlags(vk::CommandPoolCreateFlagBits::eResetCommandBuffer)
            .setQueueFamilyIndex(queueFamilyIndices.graphicQueueFamily.value());

    mCommandPool = mDevice.createCommandPool(commandPoolCreateInfo);
}

void VulkanRenderer::createCommandBuffers() {
    vk::CommandBufferAllocateInfo commandBufferAllocateInfo;
    commandBufferAllocateInfo.setCommandPool(mCommandPool)
            .setLevel(vk::CommandBufferLevel::ePrimary)
            .setCommandBufferCount(MAX_FRAMES_IN_FLIGHT);

    // 返回 vector<CommandBuffer>, 取 [0]
    mCommandBuffers = mDevice.allocateCommandBuffers(commandBufferAllocateInfo);
    // mCommandBuffer 当 CommandPool 被销毁时会自动销毁, 不需要手动销毁
}

void VulkanRenderer::recordCommandBuffer(const vk::CommandBuffer &commandBuffer, vk::Framebuffer &framebuffer) {
    vk::CommandBufferBeginInfo commandBufferBeginInfo;
//    commandBufferBeginInfo.setFlags(vk::CommandBufferUsageFlagBits::eOneTimeSubmit);
//    commandBufferBeginInfo.setPInheritanceInfo(nullptr);
    commandBuffer.begin(commandBufferBeginInfo);

    vk::Rect2D renderArea;
    renderArea.setOffset(vk::Offset2D{0, 0})
            .setExtent(mMappingExtent);

    vk::RenderPassBeginInfo renderPassBeginInfo{};
    renderPassBeginInfo.setRenderPass(mRenderPass)
            .setFramebuffer(framebuffer)
            .setRenderArea(renderArea);

    vk::ClearValue colorClearValue = vk::ClearValue{mClearColor};
    std::array<float, 4> depthStencil = {1.0f, 0, 0, 0};
    vk::ClearValue depthStencilClearValue = vk::ClearValue{vk::ClearColorValue(depthStencil)};
    std::array<vk::ClearValue, 2> clearValues = {colorClearValue, depthStencilClearValue};
    renderPassBeginInfo.setClearValueCount(clearValues.size())
            .setClearValues(clearValues);

    commandBuffer.beginRenderPass(&renderPassBeginInfo, vk::SubpassContents::eInline);
    {
        vk::Viewport viewport;
        viewport.setX(0.0f)
                .setY(0.0f)
                .setWidth((float) mMappingExtent.width)
                .setHeight((float) mMappingExtent.height)
                .setMinDepth(0.0f)
                .setMaxDepth(1.0f);

        vk::Rect2D scissor{};
        scissor.setOffset(vk::Offset2D{0, 0})
                .setExtent(mMappingExtent);

        vk::Buffer vertexBuffers[] = {mVertexBuffer};
        vk::DeviceSize offsets[] = {0};

        commandBuffer.bindPipeline(vk::PipelineBindPoint::eGraphics, mGraphicsPipeline);
        commandBuffer.setViewport(0, 1, &viewport);
        commandBuffer.setScissor(0, 1, &scissor);
        commandBuffer.bindVertexBuffers(0, vertexBuffers, offsets);
        commandBuffer.bindIndexBuffer(mIndexBuffer, 0, vk::IndexType::eUint32);
        // vertexCount：即使我们没有顶点缓冲区，从技术上讲我们仍然有 3 个顶点要绘制。
        // instanceCount：用于实例渲染，1如果您不这样做，请使用。
        // firstVertex：用作顶点缓冲区的偏移量，定义 的最小值gl_VertexIndex。
        // firstInstance：用作实例渲染的偏移量，定义 的最小值gl_InstanceIndex。
//        commandBuffer.draw(mVertices.size(), 1, 0, 0);
//        commandBuffer.bindDescriptorSets(vk::PipelineBindPoint::eGraphics, mPipelineLayout, 0, {mDescriptorSets[mCurrentFrame]}, nullptr);
        commandBuffer.drawIndexed(mTriangleIndices.size(), 1, 0, 0, 0);
    }

    commandBuffer.endRenderPass();

    resolveMultisampledImage(mMappingImage, mResolvedImage, mMappingExtent.width, mMappingExtent.height);

    commandBuffer.end();
}

void VulkanRenderer::resolveMultisampledImage(vk::Image srcImage, vk::Image dstImage, uint32_t width, uint32_t height) {
    vk::CommandBuffer commandBuffer = beginSingleTimeCommands();

    vk::ImageResolve resolveRegion = {};
    resolveRegion.srcSubresource.setAspectMask(vk::ImageAspectFlagBits::eColor)
            .setMipLevel(0)
            .setBaseArrayLayer(0)
            .setLayerCount(1);

    resolveRegion.setSrcOffset({0, 0, 0});

    resolveRegion.dstSubresource.setAspectMask(vk::ImageAspectFlagBits::eColor)
            .setMipLevel(0)
            .setBaseArrayLayer(0)
            .setLayerCount(1);

    resolveRegion.setDstOffset({0, 0, 0});
    resolveRegion.setExtent({width, height, 1});

    commandBuffer.resolveImage(
            srcImage, vk::ImageLayout::eTransferSrcOptimal,
            dstImage, vk::ImageLayout::eTransferDstOptimal,
            1, &resolveRegion
    );

    endSingleTimeCommands(commandBuffer);
}

void VulkanRenderer::drawFrame() {
    LOG_D("VulkanRenderer::drawFrame() mDevice.waitForFences");
    vk::Result result = mDevice.waitForFences(1, &mInFlightFences[mCurrentFrame], vk::True, std::numeric_limits<uint64_t>::max());
    if (result != vk::Result::eSuccess) {
        throw std::runtime_error("waitForFences failed");
    }

    mCommandBuffers[mCurrentFrame].reset();

    LOG_D("VulkanRenderer::drawFrame() recordCommandBuffer");
    recordCommandBuffer(mCommandBuffers[mCurrentFrame], mMappingFrameBuffer);

    vk::SubmitInfo submitInfo{};

    vk::Semaphore waitSemaphores[] = {mImageAvailableSemaphores[mCurrentFrame]};
    vk::PipelineStageFlags waitStages[] = {vk::PipelineStageFlagBits::eColorAttachmentOutput};
    submitInfo.setWaitSemaphoreCount(1);
    submitInfo.setPWaitSemaphores(waitSemaphores);
    submitInfo.setPWaitDstStageMask(waitStages);
    submitInfo.setCommandBufferCount(1);
    submitInfo.setPCommandBuffers(&mCommandBuffers[mCurrentFrame]);

    vk::Semaphore signalSemaphores[] = {mRenderFinishedSemaphores[mCurrentFrame]};
    submitInfo.setSignalSemaphoreCount(1);
    submitInfo.setPSignalSemaphores(signalSemaphores);

    // 仅在我们提交工作时重置栅栏
    result = mDevice.resetFences(1, &mInFlightFences[mCurrentFrame]);
    if (result != vk::Result::eSuccess) {
        throw std::runtime_error("resetFences failed");
    }

    LOG_D("VulkanRenderer::drawFrame() mGraphicsQueue.submit");
    result = mGraphicsQueue.submit(1, &submitInfo, mInFlightFences[mCurrentFrame]);
    if (result != vk::Result::eSuccess) {
        throw std::runtime_error("graphicQueue.submit failed");
    }

    mCurrentFrame = (mCurrentFrame + 1) % MAX_FRAMES_IN_FLIGHT;
}

void VulkanRenderer::createSyncObjects() {
    vk::SemaphoreCreateInfo semaphoreCreateInfo;

    vk::FenceCreateInfo fenceCreateInfo;
    // 已发出信号的状态下创建栅栏，以便第一次调用 vkWaitForFences()立即返回
    fenceCreateInfo.flags = vk::FenceCreateFlagBits::eSignaled;

    mImageAvailableSemaphores.resize(MAX_FRAMES_IN_FLIGHT);
    mRenderFinishedSemaphores.resize(MAX_FRAMES_IN_FLIGHT);
    mInFlightFences.resize(MAX_FRAMES_IN_FLIGHT);
    for (int i = 0; i < MAX_FRAMES_IN_FLIGHT; i++) {
        mImageAvailableSemaphores[i] = mDevice.createSemaphore(semaphoreCreateInfo);
        mRenderFinishedSemaphores[i] = mDevice.createSemaphore(semaphoreCreateInfo);
        mInFlightFences[i] = mDevice.createFence(fenceCreateInfo);
    }
}

void VulkanRenderer::cleanSyncObjects() {
    for (int i = 0; i < MAX_FRAMES_IN_FLIGHT; i++) {
        mDevice.destroy(mImageAvailableSemaphores[i]);
        mDevice.destroy(mRenderFinishedSemaphores[i]);
        mDevice.destroy(mInFlightFences[i]);
    }
}

uint32_t VulkanRenderer::findMemoryType(uint32_t typeFilter, vk::MemoryPropertyFlags properties) {
    vk::PhysicalDeviceMemoryProperties memoryProperties = mPhysicalDevice.getMemoryProperties();

    for (int i = 0; i < memoryProperties.memoryTypeCount; i++) {
        if (typeFilter & (1 << i) && ((memoryProperties.memoryTypes[i].propertyFlags & properties) == properties)) {
            return i;
        }
    }

    throw std::runtime_error("failed to find suitable memory type !");
}

std::pair<vk::Buffer, vk::DeviceMemory> VulkanRenderer::createBuffer(vk::DeviceSize size, vk::BufferUsageFlags usage, vk::MemoryPropertyFlags properties) {
    vk::BufferCreateInfo bufferCreateInfo{};
    bufferCreateInfo.setSize(size)
            .setUsage(usage)
            .setSharingMode(vk::SharingMode::eExclusive);

    vk::Buffer buffer = mDevice.createBuffer(bufferCreateInfo);
    vk::MemoryRequirements memoryRequirements = mDevice.getBufferMemoryRequirements(buffer);

    uint32_t memoryType = findMemoryType(memoryRequirements.memoryTypeBits, properties);
    vk::MemoryAllocateInfo memoryAllocateInfo{};
    memoryAllocateInfo.setAllocationSize(memoryRequirements.size)
            .setMemoryTypeIndex(memoryType);

    vk::DeviceMemory bufferMemory = mDevice.allocateMemory(memoryAllocateInfo);
    mDevice.bindBufferMemory(buffer, bufferMemory, 0);

    return {buffer, bufferMemory};
}

void VulkanRenderer::copyBuffer(vk::Buffer srcBuffer, vk::Buffer dstBuffer, vk::DeviceSize size) {
    vk::CommandBuffer commandBuffer = beginSingleTimeCommands();
    {
        vk::BufferCopy bufferCopy;
        bufferCopy.setSrcOffset(0)
                .setDstOffset(0)
                .setSize(size);

        commandBuffer.copyBuffer(srcBuffer, dstBuffer, bufferCopy);
    }
    endSingleTimeCommands(commandBuffer);
}


std::pair<vk::Image, vk::DeviceMemory>
VulkanRenderer::createImage(uint32_t width, uint32_t height, uint32_t mipLevels, vk::SampleCountFlagBits numSamples,
                            vk::Format format, vk::ImageTiling imageTiling, vk::ImageUsageFlags imageUsage, vk::MemoryPropertyFlags memoryProperty) {
//    vk::DeviceSize imageSize = textureWidth * textureHeight * 4;

    vk::Extent3D extent;
    extent.setWidth(width)
            .setHeight(height)
            .setDepth(1);

    vk::ImageCreateInfo imageCreateInfo;
    imageCreateInfo.setImageType(vk::ImageType::e2D)
            .setExtent(extent)
            .setMipLevels(mipLevels)
            .setArrayLayers(1)
            .setFormat(format)
            .setTiling(imageTiling)
            .setInitialLayout(vk::ImageLayout::eUndefined)
            .setUsage(imageUsage)
            .setSharingMode(vk::SharingMode::eExclusive)
            .setSamples(numSamples)
            .setFlags(static_cast<vk::ImageCreateFlags>(0));

    vk::Image image = mDevice.createImage(imageCreateInfo);

    vk::MemoryRequirements memoryRequirements = mDevice.getImageMemoryRequirements(image);

    vk::MemoryAllocateInfo memoryAllocateInfo;
    uint32_t memoryType = findMemoryType(memoryRequirements.memoryTypeBits, memoryProperty);
    memoryAllocateInfo.setAllocationSize(memoryRequirements.size)
            .setMemoryTypeIndex(memoryType);

    vk::DeviceMemory imageMemory = mDevice.allocateMemory(memoryAllocateInfo);

    mDevice.bindImageMemory(image, imageMemory, 0);

    return {image, imageMemory};
}

vk::CommandBuffer VulkanRenderer::beginSingleTimeCommands() {
    vk::CommandBufferAllocateInfo commandBufferAllocateInfo{};
    commandBufferAllocateInfo.setLevel(vk::CommandBufferLevel::ePrimary)
            .setCommandPool(mCommandPool)
            .setCommandBufferCount(1);

    vk::CommandBuffer commandBuffer = mDevice.allocateCommandBuffers(commandBufferAllocateInfo)[0];

    vk::CommandBufferBeginInfo commandBufferBeginInfo{};
    commandBufferBeginInfo.setFlags(vk::CommandBufferUsageFlagBits::eOneTimeSubmit);
    commandBuffer.begin(commandBufferBeginInfo);

    return commandBuffer;
}

void VulkanRenderer::endSingleTimeCommands(vk::CommandBuffer &commandBuffer) {
    commandBuffer.end();

    vk::SubmitInfo submitInfo{};
    submitInfo.setCommandBufferCount(1)
            .setPCommandBuffers(&commandBuffer);

    mGraphicsQueue.submit(submitInfo);
    mGraphicsQueue.waitIdle();

    mDevice.freeCommandBuffers(mCommandPool, commandBuffer);
}

void VulkanRenderer::transitionImageLayout(vk::Image image, vk::Format format, vk::ImageLayout oldImageLayout, vk::ImageLayout newImageLayout, uint32_t mipLevels) {
    vk::CommandBuffer commandBuffer = beginSingleTimeCommands();
    {
        vk::ImageSubresourceRange imageSubresourceRange;
        imageSubresourceRange
                .setBaseMipLevel(0)
                .setLevelCount(mipLevels)
                .setBaseArrayLayer(0)
                .setLayerCount(1);

        // 注意这里一定要是 vk::ImageLayout::eDepthStencilAttachmentOptimal ， 写成了 vk::ImageLayout::eStencilAttachmentOptimal 后面会报警告
        if (newImageLayout == vk::ImageLayout::eDepthStencilAttachmentOptimal) {
            if (hasStencilComponent(format)) {
                imageSubresourceRange.setAspectMask(vk::ImageAspectFlagBits::eDepth | vk::ImageAspectFlagBits::eStencil);
            } else {
                imageSubresourceRange.setAspectMask(vk::ImageAspectFlagBits::eDepth);
            }
        } else {
            imageSubresourceRange.setAspectMask(vk::ImageAspectFlagBits::eColor);
        }


        vk::ImageMemoryBarrier imageMemoryBarrier;
        imageMemoryBarrier.setOldLayout(oldImageLayout)
                .setNewLayout(newImageLayout)
                .setSrcQueueFamilyIndex(vk::QueueFamilyIgnored)
                .setDstQueueFamilyIndex(vk::QueueFamilyIgnored)
                .setImage(image)
                .setSubresourceRange(imageSubresourceRange);
//                .setSrcAccessMask(static_cast<vk::AccessFlags>(0))
//                .setDstAccessMask(static_cast<vk::AccessFlags>(0));

        vk::PipelineStageFlags sourceStage;
        vk::PipelineStageFlags destinationStage;

        if (oldImageLayout == vk::ImageLayout::eUndefined && newImageLayout == vk::ImageLayout::eTransferDstOptimal) {
            imageMemoryBarrier.setSrcAccessMask(static_cast<vk::AccessFlags>(0))
                    .setDstAccessMask(vk::AccessFlagBits::eTransferWrite);

            sourceStage = vk::PipelineStageFlagBits::eTopOfPipe;
            destinationStage = vk::PipelineStageFlagBits::eTransfer;
        } else if (oldImageLayout == vk::ImageLayout::eTransferDstOptimal && newImageLayout == vk::ImageLayout::eShaderReadOnlyOptimal) {
            imageMemoryBarrier.setSrcAccessMask(vk::AccessFlagBits::eTransferWrite)
                    .setDstAccessMask(vk::AccessFlagBits::eShaderRead);

            sourceStage = vk::PipelineStageFlagBits::eTransfer;
            destinationStage = vk::PipelineStageFlagBits::eFragmentShader;
        } else if (oldImageLayout == vk::ImageLayout::eUndefined && newImageLayout == vk::ImageLayout::eDepthStencilAttachmentOptimal) {
            imageMemoryBarrier.setSrcAccessMask(static_cast<vk::AccessFlags>(0))
                    .setDstAccessMask(vk::AccessFlagBits::eDepthStencilAttachmentRead | vk::AccessFlagBits::eDepthStencilAttachmentWrite);

            sourceStage = vk::PipelineStageFlagBits::eTopOfPipe;
            destinationStage = vk::PipelineStageFlagBits::eEarlyFragmentTests;
        } else {
            throw std::runtime_error("unsupported layout transition!");
        }

        commandBuffer.pipelineBarrier(sourceStage,
                                      destinationStage,
                                      static_cast<vk::DependencyFlags>(0),
                // 内存屏障
                                      {},
                // 缓冲区内存屏障
                                      {},
                // 图像内存屏障
                                      {imageMemoryBarrier});


    }
    endSingleTimeCommands(commandBuffer);
}

void VulkanRenderer::copyBufferToImage(vk::Buffer buffer, vk::Image image, uint32_t width, uint32_t height) {
    vk::CommandBuffer commandBuffer = beginSingleTimeCommands();

    vk::ImageSubresourceLayers imageSubresourceLayers;
    imageSubresourceLayers.setAspectMask(vk::ImageAspectFlagBits::eColor)
            .setMipLevel(0)
            .setBaseArrayLayer(0)
            .setLayerCount(1);

    vk::Offset3D offset{0, 0, 0};
    vk::Extent3D extent{width, height, 1};

    vk::BufferImageCopy bufferImageCopy;
    bufferImageCopy.setBufferOffset(0)
            .setBufferRowLength(0)
            .setBufferImageHeight(0)
            .setImageSubresource(imageSubresourceLayers)
            .setImageOffset(offset)
            .setImageExtent(extent);

    commandBuffer.copyBufferToImage(buffer, image, vk::ImageLayout::eTransferDstOptimal, {bufferImageCopy});

    endSingleTimeCommands(commandBuffer);
}

vk::ImageView VulkanRenderer::createImageView(const vk::Image &image, vk::Format format, vk::ImageAspectFlags imageAspect, uint32_t mipLevels) {
    vk::ImageViewCreateInfo imageViewCreateInfo;

    imageViewCreateInfo.setImage(image)
            .setViewType(vk::ImageViewType::e2D)
            .setFormat(format);
//            .setSubresourceRange(imageSubresourceRange)
//            .setComponents(componentMapping);

    vk::ImageSubresourceRange &imageSubresourceRange = imageViewCreateInfo.subresourceRange;
    imageSubresourceRange.setAspectMask(imageAspect)
            .setBaseMipLevel(0)
            .setLevelCount(mipLevels)
            .setBaseArrayLayer(0)
            .setLayerCount(1);

    vk::ComponentMapping &componentMapping = imageViewCreateInfo.components;
    componentMapping.setR(vk::ComponentSwizzle::eIdentity)
            .setG(vk::ComponentSwizzle::eIdentity)
            .setB(vk::ComponentSwizzle::eIdentity)
            .setA(vk::ComponentSwizzle::eIdentity);

    return mDevice.createImageView(imageViewCreateInfo);
}

vk::Format VulkanRenderer::findSupportedFormat(const std::vector<vk::Format> &candidates, vk::ImageTiling tiling, vk::FormatFeatureFlags features) {
    for (const auto &format: candidates) {
        vk::FormatProperties properties = mPhysicalDevice.getFormatProperties(format);
        if (tiling == vk::ImageTiling::eLinear && (properties.linearTilingFeatures & features) == features) {
            return format;
        } else if (tiling == vk::ImageTiling::eOptimal && (properties.optimalTilingFeatures & features) == features) {
            return format;
        }
    }

    throw std::runtime_error("failed to find supported format !");
}

vk::Format VulkanRenderer::findDepthFormat() {
    return findSupportedFormat(
            {vk::Format::eD32Sfloat, vk::Format::eD32SfloatS8Uint, vk::Format::eD24UnormS8Uint},
            vk::ImageTiling::eOptimal,
            vk::FormatFeatureFlagBits::eDepthStencilAttachment
    );
}

bool VulkanRenderer::hasStencilComponent(vk::Format format) {
    return format == vk::Format::eD32SfloatS8Uint || format == vk::Format::eD24UnormS8Uint;
}

vk::SampleCountFlagBits VulkanRenderer::getMaxUsableSampleCount() {
    vk::PhysicalDeviceProperties properties = mPhysicalDevice.getProperties();
    vk::PhysicalDeviceLimits &limits = properties.limits;

    vk::SampleCountFlags counts = limits.framebufferColorSampleCounts & limits.framebufferDepthSampleCounts;
    if (counts & vk::SampleCountFlagBits::e64) {
        return vk::SampleCountFlagBits::e64;
    } else if (counts & vk::SampleCountFlagBits::e32) {
        return vk::SampleCountFlagBits::e32;
    } else if (counts & vk::SampleCountFlagBits::e16) {
        return vk::SampleCountFlagBits::e16;
    } else if (counts & vk::SampleCountFlagBits::e8) {
        return vk::SampleCountFlagBits::e8;
    } else if (counts & vk::SampleCountFlagBits::e4) {
        return vk::SampleCountFlagBits::e4;
    } else if (counts & vk::SampleCountFlagBits::e2) {
        return vk::SampleCountFlagBits::e2;
    } else {
        return vk::SampleCountFlagBits::e1;
    }
}

void VulkanRenderer::createMappedImage() {
    vk::Format colorFormat = mMappingImageFormat.format;

    std::tie(mMappingImage, mMappingImageDeviceMemory) = createImage(mMappingExtent.width, mMappingExtent.height, 1, mMsaaSamples, colorFormat,
                                                                     vk::ImageTiling::eOptimal,
                                                                     vk::ImageUsageFlagBits::eSampled | vk::ImageUsageFlagBits::eTransferDst | vk::ImageUsageFlagBits::eColorAttachment |
                                                                     vk::ImageUsageFlagBits::eTransferSrc,
                                                                     vk::MemoryPropertyFlagBits::eDeviceLocal);

    mMappingImageView = createImageView(mMappingImage, colorFormat, vk::ImageAspectFlagBits::eColor, 1);


    std::tie(mResolvedImage, mResolvedImageDeviceMemory) = createImage(mMappingExtent.width, mMappingExtent.height, 1, vk::SampleCountFlagBits::e1, colorFormat,
                                                                       vk::ImageTiling::eOptimal,
                                                                       vk::ImageUsageFlagBits::eTransferDst | vk::ImageUsageFlagBits::eColorAttachment |
                                                                       vk::ImageUsageFlagBits::eTransferSrc,
                                                                       vk::MemoryPropertyFlagBits::eDeviceLocal);

    mResolvedImageView = createImageView(mResolvedImage, colorFormat, vk::ImageAspectFlagBits::eColor, 1);
}

void VulkanRenderer::createMappingFrameBuffer() {
    std::array<vk::ImageView, 1> attachments = {
            mMappingImageView,
    };

    vk::FramebufferCreateInfo framebufferCreateInfo{};
    framebufferCreateInfo.setRenderPass(mRenderPass)
//                .setAttachmentCount(attachments.size())
            .setAttachments(attachments)
            .setWidth(mMappingExtent.width)
            .setHeight(mMappingExtent.height)
            .setLayers(1);

    mMappingFrameBuffer = mDevice.createFramebuffer(framebufferCreateInfo);
}

void VulkanRenderer::createTransferBuffer() {
    vk::DeviceSize bufferSize = mMappingExtent.width * mMappingExtent.height * 4;

    std::tie(mTransferBuffer, mTransferBufferDeviceMemory) = createBuffer(bufferSize,
                                                                          vk::BufferUsageFlagBits::eTransferSrc | vk::BufferUsageFlagBits::eTransferDst,
                                                                          vk::MemoryPropertyFlagBits::eHostVisible);

    mMappingData = mDevice.mapMemory(mTransferBufferDeviceMemory, 0, bufferSize, static_cast<vk::MemoryMapFlags>(0)/*vk::MemoryMapFlagBits::ePlacedEXT*/);

    mMappingDataSize = bufferSize;
}

void *VulkanRenderer::getMappingData() {
    return mMappingData;
}

uint64_t VulkanRenderer::getMappingDataSize() {
    return mMappingDataSize;
}

void VulkanRenderer::copyImageToBuffer() {
    copyImageToBuffer(mResolvedImage, mTransferBuffer, mMappingExtent.width, mMappingExtent.height);
}

void VulkanRenderer::copyImageToBuffer(vk::Image image, vk::Buffer buffer, uint32_t width, uint32_t height) {
    vk::CommandBuffer commandBuffer = beginSingleTimeCommands();
    {
        vk::BufferImageCopy bufferImageCopy{};
        bufferImageCopy.setBufferOffset(0)
                .setBufferRowLength(0)
                .setBufferImageHeight(0);

        bufferImageCopy.imageSubresource.setAspectMask(vk::ImageAspectFlagBits::eColor)
                .setMipLevel(0)
                .setBaseArrayLayer(0)
                .setLayerCount(1);

        bufferImageCopy.imageExtent.setWidth(width)
                .setHeight(height)
                .setDepth(1);

        bufferImageCopy.imageOffset.setX(0)
                .setY(0)
                .setZ(0);

        commandBuffer.copyImageToBuffer(image, vk::ImageLayout::eTransferSrcOptimal, buffer, 1, &bufferImageCopy);
    }
    endSingleTimeCommands(commandBuffer);
}

void VulkanRenderer::loadTriangleModel() {
    mTriangleVertices.push_back({{-1.0, -1.0},
                                 {1.0,  0.0, 0.0}});

    mTriangleVertices.push_back({{-1.0, 1.0},
                                 {0.0,  1.0, 0.0}});

    mTriangleVertices.push_back({{1.0, 1.0},
                                 {0.0, 0.0, 1.0}});

    mTriangleVertices.push_back({{1.0, -1.0},
                                 {1.0, 1.0, 1.0}});

    mTriangleIndices.push_back(0);
    mTriangleIndices.push_back(1);
    mTriangleIndices.push_back(2);

    mTriangleIndices.push_back(2);
    mTriangleIndices.push_back(3);
    mTriangleIndices.push_back(0);
}

void VulkanRenderer::createTriangleVertexBuffer() {
    vk::DeviceSize bufferSize = sizeof(mTriangleVertices[0]) * mTriangleVertices.size();

    auto [stagingBuffer, stagingBufferMemory] = createBuffer(bufferSize, vk::BufferUsageFlagBits::eTransferSrc,
                                                             vk::MemoryPropertyFlagBits::eHostVisible | vk::MemoryPropertyFlagBits::eHostCoherent);
    void *data = mDevice.mapMemory(stagingBufferMemory, 0, bufferSize, static_cast<vk::MemoryMapFlags>(0)/*vk::MemoryMapFlagBits::ePlacedEXT*/);
    {
        memcpy(data, mTriangleVertices.data(), (size_t) bufferSize);
    }
    mDevice.unmapMemory(stagingBufferMemory);

    std::tie(mVertexBuffer, mVertexBufferMemory) = createBuffer(bufferSize,
                                                                vk::BufferUsageFlagBits::eTransferDst | vk::BufferUsageFlagBits::eVertexBuffer,
                                                                vk::MemoryPropertyFlagBits::eDeviceLocal);

    copyBuffer(stagingBuffer, mVertexBuffer, bufferSize);

    mDevice.destroy(stagingBuffer);
    mDevice.freeMemory(stagingBufferMemory);
}

void VulkanRenderer::createTriangleIndexBuffer() {
    vk::DeviceSize bufferSize = sizeof(mTriangleIndices[0]) * mTriangleIndices.size();

    auto [stagingBuffer, stagingBufferMemory] = createBuffer(bufferSize, vk::BufferUsageFlagBits::eTransferSrc,
                                                             vk::MemoryPropertyFlagBits::eHostVisible | vk::MemoryPropertyFlagBits::eHostCoherent);
    void *data = mDevice.mapMemory(stagingBufferMemory, 0, bufferSize, static_cast<vk::MemoryMapFlags>(0)/*vk::MemoryMapFlagBits::ePlacedEXT*/);
    {
        memcpy(data, mTriangleIndices.data(), (size_t) bufferSize);
    }
    mDevice.unmapMemory(stagingBufferMemory);

    std::tie(mIndexBuffer, mIndexBufferMemory) = createBuffer(bufferSize,
                                                              vk::BufferUsageFlagBits::eTransferDst | vk::BufferUsageFlagBits::eIndexBuffer,
                                                              vk::MemoryPropertyFlagBits::eDeviceLocal);

    copyBuffer(stagingBuffer, mIndexBuffer, bufferSize);

    mDevice.destroy(stagingBuffer);
    mDevice.freeMemory(stagingBufferMemory);
}

vk::Result CreateDebugUtilsMessengerEXT(vk::Instance instance,
                                        const vk::DebugUtilsMessengerCreateInfoEXT *pCreateInfo,
                                        const vk::AllocationCallbacks *pAllocator,
                                        vk::DebugUtilsMessengerEXT *pDebugMessenger) {
    auto func = (PFN_vkCreateDebugUtilsMessengerEXT) vkGetInstanceProcAddr(instance, "vkCreateDebugUtilsMessengerEXT");
    if (func == nullptr) {
        return vk::Result::eErrorExtensionNotPresent;
    }

    VkResult vkResult = func(instance, (VkDebugUtilsMessengerCreateInfoEXT *) pCreateInfo, (VkAllocationCallbacks *) pAllocator, (VkDebugUtilsMessengerEXT *) pDebugMessenger);
    if (vkResult != VK_SUCCESS) {
        throw std::runtime_error("vkCreateDebugUtilsMessengerEXT failed !");
    }
    return vk::Result::eSuccess;
}

void DestroyDebugUtilsMessengerEXT(vk::Instance instance, vk::DebugUtilsMessengerEXT debugMessenger, const vk::AllocationCallbacks *pAllocator) {
    auto func = (PFN_vkDestroyDebugUtilsMessengerEXT) vkGetInstanceProcAddr(instance, "vkDestroyDebugUtilsMessengerEXT");
    if (func == nullptr) {
        throw std::runtime_error("func vkDestroyDebugUtilsMessengerEXT not found!");
    }

    func(instance, debugMessenger, (VkAllocationCallbacks *) pAllocator);
}

void populateDebugMessengerCreateInfo(vk::DebugUtilsMessengerCreateInfoEXT &createInfo) {
    createInfo.messageSeverity = vk::DebugUtilsMessageSeverityFlagBitsEXT::eError
                                 | vk::DebugUtilsMessageSeverityFlagBitsEXT::eWarning
                                 | vk::DebugUtilsMessageSeverityFlagBitsEXT::eInfo;

    createInfo.messageType = vk::DebugUtilsMessageTypeFlagBitsEXT::eGeneral
                             | vk::DebugUtilsMessageTypeFlagBitsEXT::eValidation
                             | vk::DebugUtilsMessageTypeFlagBitsEXT::ePerformance
                             | vk::DebugUtilsMessageTypeFlagBitsEXT::eDeviceAddressBinding;
    createInfo.pfnUserCallback = debugCallback;
}