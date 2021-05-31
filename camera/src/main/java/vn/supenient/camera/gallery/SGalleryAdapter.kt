package vn.supenient.camera.gallery

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_view_detail_album_image.view.*
import vn.supenient.camera.R

class SGalleryAdapter(
    private val listMedia: ArrayList<String>,
    private val maxSizeSelected: Int = 0,
    private val type: GalleryType = GalleryType.PHOTO,
    val onItemClick: ((Int, Boolean) -> Unit)? = null) : RecyclerView.Adapter<SGalleryAdapter.ViewHolder>() {

    private val listMediaSelected = arrayListOf<String>()

    val mediaSelected: ArrayList<String>
        get() = listMediaSelected

    inner class ViewHolder(private val containerView: View) : RecyclerView.ViewHolder(containerView) {

        private val ivPlay = containerView.ivPlay
        private val btnCheck:CheckBox = containerView.checkSelected
        private val image = containerView.imgItemImage

        fun bindData(position: Int, path: String) {
            ivPlay.visibility = if (type == GalleryType.VIDEO) View.VISIBLE else View.GONE

            if (listMediaSelected.contains(path)) {
                btnCheck.visibility = View.VISIBLE
            } else {
                btnCheck.visibility = View.GONE
            }
            Glide.with(containerView.context)
                .load(path)
                .placeholder(R.drawable.placeholder)
                .centerCrop()
                .centerInside()
                .into(image)
            image.setOnClickListener {
                btnCheck.performClick()
            }
            btnCheck.setOnClickListener {
                val isMax = if (listMediaSelected.contains(path)) {
                    listMediaSelected.remove(path)
                    btnCheck.visibility = View.GONE
                    false
                } else {
                    if (maxSizeSelected > 0 && listMediaSelected.size < maxSizeSelected) {
                        btnCheck.isChecked = true
                        listMediaSelected.add(path)
                        btnCheck.visibility = View.VISIBLE
                        false
                    } else {
                        true
                    }
                }
                onItemClick?.invoke(position, isMax)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_view_detail_album_image, parent, false)
        return ViewHolder(v)
    }

    override fun getItemCount(): Int {
        return listMedia.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(position, listMedia[position])
    }
}