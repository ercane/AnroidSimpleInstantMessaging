package messaging.mqtt.android.common.ref;

import java.util.HashSet;
import java.util.Set;

public enum ConversationMessageStatus {
    CREATED(1, "Created"),
    POST(2, "Post"),
    RECEIVED(3, "Received"),
    READ(4, "Read"),
    FAILED(5, "Failed");

    private int code;
    private String desc;

    private ConversationMessageStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    public static ConversationMessageStatus get(Integer code) {

        if (code == null) {
            return null;
        }

        for (ConversationMessageStatus v : values()) {
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

    public static String encode(Set < ConversationMessageStatus > csSet) {
        StringBuilder enc = new StringBuilder();

        for (ConversationMessageStatus cs : csSet) {
            enc.append(cs.getCode()).append("-");
        }

        if (enc.length() > 1) {
            enc.replace(enc.length() - 1, enc.length(), "");
        }

        return enc.toString();
    }

    public static Set < ConversationMessageStatus > decode(String encoded) {

        Set < ConversationMessageStatus > csList = new HashSet < ConversationMessageStatus >();

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

    public static Set < Integer > getCodes(Set < ConversationMessageStatus > csSet) {
        Set < Integer > codeSet = new HashSet < Integer >();

        for (ConversationMessageStatus cs : csSet) {
            codeSet.add(cs.getCode());
        }

        return codeSet;
    }

    public static Set < Integer > getCodes(String encoded) {
        return getCodes(decode(encoded));
    }
}
