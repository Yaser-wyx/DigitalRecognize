/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description: 构建神经网络
 */

package com.yaser.neural;

import com.yaser.train_model.DigitalModel;
import com.yaser.utils.math.ActivationFn;
import com.yaser.utils.math.LossFn;
import com.yaser.utils.math.Matrix;
import com.yaser.utils.math.Sigmoid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class NeuralNet {
    private ArrayList<NeuronsLayer> neuronsLayers = new ArrayList<>();
    private double learningRate;//默认值
    private int inputNum;//输入神经元数
    private int outputNum;//输出神经元数
    private int hiddenLayerNum;//隐藏层数
    private ActivationFn[] activationFns;//各层激活函数
    private int[] perHiddenNeuronsNum;

    public NeuralNet(final int inputNum, final int outputNum, final int hiddenLayerNum) {
        this(inputNum, outputNum, hiddenLayerNum, new Sigmoid(), null);
    }

    /**
     *
     * @param inputNum 输入层神经元个数
     * @param outputNum 输出层神经元个数
     * @param hiddenLayerNum 隐藏层个数
     * @param activeFn 激活函数
     * @param perHiddenNeuronsNum 每一个隐藏层神经元个数
     */
    public NeuralNet(final int inputNum, final int outputNum, final int hiddenLayerNum, final ActivationFn activeFn, final int[] perHiddenNeuronsNum) {
        ActivationFn[] activationFns = new ActivationFn[hiddenLayerNum + 1];
        Arrays.fill(activationFns, activeFn);
        this.inputNum = inputNum;
        this.outputNum = outputNum;
        this.hiddenLayerNum = hiddenLayerNum;
        this.activationFns = activationFns;
        if (perHiddenNeuronsNum != null) {
            this.perHiddenNeuronsNum = perHiddenNeuronsNum;
        } else {
            this.perHiddenNeuronsNum = new int[hiddenLayerNum];
            Arrays.fill(this.perHiddenNeuronsNum, (int) ((inputNum + outputNum) * 1.5));
        }
        this.buildNetWork();
    }

    public NeuralNet(final int inputNum, final int outputNum, final int hiddenLayerNum, final ActivationFn[] activationFns) {
        this.inputNum = inputNum;
        this.outputNum = outputNum;
        this.hiddenLayerNum = hiddenLayerNum;
        this.activationFns = activationFns;
        this.buildNetWork();
    }

    /**
     * @param model 待转换的模型
     * @description 从模型中实例化一个神经网络
     */
    protected NeuralNet(NeuralModel model) {
        final int[] perLayerNeuronNum = model.getPerLayerNeuronNum();
        ActivationFn[] activationFns = model.getActivationFns();
        //恢复输入层
        NeuronsLayer inputLayer = new NeuronsLayer(perLayerNeuronNum[0], LayerType.INPUT, null, false);
        neuronsLayers.add(inputLayer);
        int activationFnIndex = 0;
        final int lastLayerIndex = model.getNeuralLayersNum() - 1;
        //恢复隐藏层
        for (int hiddenLayerIndex = 1; hiddenLayerIndex < lastLayerIndex; hiddenLayerIndex++) {
            NeuronsLayer hiddenLayer = new NeuronsLayer(perLayerNeuronNum[hiddenLayerIndex], LayerType.HIDDEN, activationFns[activationFnIndex++], false);
            neuronsLayers.add(hiddenLayer);
        }
        //恢复输出层
        NeuronsLayer outputLayer = new NeuronsLayer(perLayerNeuronNum[lastLayerIndex], LayerType.OUTPUT, activationFns[activationFnIndex], false);
        neuronsLayers.add(outputLayer);
        ArrayList<double[][]> weightValueList = model.getWeightValueList();
        //使用模型中的连接权值对神经网络进行连接操作
        NeuronsLayer preLayer, nextLayer;
        for (int weightIndex = 0; weightIndex < weightValueList.size(); weightIndex++) {
            preLayer = neuronsLayers.get(weightIndex);
            nextLayer = neuronsLayers.get(weightIndex + 1);
            NeuronsLayer.connectLayer(preLayer, nextLayer, weightValueList.get(weightIndex));
        }
    }

    /**
     * @description 根据输入的神经网络参数，来自动构建一个神经网络
     */
    private void buildNetWork() {
        //创建输入神经层
        NeuronsLayer inputLayer = new NeuronsLayer(inputNum, LayerType.INPUT, null);
        neuronsLayers.add(inputLayer);
        int activationFnIndex = 0;
        int hiddenIndex = 0;
        //创建隐藏层
        for (int i = 0; i < hiddenLayerNum; i++) {
            NeuronsLayer hiddenLayer =
                    new NeuronsLayer(this.perHiddenNeuronsNum[hiddenIndex++],
                            activationFns[activationFnIndex++]);
            neuronsLayers.add(hiddenLayer);
        }
        //创建输出层
        NeuronsLayer outLayer =
                new NeuronsLayer(outputNum, LayerType.OUTPUT, activationFns[activationFnIndex]);
        neuronsLayers.add(outLayer);

        //进行网络连接操作
        for (int layerIndex = 0; layerIndex < neuronsLayers.size() - 1; layerIndex++) {
            NeuronsLayer.connectLayer(neuronsLayers.get(layerIndex),
                    neuronsLayers.get(layerIndex + 1));//将前后两层神经网络相连接
        }
    }

    /**
     * @param inputMatrix      输入值
     * @param realOutputMatrix 真实值
     * @description 使用所输入的样本进行训练
     */
    private void train(final Matrix inputMatrix, final Matrix realOutputMatrix) {
        NeuronsLayer inputLayer = this.neuronsLayers.get(0);
        assert inputLayer.getLayerType() == LayerType.INPUT;
        inputLayer.setInputMatrix(inputMatrix);
        //前向传播
        for (int layerIndex = 1; layerIndex < neuronsLayers.size(); layerIndex++) {
            NeuronsLayer curLayer = neuronsLayers.get(layerIndex);
            curLayer.forward();
        }

        //获取输出层
        NeuronsLayer outputLayer = neuronsLayers.get(neuronsLayers.size() - 1);
        assert outputLayer.getLayerType() == LayerType.OUTPUT;

        //从输出层开始进行反向误差计算
        outputLayer.calErrorDelta(realOutputMatrix);
        for (int layerIndex = neuronsLayers.size() - 2; layerIndex >= 1; layerIndex--) {//注：输入层不需要计算神经元误差
            neuronsLayers.get(layerIndex).calErrorDelta();//计算神经元误差，并进行反向传播
        }
        //从第一个隐藏层开始，调整神经网络权重
        for (NeuronsLayer neuronsLayer : neuronsLayers) {
            if (neuronsLayer.getLayerType() != LayerType.INPUT)
                neuronsLayer.adjustment(learningRate);
        }
    }

    /**
     * @param learningRate 学习率
     * @param trainSteps   训练步数
     * @param batchSize    每次批梯度下降的batch大小
     * @param inputSet     输入数据
     * @param outputSet    输出数据集
     */
    public void trainStart(double learningRate, int trainSteps, int batchSize, double[][] inputSet, double[][] outputSet) {
        this.learningRate = learningRate;
        NeuronsLayer outputLayer = neuronsLayers.get(neuronsLayers.size() - 1);
        Random random = new Random();
        for (int stepIndex = 0; stepIndex < trainSteps; stepIndex++) {
            //从总的训练样本中，随机抽取batch大小的训练样本用于本次训练
            double[][] batchInput = new double[batchSize][inputSet[0].length];
            double[][] batchOutput = new double[batchSize][outputSet[0].length];
            for (int batchIndex = 0; batchIndex < batchSize; batchIndex++) {
                int randomIndex = random.nextInt(inputSet.length);
                batchInput[batchIndex] = inputSet[randomIndex];
                batchOutput[batchIndex] = outputSet[randomIndex];
            }
            Matrix batchInputMatrix = new Matrix(batchInput);
            Matrix batchOutputMatrix = new Matrix(batchOutput);
            //开始训练
            this.train(batchInputMatrix, batchOutputMatrix);

            if (stepIndex % 100 == 0) {
                //每100次输出一次训练结果
                System.out.println("stepIndex: " + stepIndex + " of " + trainSteps);
                System.out.println("loss value: " + LossFn.L2Loss(batchOutputMatrix, outputLayer.getOutput()) + "\n");
            }
            if (stepIndex % 1000 == 0 && stepIndex != 0) {
                //每1000次保存一次模型
                System.out.println("输出一个模型，编号" + stepIndex + "，loss value: " + LossFn.L2Loss(batchOutputMatrix, outputLayer.getOutput()) + "\n");
                String modelName = DigitalModel.getModelSavedPath() + "\\netModel_" + stepIndex + ".model";
                System.out.println(modelName);
                NeuralNet.saveModel(this, modelName);
            }

        }
    }

    /**
     * @param inputSample 输入样例
     * @return 返回预测结果
     */
    public Matrix predict(double[][] inputSample) {
        NeuronsLayer inputLayer = this.neuronsLayers.get(0);
        assert inputLayer.getLayerType() == LayerType.INPUT;
        inputLayer.setInputMatrix(new Matrix(inputSample));
        for (int layerIndex = 1; layerIndex < neuronsLayers.size(); layerIndex++) {
            NeuronsLayer curLayer = neuronsLayers.get(layerIndex);
            curLayer.forward();
        }
        NeuronsLayer outputLayer = neuronsLayers.get(neuronsLayers.size() - 1);
        return outputLayer.getOutput();
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        NeuronsLayer outputLayer = neuronsLayers.get(neuronsLayers.size() - 1);
        stringBuilder.append("input: ").append(neuronsLayers.get(0).getOutput());
        stringBuilder.append("output: ").append(outputLayer.getOutput());
        return stringBuilder.toString();
    }

    public static void saveModel(NeuralNet neuralNet, String path) {
        if (!NeuralModel.serializeNeuralNetAsModel(neuralNet, path)) {
            throw new RuntimeException("模型保存失败！");
        }
    }

    public static NeuralNet reloadModel(String path) {
        NeuralNet neuralNet = NeuralModel.deSerializeModelAsNeuralNet(path);
        if (neuralNet == null) {
            throw new RuntimeException("模型恢复失败！");
        }
        return neuralNet;
    }

    public ArrayList<NeuronsLayer> getNeuronsLayers() {
        return neuronsLayers;
    }

    public ActivationFn[] getActivationFns() {
        return activationFns;
    }
}
