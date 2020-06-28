/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description:
 */

package com.yaser.utils.math;

public class Matrix {
    private int row;//行向量个数
    private int column;//列向量个数
    private double[][] value;//矩阵具体数据

    /**
     * @param row    行
     * @param column 列
     * @description 初始化一个指定行列个数的矩阵
     */
    public Matrix(int row, int column) {
        this.row = row;
        this.column = column;
        this.value = new double[row][column];
    }

    /**
     *
     * @param value 矩阵的值
     */
    public Matrix(double[][] value) {
        this.row = value.length;
        this.column = value[0].length;
        this.value = value;
    }

    /**
     *
     * @param originMatrix 要复制的矩阵
     * @return 返回一个完全拷贝的矩阵
     */
    public static Matrix copyMatrix(Matrix originMatrix) {
        double[][] targetMatrixValue = new double[originMatrix.row][originMatrix.column];
        for (int i = 0; i < originMatrix.row; i++) {
            if (originMatrix.column >= 0)
                System.arraycopy(originMatrix.value[i], 0, targetMatrixValue[i], 0, originMatrix.column);
        }
        return new Matrix(targetMatrixValue);
    }

    /**
     * @description 设置为一个列向量
     */
    public Matrix(double[] value) {
        this.row = value.length;
        this.column = 1;
        this.value = new double[row][1];
        for (int i = 0; i < row; i++) {
            this.value[i][0] = value[i];
        }
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public double[][] getValue() {
        return value;
    }

    /**
     * @param val 需要添加的数据
     * @description 给矩阵添加一列指定的数据
     */
    public static Matrix addOneColumn(Matrix matrix, double val) {
        int row = matrix.row;
        int column = matrix.column;

        double[][] newValue = new double[row][column + 1];
        for (int rowIndex = 0; rowIndex < row; rowIndex++) {
            System.arraycopy(matrix.value[rowIndex], 0, newValue[rowIndex], 0, column);
            newValue[rowIndex][column] = val;
        }
        return new Matrix(newValue);
    }

    /**
     * @param target 指定的矩阵
     * @param columnIndex 指定的列
     * @param val         填充的数据
     * @description 将矩阵指定的列设置为指定的值
     */
    public static void setAppointColumnVal(Matrix target, int columnIndex, double val) {
        for (int rowIndex = 0; rowIndex < target.row; rowIndex++) {
            target.value[rowIndex][columnIndex] = val;
        }
    }

    //判断是否可以与另一个矩阵相加
    public boolean couldAddOrSub(Matrix matrix) {
        return this.row == matrix.row && this.column == matrix.column;
    }

    //判断是否可以与另一个矩阵相乘
    public boolean couldMul(Matrix matrix) {
        return this.column == matrix.row;
    }


    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                stringBuilder.append(value[i][j]).append(" ");
            }
            stringBuilder.append("\n");
        }
        return "\n" + stringBuilder.toString();
    }
}
