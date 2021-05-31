package vn.supenient.camera.previewlistimages

import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.appcompat.widget.AppCompatImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import vn.supenient.camera.R

class ImageSliderAdapter(private val listImage: ArrayList<String>) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return listImage.size
    }

    override fun instantiateItem(@NonNull container: ViewGroup, @NonNull position: Int): Any {
        val imageView = AppCompatImageView(container.context)
        Glide.with(container.context).load(listImage[position]).error(R.drawable.placeholder)
            .into(imageView)
        container.addView(imageView)
        return imageView
    }

    override fun destroyItem(@NonNull container: ViewGroup, position: Int, @NonNull `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getItemPosition(`object`: Any): Int {
        return POSITION_NONE
    }
}