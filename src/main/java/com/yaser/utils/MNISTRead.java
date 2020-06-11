/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description: MNIST数据集读取工具
 */

package com.yaser.utils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class MNISTRead {
    public static final String TRAIN_IMAGES_FILE = "src\\main\\resource\\data\\mnist\\train-images.idx3-ubyte";
    public static final String TRAIN_LABELS_FILE = "src\\main\\resource\\data\\mnist\\train-labels.idx1-ubyte";
    public static final String TEST_IMAGES_FILE = "src\\main\\resource\\data\\mnist\\t10k-images.idx3-ubyte";
    public static final String TEST_LABELS_FILE = "src\\main\\resource\\data\\mnist\\t10k-labels.idx1-ubyte";

    /**
     * change bytes into a hex string.
     *
     * @param bytes bytes
     * @return the returned hex string
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < bytes.length; i++) {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if (hex.length() < 2) {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }

    /**
     * @return get the mnist data set
     */
    private static Pair getDataSet(String imagePath, String labelPath, int size) {
        //获取数据
        double[][] images = getImages(imagePath, size);
        double[] labels = getLabels(labelPath, size);
        images = enhanceImage(images);
        double[][] labelOneHot = oneHotEncode(labels, 0, 9);
        return new Pair(images, labelOneHot);
    }

    /**
     * @param size 获取的训练集数量
     * @return 返回mnist训练数据集
     */
    public static Pair getTrainData(int size) {
        return getDataSet(TRAIN_IMAGES_FILE, TRAIN_LABELS_FILE, size);
    }

    /**
     * @param size 获取的训练集数量
     * @return 返回mnist测试数据集
     */
    public static Pair getTestData(int size) {
        return getDataSet(TEST_IMAGES_FILE, TEST_LABELS_FILE, size);
    }

    /**
     * enhance images
     *
     * @param images the picture need to be enhanced
     * @return enhanced picture
     */
    private static double[][] enhanceImage(double[][] images) {
        double[][] newImages = new double[images.length][images[0].length];
        for (int rowIndex = 0; rowIndex < images.length; rowIndex++) {
            for (int columnIndex = 0; columnIndex < images[0].length; columnIndex++) {
                //将图片映射到0或1
                newImages[rowIndex][columnIndex] = Math.round(images[rowIndex][columnIndex] / 255);
            }
        }
        return newImages;
    }

    /**
     * @param labels 标签
     * @param start  编码开始
     * @param end    编码结束（包含）
     * @return 返回oneHot编码结果
     */
    private static double[][] oneHotEncode(double[] labels, int start, int end) {
        double[][] encodeRes = new double[labels.length][end - start + 1];
        for (int labelIndex = 0; labelIndex < labels.length; labelIndex++) {
            for (int i = start; i <= end; i++) {
                if (i == (int) labels[labelIndex]) {
                    encodeRes[labelIndex][i] = 1;
                } else {
                    encodeRes[labelIndex][i] = 0;
                }
            }
        }
        return encodeRes;
    }

    /**
     * get images of 'train' or 'test'
     *
     * @param fileName the file of 'train' or 'test' about image
     * @return one row show a `picture`
     */
    private static double[][] getImages(String fileName, int size) {
        double[][] imageValue;
        try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] bytes = new byte[4];
            bin.read(bytes, 0, 4);
            if (!"00000803".equals(bytesToHex(bytes))) {                        // 读取魔数
                throw new RuntimeException("Please select the correct file!");
            } else {
                bin.read(bytes, 0, 4);
                int number = Math.min(size, Integer.parseInt(bytesToHex(bytes), 16));           // 读取样本总数
                bin.read(bytes, 0, 4);
                int xPixel = Integer.parseInt(bytesToHex(bytes), 16);           // 读取每行所含像素点数
                bin.read(bytes, 0, 4);
                int yPixel = Integer.parseInt(bytesToHex(bytes), 16);           // 读取每列所含像素点数
                imageValue = new double[number][xPixel * yPixel];
                for (int i = 0; i < number; i++) {
                    double[] element = new double[xPixel * yPixel];
                    for (int j = 0; j < xPixel * yPixel; j++) {
                        element[j] = bin.read();                                // 逐一读取像素值
                    }
                    imageValue[i] = element;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return imageValue;
    }

    /**
     * get labels of `train` or `test`
     *
     * @param fileName the file of 'train' or 'test' about label
     * @return
     */
    public static double[] getLabels(String fileName, int size) {
        double[] y;
        try (BufferedInputStream bin = new BufferedInputStream(new FileInputStream(fileName))) {
            byte[] bytes = new byte[4];
            bin.read(bytes, 0, 4);
            if (!"00000801".equals(bytesToHex(bytes))) {
                throw new RuntimeException("Please select the correct file!");
            } else {
                bin.read(bytes, 0, 4);
                int number = Math.min(size, Integer.parseInt(bytesToHex(bytes), 16));
                y = new double[number];
                for (int i = 0; i < number; i++) {
                    y[i] = bin.read();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return y;
    }

    /**
     * draw a gray picture and the image format is JPEG.
     *
     * @param pixelValues pixelValues and ordered by column.
     * @param width       width
     * @param high        high
     * @param fileName    image saved file.
     */
    public static void drawGrayPicture(int[] pixelValues, int width, int high, String fileName) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(width, high, BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < high; j++) {
                int pixel = 255 - pixelValues[i * high + j];
                int value = pixel + (pixel << 8) + (pixel << 16);   // r = g = b 时，正好为灰度
                bufferedImage.setRGB(j, i, value);
            }
        }
        ImageIO.write(bufferedImage, "JPEG", new File(fileName));
    }


    public static void main(String[] args) {
        Pair trainDataSet = getTrainData(500);
        double[][] inputSet = trainDataSet.getInputSet();
        double[][] labelsSet = trainDataSet.getLabelsSet();
        System.out.println(inputSet.length);
    /*    StringBuilder stringBuilder = new StringBuilder();
        for (int sampleIndex = 0; sampleIndex < 3; sampleIndex++) {
            for (int index = 0; index < inputSet[sampleIndex].length; index++) {
                stringBuilder.append((int) inputSet[sampleIndex][index]).append(" ");
                if ((index + 1) % 28 == 0) {
                    stringBuilder.append("\n");
                }
            }
            stringBuilder.append(Arrays.toString(labelsSet[sampleIndex])).append("\n");
        }
        System.out.println(stringBuilder);*/
    }

    private static void printTest() {
        double[][] images = getImages(TEST_IMAGES_FILE, 10);
        StringBuilder stringBuilder = new StringBuilder();
        int target = 10;
        int[] singleImage = new int[images[target].length];
        for (int index = 0; index < images[target].length; index++) {
            singleImage[index] = (int) images[target][index];

            stringBuilder.append(Math.round(images[target][index] / 255)).append(" ");
            if ((index + 1) % 28 == 0) {
                stringBuilder.append("\n");
            }
        }
        System.out.println(stringBuilder);
        try {
            drawGrayPicture(singleImage, 28, 28, "test2.jpeg");
        } catch (IOException e) {
            e.printStackTrace();
        }
        double[] labels = getLabels(TEST_LABELS_FILE, 10);
        ;
        System.out.println(Arrays.toString(labels));

        System.out.println(Arrays.deepToString(oneHotEncode(labels, 0, 9)));
    }
}
