package com.tryden.spotifyclone.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.RequestManager
import com.tryden.spotifyclone.R
import com.tryden.spotifyclone.adapters.SwipeSongAdapter
import com.tryden.spotifyclone.data.entities.Song
import com.tryden.spotifyclone.databinding.ActivityMainBinding
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        subscribeToObservers()

        binding.vpSong.adapter = swipeSongAdapter


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
    }


}