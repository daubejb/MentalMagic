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
import kotlinx.android.synthetic.main.fragment_wave.*

private const val TAG = "Wave"
private const val SENSOR_DELAY_MICROS = 16 * 1000 // 16ms

class WaveFragment : Fragment(), SensorEventListener {

    private lateinit var mSensorManager: SensorManager
    private var mProximitySensor: Sensor? = null
    private var mLastAccuracy: Int = SensorManager.SENSOR_STATUS_UNRELIABLE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mSensorManager = this.activity.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
        this.init()
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        mLastAccuracy = accuracy
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (mLastAccuracy == SensorManager.SENSOR_STATUS_UNRELIABLE) {
            return
        }

        if (event != null) {
            if(event.sensor == mProximitySensor) {
                val position = event.values[0]
                Log.w(TAG, "Position: $position")
                if (position < 1) {
                    waveImageView.visibility = View.VISIBLE
                    this.stopListening()
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_wave, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        waveImageView.visibility = View.VISIBLE

        clubsButton.setOnClickListener { waveImageView.setImageResource(R.drawable.clubs) }
        spadesButton.setOnClickListener { waveImageView.setImageResource(R.drawable.spades) }
        heartsButton.setOnClickListener { waveImageView.setImageResource(R.drawable.hearts) }
        diamondsButton.setOnClickListener { waveImageView.setImageResource(R.drawable.diamonds) }

        waveImageView.setOnClickListener { this.startListening()
            Log.w(TAG, "clicked")
            waveImageView.visibility = View.INVISIBLE
        }

        waveYouWillChooseText.setOnLongClickListener {
            waveImageView.visibility = View.VISIBLE
            this.stopListening()
            true
        }
    }

    private fun init() {
        mProximitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
    }

    private fun startListening() {
        if (mProximitySensor == null) {
            Log.w(TAG, "Proximity sensor not available, will not provide data.")
            return
        }
        mSensorManager.registerListener(this, mProximitySensor, SENSOR_DELAY_MICROS)
    }

    private fun stopListening() {
        mSensorManager.unregisterListener(this)
    }

}
