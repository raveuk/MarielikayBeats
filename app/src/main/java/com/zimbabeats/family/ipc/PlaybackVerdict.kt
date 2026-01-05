package com.zimbabeats.family.ipc

import android.os.Parcel
import android.os.Parcelable

/**
 * Result of content playback evaluation.
 */
data class PlaybackVerdict(
    val allowed: Boolean,
    val blockReason: Int,
    val blockMessage: String?,
    val guardianScore: Int,
    val verdictId: String?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        allowed = parcel.readByte() != 0.toByte(),
        blockReason = parcel.readInt(),
        blockMessage = parcel.readString(),
        guardianScore = parcel.readInt(),
        verdictId = parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (allowed) 1 else 0)
        parcel.writeInt(blockReason)
        parcel.writeString(blockMessage)
        parcel.writeInt(guardianScore)
        parcel.writeString(verdictId)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<PlaybackVerdict> {
        override fun createFromParcel(parcel: Parcel): PlaybackVerdict = PlaybackVerdict(parcel)
        override fun newArray(size: Int): Array<PlaybackVerdict?> = arrayOfNulls(size)

        // Block reason constants
        const val BLOCK_REASON_NONE = 0
        const val BLOCK_REASON_CONTENT_BLOCKED = 1
        const val BLOCK_REASON_SCREEN_TIME = 2
        const val BLOCK_REASON_BEDTIME = 3
        const val BLOCK_REASON_CHANNEL_BLOCKED = 4
        const val BLOCK_REASON_AGE_RESTRICTED = 5

        /**
         * Create an allowed verdict
         */
        fun allowed(guardianScore: Int = 1000, verdictId: String? = null) = PlaybackVerdict(
            allowed = true,
            blockReason = BLOCK_REASON_NONE,
            blockMessage = null,
            guardianScore = guardianScore,
            verdictId = verdictId
        )

        /**
         * Create a blocked verdict
         */
        fun blocked(reason: Int, message: String, verdictId: String? = null) = PlaybackVerdict(
            allowed = false,
            blockReason = reason,
            blockMessage = message,
            guardianScore = 0,
            verdictId = verdictId
        )
    }
}
