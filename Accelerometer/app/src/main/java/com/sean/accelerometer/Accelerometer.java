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
    private float accelerationValueX, accelerationValueY, accelerationValueZ;

    private SensorManager sensorM;
    private Sensor accelerometer;

    private float xMaxValue = 0, yMaxValue = 0, zMaxValue;
    private double acceleration =0.0;
    private float changeValueX = 0, changeValueY = 0, changeValueZ = 0, Vibration = 0;
    private TextView currentXValue, currentYValue, currentZValue, maxXValue, maxYValue, maxZValue, maxAccel;

    public Vibrator vibrate;

    final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private double theta = 5.0;
    double thetaZ;

    public double test(){
      //  xMaxValue =changeValueX;
        return thetaZ= Math.sin(theta)* xMaxValue;

    }
    //double number = -895.25;
   // String numberAsString = new Double(number).toString();
   // String entry1 =new Double(thetaZ).toString();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeViews();
        allowPermission();

        sensorM = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null) {
            // success! we have an accelerometer

            accelerometer = sensorM.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            sensorM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
            Vibration = accelerometer.getMaximumRange() / 2;
        } else {
            // fail! we don't have an accelerometer!
            Toast.makeText(getBaseContext(), "Can't Find Data ", Toast.LENGTH_LONG).show();
        }
        //initialize vibration
        vibrate = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);
    }

    private void initializeViews() {

        currentXValue = (TextView) findViewById(R.id.currentX);
        currentYValue = (TextView) findViewById(R.id.currentY);
        currentZValue = (TextView) findViewById(R.id.currentZ);

        maxXValue = (TextView) findViewById(R.id.maxX);
        maxYValue = (TextView) findViewById(R.id.maxY);
        maxZValue = (TextView) findViewById(R.id.maxZ);
        maxAccel = (TextView) findViewById(R.id.maxAccel);
    }

    //onResume() register the accelerometer for listening the events
    protected void onResume() {
        super.onResume();
        sensorM.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    //onPause() unregister the accelerometer for stop listening the events
    protected void onPause() {
        super.onPause();
        sensorM.unregisterListener(this);
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
        changeValueX = Math.abs(accelerationValueX - event.values[0]);
        changeValueY = Math.abs(accelerationValueY - event.values[1]);
        changeValueZ = Math.abs(accelerationValueZ - event.values[2]);

                
        // if the change is below 2, it is just plain noise
        if (changeValueX < 2)
        changeValueX = 0;
        if (changeValueY < 2)
        changeValueY = 0;
        if ((changeValueX > Vibration) || (changeValueY > Vibration) || (changeValueZ > Vibration)) {
            vibrate.vibrate(50);
        }

        String entry = "\n X: " + currentXValue.getText().toString()  + ", Y: "+ currentYValue.getText().toString() + ", Z: " + currentZValue.getText().toString()  +
               ", Acceleration: " + maxAccel.getText().toString() + "\n";
        try {

            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/sean");
            Boolean mkdir = dir.mkdir();
            Log.v("Accel", mkdir.toString());

            File file = new File(dir, "output.csv");
            FileOutputStream f = new FileOutputStream(file, true);

            try {
                f.write(entry.getBytes());
                f.flush();
                f.close();
                Toast.makeText(getBaseContext(), "Data Saved", Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void displayCleanValues() {
        currentXValue.setText("0.0");
        currentYValue.setText("0.0");
        currentZValue.setText("0.0");
    }

    // display the current x,y,z accelerometer values
    public void displayCurrentValues() {
        currentXValue.setText(Float.toString(changeValueX));
        currentYValue.setText(Float.toString(changeValueY));
        currentZValue.setText(Float.toString(changeValueZ));
    }

    // display the max x,y,z accelerometer values
    public void displayMaxValues() {
        if (changeValueX > xMaxValue) {
            xMaxValue = changeValueX;
            maxXValue.setText(Float.toString(xMaxValue));
        }

        if (changeValueY > yMaxValue) {
            yMaxValue = changeValueY;
            maxYValue.setText(Float.toString(yMaxValue));
        }

        if (changeValueZ > zMaxValue) {
            zMaxValue = changeValueZ;
            maxZValue.setText(Float.toString(zMaxValue));
        }

            acceleration = Math.sqrt(Math.pow(Double.parseDouble(currentXValue.getText().toString()), 2) +
                    Math.pow(Double.parseDouble(currentYValue.getText().toString()), 2) +
                    Math.pow(Double.parseDouble(currentZValue.getText().toString()), 2));
            maxAccel.setText(Double.toString(acceleration));
    }

    private void allowPermission() {
        int writeContactsPermission = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (writeContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        Toast.makeText(getBaseContext(), "Permissions are already Granted", Toast.LENGTH_LONG).show();
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    Toast.makeText(getBaseContext(), "Permission are Granted", Toast.LENGTH_LONG).show();
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