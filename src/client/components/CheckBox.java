package client.components;

import client.window.GUIConstants;
import client.window.TextureHandler;
import client.window.Window;


import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class CheckBox extends JComponent {
    private BufferedImage image;

    private boolean entered = false;
    private boolean activated = false;



    public CheckBox(Window w, ActionListener actionListener) {
        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (entered) {
                    activated = !activated;

                    if(!activated) setImage(null);
                    else setImage(TextureHandler.getImagePng("Check"));

                    w.getPlayer().playAudio("Click");
                    actionListener.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, null));
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                entered = true;
            }

            @Override
            public void mouseExited(MouseEvent e) {
                entered = false;
            }
        });
    }


    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.WHITE);
        if (entered) {
            if(image != null) g.drawImage(image, (int) (getWidth()*(1 - GUIConstants.BUTTON_HOVER_SIZE)/2), (int) (getHeight()*(1 - GUIConstants.BUTTON_HOVER_SIZE)/2), (int) (getWidth() * GUIConstants.BUTTON_HOVER_SIZE), (int) (getHeight() * GUIConstants.BUTTON_HOVER_SIZE), null);
            g.drawRect((int)(getWidth()*(1 - GUIConstants.BUTTON_HOVER_SIZE)/2), (int) (getHeight()*(1 - GUIConstants.BUTTON_HOVER_SIZE)/2), (int) (getWidth() * GUIConstants.BUTTON_HOVER_SIZE), (int) (getHeight() * GUIConstants.BUTTON_HOVER_SIZE));
        } else {
            if(image != null) g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
            g.drawRect(0, 0, getWidth()-1, getHeight()-1);
        }
    }

    /**
     * Changes the shown image
     * @param image that should be shown
     */
    private void setImage(BufferedImage image) {
        this.image = image;
    }
}
