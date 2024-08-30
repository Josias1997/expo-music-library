package expo.modules.musiclibrary

import android.provider.MediaStore

enum class MediaType(val apiName: String, val mediaColumn: Int?) {
    AUDIO("audio", MediaStore.Files.FileColumns.MEDIA_TYPE_AUDIO),
    ALL("all", null);
  
    companion object {
      // all constants have keys equal to the values
      fun getConstants() = entries.associate { Pair(it.apiName, it.apiName) }
  
      fun fromApiName(constantName: String) = entries.find { it.apiName == constantName }
    }
  }
  
  enum class SortBy(val keyName: String, val mediaColumnName: String) {
    DEFAULT("default", MediaStore.Images.Media._ID),
    CREATION_TIME("creationTime", MediaStore.Images.Media.DATE_TAKEN),
    MODIFICATION_TIME("modificationTime", MediaStore.Images.Media.DATE_MODIFIED),
    MEDIA_TYPE("mediaType", MediaStore.Files.FileColumns.MEDIA_TYPE),
    WIDTH("width", MediaStore.MediaColumns.WIDTH),
    HEIGHT("height", MediaStore.MediaColumns.HEIGHT),
    DURATION("duration", MediaStore.Video.VideoColumns.DURATION);
  
    companion object {
      // all constants have keys equal to the values
      fun getConstants() = entries.associate { Pair(it.keyName, it.keyName) }
  
      fun fromKeyName(keyName: String) = entries.find { it.keyName == keyName }
    }
  }