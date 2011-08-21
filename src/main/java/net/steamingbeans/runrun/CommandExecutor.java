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
    private final ExecutorService sessionThread = Executors.newSingleThreadExecutor();
    private final PrintStream out;
    private final PrintStream err;
    private final ObservableStringOutputStream outputStream = new ObservableStringOutputStream("UTF-8");
    private final ObservableStringOutputStream errorStream = new ObservableStringOutputStream("UTF-8");
    private final ByteArrayInputStream in = new ByteArrayInputStream(new byte[0]);
    private volatile boolean started;
    private final CommandProcessor processor;

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
            sessionThread.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        session = processor.createSession(in, out, err);
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
                out.flush();
                err.flush();
                out.close();
                err.close();
                session.close();
            }
        });
        sessionThread.shutdown();
    }

    public final void execute(final String command) {
        if(command.isEmpty()) return;
        sessionThread.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    out.println("\nr! " + command);
                    Object o = session.execute(command);
                    if(o != null) {
                        out.println(session.format(o, Converter.INSPECT));
                    }
                } catch (Exception ex) {
                    outputThrowable(ex, command);
                } finally {
                    out.flush();
                    err.flush();
                }
            }
        });
    }

    private void outputThrowable(Throwable t, String causeByCommand) {
        try {
            String name = t.getClass().getName();
            String message = t.getMessage();
            if(message != null && message.contains("Command not found")) {
                out.println("\t" + message);
            } else {
                out.println("\tError, see error log (ctrl-e) for details.");
            }

            err.println(new Date().toString() + " - " + causeByCommand + " caused:");
            err.println(name + " " + message + ": ");
            for(StackTraceElement elem : t.getStackTrace()) {
                err.println(elem.toString());
            }
        } catch (Exception ex) {
            err.print("RunRun Internal error");
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "RunRun Internal error: Could not print following exception: ", t);
            Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "RunRun Internal error: Cause for not printing exception: ", ex);
        }
    }
}
