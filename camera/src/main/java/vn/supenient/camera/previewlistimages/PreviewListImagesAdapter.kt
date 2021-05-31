package vn.supenient.camera.previewlistimages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_image.view.*
import vn.supenient.camera.R

class PreviewListImagesAdapter(private val listImagePath: ArrayList<String>, val onItemClick: (String, Int) -> Unit) :
    RecyclerView.Adapter<PreviewListImagesAdapter.ViewHolder>() {

    companion object {
        const val DELETE_IMAGE = "delete_image"
        const val VIEW_IMAGE = "view_image"
    }

    private var isShowDelete = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false))
    }

    override fun getItemCount(): Int = listImagePath.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindData(listImagePath[position], position)
    }

    inner class ViewHolder(viewItem: View) : RecyclerView.ViewHolder(viewItem) {
        private val ivImage = viewItem.ivImage
        private val ivDelete = viewItem.ivDelete
        fun bindData(imagePath: String, position: Int) {
            val circularProgressDrawable = CircularProgressDrawable(itemView.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()
            Glide.with(itemView.context)
                .load(imagePath)
                .placeholder(circularProgressDrawable)
                .error(R.drawable.placeholder)
                .centerCrop()
                .into(ivImage)
            ivDelete.visibility = if (isShowDelete) View.VISIBLE else View.GONE
            ivDelete.setOnClickListener {
                onItemClick(DELETE_IMAGE, position)
            }
            ivImage.setOnClickListener {
                onItemClick(VIEW_IMAGE, position)
            }
        }
    }

    fun changeDeleteStatus() {
        isShowDelete = !isShowDelete
        notifyDataSetChanged()
    }
}