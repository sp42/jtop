package me.hatter.tools.commons.io;

import java.io.*;

public class FilePrintWriter extends PrintWriter {

    public FilePrintWriter(File file) throws FileNotFoundException {
        super(new OutputStreamWriter(new FileOutputStream(file)));
    }

    public FilePrintWriter(File file, String charset) throws UnsupportedEncodingException, FileNotFoundException {
        super(new OutputStreamWriter(new FileOutputStream(file), charset));
    }
}
