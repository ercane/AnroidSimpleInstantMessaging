package messaging.mqtt.android.database;


public class DbConstants{

    public static final String CHAT_MSGK = "msgk";
    /************************************
     * DATABASE CONSTANTS
     ***********************************/
    public static Integer ASIM_DB_VERSION = 1;
    public static String ASIM_DATABASE_NAME = "ASIM";
    //Chat Table
    public static String CHAT_TABLE_NAME = "Chat";
    public static String CHAT_ID = "_id";
    public static String CHAT_NAME = "name";
    public static String CHAT_STATUS = "status";
    public static String CHAT_TOPIC = "topic";
    public static String CHAT_PBK = "pbk";
    public static String CHAT_PBK_SENT = "pbk_sent";
    public static String CHAT_PRK = "prk";
    public static String CHAT_OPBK = "opbk";
    public static String CHAT_LAST_ACTION_TIME = "lastAction";
    public static String CHAT_UNREAD_MESSAGE = "numberOfUnread";


    //Message Table
    public static String MESSAGE_TABLE_NAME = "ChatMessages";
    public static String MESSAGE_ID = "_id";
    public static String MESSAGE_CHAT_ID = "chatId";
    public static String MESSAGE_OWN_ID = "ownId";
    public static String MESSAGE_TYPE = "type";
    public static String MESSAGE_SENDING_TIME = "sendingTime";
    public static String MESSAGE_CONTENT = "content";
    public static String MESSAGE_STATUS = "status";
    public static String MESSAGE_CONTENT_TYPE = "content_type";

}
