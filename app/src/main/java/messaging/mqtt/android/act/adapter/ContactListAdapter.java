package messaging.mqtt.android.act.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import messaging.mqtt.android.R;
import messaging.mqtt.android.common.model.ConversationInfo;
import messaging.mqtt.android.database.DbEntryService;


public class ContactListAdapter extends ArrayAdapter<ConversationInfo> {
    private int layoutResource;
    private List<ConversationInfo> mContacts = new ArrayList<>();
    private LayoutInflater inflater;
    private ListView listView;
    private SparseBooleanArray mSelectedItemsIds;

    public ContactListAdapter(Context context, int resource) {
        super(context, resource);
        layoutResource = resource;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mSelectedItemsIds = new SparseBooleanArray();
    }

    public void setmContacts(List<ConversationInfo> mContacts) {
        this.mContacts = mContacts;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.contact_layout, parent, false);
        }


        ConversationInfo coment = getItem(position);

        RelativeLayout contactLayout = (RelativeLayout) row.findViewById(R.id.contactLayout);
        TextView name = (TextView) row.findViewById(R.id.contactName);
        TextView topic = (TextView) row.findViewById(R.id.contactMail);
        TextView unreadNumber = (TextView) row.findViewById(R.id.msgUnread);
        ImageView image = (ImageView) row.findViewById(R.id.imageView2);

        name.setText(coment.getRoomName());
        topic.setText(coment.getRoomTopic());
        if (coment.getStatus() != null) {
            switch (coment.getStatus()) {
                case SUBSCRIBED:
                    topic.setTextColor(Color.GREEN);
                    break;
                case UNSUBSCRIBED:
                    topic.setTextColor(Color.RED);
                    break;
            }
        }

        if (mSelectedItemsIds.get(position)) {
            contactLayout.setBackgroundResource(R.drawable.home_bckgrnd);
        } else {
            contactLayout.setBackgroundColor(Color.WHITE);
        }
        if (coment.getUnreadMsgNumber() != null && coment.getUnreadMsgNumber() != 0) {
            unreadNumber.setText("(" + coment.getUnreadMsgNumber() + ")");
        }

        return row;
    }


    @Override
    public void add(ConversationInfo object) {
        mContacts.add(object);
        super.add(object);
    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    public void setUnreadMsg() {
        for (ConversationInfo ci : mContacts) {
            int unreadNumber = DbEntryService.getUnreadNumber(ci.getId());
            ci.setUnreadMsgNumber(unreadNumber);
        }
        clear();
        addAll(mContacts);
    }
}
