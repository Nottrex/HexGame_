package client.window.view;

import client.Controller;
import client.components.CheckBox;
import client.components.TextButton;
import client.components.TextLabel;
import client.window.GUIConstants;
import client.window.TextureHandler;
import client.window.View;
import client.window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ViewOptions extends View {

    private Window window;
    private DynamicBackground background;

    private TextButton button_accept, button_cancel;
    private CheckBox box_antialising;
    private TextLabel text_antialiasing;
    /*
        private Slider volumeMusic, volumeEffects;
     */

    private Object newAntialiasing;

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

        TextureHandler.loadImagePng("Check", "ui/buttons/checkmark");

        button_accept = new TextButton(window, "Accept", e -> {
            window.updateView(new ViewMainMenu(background));
            if(newAntialiasing != null) GUIConstants.VALUE_ANTIALIASING = newAntialiasing;
        });
        button_cancel = new TextButton(window, "Cancel", e -> window.updateView(new ViewMainMenu(background)));
        box_antialising = new CheckBox(window, GUIConstants.VALUE_ANTIALIASING.equals(RenderingHints.VALUE_ANTIALIAS_ON), e -> {
            if(box_antialising.isChecked()) newAntialiasing = RenderingHints.VALUE_ANTIALIAS_ON;
            else newAntialiasing = RenderingHints.VALUE_ANTIALIAS_OFF;
        });
        text_antialiasing = new TextLabel(new TextLabel.Text() {
            @Override
            public String getText() {
                return "Use AntiAliasing";
            }
        }, false);

        window.getPanel().add(button_accept);
        window.getPanel().add(button_cancel);
        window.getPanel().add(box_antialising);
        window.getPanel().add(text_antialiasing);

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

        box_antialising.setBounds(5, 5, componentHeight, componentHeight);
        text_antialiasing.setBounds(10 + componentHeight, componentHeight/2 - 5, componentWidth, componentHeight);
        button_accept.setBounds(width/2 - componentWidth - 5, height - 2*componentHeight, componentWidth, componentHeight);
        button_cancel.setBounds(width/2 + 5, height - 2*componentHeight, componentWidth, componentHeight);
    }

    @Override
    public void stop() {
        started = false;
    }
}
