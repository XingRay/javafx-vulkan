//
// Created by leixing on 2024/7/31.
//
#include <jni.h>
#include "VulkanRenderer.h"
#include "JniUtil.h"
#include "CommonLog.h"

extern "C"
JNIEXPORT jlong JNICALL
Java_com_github_xingray_renderer_VulkanRenderer_newVulkanRenderer(JNIEnv *env,
                                                                  jobject thiz) {
    LOG_D("Java_com_github_xingray_renderer_VulkanRenderer_newVulkanRenderer");
    VulkanRenderer *pVulkanRenderer = new VulkanRenderer();
    return reinterpret_cast<jlong>(pVulkanRenderer);
}

extern "C"
JNIEXPORT void JNICALL
Java_com_github_xingray_renderer_VulkanRenderer_deleteVulkanRenderer(JNIEnv *env,
                                                                     jobject thiz,
                                                                     jlong pointer) {
    LOG_D("Java_com_github_xingray_renderer_VulkanRenderer_deleteVulkanRenderer");
    VulkanRenderer *pVulkanRenderer = reinterpret_cast<VulkanRenderer *>(pointer);
    delete pVulkanRenderer;
}

extern "C"
JNIEXPORT jobject JNICALL
Java_com_github_xingray_renderer_VulkanRenderer_nativeCreateMappingByteBuffer(JNIEnv *env,
                                                                              jobject thiz,
                                                                              jlong pointer) {
    LOG_D("Java_com_github_xingray_renderer_VulkanRenderer_nativeCreateMappingByteBuffer");
    auto *pVulkanRenderer = reinterpret_cast<VulkanRenderer *>(pointer);
    return env->NewDirectByteBuffer(pVulkanRenderer->getMappingData(), (jlong) pVulkanRenderer->getMappingDataSize());
}

extern "C"
JNIEXPORT void JNICALL
Java_com_github_xingray_renderer_VulkanRenderer_nativeDrawFrame(JNIEnv *env,
                                                                jobject thiz,
                                                                jlong pointer) {
    LOG_D("Java_com_github_xingray_renderer_VulkanRenderer_nativeDrawFrame");
    VulkanRenderer *pVulkanRenderer = reinterpret_cast<VulkanRenderer *>(pointer);
    pVulkanRenderer->drawFrame();
}

extern "C"
JNIEXPORT void JNICALL
Java_com_github_xingray_renderer_VulkanRenderer_nativeCopyImageToBuffer(JNIEnv *env,
                                                                        jobject thiz,
                                                                        jlong pointer) {
    LOG_D("Java_com_github_xingray_renderer_VulkanRenderer_nativeDrawFrame");
    VulkanRenderer *pVulkanRenderer = reinterpret_cast<VulkanRenderer *>(pointer);
    pVulkanRenderer->copyImageToBuffer();
}