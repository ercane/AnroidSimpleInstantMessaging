/*

 *
 * Project: mcys-server-common
 * Date Created: Dec 22, 2015
 * Created By: eercan
 */
package messaging.mqtt.android.common.model;


import java.util.Date;

import messaging.mqtt.android.common.ref.ContentType;
import messaging.mqtt.android.common.ref.ConversationMessageStatus;
import messaging.mqtt.android.common.ref.ConversationMessageType;


public class ConversationMessageInfo extends MarkedInfo{
    public Date sentReceiveDate;
    private Long chatId;
    private String ownId;
    private ConversationMessageType type;
    private ConversationMessageStatus status;
    private byte[] content;
    private ContentType contentType;

    public Long getChatId(){
        return chatId;
    }

    public void setChatId(Long chatId){
        this.chatId = chatId;
    }

    public String getOwnId(){
        return ownId;
    }

    public void setOwnId(String ownId){
        this.ownId = ownId;
    }

    public ConversationMessageType getType(){
        return type;
    }

    public void setType(ConversationMessageType type){
        this.type = type;
    }

    public ConversationMessageStatus getStatus(){
        return status;
    }

    public void setStatus(ConversationMessageStatus status){
        this.status = status;
    }

    public byte[] getContent(){
        return content;
    }

    public void setContent(byte[] content){
        this.content = content;
    }

    public Date getSentReceiveDate(){
        return sentReceiveDate;
    }

    public void setSentReceiveDate(Date sentReceiveDate){
        this.sentReceiveDate = sentReceiveDate;
    }

    public ContentType getContentType(){
        return contentType;
    }

    public void setContentType(ContentType contentType){
        this.contentType = contentType;
    }
}
