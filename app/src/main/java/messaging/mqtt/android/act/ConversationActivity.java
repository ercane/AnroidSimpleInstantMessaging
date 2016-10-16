package messaging.mqtt.android.act;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import messaging.mqtt.android.R;
import messaging.mqtt.android.act.adapter.MessageListAdapter;
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
import messaging.mqtt.android.tasks.ActivityStatus;
import messaging.mqtt.android.tasks.DbDecyrptTask;
import messaging.mqtt.android.tasks.MqttSendMsgTask;
import messaging.mqtt.android.util.HtmlUtils;
import messaging.mqtt.android.util.Notification;

public class ConversationActivity extends AppCompatActivity{

    public static final String ADD_MESSAGE = "AddMessage";
    public static final String READ_MESSAGE = "ReadMessage";
    public static final String RECEIVE_MESSAGE = "ReceiveMessage";
    public static final String RECEIVE_ID = "ReceiveId";
    public static Long chatId;
    public static String chatTopic;
    public static ActivityStatus status;
    public static Comparator<ConversationMessageInfo> comparator = new
            Comparator<ConversationMessageInfo>(){
                @Override
                public int compare(ConversationMessageInfo first, ConversationMessageInfo second){
                    return first.getSentReceiveDate().compareTo(second.getSentReceiveDate());
                }
            };
    private static Integer SHOW_LIMIT = 20;
    private static Handler addHandler;
    private static Handler titleHandler;
    private static Handler statusHandler;
    private static String TAG = ConversationActivity.class.getSimpleName();
    private static MessageListAdapter adapter;
    public Long messageLimitTime = System.currentTimeMillis();
    private ActionBar actionBar;
    private ListView listView;
    private EditText messageText;
    private Button sendMessage;
    private List<ConversationMessageInfo> messageList;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ActionMode mode;
    private ProgressBar pbError;


