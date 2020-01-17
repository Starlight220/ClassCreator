package logic;

import gui.Controller;

import java.util.HashMap;

public class LogMap extends HashMap<String, String> {
    private static final long serialVersionUID = -1473367291655026433L;

    @Override
    public String put(String key, String value) {
                   System.out.printf("put in map : \t key = %s \t value = %s\n", key, value);
        return super.put(key.replace("null",""), value.replace("null",""));
    }

    @Override
    public String get(Object key) {
        String val = super.get(key);
        if(val == null)
            return "";

        System.err.printf("for key %s , returned value %s to caller %s\n", key, val, Controller.getCaller());
        return val.replace("null","").strip();
    }

    @Override
    public String getOrDefault(Object key, String defaultValue) {
        String val = super.getOrDefault(key, defaultValue);
        System.err.printf("for key %s , returned value %s to caller %s with default %s\n",
                                    key,                val,     Controller.getCaller(),   defaultValue);
        return val;
    }


}
