package vn.supenient.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.os.Parcelable
import android.os.StatFs
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.WindowManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import io.fotoapparat.Fotoapparat
import io.fotoapparat.configuration.CameraConfiguration
import io.fotoapparat.log.fileLogger
import io.fotoapparat.log.logcat
import io.fotoapparat.log.loggers
import io.fotoapparat.selector.*
import kotlinx.android.synthetic.main.activity_camera.*
import vn.supenient.camera.previewlistimages.PreviewListImagesActivity
import java.io.File
import java.io.FileOutputStream
import java.util.*


enum class CameraType {
    FRONT_CAMERA,
    BACK_CAMERA
}

class SCameraActivity<T : Parcelable> : BaseLanguageActivity() {

    companion object {
        const val CAMERA_DATA_EXTRA = "CAMERA_DATA_EXTRA"
        const val IMAGES_CAPTURE = "IMAGES_CAPTURE"
        const val PREVIEW_RESULT = "PREVIEW_RESULT"
        const val CAMERA_RESULT = "CAMERA_RESULT"
        const val CAMERA_RESULT_PATH = "CAMERA_RESULT_PATH"
        const val CAMERA_REQUEST_CODE = 2211
        const val PREVIEW_RESULT_CODE = 1122

        private const val MAX_BYTE: Int = 1024 * 1024
        private const val FREE_SPACE_NEED = 50

        inline fun <reified T : Parcelable> start(context: Context, builder: CameraBuilder<T>) {
            val intent = Intent(context, SCameraActivity::class.java)
            intent.putExtra(CAMERA_DATA_EXTRA, builder)
            context.startActivity(intent)
        }

        inline fun <reified T : Parcelable> startForResult(
            fragment: Fragment,
            builder: CameraBuilder<T>,
            requestCode: Int = CAMERA_REQUEST_CODE
        ) {
            val intent = Intent(fragment.context, SCameraActivity::class.java)
            intent.putExtra(CAMERA_DATA_EXTRA, builder)
            fragment.startActivityForResult(intent, requestCode)
        }

        inline fun <reified T : Parcelable> startForResult(
            activity: Activity,
            builder: CameraBuilder<T>,
            requestCode: Int = CAMERA_REQUEST_CODE
        ) {
            val intent = Intent(activity, SCameraActivity::class.java)
            intent.putExtra(CAMERA_DATA_EXTRA, builder)
            activity.startActivityForResult(intent, requestCode)
        }
    }

    private lateinit var fotoapparat: Fotoapparat
    private val listImagePath: ArrayList<String> = arrayListOf()

    private val listImagesUpload = arrayListOf<T>()
    private var cameraBuilder: CameraBuilder<T>? = null
    private val limitSize: Int
        get() = cameraBuilder?.limitSize ?: 10
    private val imageSize: Int
        get() = cameraBuilder?.imageSize ?: 1
    private lateinit var cameraUse: CameraType

    private val cameraConfiguration = CameraConfiguration(
        pictureResolution = standardRatio(highestResolution()),
        previewResolution = standardRatio(highestResolution()),
        focusMode = firstAvailable(  // (optional) use the first focus mode which is supported by device
            continuousFocusPicture(),
            autoFocus(), // in case if continuous focus is not available on device, auto focus will be used
            fixed()             // if even auto focus is not available - fixed focus mode will be used
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        cameraBuilder =
            savedInstanceState?.getParcelable(CAMERA_DATA_EXTRA) ?: intent.getParcelableExtra(
                CAMERA_DATA_EXTRA
            )
        cameraUse = cameraBuilder?.cameraUse ?: CameraType.BACK_CAMERA
        setupView()
    }

    private fun setupView() {
        findViewById<Toolbar>(R.id.toolbar)?.let {
            setSupportActionBar(it)
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "SCSoft"
        fotoapparat = makeFotoapparat()
        thumnail.setOnClickListener {
            showImage()
        }

        btnSave.setOnClickListener {
            uploadImages()
        }

        visibilityImagesCount()

        captureImage.setOnClickListener {
            takeImage()
        }
    }

    private fun visibilityImagesCount() {
        if (listImagePath.isNullOrEmpty())
            hideImagesCount()
        else
            showImagesCount(listImagePath.last(), listImagePath.size)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.camera_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuCamera -> {
                changeCamera()
                true
            }
            else -> {
                false
            }
        }
    }

    private fun changeCamera() {
        when (cameraUse) {
            CameraType.FRONT_CAMERA -> {
                fotoapparat.switchTo(
                    lensPosition = back(),
                    cameraConfiguration = cameraConfiguration
                )
                cameraUse = CameraType.BACK_CAMERA
            }
            CameraType.BACK_CAMERA -> {
                fotoapparat.switchTo(
                    lensPosition = front(),
                    cameraConfiguration = cameraConfiguration
                )
                cameraUse = CameraType.FRONT_CAMERA
            }
        }
    }

    override fun onStart() {
        super.onStart()
        createCamera()
    }

    private fun createFile(): File {
        return CameraUtils.createImageFile(this)
    }

    private fun enableTouch() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
    }

