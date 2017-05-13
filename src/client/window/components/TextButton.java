package client.window.components;

import client.Options;
import client.window.GUIConstants;
import client.window.Window;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TextButton extends JComponent {
	private String text;

	private boolean entered = false;

	public TextButton(Window w, String text, ActionListener actionListener) {
		this.text = text;
		this.setPreferredSize(new Dimension(300, 50));

		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				if (entered) {
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
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, Options.VALUE_ANTIALIASING);

		int x = 0, y = 0, width = getWidth(), height = getHeight();
		if (entered) {
			x = (int) (width*(1- GUIConstants.BUTTON_HOVER_SIZE)/2);
			y = (int) (height*(1- GUIConstants.BUTTON_HOVER_SIZE)/2);
			width = (int) (width*GUIConstants.BUTTON_HOVER_SIZE);
			height = (int) (height*GUIConstants.BUTTON_HOVER_SIZE);
		}
		x += GUIConstants.BUTTON_LINE_WIDTH/2;
		y += GUIConstants.BUTTON_LINE_WIDTH/2;
		width -= GUIConstants.BUTTON_LINE_WIDTH;
		height -= GUIConstants.BUTTON_LINE_WIDTH;

		g2.setStroke(new BasicStroke(GUIConstants.BUTTON_LINE_WIDTH));
		g2.setColor(GUIConstants.BUTTON_COLOR);
		g2.drawRoundRect(x, y, width, height, GUIConstants.CORNER_RADIUS, GUIConstants.CORNER_RADIUS);
		g2.setStroke(new BasicStroke(1));

		Font font = GUIConstants.FONT.deriveFont(height*0.5f);
		g2.setFont(font);

		double fWidth = g2.getFontMetrics(font).getStringBounds(text, g).getWidth();
		g2.drawString(text, (int) ((width-fWidth)/2) + x, height*3/4+y);
	}

	/**
	 * Changes text on button
	 * @param text that should be displayed on the button
	 */
	public void setText(String text) {
		this.text = text;
	}
}
