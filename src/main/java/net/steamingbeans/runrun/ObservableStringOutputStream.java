/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.steamingbeans.runrun;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Per-Erik
 */
class ObservableStringOutputStream extends OutputStream {
    private ByteArrayOutputStream bos;
    private String charsetName;
    private OutputStreamObserver<String> observer;

    /**
     * Constructs a new observable output stream that will forward all flushes
     * to the OutputStreamObserver. Note that the observer may be
     * called from any thread and must be thread safe. It must also execute
     * as quickly as possible.
     * @param charsetName name of the charset to use for output, e.g. "UTF-8".
     */
    public ObservableStringOutputStream(String charsetName) {
        this.charsetName = charsetName;
        this.bos = new ByteArrayOutputStream();
    }

    public synchronized void setObserver(OutputStreamObserver<String> observer) {
        this.observer = observer;
    }

    @Override
    public synchronized void close() throws IOException {
        observer = null;
    }

    @Override
    public synchronized void flush() throws IOException {
        String s = bos.toString(charsetName); //May throw unsupported encoding exception
        if(observer != null) {
            observer.flushing(s);
        }
        bos.reset();
        bos.flush();
    }

    @Override
    public synchronized void write(int b) {
        bos.write(b);
    }


}
