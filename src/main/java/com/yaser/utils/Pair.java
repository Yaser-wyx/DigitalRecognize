/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description:
 */

package com.yaser.utils;

public class Pair {
    private double[][] inputSet;
    private double[][] labelsSet;

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
