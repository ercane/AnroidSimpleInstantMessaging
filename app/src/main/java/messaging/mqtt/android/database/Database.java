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

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


}
