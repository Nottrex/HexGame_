package view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class ImageButton extends JComponent {
	private BufferedImage normalImage;
	private BufferedImage hoverImage;
	private BufferedImage clickImage;

	private boolean clicked = false;
	private boolean entered = false;

	private ActionListener actionListener;

	public ImageButton(BufferedImage image, ActionListener actionListener) {
		this(image, image, image, actionListener);
	}

	public ImageButton(BufferedImage normalImage, BufferedImage hoverImage, BufferedImage clickImage, ActionListener actionListener) {
		this.normalImage = normalImage;
		this.hoverImage = hoverImage;
		this.clickImage = clickImage;

		this.actionListener = actionListener;

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				clicked = true;
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				clicked = false;
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
		Graphics2D g2 = (Graphics2D) g;

		if (entered) {
			if (clicked) {
				g2.drawImage(clickImage, 0, 0, getWidth(), getHeight(), null);
			} else {
				g2.drawImage(hoverImage, 0, 0, getWidth(), getHeight(), null);
			}
		} else {
			g2.drawImage(normalImage, 0, 0, getWidth(), getHeight(), null);
		}
	}

	public void setNormalImage(BufferedImage normalImage) {
		this.normalImage = normalImage;
	}

	public void setHoverImage(BufferedImage hoverImage) {
		this.hoverImage = hoverImage;
	}

	public void setClickImage(BufferedImage clickImage) {
		this.clickImage = clickImage;
	}

	public void setImage(BufferedImage image) {
		this.normalImage = image;
		this.hoverImage = image;
		this.clickImage = image;
	}
}
