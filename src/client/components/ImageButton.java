package client.components;

import client.window.GUIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ImageButton extends JComponent {
	private BufferedImage image;

	private boolean entered = false;

	public ImageButton(BufferedImage image, ActionListener actionListener) {
		this.image = image;

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (entered) {
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

		if (entered) {
			g.drawImage(image, (int) (getWidth()*(1 - GUIConstants.BUTTON_HOVER_SIZE)/2), (int) (getHeight()*(1 - GUIConstants.BUTTON_HOVER_SIZE)/2), (int) (getWidth() * GUIConstants.BUTTON_HOVER_SIZE), (int) (getHeight() * GUIConstants.BUTTON_HOVER_SIZE), null);
		} else {
			g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		}
	}

	public void setImage(BufferedImage image) {
		this.image = image;
	}
}
