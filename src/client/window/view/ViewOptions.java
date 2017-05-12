package client.window.view;

import client.game.Controller;
import client.audio.AudioConstants;
import client.window.components.CheckBox;
import client.window.components.HorizontalSlider;
import client.window.components.TextButton;
import client.window.components.TextLabel;
import client.window.GUIConstants;
import client.window.TextureHandler;
import client.window.Window;
import client.i18n.LanguageLoader;
import client.i18n.Strings;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ViewOptions extends View {

    private Window window;
    private DynamicBackground background;

    private TextButton button_accept, button_cancel;

    private Object newAntialiasing;
    private CheckBox box_antialising;
    private TextLabel text_antialiasing;

    private HorizontalSlider volumeMusic;
    private float newMusicVolume;
    private TextLabel text_volumeMusic;

    private HorizontalSlider volumeEffects;
    private float newEffectsVolume;
    private TextLabel text_volumeEffects;

    private TextButton button_change_lang;

    private boolean started = false;

    private List<String> languages;
    private int langIndex;

    public ViewOptions (Window window, DynamicBackground background) {
        this.background = background;
        this.window = window;

        languages = new ArrayList<>();
        languages.add("English");
        langIndex = 0;
    }

    public void draw() {
        if (!started) return;
        JPanel panel = window.getPanel();

        BufferedImage buffer = background.draw(panel.getWidth(), panel.getHeight());

        Graphics g = buffer.getGraphics();

        for (Component component: panel.getComponents()) {
            g.translate(component.getX(), component.getY());
            component.update(g);
            g.translate(-component.getX(), -component.getY());
        }

        panel.getGraphics().drawImage(buffer, 0, 0, null);
    }

    @Override
    public void init(Window window, Controller controller) {
        File d = new File(LanguageLoader.LANGUAGE_FOLDER);
        if(d.exists()) {
            for (File f: d.listFiles()) {
                languages.add(f.getName().split("\\.")[0]);
            }
        }else d.mkdirs();

        for(int i = 0; i < languages.size(); i++) {
            if(languages.get(i).equals(LanguageLoader.language)) {
                langIndex = i;
                break;
            }
        }

        newMusicVolume = AudioConstants.MUSIC_VOLUME;
        newEffectsVolume = AudioConstants.EFFECT_VOLUME;

        window.getPanel().setLayout(null);

        TextureHandler.loadImagePng("Check", "ui/buttons/checkmark");

        button_change_lang = new TextButton(window, languages.get(langIndex), e -> {
           if(langIndex + 1 < languages.size()) langIndex+=1;
            else langIndex = 0;
            button_change_lang.setText(languages.get(langIndex));
        });

        button_accept = new TextButton(window, Strings.get("Accept"), e -> {
            if(newAntialiasing != null) GUIConstants.VALUE_ANTIALIASING = newAntialiasing;

            AudioConstants.EFFECT_VOLUME = newEffectsVolume;
            AudioConstants.MUSIC_VOLUME = newMusicVolume;

            LanguageLoader.language = languages.get(langIndex);
            LanguageLoader.load();

            window.updateView(new ViewMainMenu(background));
        });
        button_cancel = new TextButton(window, Strings.get("Cancel"),e -> window.updateView(new ViewMainMenu(background)));


        box_antialising = new CheckBox(window, GUIConstants.VALUE_ANTIALIASING.equals(RenderingHints.VALUE_ANTIALIAS_ON), e -> {
            if(box_antialising.isChecked()) newAntialiasing = RenderingHints.VALUE_ANTIALIAS_ON;
            else newAntialiasing = RenderingHints.VALUE_ANTIALIAS_OFF;
        });

        text_antialiasing = new TextLabel(new TextLabel.Text() {
            @Override
            public String getText() {
                return Strings.get("Use AA");
            }
        }, false);


        text_volumeMusic = new TextLabel(new TextLabel.Text() {
            @Override
            public String getText() {
                return Strings.get("Music Volume");
            }
        }, false);
        volumeMusic = new HorizontalSlider( (AudioConstants.MUSIC_VOLUME - AudioConstants.MIN_VOLUME)/Math.abs(AudioConstants.MAX_VOLUME - AudioConstants.MIN_VOLUME), e -> {

            float musicDistance = Math.abs(AudioConstants.MAX_VOLUME - AudioConstants.MIN_VOLUME);
            newMusicVolume =(float)(musicDistance* volumeMusic.getValue() + AudioConstants.MIN_VOLUME);
        });

        text_volumeEffects = new TextLabel(new TextLabel.Text() {
            @Override
            public String getText() {
                return Strings.get("Effects Volume");
            }
        }, false);
        volumeEffects = new HorizontalSlider( (AudioConstants.EFFECT_VOLUME - AudioConstants.MIN_VOLUME)/Math.abs(AudioConstants.MAX_VOLUME - AudioConstants.MIN_VOLUME), e -> {

            float musicDistance = Math.abs(AudioConstants.MAX_VOLUME - AudioConstants.MIN_VOLUME);
            newEffectsVolume =(float)(musicDistance* volumeEffects.getValue() + AudioConstants.MIN_VOLUME);
        });

        changeSize();

        window.getPanel().add(button_accept);
        window.getPanel().add(button_cancel);

        window.getPanel().add(box_antialising);
        window.getPanel().add(text_antialiasing);

        window.getPanel().add(volumeMusic);
        window.getPanel().add(text_volumeMusic);

        window.getPanel().add(volumeEffects);
        window.getPanel().add(text_volumeEffects);

        window.getPanel().add(button_change_lang);

        started = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (started) {
                    draw();
                }
            }
        }).start();
    }

    @Override
    public void changeSize() {
        int width = window.getWidth();
        int height = window.getHeight();

        int componentHeight = height/12;
        int componentWidth = componentHeight * 5;

        box_antialising.setBounds(5, 5, componentHeight, componentHeight);
        text_antialiasing.setBounds(10 + componentHeight, componentHeight/2 - 5, componentWidth, componentHeight);

        button_accept.setBounds(width/2 - componentWidth - 5, height - 2*componentHeight, componentWidth, componentHeight);
        button_cancel.setBounds(width/2 + 5, height - 2*componentHeight, componentWidth, componentHeight);

        text_volumeMusic.setBounds(10 + componentWidth, 10+componentHeight, componentWidth/2, componentHeight);
        volumeMusic.setBounds(5, 10 + componentHeight, componentWidth, componentHeight/2);

        text_volumeEffects.setBounds((int)(2.5f*componentWidth+20), 10+componentHeight, componentWidth/2, componentHeight);
        volumeEffects.setBounds((int)(1.5f*componentWidth + 15), 10 + componentHeight, componentWidth, componentHeight/2);

        button_change_lang.setBounds(5, 15 + 2* componentHeight, componentWidth, componentHeight);
    }

    @Override
    public void stop() {
        started = false;
    }
}
