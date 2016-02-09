package com.example.nick.metronome2;


import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.media.SoundPool;
import android.media.AudioManager;
import java.util.Timer;
import java.util.TimerTask;
import android.media.SoundPool.OnLoadCompleteListener;

public class MainActivity extends AppCompatActivity implements OnItemSelectedListener {
    private SoundPool soundpool;
    private int soundID;
    boolean loaded = false;
    Timer timer;
    TimerTask playSound;
    private Spinner spinner;
    private Spinner spinner2;

    private static final Integer[] topItems = new Integer[]{2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16};
    private static final Integer[] botItems = new Integer[]{4,8,16};

    private static int mult = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
        soundpool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
        soundID = soundpool.load(this, R.raw.sound, 1);

        spinner = (Spinner)findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        spinner2 = (Spinner)findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(this);


        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_dropdown_item, topItems);

        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_dropdown_item, botItems);
        spinner.setAdapter(adapter);
        spinner2.setAdapter(adapter2);
        spinner.setSelection(2);


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

    /**
     * Starts the metronome
     * @param view
     */
    public void start(View view) {
        double bpm; //Beats per minute
        long spb; //Seconds per beat

        loaded = true;

        //Get the bpm that the user has inputted
        EditText bpmInput = (EditText)findViewById(R.id.bpmIn);
        bpm = Double.parseDouble(bpmInput.getText().toString());

        //Calculate the seconds per beat for the user's bpm
        spb = (long) (calcSecBeat(bpm) * 1000);

        if (timer == null) {
            timer = new Timer();
            initializeTimerTask();
            Toast.makeText(this, Long.toString(spb), Toast.LENGTH_SHORT).show();
            timer.schedule(playSound, 0, spb);
        } else {
            Toast.makeText(this, "Timer running", Toast.LENGTH_LONG).show();
        }// END IF
    }// END START

    /**
     * Stops the metronome
     * @param view
     */
    public void stop(View view) {
        Toast.makeText(this, "Stopping Metronome", Toast.LENGTH_SHORT).show();
        if (timer!=null) {
            timer.cancel();
            timer = null;
        }//End If
    }//End Stop

    /**
     * Turns a set bpm into seconds per beat
     * @param bpm the bpm set by the user
     * @return seconds per beat
     */
    public double calcSecBeat(double bpm) {

        return (1/(mult * bpm/60));
    } //END calcSetBeat

    /**
     * Initialize the scheduled task to run
     */
    public void initializeTimerTask() {
        playSound = new TimerTask() {
            public void run() {
                //Get the user sound settings
                AudioManager audiomanager = (AudioManager) getSystemService(AUDIO_SERVICE);
                float actualVolume = (float) audiomanager.getStreamVolume(AudioManager.STREAM_MUSIC);
                float maxVolume = (float) audiomanager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float volume = actualVolume / maxVolume;

                //Play the sound
                soundpool.play(soundID, volume, volume, 1, 0, 1f);
            }
        };// ENDTimerTask
    }//END initializeTimerTask

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if ((Integer) spinner2.getItemAtPosition(position) == 8) {
            mult = 2;
        } else if ((Integer) spinner2.getItemAtPosition(position) == 16) {
            mult = 4;
        } else {
            mult = 1;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
