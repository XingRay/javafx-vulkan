cmake_minimum_required(VERSION 3.28)

project("vulkan_renderer")

set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# glm
# https://github.com/g-truc/glm
set(GLM_INSTALL_DIR D:\\develop\\opengl\\glm\\glm-1.0.1-light)

# vulkan
# https://www.vulkan.org/
set(VULKAN_INSTALL_DIR D:\\develop\\vulkan)
set(VULKAN_INCLUDE_DIR ${VULKAN_INSTALL_DIR}/Include)
set(VULKAN_LIB_DIR ${VULKAN_INSTALL_DIR}/Lib)

# stb
set(STB_INSTALL_DIR D:\\develop\\opengl\\stb)
set(STB_INCLUDE_DIR ${STB_INSTALL_DIR})

# tinyobjloader
set(TINYOBJLOADER_INSTALL_DIR D:\\develop\\opengl\\tinyobjloader)
set(TINYOBJLOADER_INCLUE_DIR ${TINYOBJLOADER_INSTALL_DIR})

# jdk
# D:\develop\java\jdk\openjdk\jdk-21
set(JDK_HOME_DIR D:\\develop\\java\\jdk\\openjdk\\jdk-21)

# visual studio
set(VS_HOME_DIR C:\\Program Files\\Microsoft Visual Studio\\2022\\Community\\VC\\Tools\\MSVC\\14.39.33519)

###########################################################################################################
##                                                                                                       ##
##                                             set params                                                ##
##                                                                                                       ##
###########################################################################################################

include_directories(
        # glm
        ${GLM_INSTALL_DIR}

        # vulkan
        ${VULKAN_INCLUDE_DIR}

        # jdk
        ${JDK_HOME_DIR}/include
        ${JDK_HOME_DIR}/include/win32

        # visual studio
        ${VS_HOME_DIR}/include

        # STB
        ${STB_INCLUDE_DIR}

        # tinyobjloader
        ${TINYOBJLOADER_INCLUE_DIR}

        # src
        src/jni
        src/renderer
        src/util
        src/vulkan
        src
)


file(GLOB_RECURSE SOURCE_FILES "${CMAKE_CURRENT_SOURCE_DIR}/src/*.cpp")

add_library( # Sets the name of the library.
        ${PROJECT_NAME}

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${SOURCE_FILES})

link_directories(
        ${JDK_HOME_DIR}/lib
        ${VS_HOME_DIR}/lib/x64
)

target_link_libraries(
        ${PROJECT_NAME}

        ${VULKAN_LIB_DIR}/vulkan-1.lib
)