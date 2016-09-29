package messaging.mqtt.android.act;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import messaging.mqtt.android.R;
import messaging.mqtt.android.act.adapter.ContactListAdapter;
import messaging.mqtt.android.common.model.ConversationInfo;
import messaging.mqtt.android.common.ref.ConversationStatus;
import messaging.mqtt.android.crypt.MsgEncryptOperations;
import messaging.mqtt.android.database.DbConstants;
import messaging.mqtt.android.database.DbEntryService;
import messaging.mqtt.android.mqtt.MqttConstants;
import messaging.mqtt.android.service.AsimService;
import messaging.mqtt.android.tasks.MqttSubscribeTask;
import messaging.mqtt.android.util.BoolFlag;
import messaging.mqtt.android.util.Notification;

public class ContactActivity extends AppCompatActivity {

    public static String CONTACT_ID = "Contact Id";
    public static String CONTACT_TOPIC = "Contact Topic";
    public static String CONTACT_NAME = "Contact Email";
    public static BoolFlag selectMode;
    public static String TAG = ContactActivity.class.getName();
    private static int counter;
    private ProgressBar addProgressBar;
    private TextView addMEnterField;
    private TextView addMsgField;
    private EditText addTopicField;
    private AlertDialog addDialog;
    private Button addContactBtn;
    private ProgressBar joinProgressBar;
    private TextView joinMsgField;
    private EditText joinNameField;
    private EditText joinTopicField;
    private AlertDialog joinDialog;
    private Button joinContactBtn;
    private List<ConversationInfo> contactInfos;
    private ContactListAdapter mAdapter;
    private EditText contactName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        selectMode = new BoolFlag();

        AdapterView.OnItemClickListener itemClickListener = getItemClickListener();

        final ListView listView = (ListView) findViewById(R.id.contactList);
        listView.setOnItemClickListener(itemClickListener);

        mAdapter = new ContactListAdapter(this, R.layout.contact_layout);
        if (counter == 0) {
            fillContactInfo();
            counter++;
        }