    private fun disableTouch() {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        )
    }

    override fun onStop() {
        super.onStop()
        stopCamera()
    }

    private fun showAlert(messageRes: Int) {
        AlertDialog.Builder(this)
            .setMessage(messageRes)
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun showAlertMaxSize(message: Int, num: Int) {
        AlertDialog.Builder(this)
            .setMessage(getString(message, num))
            .setPositiveButton(R.string.ok, null)
            .show()
    }

    private fun showImage() {
        val intent = Intent(this, PreviewListImagesActivity::class.java)
        intent.putStringArrayListExtra(IMAGES_CAPTURE, listImagePath)
        startActivityForResult(intent, PREVIEW_RESULT_CODE)
    }

    private fun showImagesCount(imagePath: String?, imagesCount: Int) {
        Glide.with(this)
            .load(imagePath ?: "")
            .error(R.drawable.placeholder)
            .into(thumnail)
        tvImagesCount.visibility = VISIBLE
        tvImagesCount.text = imagesCount.toString()
        btnSave.visibility = VISIBLE
    }

    override fun onBackPressed() {
        resultCamera()
        super.onBackPressed()
    }

    private fun resultCamera() {
        val intent = Intent()
        intent.putParcelableArrayListExtra(CAMERA_RESULT, listImagesUpload)
        intent.putStringArrayListExtra(CAMERA_RESULT_PATH, listImagePath)
        setResult(Activity.RESULT_OK, intent)
    }

    private fun hideImagesCount() {
        thumnail.setImageBitmap(null)
        tvImagesCount.visibility = GONE
        btnSave.visibility = GONE
    }

    private fun stopCamera() {
        fotoapparat.stop()
    }

    private fun createCamera() {
        fotoapparat.start()
    }

    private fun makeFotoapparat(): Fotoapparat {
        return Fotoapparat.with(this)
            .into(cameraView)           // view which will draw the camera preview
            .previewResolution(standardRatio(highestResolution()))
            .photoResolution(standardRatio(highestResolution()))
            .lensPosition(back()) // we want back camera
            .focusMode(
                firstAvailable(  // (optional) use the first focus mode which is supported by device
                    continuousFocusPicture(),
                    autoFocus(), // in case if continuous focus is not available on device, auto focus will be used
                    fixed()             // if even auto focus is not available - fixed focus mode will be used
                )
            )
            .logger(
                loggers(            // (optional) we want to log camera events in 2 places at once
                    logcat(), // ... in logcat
                    fileLogger(this)    // ... and to file
                )
            )
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PREVIEW_RESULT_CODE && resultCode == RESULT_OK) {
            data?.extras?.getStringArrayList(PREVIEW_RESULT)?.let {
                listImagePath.clear()
                listImagePath.addAll(it)
                visibilityImagesCount()
            }

        }
    }

    private fun maxSizeUpload(): Double {
        return (imageSize * MAX_BYTE).toDouble()
    }

    private fun freeSpaceOfMemory(): Long {
        val stat = StatFs(Environment.getRootDirectory().absolutePath)
        return stat.availableBlocksLong * stat.blockSizeLong
    }

    private fun takeImage() {
        disableTouch()
        if (listImagePath.size < limitSize) {
            val maxSize = maxSizeUpload()
            if (freeSpaceOfMemory() > listImagePath.size * maxSize + FREE_SPACE_NEED) {
                fotoapparat.takePicture().toBitmap().whenAvailable { bitmapPhoto ->
                    bitmapPhoto?.let { photo ->
                        var bitmap = photo.bitmap.rotate(-photo.rotationDegrees)
                        bitmap = CameraUtils.resizeBitmap(bitmap, maxSize)
                        try {
                            val photoFile = createFile()
                            val fos = FileOutputStream(photoFile)
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                            fos.flush()
                            fos.close()
                            listImagePath.add(photoFile.path)
                            showImagesCount(photoFile.path, listImagePath.size)
                        } catch (e: Exception) {
                            showAlert(R.string.error_system_error)
                        }
                        enableTouch()
                    }
                }
            } else {
                showAlert(R.string.alert_not_enough_space)
                enableTouch()
            }
        } else {
            enableTouch()
            showAlertMaxSize(R.string.max_value_upload, limitSize)
        }

    }

    private fun uploadImages() {
        fotoapparat.stop()
        if (cameraBuilder?.isUpload == true) {
            cameraBuilder?.upload(listImagePath, onStartUpload = {
                fProgressBar.visibility = View.VISIBLE
            }, onSuccess = { images ->
                listImagePath.clear()
                fProgressBar.visibility = View.GONE
                showAlert(R.string.upload_images_success)
                images?.let {
                    listImagesUpload.addAll(images)
                    hideImagesCount()
                }
                fotoapparat.start()
            }, onError = { message ->
                fProgressBar.visibility = View.GONE
                AlertDialog.Builder(this)
                    .setMessage(message)
                    .setPositiveButton(R.string.ok, null)
                    .show()
                fotoapparat.start()
            })
        }
    }
}