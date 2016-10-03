package messaging.mqtt.android.tasks;

import java.util.HashSet;
import java.util.Set;


public enum ActivityStatus {
    CREATED(1, "Created"),
    STARTED(2, "Started"),
    PAUSED(3, "Paused"),
    RESUMED(4, "Resumed"),
    STOPPED(5, "Stopped"),
    DESTROYED(5, "Destroyed"),
    RESTARTED(6, "Restarted");

    private int code;
    private String desc;

    ActivityStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    public static ActivityStatus get(Integer code) {

        if (code == null) {
            return null;
        }

        for (ActivityStatus v : values()) {
            if (v.code == code) {
                return v;
            }
        }
        throw new IllegalArgumentException("No matching type: " + code);
    }

    public static String encode(Set<ActivityStatus> csSet) {
        StringBuilder enc = new StringBuilder();

        for (ActivityStatus cs : csSet) {
            enc.append(cs.getCode()).append("-");
        }

        if (enc.length() > 1) {
            enc.replace(enc.length() - 1, enc.length(), "");
        }

        return enc.toString();
    }

    public static Set<ActivityStatus> decode(String encoded) {

        Set<ActivityStatus> csList = new HashSet<ActivityStatus>();

        if (encoded != null) {

            String[] split = encoded.split("-");

            for (String s : split) {
                try {
                    int c = Integer.parseInt(s);
                    csList.add(get(c));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return csList;
    }

    public static Set<Integer> getCodes(Set<ActivityStatus> csSet) {
        Set<Integer> codeSet = new HashSet<Integer>();

        for (ActivityStatus cs : csSet) {
            codeSet.add(cs.getCode());
        }

        return codeSet;
    }

    public static Set<Integer> getCodes(String encoded) {
        return getCodes(decode(encoded));
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
