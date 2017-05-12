package client.window;

import client.window.view.View;
import client.game.Controller;
import client.Options;
import client.audio.AudioHandler;
import client.audio.AudioPlayer;
import client.window.view.ViewMainMenu;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Window extends JFrame {
	private static final long serialVersionUID = 1L;

	//Window stuff
	private JPanel panel;

	private boolean stop = false;

	private Controller controller;
	private AudioPlayer uiPlayer = new AudioPlayer();
	private View view;

	public Window() {
		super("HexGame");
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		initComponents();

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				if (view != null) view.changeSize();
			}
		});

		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setUndecorated(false);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Options.save();
			}
		});

		controller = new Controller();
		updateView(new ViewMainMenu());
	}


	public JPanel getPanel() {
		return panel;
	}

	/**
	 *
	 * @param view to check
	 * @return if view is current {@link View}
	 */
	public boolean isCurrentView(View view) {
		return this.view == view;
	}

	/**
	 * Switches current {@link View} to a new one
	 * @param newView
	 */
	public void updateView(View newView) {
		if (this.view != null) this.view.stop();
		this.view = newView;
		panel.removeAll();

		newView.init(this, controller);
		if (panel.getLayout() != null) panel.doLayout();
	}

	/**
	 * Called on start
	 * Prepares window
	 */
	private void initComponents() {
		panel = new JPanel(new BorderLayout());
		panel.setIgnoreRepaint(true);
		this.setContentPane(panel);

		AudioHandler.loadMusicWav("Click", "sounds/click");

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		TextureHandler.loadImagePng("cursor","ui/cursor");
		Cursor c = toolkit.createCustomCursor(TextureHandler.getImagePng("cursor") , new Point(0, 0), "img");
		this.setCursor(c);
	}

	public AudioPlayer getPlayer() {
		return uiPlayer;
	}
}
