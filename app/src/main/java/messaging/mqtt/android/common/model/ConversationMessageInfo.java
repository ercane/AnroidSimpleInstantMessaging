/*

 *
 * Project: mcys-server-common
 * Date Created: Dec 22, 2015
 * Created By: eercan
 */
package messaging.mqtt.android.common.model;


import messaging.mqtt.android.common.ref.ConversationMessageStatus;


public class ConversationMessageInfo extends MarkedInfo {
    private Long chatId;
    private String senderId;
    private ConversationMessageStatus status;
    private byte[] content;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public ConversationMessageStatus getStatus() {
        return status;
    }

    public void setStatus(ConversationMessageStatus status) {
        this.status = status;
    }

    public byte[] getContent() {
        return content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
