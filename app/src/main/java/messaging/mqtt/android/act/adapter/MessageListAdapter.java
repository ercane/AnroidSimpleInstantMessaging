package messaging.mqtt.android.act.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import messaging.mqtt.android.R;
import messaging.mqtt.android.act.ConversationActivity;
import messaging.mqtt.android.common.model.ConversationMessageInfo;
import messaging.mqtt.android.common.ref.ContentType;
import messaging.mqtt.android.common.ref.ConversationMessageStatus;
import messaging.mqtt.android.common.ref.ConversationMessageType;
import messaging.mqtt.android.mqtt.MqttConstants;
import messaging.mqtt.android.util.FileHelper;


public class MessageListAdapter extends ArrayAdapter<ConversationMessageInfo>{


    private static final String TAG = MessageListAdapter.class.getSimpleName();
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
    public static SimpleDateFormat hourFormat = new SimpleDateFormat("HH:mm");
    private HashMap<Long, Boolean> decyrptMap = new HashMap<Long, Boolean>();
    private HashMap<Long, Boolean> selectMap = new HashMap<Long, Boolean>();
    private TextView message;
    private HashMap<String, Long> dateList = new HashMap<String, Long>();
    private RelativeLayout wrapper;
    private LinearLayout messageLayout;
    private TextView messageDate;
    private ProgressBar decyrptBar;
    private Context mContext;
    private List<ConversationMessageInfo> messageList;
    //private SparseBooleanArray mSelectedItemsIds;
    private TextView messageHour;
    private ImageView ivContent;
    private ImageView messageStatus;

    public MessageListAdapter(Context context, int textViewResourceId, List<ConversationMessageInfo> messageList){
        super(context, textViewResourceId, messageList);
        mContext = context;
        this.messageList = messageList;
        //mSelectedItemsIds = new SparseBooleanArray();
        decyrptMap.clear();
        setDateList(messageList);
    }

    public HashMap<Long, Boolean> getSelectMap(){
        return selectMap;
    }

    @Override
    public void notifyDataSetChanged(){
        setDateList(messageList);
        super.notifyDataSetChanged();
    }

    public HashMap<Long, Boolean> getDecyrptMap(){
        return decyrptMap;
    }

    public void setDateList(List<ConversationMessageInfo> messages){
        dateList.clear();
        int i = 0;
        HashMap<String, List<ConversationMessageInfo>> groupByDate = new HashMap<String, List<ConversationMessageInfo>>();
        for (ConversationMessageInfo cmi : messages) {
            if (cmi.getUpdatedDate() != null) {
                String date = dateFormat.format(cmi.getUpdatedDate());
                if (!groupByDate.containsKey(date)) {
                    List<ConversationMessageInfo> newList = new ArrayList<>();
                    newList.add(cmi);
                    groupByDate.put(date, newList);
                } else {
                    List<ConversationMessageInfo> list = groupByDate.get(date);
                    list.add(cmi);
                    groupByDate.put(date, list);
                }
            }
        }

        for (String date : groupByDate.keySet()) {
            List<ConversationMessageInfo> list = groupByDate.get(date);
            if (list.size() > 0) {
                Long id = list.get(0).getId();
                dateList.put(date, id);
            }
        }

    }

    @Override
    public void add(ConversationMessageInfo object){
        super.add(object);
        setDateList(messageList);
        //Collections.sort(messageList, ConversationMessageActivity.comparator);
        sort(ConversationActivity.comparator);
    }

    public int getCount(){
        return messageList.size();
    }

    public ConversationMessageInfo getItem(int index){
        if (index >= 0 || index < messageList.size() - 1) {
            return messageList.get(index);
        } else {
            throw new InvalidParameterException("Index must be between 0-" + (messageList.size() - 1));
        }
    }

    public ConversationMessageInfo getItem(Long id){
        for (ConversationMessageInfo cmi : messageList) {
            if (cmi.getId() == id) {
                return cmi;
            }
        }
        return null;
    }

