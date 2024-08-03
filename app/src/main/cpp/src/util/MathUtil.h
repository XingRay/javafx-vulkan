//
// Created by leixing on 2023/12/23.
//

#pragma once

namespace util {
    void getAffineMatrix(float x1, float y1, float x2, float y2, float x3, float y3,
                         float tx1, float ty1, float tx2, float ty2, float tx3, float ty3,
                         float *texMatrix);
}