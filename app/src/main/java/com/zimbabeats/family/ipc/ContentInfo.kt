package com.zimbabeats.family.ipc

import android.os.Parcel
import android.os.Parcelable

/**
 * Content metadata passed to the parental control service for evaluation.
 * Supports both video and music content.
 */
data class ContentInfo(
    val contentId: String,
    val title: String,
    val channelId: String,
    val channelName: String,
    val description: String?,
    val durationSeconds: Long,
    val viewCount: Long,
    val category: String?,
    val contentType: Int = CONTENT_TYPE_VIDEO,
    val isExplicit: Boolean = false,
    val artistName: String? = null,
    val albumName: String? = null
) : Parcelable {

    constructor(parcel: Parcel) : this(
        contentId = parcel.readString() ?: "",
        title = parcel.readString() ?: "",
        channelId = parcel.readString() ?: "",
        channelName = parcel.readString() ?: "",
        description = parcel.readString(),
        durationSeconds = parcel.readLong(),
        viewCount = parcel.readLong(),
        category = parcel.readString(),
        contentType = parcel.readInt(),
        isExplicit = parcel.readByte() != 0.toByte(),
        artistName = parcel.readString(),
        albumName = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(contentId)
        parcel.writeString(title)
        parcel.writeString(channelId)
        parcel.writeString(channelName)
        parcel.writeString(description)
        parcel.writeLong(durationSeconds)
        parcel.writeLong(viewCount)
        parcel.writeString(category)
        parcel.writeInt(contentType)
        parcel.writeByte(if (isExplicit) 1 else 0)
        parcel.writeString(artistName)
        parcel.writeString(albumName)
    }

    override fun describeContents(): Int = 0

    val isVideo: Boolean get() = contentType == CONTENT_TYPE_VIDEO
    val isMusic: Boolean get() = contentType == CONTENT_TYPE_MUSIC

    companion object CREATOR : Parcelable.Creator<ContentInfo> {
        override fun createFromParcel(parcel: Parcel): ContentInfo = ContentInfo(parcel)
        override fun newArray(size: Int): Array<ContentInfo?> = arrayOfNulls(size)

        const val CONTENT_TYPE_VIDEO = 0
        const val CONTENT_TYPE_MUSIC = 1
    }
}
