package interfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestInfo {
    private Map<String, String> map;
    private String version;
    private String path;
    private String method;

    public RequestInfo(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.map = new HashMap<>();
    }

    public Set<String> getKeys() {
        return map.keySet();
    }

    public String getValue(String key) {
        return map.get(key);
    }

    public void putValue(String key, String value) {
        map.put(key, value);
    }

    public int getContentLength() {
        String val = map.get("Content Length");
        if (val == null) return -1;
        return Integer.parseInt(val);
    }

    public String getVersion() {
        return version;
    }

    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }
}
