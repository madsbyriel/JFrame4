package classes;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import interfaces.IRequestReader;
import interfaces.RequestInfo;

public class RequestReader implements IRequestReader {
    private byte[] buffer;
    private InputStream iStream;

    public RequestReader(InputStream iStream) {
        buffer = new byte[10000];
        this.iStream = iStream;
    }

    private static String[][] getRequestQueryKVP(String fullQueryString) {
        List<String[]> list = new ArrayList<>();
        for (String stringKvp : fullQueryString.split(",")) {
            String[] kvp = stringKvp.split("=");
            if (kvp.length != 2) continue;
            list.add(new String[] {kvp[0], kvp[1]});
        }
        return list.toArray(new String[0][]);
    }

    private static String[][] getRequestParamKVP(String[] lineSplits) {
        List<String[]> list = new ArrayList<>();
        for (String line : lineSplits) {
            String[] split = line.split(": ");
            if (split.length != 2) continue;
            list.add(split);
        }
        return list.toArray(new String[0][]);
    }
    
    private String readRawRequest() throws IOException {
        int readBytes;
        int totalReadBytes = 0;
        while (this.iStream.available() > 0 && (readBytes = this.iStream.read(this.buffer, totalReadBytes, this.buffer.length - totalReadBytes)) != -1) {
            totalReadBytes += readBytes;
        }
        return new String(buffer, 0, totalReadBytes, "UTF-8");
    }

    @Override
    public RequestInfo readRequest() {
        String rawRequest;
        try {
            rawRequest = readRawRequest();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Connection closed probably.");
            return null;
        }

        RequestInfo requestInfo;

        // First split into header and body
        String[] splits = rawRequest.split("\r\n\r\n");

        // Split the header by newlines (all except first line are parameters)
        String[] lineSplits = splits[0].split("\r\n");

        // Split the first, e.g. split GET / HTTP/1.1 into method, URI, version
        String[] headSplit = lineSplits[0].split(" ");
        if (headSplit.length != 3) return null;

        // Convert the URI into a URI, and if it doesn't contain a path, exit
        URI uri = null;
        try {
            uri = new URI(headSplit[1]);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        if (uri == null) return null;
        String path = uri.getPath();
        if (path == null) return null;

        // Create ->                  Method,      path,   version
        requestInfo = new RequestInfo(headSplit[0], path.replace("/", "\\"), headSplit[2]);

        // Append parameter information to the object
        if (lineSplits.length < 1) return null;
        String[][] paramKVPs = getRequestParamKVP(Arrays.copyOfRange(lineSplits, 1, lineSplits.length));
        for (String[] kvp : paramKVPs) {
            requestInfo.putParamValue(kvp[0], kvp[1]);
        }

        // Append query information to the object
        String fullQueryString = uri.getQuery();
        if (fullQueryString == null) return requestInfo;
        String[][] queryKvps = getRequestQueryKVP(fullQueryString);
        for (String[] pair : queryKvps) {
            requestInfo.putQueryValue(pair[0], pair[1]);
        }

        return requestInfo;
    }
}
