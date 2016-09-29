package messaging.mqtt.android.tasks;

import messaging.mqtt.android.act.adapter.ContactListAdapter;
import messaging.mqtt.android.common.model.ConversationInfo;
import messaging.mqtt.android.common.ref.ConversationStatus;
import messaging.mqtt.android.service.AsimService;

/**
 * Created by mree on 28.09.2016.
 */
public class MqttSubscribeTask implements Runnable {
    private ContactListAdapter adapter;
    private ConversationInfo ci;

    public MqttSubscribeTask(ContactListAdapter adapter, ConversationInfo ci) {
        this.adapter = adapter;
        this.ci = ci;
    }

    @Override
    public void run() {
        if (AsimService.getMqttInit().subscribe(ci.getRoomTopic())) {
            ci.setStatus(ConversationStatus.SUBSCRIBED);
        } else {
            ci.setStatus(ConversationStatus.UNSUBSCRIBED);
        }
        adapter.notifyDataSetChanged();

    }
}
