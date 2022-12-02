package com.tryden.spotifyclone.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.session.PlaybackStateCompat
import androidx.activity.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.RequestManager
import com.google.android.material.snackbar.Snackbar
import com.tryden.spotifyclone.R
import com.tryden.spotifyclone.adapters.SwipeSongAdapter
import com.tryden.spotifyclone.data.entities.Song
import com.tryden.spotifyclone.databinding.ActivityMainBinding
import com.tryden.spotifyclone.exoplayer.isPlaying
import com.tryden.spotifyclone.exoplayer.toSong
import com.tryden.spotifyclone.other.Status
import com.tryden.spotifyclone.other.Status.*
import com.tryden.spotifyclone.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint // If we inject anything into Android components, we need this annotation
class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels() // bind it to the lifecycle of this activity

    @Inject
    lateinit var swipeSongAdapter: SwipeSongAdapter

    @Inject
    lateinit var glide: RequestManager

    private var curPlayingSong: Song? = null

    private var playbackState: PlaybackStateCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        subscribeToObservers()

        binding.vpSong.adapter = swipeSongAdapter

        registerCallback()

        binding.ivPlayPause.setOnClickListener {
            curPlayingSong?.let {
                mainViewModel.playOrToggleSong(it, true) // true here because we togglin'
            }
        }
    }

    // Swipes view pager to current song
    private fun switchViewPagerToCurrentSong(song: Song) {
        val newItemIndex = swipeSongAdapter.songs.indexOf(song)
        if (newItemIndex != -1) { // if == -1, then song doesn't exist
            binding.vpSong.currentItem = newItemIndex // ViewPager current song is now newItemIndex from adapter
            curPlayingSong = song
        }
    }

    // Subscribe to observers
    private fun subscribeToObservers() {
        mainViewModel.mediaItems.observe(this) {
            it?.let { result ->
                when (result.status) {
                    SUCCESS -> {
                        result.data?.let { songs ->
                            swipeSongAdapter.songs = songs
                            if (songs.isNotEmpty()) {
                                glide.load((curPlayingSong ?: songs[0]).imageUrl).into(binding.ivCurSongImage)
                            }
                            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
                        }
                    }
                    ERROR -> Unit // Do nothing, return Unit
                    LOADING -> Unit // Do nothing, return Unit
                }
            }
        }

        mainViewModel.curPlayingSong.observe(this) {
            if (it == null) return@observe

            curPlayingSong = it.toSong() // Convert MediaMetadataCompat to Song object
            glide.load(curPlayingSong?.imageUrl).into(binding.ivCurSongImage)
            switchViewPagerToCurrentSong(curPlayingSong ?: return@observe)
        }

        // Anytime the playback state changes:
        // song is paused, played, next/previous, etc.
        mainViewModel.playbackState.observe(this) {
            playbackState = it
            binding.ivPlayPause.setImageResource(
                if (playbackState?.isPlaying == true) R.drawable.ic_pause else R.drawable.ic_play
            )
        }

        mainViewModel.isConnected.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    ERROR -> Snackbar.make(
                            binding.rootLayout,
                        result.message ?: "An unknown error occurred.",
                            Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit // do nothing, return Unit
                }
            }
        }

        mainViewModel.networkError.observe(this) {
            it?.getContentIfNotHandled()?.let { result ->
                when (result.status) {
                    ERROR -> Snackbar.make(
                        binding.rootLayout,
                        result.message ?: "An unknown network error occurred.",
                        Snackbar.LENGTH_LONG
                    ).show()
                    else -> Unit // do nothing, return Unit
                }
            }
        }
    }

    private fun registerCallback() {
        binding.vpSong.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                if (playbackState?.isPlaying == true) {
                    mainViewModel.playOrToggleSong(swipeSongAdapter.songs[position])
                } else {
                    curPlayingSong = swipeSongAdapter.songs[position]
                }
            }
        })
    }


}