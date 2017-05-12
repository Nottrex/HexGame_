package client.window.components;

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

		for(int i = 0; i <  lines; i++) {
			String toDraw = text.split("\n")[i];
			if (centerText) fWidth = (int) g.getFontMetrics(font).getStringBounds(toDraw, g).getWidth();
			int y = fHeight*(i + 1);
			int x = (getWidth()-fWidth)/2;
			g.drawString(toDraw, x, y);
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
