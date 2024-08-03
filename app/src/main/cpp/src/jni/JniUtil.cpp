//
// Created by leixing on 2023/12/5.
//

#include "JniUtil.h"
#include <jni.h>
#include <vector>
#include <string>
#include "CommonLog.h"
#include "StringUtil.h"

using namespace std;

namespace jni {

    const int getEnumFieldOrdinalByClassName(JNIEnv *env, jobject javaObject, const char *fieldName,
                                             const char *enumClassName) {
        char sig[256];
        javaClassNameToSignature(enumClassName, sig);
        LOG_I("enumClassFullName:%s, converted sig: %s", enumClassName, sig);
        return getEnumFieldOrdinal(env, javaObject, fieldName, sig);
    }

    const int getEnumFieldOrdinal(JNIEnv *env, jobject javaObject, const char *fieldName,
                                  const char *sig) {
        if (javaObject == nullptr) {
            LOG_E("javaObject is null, fieldName: %s", fieldName);
        }
        jclass javaClass = env->GetObjectClass(javaObject);
        jfieldID fieldId = env->GetFieldID(javaClass, fieldName, sig);
        return getEnumFieldOrdinal(env, javaObject, fieldId);
    }

    const int getEnumFieldOrdinal(JNIEnv *env, jobject javaObject, jfieldID fieldId) {
        jobject enumObject = env->GetObjectField(javaObject, fieldId);
        return getEnumOrdinal(env, enumObject);
    }

    int getEnumOrdinal(JNIEnv *env, jobject enumObject) {
        jclass enumObjectClass = env->GetObjectClass(enumObject);
        jmethodID ordinalMethodId = env->GetMethodID(enumObjectClass, "ordinal",
                                                     jni::SIG_FUNCTION_VOID_RETURNS_INT);
        return env->CallIntMethod(enumObject, ordinalMethodId);
    }

    void javaClassNameToSignature(const char *classFullName, char *replaced) {
        std::string s(classFullName);
        util::replaceAll(s, ".", "/");
        std::string replacedString = "L" + s + ";";

        // 使用 std::copy_n 避免内存越界
        std::copy_n(replacedString.c_str(), replacedString.size() + 1, replaced);
    }

    void javaClassNameToNativeClassName(const char *classFullName, char *replaced) {
        std::string s(classFullName);
        util::replaceAll(s, ".", "/");
        // 使用 std::copy_n 避免内存越界
        std::copy_n(s.c_str(), s.size() + 1, replaced);
    }

    void jfloatArrayFieldToFloatArray(JNIEnv *env, jobject javaObject, string fieldName,
                                      float *&outputData, int &outputDataSize) {
        jclass javaClass = env->GetObjectClass(javaObject);
        jfieldID fieldId = env->GetFieldID(javaClass, fieldName.c_str(), jni::SIG_FLOAT_ARRAY);
        jfloatArrayFieldToFloatArray(env, javaObject, fieldId, outputData, outputDataSize);
    }

    void jfloatArrayFieldToFloatArray(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                      float *&outputData, int &outputDataSize) {
        jfloatArray jFloatArray = static_cast<jfloatArray>(env->GetObjectField(javaObject, fieldId));
        jFloatArrayToFloatArray(env, jFloatArray, outputData, outputDataSize);
    }


    void
    jFloatArrayToFloatArray(JNIEnv *env, jfloatArray jFloatArray,
                            float *&outputData, int &outputDataSize) {
        if (jFloatArray == nullptr) {
            outputDataSize = 0;
            if (outputData != nullptr) {
                delete[] outputData;
            }
            outputData = nullptr;
        } else {
            int inputDataSize = env->GetArrayLength(jFloatArray);
            if (inputDataSize != outputDataSize) {
                outputDataSize = inputDataSize;
                if (outputData != nullptr) {
                    delete[] outputData;
                }
                outputData = new float[outputDataSize];
            }

            float *jniData = env->GetFloatArrayElements(jFloatArray, 0);
//            env->GetFloatArrayRegion();
//            env->GetFloatArrayElements();
            memcpy(outputData, jniData, outputDataSize * sizeof(float));
            for (int i = 0; i < outputDataSize; i++) {
                if (outputData[i] != jniData[i]) {
                    LOG_E("outputData[i]!=jniData[i], i:%d", i);
                }
            }
//            env->ReleaseFloatArrayElements(jFloatArray, jniData, JNI_ABORT);
        }
    }

