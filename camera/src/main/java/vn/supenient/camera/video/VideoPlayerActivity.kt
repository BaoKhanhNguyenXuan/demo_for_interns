package vn.supenient.camera.video

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import kotlinx.android.synthetic.main.activity_video_player.*
import kotlinx.android.synthetic.main.exo_playback_control_view.*
import vn.supenient.camera.BaseLanguageActivity
import vn.supenient.camera.R


class VideoPlayerActivity : BaseLanguageActivity() {

    companion object {
        private const val EXTRA_URI_VIDEO = "extra.uri.video"

        fun start(context: Context?, uri: Uri) {
            val intent = Intent(context, VideoPlayerActivity::class.java)
            intent.putExtra(EXTRA_URI_VIDEO, uri)
            context?.startActivity(intent)
        }
    }

    private var player: SimpleExoPlayer? = null
    private var playWhenReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    private var fullscreen = false

    private var uriVideo: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_player)
        setupView()
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun setupView() {
        uriVideo = intent.getParcelableExtra(EXTRA_URI_VIDEO)
        exo_fullscreen_icon.setOnClickListener {
            if (fullscreen) {
                exo_fullscreen_icon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_fullscreen_open
                    )
                )
                val params =
                    videoPlayer.layoutParams as FrameLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT
                videoPlayer.layoutParams = params
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                fullscreen = false
            } else {
                exo_fullscreen_icon.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.ic_fullscreen_close
                    )
                )
                val params =
                    videoPlayer.layoutParams as FrameLayout.LayoutParams
                params.width = ViewGroup.LayoutParams.MATCH_PARENT
                params.height = ViewGroup.LayoutParams.MATCH_PARENT
                videoPlayer.layoutParams = params
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                fullscreen = true
            }
        }

        btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initializePlayer() {
        player = ExoPlayerFactory.newSimpleInstance(this)
        videoPlayer.player = player
        val mediaSource =
            uriVideo?.let { buildMediaSource(it) }

        videoPlayer.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        player?.playWhenReady = playWhenReady
        player?.seekTo(currentWindow, playbackPosition)
        player?.prepare(mediaSource, false, false)
        player?.playWhenReady = true
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val dataSource = DefaultDataSourceFactory(this, "exoplayer-maker")
        return ProgressiveMediaSource.Factory(dataSource).createMediaSource(uri)
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) {
            initializePlayer()
        }
    }

    override fun onResume() {
        super.onResume()
        hideSystemUi()
        if ((Util.SDK_INT < 24 || player == null)) {
            initializePlayer()
        }
    }

    private fun hideSystemUi() {
        videoPlayer.systemUiVisibility = (View.SYSTEM_UI_FLAG_LOW_PROFILE
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onPause() {
        super.onPause()
        if (Util.SDK_INT < 24) {
            releasePlayer()
        }
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        player?.let {
            playWhenReady = it.playWhenReady
            playbackPosition = it.currentPosition
            currentWindow = it.currentWindowIndex
            player?.release()
            player = null
        }
    }
}