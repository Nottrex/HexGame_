package client;

import client.window.Window;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Options.load();
        SwingUtilities.invokeLater(() -> new Window());
    }
}