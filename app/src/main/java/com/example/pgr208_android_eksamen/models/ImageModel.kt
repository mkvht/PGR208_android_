package com.example.pgr208_android_eksamen.models

import android.os.Parcel
import android.os.Parcelable

data class ImageModel (
    var id: Long? = null,
    var image: ByteArray
): Parcelable {
    override fun describeContents(): Int {
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ImageModel

        if (id != other.id) return false
        if (!image.contentEquals(other.image)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id?.hashCode() ?: 0
        result = 31 * result + image.contentHashCode()
        return result
    }

    override fun toString(): String {
        return super.toString()
    }

    override fun writeToParcel(out: Parcel, p1: Int) {
        id?.let { out.writeLong(it) } ?: run { out.writeLong(-1) }
        out.writeByteArray(image)
    }

    companion object CREATOR: Parcelable.Creator<ImageModel>{
        override fun createFromParcel(input: Parcel): ImageModel {
            var id: Long? = input.readLong()
            if(id == (-1).toLong()) id = null

            val image = byteArrayOf()
            input.readByteArray(image)
            return ImageModel(id, image)
        }

        override fun newArray(p0: Int): Array<ImageModel?> {
            return arrayOfNulls(p0)
        }
    }
}