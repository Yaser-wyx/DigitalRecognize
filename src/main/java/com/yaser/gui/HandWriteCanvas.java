/*
 * Copyright (c) 2020. yaser. All rights reserved
 * Description:
 */

package com.yaser.gui;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

class HandWriteCanvas extends Canvas implements MouseListener, MouseMotionListener {

    private Point now;
    private Graphics graphics;
    private final int targetSize = 28;
    private double pixel[][] = new double[targetSize][targetSize];
    private int rate;

    public double[][] getPixel() {
        return pixel;
    }

    public HandWriteCanvas(int paintSize) {
        super();
        this.rate = paintSize / targetSize;
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
    }

    @Override
    public void update(Graphics g) {
        super.update(g);
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    public void repaint() {
        super.repaint();
        for (int i = 0; i < targetSize; i++) {
            for (int j = 0; j < targetSize; j++) {
                pixel[i][j] = 0;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void mouseDragged(MouseEvent e) {
        now = new Point(e.getX(), e.getY());
        graphics = this.getGraphics();
        graphics.setColor(Color.WHITE);
        if (now != null) {
            graphics.fillOval(now.x, now.y, 5, 5);
            graphics.setColor(Color.WHITE);
            int x = now.x / this.rate, y = now.y / this.rate;
            pixel[y][x] = 1;
            if (y + 1 < targetSize && x + 1 < targetSize) {
                pixel[y + 1][x + 1] = 1;
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {

    }
}

