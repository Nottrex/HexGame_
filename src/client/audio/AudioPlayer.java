package client.audio;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import java.util.ArrayList;
import java.util.List;

public class AudioPlayer {

    private List<Clip> removeBuffer;
    private List<Clip> currentlyPlaying;
    private List<Clip> queue;
    private List<Integer> loops;

    private Clip currentClip;
    private int currentLoops;

    private int pausedFrame;
    private boolean forcedStop;

    public AudioPlayer(String startClip) {
        this(startClip, 1);
    }

    public AudioPlayer(String startClip, int startloop) {
        removeBuffer = new ArrayList<>();
        currentlyPlaying = new ArrayList<>();
        queue = new ArrayList<>();
        loops = new ArrayList<>();

        currentClip = AudioHandler.getMusicWav(startClip);
        setVolume(currentClip, AudioConstants.MUSIC_VOLUME);
        currentLoops = startloop;

        pausedFrame = 0;
        forcedStop = false;

        currentClip.addLineListener(new LineListener() {
            @Override
            public void update(LineEvent event) {
                if(event.getType() == LineEvent.Type.STOP && !forcedStop) {

                    if(currentLoops > 0 || currentLoops == Clip.LOOP_CONTINUOUSLY) {
                        setVolume(currentClip, AudioConstants.MUSIC_VOLUME);
                        currentClip.setFramePosition(0);
                        currentClip.start();

                        if(currentLoops > 0)currentLoops -= 1;
                    }else if(currentLoops == 0) {

                        currentClip = queue.get(0);
                        currentLoops = loops.get(0);

                        setVolume(currentClip, AudioConstants.MUSIC_VOLUME);
                        currentClip.setFramePosition(0);
                        currentClip.start();

                        queue.remove(0);
                        loops.remove(0);
                    }
                }
            }
        });
    }

    public void start() {
        currentClip.start();

        if(!forcedStop && currentLoops != Clip.LOOP_CONTINUOUSLY) currentLoops--;
        forcedStop = false;
    }

    public void stop() {
        pausedFrame = 0;
        forcedStop = true;
        currentClip.stop();
    }

    public void pause() {
        pausedFrame = currentClip.getFramePosition();
        forcedStop = true;
        currentClip.stop();
    }

    public void resume() {
        currentClip.setFramePosition(pausedFrame);
        start();
    }

    public void playAudio(String audioName) {
        Clip c = AudioHandler.getMusicWav(audioName);
        setVolume(c, AudioConstants.EFFECT_VOLUME);
        c.setFramePosition(0);
        c.start();

        currentlyPlaying.add(c);
        c.addLineListener(new LineListener() {
            @Override
            public void update(LineEvent event) {
                if(event.getType() == LineEvent.Type.STOP) {

                    removeBuffer.add(c);
                    c.close();
                }
            }
        });
    }

    public void loopAudio(String audioName, int loop) {
        Clip c = AudioHandler.getMusicWav(audioName);
        setVolume(c, AudioConstants.EFFECT_VOLUME);
        c.loop(loop);

        currentlyPlaying.add(c);
        c.addLineListener(new LineListener() {
            @Override
            public void update(LineEvent event) {
                if(event.getType() == LineEvent.Type.STOP) {
                    removeBuffer.add(c);
                    c.close();
                }
            }
        });
    }

    public void addMusic(String audioName) {
        addMusic(audioName, 1);
    }

    public void addMusic(String audioName, int loop) {
        queue.add(AudioHandler.getMusicWav(audioName));
        loops.add(loop);
    }

    private void setVolume(Clip c, int volume) {
        FloatControl fc = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
        fc.setValue(volume);
    }

    public void updateVolume() {
        setVolume(currentClip, AudioConstants.MUSIC_VOLUME);
        for(Clip c: removeBuffer) {
            currentlyPlaying.remove(c);
        }
        removeBuffer.clear();

        for(Clip c: currentlyPlaying) {
            setVolume(c, AudioConstants.EFFECT_VOLUME);
        }
    }
}