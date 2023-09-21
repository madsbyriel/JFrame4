package classes;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import interfaces.IFileReader;

public class FileReader implements IFileReader {
    private File file;
    private static String wwwRoot;
    private static Map<String, commandFunction> commands;

    static {
        commands = new HashMap<>();
        commands.put("@include(", (String x) -> includeFunc(x));
        commands.put("@path(", (String x) -> pathFunc(x));
        wwwRoot = System.getProperty("user.dir") + "\\wwwRoot";
    }

    public FileReader(File file) {
        this.file = file;
    }

    @Override
    public int read(byte[] buffer, int start, int length) {
        throw new UnsupportedOperationException("Unimplemented method 'read'");
    }

    @Override
    public int length() {
        throw new UnsupportedOperationException("Unimplemented method 'length'");
    }

    private static void includeFunc(String args) {
        System.out.println("Hello World!");
    }

    private static void pathFunc(String args) {
        System.out.println("PATH FUNCTION");
    }
    
    private interface commandFunction {
        void func(String args);
    }
}
