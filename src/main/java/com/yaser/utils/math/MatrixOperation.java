/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description:
 */

package com.yaser.utils.math;

public class MatrixOperation {
    /**
     * @param matrix1 第一个矩阵
     * @param matrix2 第二个矩阵
     * @param flagAdd 加法标记
     * @return 返回相加减后的矩阵
     */
    private static Matrix matrixAddOrSub(Matrix matrix1, Matrix matrix2, boolean flagAdd) {
        if (matrix1.couldAddOrSub(matrix2)) {
            final int row = matrix1.getRow();
            final int column = matrix1.getColumn();
            final double[][] matrix1Value = matrix1.getValue();
            final double[][] matrix2Value = matrix2.getValue();
            double[][] addedMatrix = new double[matrix1.getRow()][matrix1.getColumn()];
            for (int i = 0; i < row; i++) {
                for (int j = 0; j < column; j++) {
                    if (flagAdd) {
                        addedMatrix[i][j] = matrix1Value[i][j] + matrix2Value[i][j];
                    } else {
                        addedMatrix[i][j] = matrix1Value[i][j] - matrix2Value[i][j];
                    }
                }
            }
            return new Matrix(addedMatrix);
        } else {
            if (flagAdd) {
                throw new RuntimeException("矩阵不匹配，无法相加!");
            } else {
                throw new RuntimeException("矩阵不匹配，无法相减!");
            }
        }
    }

    /**
     * @param matrix1 第一个矩阵
     * @param matrix2 第二个矩阵
     * @return 返回相加后的矩阵
     */
    public static Matrix matrixAdd(Matrix matrix1, Matrix matrix2) {
        return matrixAddOrSub(matrix1, matrix2, true);
    }

    /**
     * @param matrix1 第一个矩阵
     * @param matrix2 第二个矩阵
     * @return 返回相减后的矩阵
     */
    public static Matrix matrixSub(Matrix matrix1, Matrix matrix2) {
        return matrixAddOrSub(matrix1, matrix2, false);
    }

    /**
     * @param matrix1 第一个矩阵
     * @param matrix2 第二个矩阵
     * @return 返回相乘后的矩阵
     */
    public static Matrix matrixMul(Matrix matrix1, Matrix matrix2) {
        if (matrix1.couldMul(matrix2)) {
            final double[][] matrix1Value = matrix1.getValue();
            final double[][] matrix2Value = matrix2.getValue();
            double[][] muledMatrix = new double[matrix1.getRow()][matrix2.getColumn()];
            for (int i = 0; i < matrix1.getRow(); i++) {
                for (int k = 0; k < matrix2.getColumn(); k++) {
                    double sum = 0.0;
                    for (int j = 0; j < matrix1.getColumn(); j++) {
                        sum += matrix1Value[i][j] * matrix2Value[j][k];
                    }
                    muledMatrix[i][k] = sum;
                }
            }
            return new Matrix(muledMatrix);
        } else {
            throw new RuntimeException("矩阵不匹配，无法相乘!");
        }
    }

    /**
     * @param matrix1 第一个矩阵
     * @param matrix2 第二个矩阵
     * @return 返回Hadamard乘积后的矩阵
     */
    public static Matrix hadamardMul(Matrix matrix1, Matrix matrix2) {
        if (matrix1.getRow() == matrix2.getRow() && matrix1.getColumn() == matrix2.getColumn()) {
            final double[][] matrix1Value = matrix1.getValue();
            final double[][] matrix2Value = matrix2.getValue();
            double[][] muledMatrix = new double[matrix1.getRow()][matrix1.getColumn()];
            for (int rowIndex = 0; rowIndex < matrix1.getRow(); rowIndex++) {
                for (int columnIndex = 0; columnIndex < matrix1.getColumn(); columnIndex++) {
                    muledMatrix[rowIndex][columnIndex] = matrix1Value[rowIndex][columnIndex] * matrix2Value[rowIndex][columnIndex];
                }
            }
            return new Matrix(muledMatrix);
        } else {
            throw new RuntimeException("矩阵不匹配，无法进行Hadamard乘积!");
        }
    }

    /**
     * @param scalar 标量
     * @param matrix 矩阵
     * @return 标量与矩阵的乘积
     */
    public static Matrix scalarMul(double scalar, Matrix matrix) {
        final double[][] matrixVal = matrix.getValue();
        double[][] mulResMatrixVal = new double[matrix.getRow()][matrix.getColumn()];
        for (int rowIndex = 0; rowIndex < matrix.getRow(); rowIndex++) {
            for (int columnIndex = 0; columnIndex < matrix.getColumn(); columnIndex++) {
                mulResMatrixVal[rowIndex][columnIndex] = scalar * matrixVal[rowIndex][columnIndex];
            }
        }
        return new Matrix(mulResMatrixVal);
    }


    //矩阵转置
    public static Matrix transposition(Matrix matrix) {
        double[][] transposedMatrix = new double[matrix.getColumn()][matrix.getRow()];
        final double[][] matrixVal = matrix.getValue();
        for (int i = 0; i < matrix.getRow(); i++) {
            for (int j = 0; j < matrix.getColumn(); j++) {
                transposedMatrix[j][i] = matrixVal[i][j];
            }
        }

        return new Matrix(transposedMatrix);
    }
}
