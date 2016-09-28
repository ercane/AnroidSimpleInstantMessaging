package messaging.mqtt.android.tasks;


import messaging.mqtt.android.act.adapter.MessageListAdapter;
import messaging.mqtt.android.common.model.ConversationMessageInfo;
import messaging.mqtt.android.crypt.DbEncryptOperations;

/**
 * Created by eercan on 07.01.2016.
 */
public class DbDecyrptTask implements Runnable {


    private MessageListAdapter adapter;
    private ConversationMessageInfo msg;

    public DbDecyrptTask(MessageListAdapter adapter, ConversationMessageInfo msg) {
        this.adapter = adapter;
        this.msg = msg;
        adapter.getDecyrptMap().put(msg.getId(), false);
    }

    @Override
    public void run() {
        try {
            byte[] content = msg.getContent();
            byte[] decrypt = DbEncryptOperations.decrypt(content);
            msg.setContent(decrypt);
            adapter.getDecyrptMap().put(msg.getId(), true);
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            adapter.notifyDataSetChanged();
        }
    }

}
