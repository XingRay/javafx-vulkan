//
// Created by leixing on 2023/12/8.
//

#pragma once
#include "stdint.h"


namespace util {
    class ImageUtil {

    public:
        static
        void generateRGBA(unsigned char *dst, int width, int height);

        static
        void generateRGB(unsigned char *dst, int width, int height);

        static
        void Rgb2Hsv(float R, float G, float B, float &H, float &S, float &V);

        static
        void saveImageToFile(uint8_t* data, int  width, int  height, const char* path, const char* imageName);

        static
        void flipImage(uint8_t *srcImageData, int width, int height, uint8_t *flipImageData);
    };
}