    public View getView(final int position, View convertView, ViewGroup parent){
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(R.layout.message_layout, parent, false);
        }

        wrapper = (RelativeLayout) row.findViewById(R.id.wrapper);
        messageLayout = (LinearLayout) row.findViewById(R.id.messageLayout);
        messageDate = (TextView) row.findViewById(R.id.messageDate);
        messageHour = (TextView) row.findViewById(R.id.messageHour);
        ivContent = (ImageView) row.findViewById(R.id.ivContent);
        messageStatus = (ImageView) row.findViewById(R.id.msgStatus);
        final ConversationMessageInfo coment = getItem(position);

        decyrptBar = (ProgressBar) row.findViewById(R.id.decryptBar);
        message = (TextView) row.findViewById(R.id.comment);
        if (coment.getContentType() == ContentType.TEXT) {
            if (decyrptMap.containsKey(coment.getId()) && decyrptMap.get(coment.getId())) {
                decyrptBar.setVisibility(View.GONE);
                message.setVisibility(View.VISIBLE);
                ivContent.setVisibility(View.GONE);
                try {
                    String msg = new String(coment.getContent(), "UTF-8");
                    message.setText(msg.split(MqttConstants.MQTT_SPLIT_PREFIX)[2]);
                } catch (Exception e) {
                    message.setText(new String(coment.getContent()));
                }
            } else {
                decyrptBar.setVisibility(View.VISIBLE);
                message.setVisibility(View.GONE);
            }
        } else if (coment.getContentType() == ContentType.PICTURE) {
            if (decyrptMap.containsKey(coment.getId()) && decyrptMap.get(coment.getId())) {
                decyrptBar.setVisibility(View.GONE);
                message.setVisibility(View.GONE);
                ivContent.setVisibility(View.VISIBLE);
                try {
                    ivContent.setImageBitmap(FileHelper.getBitmap(coment.getContent()));
                } catch (Exception e) {
                    message.setText(new String(coment.getContent()));
                }
            } else {
                decyrptBar.setVisibility(View.VISIBLE);
                message.setVisibility(View.GONE);
            }
        }
        messageHour.setText(hourFormat.format(coment.getSentReceiveDate()));

        Drawable incoming;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            incoming = mContext.getDrawable(R.drawable.bubble_yellow);
        } else {
            incoming = mContext.getResources().getDrawable(R.drawable.bubble_yellow);
        }

        ColorFilter colorFilter = getColorFilter("#E6E6E6");
        incoming.setColorFilter(colorFilter);

        Drawable outgoing;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            outgoing = mContext.getDrawable(R.drawable.bubble_green);
        } else {
            outgoing = mContext.getResources().getDrawable(R.drawable.bubble_green);
        }
        colorFilter = getColorFilter("#DCF8C6");
        outgoing.setColorFilter(colorFilter);

        String date = dateFormat.format(coment.getSentReceiveDate());
        if (dateList.size() != 0) {
            if (dateList.get(date) == coment.getId()) {
                messageDate.setVisibility(View.VISIBLE);
                messageDate.setText(date);
            } else {
                messageDate.setVisibility(View.GONE);
            }
        }

        if (coment.getType() == ConversationMessageType.RECEIVED) {
            wrapper.setBackground(incoming);
            messageLayout.setGravity(Gravity.LEFT);
            messageLayout.setPadding(10, 5, 50, 5);
            messageStatus.setVisibility(View.GONE);
        } else {
            wrapper.setBackground(outgoing);
            messageLayout.setGravity(Gravity.RIGHT);
            messageLayout.setPadding(50, 5, 10, 5);
            int drawableIndex = 0;
            if (coment.getStatus() != null) {
                switch (coment.getStatus()) {
                    case CREATED:
                        drawableIndex = R.drawable.msg_created;
                        break;
                    case POST:
                        drawableIndex = R.drawable.msg_post;
                        break;
                    case RECEIVED:
                        drawableIndex = R.drawable.msg_received;
                        break;
                    case READ:
                        drawableIndex = R.drawable.msg_read;
                        break;
                    case FAILED:
                        drawableIndex = R.drawable.msg_failed;
                        break;
                }
            }

            try {
                Drawable drawable;
                if (drawableIndex != 0) {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                        drawable = mContext.getDrawable(drawableIndex);
                    } else {
                        drawable = mContext.getResources().getDrawable(drawableIndex);
                    }
                    messageStatus.clearColorFilter();
                    messageStatus.setImageDrawable(drawable);
                    messageStatus.setVisibility(View.VISIBLE);
                }
            } catch (Resources.NotFoundException e) {
                e.printStackTrace();
            }
        }


        //wrapper.setBackground(coment.left ? incoming : outgoing);

        //messageLayout.setGravity(coment.left ? Gravity.LEFT : Gravity.RIGHT);

        if (selectMap.get(coment.getId()) != null && selectMap.get(coment.getId())) {
            messageLayout.setBackgroundResource(R.drawable.home_bckgrnd);
        } else {
            messageLayout.setBackgroundResource(R.drawable.contact_bckgrnd);
        }


        return row;
    }

    @Override
    public void remove(ConversationMessageInfo object){
        int size = messageList.size();
        super.remove(object);
        size = messageList.size();
        notifyDataSetChanged();
    }

    public void toggleSelection(int position){
        ConversationMessageInfo item = getItem(position);
        if (!selectMap.containsKey(item.getId())) {
            selectMap.put(item.getId(), false);
        }
        Boolean value = selectMap.get(item.getId());
        selectMap.put(item.getId(), !value);

        notifyDataSetChanged();
    }

    public void removeSelection(){
        selectMap = new HashMap<>();
        notifyDataSetChanged();
    }

