package vn.supenient.camera

class CameraSetup {

    var language: String? = null

    companion object {
        @Volatile
        private var INSTANCE: CameraSetup? = null

        internal fun shared() = INSTANCE ?: synchronized(this) {
            INSTANCE ?: CameraSetup().also {
                INSTANCE = it
            }
        }

        fun Build(language: String? = null) {
            language?.let { shared().language = it }
        }
    }
}