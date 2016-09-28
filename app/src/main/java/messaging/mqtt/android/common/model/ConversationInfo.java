package messaging.mqtt.android.common.model;

public class ConversationInfo {

    private Long id;
    private String roomName;
    private String roomTopic;
    private Integer unreadMsgNumber;

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
}
