package expo.modules.musiclibrary.genres

import android.content.Context
import android.database.Cursor.FIELD_TYPE_NULL
import android.os.Bundle
import android.provider.MediaStore.Audio.Genres
import expo.modules.kotlin.Promise
import expo.modules.musiclibrary.AlbumException
import expo.modules.musiclibrary.ERROR_UNABLE_TO_LOAD
import expo.modules.musiclibrary.ERROR_UNABLE_TO_LOAD_PERMISSION
import expo.modules.musiclibrary.GENRE_PROJECTION

internal open class GetGenres(
    private val context: Context,
    private val promise: Promise
) {
    fun execute() {
        val projection = GENRE_PROJECTION

        val genres = HashMap<String, Genre>()

        try {
            context.contentResolver
                .query(
                    Genres.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    "${Genres.NAME} ASC"
                )
                .use { genreCursor ->
                    if (genreCursor == null) {
                        throw AlbumException("Could not get genres. Query returns null")
                    }
                    val genreIdIndex = genreCursor.getColumnIndex(Genres._ID)
                    val genreDisplayNameIndex = genreCursor.getColumnIndex(Genres.NAME)

                    while (genreCursor.moveToNext()) {
                        val id = genreCursor.getString(genreIdIndex)

                        if (genreCursor.getType(genreDisplayNameIndex) == FIELD_TYPE_NULL) {
                            continue
                        }

                        val genre = genres[id] ?: Genre(
                            id = id,
                            title = genreCursor.getString(genreDisplayNameIndex),
                        ).also {
                            genres[id] = it
                        }

                        genre.count++
                    }

                    promise.resolve(genres.values.map { it.toBundle() })
                }
        } catch (e: SecurityException) {
            promise.reject(
                ERROR_UNABLE_TO_LOAD_PERMISSION,
                "Could not get genres: need READ_EXTERNAL_STORAGE permission.", e
            )
        } catch (e: RuntimeException) {
            promise.reject(ERROR_UNABLE_TO_LOAD, "Could not get genres.", e)
        }
    }

    private class Genre(private val id: String, private val title: String, var count: Int = 0) {
        fun toBundle() = Bundle().apply {
            putString("id", id)
            putString("title", title)
            putParcelable("type", null)
            putInt("assetCount", count)
        }
    }
}