        listView.setAdapter(mAdapter);
        listView.setTranscriptMode(ListView.TRANSCRIPT_MODE_NORMAL);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new ListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                final int checkedCount = listView.getCheckedItemCount();
                // Set the CAB title according to total checked items
                mode.setTitle(checkedCount + " Selected");
                // Calls toggleSelection method from ListViewAdapter Class
                mAdapter.toggleSelection(position);
            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.contact_contextual, menu);

                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                SparseBooleanArray selected;
                switch (item.getItemId()) {
                    case R.id.item_delete:
                        // Calls getSelectedIds method from ListViewAdapter Class
                        selected = mAdapter.getSelectedIds();
                        // Captures all selected ids with a loop
                        for (int i = (selected.size() - 1); i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                try {
                                    ConversationInfo mAdapterItem = (ConversationInfo) mAdapter
                                            .getItem(selected.keyAt(i));
                                    // Remove selected items following the ids
                                    mAdapter.remove(mAdapterItem);
                                    DbEntryService.removeChat(mAdapterItem.getId());
                                    Toast.makeText(ContactActivity.this, "Conversation with " + mAdapterItem.getRoomName() + " is removed.", Toast.LENGTH_SHORT);
                                } catch (Exception e) {
                                    Toast.makeText(ContactActivity.this, "Conversation with cannot be removed. " + e.getMessage(), Toast.LENGTH_LONG);
                                }
                            }
                        }
                        // Close CAB
                        mode.finish();
                        return true;
                    default:
                        return false;
                }
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mAdapter.removeSelection();
                selectMode.setFlag(false);
            }


        });
        Notification.clearNotification(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        counter = 0;
    }

    @Override
    protected void onStop() {
        super.onStop();
        counter = 0;
    }


    public AdapterView.OnItemClickListener getItemClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                counter = 0;
                Intent conversation = new Intent(ContactActivity.this, ConversationActivity.class);
                conversation.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                conversation.putExtra(CONTACT_NAME, contactInfos.get(position).getRoomName());
                conversation.putExtra(CONTACT_ID, contactInfos.get(position).getId());
                conversation.putExtra(CONTACT_TOPIC, contactInfos.get(position).getRoomTopic());
                startActivity(conversation);
            }
        };
    }

    private List<ConversationInfo> fillContactInfo() {
        contactInfos = new ArrayList<ConversationInfo>();
        try {
            final ArrayList<HashMap<String, String>> allChats = DbEntryService.getAllChats();


            final ProgressDialog progress = new ProgressDialog(this);
            progress.setMessage("Getting conversations!");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            progress.setProgress(0);
            progress.setCancelable(false);
            progress.show();


            AsyncTask<Void, Void, Boolean> getList = new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    try {
                        for (HashMap<String, String> chat : allChats) {
                            ConversationInfo ci = new ConversationInfo();
                            ci.setId(Long.parseLong(chat.get(DbConstants.CHAT_ID)));
                            ci.setRoomTopic(chat.get(DbConstants.CHAT_TOPIC));
                            ci.setRoomName(chat.get(DbConstants.CHAT_NAME));
                            ci.setIsSent(Integer.parseInt(chat.get(DbConstants.CHAT_PBK_SENT)));
                            int count = DbEntryService.getUnreadNumber(ci.getId());
                            ci.setUnreadMsgNumber(count);
                            contactInfos.add(ci);

                            MqttSubscribeTask subscribeTask = new MqttSubscribeTask(mAdapter, ci);
                            AsimService.getThreadPoolExecutor().submit(subscribeTask);
                        }
                        return true;
                    } catch (Exception e) {
                        return false;
                    }
                }

                @Override
                protected void onPostExecute(Boolean state) {
                    if (state) {
                        mAdapter.clear();
                        for (ConversationInfo ci : contactInfos) {
                            mAdapter.add(ci);
                        }
                    }
                    progress.dismiss();
                    progress.cancel();
                }


            };

            getList.execute();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return contactInfos;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contacts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.addContact:
                addContact();
                break;
            case R.id.joinContact:
                joinContact();
                break;
        }
        return true;
    }


    private void addContact() {
        LayoutInflater inflater;
        View v;
        inflater = getLayoutInflater();
        v = inflater.inflate(R.layout.add_contact_layout, null);

        addProgressBar = (ProgressBar) v.findViewById(R.id.addChatBar);
        addProgressBar.setVisibility(View.GONE);
        addMEnterField = (TextView) v.findViewById(R.id.enterMsg);
        addMsgField = (TextView) v.findViewById(R.id.addContactMsg);
        addMsgField.setVisibility(View.GONE);
        addTopicField = (EditText) v.findViewById(R.id.topic);
        addTopicField.setVisibility(View.GONE);

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setCancelable(true);
        contactName = (EditText) v.findViewById(R.id.name);
        contactName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                addContactBtn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        addContactBtn = (Button) v.findViewById(R.id.btnAddContact);

        addContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    addMsgField.setText("");

                    if (contactName.getText() == null || "".equals(contactName.getText().toString())) {
                        throw new Exception("Name field cannot be null");
                    } else {
                        addContactTask(contactName.getText().toString()).execute();
                    }
                } catch (Exception e) {
                    addMsgField.setText(e.getMessage());
                    addMsgField.setTextColor(Color.RED);
                }
            }
        });

        addDialog = builder.create();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        addDialog.show();
    }

    public AsyncTask<Void, Void, Boolean> addContactTask(final String contactId) {
        return new AsyncTask<Void, Void, Boolean>() {
            ConversationInfo ci;
            String topic;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                addProgressBar.setVisibility(View.VISIBLE);
                addMsgField.clearComposingText();
                addContactBtn.setEnabled(true);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                ci = new ConversationInfo();
                ci.setRoomName(contactId);
                ci.setIsSent(0);
                try {
                    Long time = System.currentTimeMillis();

                    topic = Build.ID + "___" + time;
                    ci.setRoomTopic(topic);

                    return AsimService.getMqttInit().subscribe(topic);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean state) {
                super.onPostExecute(state);
                if (state) {
                    ci.setStatus(ConversationStatus.SUBSCRIBED);
                    addProgressBar.setVisibility(View.GONE);
                    contactName.setVisibility(View.GONE);
                    addMEnterField.setVisibility(View.GONE);
                    addMsgField.setVisibility(View.VISIBLE);
                    addTopicField.setVisibility(View.VISIBLE);
                    addMsgField.setTextColor(Color.GREEN);
                    addMsgField.setText("Oda oluşturuldu. Lütfen katılımcılara aşağıdaki kodu gönderin.");
                    addTopicField.setText(topic);
                    addContactBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            addDialog.dismiss();
                        }
                    });

                    try {
                        byte[] publicKey = MsgEncryptOperations.createSelfKeySpec(getApplicationContext(), ci.getRoomTopic());
                        String pbStr = Base64.encodeToString(publicKey, Base64.DEFAULT);
                        AsimService.getMqttInit().sendMessage(topic, (MqttConstants.MQTT_DH_PUBLIC_KEY + pbStr).getBytes());
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }

                    ci.setId(DbEntryService.saveChat(ci));
                    if (mAdapter != null) {
                        mAdapter.add(ci);
                    }
                    //addDialog.dismiss();
                } else {
                    ci.setStatus(ConversationStatus.UNSUBSCRIBED);
                    addMsgField.setTextColor(Color.RED);
                    addMsgField.setText("Oda oluşturulamadı!");
                    addDialog.dismiss();
                }


            }
        };
    }


    private void joinContact() {
        LayoutInflater inflater;
        View v;
        inflater = getLayoutInflater();
        v = inflater.inflate(R.layout.join_contact_layout, null);

        joinProgressBar = (ProgressBar) v.findViewById(R.id.joinChatBar);
        joinProgressBar.setVisibility(View.GONE);
        joinMsgField = (TextView) v.findViewById(R.id.joinContactMsg);
        joinMsgField.setVisibility(View.GONE);
        joinNameField = (EditText) v.findViewById(R.id.name);
        joinTopicField = (EditText) v.findViewById(R.id.topic);

        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(this);
        builder.setView(v);
        builder.setCancelable(true);
        joinNameField = (EditText) v.findViewById(R.id.name);
        joinNameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                joinContactBtn.setEnabled(true);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        joinContactBtn = (Button) v.findViewById(R.id.btnJoinContact);

        joinContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    joinMsgField.setText("");

                    if (joinNameField.getText() == null || "".equals(joinNameField.getText().toString()) ||
                            joinTopicField.getText() == null || "".equals(joinTopicField.getText().toString())) {
                        throw new Exception("Name or topic field cannot be null");
                    } else {
                        joinContactTask(joinNameField.getText().toString(), joinTopicField.getText().toString()).execute();
                    }
                } catch (Exception e) {
                    joinMsgField.setText(e.getMessage());
                    joinMsgField.setTextColor(Color.RED);
                }
            }
        });

        joinDialog = builder.create();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        joinDialog.show();
    }

    public AsyncTask<Void, Void, Boolean> joinContactTask(final String roomName, final String roomTopic) {
        return new AsyncTask<Void, Void, Boolean>() {
            ConversationInfo ci;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                joinProgressBar.setVisibility(View.VISIBLE);
                joinMsgField.clearComposingText();
                joinContactBtn.setEnabled(false);
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                ci = new ConversationInfo();
                ci.setRoomName(roomName);
                ci.getRoomTopic();
                ci.setIsSent(0);
                try {
                    return AsimService.getMqttInit().subscribe(roomTopic);
                } catch (Exception e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean state) {
                super.onPostExecute(state);
                if (state) {
                    ci.setStatus(ConversationStatus.SUBSCRIBED);
                    joinProgressBar.setVisibility(View.GONE);
                    joinMsgField.setVisibility(View.VISIBLE);
                    addDialog.dismiss();
                    ci.setId(DbEntryService.saveChat(ci));

                    if (mAdapter != null) {
                        mAdapter.add(ci);
                    }

                    try {
                        byte[] publicKey = MsgEncryptOperations.createSelfKeySpec(getApplicationContext(), ci.getRoomTopic());
                        String pbStr = Base64.encodeToString(publicKey, Base64.DEFAULT);
                        AsimService.getMqttInit().sendMessage(ci.getRoomTopic(),
                                (MqttConstants.MQTT_DH_PUBLIC_KEY + pbStr).getBytes());
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                } else {
                    ci.setStatus(ConversationStatus.UNSUBSCRIBED);
                    joinMsgField.setTextColor(Color.RED);
                    joinMsgField.setText("Oda katılım gerçekleşmedi!");
                    addDialog.dismiss();
                }


            }
        };
    }
}
