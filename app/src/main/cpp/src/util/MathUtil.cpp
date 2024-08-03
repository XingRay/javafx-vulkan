//
// Created by leixing on 2023/12/23.
//

#include "MathUtil.h"

namespace util {
    // 计算仿射变换矩阵
    void getAffineMatrix(float x1, float y1, float x2, float y2, float x3, float y3,
                         float tx1, float ty1, float tx2, float ty2, float tx3, float ty3,
                         float *texMatrix) {
        float detA;
        detA = tx1 * ty2 + tx2 * ty3 + tx3 * ty1 - tx3 * ty2 - tx1 * ty3 - tx2 * ty1;
        float A11, A12, A13, A21, A22, A23, A31, A32, A33;
        A11 = ty2 - ty3;
        A21 = -(ty1 - ty3);
        A31 = ty1 - ty2;
        A12 = -(tx2 - tx3);
        A22 = tx1 - tx3;
        A32 = -(tx1 - tx2);
        A13 = tx2 * ty3 - tx3 * ty2;
        A23 = -(tx1 * ty3 - tx3 * ty1);
        A33 = tx1 * ty2 - tx2 * ty1;
        texMatrix[0] = (x1 * A11 + x2 * A21 + x3 * A31) / detA;
        texMatrix[1] = (x1 * A12 + x2 * A22 + x3 * A32) / detA;
        texMatrix[2] = (x1 * A13 + x2 * A23 + x3 * A33) / detA;
        texMatrix[3] = (y1 * A11 + y2 * A21 + y3 * A31) / detA;
        texMatrix[4] = (y1 * A12 + y2 * A22 + y3 * A32) / detA;
        texMatrix[5] = (y1 * A13 + y2 * A23 + y3 * A33) / detA;
    }
}