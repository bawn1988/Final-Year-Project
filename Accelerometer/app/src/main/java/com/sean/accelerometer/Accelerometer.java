package com.sean.accelerometer;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
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

import static java.lang.Math.PI;
import static java.lang.Math.atan;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/*****************************************************
 * Title: Android Accelerometer Example
 * Author: Chryssa Aliferi
 * Site owner/sponsor: javacodegeeks
 * Date: July 18th, 2014
 * Code version:
 * Availability: https://examples.javacodegeeks.com/android/core/hardware/sensor/android-accelerometer-example/
 * (Accessed 8 November 2016)
 * Modified: refactored all the variable and the non standard Android methods
 *****************************************************/

public class Accelerometer extends Activity implements SensorEventListener {
    private float accelerationValueX, accelerationValueY, accelerationValueZ;

    private SensorManager sensorM;
    private Sensor accelerometer;

    private float xMaxValue = 0, yMaxValue = 0, zMaxValue;
    private double acceleration = 0.0;
    private float changeValueX = 0, changeValueY = 0, changeValueZ = 0, Vibration = 0;
    private TextView currentXValue, currentYValue, currentZValue, maxXValue, maxYValue, maxZValue, maxAccel;
    private double absoluteVI = 0.0;
    double roll = 0.00, pitch = 0.00;        //Roll & Pitch are the angles which rotate by the axis X and y
    double threshold = 24.15;

    public Vibrator vibrate;

    final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    Intent emailAddress = new Intent(Intent.ACTION_SEND);
    // private GoogleApiClient client;

    /*****************************************************
     * Title: Android - Sending Email
     * Author: Tutorial Point Staff
     * Site owner/sponsor: Tutorial Point
     * Date: 2017
     * Code version:
     * Availability: https://www.tutorialspoint.com/android/android_sending_email.htm
     * (4th of May )
     * Modified: refactored all the variable and the non standard Android methods
     *****************************************************/

    //Email notifier
    protected void sendEmail() {
        Log.i("Send emailAddress", "");
        String[] TO = {"hayessean23@yahoo.com"};
        String[] CC = {""};
        Intent emailConnection = new Intent(Intent.ACTION_SEND);

        emailConnection.setData(Uri.parse("mailto:"));
        emailConnection.setType("text/plain");
        emailConnection.putExtra(Intent.EXTRA_EMAIL, TO);
        emailConnection.putExtra(Intent.EXTRA_CC, CC);
        emailConnection.putExtra(Intent.EXTRA_SUBJECT, "A Fall has been detected ");
        emailConnection.putExtra(Intent.EXTRA_TEXT, "HELP IM AFTER FALLING ");

        try {
            startActivity(Intent.createChooser(emailConnection, "Send mail..."));
            finish();
            Log.i("Finished ...", "");
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(Accelerometer.this, "There is no emailAddress client installed.", Toast.LENGTH_SHORT).show();
        }
    } //End of [non-original or refactored] code

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

    //Dispalys the values of to the interface
    private void initializeViews() {

        currentXValue = (TextView) findViewById(R.id.currentDisplayedX);
        currentYValue = (TextView) findViewById(R.id.currentDisplayedY);
        currentZValue = (TextView) findViewById(R.id.currentDisplayedZ);

        maxXValue = (TextView) findViewById(R.id.maxDisplayedX);
        maxYValue = (TextView) findViewById(R.id.maxDisplayedY);
        maxZValue = (TextView) findViewById(R.id.maxDisplayedZ);
        maxAccel = (TextView) findViewById(R.id.maxAccel); //added this line myself
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
    public void onSensorChanged(SensorEvent eventChange) {

        // clean current values
        displayCleanValues();
        // display the current x,y,z accelerometer values
        displayCurrentValues();
        // display the max x,y,z accelerometer values
        displayMaxValues();

        //calculate pitch
        double pitch = (double) atan(changeValueX / sqrt(pow(changeValueY, 2) + pow(changeValueZ, 2)));
        pitch = pitch * (180.0 / PI);

        //calculate the roll

        double roll = (double) atan(changeValueY / sqrt(pow(changeValueX, 2) + pow(changeValueZ, 2)));
        roll = roll * (180.0 / PI);

        // get the change of the x,y,z values of the accelerometer
        changeValueX = Math.abs(accelerationValueX - eventChange.values[0]);
        changeValueY = Math.abs(accelerationValueY - eventChange.values[1]);
        changeValueZ = Math.abs(accelerationValueZ - eventChange.values[2]);

        absoluteVI = Math.abs((changeValueX * Math.sin(pitch) + (changeValueY * Math.sin(roll)) - (changeValueZ * Math.cos(pitch) * Math.cos(roll))));

        // if the change is below 2, it is just plain noise
        if (changeValueX < 2)
            changeValueX = 0;
        if (changeValueY < 2)
            changeValueY = 0;
        if ((changeValueX > Vibration) || (changeValueY > Vibration) || (changeValueZ > Vibration)) { //original code had a a bit of incorrect but since has been fixed
            vibrate.vibrate(50);
        }//End of [non-original or refactored] code

        if (acceleration >= threshold || acceleration >= threshold) {
            sendEmail();

            /*****************************************************
             * Title: Everything every Android Developer must know about new Android's Runtime Permission
             * Author: nuuneoi (Android GDE, CTO & CEO at The Cheese Factory)
             * Site owner/sponsor: inthecheesefactory
             * Date: 26 Aug 2015 05:16
             * Code version:
             * Availability: https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
             (Accessed 28 February 2017)
             * Modified: refactored all the variable and the methods name
             *****************************************************/

            String entry = "\n X: " + currentXValue.getText().toString() + ", Y: " + currentYValue.getText().toString() + ", Z: " + currentZValue.getText().toString() +
                    ", Acceleration: " + maxAccel.getText().toString() + ", Basic Algorithm: " + absoluteVI + "\n";
            try {

                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/sean");
                Boolean mkdir = dir.mkdir();
                Log.v(" Save Accelerometer ", mkdir.toString());

                File file = new File(dir, "output.csv");
                FileOutputStream f = new FileOutputStream(file, true);
                //End of [non-original or refactored] code

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
    }
    /*****************************************************
     * Title: Android Accelerometer Example
     * Author: Chryssa Aliferi
     * Site owner/sponsor: javacodegeeks
     * Date: July 18th, 2014
     * Code version:
     * Availability: https://examples.javacodegeeks.com/android/core/hardware/sensor/android-accelerometer-example/
     * (Accessed 8 November 2016)
     * Modified: refactored all the variable and the non standard Android methods
     *****************************************************/

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
        }//End of [non-original or refactored] code
        acceleration = sqrt(pow(Double.parseDouble(currentXValue.getText().toString()), 2) +
                pow(Double.parseDouble(currentYValue.getText().toString()), 2) +
                pow(Double.parseDouble(currentZValue.getText().toString()), 2));
        maxAccel.setText(Double.toString(acceleration));
    }

    /*****************************************************
     * Title: Everything every Android Developer must know about new Android's Runtime Permission
     * Author: nuuneoi (Android GDE, CTO & CEO at The Cheese Factory)
     * Site owner/sponsor: inthecheesefactory
     * Date: 26 Aug 2015 05:16
     * Code version:
     * Availability: https://inthecheesefactory.com/blog/things-you-need-to-know-about-android-m-permission-developer-edition/en
     * (Accessed 28 February 2017)
     * Modified: refactored all the variable and the methods name
     *****************************************************/

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
        } //End of [non-original or refactored] code
    }
}
