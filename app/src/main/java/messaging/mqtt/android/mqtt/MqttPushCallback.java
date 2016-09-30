package messaging.mqtt.android.mqtt;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import messaging.mqtt.android.act.ConversationActivity;
import messaging.mqtt.android.common.model.ConversationMessageInfo;
import messaging.mqtt.android.common.ref.ConversationMessageStatus;
import messaging.mqtt.android.crypt.DbEncryptOperations;
import messaging.mqtt.android.database.DbEntryService;
import messaging.mqtt.android.service.AsimService;
import messaging.mqtt.android.tasks.MsgProcessorTask;

/**
 * Created by eercan on 28.03.2016.
 */
public class MqttPushCallback implements MqttCallback {
    private static final String TAG = MqttPushCallback.class.getSimpleName();
    private Context context;


    public MqttPushCallback(Context context) {
        this.context = context;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {

        try {
            MsgProcessorTask task = new MsgProcessorTask(context, topic, message.getPayload());
            AsimService.getProcessorExecutor().submit(task);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        AsimService.getMqttInit().subscribe(topic);
    }

    private void saveMessage(Long chatId, String msg) throws Exception {
        ConversationMessageInfo cmi = new ConversationMessageInfo();
        cmi.setContent(msg.getBytes());
        cmi.setChatId(chatId);

        byte[] encrypt = DbEncryptOperations.encrypt(msg.getBytes());
        Long cmiId = DbEntryService.saveMessage(chatId,
                1,
                Base64.encodeToString(encrypt, Base64.DEFAULT),
                System.currentTimeMillis(),
                ConversationMessageStatus.CREATED.getCode());

        cmi.setId(cmiId);
        switch (ConversationActivity.status) {
            case CREATED:
            case STARTED:
            case RESUMED:
            case RESTARTED:
                Bundle b = new Bundle();
                b.putSerializable(ConversationActivity.ADD_MESSAGE, cmi);
                Message m = new Message();
                m.setData(b);
                ConversationActivity.getOnlineHandler().sendMessage(m);
                break;
            case PAUSED:
            case STOPPED:
            case DESTROYED:
                break;
        }

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

}
