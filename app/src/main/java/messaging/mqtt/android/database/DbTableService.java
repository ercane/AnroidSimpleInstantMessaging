package messaging.mqtt.android.database;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbTableService {

    public static SQLiteOpenHelper sqoh;
    public static String TAG = "DATABASE";


    public static void createChatTable() {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + DbConstants.CHAT_TABLE_NAME + "("
                    + DbConstants.CHAT_LAST_ACTION_TIME + " NUMERIC,"
                    + DbConstants.CHAT_ID + " INTEGER PRIMARY KEY,"
                    + DbConstants.CHAT_NAME + " TEXT,"
                    + DbConstants.CHAT_TOPIC + " TEXT,"
                    + DbConstants.CHAT_PBK_SENT + " INT,"
                    + DbConstants.CHAT_PBK + " TEXT,"
                    + DbConstants.CHAT_PRK + " TEXT,"
                    + DbConstants.CHAT_OPBK + " TEXT,"
                    + DbConstants.CHAT_MSGK + " TEXT,"
                    + DbConstants.CHAT_STATUS + " NUMERIC,"
                    + DbConstants.CHAT_UNREAD_MESSAGE + " NUMERIC)";
            db.execSQL(CREATE_MESSAGE_TABLE);
            db.close();
            Log.e(TAG, "Chat table created. ");
        } catch (Exception e) {
            Log.e(TAG, "Chat table cannot be created. Exception: " + e.getMessage());
        }
    }

    public static void createMessageTable() {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String CREATE_MESSAGE_TABLE = "CREATE TABLE IF NOT EXISTS " + DbConstants.MESSAGE_TABLE_NAME + "("
                    + DbConstants.MESSAGE_ID + " INTEGER PRIMARY KEY,"
                    + DbConstants.MESSAGE_CHAT_ID + " NUMERIC,"
                    + DbConstants.MESSAGE_STATUS + " NUMERIC,"
                    + DbConstants.MESSAGE_TYPE + " NUMERIC,"
                    + DbConstants.MESSAGE_SENDING_TIME + " NUMERIC,"
                    + DbConstants.MESSAGE_CONTENT + " TEXT," +
                    " FOREIGN KEY (" + DbConstants.MESSAGE_CHAT_ID + ") REFERENCES " + DbConstants.CHAT_TABLE_NAME + "(" + DbConstants.CHAT_ID + "));";
            db.execSQL(CREATE_MESSAGE_TABLE);
            db.close();
            Log.e(TAG, "Message table created. ");
        } catch (Exception e) {
            Log.e(TAG, "Message table cannot be created. Exception: " + e.getMessage());
        }
    }

    public static void dropTable(String tableName) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String DROP_TABLE = "DROP TABLE IF EXISTS " + tableName;
            db.execSQL(DROP_TABLE);
            db.close();
            Log.e(TAG, "Table '" + tableName + "' dropped");
        } catch (SQLException e) {
            Log.e(TAG, "Table '" + tableName + "'cannot be dropped. Exception: " + e.getMessage());
        }
    }

}
