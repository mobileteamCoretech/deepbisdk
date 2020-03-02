package com.pl.deepbisdk.localdata;

import java.util.HashMap;
import java.util.Map;

public class BusEvent {
    private String eventType;

    private Map<String, Object> datas = new HashMap<>();

    public BusEvent(String eventType) {
        this.eventType = eventType;
    }

    public String getEventType() {
        return eventType;
    }

    public void putData(String key, Object value) {
        datas.put(key, value);
    }

    public Object getData(String key) {
        return datas.get(key);
    }
}
