package client.window.view;

import client.Options;
import client.game.Controller;
import client.window.components.CustomTextField;
import client.window.components.ImageButton;
import client.window.components.TextButton;
import client.window.GUIConstants;
import client.window.TextureHandler;
import client.window.Window;
import game.map.presets.MapPreset;
import client.i18n.LanguageHandler;
import server.ServerMain;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ViewServerCreate extends View {
    private Window window;
    private Controller controller;

    private ImageButton buttonOptions;
    private TextButton buttonConnect, buttonBackToMainMenu, buttonServerOptions;
    private CustomTextField textFieldName, textFieldPort;

    private JPanel panel;
    private DynamicBackground background;
    private MapPreset mp;
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

        buttonOptions = new ImageButton(window, TextureHandler.getImagePng("Options"), e->window.updateView(new ViewOptions(window, background, this)));
        buttonServerOptions = new TextButton(window, LanguageHandler.get("Advanced Settings"), e->window.updateView(new ViewServerOptions(window, this, background)));
        buttonConnect = new TextButton(window, LanguageHandler.get("Create Game"), e ->
        {
            if (textFieldName.getText().isEmpty()) return;

            int port = -1;

            try {
                port = Integer.valueOf(textFieldPort.getText());
            } catch (Exception e2) {}

            Options.LAST_USERNAME = textFieldName.getText();
            Options.LAST_IP = "localhost";
            Options.LAST_PORT = textFieldPort.getText();

            if(mp == null) window.updateView(new ViewGameSetup(new ServerMain(port), background, textFieldName.getText(), "localhost", port));
            else window.updateView(new ViewGameSetup(new ServerMain(mp, port), background, textFieldName.getText(), "localhost", port));
        });
        buttonBackToMainMenu = new TextButton(window, LanguageHandler.get("Back to Mainmenu"), e -> window.updateView(new ViewMainMenu(background)));
        textFieldName = new CustomTextField("Name", CustomTextField.KEY_RESTRICT_NORMAL);
        textFieldPort = new CustomTextField("Port", CustomTextField.KEY_RESTRICT_ONLY_DIGITS);

        if(Options.LAST_PORT != null) textFieldPort.setText(Options.LAST_PORT);
        if(Options.LAST_USERNAME != null) textFieldName.setText(Options.LAST_USERNAME);

        changeSize();

        window.getPanel().add(textFieldName);
        window.getPanel().add(textFieldPort);
        window.getPanel().add(buttonConnect);
        window.getPanel().add(buttonOptions);
        window.getPanel().add(buttonServerOptions);
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

        buttonOptions.setBounds(5, 5, height/12, height/12);
        buttonServerOptions.setBounds(5, 500, height/12, height/12);
        textFieldName.setBounds((width-elementWidth)/2, (height-5*elementHeight)/2, elementWidth, elementHeight);
        textFieldPort.setBounds((width-elementWidth)/2, (height-3*elementHeight)/2, elementWidth, elementHeight);
        buttonConnect.setBounds((width-elementWidth)/2, (height-elementHeight)/2, elementWidth, elementHeight);
        buttonServerOptions.setBounds((width-elementWidth)/2, (height+2*elementHeight)/2, elementWidth, elementHeight);
        buttonBackToMainMenu.setBounds((width-elementWidth)/2, (height+5*elementHeight)/2, elementWidth, elementHeight);
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

    public void setPreset(MapPreset mp) {
        this.mp = mp;
    }

    @Override
    public void stop() {
        started = false;
    }
}
