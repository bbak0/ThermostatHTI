package com.example.thermostat.thermostat;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import java.net.ConnectException;

import util.HeatingSystem;

public class MainActivity extends AppCompatActivity {

    int mInterval = 1000;
    private Handler mHandler;

    TextView tvCurrentTemperature;
    TextView tvTargetTemperature;
    Button bMinus;
    Button bPlus;
    SeekBar tempBar;

    double targetTemperature = 20;
    double currentTemperature = 20;

    String getParam;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getTarget();

        tvCurrentTemperature = (TextView)findViewById(R.id.current_temp);
        tvTargetTemperature = (TextView)findViewById(R.id.target_temperature_display);
        bPlus = (Button)findViewById(R.id.bPlus);
        bMinus = (Button)findViewById(R.id.bMinus);
        tempBar = (SeekBar)findViewById(R.id.target_temp_seekbar);

        bPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (targetTemperature < 30.0) {
                    targetTemperature += 0.1;
                    tempBar.incrementProgressBy(1);
                    targetTemperature = Math.round(targetTemperature * 10) / 10.0d;
                    tvTargetTemperature.setText(String.valueOf(targetTemperature));
                }
            }
        });

        bMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (targetTemperature > 5.0) {
                    targetTemperature -= 0.1;
                    tempBar.incrementProgressBy(-1);
                    targetTemperature = Math.round(targetTemperature * 10) / 10.0d;
                    tvTargetTemperature.setText(String.valueOf(targetTemperature));
                }
            }
        });
        tempBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                targetTemperature = ((double)seekBar.getProgress() + 50) / 10.0d;
                Log.d("round",targetTemperature+"");
                tvTargetTemperature.setText(String.valueOf(targetTemperature));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




        mHandler = new Handler();
        startRepeatingTask();
    }


    Runnable mStatusChecker = new Runnable() {
        @Override
        public void run() {
            try {
                getTemp(); //this function can change value of mInterval.
                updateTarget();
            } finally {
                // 100% guarantee that this always happens, even if
                // your update method throws an exception
                mHandler.postDelayed(mStatusChecker, mInterval);
            }
        }
    };

    void getTemp(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                getParam = "";
                try {
                    getParam = HeatingSystem.get("currentTemperature");
                    Log.d("a",getParam);
                    tvCurrentTemperature.post(new Runnable() {
                        @Override
                        public void run() {
                            currentTemperature = Double.parseDouble(getParam);
                            currentTemperature = Math.round(currentTemperature*10)/10.0d;
                            tvCurrentTemperature.setText(currentTemperature + "°C");
                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata "+e);
                }
            }
        }).start();
    }
    void getTarget(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                getParam = "";
                try {
                    getParam = HeatingSystem.get("targetTemperature");
                    tvTargetTemperature.post(new Runnable() {
                        @Override
                        public void run() {

                            targetTemperature = Double.parseDouble(getParam);
                            targetTemperature =  Math.round(targetTemperature*10)/10.0d;
                            tvTargetTemperature.setText(targetTemperature + "°C");
                            tempBar.setProgress((int)(10.0*(targetTemperature - 5.0)));

                        }
                    });
                } catch (Exception e) {
                    System.err.println("Error from getdata "+e);
                }
            }
        }).start();
    }
    void updateTarget(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                getParam = "";
                try {
                    HeatingSystem.put("targetTemperature", String.valueOf(targetTemperature));
                } catch (Exception e) {
                    System.err.println("Error from getdata "+e);
                }
            }
        }).start();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRepeatingTask();
    }

    void startRepeatingTask() {
        mStatusChecker.run();
    }

    void stopRepeatingTask() {
        mHandler.removeCallbacks(mStatusChecker);
    }
}
