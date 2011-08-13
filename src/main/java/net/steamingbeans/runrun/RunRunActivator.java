package net.steamingbeans.runrun;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import net.steamingbeans.runrun.ui.RunRunConsole;
import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Hello world!
 *
 */
public class RunRunActivator implements BundleActivator {

    private BundleContext context;
    private ServiceTracker commandProcessorTracker;
    private RunRunConsole console;
    private volatile CommandSession session;
    private final PipedInputStream outReader = new PipedInputStream();
    private long bundleID;

    @Override
    public void start(BundleContext bc) throws Exception {
        context = bc;
        bundleID = context.getBundle().getBundleId();
        commandProcessorTracker = createAndOpenTracker();
    }

    @Override
    public void stop(BundleContext bc) throws Exception {
        commandProcessorTracker.close();
    }

    private ServiceTracker createAndOpenTracker() {
        ServiceTracker tracker = new ServiceTracker(context, CommandProcessor.class.getName(), null) {
            @Override
            public Object addingService(ServiceReference reference) {
                CommandProcessor processor = (CommandProcessor) super.addingService(reference);
                startShell(processor);
                return processor;
            }

            @Override
            public void removedService(ServiceReference reference, Object service) {
                closeConsoleOnEDTAndBlock();
                super.removedService(reference, service);
            }
        };

        tracker.open();
        return tracker;
    }

    private synchronized void startShell(final CommandProcessor processor) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    PrintStream out = new PrintStream(new PipedOutputStream(outReader));
                    session = processor.createSession(System.in, out, out);
                    console = new RunRunConsole(outReader, session, "stop " + bundleID);
                    console.pack();
                    console.setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(RunRunActivator.class.getName()).log(Level.SEVERE, "failure to connect piped streams", ex);
                }
            }
        });
    }

    private void closeConsoleOnEDTAndBlock() {
        Runnable disposer = new Runnable() {
            @Override
            public void run() {
                if (console != null) {
                    console.dispose();
                }
            }
        };
        if(SwingUtilities.isEventDispatchThread()) {
            disposer.run();
        } else {
            try {
                SwingUtilities.invokeAndWait(disposer);
            } catch (InterruptedException ex) {
                Logger.getLogger(RunRunActivator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InvocationTargetException ex) {
                Logger.getLogger(RunRunActivator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            if(session != null) {
                session.close();
            }
        } catch (IllegalStateException ex) {/*Thrown if session already closed*/}
        try {
            outReader.close();
        } catch (IOException ex) {
            Logger.getLogger(RunRunActivator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
