package vn.supenient.camera.gallery

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_gallery.*
import vn.supenient.camera.BaseLanguageActivity
import vn.supenient.camera.CameraUtils
import vn.supenient.camera.R

enum class GalleryType {
    PHOTO,
    VIDEO
}

@SuppressLint("SetTextI18n")
class SGalleryActivity : BaseLanguageActivity() {

    companion object {
        const val GALLERY_RESULT = "GALLERY_RESULT"
        const val GALLERY_TYPE = "GALLERY_TYPE"
        const val LIMIT_UPLOAD = "LIMIT_UPLOAD"
        const val MAX_LENGTH_VIDEO = "MAX_LENGTH_VIDEO"

        fun startGallery(fragment: Fragment, requestCode: Int, limitUpload: Int, maxLengthVideo: Int? = 0, type: GalleryType = GalleryType.PHOTO) {
            val intent = Intent(fragment.context, SGalleryActivity::class.java)
            intent.putExtra(GALLERY_TYPE, type.name)
            intent.putExtra(LIMIT_UPLOAD, limitUpload)
            intent.putExtra(MAX_LENGTH_VIDEO, maxLengthVideo)
            fragment.startActivityForResult(intent, requestCode)
        }

        fun startGallery(activity: Activity, requestCode: Int, limitUpload: Int, maxLengthVideo: Int? = 0, type: GalleryType = GalleryType.PHOTO) {
            val intent = Intent(activity, SGalleryActivity::class.java)
            intent.putExtra(GALLERY_TYPE, type.name)
            intent.putExtra(LIMIT_UPLOAD, limitUpload)
            intent.putExtra(MAX_LENGTH_VIDEO, maxLengthVideo)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private lateinit var adapter: SGalleryAdapter
    private var listMedia = ArrayList<String>()
    private var limitSize: Int = 10
    private var type: GalleryType = GalleryType.PHOTO
    private var maxLengthVideo: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)
        (savedInstanceState?.getString(GALLERY_TYPE) ?: intent?.getStringExtra(GALLERY_TYPE))?.let {
            type = enumValueOf(it)
        }
        (savedInstanceState?.getInt(LIMIT_UPLOAD) ?: intent?.getIntExtra(LIMIT_UPLOAD, 10))?.let {limitUpload ->
            limitSize = limitUpload
        }
        (savedInstanceState?.getInt(MAX_LENGTH_VIDEO) ?: intent?.getIntExtra(MAX_LENGTH_VIDEO, 0))?.let {maxLength ->
            maxLengthVideo = maxLength
        }
        setupView()
    }

    private fun setupView() {
        findViewById<Toolbar>(R.id.toolbar)?.let {
            setSupportActionBar(it)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btnOpenImageSelected.setOnClickListener {
            if (adapter.mediaSelected.isNotEmpty()){
                val intent = Intent()
                intent.putStringArrayListExtra(GALLERY_RESULT, adapter.mediaSelected)
                setResult(Activity.RESULT_OK, intent)
            }
            finish()
        }

        updateCount(0)
        showImage.layoutManager = GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false)
        adapter = SGalleryAdapter(
            listMedia,
            limitSize,
            type
        ) { _, isMax ->
            updateCount(adapter.mediaSelected.size)
            if (isMax) {
                showAlertMaxSelected()
            }

        }
        showImage.adapter = adapter
        pbLoading.visibility = View.VISIBLE
        LoadImageWorker(this, callBack = {
            listMedia.clear()
            listMedia.addAll(it)
            adapter.notifyDataSetChanged()
            pbLoading.visibility = View.GONE
        }).execute(maxLengthVideo)
    }

    private fun showAlertMaxSelected() {
        AlertDialog.Builder(this)
            .setMessage(getString(R.string.max_value_upload, limitSize))
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun updateCount(count: Int) {
        countImageSelect.text = "$count/$limitSize"
    }

    override fun onBackPressed() {
        if (adapter.mediaSelected.size == 0)
            super.onBackPressed()
        else
            dialogBack()
    }

    private fun dialogBack() {
        AlertDialog.Builder(this)
            .setMessage(R.string.alert_setting_change_dont_save)
            .setPositiveButton(R.string.ok) { dialog, which ->
                super.onBackPressed()
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    class LoadImageWorker(val context: Context, private val callBack: ((List<String>) -> Unit)?) :
        AsyncTask<Int, Void, List<String>>() {

        override fun doInBackground(vararg params: Int?): List<String> {
            return CameraUtils.getMediaFromStorage(context, params[0] ?: 0)
        }

        override fun onPostExecute(result: List<String>?) {
            super.onPostExecute(result)
            result?.let { callBack?.invoke(it) }
        }

        override fun onCancelled() {
            super.onCancelled()
            callBack?.invoke(arrayListOf())
        }

        override fun onCancelled(result: List<String>?) {
            super.onCancelled(result)
            result?.let { callBack?.invoke(it) }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(GALLERY_TYPE, type.name)
        outState.putInt(LIMIT_UPLOAD, limitSize)
        outState.putInt(MAX_LENGTH_VIDEO, maxLengthVideo)
    }
}