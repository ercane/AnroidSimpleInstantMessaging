package messaging.mqtt.android.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import messaging.mqtt.android.act.MainActivity;
import messaging.mqtt.android.database.Database;
import messaging.mqtt.android.database.DbEntryService;
import messaging.mqtt.android.database.DbTableService;
import messaging.mqtt.android.mqtt.MqttInit;
import messaging.mqtt.android.sharedPrefs.SharedPreferencesService;


public class AsimService extends Service{
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    public static SharedPreferencesService preferencesService;
    public static boolean running;
    private static Context context;
    private static Database db;
    private static MqttInit mqttInit;
    private static int corePoolSize = 5;
    private static int maximumPoolSize = 15;
    private static LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>();
    private static LinkedBlockingQueue<Runnable> workQueue2 = new LinkedBlockingQueue<>();
    private static ThreadPoolExecutor subSendExecutor = new ThreadPoolExecutor(
            corePoolSize,       // Initial pool size
            maximumPoolSize,       // Max pool size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue);

    private static ThreadPoolExecutor processorExecutor = new ThreadPoolExecutor(
            1,       // Initial pool size
            100,       // Max pool size
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            workQueue2);

    public static SharedPreferences fillMitrilPreferences(){

        return context.getSharedPreferences(MainActivity.class.getSimpleName(),
                Context.MODE_PRIVATE);
    }

    public static SharedPreferencesService getPreferencesService(){
        if (preferencesService == null) {
            preferencesService = new SharedPreferencesService(fillMitrilPreferences());
        }
        return preferencesService;
    }

    public static MqttInit getMqttInit(){
        //mqttInit = new MqttInit(context, "tcp://iot.eclipse.org:1883", Build.ID);
        //mqttInit = new MqttInit(context, "tcp://159.203.63.25:1883", Build.ID);
        if (mqttInit == null) {
            mqttInit = new MqttInit(context,
                    getBrokerAddress(), Build.ID);
        }
        return mqttInit;
    }

    private static String getBrokerAddress(){
        return getPreferencesService().getBrokerProtocol() + "://" +
                getPreferencesService().getBrokerIp() + ":" +
                getPreferencesService().getBrokerPort();
    }

    public static void setMqttInit(){
        mqttInit = new MqttInit(context, getBrokerAddress(), Build.ID);
    }

    public static ThreadPoolExecutor getSubSendExecutor(){
        return subSendExecutor;
    }

    public static ThreadPoolExecutor getProcessorExecutor(){
        return processorExecutor;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent ıntent){
        return null;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        if (db == null) {
            db = new Database(getApplicationContext());
        }
        DbTableService.sqoh = db;
        DbEntryService.sqoh = db;
        createTables();
        running = true;
        context = getApplicationContext();

        if (TextUtils.isEmpty(getPreferencesService().getBrokerIp())) {
            getPreferencesService().setBrokerProtocol("tcp");
            getPreferencesService().setBrokerIp("159.203.63.25");
            getPreferencesService().setBrokerPort("1883");
        }
    }

    private void createTables(){
        DbTableService.createChatTable();
        DbTableService.createMessageTable();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }
}
