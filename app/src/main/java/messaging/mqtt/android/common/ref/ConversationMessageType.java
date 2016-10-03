package messaging.mqtt.android.common.ref;

import java.util.HashSet;
import java.util.Set;


public enum ConversationMessageType {
    SENT(1, "Sent"),
    RECEIVED(2, "Received");

    private int code;
    private String desc;

    ConversationMessageType(int code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    public static ConversationMessageType get(Integer code) {

        if (code == null) {
            return null;
        }

        for (ConversationMessageType v : values()) {
            if (v.code == code) {
                return v;
            }
        }
        throw new IllegalArgumentException("No matching type: " + code);
    }

    public static String encode(Set<ConversationMessageType> csSet) {
        StringBuilder enc = new StringBuilder();

        for (ConversationMessageType cs : csSet) {
            enc.append(cs.getCode()).append("-");
        }

        if (enc.length() > 1) {
            enc.replace(enc.length() - 1, enc.length(), "");
        }

        return enc.toString();
    }

    public static Set<ConversationMessageType> decode(String encoded) {

        Set<ConversationMessageType> csList = new HashSet<ConversationMessageType>();

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

    public static Set<Integer> getCodes(Set<ConversationMessageType> csSet) {
        Set<Integer> codeSet = new HashSet<Integer>();

        for (ConversationMessageType cs : csSet) {
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
