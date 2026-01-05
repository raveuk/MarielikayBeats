package com.zimbabeats.family.ipc

import android.os.Parcel
import android.os.Parcelable

/**
 * Result of a parent unlock request.
 */
data class UnlockResult(
    val success: Boolean,
    val unlockType: Int,
    val additionalMinutes: Int,
    val expiresAt: Long
) : Parcelable {

    constructor(parcel: Parcel) : this(
        success = parcel.readByte() != 0.toByte(),
        unlockType = parcel.readInt(),
        additionalMinutes = parcel.readInt(),
        expiresAt = parcel.readLong()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeByte(if (success) 1 else 0)
        parcel.writeInt(unlockType)
        parcel.writeInt(additionalMinutes)
        parcel.writeLong(expiresAt)
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<UnlockResult> {
        override fun createFromParcel(parcel: Parcel): UnlockResult = UnlockResult(parcel)
        override fun newArray(size: Int): Array<UnlockResult?> = arrayOfNulls(size)

        // Unlock type constants
        const val UNLOCK_TYPE_PIN_VERIFIED = 0
        const val UNLOCK_TYPE_TEMPORARY_OVERRIDE = 1
        const val UNLOCK_TYPE_EXTEND_TIME = 2
        const val UNLOCK_TYPE_ACCESS_SETTINGS = 3
        const val UNLOCK_TYPE_BEDTIME_OVERRIDE = 4

        /**
         * Create a successful unlock result
         */
        fun success(unlockType: Int, additionalMinutes: Int = 0, expiresAt: Long = 0L) = UnlockResult(
            success = true,
            unlockType = unlockType,
            additionalMinutes = additionalMinutes,
            expiresAt = expiresAt
        )

        /**
         * Create a failed unlock result
         */
        fun failure() = UnlockResult(
            success = false,
            unlockType = -1,
            additionalMinutes = 0,
            expiresAt = 0L
        )
    }
}
