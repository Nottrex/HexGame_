package client.game.overlay;

import client.game.ViewGame;
import client.window.GUIConstants;
import client.window.OptionComponents;
import client.window.Window;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class OptionsOverlay extends Overlay {

	private Window w;
	private ViewGame game;
	private OptionComponents options;

	public OptionsOverlay(Window window, ViewGame game) {
		this.w = window;
		this.game = game;

		options = new OptionComponents(w, this, game.getCenter().getWidth(), game.getCenter().getHeight(), new OptionComponents.OptionFinishListener() {
			@Override
			public void onOptionsAccept() {
				game.setOverlay(new ESC_Overlay(w, game));
				game.getPlayer().updateVolume();
			}

			@Override
			public void onOptionsCancel() {
				game.setOverlay(new ESC_Overlay(w, game));
			}
		});

		changeSize();

		this.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
			}
		});
	}

	@Override
	public boolean destroyable() {
		return false;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(GUIConstants.COLOR_OVERLAY_BACKGROUND);
		g.fillRect(0, 0, w.getPanel().getWidth(), w.getPanel().getHeight());
	}

	public void changeSize() {
		int width = w.getPanel().getWidth();
		int height = w.getPanel().getHeight() - game.getBottomHeigth();
		setBounds(0, 0, width, height);
		options.changeSize(width, height);
	}

}
