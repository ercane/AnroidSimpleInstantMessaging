package messaging.mqtt.android.tasks;

import android.content.Context;
import android.util.Log;

import messaging.mqtt.android.crypt.MsgEncryptOperations;

public class PbKeyProcessorTask implements Runnable {

    private static final String TAG = PbKeyProcessorTask.class.getSimpleName();
    private Context context;
    private String topic;
    private int type;

    public PbKeyProcessorTask(Context context, String topic, int type) {
        this.context = context;
        this.topic = topic;
        this.type = type;
    }

    @Override
    public void run() {
        try {
            MsgEncryptOperations.createSelfKeySpec(context, topic);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
