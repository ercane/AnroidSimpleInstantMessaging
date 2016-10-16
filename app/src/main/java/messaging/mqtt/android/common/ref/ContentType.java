package messaging.mqtt.android.common.ref;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by mree on 16.10.2016.
 */
public enum ContentType{
    TEXT(1, "Text"),
    PICTURE(2, "Picture");

    private int code;
    private String desc;

    ContentType(int code, String desc){
        this.code = code;
        this.desc = desc;

    }

    public static ContentType get(Integer code){

        if (code == null) {
            return null;
        }

        for (ContentType v : values()) {
            if (v.code == code) {
                return v;
            }
        }
        throw new IllegalArgumentException("No matching type: " + code);
    }

    public static String encode(Set<ContentType> csSet){
        StringBuilder enc = new StringBuilder();

        for (ContentType cs : csSet) {
            enc.append(cs.getCode()).append("-");
        }

        if (enc.length() > 1) {
            enc.replace(enc.length() - 1, enc.length(), "");
        }

        return enc.toString();
    }

    public static Set<ContentType> decode(String encoded){

        Set<ContentType> csList = new HashSet<ContentType>();

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

    public static Set<Integer> getCodes(Set<ContentType> csSet){
        Set<Integer> codeSet = new HashSet<Integer>();

        for (ContentType cs : csSet) {
            codeSet.add(cs.getCode());
        }

        return codeSet;
    }

    public static Set<Integer> getCodes(String encoded){
        return getCodes(decode(encoded));
    }

    public Integer getCode(){
        return code;
    }

    public String getDesc(){
        return desc;
    }


}
