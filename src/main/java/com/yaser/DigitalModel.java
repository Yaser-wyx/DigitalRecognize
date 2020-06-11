/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description:
 */

package com.yaser;


import com.yaser.neural.NeuralNet;
import com.yaser.utils.MNISTRead;
import com.yaser.utils.Pair;
import com.yaser.utils.math.Matrix;
import com.yaser.utils.math.Sigmoid;

import java.util.Arrays;
import java.util.Random;

public class DigitalModel {
    private static int input = 28 * 28;
    private static int output = 10;
    private static final int testSize = 50;
    private static final int trainSize = 60000;
    private static final String modelSavedPath = "src\\main\\resource\\model";
    private static final int trainBatchSize = 64;

    public static String getModelSavedPath() {
        return modelSavedPath;
    }

    public static void main(String[] args) {
    }

    public static void testNet() {
        input = 2;
        output = 1;
        Pair trainSet = createSumSample(trainSize, input);
        NeuralNet neuralNet;
        //构建网络
        neuralNet = new NeuralNet(input, output, 2);
        //开始训练
        neuralNet.trainStart(0.01, 150000, trainBatchSize, trainSet.getInputSet(), trainSet.getLabelsSet());
        //训练完成，保存模型
        NeuralNet.saveModel(neuralNet, modelSavedPath + "\\testFinalModel.model");

        //设置测试数据集
        trainSet = createSumSample(testSize, input);
        String stringBuilder = "输入值：" + new Matrix(trainSet.getInputSet()) + "\n" +
                "预测值：" + neuralNet.predict(trainSet.getInputSet()) + "\n" +
                "真实值：" + new Matrix(trainSet.getLabelsSet()) + "\n";
        System.out.println(stringBuilder);

    }

    public static void testMnistModel(String name) {
        NeuralNet neuralNet = NeuralNet.reloadModel(modelSavedPath + "\\" + name);
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

    public int predictSingle(double[][] input) {
        NeuralNet neuralNet = NeuralNet.reloadModel(modelSavedPath + "\\digitalFinal.model");
        Matrix predictMatrix = neuralNet.predict(input);
        int predictRes = 0;
        double[][] predictMatrixVal = predictMatrix.getValue();
        double curMaxVal = 0;
        for (int index = 0; index < 10; index++) {
            if (predictMatrixVal[0][index] > curMaxVal) {
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
        neuralNet.trainStart(0.005, 100000, trainBatchSize, trainSet.getInputSet(), trainSet.getLabelsSet());
        System.out.println("训练完成，开始保存训练模型");
        //训练完成，保存模型
        NeuralNet.saveModel(neuralNet, modelSavedPath + "\\digitalFinal.model");
        System.out.println("模型保存完毕。。。");

    }

    public static Pair createSumSample(int size, int inputNum) {
        double[][] inputSet = new double[size][inputNum];
        double[][] outputSet = new double[size][1];
        Random random = new Random();
        for (int index = 0; index < size; index++) {
            double sum = 0;
            for (int inputIndex = 0; inputIndex < inputNum; inputIndex++) {
                double val = random.nextDouble() * 0.5;
                inputSet[index][inputIndex] = val;
                sum += val;
            }
            outputSet[index][0] = sum;
        }
        return new Pair(inputSet, outputSet);
    }

    public static Pair createClassifySample() {
        double[][] inputSet = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[][] outputSet = {{0, 0, 0, 0}, {0, 1, 0, 0}, {0, 0, 1, 0}, {0, 0, 0, 1}};
        return new Pair(inputSet, outputSet);
    }

    public static Pair createXORSample() {
        double[][] inputSet = {{0, 0}, {0, 1}, {1, 0}, {1, 1}};
        double[][] outputSet = {{0}, {1}, {1}, {0}};
        return new Pair(inputSet, outputSet);
    }
}

