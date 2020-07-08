package mumble.mburger.mbmessages.iam

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import mumble.mburger.mbmessages.MBMessagesParser
import mumble.mburger.mbmessages.iam.MBIAMData.Campaign
import org.json.JSONObject

val DATABASE_VERSION = 1
val DATABASE_NAME = "iam.db"

class MBIAMDBHelper(var context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    val TABLE_CAMPAIGNS = "CAMPAIGNS"
    val COLUMN_CAMPAIGN_ID = "id"
    val COLUMN_CAMPAIGN_CONTENT = "content"

    val CREATE_CAMPAIGNS_TABLE = "CREATE TABLE " + TABLE_CAMPAIGNS + " ( " +
            COLUMN_CAMPAIGN_ID + " LONG PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_CAMPAIGN_CONTENT + " TEXT)"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CREATE_CAMPAIGNS_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_CAMPAIGNS")
    }

    fun dataIsAlreadyIn(db: SQLiteDatabase, id: Long): Boolean {
        val query = "Select * from $TABLE_CAMPAIGNS where $COLUMN_CAMPAIGN_ID = $id"
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
            values.put(COLUMN_CAMPAIGN_ID, id)
            values.put(COLUMN_CAMPAIGN_CONTENT, DatabaseUtils.sqlEscapeString(campaign_content.toString()))
            db.insert(TABLE_CAMPAIGNS, null, values)
        }

        db.close()
    }

    fun getCampaigns(): ArrayList<Campaign> {
        val campaigns = ArrayList<Campaign>()
        val query = "SELECT * FROM $TABLE_CAMPAIGNS"
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

    fun getSingleCampaign(cursor: Cursor): Campaign {
        val jContent = cursor.getString(1)
        return MBMessagesParser.parseCampaign(context, JSONObject(jContent))
    }

    fun updateACampaign(db: SQLiteDatabase, id: Long, campaign_content: JSONObject) {
        val values = ContentValues()
        values.put(COLUMN_CAMPAIGN_CONTENT, DatabaseUtils.sqlEscapeString(campaign_content.toString()))
        db.update(TABLE_CAMPAIGNS, values, "$COLUMN_CAMPAIGN_ID = $id", null)
    }

    fun deleteCampaign(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_CAMPAIGNS, "$COLUMN_CAMPAIGN_ID = $id", null)
        db.close()
    }
}