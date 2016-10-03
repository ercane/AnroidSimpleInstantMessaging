package messaging.mqtt.android.tasks;

import messaging.mqtt.android.act.adapter.MessageListAdapter;
import messaging.mqtt.android.common.model.ConversationMessageInfo;

public class MsgDescryptTask implements Runnable {

    private MessageListAdapter adapter;
    private ConversationMessageInfo msg;

    public MsgDescryptTask(MessageListAdapter adapter, ConversationMessageInfo msg) {
        this.adapter = adapter;
        this.msg = msg;
    }

    @Override
    public void run() {

    }
}
