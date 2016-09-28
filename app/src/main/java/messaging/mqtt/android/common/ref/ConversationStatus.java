package messaging.mqtt.android.common.ref;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mree on 28.09.2016.
 */
public enum ConversationStatus {
    SUBSCRIBED(1, "Subscribed"),
    UNSUBSCRIBED(2, "Unsubscribed");

    private int code;
    private String desc;

    private ConversationStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    public static ConversationStatus get(Integer code) {

        if (code == null) {
            return null;
        }

        for (ConversationStatus v : values()) {
            if (v.code == code) {
                return v;
            }
        }
        throw new IllegalArgumentException("No matching type: " + code);
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static String encode(Set<ConversationStatus> csSet) {
        StringBuilder enc = new StringBuilder();

        for (ConversationStatus cs : csSet) {
            enc.append(cs.getCode()).append("-");
        }

        if (enc.length() > 1) {
            enc.replace(enc.length() - 1, enc.length(), "");
        }

        return enc.toString();
    }

    public static Set<ConversationStatus> decode(String encoded) {

        Set<ConversationStatus> csList = new HashSet<ConversationStatus>();

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

    public static Set<Integer> getCodes(Set<ConversationStatus> csSet) {
        Set<Integer> codeSet = new HashSet<Integer>();

        for (ConversationStatus cs : csSet) {
            codeSet.add(cs.getCode());
        }

        return codeSet;
    }

    public static Set<Integer> getCodes(String encoded) {
        return getCodes(decode(encoded));
    }
}
