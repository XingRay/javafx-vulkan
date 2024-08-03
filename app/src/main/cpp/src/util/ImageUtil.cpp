//
// Created by leixing on 2023/12/8.
//

#include "ImageUtil.h"
#include <memory>
#include <string>
#include "CommonLog.h"

#define STB_IMAGE_WRITE_IMPLEMENTATION

#include "stb_image_write.h"

namespace util {

    void ImageUtil::generateRGBA(unsigned char *dst, int width, int height) {
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // r
                dst[index++] = x < 256 ? x : 0;
//                dst[index++] = 0;
                //g
                dst[index++] = y < 256 ? y : 0;
                //b
                dst[index++] = 0;
                //a
                dst[index++] = 255;
            }
        }
    }

    void ImageUtil::generateRGB(unsigned char *dst, int width, int height) {
        int index = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // r
                dst[index++] = x < 256 ? x : 0;
//                dst[index++] = 0;
                //g
                dst[index++] = y < 256 ? y : 0;
                //b
                dst[index++] = 0;
            }
        }
    }

    // r,g,b values are from 0 to 1
    // h = [0,360], s = [0,1], v = [0,1]
    void ImageUtil::Rgb2Hsv(float R, float G, float B, float &H, float &S, float &V) {

        // if s == 0, then h = -1 (undefined)
        float min, max, delta, tmp;
        tmp = R > G ? G : R;
        min = tmp > B ? B : tmp;
        tmp = R > G ? R : G;
        max = tmp > B ? tmp : B;
        V = max; // v
        delta = max - min;
        if (max != 0)
            S = delta / max; // s
        else {
            // r = g = b = 0 // s = 0, v is undefined
            S = 0;
            H = 0;
            return;
        }
        if (delta == 0) {
            H = 0;
            return;
        } else if (R == max) {
            if (G >= B)
                H = (G - B) / delta; // between yellow & magenta
            else
                H = (G - B) / delta + 6.0;
        } else if (G == max)
            H = 2.0 + (B - R) / delta; // between cyan & yellow
        else if (B == max)
            H = 4.0 + (R - G) / delta; // between magenta & cyan
        H *= 60.0; // degrees
    }

    void ImageUtil::saveImageToFile(uint8_t *data, int width, int height, const char *path, const char *imageName) {
        std::string filename = std::string(path) + "/" + std::string(imageName) + "_" + std::to_string(width) + "x" + std::to_string(height) + ".png";
        // 将RGBA像素数据保存为PNG文件
        int result = stbi_write_png(filename.c_str(), width, height, 4, data, width * 4);
        if (!result) {
            LOG_E("Failed to save image to %s", filename.c_str());
        } else {
            LOG_I("Image saved to %s", filename.c_str());
        }
    }

    void ImageUtil::flipImage(uint8_t *srcImageData, int width, int height, uint8_t *flipImageData) {
        for (int i = 0; i < height; i++) {
            memcpy(flipImageData + (height - i - 1) * width * 4, srcImageData + i * width * 4, width * 4);
        }
    }
}