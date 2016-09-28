package messaging.mqtt.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import messaging.mqtt.android.act.MainActivity;
import messaging.mqtt.android.database.Database;
import messaging.mqtt.android.database.DbEntryService;
import messaging.mqtt.android.database.DbTableService;
import messaging.mqtt.android.mqtt.MqttInit;
import messaging.mqtt.android.sharedPrefs.SharedPreferencesService;

/**
 * Created by mree on 27.09.2016.
 */
public class AsimService extends Service {
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    public static SharedPreferencesService preferencesService;
    private static Context context;
    private static Database db;
    private static MqttInit mqttInit;
    public static boolean running;
    private static int corePoolSize = 5;
    private static int maximumPoolSize = 10;
    private static LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
            corePoolSize,       // Initial pool size
            maximumPoolSize,       // Max pool size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue);

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (db == null) {
            db = new Database(getApplicationContext());
        }
        DbTableService.sqoh = db;
        DbEntryService.sqoh = db;
        createTables();
        running = true;
        context = getApplicationContext();
        preferencesService = new SharedPreferencesService(fillMitrilPreferences());
    }

    private void createTables() {
        DbTableService.createChatTable();
        DbTableService.createMessageTable();
    }

    public static SharedPreferences fillMitrilPreferences() {

        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    public static SharedPreferencesService getPreferencesService() {
        return preferencesService;
    }

    public static MqttInit getMqttInit() {
        if (mqttInit == null)
            mqttInit = new MqttInit(context, "tcp://iot.eclipse.org:1883", Build.ID);
        return mqttInit;
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {
        return threadPoolExecutor;
    }
}
