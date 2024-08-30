package expo.modules.musiclibrary.albums

import android.content.ContentUris
import android.content.Context
import android.database.Cursor.FIELD_TYPE_NULL
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore.Audio.Albums
import expo.modules.kotlin.Promise
import expo.modules.musiclibrary.ALBUM_PROJECTION
import expo.modules.musiclibrary.AlbumException
import expo.modules.musiclibrary.ERROR_UNABLE_TO_LOAD
import expo.modules.musiclibrary.ERROR_UNABLE_TO_LOAD_PERMISSION

internal open class GetAlbums(
    private val context: Context,
    private val promise: Promise
) {
    fun execute() {
        val albums = HashMap<String, Album>()

        try {
            context.contentResolver
                .query(
                    Albums.EXTERNAL_CONTENT_URI,
                    ALBUM_PROJECTION,
                    null,
                    null,
                    "${Albums.ALBUM} ASC",  
                )
                .use { albumsCursor ->
                    if (albumsCursor == null) {
                        throw AlbumException("Could not get albums. Query returns null")
                    }
                    val bucketIdIndex = albumsCursor.getColumnIndex(Albums._ID)
                    val bucketDisplayNameIndex = albumsCursor.getColumnIndex(Albums.ALBUM)
                    val bucketArtistIndex = albumsCursor.getColumnIndex(Albums.ARTIST)
                    val albumSongsIndex = albumsCursor.getColumnIndex(Albums.NUMBER_OF_SONGS)

                    while (albumsCursor.moveToNext()) {
                        val id = albumsCursor.getString(bucketIdIndex)

                        if (albumsCursor.getType(bucketDisplayNameIndex) == FIELD_TYPE_NULL) {
                            continue
                        }
                        val artworkUri: Uri = Uri.parse("content://media/external/audio/albumart")
                        val albumArtPath: Uri = ContentUris.withAppendedId(artworkUri, albumsCursor.getLong(bucketIdIndex))

                        val album = albums[id] ?: Album(
                            id = id,
                            title = albumsCursor.getString(bucketDisplayNameIndex),
                            artwork = albumArtPath.toString(),
                            artist = albumsCursor.getString(bucketArtistIndex),
                            albumSongs = albumsCursor.getInt(albumSongsIndex)
                        ).also {
                            albums[id] = it
                        }

                        album.count++
                    }

                    promise.resolve(albums.values.map { it.toBundle() })
                }
        } catch (e: SecurityException) {
            promise.reject(
                ERROR_UNABLE_TO_LOAD_PERMISSION,
                "Could not get albums: need READ_EXTERNAL_STORAGE permission.", e
            )
        } catch (e: RuntimeException) {
            promise.reject(ERROR_UNABLE_TO_LOAD, "Could not get albums.", e)
        }
    }

    private class Album(private val id: String, private val title: String, var count: Int = 0, private val artwork: String, private val artist: String, private val albumSongs: Int) {
        fun toBundle() = Bundle().apply {
            putString("id", id)
            putString("title", title)
            putString("artwork", artwork)
            putString("artist", artist)
            putParcelable("type", null)
            putInt("assetsCount", count)
            putInt("albumSongs", albumSongs)
        }
    }
}