/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.steamingbeans.runrun;

import java.io.ByteArrayInputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Converter;

/**
 *
 * @author Per-Erik
 */
public class CommandExecutor {

    //All session executions are done on a separate thread. To be "safe", we also
    //create the session on that thread. ("Safe" = I have no idea what the thread
    //rules of a CommandSession is.)
    private CommandSession session;
    private ExecutorService sessionThread = Executors.newSingleThreadExecutor();
    private PrintStream out;
    private PrintStream err;
    private ObservableStringOutputStream outputStream = new ObservableStringOutputStream("UTF-8");
    private ObservableStringOutputStream errorStream = new ObservableStringOutputStream("UTF-8");
    private ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
    private volatile boolean started;
    private CommandProcessor processor;

    public CommandExecutor(final CommandProcessor processor) {
        PrintStream outs;
        PrintStream errs;
        this.processor = processor;
        try {
            outs = new PrintStream(outputStream, false, "UTF-8");
        } catch (UnsupportedEncodingException ex) { outs = new PrintStream(outputStream); /*Never happens but satisfies compiler*/ }
        try {
            errs = new PrintStream(errorStream, false, "UTF-8");
        } catch (UnsupportedEncodingException ex) { errs = new PrintStream(errorStream); /*Never happens but satisfies compiler*/ }
        this.out = outs;
        this.err = errs;
    }

    public void setErrorObserver(OutputStreamObserver<String> errorObserver) {
        errorStream.setObserver(errorObserver);
    }

    public void setObserver(OutputStreamObserver<String> observer) {
        outputStream.setObserver(observer);
    }

    /**
     * Starts this command executor and makes it available for calls to execute
     */
    public synchronized void start() {
        if(!started) {
            session = processor.createSession(in, out, err);
            sessionThread.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        out.println("_____________________________");
                        out.println("   WELCOME TO RUNRUN COVER   ");
                        out.flush();
                    } catch (Exception ex) {
                        Logger.getLogger(CommandExecutor.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
            started = true;
        } else {
            throw new IllegalStateException("Already started or already stopped. Cannot restart a command executor.");
        }
    }

    /**
     * Closes this command executor and cleans up all resources (if any).
     */
    public synchronized void close() {
        sessionThread.submit(new Runnable() {
            @Override
            public void run() {
                System.out.println("BYE!");
                session.close();
            }
        });
        sessionThread.shutdown();
        out.close();
        err.close();
    }

    public final void execute(final String command) {
        if(command.isEmpty()) return;
        try {
            out.println("\nr! " + command);
            Object o = session.execute(command);
            if(o != null) {
                out.println(session.format(o, Converter.INSPECT));
            }
            out.flush();
        } catch (Exception ex) {
            String name = ex.getClass().getName();
            String message = ex.getMessage();
            if(message.contains("Command not found")) {
                out.println("\t" + message);
            } else {
                out.println("\tError, see error log (ctrl-o) for details.");
            }

            err.println(new Date().toString() + " - " + command + " caused:");
            err.println(name + " " + message + ": ");
            for(StackTraceElement elem : ex.getStackTrace()) {
                err.println(elem.toString());
            }
        } finally {
            out.flush();
            err.flush();
        }
    }
}
