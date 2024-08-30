package expo.modules.musiclibrary.folders

import android.content.Context
import android.provider.MediaStore
import expo.modules.kotlin.Promise
import expo.modules.musiclibrary.ASSET_PROJECTION
import expo.modules.musiclibrary.AssetQueryException
import expo.modules.musiclibrary.ERROR_NO_PERMISSIONS
import expo.modules.musiclibrary.ERROR_UNABLE_TO_LOAD
import expo.modules.musiclibrary.ERROR_UNABLE_TO_LOAD_PERMISSION
import expo.modules.musiclibrary.assets.fillAssetBundle
import expo.modules.musiclibrary.models.Asset
import java.io.IOException

internal class GetFolderAssets(
        private val context: Context,
        private val folderId: String,
        private val promise: Promise
) {
    fun execute() {
        val contentResolver = context.contentResolver
        val selection = "${MediaStore.Audio.Media.BUCKET_ID} = ?"
        val selectionArgs = arrayOf(folderId)
        val sortBy = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val assets = HashMap<String, Asset>()

        try {
            contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    ASSET_PROJECTION,
                    selection,
                    selectionArgs,
                    sortBy
            ).use { assetsCursor ->
                if (assetsCursor == null) {
                    throw AssetQueryException()
                }
                fillAssetBundle(assetsCursor, assets)
                promise.resolve(assets.values.map { it.toBundle() })
            }
        } catch (e: SecurityException) {
            promise.reject(
                    ERROR_UNABLE_TO_LOAD_PERMISSION,
                    "Could not get asset: need READ_EXTERNAL_STORAGE permission.", e
            )
        } catch (e: IOException) {
            promise.reject(ERROR_UNABLE_TO_LOAD, "Could not read file", e)
        } catch (e: IllegalArgumentException) {
            promise.reject(ERROR_UNABLE_TO_LOAD, e.message ?: "Invalid MediaType", e)
        } catch (e: UnsupportedOperationException) {
            e.printStackTrace()
            promise.reject(ERROR_NO_PERMISSIONS, e.message, e)
        }
    }
}