package messaging.mqtt.android.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

import messaging.mqtt.android.common.model.ConversationInfo;
import messaging.mqtt.android.common.ref.ConversationMessageStatus;


public class DbEntryService {

    public static SQLiteOpenHelper sqoh;
    public static String TAG = "DbEntryService";


    /*Save operations
    *******************************
    */
    public static Long saveChat(ConversationInfo ci) {

        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            int count = -1;

            String select = "select count(*) from " + DbConstants.CHAT_TABLE_NAME +
                    " where " + DbConstants.CHAT_NAME + " = '" + ci.getRoomName() + "' ";

            Cursor mCount = db.rawQuery(select, null);
            mCount.moveToFirst();
            count = mCount.getInt(0);
            mCount.close();

            ContentValues values = new ContentValues();

            if (count == 0) {
                values.put(DbConstants.CHAT_UNREAD_MESSAGE, 0);
                values.put(DbConstants.CHAT_NAME, ci.getRoomName());
                values.put(DbConstants.CHAT_PBK_SENT, ci.getIsSent());
                values.put(DbConstants.CHAT_TOPIC, ci.getRoomTopic());
                values.put(DbConstants.CHAT_STATUS, ci.getStatus().getCode());
            }

            Long id = db.insert(DbConstants.CHAT_TABLE_NAME, null, values);
            db.close(); //Database Bağlantısını kapattık*/
            Log.e(TAG, "Chat saved to database. Date: " + ci.getRoomName());
            return id;
        } catch (Exception e) {
            Log.e(TAG, "Chat cannot be saved to database. Exception: " + e.getMessage());
            return -1l;
        }
    }

    public static Long saveMessage(Long chatId, Integer type, String content, Long sendingTime, Integer status) {

        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DbConstants.MESSAGE_CHAT_ID, chatId);
            values.put(DbConstants.MESSAGE_CONTENT, content);
            values.put(DbConstants.MESSAGE_TYPE, type);
            values.put(DbConstants.MESSAGE_STATUS, status);
            values.put(DbConstants.MESSAGE_SENDING_TIME, sendingTime);


            long insert = db.insert(DbConstants.MESSAGE_TABLE_NAME, null, values);
            db.close(); //Database Bağlantısını kapattık*/
            Log.e(TAG, "Message saved to database. Date: " + insert);
            return insert;
        } catch (Exception e) {
            Log.e(TAG, "Message cannot be saved to database. Exception: " + e.getMessage());
            return -1l;
        }
    }

    /*
    Get operations
    **************************************************************
     */

    public static ArrayList<HashMap<String, String>> getAllChats() {
        SQLiteDatabase db = sqoh.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DbConstants.CHAT_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<HashMap<String, String>> contactList = new ArrayList<HashMap<String, String>>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }

                contactList.add(map);
            } while (cursor.moveToNext());
        }
        db.close();

        return contactList;
    }

    public static HashMap<String, String> getChatByTopic(String topic) {
        SQLiteDatabase db = sqoh.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DbConstants.CHAT_TABLE_NAME + " WHERE (" +
                DbConstants.CHAT_TOPIC + " = '" + topic + "' )";
        Cursor cursor = db.rawQuery(selectQuery, null);
        HashMap<String, String> chat = new HashMap<String, String>();

        if (cursor.moveToFirst()) {
            do {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    chat.put(cursor.getColumnName(i), cursor.getString(i));
                }
            } while (cursor.moveToNext());
        }
        db.close();

        return chat;
    }

    public static HashMap<String, String> getChatById(Long id) {
        SQLiteDatabase db = sqoh.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DbConstants.CHAT_TABLE_NAME + " WHERE (" +
                DbConstants.CHAT_ID + " = " + id + " )";
        Cursor cursor = db.rawQuery(selectQuery, null);
        HashMap<String, String> chat = new HashMap<String, String>();

        if (cursor.moveToFirst()) {
            do {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    chat.put(cursor.getColumnName(i), cursor.getString(i));
                }
            } while (cursor.moveToNext());
        }
        db.close();

        return chat;
    }

    public static HashMap<String, String> getMessageById(Long id) {
        SQLiteDatabase db = sqoh.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DbConstants.MESSAGE_TABLE_NAME + " WHERE (" +
                DbConstants.MESSAGE_ID + " =" + id + " )";
        Cursor cursor = db.rawQuery(selectQuery, null);
        HashMap<String, String> chat = new HashMap<String, String>();

        if (cursor.moveToFirst()) {
            do {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    chat.put(cursor.getColumnName(i), cursor.getString(i));
                }
            } while (cursor.moveToNext());
        }
        db.close();

        return chat;
    }

    public static ArrayList<HashMap<String, String>> getAllMessagesByChat(Long chatId, Integer size, Long time) {
        SQLiteDatabase db = sqoh.getReadableDatabase();
        long today = System.currentTimeMillis();
        long hour = 1000 * 60 * 60;
        long day = 24 * hour;
        long month = 30 * day;
        long before3month = today - (3 * month);
        String selectQuery = "SELECT * FROM " + DbConstants.MESSAGE_TABLE_NAME + " WHERE " +
                DbConstants.MESSAGE_CHAT_ID + " = '" + chatId + "' AND " +
                DbConstants.MESSAGE_SENDING_TIME + " > " + before3month + " AND " +
                DbConstants.MESSAGE_SENDING_TIME + " < " + time +
                " order by " + DbConstants.MESSAGE_SENDING_TIME + " desc" +
                " limit " + size;
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<HashMap<String, String>> messageList = new ArrayList<HashMap<String, String>>();
        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }

                messageList.add(map);
            } while (cursor.moveToNext());
        }
        db.close();

        return messageList;
    }

    /*Remove operations
    *******************************************************************
     */

    public static void removeChat(Long id) {
        try {
            removeMessages(id);
            SQLiteDatabase db = sqoh.getWritableDatabase();
            db.delete(DbConstants.CHAT_TABLE_NAME, DbConstants.CHAT_ID + " = '" + id + "' ", null);
            db.close();
            Log.e(TAG, "Chat deleted. ID: ");
        } catch (Exception e) {
            Log.e(TAG, "Chat cannot be deleted. ID: ");
        }
    }

    public static void removeMessages(Long chatId) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            db.delete(DbConstants.MESSAGE_TABLE_NAME, DbConstants.MESSAGE_CHAT_ID + " = '" + chatId + "' ", null);
            db.close();
            Log.e(TAG, "Message deleted. ID: " + chatId);
        } catch (Exception e) {
            Log.e(TAG, "Message cannot be deleted. ID: " + chatId);
        }
    }

    public static void removeMessage(Long id) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            db.delete(DbConstants.MESSAGE_TABLE_NAME, DbConstants.MESSAGE_ID + " = " + id + " ", null);
            db.close();
            Log.e(TAG, "Message deleted. ID: " + id);
        } catch (Exception e) {
            Log.e(TAG, "Message cannot be deleted. ID: " + id);
        }
    }


    /*
    Update Operations
    *********************************************************************
     */


    public static boolean updateMessageStatus(Long id, Integer code) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String where = DbConstants.MESSAGE_ID + " = " + id;

            ContentValues values = new ContentValues();
            values.put(DbConstants.MESSAGE_STATUS, code);

            db.update(DbConstants.MESSAGE_TABLE_NAME, values, where, null);
            db.close();
            Log.e(TAG, DbConstants.MESSAGE_TABLE_NAME + " status updated. ID: " + id + " STATUS:" + code);
            return true;
        } catch (Exception e) {
            Log.e(TAG, DbConstants.MESSAGE_TABLE_NAME + " status cannot be updated. ID: " + id + " STATUS:" + code);
            return false;
        }
    }

    public static boolean updateMessagesToReceive(Long id) {
        try {
            HashMap<String, String> chat = getChatById(id);
            String recipientMail = chat.get(DbConstants.CHAT_ID);

            SQLiteDatabase db = sqoh.getWritableDatabase();

            String where = "(" + DbConstants.MESSAGE_CHAT_ID + " = '" + recipientMail + "' AND " +
                    DbConstants.MESSAGE_TYPE + " = " + 1 + " AND " +
                    DbConstants.MESSAGE_STATUS + " = " + ConversationMessageStatus.POST.getCode() + ")";


            ContentValues values = new ContentValues();
            values.put(DbConstants.MESSAGE_STATUS, ConversationMessageStatus.RECEIVED.getCode());

            int update = db.update(DbConstants.MESSAGE_TABLE_NAME, values, where, null);
            db.close();
            Log.e(TAG, DbConstants.MESSAGE_TABLE_NAME + "all status set to receive. recipientMail: " + recipientMail);
            return true;
        } catch (Exception e) {
            Log.e(TAG, DbConstants.MESSAGE_TABLE_NAME + " status cannot be set to received updated. ");
            return false;
        }
    }

    public static boolean updateSentMessagesToRead(Long id) {
        try {
            HashMap<String, String> chat = getChatById(id);
            String recipientMail = chat.get(DbConstants.CHAT_ID);

            SQLiteDatabase db = sqoh.getWritableDatabase();

            String where = "(" + DbConstants.MESSAGE_CHAT_ID + " = '" + recipientMail + "' AND " +
                    DbConstants.MESSAGE_TYPE + " = " + 1 + " AND " +
                    "(" +
                    DbConstants.MESSAGE_STATUS + " = " + ConversationMessageStatus.RECEIVED.getCode() + " OR " +
                    DbConstants.MESSAGE_STATUS + " = " + ConversationMessageStatus.POST.getCode() +
                    "))";

            ContentValues values = new ContentValues();
            values.put(DbConstants.MESSAGE_STATUS, ConversationMessageStatus.READ.getCode());

            db.update(DbConstants.MESSAGE_TABLE_NAME, values, where, null);
            db.close();
            Log.e(TAG, DbConstants.MESSAGE_TABLE_NAME + "all status set to receive. recipientMail: " + recipientMail);
            return true;
        } catch (Exception e) {
            Log.e(TAG, DbConstants.MESSAGE_TABLE_NAME + " status cannot be set to received updated. ");
            return false;
        }
    }

    public static boolean updateMessagesToRead(Long date) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();

            String where = "(" + DbConstants.MESSAGE_SENDING_TIME + " >= " + date + " AND " +
                    DbConstants.MESSAGE_TYPE + " = " + 0 + ")";

            ContentValues values = new ContentValues();
            values.put(DbConstants.MESSAGE_STATUS, ConversationMessageStatus.READ.getCode());

            int update = db.update(DbConstants.MESSAGE_TABLE_NAME, values, where, null);
            db.close();
            Log.e(TAG, DbConstants.MESSAGE_TABLE_NAME + "all status set to receive. Number: " + update);
            return true;
        } catch (Exception e) {
            Log.e(TAG, DbConstants.MESSAGE_TABLE_NAME + " status cannot be set to received updated. ");
            return false;
        }
    }

    public static boolean updateChatUnreadNumber(Long id, Integer unread) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String selectQuery = "UPDATE " + DbConstants.CHAT_TABLE_NAME +
                    " SET " + DbConstants.CHAT_UNREAD_MESSAGE + " = " + unread +
                    " WHERE " + DbConstants.CHAT_ID + " = " + id;
            db.rawQuery(selectQuery, null);
            db.close();
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status updated. ID: " + id);
            return true;
        } catch (Exception e) {
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status cannot be updated. ID: " + id);
            return false;
        }
    }

    public static boolean updateChatStatus(Long id, Integer code) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String selectQuery = "UPDATE " + DbConstants.CHAT_TABLE_NAME +
                    " SET " + DbConstants.CHAT_STATUS + " = " + code +
                    " WHERE " + DbConstants.CHAT_ID + " = " + id;
            db.rawQuery(selectQuery, null);
            db.close();
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status updated. ID: " + id);
            return true;
        } catch (Exception e) {
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status cannot be updated. ID: " + id);
            return false;
        }
    }


    public static void updateChat(Long id, String recipientName, String recipientMail) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String where = DbConstants.CHAT_ID + " = '" + recipientMail + "'";

            ContentValues values = new ContentValues();
            values.put(DbConstants.CHAT_NAME, recipientName);
            values.put(DbConstants.CHAT_ID, id);
            int update = db.update(DbConstants.CHAT_TABLE_NAME, values, where, null);
            db.close();
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status updated. ID: " + recipientMail);
        } catch (Exception e) {
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status cannot be updated. ID: " + recipientMail);

        }
    }

    public static void updateChatPbSpec(String topic, String pb, String pr) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String where = DbConstants.CHAT_TOPIC + " = '" + topic + "'";

            ContentValues values = new ContentValues();
            values.put(DbConstants.CHAT_PBK, pb);
            values.put(DbConstants.CHAT_PRK, pr);
            int update = db.update(DbConstants.CHAT_TABLE_NAME, values, where, null);
            db.close();
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status updated. ID: " + topic);
        } catch (Exception e) {
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status cannot be updated. ID: " + topic);

        }
    }

    public static void updateChatMsgSpec(String topic, String opb, String msg) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String where = DbConstants.CHAT_TOPIC + " = '" + topic + "'";

            ContentValues values = new ContentValues();
            values.put(DbConstants.CHAT_MSGK, msg);
            values.put(DbConstants.CHAT_OPBK, opb);
            int update = db.update(DbConstants.CHAT_TABLE_NAME, values, where, null);
            db.close();
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status updated. ID: " + topic);
        } catch (Exception e) {
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status cannot be updated. ID: " + topic);

        }
    }


    public static void updateChatPbStatus(String topic, int i) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String where = DbConstants.CHAT_TOPIC + " = '" + topic + "'";

            ContentValues values = new ContentValues();
            values.put(DbConstants.CHAT_PBK_SENT, i);
            int update = db.update(DbConstants.CHAT_TABLE_NAME, values, where, null);
            db.close();
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status updated. ID: " + topic);
        } catch (Exception e) {
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " status cannot be updated. ID: " + topic);

        }
    }

    public static int getUnreadNumber(Long id) {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String select = "select count(*) from " + DbConstants.MESSAGE_TABLE_NAME +
                    " where " + DbConstants.MESSAGE_TYPE + " = " + 0 + " and " +
                    DbConstants.MESSAGE_CHAT_ID + " = '" + id + "' and " +
                    DbConstants.MESSAGE_STATUS + " != " + ConversationMessageStatus.READ.getCode();
            //     " and  " + DbConstants.MESSAGE_STATUS + " is not null ";

            Cursor mCount = db.rawQuery(select, null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();
            db.close();
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " unread number get. ID: " + id);
            return count;
        } catch (Exception e) {
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " unread cannot be get. ID: " + id);
            return 0;

        }
    }


    public static int getUnreadNumber() {
        try {
            SQLiteDatabase db = sqoh.getWritableDatabase();
            String select = "select count(*) from " + DbConstants.MESSAGE_TABLE_NAME +
                    " where " + DbConstants.MESSAGE_TYPE + " = " + 0 + " AND " +
                    DbConstants.MESSAGE_STATUS + " != " + ConversationMessageStatus.READ.getCode();
            //     " and  " + DbConstants.MESSAGE_STATUS + " is not null ";

            Cursor mCount = db.rawQuery(select, null);
            mCount.moveToFirst();
            int count = mCount.getInt(0);
            mCount.close();
            db.close();
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " unread number get. ");
            return count;
        } catch (Exception e) {
            Log.e(TAG, DbConstants.CHAT_TABLE_NAME + " unread cannot be get.");
            return 0;

        }
    }


}
