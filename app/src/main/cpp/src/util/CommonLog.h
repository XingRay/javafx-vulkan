#define TAG "vulkan-renderer"

//#define BUILD_TYPE_RELEASE

#define LOG_NONE 0
#define LOG_ANDROID 1
#define LOG_WINDOWS 2

//#define BUILD_TYPE_RELEASE

#ifdef BUILD_TYPE_RELEASE
#define LOG_TYPE LOG_NONE
#else
#define LOG_TYPE LOG_WINDOWS
//#define LOG_TYPE LOG_ANDROID
#endif


#if LOG_TYPE == LOG_NONE

#define LOG_V(...) ((void)0)
#define LOG_D(...) ((void)0)
#define LOG_I(...) ((void)0)
#define LOG_W(...) ((void)0)
#define LOG_E(...) ((void)0)
#define LOG_F(...) ((void)0)

#elif LOG_TYPE == LOG_ANDROID
#include <android/log.h>

#define LOG_V(...) __android_log_print(ANDROID_LOG_VERBOSE, TAG, __VA_ARGS__)
#define LOG_D(...) __android_log_print(ANDROID_LOG_DEBUG, TAG, __VA_ARGS__)
#define LOG_I(...) __android_log_print(ANDROID_LOG_INFO, TAG, __VA_ARGS__)
#define LOG_W(...) __android_log_print(ANDROID_LOG_WARN, TAG, __VA_ARGS__)
#define LOG_E(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)
#define LOG_F(...) __android_log_print(ANDROID_LOG_FATAL, TAG, __VA_ARGS__)

#elif LOG_TYPE == LOG_WINDOWS

#include <cstdio>
#include <thread>

#define LOG_V(...) printf("%lu native-%s\t", static_cast<unsigned long>(std::hash<std::thread::id>{}(std::this_thread::get_id())), TAG);printf(__VA_ARGS__);printf("\n")
#define LOG_D(...) printf("%lu native-%s\t", static_cast<unsigned long>(std::hash<std::thread::id>{}(std::this_thread::get_id())), TAG);printf(__VA_ARGS__);printf("\n")
#define LOG_I(...) printf("%lu native-%s\t", static_cast<unsigned long>(std::hash<std::thread::id>{}(std::this_thread::get_id())), TAG);printf(__VA_ARGS__);printf("\n")
#define LOG_W(...) printf("%lu native-%s\t", static_cast<unsigned long>(std::hash<std::thread::id>{}(std::this_thread::get_id())), TAG);printf(__VA_ARGS__);printf("\n")
#define LOG_E(...) printf("%lu native-%s\t", static_cast<unsigned long>(std::hash<std::thread::id>{}(std::this_thread::get_id())), TAG);printf(__VA_ARGS__);printf("\n")
#define LOG_F(...) printf("%lu native-%s\t", static_cast<unsigned long>(std::hash<std::thread::id>{}(std::this_thread::get_id())), TAG);printf(__VA_ARGS__);printf("\n")

#else

#define LOG_V(...) ((void)0)
#define LOG_D(...) ((void)0)
#define LOG_I(...) ((void)0)
#define LOG_W(...) ((void)0)
#define LOG_E(...) ((void)0)
#define LOG_F(...) ((void)0)

#endif
