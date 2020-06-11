/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description: 单层神经元
 */

package com.yaser.neural;

import com.yaser.utils.math.ActivationFn;
import com.yaser.utils.math.Matrix;
import com.yaser.utils.math.MatrixOperation;

enum LayerType {
    INPUT, HIDDEN, OUTPUT
}

public class NeuronsLayer {
    private final int neuronNum;//当前层神经元的个数
    private boolean hasBias;//是否有偏置神经元，偏置为常量1
    private Matrix activatedMatrix;//激活后的数值
    private LayerWeights preWeights;//左侧权重
    private LayerWeights nextWeights;//右侧权重
    private Matrix errorDerivative;//权重误差偏导数值
    private final ActivationFn activationFn;

    private final LayerType layerType;

    /**
     * @param neuronNum    神经元个数
     * @param activationFn 激活函数
     * @description 用于创建隐藏层
     */
    public NeuronsLayer(int neuronNum, final ActivationFn activationFn) {
        this(neuronNum, LayerType.HIDDEN, activationFn, true);
    }

    public NeuronsLayer(final int neuronNum, final LayerType layerType, final ActivationFn activationFn) {
        this(neuronNum, layerType, activationFn, true);
    }

    /**
     * @param neuronNum    神经元个数
     * @param layerType    网络类型
     * @param activationFn 激活函数
     * @param addBias      是否需要手动添加偏置
     */
    public NeuronsLayer(final int neuronNum, final LayerType layerType, final ActivationFn activationFn, boolean addBias) {
        this.layerType = layerType;
        this.activationFn = activationFn;
        this.hasBias = layerType != LayerType.OUTPUT;
        if (addBias && this.hasBias) {
            this.neuronNum = neuronNum + 1;//加一个偏置单元
        } else {
            this.neuronNum = neuronNum;
        }
    }


    /**
     * @param layer1 要连接的前一个神经元层
     * @param layer2 要连接的后一个神经元层
     * @description 对神经元进行连接操作
     */
    public static void connectLayer(NeuronsLayer layer1, NeuronsLayer layer2) {
        LayerWeights weights = new LayerWeights(layer1, layer2);//创建一个连接权重，并初始化
        layer1.nextWeights = weights;
        layer2.preWeights = weights;
    }

    /**
     * @param layer1            要连接的前一个神经元层
     * @param layer2            要连接的后一个神经元层
     * @param layerWeightsValue 指定的连接权值
     * @description 使用指定的连接权值对神经元进行连接操作
     */
    public static void connectLayer(NeuronsLayer layer1, NeuronsLayer layer2, double[][] layerWeightsValue) {
        LayerWeights layerWeights = new LayerWeights(layer1, layer2, layerWeightsValue);//创建一个连接权重，并初始化
        layer1.nextWeights = layerWeights;
        layer2.preWeights = layerWeights;
    }

    public int getNeuronNum() {
        return neuronNum;
    }

    /**
     * @description 进行前向传播操作
     */
    public void forward() {
        assert this.preWeights != null;
        NeuronsLayer preLayer = this.preWeights.getPreLayer();//获取前一层的神经元
        assert preLayer != null;

        Matrix noneActivateMatrix = MatrixOperation.matrixMul(preLayer.getOutput(),
                MatrixOperation.transposition(this.preWeights.getWeightMatrix()));//计算传播过来的值，也就是本层神经网络的输入
        this.activatedMatrix = ActivationFn.useActivationFn(
                noneActivateMatrix, this.activationFn::activeFn);//使用激活函数，对神经元进行激活操作
        if (this.hasBias) {
            //如果需要偏置单元，则将最后一列设置为常数1
            Matrix.setAppointColumnVal(this.activatedMatrix, this.neuronNum - 1, 1);
        }
    }

    /**
     * @param outputMatrix 训练结果集
     * @description 计算神经元误差，只可用于输出层的神经元
     */
    public void calErrorDelta(Matrix outputMatrix) {
        assert this.layerType == LayerType.OUTPUT;
        //计算损失
        Matrix errorMatrix = MatrixOperation.matrixSub(outputMatrix, this.activatedMatrix);
        //计算每一个神经元的误差值
        this.errorDerivative = MatrixOperation.hadamardMul
                (errorMatrix, ActivationFn.useActivationFn
                        (this.activatedMatrix, this.activationFn::activeFnDerivative));
    }

    /**
     * @description 计算神经元误差，只可用于隐藏层
     */
    public void calErrorDelta() {
        assert this.layerType == LayerType.HIDDEN;
        NeuronsLayer nextLayer = this.nextWeights.getNextLayer();
        assert nextLayer != null;
        //计算损失
        Matrix errorMatrix = MatrixOperation.matrixMul
                (nextLayer.errorDerivative, this.nextWeights.getWeightMatrix());
        //计算每一个神经元的误差的偏导数值
        this.errorDerivative = MatrixOperation.hadamardMul
                (errorMatrix, ActivationFn.useActivationFn
                        (this.activatedMatrix, this.activationFn::activeFnDerivative));
    }

    /**
     * @description 神经网络，权值误差调整
     */
    public void adjustment(double learningRate) {
        assert this.preWeights != null;
        //计算要调整的权重误差
        NeuronsLayer preLayer = this.preWeights.getPreLayer();
        assert preLayer != null;
        //计算权重误差
        Matrix weightsDelta = MatrixOperation.matrixMul(
                MatrixOperation.transposition(this.errorDerivative),
                preLayer.activatedMatrix);
        //使用计算的误差调整权重网络
        this.preWeights.updateWeights(weightsDelta, learningRate);
    }

    public Matrix getOutput() {
        return this.activatedMatrix;
    }

    /**
     * @param inputMatrix 输入矩阵 是一个m*n的矩阵，m为训练个数，n为特征数
     * @description 设置输入矩阵
     */
    public void setInputMatrix(Matrix inputMatrix) {
        assert this.layerType == LayerType.INPUT;
        this.activatedMatrix = Matrix.addOneColumn(inputMatrix, 1);//添加一列偏置单元
    }

    public LayerType getLayerType() {
        return layerType;
    }


    @Override
    public String toString() {
        return this.preWeights.getWeightMatrix().toString();
    }

    public LayerWeights getPreWeights() {
        return preWeights;
    }

    public void setPreWeights(LayerWeights preWeights) {
        this.preWeights = preWeights;
    }
}

