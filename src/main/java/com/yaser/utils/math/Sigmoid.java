/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description:
 */

package com.yaser.utils.math;

public class Sigmoid extends ActivationFn{
    /**
     * @param x 参数
     * @return 返回sigmoid函数计算结果
     */
    public  double activeFn(double x) {
        return 1.0 / (1.0 + Math.exp(-x));
    }

    /**
     * @param x 参数
     * @return 返回导数值
     */
    public  double activeFnDerivative(double x) {
        return x * (1 - x);
    }
}