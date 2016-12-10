package messaging.mqtt.android.tasks;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;

import messaging.mqtt.android.act.ConversationActivity;
import messaging.mqtt.android.common.model.ConversationMessageInfo;
import messaging.mqtt.android.common.ref.ConversationMessageStatus;
import messaging.mqtt.android.common.ref.ConversationMessageType;
import messaging.mqtt.android.common.ref.ReceipentStatus;
import messaging.mqtt.android.crypt.DbEncryptOperations;
import messaging.mqtt.android.crypt.MsgEncryptOperations;
import messaging.mqtt.android.database.DbConstants;
import messaging.mqtt.android.database.DbEntryService;
import messaging.mqtt.android.mqtt.MqttConstants;
import messaging.mqtt.android.service.AsimService;

public class MsgProcessorTask implements Runnable{

    private static final String TAG = MsgProcessorTask.class.getSimpleName();
    private Context context;
    private String topic;
    private byte[] payload;
    private Long time;

    public MsgProcessorTask(Context context, String topic, byte[] payload){
        this.context = context;
        this.topic = topic;
        this.payload = payload;
        time = System.currentTimeMillis();
    }

    @Override
    public void run(){
        try {
            String payloadMsg = new String(payload, "UTF-8");
            Log.e(TAG, "Message arrived: " + payloadMsg);
            HashMap<String, String> chatByTopic = DbEntryService.getChatByTopic(topic);

            String[] split = payloadMsg.split(MqttConstants.MQTT_SPLIT_PREFIX);
            if (payloadMsg.startsWith(MqttConstants.MQTT_PB)) {
                if (!Build.ID.equals(split[1])) {
                    MsgEncryptOperations.createMsgKeySpec(context, topic, split[2],
                            Integer.parseInt(chatByTopic.get(DbConstants.CHAT_PBK_SENT)));
                }
            } else if (payloadMsg.startsWith(MqttConstants.MQTT_PB_TAKEN)) {
                if (!Build.ID.equals(split[1])) {
                    DbEntryService.updateChatPbStatus(topic, 1);
                }
            } else if (payloadMsg.startsWith(MqttConstants.MQTT_READ_ALL)) {
                if (!Build.ID.equals(split[1])) {
                    changeStatus(Long.parseLong(chatByTopic.get(DbConstants.CHAT_ID)), null,
                            ConversationMessageStatus.READ);
                }
            } else if (payloadMsg.startsWith(MqttConstants.MQTT_RECEIVE_ALL)) {
                if (!Build.ID.equals(split[1])) {
                    if (split.length > 2) {
                        changeStatus(Long.parseLong(chatByTopic.get(DbConstants.CHAT_ID)), split[2],
                                ConversationMessageStatus.RECEIVED);
                    } else {
                        changeStatus(Long.parseLong(chatByTopic.get(DbConstants.CHAT_ID)), null,
                                ConversationMessageStatus.RECEIVED);
                    }
                }
            } else if (payloadMsg.startsWith(MqttConstants.MQTT_ONLINE_APPROVE)) {
                if (!Build.ID.equals(split[1])) {
                    applyOnline();
                }
            } else if (payloadMsg.startsWith(MqttConstants.MQTT_ONLINE_CHECK)) {
                if (!Build.ID.equals(split[1])) {
                    checkOnline();
                }
            } else if (payloadMsg.startsWith(MqttConstants.MQTT_OFFLINE)) {
                if (!Build.ID.equals(split[1])) {
                    applyOffline();
                }
            } else {
                byte[] decryptMsg = MsgEncryptOperations.decryptMsg(topic, payload);
                String cmiJson = new String(decryptMsg, "UTF-8");
                //ConversationMessageInfo cmi = new Gson().fromJson(cmiJson, ConversationMessageInfo.class);
                ConversationMessageInfo cmi = new ObjectMapper().readValue(decryptMsg, ConversationMessageInfo.class);
                if (!Build.ID.equals(cmi.getOwnId())) {
                    saveMessage(Long.parseLong(chatByTopic.get(DbConstants.CHAT_ID)), cmi);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void checkOnline(){
        MqttSendMsgTask task = new MqttSendMsgTask(topic, MqttConstants.MQTT_ONLINE_APPROVE_SELF.getBytes());
        AsimService.getSubSendExecutor().submit(task);
    }

    private void applyOffline(){
        Handler handler = ConversationActivity.getTitleHandler();
        if (handler != null && topic.equals(ConversationActivity.chatTopic)) {
            handler.sendEmptyMessage(ReceipentStatus.OFFLINE.getCode());
        }
    }

    private synchronized void applyOnline(){
        Handler handler = ConversationActivity.getTitleHandler();
        if (handler != null && topic.equals(ConversationActivity.chatTopic)) {
            handler.sendEmptyMessage(ReceipentStatus.ONLINE.getCode());
        }
    }

    private synchronized void saveMessage(Long chatId, ConversationMessageInfo cmi) throws Exception{
        Log.e(TAG, "Message saving....");
        cmi.setChatId(chatId);
        cmi.setType(ConversationMessageType.RECEIVED);
        byte[] encrypt = DbEncryptOperations.encrypt(cmi.getContent());
        Long cmiId = DbEntryService.saveMessage(chatId,
                cmi.getOwnId(),
                ConversationMessageType.RECEIVED,
                Base64.encodeToString(encrypt, Base64.DEFAULT),
                cmi.getContentType(),
                cmi.getSentReceiveDate().getTime(),
                ConversationMessageStatus.RECEIVED.getCode());

        MqttSendMsgTask task = new MqttSendMsgTask(topic, (MqttConstants.MQTT_RECEIVE_ALL_SELF + cmi.getId()).getBytes());
        AsimService.getSubSendExecutor().submit(task);

        cmi.setId(cmiId);
        switch (ConversationActivity.status) {
            case CREATED:
            case STARTED:
            case RESUMED:
            case RESTARTED:
                if (ConversationActivity.getAddHandler() != null) {
                    Bundle b = new Bundle();
                    b.putSerializable(ConversationActivity.ADD_MESSAGE, cmi);
                    Message m = new Message();
                    m.setData(b);
                    ConversationActivity.getAddHandler().sendMessage(m);
                }
                break;
            case PAUSED:
            case STOPPED:
            case DESTROYED:

                break;
        }


    }

    private void changeStatus(Long chatId, String msgId, ConversationMessageStatus status){
        Bundle b = new Bundle();
        switch (status) {
            case CREATED:
                break;
            case POST:
                break;
            case RECEIVED:
                b.putSerializable(ConversationActivity.RECEIVE_MESSAGE, chatId);
                if (msgId != null) {
                    b.putSerializable(ConversationActivity.RECEIVE_ID, Long.parseLong(msgId));
                }
                break;
            case READ:
                b.putSerializable(ConversationActivity.READ_MESSAGE, chatId);
                if (msgId != null) {
                    DbEntryService.updateMessageStatus(Long.parseLong(msgId), ConversationMessageStatus.READ.getCode());
                }
                break;
            case FAILED:
                break;
        }

        Message m = new Message();
        m.setData(b);
        ConversationActivity.getStatusHandler().sendMessage(m);

    }
}
