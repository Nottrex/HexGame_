package client.window.view;

import client.FileHandler;
import client.Options;
import client.game.Controller;
import client.audio.AudioConstants;
import client.window.OptionComponents;
import client.window.components.CheckBox;
import client.window.components.HorizontalSlider;
import client.window.components.TextButton;
import client.window.components.TextLabel;
import client.window.GUIConstants;
import client.window.TextureHandler;
import client.window.Window;
import client.i18n.LanguageHandler;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ViewOptions extends View {

    private Window window;
    private DynamicBackground background;
    private View previousView;

    private OptionComponents options;

    private boolean started = false;


    public ViewOptions (Window window, DynamicBackground background, View previousView) {
        this.background = background;
        this.window = window;
        this.previousView = previousView;
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
        started = true;

        options = new OptionComponents(window, window.getPanel(), window.getWidth(), window.getHeight(), new OptionComponents.OptionFinishListener() {
            @Override
            public void onOptionsAccept() {
                window.updateView(previousView);
            }

            @Override
            public void onOptionsCancel() {
                window.updateView(previousView);
            }
        });

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

        options.changeSize(width, height);
    }

    @Override
    public void stop() {
        started = false;
    }
}
