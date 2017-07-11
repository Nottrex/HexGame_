package client.window;

import client.Options;
import client.audio.AudioConstants;
import client.i18n.LanguageHandler;
import client.window.components.CheckBox;
import client.window.components.HorizontalSlider;
import client.window.components.TextButton;
import client.window.components.TextLabel;

import javax.swing.*;
import java.awt.*;

public class OptionComponents {
	private TextButton button_accept, button_cancel;

	private Object newAntialiasing;
	private CheckBox box_antialiasing;
	private TextLabel text_antialiasing;

	private HorizontalSlider volumeMusic;
	private float newMusicVolume;
	private TextLabel text_volumeMusic;

	private HorizontalSlider volumeEffects;
	private float newEffectsVolume;
	private TextLabel text_volumeEffects;

	private TextButton button_change_lang;

	private java.util.List<String> languages;
	private int langIndex;

	public OptionComponents(Window window, JComponent component, int width, int height, OptionFinishListener listener) {
		languages = LanguageHandler.availableLanguages();

		for(int i = 0; i < languages.size(); i++) {
			if(languages.get(i).equals(Options.language)) {
				langIndex = i;
				break;
			}
		}

		newMusicVolume = Options.MUSIC_VOLUME;
		newEffectsVolume = Options.EFFECT_VOLUME;

		component.setLayout(null);

		TextureHandler.loadImagePng("Check", "ui/buttons/checkmark");

		button_change_lang = new TextButton(window, languages.get(langIndex), e -> {
			if(langIndex + 1 < languages.size()) langIndex+=1;
			else langIndex = 0;
			button_change_lang.setText(languages.get(langIndex));
		});

		button_accept = new TextButton(window, LanguageHandler.get("Accept"), e -> {
			if(newAntialiasing != null) Options.VALUE_ANTIALIASING = newAntialiasing;

			Options.EFFECT_VOLUME = newEffectsVolume;
			Options.MUSIC_VOLUME = newMusicVolume;

			Options.language = languages.get(langIndex);
			LanguageHandler.load();

			listener.onOptionsAccept();
		});
		button_cancel = new TextButton(window, LanguageHandler.get("Cancel"), e -> listener.onOptionsCancel());


		box_antialiasing = new CheckBox(window, Options.VALUE_ANTIALIASING.equals(RenderingHints.VALUE_ANTIALIAS_ON), e -> {
			if(box_antialiasing.isChecked()) newAntialiasing = RenderingHints.VALUE_ANTIALIAS_ON;
			else newAntialiasing = RenderingHints.VALUE_ANTIALIAS_OFF;
		});

		text_antialiasing = new TextLabel(new TextLabel.Text() {
			@Override
			public String getText() {
				return LanguageHandler.get("Use AA");
			}
		}, false);


		text_volumeMusic = new TextLabel(new TextLabel.Text() {
			@Override
			public String getText() {
				return LanguageHandler.get("Music Volume");
			}
		}, false);
		volumeMusic = new HorizontalSlider( (Options.MUSIC_VOLUME - AudioConstants.MIN_VOLUME)/Math.abs(AudioConstants.MAX_VOLUME - AudioConstants.MIN_VOLUME), e -> {

			float musicDistance = Math.abs(AudioConstants.MAX_VOLUME - AudioConstants.MIN_VOLUME);
			newMusicVolume =(float)(musicDistance* volumeMusic.getValue() + AudioConstants.MIN_VOLUME);
		});

		text_volumeEffects = new TextLabel(new TextLabel.Text() {
			@Override
			public String getText() {
				return LanguageHandler.get("Effects Volume");
			}
		}, false);
		volumeEffects = new HorizontalSlider( (Options.EFFECT_VOLUME - AudioConstants.MIN_VOLUME)/Math.abs(AudioConstants.MAX_VOLUME - AudioConstants.MIN_VOLUME), e -> {

			float musicDistance = Math.abs(AudioConstants.MAX_VOLUME - AudioConstants.MIN_VOLUME);
			newEffectsVolume =(float)(musicDistance* volumeEffects.getValue() + AudioConstants.MIN_VOLUME);
		});

		changeSize(width, height);

		component.add(button_accept);
		component.add(button_cancel);

		component.add(box_antialiasing);
		component.add(text_antialiasing);

		component.add(volumeMusic);
		component.add(text_volumeMusic);

		component.add(volumeEffects);
		component.add(text_volumeEffects);

		component.add(button_change_lang);

	}

	public void changeSize(int width, int height) {
		int componentHeight = height/12;
		int componentWidth = componentHeight * 5;

		box_antialiasing.setBounds(5, 5, componentHeight, componentHeight);
		text_antialiasing.setBounds(10 + componentHeight, componentHeight/2 - 5, componentWidth, componentHeight);

		button_accept.setBounds(width/2 - componentWidth - 5, height - 2*componentHeight, componentWidth, componentHeight);
		button_cancel.setBounds(width/2 + 5, height - 2*componentHeight, componentWidth, componentHeight);

		text_volumeMusic.setBounds(10 + componentWidth, 10+componentHeight, componentWidth/2, componentHeight);
		volumeMusic.setBounds(5, 10 + componentHeight, componentWidth, componentHeight/2);

		text_volumeEffects.setBounds((int)(2.5f*componentWidth+20), 10+componentHeight, componentWidth/2, componentHeight);
		volumeEffects.setBounds((int)(1.5f*componentWidth + 15), 10 + componentHeight, componentWidth, componentHeight/2);

		button_change_lang.setBounds(5, 15 + 2* componentHeight, componentWidth, componentHeight);
	}

	public interface OptionFinishListener {
		void onOptionsAccept();
		void onOptionsCancel();
	}
}
