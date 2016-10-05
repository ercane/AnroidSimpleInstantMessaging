package messaging.mqtt.android.mqtt;

import android.os.Build;

public class MqttConstants {
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

    public static final String MQTT_ONLINE_CHECK = "ONLINECHECK" + MQTT_SPLIT_PREFIX;
    public static final String MQTT_ONLINE_CHECK_SELF = MQTT_ONLINE_CHECK + MQTT_SELF_PREFIX;
    public static final String MQTT_ONLINE_APPROVE = "ONLINEAPPROVE" + MQTT_SPLIT_PREFIX;
    public static final String MQTT_ONLINE_APPROVE_SELF = MQTT_ONLINE_APPROVE + MQTT_SELF_PREFIX;
    public static final String MQTT_OFFLINE = "OFFLINE" + MQTT_SPLIT_PREFIX;
    public static final String MQTT_OFFLINE_SELF = MQTT_OFFLINE + MQTT_SELF_PREFIX;
}
