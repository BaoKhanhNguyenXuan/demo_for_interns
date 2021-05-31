package com.app.album_maker.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.fragment.findNavController
import com.app.album_maker.R
import android.graphics.PorterDuff
import android.R.color
import androidx.core.content.ContextCompat
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.Drawable
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes


object AppExtension {

    fun Fragment.goBack(): Boolean {
        return findNavController().popBackStack()
    }

    fun Fragment.actionbar(): ActionBar? {
        return (activity as? AppCompatActivity)?.supportActionBar
    }
}

fun ViewGroup.inflate(@LayoutRes resource: Int): View = LayoutInflater.from(context).inflate(resource, this, false)


fun FragmentActivity.activePage(holder: Int = R.id.fragmentHost): Fragment? {
    return supportFragmentManager.findFragmentById(holder)?.childFragmentManager?.fragments?.firstOrNull()
}

fun Fragment.activePage(holder: Int = R.id.fragmentHost): Fragment? {
    return parentFragmentManager.findFragmentById(holder)?.childFragmentManager?.fragments?.firstOrNull()
}

fun TextView.setDrawableTint(@ColorRes color: Int) {
    for (drawable in this.getCompoundDrawables()) {
        if (drawable != null) {
            drawable.setColorFilter(
                PorterDuffColorFilter(
                    ContextCompat.getColor(
                        getContext(),
                        color
                    ), PorterDuff.Mode.SRC_IN
                )
            )
        }
    }
}