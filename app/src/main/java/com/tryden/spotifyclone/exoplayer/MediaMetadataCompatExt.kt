package com.tryden.spotifyclone.exoplayer

import android.support.v4.media.MediaMetadataCompat
import com.tryden.spotifyclone.data.entities.Song

// Converts the MediaMetadataCompat object to a Song object
fun MediaMetadataCompat.toSong() : Song? {
    return description?.let {
        Song(
            it.mediaId ?: "",
                    it.title.toString(),
                    it.subtitle.toString(),
                    it.mediaUri.toString(),
                    it.iconUri.toString()
        )
    }

}