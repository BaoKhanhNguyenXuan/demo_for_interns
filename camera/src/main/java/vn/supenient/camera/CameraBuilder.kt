package vn.supenient.camera

import android.os.Parcelable

abstract class CameraBuilder<T : Parcelable>(
    val limitSize: Int,
    val imageSize: Int,
    val cameraUse: CameraType,
    val isUpload: Boolean) : Parcelable {
    abstract fun upload(images: ArrayList<String>,
                        onStartUpload:(() -> Unit),
                        onSuccess: ((List<T>?) -> Unit)?,
                        onError: ((String) -> Unit))
}