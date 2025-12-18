import javax.sound.sampled.*;
import java.util.concurrent.*;

public class SoundSystem {
    private ExecutorService executor;
    private volatile boolean playing;
    private byte[] recordedAudio;
    private TargetDataLine recordLine;
    
    public enum Waveform {
        SINE, SQUARE, SAWTOOTH, TRIANGLE
    }
    
    private Waveform currentWaveform = Waveform.SINE;
    
    public SoundSystem() {
        executor = Executors.newSingleThreadExecutor();
        playing = false;
    }
    
    public void setWaveform(Waveform waveform) {
        this.currentWaveform = waveform;
    }
    
    public void setWaveform(String waveformName) {
        try {
            this.currentWaveform = Waveform.valueOf(waveformName.toUpperCase());
        } catch (IllegalArgumentException e) {
            this.currentWaveform = Waveform.SINE;
        }
    }
    
    public void beep(int duration) {
        playTone(800, duration, 0.5);
    }
    
    public void playTone(double frequency, int duration, double volume) {
        playTone(frequency, duration, volume, currentWaveform);
    }
    
    public void playTone(double frequency, int duration, double volume, Waveform waveform) {
        executor.submit(() -> {
            try {
                playing = true;
                int sampleRate = 44100;
                int numSamples = (int)((duration / 1000.0) * sampleRate);
                byte[] buffer = new byte[numSamples * 2];
                
                for (int i = 0; i < numSamples; i++) {
                    double time = (double)i / sampleRate;
                    double sample = generateWaveform(waveform, frequency, time) * volume;
                    short sampleShort = (short)(sample * 32767.0);
                    buffer[i * 2] = (byte)(sampleShort & 0xFF);
                    buffer[i * 2 + 1] = (byte)((sampleShort >> 8) & 0xFF);
                }
                
                AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                
                if (!AudioSystem.isLineSupported(info)) {
                    System.err.println("Audio line not supported");
                    playing = false;
                    return;
                }
                
                SourceDataLine line = null;
                try {
                    line = (SourceDataLine)AudioSystem.getLine(info);
                    line.open(format);
                    line.start();
                    line.write(buffer, 0, buffer.length);
                    line.drain();
                } finally {
                    if (line != null) {
                        line.close();
                    }
                    playing = false;
                }
                
            } catch (Exception e) {
                System.err.println("Sound error: " + e.getMessage());
                playing = false;
            }
        });
    }
    
    private double generateWaveform(Waveform waveform, double frequency, double time) {
        double phase = (time * frequency) % 1.0;
        
        switch (waveform) {
            case SINE:
                return Math.sin(2.0 * Math.PI * frequency * time);
                
            case SQUARE:
                return phase < 0.5 ? 1.0 : -1.0;
                
            case SAWTOOTH:
                return 2.0 * phase - 1.0;
                
            case TRIANGLE:
                return phase < 0.5 ? (4.0 * phase - 1.0) : (3.0 - 4.0 * phase);
                
            default:
                return Math.sin(2.0 * Math.PI * frequency * time);
        }
    }
    
    public void playNote(String note, int duration, double volume) {
        double frequency = noteToFrequency(note);
        playTone(frequency, duration, volume);
    }
    
    public void playSequence(String sequence) {
        // Parse sequence like "C4:500,E4:500,G4:1000"
        String[] notes = sequence.split(",");
        executor.submit(() -> {
            for (String noteSpec : notes) {
                String[] parts = noteSpec.trim().split(":");
                if (parts.length == 2) {
                    String note = parts[0].trim();
                    int duration = Integer.parseInt(parts[1].trim());
                    double freq = noteToFrequency(note);
                    playToneSync(freq, duration, 0.5, currentWaveform);
                    try {
                        Thread.sleep(50); // Gap between notes
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        });
    }
    
    private void playToneSync(double frequency, int duration, double volume, Waveform waveform) {
        try {
            int sampleRate = 44100;
            int numSamples = (int)((duration / 1000.0) * sampleRate);
            byte[] buffer = new byte[numSamples * 2];
            
            for (int i = 0; i < numSamples; i++) {
                double time = (double)i / sampleRate;
                double sample = generateWaveform(waveform, frequency, time) * volume;
                short sampleShort = (short)(sample * 32767.0);
                buffer[i * 2] = (byte)(sampleShort & 0xFF);
                buffer[i * 2 + 1] = (byte)((sampleShort >> 8) & 0xFF);
            }
            
            AudioFormat format = new AudioFormat(sampleRate, 16, 1, true, false);
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
            SourceDataLine line = null;
            try {
                line = (SourceDataLine)AudioSystem.getLine(info);
                line.open(format);
                line.start();
                line.write(buffer, 0, buffer.length);
                line.drain();
            } finally {
                if (line != null) {
                    line.close();
                }
            }

        } catch (Exception e) {
            System.err.println("Sound error: " + e.getMessage());
        }
    }
    
    private double noteToFrequency(String note) {
        // Convert note names like C4, A5, etc. to frequency
        note = note.toUpperCase().trim();
        
        int octave = 4;
        if (note.length() > 1 && Character.isDigit(note.charAt(note.length() - 1))) {
            octave = Character.getNumericValue(note.charAt(note.length() - 1));
            note = note.substring(0, note.length() - 1);
        }
        
        int semitone = 0;
        switch (note) {
            case "C": semitone = 0; break;
            case "C#": case "DB": semitone = 1; break;
            case "D": semitone = 2; break;
            case "D#": case "EB": semitone = 3; break;
            case "E": semitone = 4; break;
            case "F": semitone = 5; break;
            case "F#": case "GB": semitone = 6; break;
            case "G": semitone = 7; break;
            case "G#": case "AB": semitone = 8; break;
            case "A": semitone = 9; break;
            case "A#": case "BB": semitone = 10; break;
            case "B": semitone = 11; break;
            default: return 440.0;
        }
        
        // A4 = 440Hz
        return 440.0 * Math.pow(2.0, (octave - 4) + (semitone - 9) / 12.0);
    }
    
    public void startRecording(int duration) {
        executor.submit(() -> {
            try {
                AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
                
                if (!AudioSystem.isLineSupported(info)) {
                    System.err.println("Recording not supported");
                    return;
                }
                
                recordLine = (TargetDataLine) AudioSystem.getLine(info);
                try {
                    recordLine.open(format);
                    recordLine.start();

                    int numBytes = (int)((duration / 1000.0) * 44100 * 2);
                    recordedAudio = new byte[numBytes];

                    recordLine.read(recordedAudio, 0, numBytes);
                    recordLine.stop();

                    System.out.println("Recording complete");
                } finally {
                    if (recordLine != null) {
                        recordLine.close();
                        recordLine = null;
                    }
                }
                
            } catch (Exception e) {
                System.err.println("Recording error: " + e.getMessage());
            }
        });
    }
    
    public void playRecording() {
        if (recordedAudio == null) {
            System.err.println("No recording available");
            return;
        }
        
        executor.submit(() -> {
            SourceDataLine line = null;
            try {
                AudioFormat format = new AudioFormat(44100, 16, 1, true, false);
                DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
                line = (SourceDataLine) AudioSystem.getLine(info);

                line.open(format);
                line.start();
                line.write(recordedAudio, 0, recordedAudio.length);
                line.drain();

            } catch (Exception e) {
                System.err.println("Playback error: " + e.getMessage());
            } finally {
                if (line != null) {
                    line.close();
                }
            }
        });
    }
    
    public void stop() {
        playing = false;
    }
    
    public boolean isPlaying() {
        return playing;
    }
    
    public void shutdown() {
        executor.shutdownNow();
    }
}
