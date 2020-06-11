/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description:
 */
package com.yaser.utils.math;

import java.io.Serializable;
import java.util.function.Function;

public abstract class ActivationFn implements Serializable {
    /**
     * @param x 参数
     * @return 返回sigmoid函数计算结果
     */
    public abstract double activeFn(double x);

    /**
     * @param x 参数
     * @return 返回导数值
     */
    public abstract double activeFnDerivative(double x);

    /**
     * @param matrix 待使用激活函数的矩阵
     * @param fn     所使用的的激活函数
     * @return 返回使用指定激活函数后的矩阵
     */
    public static Matrix useActivationFn(Matrix matrix, Function<Double, Double> fn) {
        Matrix resMatrix = Matrix.copyMatrix(matrix);//复制一份，以免污染原始数据
        double[][] resMatrixVal = resMatrix.getValue();
        for (int rowIndex = 0; rowIndex < matrix.getRow(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < matrix.getColumn(); columnIndex++) {
                resMatrixVal[rowIndex][columnIndex] = fn.apply(resMatrixVal[rowIndex][columnIndex]);//使用指定的函数
            }
        }
        return resMatrix;
    }
}