    public static byte[] getDbEncrypted(byte[] content){
        String def = "CANNOT ENCRYPTED";
        byte[] encrypted = def.getBytes();
        try {
            encrypted = DbEncryptOperations.encrypt(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }

    public static byte[] getDbDecrypted(byte[] content){
        String def = "CANNOT DECRYPTED";
        byte[] decyrpted = def.getBytes();
        try {
            decyrpted = DbEncryptOperations.decrypt(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decyrpted;
    }

    public static Handler getAddHandler(){
        return addHandler;
    }

    public static Handler getStatusHandler(){
        if (statusHandler == null) {
            statusHandler = new Handler(Looper.getMainLooper()){
                @Override
                public void handleMessage(Message msg){
                    try {
                        Bundle data = msg.getData();
                        if (data.getSerializable(RECEIVE_MESSAGE) != null) {
                            long id = (long) data.getSerializable(RECEIVE_MESSAGE);
                            long msgId = (long) data.getSerializable(RECEIVE_ID);
                            DbEntryService.updateMessageStatus(msgId, ConversationMessageStatus.RECEIVED.getCode());
                            //DbEntryService.updateMessagesToReceive (id);
                            if (adapter != null && chatId == id) {
                                adapter.setStatusToReceive(msgId);
                            }
                        } else if (data.getSerializable(READ_MESSAGE) != null) {
                            long id = (long) data.getSerializable(READ_MESSAGE);
                            DbEntryService.updateSentMessagesToRead(id);
                            if (adapter != null && chatId == id) {
                                adapter.setStatusToRead();
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage() + "");
                    }
                }
            };
        }
        return statusHandler;
    }

    public static byte[] getMsgEncrypted(byte[] content){
        String def = "CANNOT ENCRYPTED";
        byte[] encrypted = def.getBytes();
        try {
            encrypted = MsgEncryptOperations.encryptMsg(chatTopic, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypted;
    }

    public static byte[] getMsgDecrypted(byte[] content){
        String def = "CANNOT DECRYPT";
        byte[] decyrpted = def.getBytes();
        try {
            decyrpted = MsgEncryptOperations.decryptMsg(chatTopic, content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decyrpted;
    }

    public static Handler getTitleHandler(){
        return titleHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        actionBar = getSupportActionBar();
        status = ActivityStatus.CREATED;

        chatId = getIntent().getLongExtra(ContactActivity.CONTACT_ID, -1);
        chatTopic = getIntent().getStringExtra(ContactActivity.CONTACT_TOPIC);
        final String contactName = getIntent().getStringExtra(ContactActivity.CONTACT_NAME);

        if (actionBar != null) {
            actionBar.setTitle(Html.fromHtml("<font color=\"" + HtmlUtils.WHITE + "\">" + contactName + "</font>"));
        }

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id
                .activity_main_swipe_refresh_layout);
        listView = (ListView) findViewById(R.id.messageList);
        messageList = new ArrayList<ConversationMessageInfo>();


        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setStackFromBottom(true);
        listView.setDivider(null);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener(){
            private int nr = 0;

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean
                    checked){
                final int checkedCount = listView.getCheckedItemCount();
                mode.setTitle(checkedCount + " Selected");
                adapter.toggleSelection(position);

            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item){
                List<Long> selected;
                switch (item.getItemId()) {
                    case R.id.item_delete:
                        selected = adapter.getSelectedIds();
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            Long id = selected.get(i);
                            if (adapter.getSelectMap().get(id)) {
                                ConversationMessageInfo selecteditem = adapter
                                        .getItem(id);
                                DbEntryService.removeMessage(selecteditem.getId());
                                adapter.remove(selecteditem);
                            }
                        }
                        mode.finish();
                        return true;
                    case R.id.item_copy:
                        selected = adapter.getSelectedIds();
                        // Captures all selected ids with a loop
                        String copyMsg = "";
                        for (int i = 0; i < selected.size(); i++) {
                            Long id = selected.get(i);
                            if (adapter.getSelectMap().get(id)) {
                                ConversationMessageInfo selecteditem = adapter
                                        .getItem(id);
                                copyMsg += new String(selecteditem.getContent()).split(MqttConstants.MQTT_SPLIT_PREFIX)[2] + "\n";

                            }
                        }
                        ClipboardManager clipboard = (ClipboardManager) getSystemService
                                (CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("label", copyMsg);
                        clipboard.setPrimaryClip(clip);
                        // Close CAB
                        mode.finish();
                        Toast.makeText(ConversationActivity.this, "Copied to clipboard", Toast
                                .LENGTH_LONG).show();
                        return true;

                    case R.id.item_select_all:
                        adapter.removeSelection();
                        listView.clearChoices();
                        for (int i = 0; i < adapter.getMessageList().size(); i++) {
                            listView.setItemChecked(i, true);
                        }
     /*                   adapter.removeSelection();
                        int size = adapter.getMessageList().size();
                        int i;
                        for (i = 0; i < size; i++) {
                            adapter.toggleSelection(i);
                        }*/
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu){
                mode.getMenuInflater().inflate(R.menu.chat_contextual, menu);
                ConversationActivity.this.getWindow().setSoftInputMode(WindowManager.LayoutParams
                        .SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                ConversationActivity.this.mode = mode;
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode){
                // TODO Auto-generated method stub
                adapter.removeSelection();
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu){
                // TODO Auto-generated method stub
                return false;
            }
        });


        messageText = (EditText) findViewById(R.id.messageText);
        sendMessage = (Button) findViewById(R.id.sendMessage);
        sendMessage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                sendMessage.setEnabled(false);
                if (messageText.getText() == null || "".equals(messageText.getText().toString())) {
                    sendMessage.setEnabled(true);
                    return;
                }

                final String content = MqttConstants.MQTT_MSG_SENT + messageText.getText().toString();
                ConversationMessageInfo cmi = new ConversationMessageInfo();
                cmi.setContent(content.getBytes());
                cmi.setChatId(chatId);
                cmi.setStatus(ConversationMessageStatus.CREATED);
                cmi.setType(ConversationMessageType.SENT);

                Date date = new Date(System.currentTimeMillis());
                cmi.setUpdatedDate(date);
                cmi.setSentReceiveDate(date);
                byte[] encrypted = getDbEncrypted(cmi.getContent());
                Long id = DbEntryService.saveMessage(
                        chatId,
                        ConversationMessageType.SENT,
                        Base64.encodeToString(encrypted, Base64.DEFAULT),
                        cmi.getUpdatedDate().getTime(),
                        cmi.getStatus().getCode());
                cmi.setId(id);
                messageText.setText("");
                adapter.getDecyrptMap().put(cmi.getId(), true);
                adapter.add(cmi);
                sendMessageTask(cmi).execute();
            }
        });

        TextView tvError = (TextView) findViewById(R.id.tvError);
        pbError = (ProgressBar) findViewById(R.id.pbError);
        RelativeLayout msgLayout = (RelativeLayout) findViewById(R.id.messageLayout);
        final HashMap<String, String> chatRoom = DbEntryService.getChatByTopic(chatTopic);
        if ("0".equals(chatRoom.get(DbConstants.CHAT_PBK_SENT))) {
            //if ((chatRoom.get(DbConstants.CHAT_MSGK)) == null) {
            tvError.setText(R.string.key_not_created_error);
            tvError.setVisibility(View.VISIBLE);
            pbError.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setVisibility(View.GONE);
            msgLayout.setVisibility(View.GONE);
            msgLayout.setEnabled(false);

            try {
                String enc = chatRoom.get(DbConstants.CHAT_PBK);
                String pbStr = null;

                if (enc != null) {
                    byte[] dec = DbEncryptOperations.decrypt(Base64.decode(enc.getBytes(), Base64
                            .DEFAULT));
                    pbStr = Base64.encodeToString(dec, Base64.DEFAULT);
                } else {
                    byte[] pubkey = MsgEncryptOperations.createSelfKeySpec
                            (ConversationActivity.this, chatTopic);
                    pbStr = Base64.encodeToString(pubkey, Base64.DEFAULT);
                }

                MqttSendMsgTask task = new MqttSendMsgTask(chatTopic, (MqttConstants
                        .MQTT_PB_SELF + pbStr).getBytes());
                AsimService.getSubSendExecutor().submit(task);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if ((chatRoom.get(DbConstants.CHAT_MSGK)) != null) {
                MqttSendMsgTask task = new MqttSendMsgTask(chatTopic, MqttConstants.MQTT_PB_TAKEN_SELF.getBytes());
                AsimService.getSubSendExecutor().submit(task);
            }

        } else {
            tvError.setVisibility(View.GONE);
            pbError.setVisibility(View.GONE);
            mSwipeRefreshLayout.setVisibility(View.VISIBLE);
            msgLayout.setEnabled(true);
            adapter = new MessageListAdapter(getApplicationContext(), R.layout.message_layout,
                    messageList);
            listView.setAdapter(adapter);
            getMessages();
        }

        Notification.clearNotification(this);

        titleHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                super.handleMessage(msg);
                ReceipentStatus status = ReceipentStatus.get(msg.what);
                String title = "";
                switch (status) {
                    case UNKNOWN:
                        title = "<font color=\"" + HtmlUtils.WHITE + "\">" + contactName + "</font>";
                        break;
                    case ONLINE:
                        title = "<font color=\"" + HtmlUtils.GREEN + "\">" + contactName + "</font>";
                        break;
                    case OFFLINE:
                        title = "<font color=\"" + HtmlUtils.WHITE + "\">" + contactName + "</font>";
                        break;
                }

                if (actionBar != null) {
                    actionBar.setTitle(Html.fromHtml(title));
                }

            }
        };

        addHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg){
                try {
                    Bundle data = msg.getData();
                    if (data.getSerializable(ADD_MESSAGE) != null) {
                        final ConversationMessageInfo cmi = (ConversationMessageInfo) data
                                .getSerializable(ADD_MESSAGE);

                        if (adapter != null && cmi.getChatId() == chatId) {
                            adapter.getDecyrptMap().put(cmi.getId(), true);
                            adapter.add(cmi);
                        }

                        MqttSendMsgTask task = new MqttSendMsgTask(chatTopic, MqttConstants.MQTT_READ_ALL_SELF.getBytes());
                        AsimService.getSubSendExecutor().submit(task);
                    }
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage() + "");
                }
            }
        };
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        adapter = null;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.addImage:
                break;
        }
        return true;
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private void getMessages(){
        try {
            final ArrayList<HashMap<String, String>> first = DbEntryService.getAllMessagesByChat
                    (chatId, SHOW_LIMIT, messageLimitTime);
            String temp = null;
            if (first.size() > 0) {
                temp = first.get(first.size() - 1).get(DbConstants.MESSAGE_SENDING_TIME);
            }
            messageLimitTime = Long.parseLong(temp);

            showMessagesTask(first).execute();
            if (first.size() == SHOW_LIMIT) {
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener
                        (){
                    @Override
                    public void onRefresh(){
                        if (mode != null) {
                            mode.finish();
                        }
                        int size = adapter.getMessageList().size();
                        List<HashMap<String, String>> addList = DbEntryService
                                .getAllMessagesByChat(chatId, size + SHOW_LIMIT, messageLimitTime);
                        if (addList.size() == size + SHOW_LIMIT) {
                            String temp = addList.get(addList.size() - 1).get(DbConstants
                                    .MESSAGE_SENDING_TIME);
                            messageLimitTime = Long.parseLong(temp);
                        } else {
                            messageLimitTime = System.currentTimeMillis();
                        }
                        showMessagesTask(addList).execute();
                    }
                });
            } else {
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener
                        (){
                    @Override
                    public void onRefresh(){
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    public AsyncTask<Void, Void, Boolean> showMessagesTask(final List<HashMap<String, String>>
                                                                   list){
        return new AsyncTask<Void, Void, Boolean>(){
            Boolean isThereUnread = false;
            int counter = 0;
            ProgressDialog progress;
            List<ConversationMessageInfo> messageInfos = new ArrayList<>();
            List<Long> ids = new ArrayList<>();

            @Override
            protected void onPreExecute(){
                progress = new ProgressDialog(ConversationActivity.this);
                progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                progress.setIndeterminate(true);
                progress.setMessage("Retrieving messages...");
                progress.setProgress(0);
                progress.setCancelable(false);
                progress.show();
            }

            @Override
            protected Boolean doInBackground(Void... params){
                try {
                    for (HashMap<String, String> msg : list) {
                        final ConversationMessageInfo ci = new ConversationMessageInfo();
                        try {
                            String id = msg.get(DbConstants.MESSAGE_ID);
                            ci.setId(Long.parseLong(id));
                            ids.add(ci.getId());

                            Integer typeCode = Integer.parseInt(msg.get(DbConstants.MESSAGE_TYPE));
                            ConversationMessageType type = ConversationMessageType.get(typeCode);
                            ci.setType(type);

                            String code = msg.get(DbConstants.MESSAGE_STATUS);
                            ci.setStatus(ConversationMessageStatus.get(Integer.parseInt(code)));


                            String temp = msg.get(DbConstants.MESSAGE_CONTENT);
                            byte[] encrypted = Base64.decode(temp.getBytes(), Base64.DEFAULT);
                            ci.setContent(encrypted);

                            ci.setSentReceiveDate(new Date(Long.parseLong(msg.get(DbConstants.MESSAGE_SENDING_TIME))));

                            Long ms = Long.parseLong(msg.get(DbConstants.MESSAGE_SENDING_TIME));
                            ci.setUpdatedDate(new Date(ms));

                            if (type == ConversationMessageType.RECEIVED) {
                                if (ci.getStatus() == ConversationMessageStatus.RECEIVED) {
                                    isThereUnread = true;
                                }
                            }

                            final String tempId = msg.get(DbConstants.MESSAGE_ID);

                            if (ConversationMessageStatus.CREATED == ci.getStatus()) {
                                ci.setId(Long.parseLong(tempId));
                                runOnUiThread(new Runnable(){
                                    @Override
                                    public void run(){
                                        sendMessageTask(ci).execute();
                                    }
                                });
                            }

                            messageInfos.add(ci);

                            if (counter >= 30) {
                                break;
                            }

                            counter++;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                    return true;
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean state){
                super.onPostExecute(state);
                mSwipeRefreshLayout.setRefreshing(false);
                if (isThereUnread) {
                    final Date updatedDate = messageInfos.get(messageInfos.size() - 1)
                            .getUpdatedDate();
                    DbEntryService.updateMessagesToRead(updatedDate.getTime());

                    MqttSendMsgTask task = new MqttSendMsgTask(chatTopic, MqttConstants.MQTT_READ_ALL_SELF.getBytes());
                    AsimService.getSubSendExecutor().submit(task);

                }

                for (ConversationMessageInfo ci : messageInfos) {
                    AsimService.getProcessorExecutor().submit(new DbDecyrptTask(adapter, ci));
                }

                List<ConversationMessageInfo> adapterList = adapter.getMessageList();

                //List<ConversationMessageInfo> temp = adapterList.subList(0,adapterList.size());
                //adapterList.clear();
                adapterList.addAll(messageInfos);
                //adapterList.addAll(temp);
                adapter.sort(comparator);
                adapter.notifyDataSetChanged();
                //adapter.sort(comparator);
                progress.dismiss();
            }

            private String getListString(List<Long> ids){
                int i = 0;
                String s = "";
                for (Long id : ids) {
                    s += id;
                    if (i != ids.size() - 1) {
                        s += ",";
                    }
                    i++;
                }
                return s;
            }
        };

    }

    public AsyncTask<Void, Void, Boolean> sendMessageTask(final ConversationMessageInfo first){
        return new AsyncTask<Void, Void, Boolean>(){

            @Override
            protected Boolean doInBackground(Void... params){
                String json = new Gson().toJson(first);
                byte[] content = json.getBytes();
                byte[] msgEncrypted = getMsgEncrypted(content);
                return AsimService.getMqttInit().sendMessage(chatTopic, msgEncrypted);
            }

            @Override
            protected void onPostExecute(Boolean state){
                super.onPostExecute(state);
                sendMessage.setEnabled(true);
                if (state) {
                    DbEntryService.updateMessageStatus(first.getId(), ConversationMessageStatus
                            .POST.getCode());
                    adapter.setStatus(first.getId(), ConversationMessageStatus.POST);
                }

            }
        };
    }

    public class SetMsgStatusTask extends Thread{
        private Long id;
        private ConversationMessageStatus status;

        public SetMsgStatusTask(Long id, ConversationMessageStatus status){
            this.id = id;
            this.status = status;
        }

        @Override
        public void run(){
            super.run();
            try {
                //TODO restClient.setConversationMessageStat(id, status);
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }


        private void postExecute(Boolean state){
        }

    }
}
