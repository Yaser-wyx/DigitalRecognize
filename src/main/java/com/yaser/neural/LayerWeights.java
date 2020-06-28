/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description: 神经元层与层之间的权重
 */

package com.yaser.neural;

import com.yaser.utils.math.Matrix;
import com.yaser.utils.math.MatrixOperation;

import java.util.Random;

public class LayerWeights {
    private Matrix weightMatrix;//神经元之间连接的权重矩阵
    private final NeuronsLayer preLayer;//前一层神经元
    private final NeuronsLayer nextLayer;//下一层神经元

    public LayerWeights(NeuronsLayer preLayer, NeuronsLayer nextLayer) {
        this.preLayer = preLayer;
        this.nextLayer = nextLayer;
        this.initWeights();
    }

    public LayerWeights(NeuronsLayer preLayer, NeuronsLayer nextLayer, double[][] weightValue) {
        this.preLayer = preLayer;
        this.nextLayer = nextLayer;
        this.weightMatrix = new Matrix(weightValue);

    }

    /**
     * @description 进行连接权重的初始化工作，初始化使用高斯分布
     */
    private void initWeights() {
        int preNum = preLayer.getNeuronNum();
        int nextNum = nextLayer.getNeuronNum();
        double[][] initialMatrix = new double[nextNum][preNum];
        Random random = new Random();
        //填充数据
        for (int nextIndex = 0; nextIndex < nextNum; nextIndex++) {
            for (int preIndex = 0; preIndex < preNum; preIndex++) {
                initialMatrix[nextIndex][preIndex] = random.nextGaussian();//使用高斯分布进行值的初始化
            }
        }
        this.weightMatrix = new Matrix(initialMatrix);
    }

    public Matrix getWeightMatrix() {
        return weightMatrix;
    }

    public NeuronsLayer getPreLayer() {
        return preLayer;
    }

    /**
     * @param weightsDelta 当前连接权重的误差偏微分
     */
    public void updateWeights(Matrix weightsDelta, double learningRate) {
        //使用梯度下降法计算新的权重值
        this.weightMatrix = MatrixOperation.matrixAdd(this.weightMatrix, MatrixOperation.scalarMul(learningRate, weightsDelta));
    }

    public NeuronsLayer getNextLayer() {
        return nextLayer;
    }
}

