package com.daubedesign.mentalmagic

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_wave.*

class WaveFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_wave, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        clubsButton.setOnClickListener { waveImageView.setImageResource(R.drawable.clubs) }
        spadesButton.setOnClickListener { waveImageView.setImageResource(R.drawable.spades) }
        heartsButton.setOnClickListener { waveImageView.setImageResource(R.drawable.hearts) }
        diamondsButton.setOnClickListener { waveImageView.setImageResource(R.drawable.diamonds) }

    }

}// Required empty public constructor
