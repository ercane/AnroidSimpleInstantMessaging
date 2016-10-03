package messaging.mqtt.android.mqtt;

import android.os.Build;

public class MqttConstants {
    public static final String MQTT_CONNECTION_LOST_ACTION = "ACTION_MQTT_CONNECTION_LOST";
    public static final String MQTT_MESSAGE_ARRIVED_ACTION = "ACTION_MQTT_MESSAGE_ARRIVED";
    public static final String MQTT_MSG_RECEIVED_INTENT = "ACTION_MQTT_MESSAGE_ARRIVED";
    public static final String MQTT_MSG_RECEIVED_TOPIC = "com.dalelane.mqtt.MSGRECVD_TOPIC";
    public static final String MQTT_MSG_RECEIVED_MSG = "com.dalelane.mqtt.MSGRECVD_MSGBODY";
    public static final String MQTT_STATUS_INTENT = "ACTION_MQTT_STATUS_CHANGED";
    public static final String MQTT_STATUS_CODE = "com.dalelane.mqtt.STATUS_MSG";
    public static final String MQTT_PING_ACTION = "com.dalelane.mqtt.PING";
    public static final String MQTT_SPLIT_PREFIX = "__-__-__";
    public static final String MQTT_SELF_PREFIX = Build.ID + MQTT_SPLIT_PREFIX;

    public static final String MQTT_PB = "PB" + MQTT_SPLIT_PREFIX;
    public static final String MQTT_PB_SELF = MQTT_PB + MQTT_SELF_PREFIX;
    public static final String MQTT_PB_TAKEN = "PBTAKEN" + MQTT_SPLIT_PREFIX;
    public static final String MQTT_PB_TAKEN_SELF = "PBTAKEN" + MQTT_SPLIT_PREFIX + MQTT_SELF_PREFIX;
    public static final String MQTT_SENT = "SENT" + MQTT_SPLIT_PREFIX;
    public static final String MQTT_MSG_SENT = "SENT" + MQTT_SPLIT_PREFIX + MQTT_SELF_PREFIX;
    public static final String MQTT_RECEIVE_ALL = "RECEIVE" + MQTT_SPLIT_PREFIX;
    public static final String MQTT_RECEIVE_ALL_SELF = MQTT_RECEIVE_ALL + MQTT_SELF_PREFIX;
    public static final String MQTT_READ_ALL = "READ" + MQTT_SPLIT_PREFIX;
    public static final String MQTT_READ_ALL_SELF = MQTT_READ_ALL + MQTT_SELF_PREFIX;
}
