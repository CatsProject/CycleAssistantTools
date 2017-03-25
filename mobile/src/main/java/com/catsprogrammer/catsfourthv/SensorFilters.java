package com.catsprogrammer.catsfourthv;

import android.hardware.SensorManager;
import android.util.Log;

import com.catsprogrammer.shared.TagName;

import java.util.Timer;

import static com.catsprogrammer.catsfourthv.Data.accMagOrientationM;
import static com.catsprogrammer.catsfourthv.Data.accMagOrientationW;
import static com.catsprogrammer.catsfourthv.Data.checkSend;
import static com.catsprogrammer.catsfourthv.Data.fusedSensorW;
import static com.catsprogrammer.catsfourthv.Data.gyroMatrixM;
import static com.catsprogrammer.catsfourthv.Data.gyroMatrixW;
import static com.catsprogrammer.catsfourthv.Data.gyroOrientationM;
import static com.catsprogrammer.catsfourthv.Data.gyroOrientationW;
import static com.catsprogrammer.catsfourthv.Data.isInput;
import static com.catsprogrammer.catsfourthv.Data.rotationMatrixM;
import static com.catsprogrammer.catsfourthv.Data.rotationMatrixW;

/**
 * Created by C on 2016-11-20.
 */

public class SensorFilters implements TagName {
    private MatrixCalculator matrixCalculator = new MatrixCalculator();
    private boolean initState;
    private long timestamp;

    private int time = 0;

    //public static SensorFiltering instance;

    private float[] ac = new float[3];
    private float[] gy = new float[3];
    private float[] mg = new float[3];

    public SensorFilters(boolean isMobileOrWearable) {
        initState = true;


        if(isMobileOrWearable) {
            for(int i = 0; i < MATRIX_SIZE; i++) {
                gyroMatrixM[i] = 0.0f;

                if(i == 0 || i == 4 || i == 8) {
                    gyroMatrixM[i] = 1.0f;
                }
            }
            gyroOrientationM[0] = 0.0f;
            gyroOrientationM[1] = 0.0f;
            gyroOrientationM[2] = 0.0f;
        }
        else {
            for (int i = 0; i < MATRIX_SIZE; i++) {
                gyroMatrixW[i] = 0.0f;

                if (i == 0 || i == 4 || i == 8) {
                    gyroMatrixW[i] = 1.0f;
                }
            }
            gyroOrientationW[0] = 0.0f;
            gyroOrientationW[1] = 0.0f;
            gyroOrientationW[2] = 0.0f;
        }
    }

    public void stopTimer() {
    }


    public void getSensor(float[] sensorValues, int type, long timestamp) { // mobile
        switch (type) {
            case SENS_ACCELEROMETER:
                ac = sensorValues;
                calculateAccMagOrientation(true);
                break;
            case SENS_GYROSCOPE:
                gy = sensorValues;
                gyroFunctionM(timestamp,  sensorValues);
                break;
            case SENS_MAGNETIC:
                mg = sensorValues;
                break;
        }

        if(type == SENS_ACCELEROMETER) {
            Log.i("GetSensor","ACC");
        }
        if(type == SENS_MAGNETIC) {
            Log.i("GetSensor","MAG");
        }
        if(type == SENS_GYROSCOPE) {
            Log.i("GetSensor","GYRO");
        }
    }

    public void getSensor(float[] ac, float[] gy, float[] mg, long timestamp){ //wearable

        this.ac = ac;
        this.gy = gy;
        this.mg = mg;
        calculateAccMagOrientation(false);
        gyroFunctionW(timestamp, this.gy);

        Log.i("checkAcc", ac[0] + ", " + ac[1] + ", " +ac[2]);
        Log.i("checkSensorGyroW", gyroOrientationW[0] + ", " + gyroOrientationW[1] + ", " +gyroOrientationW[2]);
        Log.i("checkSensorAccW", accMagOrientationW[0] + ", " + accMagOrientationW[1] + ", " +accMagOrientationW[2]);


        for (int i = 0; i < 3; i++) {
            if (gyroOrientationW[i] < -0.5 * Math.PI && accMagOrientationW[i] > 0.0) {
                fusedSensorW[time][i] = (float) (FILTER_COEFFICIENT * (gyroOrientationW[i] + 2.0 * Math.PI) + oneMinusCoeff * accMagOrientationW[i]);
                fusedSensorW[time][i] -= (fusedSensorW[time][i] > Math.PI) ? 2.0 * Math.PI : 0;
            } else if (accMagOrientationW[i] < -0.5 * Math.PI && gyroOrientationW[i] > 0.0) {
                fusedSensorW[time][i] = (float) (FILTER_COEFFICIENT * gyroOrientationW[i] + oneMinusCoeff * (accMagOrientationW[i] + 2.0 * Math.PI));
                fusedSensorW[time][i] -= (fusedSensorW[time][i] > Math.PI) ? 2.0 * Math.PI : 0;
            } else {
                fusedSensorW[time][i] = FILTER_COEFFICIENT * gyroOrientationW[i] + oneMinusCoeff * accMagOrientationW[i];
            }
        }
        gyroMatrixW = matrixCalculator.getRotationMatrixFromOrientation(fusedSensorW[time]);
        System.arraycopy(fusedSensorW[time], 0, gyroOrientationW, 0, 3);

        isInput = true;


        Log.i("Timer1"," : " +time);
        //그냥 time 30 지나면 초기화
        time++; if(time >= 30) time = 0;


    }


