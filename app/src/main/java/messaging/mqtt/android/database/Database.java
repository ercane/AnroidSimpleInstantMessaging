package messaging.mqtt.android.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {


    public Database(Context context) {
        super(context, DbConstants.ASIM_DATABASE_NAME, null, DbConstants.ASIM_DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        DbTableService.sqoh = this;
        DbEntryService.sqoh = this;
    }


/*    pub void createServerMessageTable() {
        CreateOrDropTables.createServerMessageTable();
    }

    pub void createErrorTable() {
        CreateOrDropTables.createErrorTable();
    }

    pub void createValidProfilesTable() {
        CreateOrDropTables.createValidProfilesTable();
    }

    pub void createValidPoliciesTable() {
        CreateOrDropTables.createValidPoliciesTable();
    }


    pub void dropTable(String tableName) {
        CreateOrDropTables.dropTable(this, tableName);
    }*/

   /* pub void saveServerMessage(Long opId, String content, Long date) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DbConstants.SERVER_MESSAGE_OP_ID, opId);
            values.put(DbConstants.SERVER_MESSAGE_CONTENT, content);
            values.put(DbConstants.SERVER_MESSAGE_DATE, date);


            db.insert(DbConstants.SERVER_MESSAGE_TABLE_NAME, null, values);
            db.close(); //Database Bağlantısını kapattık*//*
            Log.e("DATABASE", "Message saved to database. Date: " + date);
        } catch (Exception e) {
            Log.e("DATABASE", "Message cannot be saved to database. Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    pub void savePushError(int opCode, Long opId, String message, Long date) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DbConstants.PUSH_ERROR_OP_CODE, opCode);
            values.put(DbConstants.PUSH_ERROR_OP_ID, opId);
            values.put(DbConstants.PUSH_ERROR_MESSAGE, message);
            values.put(DbConstants.PUSH_ERROR_DATE, date);


            db.insert(DbConstants.PUSH_ERROR_TABLE_NAME, null, values);
            db.close(); //Database Bağlantısını kapattık*//*
            Log.e("DATABASE", "Error saved to database. Date: " + date);
        } catch (Exception e) {
            Log.e("DATABASE", "Error cannot be saved to database. Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    pub void saveProfile(String id, String name, Integer revision, Integer type, Integer status, Long date, String details) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DbConstants.PROFILE_ID, id);
            values.put(DbConstants.PROFILE_NAME, name);
            values.put(DbConstants.PROFILE_REVISION, revision);
            values.put(DbConstants.PROFILE_TYPE, type);
            values.put(DbConstants.PROFILE_STATUS, status);
            values.put(DbConstants.PROFILE_IMPLEMENTED_DATE, date);
            values.put(DbConstants.PROFILE_DETAILS, details);


            db.insert(DbConstants.PROFILE_TABLE_NAME, null, values);
            db.close(); //Database Bağlantısını kapattık*//*
            Log.e("DATABASE", "Profile saved to database. ID: " + id);
        } catch (Exception e) {
            Log.e("DATABASE", "Profile cannot be saved to database. Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    pub void savePolicy(String id, String name, Integer revision, Integer type, Integer status, String valid, Long date, String details) {

        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DbConstants.POLICY_ID, id);
            values.put(DbConstants.POLICY_NAME, name);
            values.put(DbConstants.POLICY_REVISION, revision);
            values.put(DbConstants.POLICY_TYPE, type);
            values.put(DbConstants.POLICY_STATUS, status);
            values.put(DbConstants.POLICY_VALID_FLAG, valid);
            values.put(DbConstants.POLICY_IMPLEMENTED_DATE, date);
            values.put(DbConstants.POLICY_DETAILS, details);
            db.insert(DbConstants.POLICY_TABLE_NAME, null, values);
            db.close(); //Database Bağlantısını kapattık*//*
            Log.e("DATABASE", "Policy saved to database. ID: " + id);
        } catch (Exception e) {
            Log.e("DATABASE", "Policy cannot be saved to database. Exception: " + e.getMessage());
            e.printStackTrace();
        }
    }*/

/*    pub HashMap<String, Object> getProfileById(String id) {

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DbConstants.PROFILE_TABLE_NAME + " WHERE (" +
                DbConstants.PROFILE_ID + " = '" + id + "')";
        Cursor cursor = db.rawQuery(selectQuery, null);
        HashMap<String, Object> profile = new HashMap<String, Object>();

        if (cursor.moveToFirst()) {
            do {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    profile.put(cursor.getColumnName(i), cursor.getString(i));
                }
            } while (cursor.moveToNext());
        }
        db.close();

        return profile;
    }


    pub HashMap<String, Object> getValidProfile(Integer type) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DbConstants.PROFILE_TABLE_NAME + " WHERE (" +
                DbConstants.PROFILE_STATUS + " = " + CommonStatus.STARTED.getCode() + " AND " +
                DbConstants.PROFILE_TYPE + " = " + type + ")";
        Cursor cursor = db.rawQuery(selectQuery, null);
        HashMap<String, Object> profile = new HashMap<String, Object>();

        if (cursor.moveToFirst()) {
            do {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    profile.put(cursor.getColumnName(i), cursor.getString(i));
                }
            } while (cursor.moveToNext());
        }
        db.close();

        return profile;
    }

    pub void removeProfile(String id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(DbConstants.PROFILE_TABLE_NAME, DbConstants.PROFILE_ID + " = '" + id + "' ", null);
            db.close();
            Log.e("DATABASE", "Profile deleted. ID: " + id);
        } catch (Exception e) {
            Log.e("DATABASE", "Profile cannot be deleted. ID: " + id);
            e.printStackTrace();
        }
    }

    pub void removePolicy(String id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(DbConstants.POLICY_TABLE_NAME, DbConstants.POLICY_ID + " = '" + id + "' ", null);
            db.close();
            Log.e("DATABASE", "Policy deleted. ID: " + id);
        } catch (Exception e) {
            Log.e("DATABASE", "Policy cannot be deleted. ID: " + id);
            e.printStackTrace();
        }
    }

    pub HashMap<String, Object> getDefaultPolicy() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DbConstants.POLICY_TABLE_NAME + " WHERE (" +
                DbConstants.POLICY_ID + " = " + DbConstants.POLICY_DEFAULT_ID + ")";
        Cursor cursor = db.rawQuery(selectQuery, null);
        HashMap<String, Object> profile = new HashMap<String, Object>();

        if (cursor.moveToFirst()) {
            do {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    profile.put(cursor.getColumnName(i), cursor.getString(i));
                }
            } while (cursor.moveToNext());
        }
        db.close();

        return profile;
    }

    pub HashMap<String, Object> getValidPolicy() {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DbConstants.POLICY_TABLE_NAME + " WHERE (" +
                DbConstants.POLICY_VALID_FLAG + " = " + "'true'" + ")";
        Cursor cursor = db.rawQuery(selectQuery, null);
        HashMap<String, Object> profile = new HashMap<String, Object>();

        if (cursor.moveToFirst()) {
            do {
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    profile.put(cursor.getColumnName(i), cursor.getString(i));
                }
            } while (cursor.moveToNext());
        }
        db.close();

        return profile;
    }

    pub void updateValidPolicy(String id) {
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues data = new ContentValues();
            data.put(DbConstants.POLICY_VALID_FLAG, "false");
            db.update(DbConstants.POLICY_TABLE_NAME, data, DbConstants.POLICY_VALID_FLAG + " = " + "'true'", null);

            data.clear();
            data.put(DbConstants.POLICY_VALID_FLAG, "true");
            db.update(DbConstants.POLICY_TABLE_NAME, data, DbConstants.POLICY_ID + " = '" + id + "' ", null);
            db.close();
            Log.e("DATABASE", "Policy updated. ID: " + id);
        } catch (Exception e) {
            Log.e("DATABASE", "Policy cannot be updated. ID: " + id);
            e.printStackTrace();
        }
    }*/

/*    pub int getMessagesRowCount() {
        String countQuery = "SELECT  * FROM " + DbConstants.SERVER_MESSAGE_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }

    pub int getErrorRowCount() {
        String countQuery = "SELECT  * FROM " + DbConstants.PUSH_ERROR_TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

/*    pub ArrayList<HashMap<String, String>> getAllMessages() {

        //Bu methodda ise tablodaki tüm değerleri alıyoruz
        //ArrayList adı üstünde Array lerin listelendiği bir Array.Burda hashmapleri listeleyeceğiz
        //Herbir satırı değer ve value ile hashmap a atıyoruz. Her bir satır 1 tane hashmap arrayı demek.
        //olusturdugumuz tüm hashmapleri ArrayList e atıp geri dönüyoruz(return).

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DbConstants.SERVER_MESSAGE_TABLE_NAME;
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
        // return kitap liste
        return messageList;
    }

    pub ArrayList<HashMap<String, String>> getAllErrors() {

        //Bu methodda ise tablodaki tüm değerleri alıyoruz
        //ArrayList adı üstünde Array lerin listelendiği bir Array.Burda hashmapleri listeleyeceğiz
        //Herbir satırı değer ve value ile hashmap a atıyoruz. Her bir satır 1 tane hashmap arrayı demek.
        //olusturdugumuz tüm hashmapleri ArrayList e atıp geri dönüyoruz(return).

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DbConstants.PUSH_ERROR_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<HashMap<String, String>> errorList = new ArrayList<HashMap<String, String>>();
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }

                errorList.add(map);
            } while (cursor.moveToNext());
        }
        db.close();

        return errorList;
    }

    pub ArrayList<HashMap<String, String>> getAllProfiles() {

        //Bu methodda ise tablodaki tüm değerleri alıyoruz
        //ArrayList adı üstünde Array lerin listelendiği bir Array.Burda hashmapleri listeleyeceğiz
        //Herbir satırı değer ve value ile hashmap a atıyoruz. Her bir satır 1 tane hashmap arrayı demek.
        //olusturdugumuz tüm hashmapleri ArrayList e atıp geri dönüyoruz(return).

        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + DbConstants.PROFILE_TABLE_NAME;
        Cursor cursor = db.rawQuery(selectQuery, null);
        ArrayList<HashMap<String, String>> errorList = new ArrayList<HashMap<String, String>>();
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }

                errorList.add(map);
            } while (cursor.moveToNext());
        }
        db.close();

        return errorList;
    }*/
}