    void jfloatArrayFieldCopyToFloatArray(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                          float *outputData, int &outputDataSize, int capacity) {
        jfloatArray jFloatArray = static_cast<jfloatArray>(env->GetObjectField(javaObject, fieldId));
        jFloatArrayCopyToFloatArray(env, jFloatArray, outputData, outputDataSize, capacity);
    }

    void jFloatArrayCopyToFloatArray(JNIEnv *env, jfloatArray jFloatArray,
                                     float *outputData, int &outputDataSize, int capacity) {
        int inputDataSize = jFloatArray == nullptr ? 0 : env->GetArrayLength(jFloatArray);
        if (inputDataSize == 0) {
            LOG_D("jFloatArrayCopyToFloatArray inputDataSize == 0");
            outputDataSize = 0;
            return;
        }
        if (inputDataSize > capacity) {
            LOG_E("jFloatArrayCopyToFloatArray inputDataSize>capacity, inputDataSize need more memory");
            outputDataSize = 0;
            return;
        }
        outputDataSize = inputDataSize;

        float *jniData = env->GetFloatArrayElements(jFloatArray, 0);
        memcpy(outputData, jniData, inputDataSize * sizeof(float));
        env->ReleaseFloatArrayElements(jFloatArray, jniData, JNI_ABORT);
    }

    void jintArrayFieldCopyToIntArray(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                      int *outputData, int &outputDataSize, int capacity) {
        jintArray javaIntArray = static_cast<jintArray>(env->GetObjectField(javaObject, fieldId));
        jintArrayCopyToIntArray(env, javaIntArray, outputData, outputDataSize, capacity);
    }

    void jintArrayCopyToIntArray(JNIEnv *env, jintArray javaIntArray,
                                 int *outputData, int &outputDataSize, int capacity) {
        int inputDataSize = javaIntArray == nullptr ? 0 : env->GetArrayLength(javaIntArray);
        if (inputDataSize == 0) {
            LOG_D("jintArrayCopyToIntArray inputDataSize == 0");
            outputDataSize = 0;
            return;
        }
        if (inputDataSize > capacity) {
            LOG_W("jintArrayCopyToIntArray inputDataSize>capacity");
            inputDataSize = capacity;
        }
        outputDataSize = inputDataSize;

        jint *jniData = env->GetIntArrayElements(javaIntArray, 0);
        memcpy(outputData, jniData, inputDataSize * sizeof(int));
        env->ReleaseIntArrayElements(javaIntArray, jniData, JNI_ABORT);
    }

    void jintArrayFieldCopyToU32Array(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                      unsigned int *outputData, unsigned int &outputDataSize, unsigned int capacity) {
        jintArray javaIntArray = static_cast<jintArray>(env->GetObjectField(javaObject, fieldId));
        jintArrayCopyToU32Array(env, javaIntArray, outputData, outputDataSize, capacity);
    }

    void jintArrayCopyToU32Array(JNIEnv *env, jintArray javaIntArray,
                                 unsigned int *outputData, unsigned int &outputDataSize, unsigned int capacity) {
        int inputDataSize = javaIntArray == nullptr ? 0 : env->GetArrayLength(javaIntArray);
        if (inputDataSize == 0) {
            LOG_D("jintArrayCopyToIntArray inputDataSize == 0");
            outputDataSize = 0;
            return;
        }
        if (inputDataSize > capacity) {
            LOG_W("jintArrayCopyToIntArray inputDataSize>capacity");
            inputDataSize = capacity;
        }
        outputDataSize = inputDataSize;

        jint *jniData = env->GetIntArrayElements(javaIntArray, 0);
        memcpy(outputData, jniData, inputDataSize * sizeof(int));
        env->ReleaseIntArrayElements(javaIntArray, jniData, JNI_ABORT);
    }

