package com.example.alertdialogdemo

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class MyDialog : DialogFragment() {

    enum class DialogType {
        SETTINGS, DESIGN, CITY
    }

    interface DialogListener {
        fun onDesignSelected(index: Int)
        fun onCitySelected(city: String)
    }

    private var listener: DialogListener? = null
    private var type: DialogType = DialogType.SETTINGS

    fun setListener(listener: DialogListener) {
        this.listener = listener
    }

    companion object {
        fun newInstance(type: DialogType): MyDialog {
            val frag = MyDialog()
            frag.type = type
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireContext())
        
        when (type) {
            DialogType.SETTINGS -> {
                builder.setTitle(R.string.select_option)
                    .setItems(R.array.settings_options) { _: DialogInterface, which: Int ->
                        val nextType = if (which == 0) DialogType.DESIGN else DialogType.CITY
                        val nextDialog = newInstance(nextType)
                        nextDialog.setListener(listener!!)
                        nextDialog.show(parentFragmentManager, "sub_dialog")
                    }
            }
            DialogType.DESIGN -> {
                builder.setTitle(R.string.select_weather_view)
                    .setItems(R.array.weather_views) { _: DialogInterface, which: Int ->
                        listener?.onDesignSelected(which)
                    }
            }
            DialogType.CITY -> {
                val cities = resources.getStringArray(R.array.cities)
                builder.setTitle(R.string.select_city)
                    .setItems(cities) { _: DialogInterface, which: Int ->
                        listener?.onCitySelected(cities[which])
                    }
            }
        }
        
        builder.setNegativeButton(R.string.cancel, null)
        return builder.create()
    }
}