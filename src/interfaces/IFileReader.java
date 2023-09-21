package interfaces;

public interface IFileReader {
    int read(byte[] buffer, int start, int length);
    int length();
}