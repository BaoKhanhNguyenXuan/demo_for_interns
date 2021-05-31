package vn.supenient.camera.previewlistimages

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager.widget.ViewPager
import kotlinx.android.synthetic.main.activity_preview_images.*
import vn.supenient.camera.BaseLanguageActivity
import vn.supenient.camera.SCameraActivity
import vn.supenient.camera.R
import vn.supenient.camera.previewlistimages.PreviewListImagesAdapter.Companion.DELETE_IMAGE
import vn.supenient.camera.previewlistimages.PreviewListImagesAdapter.Companion.VIEW_IMAGE
import java.io.File

@SuppressLint("SetTextI18n")
class PreviewListImagesActivity : BaseLanguageActivity() {

    private var listImagePath = arrayListOf<String>()
    private var previewImagesAdapter: PreviewListImagesAdapter? = null
    private var imageSliderAdapter: ImageSliderAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview_images)
        listImagePath = savedInstanceState?.getStringArrayList(SCameraActivity.IMAGES_CAPTURE)
            ?: (intent?.extras?.getStringArrayList(SCameraActivity.IMAGES_CAPTURE) ?: arrayListOf())
        setupView()

        previewImagesAdapter = PreviewListImagesAdapter(listImagePath) { handle, position ->
            when (handle) {
                DELETE_IMAGE -> {
                    listImagePath.removeAt(position)
                    previewImagesAdapter?.notifyDataSetChanged()
                }
                VIEW_IMAGE -> {
                    viewImage(position)
                }
            }
        }
        rcvPreviewImages.adapter = previewImagesAdapter
    }

    private fun setupView() {
        toolbar?.let {
            setSupportActionBar(it)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = this.getString(R.string.preview_image)

        rcvPreviewImages.layoutManager =
            GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        ivPrevious.setOnClickListener {
            imageSlider.currentItem = imageSlider.currentItem - 1
        }
        ivNext.setOnClickListener {
            imageSlider.currentItem = imageSlider.currentItem + 1
        }

        imageSlider.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {}

            override fun onPageSelected(position: Int) {
                positionImage.text = "${position + 1}/${listImagePath.size}"
                checkPositionImage(position)
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.preview_image_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuDelete -> {
                if (viewImageVisiable()) {
                    deleteImage()
                } else
                    delete()
                true
            }
            else -> {
                false
            }
        }
    }

    private fun viewImageVisiable(): Boolean {
        return llViewImage.visibility == View.VISIBLE
    }

    private fun updateViewImageGone() {
        llViewImage.visibility = View.GONE
    }

    private fun viewImage(position: Int) {
        imageSliderAdapter = ImageSliderAdapter(listImagePath)
        imageSlider.adapter = imageSliderAdapter
        positionImage.text = "${position + 1}/${listImagePath.size}"
        imageSlider.currentItem = position
        llViewImage.visibility = View.VISIBLE
        checkPositionImage(position)
    }

    private fun showNext() {
        ivNext.visibility = View.VISIBLE
    }

    private fun hideNext() {
        ivNext.visibility = View.INVISIBLE
    }

    private fun showPre() {
        ivPrevious.visibility = View.VISIBLE
    }

    private fun hidePre() {
        ivPrevious.visibility = View.INVISIBLE
    }

    private fun checkPositionImage(id: Int) {
        if (listImagePath.size == 1){
            hideNext()
            hidePre()
        }else{
            if (listImagePath.size > 1){
                if (id > 0)
                    showPre()
                else
                    hidePre()
                if (id < listImagePath.size - 1)
                    showNext()
                else
                    hideNext()
            }
        }
    }

    private fun delete() {
        previewImagesAdapter?.changeDeleteStatus()
    }

    override fun onBackPressed() {
        if (viewImageVisiable() && listImagePath.isNotEmpty()) {
            updateViewImageGone()
        } else {
            val intent = Intent()
            intent.putStringArrayListExtra(SCameraActivity.PREVIEW_RESULT, listImagePath)
            setResult(Activity.RESULT_OK, intent)
            super.onBackPressed()
        }
    }

    private fun deleteImage() {
        val path = listImagePath[imageSlider.currentItem]
        listImagePath.remove(path)
        positionImage.text = "${imageSlider.currentItem + 1}/${listImagePath.size}"
        File(path).delete()
        if (listImagePath.isEmpty())
            onBackPressed()
        else{
            previewImagesAdapter?.notifyDataSetChanged()
            imageSliderAdapter?.notifyDataSetChanged()
        }
    }
}
