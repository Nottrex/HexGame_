package client.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AudioHandler {
	public static Map<String, Clip> audio_wav;

	static {
		audio_wav = new HashMap<String, Clip>();
	}

	private AudioHandler() {
	}

	public static void loadMusicWav(String audioName, String fileName) {
		Clip a = null;
		try {
			a = AudioSystem.getClip(null);
			a.open(AudioSystem.getAudioInputStream(new BufferedInputStream(ClassLoader.getSystemResourceAsStream("res/audio/" + fileName + ".wav"))));
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}

		audio_wav.put(audioName, a);
	}

	public static Clip getMusicWav(String audioName) {
		return audio_wav.get(audioName);
	}

	public static void unloadMusicWav(String audioName) {
		audio_wav.remove(audioName);
	}
}
