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

		Font font = GUIConstants.FONT.deriveFont(getHeight()*0.5f);
		g.setFont(font);

		int fWidth = getWidth();
		if (centerText) fWidth = (int) g.getFontMetrics(font).getStringBounds(text, g).getWidth();

		g.drawString(text, (getWidth()-fWidth)/2, getHeight()/2);
	}

	public void setContent(Text content) {
		this.content = content;
	}

	public interface Text {
		String getText();
	}
}
