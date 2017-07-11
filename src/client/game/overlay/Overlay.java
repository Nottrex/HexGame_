package client.game.overlay;

import javax.swing.*;
import java.awt.*;

public abstract class Overlay extends JComponent {

	public abstract boolean destroyable();

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

}