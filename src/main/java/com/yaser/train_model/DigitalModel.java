/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description:
 */

package com.yaser.train_model;


import com.yaser.neural.NeuralNet;
import com.yaser.utils.math.Matrix;
import com.yaser.utils.math.Sigmoid;

import java.util.Arrays;

public class DigitalModel {
    private static final int input = 28 * 28;//输入大小
    private static final int output = 10;//输出大小
    private static final int testSize = 50;//测试集大小
    private static final int trainSize = 60000;//训练样本大小
    private static final String modelSavedPath = "src\\main\\resource\\model";//中间模型的保存路径（文件夹）
    public static final String finalModelPath = "src\\main\\resource\\model\\digitalFinal.model";//最终模型的保存路径（文件）
    private static final int trainBatchSize = 64;//每一批次数据集大小
    private static final double learningRate = 0.05;//学习率
    private static final int trainStep = 100000;//学习步长
    private final NeuralNet digitalNet;//最终的神经网络模型

    public static String getModelSavedPath() {
        return modelSavedPath;
    }

    public DigitalModel() {
        //导入模型
        digitalNet = NeuralNet.reloadModel(finalModelPath);
    }

    public static void main(String[] args) {
        //使用mnist来训练网络
        trainMNIST();
    }

    /**
     * @param modelPath 指定的模型路径
     * @description 测试指定模型
     */
    public static void testMnistModel(String modelPath) {
        NeuralNet neuralNet = NeuralNet.reloadModel(modelPath);
        //设置测试数据集
        Pair trainSet = MNISTRead.getTestData(testSize);
        Matrix predictMatrix = neuralNet.predict(trainSet.getInputSet());//读取预测数据
        //对预测数据进行转换操作
        int[] predictRes = new int[testSize];
        double[][] predictMatrixVal = predictMatrix.getValue();
        for (int index = 0; index < predictMatrix.getRow(); index++) {
            double curMaxVal = 0;
            for (int columnIndex = 0; columnIndex < predictMatrix.getColumn(); columnIndex++) {
                if (predictMatrixVal[index][columnIndex] > curMaxVal) {
                    curMaxVal = predictMatrixVal[index][columnIndex];
                    predictRes[index] = columnIndex;
                }
            }
        }
        double[][] realVal = trainSet.getLabelsSet();
        int[] realRes = new int[testSize];
        for (int index = 0; index < testSize; index++) {
            for (int columnIndex = 0; columnIndex < realVal[0].length; columnIndex++) {
                if (realVal[index][columnIndex] == 1) {
                    realRes[index] = columnIndex;
                    break;
                }
            }
        }
        String stringBuilder = "预测值：" + Arrays.toString(predictRes) + "\n" +
                "真实值：" + Arrays.toString(realRes) + "\n";
        System.out.println(stringBuilder);
    }

    public int predictSingleDigital(double[][] input) {
        Matrix predictMatrix = digitalNet.predict(input);//使用模型进行训练
        int predictRes = 0;//预测值
        double[][] predictMatrixVal = predictMatrix.getValue();//获取预测结果矩阵
        double curMaxVal = 0;
        //将预测的结果矩阵转化为具体的数值，即将结果中概率最大的位置作为预测值
        for (int index = 0; index < 10; index++) {
            if (predictMatrixVal[0][index] > curMaxVal) {
                //判断该位置的概率是否是最大的
                curMaxVal = predictMatrixVal[0][index];
                predictRes = index;
            }
        }
        return predictRes;
    }

    public static void trainMNIST() {
        Pair trainSet = MNISTRead.getTrainData(trainSize);
        NeuralNet neuralNet;
        //构建网络
        neuralNet = new NeuralNet(input, output, 2, new Sigmoid(), new int[]{512, 64});
        //开始训练
        neuralNet.trainStart(learningRate, trainStep, trainBatchSize, trainSet.getInputSet(), trainSet.getLabelsSet());
        System.out.println("训练完成，开始保存训练模型");
        //训练完成，保存模型
        NeuralNet.saveModel(neuralNet, finalModelPath);
        System.out.println("模型保存完毕。。。");
    }
}

