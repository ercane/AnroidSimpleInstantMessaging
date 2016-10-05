package messaging.mqtt.android.common.ref;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mree on 06.10.2016.
 */
public enum ReceipentStatus {
    UNKNOWN(0, "Unknown"),
    ONLINE(1, "Online"),
    OFFLINE(2, "Offline");

    private int code;
    private String desc;

    ReceipentStatus(int code, String desc) {
        this.code = code;
        this.desc = desc;

    }

    public static ReceipentStatus get(Integer code) {

        if (code == null) {
            return null;
        }

        for (ReceipentStatus v : values()) {
            if (v.code == code) {
                return v;
            }
        }
        throw new IllegalArgumentException("No matching type: " + code);
    }

    public static String encode(Set<ReceipentStatus> csSet) {
        StringBuilder enc = new StringBuilder();

        for (ReceipentStatus cs : csSet) {
            enc.append(cs.getCode()).append("-");
        }

        if (enc.length() > 1) {
            enc.replace(enc.length() - 1, enc.length(), "");
        }

        return enc.toString();
    }

    public static Set<ReceipentStatus> decode(String encoded) {

        Set<ReceipentStatus> csList = new HashSet<ReceipentStatus>();

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

    public static Set<Integer> getCodes(Set<ReceipentStatus> csSet) {
        Set<Integer> codeSet = new HashSet<Integer>();

        for (ReceipentStatus cs : csSet) {
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
