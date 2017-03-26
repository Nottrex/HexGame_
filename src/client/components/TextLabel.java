package client.components;

import javax.swing.*;
import java.awt.*;

public class TextLabel extends JComponent {

	private Text content;

	public TextLabel(Text content) {
		this.content = content;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.setColor(Color.WHITE);

		String text = content.getText();

		Font font = g.getFont().deriveFont(getHeight()*0.5f);
		g.setFont(font);

		g.drawString(text, 0, getHeight()/2);
	}

	public void setContent(Text content) {
		this.content = content;
	}

	public interface Text {
		String getText();
	}
}
