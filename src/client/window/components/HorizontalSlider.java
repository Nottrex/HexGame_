package client.window.components;

import client.window.GUIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class HorizontalSlider extends JComponent {

    private double value;

    public HorizontalSlider(double startValue, ActionListener actionListener) {
        this.value = Math.max(Math.min(startValue, 1.0d), 0.0d);

        this.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                value = Math.max(Math.min((double)e.getX() / (double) getWidth(), 1.0d), 0.0d);
                actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            }
        });

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                value = Math.max(Math.min((double)e.getX() / (double) getWidth(), 1.0d), 0.0d);
                actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, GUIConstants.VALUE_ANTIALIASING);

        int sliderHeight = getHeight()/5;
        int x = (int) Math.round(value * getWidth());

        g.setColor(GUIConstants.COLOR_INFOBAR_BACKGROUND.brighter());
        g.fillRect(0, getHeight()/2-sliderHeight/2, getWidth(), sliderHeight);
        g.setColor(Color.WHITE);
        g.fillOval(x - getHeight()/2, 0, getHeight(), getHeight());
        g.fillRect(0, getHeight()/2-sliderHeight/2, x, sliderHeight);
    }

    public double getValue() {
        return value;
    }
}