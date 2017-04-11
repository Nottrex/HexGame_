package client.window.view;

import client.Controller;
import client.components.TextButton;
import client.window.View;
import client.window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ViewOptions extends View {

    private Window window;
    private DynamicBackground background;

    private TextButton button_accept, button_cancel;
    /*
        private Slider volumeMusic, volumeEffects;
        private CheckBox antialiasing;
     */

    private boolean started = false;

    public ViewOptions (Window window, DynamicBackground background) {
        this.background = background;
        this.window = window;
    }

    public void draw() {
        if (!started) return;
        JPanel panel = window.getPanel();

        BufferedImage buffer = background.draw(panel.getWidth(), panel.getHeight());

        Graphics g = buffer.getGraphics();

        for (Component component: panel.getComponents()) {
            g.translate(component.getX(), component.getY());
            component.update(g);
            g.translate(-component.getX(), -component.getY());
        }

        panel.getGraphics().drawImage(buffer, 0, 0, null);
    }

    @Override
    public void init(Window window, Controller controller) {

        button_accept = new TextButton(window, "Accept", e -> window.updateView(new ViewMainMenu(background)));
        button_cancel = new TextButton(window, "Cancel", e -> window.updateView(new ViewMainMenu(background)));

        window.getPanel().add(button_accept);
        window.getPanel().add(button_cancel);

        changeSize();

        started = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (started) {
                    draw();
                }
            }
        }).start();
    }

    @Override
    public void changeSize() {
        int width = window.getWidth();
        int height = window.getHeight();

        int componentHeight = height/12;
        int componentWidth = componentHeight * 5;

        button_accept.setBounds(width/2 - componentWidth - 5, height - 2*componentHeight, componentWidth, componentHeight);
        button_cancel.setBounds(width/2 + 5, height - 2*componentHeight, componentWidth, componentHeight);
    }

    @Override
    public void stop() {
        started = false;
    }
}
