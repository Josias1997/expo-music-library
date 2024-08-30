package expo.modules.musiclibrary.folders

import android.content.Context
import android.database.Cursor.FIELD_TYPE_NULL
import android.os.Bundle
import android.provider.MediaStore
import expo.modules.kotlin.Promise
import expo.modules.musiclibrary.ERROR_UNABLE_TO_LOAD
import expo.modules.musiclibrary.ERROR_UNABLE_TO_LOAD_PERMISSION
import expo.modules.musiclibrary.FolderException

internal open class GetFolders(
    private val context: Context,
    private val promise: Promise
) {
    fun execute() {
        val projection = arrayOf(
            MediaStore.Audio.Media.BUCKET_ID,
            MediaStore.Audio.Media.BUCKET_DISPLAY_NAME
        )

        val folders = HashMap<String, Folder>()

        try {
            context.contentResolver
                .query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    null,
                    null,
                    "${MediaStore.Audio.Media.BUCKET_DISPLAY_NAME} ASC",  
                )
                .use { foldersCursor ->
                    if (foldersCursor == null) {
                        throw FolderException("Could not get folders. Query returns null")
                    }
                    val bucketIdIndex = foldersCursor.getColumnIndex(MediaStore.Audio.Media.BUCKET_ID)
                    val bucketDisplayNameIndex = foldersCursor.getColumnIndex(MediaStore.Audio.Media.BUCKET_DISPLAY_NAME)

                    while (foldersCursor.moveToNext()) {
                        val id = foldersCursor.getString(bucketIdIndex)

                        if (foldersCursor.getType(bucketDisplayNameIndex) == FIELD_TYPE_NULL) {
                            continue
                        }

                        Folder(
                            id = id,
                            title = foldersCursor.getString(bucketDisplayNameIndex),
                        ).also {
                            folders[id] = it
                        }
                    }
                    promise.resolve(folders.values.map { it.toBundle() })
                }
        } catch (e: SecurityException) {
            promise.reject(
                ERROR_UNABLE_TO_LOAD_PERMISSION,
                "Could not get folders: need READ_EXTERNAL_STORAGE permission.", e
            )
        } catch (e: RuntimeException) {
            promise.reject(ERROR_UNABLE_TO_LOAD, "Could not get folders.", e)
        }
    }

    private class Folder(private val id: String, private val title: String) {
        fun toBundle() = Bundle().apply {
            putString("id", id)
            putString("title", title)
        }
    }
}