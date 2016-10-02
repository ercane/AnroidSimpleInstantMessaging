package messaging.mqtt.android.tasks;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import java.util.Date;
import java.util.HashMap;

import messaging.mqtt.android.act.ConversationActivity;
import messaging.mqtt.android.common.model.ConversationMessageInfo;
import messaging.mqtt.android.common.ref.ConversationMessageStatus;
import messaging.mqtt.android.common.ref.ConversationMessageType;
import messaging.mqtt.android.crypt.DbEncryptOperations;
import messaging.mqtt.android.crypt.MsgEncryptOperations;
import messaging.mqtt.android.database.DbConstants;
import messaging.mqtt.android.database.DbEntryService;
import messaging.mqtt.android.mqtt.MqttConstants;
import messaging.mqtt.android.service.AsimService;

/**
 * Created by eercan on 30.09.2016.
 */
public class MsgProcessorTask implements Runnable {

    private static final String TAG = MsgProcessorTask.class.getSimpleName();
    private Context context;
    private String topic;
    private byte[] payload;
    private Long time;

    public MsgProcessorTask(Context context, String topic, byte[] payload) {
        this.context = context;
        this.topic = topic;
        this.payload = payload;
        time = System.currentTimeMillis();
    }

    @Override
    public void run() {
        try {
            String payloadMsg = new String(payload, "UTF-8");
            Log.d(TAG, "Message arrived: " + payloadMsg);
            HashMap<String, String> chatByTopic = DbEntryService.getChatByTopic(topic);

            String[] split = payloadMsg.split(MqttConstants.MQTT_SPLIT_PREFIX);
            if (payloadMsg.startsWith(MqttConstants.MQTT_PB)) {
                if (!Build.ID.equals(split[1]))
                    MsgEncryptOperations.createMsgKeySpec(context, topic, split[2],
                            Integer.parseInt(chatByTopic.get(DbConstants.CHAT_PBK_SENT)));
            } else if (payloadMsg.startsWith(MqttConstants.MQTT_PB_TAKEN)) {
                if (!Build.ID.equals(split[1])) {
                    DbEntryService.updateChatPbStatus(topic, 1);
                }
            } else if (payloadMsg.startsWith(MqttConstants.MQTT_READ_ALL)) {
                if (!Build.ID.equals(split[1])) {
                    changeStatus(Long.parseLong(chatByTopic.get(DbConstants.CHAT_ID)), ConversationMessageStatus.READ);
                }
            } else if (payloadMsg.startsWith(MqttConstants.MQTT_RECEIVE_ALL)) {
                if (!Build.ID.equals(split[1])) {
                    changeStatus(Long.parseLong(chatByTopic.get(DbConstants.CHAT_ID)), ConversationMessageStatus.RECEIVED);
                }
            } else {
                byte[] decryptMsg = MsgEncryptOperations.decryptMsg(topic, payload);
                String msg = new String(decryptMsg, "UTF-8");
                String[] msgSplit = msg.split(MqttConstants.MQTT_SPLIT_PREFIX);
                if (!Build.ID.equals(msgSplit[1])) {
                    saveMessage(Long.parseLong(chatByTopic.get(DbConstants.CHAT_ID)), msg);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private synchronized void saveMessage(Long chatId, String msg) throws Exception {
        ConversationMessageInfo cmi = new ConversationMessageInfo();
        cmi.setContent(msg.getBytes());
        cmi.setChatId(chatId);
        cmi.setType(ConversationMessageType.RECEIVED);
        Long time = System.currentTimeMillis();
        byte[] encrypt = DbEncryptOperations.encrypt(msg.getBytes());
        Long cmiId = DbEntryService.saveMessage(chatId,
                ConversationMessageType.RECEIVED,
                Base64.encodeToString(encrypt, Base64.DEFAULT),
                time,
                ConversationMessageStatus.RECEIVED.getCode());

        cmi.setId(cmiId);
        cmi.setSentReceiveDate(new Date(time));
        MqttSendMsgTask task = new MqttSendMsgTask(topic, MqttConstants.MQTT_RECEIVE_ALL_SELF.getBytes());
        AsimService.getSubSendExecutor().submit(task);

        switch (ConversationActivity.status) {
            case CREATED:
            case STARTED:
            case RESUMED:
            case RESTARTED:
                Bundle b = new Bundle();
                b.putSerializable(ConversationActivity.ADD_MESSAGE, cmi);
                Message m = new Message();
                m.setData(b);
                ConversationActivity.getAddHandler().sendMessage(m);
                break;
            case PAUSED:
            case STOPPED:
            case DESTROYED:

                break;
        }


    }

    private void changeStatus(Long chatId, ConversationMessageStatus status) {
        switch (ConversationActivity.status) {
            case CREATED:
            case STARTED:
            case RESUMED:
            case RESTARTED:
                Bundle b = new Bundle();
                switch (status) {
                    case CREATED:
                        break;
                    case POST:
                        break;
                    case RECEIVED:
                        b.putSerializable(ConversationActivity.RECEIVE_MESSAGE, chatId);
                        break;
                    case READ:
                        b.putSerializable(ConversationActivity.READ_MESSAGE, chatId);
                        break;
                    case FAILED:
                        break;
                }
                Message m = new Message();
                m.setData(b);
                ConversationActivity.getAddHandler().sendMessage(m);
                break;
            case PAUSED:
            case STOPPED:
            case DESTROYED:
                break;
        }
    }
}
