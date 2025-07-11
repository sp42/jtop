package me.hatter.tools.commons.io;

import java.io.*;

public class FileBufferedReader extends BufferedReader {

    public FileBufferedReader(File file) throws FileNotFoundException {
        super(new InputStreamReader(new FileInputStream(file)));
    }

    public FileBufferedReader(File file, String charset) throws UnsupportedEncodingException, FileNotFoundException {
        super(new InputStreamReader(new FileInputStream(file), charset));
    }
}
