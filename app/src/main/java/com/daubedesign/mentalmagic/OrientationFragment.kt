package com.daubedesign.mentalmagic

import android.app.Activity
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_orientation.*
import kotlin.math.abs

private const val TAG = "Orientation"
private const val SENSOR_DELAY_MICROS = 16 * 1000 // 16ms

class OrientationFragment : Fragment(), SensorEventListener {

    private var mReady = false
    private var triggered = false
    private var oldRotation = 0.0f
    private var oldFlip = 0.0f
    private val youWillChooseDetecting = "You will choose the:"
    private val youWillChooseDetected = "You will choose the"
    private lateinit var mSensorManager: SensorManager

    private var mRotationSensor: Sensor? = null
    private var mLastAccuracy: Int = SensorManager.SENSOR_STATUS_UNRELIABLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = this.activity.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        this.init()
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_orientation, container, false)


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        youWillChooseText.text = youWillChooseDetecting

        orientationImageView.setOnClickListener { this.startListening()
            Log.w(TAG, "clicked")
            orientationImageView.visibility = View.INVISIBLE
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
       mLastAccuracy = accuracy
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return
        }
        if (event != null) {
            if (event.sensor == mRotationSensor) {
                val rotationMatrix = FloatArray(9)
                SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)

                // Transform rotation matrix into azimuth/pitch/roll
                val orientation = FloatArray(3)
                SensorManager.getOrientation(rotationMatrix, orientation)

                // Convert radians to degrees
                val flip = orientation[1] * -57
                val rotation = orientation[2] * -57

                if (!mReady) {
                    Log.w(TAG, "Not Ready")
                    oldRotation = rotation
                    oldFlip = flip
                    mReady = true
                } else {
                    val changeInRotation = oldRotation + rotation
                    val changeInFlip = oldFlip + flip

                    Log.w(TAG, "Change Rotation: $changeInRotation")
                    Log.w(TAG, "Change Flip: $changeInFlip")

                    if ( abs (oldRotation) > 177) {
                        triggered = true
                    }

                    if ( triggered ) {
                        // phone is lifted from top of screen upwards around long access
                        if ( changeInFlip > 10 ) {
                            orientationImageView.setImageResource(R.drawable.clubs)
                            orientationImageView.visibility = View.VISIBLE
                            youWillChooseText.text = youWillChooseDetected
                            this.stopListening()
                            triggered = false
                        }

                        // phone is lifted from bottom of screen upwards around long access
                        if ( changeInFlip < -10 ) {
                            orientationImageView.setImageResource(R.drawable.spades)
                            orientationImageView.visibility = View.VISIBLE
                            youWillChooseText.text = youWillChooseDetected
                            this.stopListening()
                            triggered = false
                        }

                        //phone is lifted from right of screen upwards around short access
                        if ( (changeInRotation > -350) && (changeInRotation < -300) ) {
                            orientationImageView.setImageResource(R.drawable.hearts)
                            orientationImageView.visibility = View.VISIBLE
                            youWillChooseText.text = youWillChooseDetected
                            this.stopListening()
                            triggered = false
                        }

                        //phone is lifted from left of screen upwards around short access
                        if ( (changeInRotation < 350) && (changeInRotation > 300) ) {
                            orientationImageView.setImageResource(R.drawable.diamonds)
                            orientationImageView.visibility = View.VISIBLE
                            youWillChooseText.text = youWillChooseDetected
                            this.stopListening()
                            triggered = false
                        }

                    }
                }
                oldRotation = rotation
                oldFlip = flip

                Log.w(TAG, "old rotation:" + oldRotation)
                Log.w(TAG, "old flip:" + oldFlip)
            }
        }
    }

    private fun init() {
        // Can be null if the sensor hardware is not available
        mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    }

    private fun startListening() {
        if (mRotationSensor == null) {
            Log.w(TAG, "Rotation vector sensor not available; will not provide orientation data.")
            return
        }
        mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY_MICROS)
    }

    private fun stopListening() {
        mSensorManager.unregisterListener(this)
    }
}