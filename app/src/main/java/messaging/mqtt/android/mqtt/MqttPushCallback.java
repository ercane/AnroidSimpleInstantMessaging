package messaging.mqtt.android.mqtt;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.HashMap;

import messaging.mqtt.android.act.ConversationActivity;
import messaging.mqtt.android.common.model.ConversationMessageInfo;
import messaging.mqtt.android.common.ref.ConversationMessageStatus;
import messaging.mqtt.android.crypt.DbEncryptOperations;
import messaging.mqtt.android.crypt.MsgEncryptOperations;
import messaging.mqtt.android.database.DbConstants;
import messaging.mqtt.android.database.DbEntryService;

/**
 * Created by eercan on 28.03.2016.
 */
public class MqttPushCallback implements MqttCallback {
    private Context context;


    public MqttPushCallback(Context context) {
        this.context = context;
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        byte[] payload = message.getPayload();
        String payloadMsg = new String(payload, "UTF-8");
        HashMap<String, String> chatByTopic = DbEntryService.getChatByTopic(topic);

        if (payloadMsg.startsWith(MqttConstants.MQTT_DH_PUBLIC_KEY)) {
            String[] split = payloadMsg.split(MqttConstants.MQTT_SPLIT_PREFIX);
            MsgEncryptOperations.createMsgKeySpec(context, topic, split[1],
                    Integer.parseInt(chatByTopic.get(DbConstants.CHAT_PBK_SENT)));
        } else if (payloadMsg.startsWith(MqttConstants.MQTT_DH_PB_SENT)) {
            DbEntryService.updateChatPbStatus(topic, 1);
        } else {
            byte[] decryptMsg = MsgEncryptOperations.decryptMsg(topic, payload);
            String msg = new String(decryptMsg, "UTF-8");

            if (!msg.startsWith(MqttConstants.MQTT_SELF_PREFIX)) {
                String[] split = msg.split(MqttConstants.MQTT_SPLIT_PREFIX);
                saveMessage(Long.parseLong(chatByTopic.get(DbConstants.CHAT_ID)), split[1]);
            }

        }
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