/*    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }*/

    /*public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }
*/
    /*public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }*/
    public List<Long> getSelectedIds(){
        List<Long> list = new ArrayList<>();
        for (Long id : selectMap.keySet()) {
            list.add(id);
        }
        return list;
    }

    public ColorFilter getColorFilter(String color){
        int iColor = Color.parseColor(color);
        int red = (iColor & 0xFF0000) / 0xFFFF;
        int green = (iColor & 0xFF00) / 0xFF;
        int blue = iColor & 0xFF;
        float[] matrix = {0, 0, 0, 0, red,
                0, 0, 0, 0, green,
                0, 0, 0, 0, blue,
                0, 0, 0, 1, 0};
        ColorFilter colorFilter = new ColorMatrixColorFilter(matrix);
        return colorFilter;
    }

    public List<ConversationMessageInfo> getMessageList(){
        return messageList;
    }

    public void setStatus(Long id, ConversationMessageStatus status){
        for (ConversationMessageInfo cmi : messageList) {
            if (cmi.getId().equals(id)) {
                cmi.setStatus(status);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void setId(Long tempId, Long id){
        for (ConversationMessageInfo cmi : messageList) {
            if (tempId.equals(cmi.getId())) {
                cmi.setId(id);
                break;
            }
        }
        setDateList(messageList);
        notifyDataSetChanged();
    }

    public void setStatusToReceive(Long msgId){
        for (ConversationMessageInfo cmi : messageList) {
            if (ConversationActivity.chatId == cmi.getChatId() &&
                    cmi.getStatus() == ConversationMessageStatus.POST &&
                    cmi.getId() == msgId) {
                cmi.setStatus(ConversationMessageStatus.RECEIVED);
            }
        }
        notifyDataSetChanged();
    }

    public void setStatusToRead(){
        for (ConversationMessageInfo cmi : messageList) {
            if (ConversationActivity.chatId == cmi.getChatId() && (
                    cmi.getStatus() == ConversationMessageStatus.RECEIVED ||
                            cmi.getStatus() == ConversationMessageStatus.POST)) {
                cmi.setStatus(ConversationMessageStatus.READ);
            }
        }
        notifyDataSetChanged();
    }
}