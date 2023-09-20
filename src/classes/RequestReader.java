package classes;

import java.io.IOException;
import java.io.InputStream;

import interfaces.IRequestReader;
import interfaces.RequestInfo;

public class RequestReader implements IRequestReader {
    private byte[] buffer;
    private InputStream iStream;

    public RequestReader(InputStream iStream) {
        buffer = new byte[10000];
        this.iStream = iStream;
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
        String[] splits = rawRequest.split("\r\n\r\n");
        String[] lineSplits = splits[0].split("\r\n");
        
        if (splits.length <= 0) return null; 
        String[] headSplit = lineSplits[0].split(" ");

        if (headSplit.length != 3) return null;
        String fullPath = headSplit[1].replace("/", "\\");
        String[] splitPath = fullPath.split("[?]");
        if (splitPath.length < 1) return null;
        requestInfo = new RequestInfo(headSplit[0], splitPath[0], headSplit[2]);
        if (splitPath.length == 2) {
            String queryString = splitPath[1];
            for (String varDef : queryString.split(",")) {
                String[] splitVar = varDef.split("=");
                if (splitVar.length == 2) {
                    requestInfo.putQueryValue(splitVar[0], splitVar[1]);
                    continue;
                }
                if (splitVar.length == 1) {
                    requestInfo.putQueryValue(splitVar[0], null);
                }
            }
        }

        for (int i = 0; i < lineSplits.length; i++) {
            String[] ss = lineSplits[i].split(": ");
            if (ss.length == 2) {
                requestInfo.putParamValue(ss[0], ss[1]);   
            }
        }

        return requestInfo;
    }
}
