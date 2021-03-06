package com.yaser.gui;

import com.yaser.train_model.DigitalModel;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class GUI extends JFrame implements ActionListener {

    private final HandWriteCanvas canvas;
    private final DigitalModel digitalModel;
    public GUI() {
        super("程序设计——神经网络");
        this.setSize(400, 521);
        this.setLayout(new FlowLayout());
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setLocationRelativeTo(null);

        JMenuBar menuBar = new JMenuBar();
        JMenu menu2 = new JMenu("选择颜色");

        JMenuItem menuItem4 = new JMenuItem("选择颜色");

        menuItem4.addActionListener(this);

        menu2.add(menuItem4);
        menuBar.add(menu2);

        canvas = new HandWriteCanvas(400);
        canvas.setBackground(Color.BLACK);
        canvas.setBounds(0, 50, 400, 400);

        JButton btn2 = new JButton("识别");
        JButton btn3 = new JButton("清除");

        btn2.addActionListener(this);
        btn3.addActionListener(this);

        btn2.setSize(200, 20);
        btn3.setSize(200, 20);

        this.add(menuBar);
        this.add(canvas);
        this.add(btn2);
        this.add(btn3);
        this.setVisible(true);
        this.setResizable(false);
        digitalModel = new DigitalModel();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "识别": {
                double[][] pixel = this.canvas.getPixel();//获取用户的输入
                //将输入转化为一个1*(28*28)的矩阵，用于模型输入
                double[][] digitalModelInput = new double[1][28 * 28];
                int digitalIndex = 0;
                for (int rowIndex = 0; rowIndex < 28; rowIndex++) {
                    for (int columnIndex = 0; columnIndex < 28; columnIndex++) {
                        digitalModelInput[0][digitalIndex++] = pixel[rowIndex][columnIndex];
                    }
                }
                //将转化后的数据输入模型
                int val = digitalModel.predictSingleDigital(digitalModelInput);
                //显示预测结果
                JOptionPane.showMessageDialog(canvas, "预测数字为：" + val);
                this.canvas.repaint();
                break;
            }
            case "清除":
            case "新建":
                this.canvas.repaint();
                break;
            case "退出":
                System.out.println(e.getActionCommand());
                System.exit(0);
            case "选择颜色":
                Color color = JColorChooser.showDialog(this, "选择颜色", Color.black);
                System.out.println(this);
                this.canvas.setBackground(color);
                break;
        }
    }
}
