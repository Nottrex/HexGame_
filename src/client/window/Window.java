package client.window;

import client.Controller;
import client.KeyBindings;
import client.audio.AudioHandler;
import client.audio.AudioPlayer;
import client.window.view.ViewMainMenu;
import game.*;
import game.enums.Direction;
import game.enums.Field;
import game.enums.PlayerColor;
import game.enums.UnitType;
import game.map.GameMap;
import game.util.ActionUtil;
import client.components.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Optional;

import javax.sound.sampled.Clip;
import javax.swing.*;

public class Window extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;

	//Window stuff
	private JPanel panel;

	private Insets i;

	private int fps = 0;
	private boolean stop = false;

	private Controller controller;
	private View view;

	public Window() {
		super("HexGame");
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);

		i = getInsets();

		initComponents();

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
			}
		});

		new Thread(this).start();
		updateView(new ViewMainMenu());
	}

	public Insets getInsets() {
		return i;
	}

	public int getFPS() {
		return fps;
	}

	public JPanel getPanel() {
		return panel;
	}

	@Override
	public void run() {
		int i = 0;
		long t = System.currentTimeMillis();
		while (!stop) {
			i++;
			if (view != null && view.autoDraw()) {
				view.draw();
			} else {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {}

			}

			if (System.currentTimeMillis()-t > 500) {
				long t2 = System.currentTimeMillis();
				fps = (int) (i / ((t2-t)/1000.0));
				t = t2;
				i = 0;
			}
		}
	}

	public void updateView(View newView) {
		if (this.view != null) this.view.stop();
		this.view = newView;
		newView.init(this, controller);
	}

	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		this.setContentPane(panel);

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		TextureHandler.loadImagePng("cursor","ui/cursor");
		Cursor c = toolkit.createCustomCursor(TextureHandler.getImagePng("cursor") , new Point(0, 0), "img");
		this.setCursor(c);
	}
}
