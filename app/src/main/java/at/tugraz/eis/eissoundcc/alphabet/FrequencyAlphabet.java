package at.tugraz.eis.eissoundcc.alphabet;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Mario Pagger on 19.01.2016.
 */
public class FrequencyAlphabet {
    private static FrequencyAlphabet instance = null;
    private final Map<Integer, Integer> frequencyMap = new HashMap<Integer, Integer>();

    private final int start = 20000;
    private final int interval = 15;
    private final int charSize = 256;

    private FrequencyAlphabet() {
        initMap();
    }

    public static FrequencyAlphabet getInstance() {
        if(instance == null) {
            instance = new FrequencyAlphabet();
        }
        return instance;
    }

    private void initMap() {
        for(int i = 0; i < charSize; i++) {
            frequencyMap.put(start + i*interval, i);
        }
    }

    public char getCharByFrequency(double frequency) {
        // search frequency
        for(int currFreq = start; currFreq < start + (charSize*interval); currFreq+=interval) {
            int nextFreq = currFreq + interval;
            if((double) currFreq <= frequency &&
                    frequency <= (double) nextFreq) {
                return (char) frequencyMap.get(currFreq).intValue();
            }
        }
        return 0;
    }

    public double getFrequencyByChar(char character) {
        int charCode = (int) character;

        for (Map.Entry<Integer, Integer> entry : frequencyMap.entrySet()) {
            if(charCode == entry.getValue()) {
                // returns middle frequency, to exclude measure failures
                return ((double) entry.getKey()) + (((double) interval) / 2.0);
            }
        }
        return 0.0;
    }
}
