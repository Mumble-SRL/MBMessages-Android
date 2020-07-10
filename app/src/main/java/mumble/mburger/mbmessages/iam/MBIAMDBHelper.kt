package mumble.mburger.mbmessages.iam

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import mumble.mburger.mbmessages.MBMessagesParser
import mumble.mburger.mbmessages.iam.MBIAMData.MBMessage
import org.json.JSONObject

val DATABASE_VERSION = 1
val DATABASE_NAME = "iam.db"

class MBIAMDBHelper(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val TABLE_MESSAGES = "MESSAGES"
    val COLUMN_MESSAGE_ID = "id"
    val COLUMN_MESSAGE_CONTENT = "content"

    val CREATE_CAMPAIGNS_TABLE = "CREATE TABLE " + TABLE_MESSAGES + " ( " +
            COLUMN_MESSAGE_ID + " LONG PRIMARY KEY, " +
            COLUMN_MESSAGE_CONTENT + " TEXT)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_CAMPAIGNS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
    }

    fun dataIsAlreadyIn(db: SQLiteDatabase, id: Long): Boolean {
        val query = "Select * from $TABLE_MESSAGES where $COLUMN_MESSAGE_ID = $id"
        val cursor: Cursor = db.rawQuery(query, null)
        if (cursor.count <= 0) {
            cursor.close()
            return false
        }

        cursor.close()
        return true
    }

    fun addACampaign(id: Long, campaign_content: JSONObject) {
        val db = this.writableDatabase
        val values = ContentValues()
        if (dataIsAlreadyIn(db, id)) {
            updateACampaign(db, id, campaign_content)
        } else {
            values.put(COLUMN_MESSAGE_ID, id)
            values.put(COLUMN_MESSAGE_CONTENT, DatabaseUtils.sqlEscapeString(campaign_content.toString()))
            db.insert(TABLE_MESSAGES, null, values)
        }

        db.close()
    }

    fun getCampaigns(): ArrayList<MBMessage> {
        val campaigns = ArrayList<MBMessage>()
        val query = "SELECT * FROM $TABLE_MESSAGES"
        val db = this.writableDatabase
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToFirst()) {
            do {
                campaigns.add(getSingleCampaign(cursor))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return campaigns
    }

    fun getSingleCampaign(cursor: Cursor): MBMessage {
        val jContent = cursor.getString(1)
        return MBMessagesParser.parseMessage(context, JSONObject(jContent))
    }

    fun updateACampaign(db: SQLiteDatabase, id: Long, campaign_content: JSONObject) {
        val values = ContentValues()
        values.put(COLUMN_MESSAGE_CONTENT, DatabaseUtils.sqlEscapeString(campaign_content.toString()))
        db.update(TABLE_MESSAGES, values, "$COLUMN_MESSAGE_ID = $id", null)
    }

    fun deleteCampaign(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_MESSAGES, "$COLUMN_MESSAGE_ID = $id", null)
        db.close()
    }
}