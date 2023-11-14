package com.example.intakab1
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.ToggleButton
import androidx.fragment.app.Fragment


class SettingsFragment : Fragment() {

    private lateinit var seekBar: SeekBar
    private lateinit var textView: TextView
    private lateinit var toggleButton: ToggleButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_settings, container, false)

        seekBar = rootView.findViewById(R.id.seekBar)
        textView = rootView.findViewById(R.id.textView)
        toggleButton = rootView.findViewById(R.id.toggleButton)

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textView.text = (progress + 1).toString()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            toggleButton.textOn = if (isChecked) "Miles" else "Kilometers"
            toggleButton.textOff = if (isChecked) "Kilometers" else "Miles"
        }

        view?.findViewById<Button>(R.id.sendDataButton)?.setOnClickListener {
            val value = seekBar.progress + 1
            val isMiles = toggleButton.isChecked

            val intent = Intent(requireActivity(), LocationFragment::class.java)
            intent.putExtra("value", value)
            intent.putExtra("isMiles", isMiles)

            val locationFragment = LocationFragment()
            locationFragment.arguments = intent.extras
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, locationFragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }



        return rootView
    }
}

