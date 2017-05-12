package client;

import client.i18n.LanguageHandler;
import client.window.Window;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        Options.load();
        LanguageHandler.load();
        SwingUtilities.invokeLater(() -> new Window());
    }
}