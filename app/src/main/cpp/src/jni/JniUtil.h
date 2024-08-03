//
// Created by leixing on 2023/12/5.
//

#ifndef OPENGLESDEMO_JNIUTIL_H
#define OPENGLESDEMO_JNIUTIL_H

#include <jni.h>
#include <vector>
#include <string>

using namespace std;

namespace jni {

    static const char *SIG_BOOLEAN = "Z";
    static const char *SIG_BYTE = "B";
    static const char *SIG_CHAR = "C";
    static const char *SIG_SHORT = "S";
    static const char *SIG_INT = "I";
    static const char *SIG_LONG = "J";
    static const char *SIG_FLOAT = "F";
    static const char *SIG_DOUBLE = "D";
    static const char *SIG_VOID = "V";

    static const char *SIG_BOOLEAN_ARRAY = "[Z";
    static const char *SIG_BYTE_ARRAY = "[B";
    static const char *SIG_CHAR_ARRAY = "[C";
    static const char *SIG_SHORT_ARRAY = "[S";
    static const char *SIG_INT_ARRAY = "[I";
    static const char *SIG_LONG_ARRAY = "[J";
    static const char *SIG_FLOAT_ARRAY = "[F";
    static const char *SIG_DOUBLE_ARRAY = "[D";

    static const char *SIG_FUNCTION_VOID_RETURNS_INT = "()I";

    struct JavaEnumElement {
        char name[50];
        int ordinal;
    };

    const int getEnumFieldOrdinalByClassName(JNIEnv *env, jobject javaObject, const char *fieldName,
                                             const char *enumClassName);

    const int getEnumFieldOrdinal(JNIEnv *env, jobject javaObject, const char *fieldName,
                                  const char *sig);

    const int getEnumFieldOrdinal(JNIEnv *env, jobject javaObject, jfieldID fieldId);

    int getEnumOrdinal(JNIEnv *env, jobject enumObject);

    void javaClassNameToSignature(const char *classFullName, char *replaced);

    void javaClassNameToNativeClassName(const char *classFullName, char *replaced);

    void jfloatArrayFieldToFloatArray(JNIEnv *env, jobject javaObject, string fieldName,
                                      float *&outputData, int &outputDataSize);

    void jfloatArrayFieldToFloatArray(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                      float *&outputData, int &outputDataSize);

    void
    jFloatArrayToFloatArray(JNIEnv *env, jfloatArray jFloatArray,
                            float *&outputData, int &outputDataSize);


    void jfloatArrayFieldCopyToFloatArray(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                          float *outputData, int &outputDataSize, int capacity);

    void jFloatArrayCopyToFloatArray(JNIEnv *env, jfloatArray jFloatArray,
                                     float *outputData, int &outputDataSize, int capacity);


    void jbyteArrayFieldToS8Array(JNIEnv *env, jobject javaObject, string fieldName,
                                  signed char *&outputData, int &outputDataSize);

    void jbyteArrayFieldToS8Array(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                  signed char *&outputData, int &outputDataSize);

    void jbyteArrayToS8Array(JNIEnv *env, jbyteArray jByteArray, signed char *&outputData,
                             int &outputDataSize);


    void jbyteArrayFieldToU8Array(JNIEnv *env, jobject javaObject, string fieldName,
                                  unsigned char *&outputData, int &outputDataSize);

    void jbyteArrayFieldToU8Array(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                  unsigned char *&outputData, int &outputDataSize);

    void jbyteArrayToU8Array(JNIEnv *env, jbyteArray jByteArray,
                             unsigned char *&outputData, int &outputDataSize);

    int getArraySize(JNIEnv *env, jobjectArray array);

    void jintArrayFieldCopyToIntArray(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                      int *outputData, int &outputDataSize, int capacity);

    void jintArrayCopyToIntArray(JNIEnv *env, jintArray javaIntArray,
                                 int *outputData, int &outputDataSize, int capacity);

    void jintArrayFieldCopyToU32Array(JNIEnv *env, jobject javaObject, jfieldID fieldId,
                                      unsigned int *outputData, unsigned int &outputDataSize, unsigned int capacity);

    void jintArrayCopyToU32Array(JNIEnv *env, jintArray javaIntArray,
                                 unsigned int *outputData, unsigned int &outputDataSize, unsigned int capacity);
}


#endif //OPENGLESDEMO_JNIUTIL_H

