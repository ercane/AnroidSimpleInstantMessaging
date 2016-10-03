package messaging.mqtt.android.mqtt;

import android.content.Context;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import messaging.mqtt.android.service.AsimService;
import messaging.mqtt.android.tasks.MsgProcessorTask;


public class MqttPushCallback implements MqttCallback {
    private static final String TAG = MqttPushCallback.class.getSimpleName();
    private Context context;


    public MqttPushCallback(Context context) {
        this.context = context;
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.e(TAG, cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.e(TAG, "Message arrived: ");
        try {
            MsgProcessorTask task = new MsgProcessorTask(context, topic, message.getPayload());
            AsimService.getProcessorExecutor().submit(task);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

}
