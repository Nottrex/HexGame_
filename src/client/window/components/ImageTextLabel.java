package client.window.components;

import client.window.GUIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageTextLabel extends JComponent {

	private ImageText content;

	public ImageTextLabel(ImageText content) {
		this.content = content;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(content.getImage(), 0, 0, getWidth(), getHeight(), null);

		g.setColor(Color.WHITE);

		String text = content.getText();

		Font font = GUIConstants.FONT.deriveFont(getHeight() * 0.5f);
		g.setFont(font);

		double fWidth = g.getFontMetrics(font).getStringBounds(text, g).getWidth();
		g.drawString(text, (int) ((getWidth() - fWidth) / 2), getHeight() * 3 / 4 - 5);
	}

	public void setContent(ImageText content) {
		this.content = content;
	}

	/*
		Used to not call setContent()
	 */
	public interface ImageText {
		BufferedImage getImage();

		String getText();
	}
}