    void jbyteArrayFieldToS8Array(JNIEnv *env, jobject javaObject, string fieldName,
                                  signed char *&outputData, int &outputDataSize) {
        jclass javaClass = env->GetObjectClass(javaObject);
        jfieldID fieldId = env->GetFieldID(javaClass, fieldName.c_str(), jni::SIG_BYTE_ARRAY);
        jbyteArrayFieldToS8Array(env, javaObject, fieldId, outputData, outputDataSize);
    }

    void jbyteArrayFieldToS8Array(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                  signed char *&outputData, int &outputDataSize) {
        jbyteArray jByteArray = (jbyteArray) env->GetObjectField(javaObject, fieldId);
        jbyteArrayToS8Array(env, jByteArray, outputData, outputDataSize);
    }

    void jbyteArrayToS8Array(JNIEnv *env, jbyteArray jByteArray, signed char *&outputData,
                             int &outputDataSize) {
        if (jByteArray == nullptr) {
            outputDataSize = 0;
            if (outputData != nullptr) {
                delete[] outputData;
            }
            outputData = nullptr;
        } else {
            int inputDataSize = env->GetArrayLength(jByteArray);
            if (inputDataSize != outputDataSize) {
                outputDataSize = inputDataSize;
                if (outputData != nullptr) {
                    delete[] outputData;
                }
                outputData = new signed char[inputDataSize];
            }

            jbyte *jniData = env->GetByteArrayElements(jByteArray, 0);
            memcpy(outputData, jniData, outputDataSize);
            env->ReleaseByteArrayElements(jByteArray, jniData, JNI_ABORT);
        }
    }


    void jbyteArrayFieldToU8Array(JNIEnv *env, jobject javaObject, string fieldName,
                                  unsigned char *&outputData, int &outputDataSize) {
        jclass javaClass = env->GetObjectClass(javaObject);
        jfieldID fieldId = env->GetFieldID(javaClass, fieldName.c_str(), jni::SIG_BYTE_ARRAY);
        jbyteArrayFieldToU8Array(env, javaObject, fieldId, outputData, outputDataSize);
    }

    void jbyteArrayFieldToU8Array(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                  unsigned char *&outputData, int &outputDataSize) {
        jbyteArray jByteArray = static_cast<jbyteArray>(env->GetObjectField(javaObject, fieldId));
        jbyteArrayToU8Array(env, jByteArray, outputData, outputDataSize);
    }

    void jbyteArrayToU8Array(JNIEnv *env, jbyteArray jByteArray,
                             unsigned char *&outputData, int &outputDataSize) {
        if (jByteArray == nullptr) {
            outputDataSize = 0;
            if (outputData != nullptr) {
                delete[] outputData;
            }
            outputData = nullptr;
        } else {
            int inputDataSize = env->GetArrayLength(jByteArray);
            if (inputDataSize != outputDataSize) {
                outputDataSize = inputDataSize;
                if (outputData != nullptr) {
                    delete[] outputData;
                }
                outputData = new unsigned char[inputDataSize];
            }

            unsigned char *jniData = (unsigned char *) env->GetByteArrayElements(jByteArray, 0);
            memcpy(outputData, jniData, outputDataSize);
            env->ReleaseByteArrayElements(jByteArray, reinterpret_cast<jbyte *>(jniData), JNI_ABORT);
        }
    }


    int getArraySize(JNIEnv *env, jobjectArray array) {
        if (array == nullptr) {
            return 0;
        }
        return env->GetArrayLength(array);
    }
}
