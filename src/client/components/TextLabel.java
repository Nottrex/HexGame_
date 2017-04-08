package client.components;

import client.window.GUIConstants;

import javax.swing.*;
import java.awt.*;

public class TextLabel extends JComponent {

	private Text content;
	private boolean centerText;

	public TextLabel(Text content, boolean centerText) {
		this.content = content;
		this.centerText = centerText;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.WHITE);

		String text = content.getText();
		int lines = text.split("\n").length;

		Font font = GUIConstants.FONT.deriveFont(Math.min(getHeight()*0.375f / lines, getHeight()*0.25f));
		g.setFont(font);

		int fWidth = getWidth();
		int fHeight = g.getFontMetrics(font).getHeight();
		if (centerText) fWidth = (int) g.getFontMetrics(font).getStringBounds(text, g).getWidth();

		for(int i = 0; i <  lines; i++) {
			String toDraw = text.split("\n")[i];
			g.drawString(toDraw, (getWidth()-fWidth)/2, getY() + fHeight*(i + 1));
		}
	}

	public void setContent(Text content) {
		this.content = content;
	}

	/*
		Used to not call setContent()
	 */
	public interface Text {
		String getText();
	}
}
