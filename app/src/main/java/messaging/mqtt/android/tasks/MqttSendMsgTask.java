package messaging.mqtt.android.tasks;

import android.util.Log;

import messaging.mqtt.android.service.AsimService;


public class MqttSendMsgTask implements Runnable {

    private static final String TAG = MqttSendMsgTask.class.getSimpleName();
    private String topic;
    private byte[] payload;

    public MqttSendMsgTask(String topic, byte[] payload) {
        this.payload = payload;
        this.topic = topic;
    }

    @Override
    public void run() {
        try {
            boolean b = AsimService.getMqttInit().sendMessage(topic, payload);
            if (b)
                Log.d(TAG, "Message sent");
            else
                Log.e(TAG, "Message cannot be sent");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
