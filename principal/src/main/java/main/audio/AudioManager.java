package main.audio;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import java.net.URL;

public class AudioManager {

    private Clip musicClip;
    private int musicVolume = 100;
    private int effectsVolume = 100;

    public void playMusic(String resourcePath, boolean loop) {
        stopMusic();
        musicClip = loadClip(resourcePath);

        if(musicClip == null) {
            return;
        }

        applyVolume(musicClip, musicVolume);
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

        applyVolume(clip, effectsVolume);
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

    public int getMusicVolume() {
        return musicVolume;
    }

    public int getEffectsVolume() {
        return effectsVolume;
    }

    public void setMusicVolume(int volume) {
        musicVolume = clampVolume(volume);

        if(musicClip != null) {
            applyVolume(musicClip, musicVolume);
        }
    }

    public void setEffectsVolume(int volume) {
        effectsVolume = clampVolume(volume);
    }

    private Clip loadClip(String resourcePath) {
        URL audioResource = getClass().getResource(resourcePath);

        if(audioResource == null) {
            System.out.println("No se encontro el audio: " + resourcePath);
            return null;
        }

        try (AudioInputStream sourceStream = AudioSystem.getAudioInputStream(audioResource)) {
            AudioInputStream playableStream = sourceStream;
            AudioFormat sourceFormat = sourceStream.getFormat();

            if(needsConversion(sourceFormat)) {
                AudioFormat targetFormat = new AudioFormat(
                        AudioFormat.Encoding.PCM_SIGNED,
                        sourceFormat.getSampleRate(),
                        16,
                        sourceFormat.getChannels(),
                        sourceFormat.getChannels() * 2,
                        sourceFormat.getSampleRate(),
                        false);

                if(AudioSystem.isConversionSupported(targetFormat, sourceFormat) == false) {
                    System.out.println("Formato de audio no compatible: " + resourcePath);
                    return null;
                }

                playableStream = AudioSystem.getAudioInputStream(targetFormat, sourceStream);
            }

            Clip clip = AudioSystem.getClip();
            try {
                clip.open(playableStream);
            } finally {
                if(playableStream != sourceStream) {
                    playableStream.close();
                }
            }
            return clip;
        } catch (Exception e) {
            System.out.println("No se pudo reproducir el audio: " + resourcePath);
            e.printStackTrace();
            return null;
        }
    }

    private boolean needsConversion(AudioFormat format) {
        return format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED) == false
                || format.getSampleSizeInBits() != 16
                || format.isBigEndian();
    }

    private int clampVolume(int volume) {
        return Math.max(0, Math.min(100, volume));
    }

    private void applyVolume(Clip clip, int volume) {
        if(clip.isControlSupported(FloatControl.Type.MASTER_GAIN) == false) {
            return;
        }

        FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        float gain = volume == 0 ? gainControl.getMinimum() : (float) (20 * Math.log10(volume / 100.0));
        gainControl.setValue(Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), gain)));
    }
}
