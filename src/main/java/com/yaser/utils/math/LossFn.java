/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description:
 */

package com.yaser.utils.math;

public class LossFn {
    /**
     * L2型损失函数
     *
     * @param y   真实值
     * @param h_x 预测值
     * @return 返回L1损失函数的计算结果
     * @description L2公式：1/2(y-h(x))^2
     */
    public static double L2Loss(Matrix y, Matrix h_x) {
        //获得真实值与预测值的误差值
        double[][] lossMatrixValue = MatrixOperation.matrixSub(y, h_x).getValue();
        double lossVal = 0;
        for (int index = 0; index < y.getRow(); index++) {
            lossVal += 1.0 / 2.0 * (Math.pow(lossMatrixValue[index][0], 2));//对每一个误差平方后乘1/2，并全部相加
        }
        return lossVal / y.getRow();//最后求平均损失
    }
}
