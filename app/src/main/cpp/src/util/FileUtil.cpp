//
// Created by leixing on 2024/5/14.
//

#include "FileUtil.h"
#include <iostream>

std::vector<char> FileUtil::readFile(const std::string &path) {
    std::cout << "open file, path: " << path << std::endl;
    std::ifstream fileStream(path, std::ios::ate | std::ios::binary);
    if (!fileStream.is_open()) {
        std::cout << "failed to open file, path: " << path << std::endl;
        throw std::runtime_error("failed to open file");
    }

    size_t fileSize = fileStream.tellg();
    std::vector<char> buffer(fileSize);

    fileStream.seekg(0);
    fileStream.read(buffer.data(), (std::streamsize) fileSize);
    fileStream.close();
    return buffer;
}