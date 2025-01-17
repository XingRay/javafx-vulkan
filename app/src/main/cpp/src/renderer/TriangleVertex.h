//
// Created by leixing on 2024/8/3.
//

#pragma one


// 强制 GLM 所有接受角度参数的函数都使用弧度，而不是度数, 以避免任何可能的混淆。
#define GLM_FORCE_RADIANS
// The perspective projection matrix generated by GLM will use the OpenGL depth range of -1.0 to 1.0 by default.
// We need to configure it to use the Vulkan range of 0.0 to 1.0 using the GLM_FORCE_DEPTH_ZERO_TO_ONE definition.
#define GLM_FORCE_DEPTH_ZERO_TO_ONE

#include <glm/glm.hpp>
#include <glm/gtc/matrix_transform.hpp>

#define GLM_ENABLE_EXPERIMENTAL

#include <glm/gtx/hash.hpp>

#include "vulkan/vulkan.hpp"



struct TriangleVertex {
    // 位置 2d
    glm::vec2 pos;

    // 颜色
    glm::vec3 color;

    static vk::VertexInputBindingDescription getBindingDescription() {
        vk::VertexInputBindingDescription description;

        // 绑定描述
        description.binding = 0;

        // 步幅，表示每个顶点数据的字节数
        description.stride = sizeof(TriangleVertex);

        // 输入速率，表示每个顶点数据的读取频率
        description.inputRate = vk::VertexInputRate::eVertex;

        return description;
    }

    static std::array<vk::VertexInputAttributeDescription, 2> getAttributeDescriptions() {
        std::array<vk::VertexInputAttributeDescription, 2> attributeDescriptions{};

        // 位置
        attributeDescriptions[0].binding = 0;
        attributeDescriptions[0].location = 0;
        attributeDescriptions[0].format = vk::Format::eR32G32Sfloat;
        attributeDescriptions[0].offset = offsetof(TriangleVertex, pos);

        // 颜色
        attributeDescriptions[1].binding = 0;
        attributeDescriptions[1].location = 1;
        attributeDescriptions[1].format = vk::Format::eR32G32B32Sfloat;
        attributeDescriptions[1].offset = offsetof(TriangleVertex, color);

        return attributeDescriptions;
    }

    bool operator==(const TriangleVertex &other) const {
        return pos == other.pos && color == other.color;
    }
};