    public void calculateAccMagOrientation(boolean isMobileOrWearable) {
        if(isMobileOrWearable) {
            if (SensorManager.getRotationMatrix(rotationMatrixM, null, ac, mg)) {
                SensorManager.getOrientation(rotationMatrixM, accMagOrientationM);
            }
        }
        else {
            if (SensorManager.getRotationMatrix(rotationMatrixW, null, ac, mg)) {
                SensorManager.getOrientation(rotationMatrixW, accMagOrientationW);
            }
        }
    }

    public void gyroFunctionM(long time, float[] values) {
        if (accMagOrientationM == null)
            return;

        if (initState) {
            float[] initMatrix = new float[9];
            initMatrix = MatrixCalculator.getRotationMatrixFromOrientation(accMagOrientationM);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);
            gyroMatrixM = MatrixCalculator.matrixMultiplication(gyroMatrixM, initMatrix);

            //  System.arraycopy(temp, 0, gyroMatrix, 0, 4);
            initState = false;
        }

        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        float[] deltaVector = new float[4];
        if (timestamp != 0) {
            final float dT = (time - timestamp) * NS2S;
            System.arraycopy(values, 0, gy, 0, 3);
            getRotationVectorFromGyro(gy, deltaVector, dT / 2.0f);
        }

        timestamp = time;

        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);

        gyroMatrixM = MatrixCalculator.matrixMultiplication(gyroMatrixM, deltaMatrix);

        //System.arraycopy(temp, 0, gyroMatrix, 0, 4);
        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(gyroMatrixM, gyroOrientationM);
    }


    public void gyroFunctionW(long time, float[] values) {

        if (accMagOrientationW == null)
            return;

        if (initState) {
            float[] initMatrix = new float[9];
            initMatrix = MatrixCalculator.getRotationMatrixFromOrientation(accMagOrientationW);
            float[] test = new float[3];
            SensorManager.getOrientation(initMatrix, test);
            gyroMatrixW = MatrixCalculator.matrixMultiplication(gyroMatrixW, initMatrix);

            //  System.arraycopy(temp, 0, gyroMatrix, 0, 4);
            initState = false;
        }

        // copy the new gyro values into the gyro array
        // convert the raw gyro data into a rotation vector
        float[] deltaVector = new float[4];
        if (timestamp != 0) {
            final float dT = (time - timestamp) * NS2S;
            System.arraycopy(values, 0, gy, 0, 3);
            getRotationVectorFromGyro(gy, deltaVector, dT / 2.0f);
        }

        timestamp = time;

        float[] deltaMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaMatrix, deltaVector);

        gyroMatrixW = MatrixCalculator.matrixMultiplication(gyroMatrixW, deltaMatrix);

        //System.arraycopy(temp, 0, gyroMatrix, 0, 4);

        // get the gyroscope based orientation from the rotation matrix
        SensorManager.getOrientation(gyroMatrixW, gyroOrientationW);
    }

    private void getRotationVectorFromGyro(float[] gyroValues, float[] deltaRotationVector, float timeFactor) {
        float[] normValues = new float[3];

        // Calculate the angular speed of the sample

        float omegaMagnitude = (float) Math.sqrt(gyroValues[0] * gyroValues[0] + gyroValues[1] * gyroValues[1] + gyroValues[2] * gyroValues[2]);
        //float omegaMagnitude = (float) Math.sqrt(Math.pow(gyroValues[0], 2) + Math.pow(gyroValues[1], 2) + Math.pow(gyroValues[2], 2));

        // Normalize the rotation vector if it's big enough to get the axis

        if (omegaMagnitude > EPSILON) {
            for(int i = 0; i < 3; i++) {
                normValues[i] = gyroValues[i] / omegaMagnitude;
            }
        }

        float thetaOverTwo = omegaMagnitude * timeFactor;
        float sinThetaOverTwo = (float) Math.sin(thetaOverTwo);
        float cosThetaOverTwo = (float) Math.cos(thetaOverTwo);

        for(int i = 0; i < 3; i++) {
            deltaRotationVector[i] = sinThetaOverTwo * normValues[i];
        }
        deltaRotationVector[3] = cosThetaOverTwo;
    }
}
