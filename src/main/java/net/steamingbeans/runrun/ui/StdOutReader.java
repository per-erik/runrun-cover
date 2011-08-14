/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.steamingbeans.runrun.ui;

import java.awt.EventQueue;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import javax.naming.event.EventDirContext;
import javax.swing.SwingWorker;
import sun.swing.SwingUtilities2;

/**
 * Extend this class and implement the output methods to get an opportunity to
 * print text and errors on the EDT.
 * @author Per-Erik
 */
abstract class StdOutReader extends SwingWorker<Void, String> {

    private final InputStream toEcho;

    /**
     * Constructs a new StdOutReader that reads from the specified input stream
     * and calls output when new data has arrived. If toEcho is closed, the
     * resulting exception will be sent to output(Throwable). This implies that
     * this class is responsible for closing the stream. Stream closing will
     * occur either when the stream end is reached or when this object is
     * disposed.
     * @param toEcho
     */
    public StdOutReader(InputStream toEcho) {
        this.toEcho = toEcho;
    }

    /**
     * Outputs a string to be viewable by the end user.
     * @param string The string to output.
     */
    public abstract void output(String string);

    /**
     * Outputs a throwable to be viewable by the end user.
     * @param t The throwable to output.
     */
    public abstract void output(Throwable t);

    public final void dispose() {
        cancel(true);
    }

    @Override
    protected final Void doInBackground() throws Exception {
        byte[] data = new byte[1024];
        int bytesRead = 0;
        while ((bytesRead = toEcho.read(data)) != -1) {
            if(isCancelled()) {
                return null;
            }
            publish(new String(data, 0, bytesRead));
        }
        return null;
    }

    @Override
    protected final void process(List<String> chunks) {
        StringBuilder builder = new StringBuilder();
        for(String string : chunks) {
            builder.append(string);
        }
        output(builder.toString());
    }

    @Override
    protected final void done() {
        try {
            get();
        } catch (InterruptedException ex) {
            output(ex);
        } catch (ExecutionException ex) {
            output(ex.getCause());
        } catch(CancellationException ex) {
            //good!
        } finally {
            try {
                toEcho.close();
            } catch (IOException ex) {
                output(ex);
            }
        }
    }
}
