package at.tugraz.eis.eissoundcc;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.w3c.dom.Text;

import java.sql.SQLOutput;

import at.tugraz.eis.eissoundcc.alphabet.FrequencyAlphabet;
import at.tugraz.eis.eissoundcc.sound.SoundCreator;
import at.tugraz.eis.eissoundcc.sound.SoundReceiver;

public class MainActivity extends AppCompatActivity {

    private Button bnSend;
    private ToggleButton tbRecieve;
    private TextView receiverTextView;

    private SoundCreator creator = new SoundCreator();
    private SoundReceiver receiver = new SoundReceiver();

    // Handler gets created on the UI-thread
    private Handler mHandler = new Handler();

    // This gets executed in a non-UI thread:
    public void setReceivedMessage(String receivedMessage) {
        final String str = receivedMessage;
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                // This gets executed on the UI thread so it can safely modify Views
                receiverTextView.setText(str);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        showInfo();

        bnSend = (Button) findViewById(R.id.bnSend);
        bnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v == bnSend) {
                    creator = new SoundCreator();

                    char[] message = ((EditText) findViewById(R.id.senderTextField)).getText().toString().toCharArray();
                    creator.setMessage(message);
                    creator.start();
                }
            }
        });

        receiverTextView = (TextView) findViewById(R.id.receiverTextView);

        final MainActivity instance = this;

        tbRecieve = (ToggleButton) findViewById(R.id.tbRecieve);
        tbRecieve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(v == tbRecieve) {
                    if(tbRecieve.isChecked()) {
                        if(receiver != null && receiver.isAlive()) {
                            return;
                        }
                        receiver = new SoundReceiver();
                        receiver.setMainActivity(instance);
                        receiver.start();
                    } else {
                        if(receiver != null) {
                            receiver.stopWorking();
                        }
                    }
                }
            }
        });
    }

    private void showInfo() {
        TextView infoView = (TextView) findViewById(R.id.infoTextView);

        StringBuffer buffer = new StringBuffer();
        buffer.append("sender information:\n");
        buffer.append("sample rate: ").append(creator.getRate()).append(" Hz\n");
        buffer.append("time per character: ").append(creator.getMs()).append(" ms\n");
        buffer.append("buffer size: ").append(creator.getBufferSize()).append(" bytes\n");
        buffer.append("\nreceiver information:\n");
        buffer.append("sample rate: ").append(receiver.getRate()).append(" Hz\n");
        buffer.append("time per sample: ").append(receiver.getMs()).append(" ms\n");
        buffer.append("buffer size: ").append(receiver.getBufferSize()).append(" bytes\n");

        infoView.setText(buffer.toString());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
