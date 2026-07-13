package main.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;

public class AudioManager {

    private Clip musicClip;

    public void playMusic(String resourcePath, boolean loop) {
        stopMusic();
        musicClip = loadClip(resourcePath);

        if(musicClip == null) {
            return;
        }

        musicClip.setFramePosition(0);

        if(loop) {
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        } else {
            musicClip.start();
        }
    }

    public void playEffect(String resourcePath) {
        Clip clip = loadClip(resourcePath);

        if(clip == null) {
            return;
        }

        clip.addLineListener(event -> {
            if(event.getType() == LineEvent.Type.STOP) {
                clip.close();
            }
        });

        clip.setFramePosition(0);
        clip.start();
    }

    public void stopMusic() {
        if(musicClip == null) {
            return;
        }

        musicClip.stop();
        musicClip.close();
        musicClip = null;
    }

    private Clip loadClip(String resourcePath) {
        try {
            Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(getClass().getResource(resourcePath)));
            return clip;
        } catch (Exception e) {
            System.out.println("No se pudo reproducir el audio: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }
}
