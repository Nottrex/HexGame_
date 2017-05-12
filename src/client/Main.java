package client;

import client.i18n.LanguageLoader;
import client.window.Window;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Options.load();
        LanguageLoader.load();
        SwingUtilities.invokeLater(() -> new Window());
    }
}