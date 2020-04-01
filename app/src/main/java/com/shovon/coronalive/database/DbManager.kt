package com.shovon.coronalive.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteQueryBuilder

class DbManager(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "corona_live.db"
        const val TABLE_NAME = "contacts"
        const val ID = "id"
        const val NAME = "name"
        const val PHONE = "phone"
        const val INDEX_NAME = "idx_contacts_number"
        const val DB_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {

        db!!.execSQL(
            "CREATE TABLE IF NOT EXISTS $TABLE_NAME (" +
                    "$ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$NAME TEXT," +
                    "$PHONE TEXT)"
        )

        db.execSQL(
            "CREATE UNIQUE INDEX $INDEX_NAME ON $TABLE_NAME ($PHONE)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
    }

    fun insert(values: ContentValues): Long {
        val sqlDB = this.writableDatabase
        return sqlDB!!.insert(TABLE_NAME, null, values)
    }

    fun query(
        projection: Array<String>,
        selection: String,
        selectionArgs: Array<String>,
        sorOrder: String
    ): Cursor {
        val sqlDB = this.writableDatabase
        val qb = SQLiteQueryBuilder()
        qb.tables = TABLE_NAME
        return qb.query(sqlDB, projection, selection, selectionArgs, null, null, sorOrder)
    }

    fun update(values: ContentValues, selection: String, selectionArgs: Array<String>): Int {
        val sqlDB = this.writableDatabase
        return sqlDB!!.update(TABLE_NAME, values, selection, selectionArgs)
    }

    fun delete(selection: String, selectionArgs: Array<String>): Int {
        val sqlDB = this.writableDatabase
        return sqlDB!!.delete(TABLE_NAME, selection, selectionArgs)
    }

    fun deleteContact(id:Int){
        val db = this.writableDatabase
        return db!!.execSQL("DELETE FROM $TABLE_NAME WHERE $ID = $id")
    }

    val showData: Cursor
        get() {
            val db = this.writableDatabase
            return db.rawQuery("SELECT * FROM $TABLE_NAME ORDER BY $NAME ASC", null)
        }
}