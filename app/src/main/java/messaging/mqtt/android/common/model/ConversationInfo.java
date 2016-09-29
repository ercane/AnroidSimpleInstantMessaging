package messaging.mqtt.android.common.model;

import messaging.mqtt.android.common.ref.ConversationStatus;

public class ConversationInfo {

    private Long id;
    private String roomName;
    private String roomTopic;
    private Integer unreadMsgNumber;
    private int isSent;
    private ConversationStatus status;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomTopic() {
        return roomTopic;
    }

    public void setRoomTopic(String roomTopic) {
        this.roomTopic = roomTopic;
    }

    public Integer getUnreadMsgNumber() {
        return unreadMsgNumber;
    }

    public void setUnreadMsgNumber(Integer unreadMsgNumber) {
        this.unreadMsgNumber = unreadMsgNumber;
    }

    public int getIsSent() {
        return isSent;
    }

    public void setIsSent(int isSent) {
        this.isSent = isSent;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationStatus status) {
        this.status = status;
    }
}
