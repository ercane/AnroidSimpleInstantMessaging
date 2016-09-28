package messaging.mqtt.android.mqtt;

/**
 * Created by eercan on 21.03.2016.
 */
public enum MQTTConnectionStatus {
    INITIAL(1,"Initial"), // initial status
    CONNECTING(2,"Connecting"), // attempting to connect
    CONNECTED(3,"Connected"), // connected
    NOTCONNECTED_WAITINGFORINTERNET(4,"Waiting internet"), // can't connect because the phone
    // does not have Internet access
    NOTCONNECTED_USERDISCONNECT(5,"User Disconnect") ,// user has explicitly requested
    // disconnection
    NOTCONNECTED_DATADISABLED(6,"Data Disabled"), // can't connect because the user
    // has disabled data access
    NOTCONNECTED_UNKNOWNREASON(7,"Unknown Reason"); // failed to connect for some reason ;

    private Integer code;
    private String desc;

    private MQTTConnectionStatus(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static MQTTConnectionStatus get(Integer code) {

        if (code == null) {
            return null;
        }

        for (MQTTConnectionStatus v : values()) {
            if (v.code == code) {
                return v;
            }
        }

        throw new IllegalArgumentException("No matching type: " + code);
    }
}

