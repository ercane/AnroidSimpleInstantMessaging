package messaging.mqtt.android.mqtt;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MqttInit {

    private static final String TAG = MqttInit.class.getSimpleName();
    private static Integer KEEP_ALIVE = 120;
    private static String broker;
    private static String clientId;
    private static MqttPushCallback callback;
    private static MqttClient mqttClient;
    private static Context context;

    public MqttInit(Context context, String broker, String clientId) {
        MqttInit.context = context;
        MqttInit.broker = broker;
        MqttInit.clientId = clientId;
        MqttInit.broker = broker;
        init();
    }

    public static String getBroker() {
        return broker;
    }

    public static void setBroker(String broker) {
        MqttInit.broker = broker;
    }

    public static String getClientId() {
        return clientId;
    }

    public static void setClientId(String clientId) {
        MqttInit.clientId = clientId;
    }

    public static MqttPushCallback getCallback() {
        if (callback == null)
            callback = new MqttPushCallback(context);
        return callback;
    }


    private static synchronized boolean init() {
        try {
            mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
            mqttClient.setCallback(getCallback());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setKeepAliveInterval(KEEP_ALIVE);
            options.setCleanSession(false);
            mqttClient.connect(options);
            Log.d(TAG, "Mqtt Client connected");
            return true;
        } catch (MqttException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public static boolean subscribe(String topic) {
        try {

            if (mqttClient == null) {
                if (!init()) {
                    throw new MqttException(new Exception("MqqtClient init failed"));
                }
            }

            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }

            mqttClient.subscribe(topic, 1);
            Log.d(TAG, "Mqtt client subscribed to: " + topic);
            return true;
        } catch (MqttException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public static synchronized boolean sendMessage(String topic, byte[] payload) {
        try {
            if (mqttClient == null) {
                init();
            }

            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }

            MqttMessage m = new MqttMessage();
            m.setPayload(payload);
            m.setQos(2);
            mqttClient.publish(topic, m);
            Log.d(TAG, "Mqtt client send message to: " + topic);
            return true;
        } catch (MqttException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public void disconnect() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            Log.e(TAG, e.getMessage());
            try {
                mqttClient.disconnectForcibly();
            } catch (MqttException e1) {
                Log.e(TAG, e1.getMessage());
            }
        }
    }
}
