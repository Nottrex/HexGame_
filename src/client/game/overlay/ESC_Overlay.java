package client.game.overlay;

import client.game.ViewGame;
import client.i18n.LanguageHandler;
import client.window.Window;
import client.window.components.TextButton;
import client.window.view.DynamicBackground;
import client.window.view.ViewMainMenu;
import client.window.view.ViewOptions;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ESC_Overlay extends  Overlay{

    private Window w;

    private TextButton button_BackToGame;
    private TextButton button_ToOptions;
    private TextButton button_quit;

    public ESC_Overlay(Window w, ViewGame g) {

        this.w = w;
        setBounds(0, 0, w.getPanel().getWidth(), w.getPanel().getHeight());

        button_BackToGame = new TextButton(w, LanguageHandler.get("Back"), e -> {g.setOverlay(null); g.unhideButtons();});
        this.add(button_BackToGame);

        button_ToOptions = new TextButton(w, LanguageHandler.get("Options"), e -> {w.updateView(new ViewOptions(w, new DynamicBackground()));});
        this.add(button_ToOptions);

        button_quit = new TextButton(w, LanguageHandler.get("Back to Mainemnu"), e -> {w.updateView(new ViewMainMenu());});
        this.add(button_quit);
        changeSize();

        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, w.getPanel().getWidth(), w.getHeight());
    }

    public void changeSize() {
        if (button_ToOptions == null || button_BackToGame == null) return;

        int width = w.getPanel().getWidth();
        int height = w.getPanel().getHeight();

        int buttonHeight = height/8;
        int buttonWidth = buttonHeight*5;

        button_BackToGame.setBounds((width-buttonWidth)/2, (height-buttonHeight)/4, buttonWidth, buttonHeight);
        button_ToOptions.setBounds((width-buttonWidth)/2, (height+4*buttonHeight)/4, buttonWidth, buttonHeight);
        button_quit.setBounds((width-buttonWidth)/2, (height+9*buttonHeight)/4, buttonWidth, buttonHeight);
    }
}