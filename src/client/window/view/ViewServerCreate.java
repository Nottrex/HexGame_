package client.window.view;

import client.Controller;
import client.components.CustomTextField;
import client.components.TextButton;
import client.window.View;
import client.window.Window;
import server.ServerMain;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ViewServerCreate extends View {
    private Window window;
    private Controller controller;

    private TextButton buttonConnect, buttonBackToMainMenu;
    private CustomTextField textFieldName, textFieldPort;

    private JPanel panel;
    private DynamicBackground background;
    private boolean started = false;

    public ViewServerCreate(DynamicBackground background) {
        this.background = background;
    }

    @Override
    public void init(Window window, Controller controller) {
        this.window = window;
        this.controller = controller;

        panel = window.getPanel();
        if(background == null) background = new DynamicBackground();
        window.getPanel().setLayout(null);

        buttonConnect = new TextButton("Create", e ->
        {
            if (textFieldName.getText().isEmpty()) return;

            int port = -1;

            try {
                port = Integer.valueOf(textFieldPort.getText());
            } catch (Exception e2) {}

            window.updateView(new ViewGameSetup(new ServerMain(port), background, textFieldName.getText(), "localhost", port));
        });
        buttonBackToMainMenu = new TextButton("Back to Main Menu", e -> window.updateView(new ViewMainMenu(background)));
        textFieldName = new CustomTextField("Name", CustomTextField.KEY_RESTRICT_NORMAL);
        textFieldPort = new CustomTextField("Port", CustomTextField.KEY_RESTRICT_ONLY_DIGITS);

        changeSize();

        window.getPanel().add(textFieldName);
        window.getPanel().add(textFieldPort);
        window.getPanel().add(buttonConnect);
        window.getPanel().add(buttonBackToMainMenu);

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
        int width = window.getPanel().getWidth();
        int height = window.getPanel().getHeight();

        int elementHeight = height/10;
        int elementWidth  = elementHeight*5;

        textFieldName.setBounds((width-elementWidth)/2, (height-5*elementHeight)/2, elementWidth, elementHeight);
        textFieldPort.setBounds((width-elementWidth)/2, (height-3*elementHeight)/2, elementWidth, elementHeight);
        buttonConnect.setBounds((width-elementWidth)/2, (height-elementHeight)/2, elementWidth, elementHeight);
        buttonBackToMainMenu.setBounds((width-elementWidth)/2, (height+2*elementHeight)/2, elementWidth, elementHeight);
    }

    /**
     * Draws this screen
     */
    public void draw() {
        if (!started) return;

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
    public void stop() {
        started = false;
    }
}