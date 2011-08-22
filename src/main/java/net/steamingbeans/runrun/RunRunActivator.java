package net.steamingbeans.runrun;

import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import net.steamingbeans.runrun.ui.RunRunConsole;
import org.apache.felix.service.command.CommandProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Hello world!
 *
 */
public class RunRunActivator implements BundleActivator {

    private BundleContext context;
    private ServiceTracker commandProcessorTracker;
    private CommandExecutor commandExecutor;
    private RunRunConsole console;
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
                commandExecutor = new CommandExecutor(processor);
                console = new RunRunConsole(commandExecutor, stopCommand);
                console.pack();
                console.setVisible(true);
            }
        });
    }

    private Runnable stopCommand = new Runnable() {
        @Override
        public void run() {
            if(commandExecutor != null) {
                try {
                    context.getBundle().stop();
                } catch (BundleException ex) {
                    //Not much to do if bundle won't stop.
                } catch(Throwable ex) {
                    System.out.println("Banana");
                }
            }
        }
    };

    private void closeConsoleOnEDTAndBlock() {
        Runnable disposer = new Runnable() {
            @Override
            public void run() {
                if (console != null) {
                    console.dispose(false);
                    commandExecutor.close();
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
    }
}
