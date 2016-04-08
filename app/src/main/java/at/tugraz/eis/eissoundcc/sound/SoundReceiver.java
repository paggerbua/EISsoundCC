package at.tugraz.eis.eissoundcc.sound;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.widget.TextView;

import org.jtransforms.fft.DoubleFFT_1D;

import at.tugraz.eis.eissoundcc.MainActivity;
import at.tugraz.eis.eissoundcc.alphabet.FrequencyAlphabet;


/**
 * Created by Mario Pagger on 19.01.2016.
 */
public class SoundReceiver extends Thread {

    private final int rate = AudioTrack.getNativeOutputSampleRate(AudioTrack.MODE_STREAM);
    private final int ms = 75;
    private final int bufferSize = rate * ms / 1000;

    private MainActivity mainActivity;

    private final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION,
            rate,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize);


    private boolean running = false;

    public SoundReceiver() {
    }



    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    private void read() {
        audioRecord.startRecording();

        char lastChar = 'Z';
        StringBuffer result = new StringBuffer();

        while(running) {
            short[] readResult = new short[bufferSize];
            audioRecord.read(readResult, 0, bufferSize);

            double[] fftResult = new double[bufferSize*2];
            for(int i = 0; i < bufferSize; i++) {
                // fill real parts
                fftResult[i*2] = 1.0 * readResult[i];
            }

            DoubleFFT_1D fft = new DoubleFFT_1D(bufferSize);
            fft.complexForward(fftResult);
            // now we have the result in read and imaginary parts

            double maxMagnitude = 0.0;
            int maxMagnitudeIndex = 0;
            double[] magnitude = new double[bufferSize/2]; // no need for mirrored values
            for(int i = 0; i < bufferSize/2; i++) {
                double re = fftResult[i*2];
                double im = fftResult[i*2+1];
                magnitude[i] = Math.sqrt(re*re + im*im);

                if(magnitude[i] > maxMagnitude) {
                    maxMagnitude = magnitude[i];
                    maxMagnitudeIndex = i;
                }
            }

            double frequency = ((1.0 * rate) / (1.0 * bufferSize)) * maxMagnitudeIndex;

            if(frequency > 19500.0) {
                char currentChar = FrequencyAlphabet.getInstance().getCharByFrequency(frequency);

                if(currentChar == '\5') {
                    result = new StringBuffer();
                } else {
                    if(lastChar != '\7' && currentChar == '\7') {
                        result.append(lastChar);
                        mainActivity.setReceivedMessage(result.toString());
                    }

                    lastChar = currentChar;
                }
            }
        }

        audioRecord.stop();
    }

    @Override
    public void run() {
        running = true;
        read();
    }

    public int getRate() {
        return rate;
    }

    public int getMs() {
        return ms;
    }

    public int getBufferSize() {
        return bufferSize;
    }

    public void stopWorking() {
        running = false;
    }
}
