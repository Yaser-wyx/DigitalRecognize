/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description:
 */

package com.yaser.train_model;

public class Pair {
    private final double[][] inputSet;
    private final double[][] labelsSet;

    public Pair(double[][] inputSet, double[][] labelsSet) {
        this.inputSet = inputSet;
        this.labelsSet = labelsSet;
    }

    public double[][] getInputSet() {
        return inputSet;
    }

    public double[][] getLabelsSet() {
        return labelsSet;
    }
}
