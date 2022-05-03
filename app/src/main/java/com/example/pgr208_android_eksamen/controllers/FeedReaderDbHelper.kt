package com.example.pgr208_android_eksamen.controllers

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.pgr208_android_eksamen.models.ImageModel
import com.example.pgr208_android_eksamen.models.ResultModel

class FeedReaderDbHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private var nextImageId: Long = -1

    override fun onCreate(p0: SQLiteDatabase?) {
        val createSearchTable = (
                "CREATE TABLE $TABLE_SEARCH_IMAGE (" +
                        "$SEARCH_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$SEARCH_IMAGE BLOB)"
                )
        val createResultTable = (
                "CREATE TABLE $TABLE_RESULTS (" +
                        "$RESULT_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "$RESULT_URL TEXT, " +
                        "$SEARCH_ID INTEGER, " +
                        "FOREIGN KEY($SEARCH_ID) REFERENCES $TABLE_SEARCH_IMAGE($SEARCH_ID))")
        p0?.execSQL(createSearchTable)
        p0?.execSQL(createResultTable)
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_SEARCH_IMAGE")
        p0?.execSQL("DROP TABLE IF EXISTS $TABLE_RESULTS")
        onCreate(p0)
    }
    fun insertImageResult(result: ResultModel): Long {
        val p0 = this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(RESULT_URL, result.url)
        contentValues.put(SEARCH_ID, result.searchId)

        val success = p0.insert(TABLE_RESULTS, null, contentValues)
        p0.close()
        return success
    }

    fun insertImageSearch(image: ImageModel) : Long{
        val p0 =this.writableDatabase

        val contentValues = ContentValues()
        contentValues.put(SEARCH_IMAGE, image.image)

        val success =p0.insert(TABLE_SEARCH_IMAGE, null, contentValues)
        p0.close()
        nextImageId++
        return success
    }
    fun getNextSaveId(): Long {
        return nextImageId
    }

    fun deleteImage(savedImage: ImageModel) {
        val p0 = this.writableDatabase

        p0.delete(TABLE_RESULTS, "$SEARCH_ID=?", arrayOf("${savedImage.id}"))
        p0.delete(TABLE_SEARCH_IMAGE, "$SEARCH_ID=?", arrayOf("${savedImage.id}"))
        p0.close()
    }

    @SuppressLint("Range")
    fun getAllSavedImages(): List<ImageModel> {
        val result = arrayListOf<ImageModel>()
        val sql = "SELECT * FROM $TABLE_SEARCH_IMAGE"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(sql, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(sql)
            return listOf()
        }
        if (cursor.moveToFirst()) {
            do {
                result.add(
                    ImageModel(
                        cursor.getLong(cursor.getColumnIndex(SEARCH_ID)),
                        cursor.getBlob(cursor.getColumnIndex(SEARCH_IMAGE))
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    @SuppressLint("Range")
    fun getResultsForSavedImage(id: Long): List<ResultModel> {
        val result = arrayListOf<ResultModel>()
        val sql = "SELECT * FROM $TABLE_RESULTS WHERE $SEARCH_ID == $id"
        val db = this.readableDatabase
        val cursor: Cursor?

        try {
            cursor = db.rawQuery(sql, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(sql)
            return listOf()
        }
        if (cursor.moveToFirst()) {
            do {
                result.add(
                    ResultModel(
                        id = cursor.getLong(cursor.getColumnIndex(RESULT_ID)),
                        url = cursor.getString(cursor.getColumnIndex(RESULT_URL)),
                        searchId = id
                    )
                )
            } while (cursor.moveToNext())
        }
        cursor.close()
        return result
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "Imagedb"

        private const val TABLE_SEARCH_IMAGE = "Image_search"
        private const val SEARCH_ID = "ID_search"
        private const val SEARCH_IMAGE = "Image"

        private const val TABLE_RESULTS = "Result_saved"
        private const val RESULT_ID = "Result_ID"
        private const val RESULT_URL = "Result_URL"
    }
}