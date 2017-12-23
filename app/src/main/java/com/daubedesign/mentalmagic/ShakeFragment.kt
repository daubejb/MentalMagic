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
import kotlinx.android.synthetic.main.fragment_shake.*

private val xNoise: Float = 7.0f
private val yNoise: Float = 5.0f
private val zNoise: Float = 16.0f
private const val TAG = "Shake"
private const val SENSOR_DELAY_MICROS = 16 * 1000 // 16ms

class ShakeFragment : Fragment(), SensorEventListener {

    private var mInit: Boolean = false
    private lateinit var mSensorManager: SensorManager
    private var mAccelSensor: Sensor? = null
    private var mLastAccuracy: Int = SensorManager.SENSOR_STATUS_UNRELIABLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = this.activity.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        this.init()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        mLastAccuracy = accuracy
    }

    override fun onSensorChanged(event: SensorEvent?) {

        if (mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return
        }

        if (event != null) {

            if(event.sensor == mAccelSensor) {

                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                var mLastX = 0.0f
                var mLastY = 0.0f
                var mLastZ = 0.0f
                val alpha = 0.8f

                var gravity: FloatArray = floatArrayOf(0.0f, 0.0f, 0.0f)

                if (!mInit) {
                    mLastX = x
                    mLastY = y
                    mLastZ = z
                    mInit = true
                } else {
                    var deltaX = Math.abs(mLastX - x)
                    var deltaY = Math.abs(mLastY - y)
                    var deltaZ = Math.abs(mLastZ - z)
                    if (deltaX < xNoise) { deltaX = 0.0f }
                    if (deltaY < yNoise) { deltaY = 0.0f }
                    if (deltaZ < zNoise) { deltaZ = 0.0f }

                    mLastX = x
                    mLastY = y
                    mLastZ = z

                    if (deltaX > deltaY) {
                        shakeImageView.setImageResource(R.drawable.hearts)
                        shakeImageView.visibility = View.VISIBLE
                    } else if ( deltaY > deltaX) {
                        shakeImageView.setImageResource(R.drawable.spades)
                        shakeImageView.visibility = View.VISIBLE
                    } else if ((deltaZ > deltaX) && (deltaZ > deltaY)) {
                        shakeImageView.setImageResource(R.drawable.diamonds)
                        shakeImageView.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_shake, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        shakeImageView.visibility = View.VISIBLE

        shakeImageView.setOnClickListener { this.startListening()
            Log.w(TAG, "clicked")
            shakeImageView.visibility = View.INVISIBLE
        }

        shakeYouWillChooseText.setOnLongClickListener {
            shakeImageView.visibility = View.VISIBLE
            this.stopListening();
            true
        }
    }

    private fun init() {
        mAccelSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    private fun startListening() {
        if (mAccelSensor == null) {
            Log.w(TAG, "Accelerometer sensor not available, will not provide data.")
            return
        }
        mSensorManager.registerListener(this, mAccelSensor, SENSOR_DELAY_MICROS)
    }

    private fun stopListening() {
        mSensorManager.unregisterListener(this)
    }

}
