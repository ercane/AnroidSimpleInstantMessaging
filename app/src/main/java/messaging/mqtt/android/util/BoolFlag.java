package messaging.mqtt.android.util;

/**
 * Created by eercan on 16.12.2015.
 */
public class BoolFlag {
    boolean flag;
    public BoolFlag(){
        flag=false;
    }

    public BoolFlag(boolean flag) {
        this.flag = flag;
    }

    public boolean getFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }
}
