package client.window;

import client.Controller;
import client.window.view.ViewMainMenu;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class Window extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;

	//Window stuff
	private JPanel panel;

	private int fps = 0;
	private boolean stop = false;

	private Controller controller;
	private View view;

	public Window() {
		super("HexGame");
		setMinimumSize(new Dimension(800, 600));
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		initComponents();

		setVisible(true);
		setVisible(true);

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
			}
		});

		new Thread(this).start();
		controller = new Controller();
		updateView(new ViewMainMenu());
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
					Thread.sleep(250);
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

	public boolean isCurrentView(View view) {
		return this.view == view;
	}

	public void updateView(View newView) {
		if (this.view != null) this.view.stop();
		this.view = newView;
		panel.removeAll();
		newView.init(this, controller);
		panel.updateUI();
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
