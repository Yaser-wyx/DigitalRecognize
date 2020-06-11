/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description: 用于序列化保存训练后的模型
 */

package com.yaser.neural;

import com.yaser.utils.math.ActivationFn;

import java.io.*;
import java.util.ArrayList;

public class NeuralModel implements Serializable {
    private int neuralLayersNum;//神经网络层数
    private int[] perLayerNeuronNum;//每一层神经网络的神经元个数
    private ActivationFn[] activationFns;//神经网络每一层所使用的激活函数
    private ArrayList<double[][]> weightValueList = new ArrayList<>();//神经网络每一层的权值

    private NeuralModel(NeuralNet neuralNet) {
        ArrayList<NeuronsLayer> neuronsLayers = neuralNet.getNeuronsLayers();
        this.neuralLayersNum = neuronsLayers.size();
        perLayerNeuronNum = new int[this.neuralLayersNum];
        this.activationFns = neuralNet.getActivationFns();
        for (int layerIndex = 0; layerIndex < this.neuralLayersNum; layerIndex++) {
            NeuronsLayer neuronsLayer = neuronsLayers.get(layerIndex);
            perLayerNeuronNum[layerIndex] = neuronsLayer.getNeuronNum();
            if (layerIndex > 0) {
                //非输入层才权值连接
                LayerWeights preLayerWeights = neuronsLayer.getPreWeights();
                weightValueList.add(preLayerWeights.getWeightMatrix().getValue());
            }
        }
    }

    protected static boolean serializeNeuralNetAsModel(NeuralNet neuralNet, String path) {
        try {
            NeuralModel model = new NeuralModel(neuralNet);
            ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(path));
            os.writeObject(model);
        } catch (IOException e) {
            e.printStackTrace();
            return false;//序列化失败
        }
        return true;
    }

    protected static NeuralNet deSerializeModelAsNeuralNet(String path) {
        try {
            ObjectInputStream is = new ObjectInputStream(new FileInputStream(path));
            NeuralModel model = (NeuralModel) is.readObject();
            //得到模型，开始从模型中恢复神经网络并进行初始化
            return new NeuralNet(model);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public int getNeuralLayersNum() {
        return neuralLayersNum;
    }

    public int[] getPerLayerNeuronNum() {
        return perLayerNeuronNum;
    }

    public ActivationFn[] getActivationFns() {
        return activationFns;
    }

    public ArrayList<double[][]> getWeightValueList() {
        return weightValueList;
    }
}
