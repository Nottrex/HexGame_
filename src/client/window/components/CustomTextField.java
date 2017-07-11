package client.window.components;

import client.Options;
import client.window.GUIConstants;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class CustomTextField extends JComponent {
	public static int BLINK_TIME = 1000;

	public static KeyRestrict KEY_RESTRICT_ONLY_DIGITS = c -> Character.isDigit(c);
	public static KeyRestrict KEY_RESTRICT_NORMAL = c -> Character.isDigit(c) || Character.isAlphabetic(c);
	public static KeyRestrict KEY_RESTRICT_NORMAL_OR_DOT = c -> Character.isDigit(c) || Character.isAlphabetic(c) || c == '.';
	public static KeyRestrict KEY_RESTRICT_EVERYTHING = c -> true;

	private String text;
	private String backgroundText;
	private KeyRestrict keyRestrict;
	private long time_start;

	public CustomTextField(String backgroundText, KeyRestrict keyRestrict) {
		this.backgroundText = backgroundText;
		this.keyRestrict = keyRestrict;
		time_start = System.currentTimeMillis();
		text = "";

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				requestFocusInWindow();
				time_start = System.currentTimeMillis();
			}
		});

		this.addKeyListener(new KeyAdapter() {

			@Override
			public void keyTyped(KeyEvent e) {
				if (e.getKeyChar() == '\b') {
					if (text.length() > 0) {
						text = text.substring(0, text.length() - 1);
					}
				} else {
					if (keyRestrict.isAllowed(e.getKeyChar())) {
						text += e.getKeyChar();
					}
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_V) {
					Clipboard c = Toolkit.getDefaultToolkit().getSystemClipboard();
					Transferable t = c.getContents(this);

					if (t == null)
						return;
					try {
						String data = (String) t.getTransferData(DataFlavor.stringFlavor);
						for (char ch : data.toCharArray()) {
							if (keyRestrict.isAllowed(ch)) {
								text += ch;
							}
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				} else if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
					text = "";
				}
			}
		});
		setFocusable(true);
		setRequestFocusEnabled(true);
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, Options.VALUE_ANTIALIASING);
		g2.setColor(GUIConstants.BUTTON_COLOR);

		String text = getText();

		Font font = GUIConstants.FONT.deriveFont(getHeight() * 0.5f);
		if (text.isEmpty() && !hasFocus()) {
			text = backgroundText;
			font = font.deriveFont(Font.PLAIN);
		}
		g.setFont(font);

		int fWidth = (int) g.getFontMetrics(font).getStringBounds(text, g).getWidth();

		int rightMove = 0;
		if (fWidth > getWidth() - 15) {
			rightMove = fWidth - getWidth() + 15;
		}

		g.drawString(text, -rightMove + 5, getHeight() / 2);

		g2.setColor(GUIConstants.BUTTON_COLOR);
		g2.setStroke(new BasicStroke(GUIConstants.BUTTON_LINE_WIDTH));

		g2.drawLine(0, (7 * this.getHeight()) / 10, getWidth(), (7 * this.getHeight()) / 10);

		if (hasFocus() && ((System.currentTimeMillis() - time_start) / BLINK_TIME) % 2 == 0) {
			g2.drawLine(fWidth - rightMove + font.getSize() / 4, 0, fWidth - rightMove + font.getSize() / 4, (3 * this.getHeight()) / 5);
		}

		g2.setStroke(new BasicStroke(1));
	}

	/**
	 * @return user input on that textfield
	 */
	public String getText() {
		return text;
	}

	/**
	 * Changes the shown text
	 *
	 * @param text that should be shown
	 */
	public void setText(String text) {
		this.text = text;
	}

	/*
	 * Determines allowed key inputs
	 */
	public interface KeyRestrict {
		boolean isAllowed(char c);
	}
}
