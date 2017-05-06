package client;

import client.window.Window;
import i18n.LanguageLoader;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Options.load();
        LanguageLoader.load();
        SwingUtilities.invokeLater(() -> new Window());
    }
}