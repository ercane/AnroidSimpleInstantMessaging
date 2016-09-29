package messaging.mqtt.android.mqtt;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


public class MqttInit implements MqttCallback {

    private static final String TAG = MqttInit.class.getSimpleName();
    private static String broker;
    private static String clientId;
    private static MqttClient mqttClient;
    private Context context;

    public MqttInit(Context context, String broker, String clientId) {
        this.context = context;
        this.broker = broker;
        this.clientId = clientId;
        this.broker = broker;
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

    private synchronized boolean init() {
        try {
            mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttPushCallback callback = new MqttPushCallback(context);
            mqttClient.setCallback(callback);
            mqttClient.connect();
            Log.d(TAG, "Mqtt Client connected");
            return true;
        } catch (MqttException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public boolean subscribe(String topic) {
        try {

            if (mqttClient == null) {
                init();
            }

            if (!mqttClient.isConnected()) {
                mqttClient.connect();
            }

            mqttClient.subscribe(topic);
            Log.d(TAG, "Mqtt client subscribed to: " + topic);
            return true;
        } catch (MqttException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    public boolean sendMessage(String topic, byte[] payload) {
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
            m.setRetained(false);
            mqttClient.publish(topic, m);
            Log.d(TAG, "Mqtt client send message to: " + topic);
            return true;
        } catch (MqttException e) {
            Log.e(TAG, e.getMessage());
            return false;
        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(MqttConstants.MQTT_MSG_RECEIVED_INTENT);
        broadcastIntent.putExtra(MqttConstants.MQTT_MSG_RECEIVED_TOPIC, topic);
        broadcastIntent.putExtra(MqttConstants.MQTT_MSG_RECEIVED_MSG, new String(message
                .getPayload()));
        context.sendBroadcast(broadcastIntent);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

}
