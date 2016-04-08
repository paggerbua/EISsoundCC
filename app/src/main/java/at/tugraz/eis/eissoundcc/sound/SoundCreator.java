package at.tugraz.eis.eissoundcc.sound;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.widget.TextView;

import org.w3c.dom.Text;

import at.tugraz.eis.eissoundcc.alphabet.FrequencyAlphabet;

/**
 * Created by Mario Pagger on 19.01.2016.
 */
public class SoundCreator extends Thread {

    private final int ms = 75;
    private final int rate = AudioTrack.getNativeOutputSampleRate(AudioTrack.MODE_STREAM);
    private final int bufferSize = rate * ms / 1000;

    private final AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
            rate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize,
            AudioTrack.MODE_STREAM);

    private char[] message;

    public SoundCreator() {
    }

    private void write() {
        audioTrack.play();

        for(int i = 0; i < message.length; i++) {
            double frequency = FrequencyAlphabet.getInstance().getFrequencyByChar(message[i]);
            short[] sine = getSine(frequency);
            audioTrack.write(sine, 0, sine.length);
        }

        audioTrack.stop();
    }

    public void setMessage(char[] message) {
        // create messages with acknowledge flags
        this.message = new char[message.length * 2 + 1];

        for(int i = 0; i < message.length; i++) {
            this.message[2*i] = message[i];
            // acknowledgement character
            this.message[2*i+1] = '\7';
        }

        // = end of transmission
        this.message[this.message.length - 1] = '\5';
    }

    private short[] getSine(double frequency) {
        short[] output = new short[bufferSize];

        double period = (double) audioTrack.getSampleRate() / frequency;

        for(int i = 0; i < bufferSize; i++) {
            double angle = 2 * Math.PI * i / period;
            // * (2^(16-1) - 1) -> 16 bit signed ... sonst sehr leise ;)
            output[i] = (short) (Math.sin(angle) * 32767f);
        }

        return output;
    }

    public int getMs() {
        return ms;
    }

    public int getRate() {
        return rate;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public void run() {
        write();
    }
}
