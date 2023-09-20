package interfaces;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class RequestInfo {
    private Map<String, String> params;
    private Map<String, String> queryParams;
    private String version;
    private String path;
    private String method;

    public RequestInfo(String method, String path, String version) {
        this.method = method;
        this.path = path;
        this.version = version;
        this.params = new HashMap<>();
        this.queryParams = new HashMap<>();
    }

    public Set<String> getParamKeys() {
        return params.keySet();
    }

    public String getParamValue(String key) {
        return params.get(key);
    }

    public void putParamValue(String key, String value) {
        params.put(key, value);
    }

    public int getContentLength() {
        String val = params.get("Content Length");
        if (val == null) return -1;
        return Integer.parseInt(val);
    }

    public Set<String> getQueryKeys() {
        return queryParams.keySet();
    }

    public String getQueryValue(String key) {
        return queryParams.get(key);
    }

    public void putQueryValue(String key, String value) {
        queryParams.put(key, value);
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
