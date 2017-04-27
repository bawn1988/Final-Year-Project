package com.sean.accelerometer;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

//https://examples.javacodegeeks.com/android/core/hardware/sensor/android-accelerometer-example/
public class Accelerometer extends Activity implements SensorEventListener {
    private float lastX, lastY, lastZ;

    private SensorManager sensorManager;
    private Sensor accelerometer;

    private float deltaXMax = 0;
    private float deltaYMax = 0;
    private float deltaZMax = 0;

    private double acceleration =0;

    private float deltaX = 0;
    private float deltaY = 0;
    private float deltaZ = 0;

    private float vibrateThreshold = 0;

    private TextView currentX, currentY, currentZ, maxX, maxY, maxZ, maxAccel;

    public Vibrator v;

    final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private double theta = 5.0;
    double thetaZ;

    public double test(){
      //  deltaXMax =deltaX;
        return thetaZ= Math.sin(theta)*deltaXMax;

    }
    //double number = -895.25;
   // String numberAsString = new Double(number).toString();
   // String entry1 =new Double(thetaZ).toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        checkPermissions();

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            vibrateThreshold = accelerometer.getMaximumRange() / 2;
        } else {
            // fai! we dont have an accelerometer!
            Toast.makeText(getBaseContext(), "Can't Find Data ", Toast.LENGTH_LONG).show();
        }

        //initialize vibration
        v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void initializeViews() {

        currentX = (TextView) findViewById(R.id.currentX);
        currentY = (TextView) findViewById(R.id.currentY);
        currentZ = (TextView) findViewById(R.id.currentZ);

        maxX = (TextView) findViewById(R.id.maxX);
        maxY = (TextView) findViewById(R.id.maxY);
        maxZ = (TextView) findViewById(R.id.maxZ);
        maxAccel = (TextView) findViewById(R.id.maxAccel);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
    @Override
    public void onSensorChanged(SensorEvent event) {
                
        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();
                
        // get the change of the x,y,z values of the accelerometer
        deltaX = Math.abs(lastX - event.values[0]);
        deltaY = Math.abs(lastY - event.values[1]);
        deltaZ = Math.abs(lastZ - event.values[2]);

                
        // if the change is below 2, it is just plain noise
        if (deltaX < 2)
        deltaX = 0;
        if (deltaY < 2)
        deltaY = 0;
        if ((deltaX > vibrateThreshold) || (deltaY > vibrateThreshold) || (deltaZ > vibrateThreshold)) {
            v.vibrate(50);
        }

        String entry = "\n X: " + currentX.getText().toString()  + ", Y: "+ currentY.getText().toString() + ", Z: " + currentZ.getText().toString() + "\n" +
               test() + "\n";
        try {

            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/sean");
            Boolean dirsMade = dir.mkdir();
            //System.out.println(dirsMade);
            Log.v("Accel", dirsMade.toString());

            File file = new File(dir, "output.csv");
            FileOutputStream f = new FileOutputStream(file, true);

            try {
                f.write(entry.getBytes());
                f.flush();
                f.close();
                Toast.makeText(getBaseContext(), "Data saved", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void displayCleanValues() {
        currentX.setText("0.0");
        currentY.setText("0.0");
        currentZ.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentX.setText(Float.toString(deltaX));
        currentY.setText(Float.toString(deltaY));
        currentZ.setText(Float.toString(deltaZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (deltaX > deltaXMax) {
            deltaXMax = deltaX;
            maxX.setText(Float.toString(deltaXMax));
        }

        if (deltaY > deltaYMax) {
            deltaYMax = deltaY;
            maxY.setText(Float.toString(deltaYMax));
        }

        if (deltaZ > deltaZMax) {
            deltaZMax = deltaZ;
            maxZ.setText(Float.toString(deltaZMax));
        }

            acceleration = Math.sqrt(Math.pow(Double.parseDouble(currentX.getText().toString()), 2) +
                    Math.pow(Double.parseDouble(currentY.getText().toString()), 2) +
                    Math.pow(Double.parseDouble(currentZ.getText().toString()), 2));
            maxAccel.setText(Double.toString(acceleration));
    }

    private void checkPermissions() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        Toast.makeText(getBaseContext(), "Permission is already granted", Toast.LENGTH_LONG).show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(getBaseContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    // Permission Denied
                    Toast.makeText(Accelerometer.this, "WRITE_EXTERNAL_STORAGE Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}

/*
// http://www.kircherelectronics.com/blog/index.php/11-android/sensors/10-low-pass-filter-linear-acceleration

    // Constants for the low-pass filters
    private float timeConstant = 0.18f;
    private float alpha = 0.9f;
    private float dt = 0;

    // Timestamps for the low-pass filters
    private float timestamp = System.nanoTime();
    private float timestampOld = System.nanoTime();

    // Gravity and linear accelerations components for the
// Wikipedia low-pass filter
    private float[] gravity = new float[]
            { 0, 0, 0 };

    private float[] linearAcceleration = new float[]
            { 0, 0, 0 };

    // Raw accelerometer data
    private float[] input = new float[]
            { 0, 0, 0 };

    private int count = 0;

    /**
     * Add a sample.
     *
     * @param acceleration
     *            The acceleration data.
     * @return Returns the output of the filter.
     */
    /*
public float[] addSamples(float[] acceleration)
{
// Get a local copy of the sensor values
    System.arraycopy(acceleration, 0, this.input, 0, acceleration.length);

    timestamp = System.nanoTime();

// Find the sample period (between updates).
// Convert from nanoseconds to seconds
    dt = 1 / (count / ((timestamp - timestampOld) / 1000000000.0f));

    count++;

    alpha = timeConstant / (timeConstant + dt);

    gravity[0] = alpha * gravity[0] + (1 - alpha) * input[0];
    gravity[1] = alpha * gravity[1] + (1 - alpha) * input[1];
    gravity[2] = alpha * gravity[2] + (1 - alpha) * input[2];

    linearAcceleration[0] = input[0] - gravity[0];
    linearAcceleration[1] = input[1] - gravity[1];
    linearAcceleration[2] = input[2] - gravity[2];

    return linearAcceleration;
}
 */


   /* public void onSensorChanged(SensorEvent event)
    {
        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate

        final float alpha = 0.8;

        gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
        gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
        gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

        linear_acceleration[0] = event.values[0] - gravity[0];
        linear_acceleration[1] = event.values[1] - gravity[1];
        linear_acceleration[2] = event.values[2] - gravity[2];
    }*/