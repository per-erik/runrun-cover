/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RunRunConsole.java
 *
 * Created on 2011-aug-13, 14:44:51
 */

package net.steamingbeans.runrun.ui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import net.steamingbeans.runrun.CommandExecutor;
import net.steamingbeans.runrun.OutputStreamObserver;

/**
 *
 * @author Per-Erik
 */
public class RunRunConsole extends javax.swing.JFrame {

    private List<String> commandList = new LinkedList<String>();
    private ListIterator<String> commandIterator = commandList.listIterator();
    private CommandListCommand lastCommandListCommandWas;
    private CommandExecutor executor;
    private JPopupMenu popup;
    private boolean errorShowing = false;
    private Runnable inputFieldFocusRunner = new Runnable() {
        @Override
        public void run() {
            inputField.requestFocusInWindow();
        }
    };
    private enum CommandListCommand {
        NEXT,
        PREVIOUS,
    }
    private OutputStreamObserver<String> outputObserver = new OutputStreamObserver<String>() {
        @Override
        public void flushing(final String string) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    output(string);
                }
            });
        }
    };
    private OutputStreamObserver<String> errorObserver = new OutputStreamObserver<String>() {
        @Override
        public void flushing(final String string) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    error(string);
                }
            });
        }
    };

    public RunRunConsole(CommandExecutor executor) {
        super("RunRun Cover");
        initComponents();
        this.executor = executor;
        executor.setErrorObserver(errorObserver);
        executor.setObserver(outputObserver);
        executor.start();
        inputField.requestFocusInWindow();
        //Substance issues handled, only relevant if platform is running Substance 4.2 or later
        outputArea.putClientProperty("substancelaf.colorizationFactor", new Double(1));
        inputField.putClientProperty("substancelaf.colorizationFactor", new Double(1));
        inputPanel.putClientProperty("substancelaf.colorizationFactor", new Double(1));
        errorArea.putClientProperty("substancelaf.colorizationFactor", new Double(1));
        jLabel1.putClientProperty("substancelaf.colorizationFactor", new Double(1));

        commandList.add(""); //Add an initial "empty" command to be able to cycle through to an empty input-line
        createPopup();
        setInputMap(outputArea);
        setInputMap(errorArea);
        setInputMap(inputField);
        setInputMap(getRootPane());
        Dimension screen = new Dimension();
        screen.width = 600;
        screen.height = 800;
        mainPanel.setPreferredSize(screen);
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        outputPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        outputArea = new javax.swing.JTextArea();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        errorArea = new javax.swing.JTextArea();
        inputPanel = new javax.swing.JPanel();
        inputField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N

        mainPanel.setBackground(new java.awt.Color(0, 0, 0));
        mainPanel.setLayout(new java.awt.BorderLayout());

        outputPanel.setLayout(new java.awt.CardLayout());

        jScrollPane1.setBorder(null);

        outputArea.setBackground(new java.awt.Color(0, 0, 0));
        outputArea.setColumns(40);
        outputArea.setEditable(false);
        outputArea.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        outputArea.setForeground(new java.awt.Color(215, 215, 215));
        outputArea.setRows(5);
        outputArea.setTabSize(4);
        outputArea.setBorder(null);
        outputArea.setCaretColor(new java.awt.Color(255, 255, 255));
        outputArea.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        outputArea.setDragEnabled(true);
        outputArea.setSelectionColor(new java.awt.Color(204, 204, 204));
        outputArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                outputAreaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                outputAreaMouseReleased(evt);
            }
        });
        jScrollPane1.setViewportView(outputArea);

        outputPanel.add(jScrollPane1, "card2");

        jScrollPane2.setBorder(null);

        errorArea.setBackground(new java.awt.Color(0, 0, 0));
        errorArea.setColumns(20);
        errorArea.setFont(new java.awt.Font("Lucida Console", 0, 13)); // NOI18N
        errorArea.setForeground(java.awt.Color.red);
        errorArea.setRows(5);
        errorArea.setBorder(null);
        errorArea.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                outputAreaMousePressed(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                outputAreaMouseReleased(evt);
            }
        });
        jScrollPane2.setViewportView(errorArea);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 517, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
        );

        outputPanel.add(jPanel1, "card3");

        mainPanel.add(outputPanel, java.awt.BorderLayout.CENTER);

        inputPanel.setBackground(new java.awt.Color(0, 0, 0));

        inputField.setBackground(new java.awt.Color(0, 0, 0));
        inputField.setFont(new java.awt.Font("Lucida Console", 0, 13));
        inputField.setForeground(new java.awt.Color(215, 215, 215));
        inputField.setBorder(javax.swing.BorderFactory.createEmptyBorder(0, 0, 0, 0));
        inputField.setCaretColor(new java.awt.Color(255, 255, 255));
        inputField.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        inputField.setSelectionColor(new java.awt.Color(204, 204, 204));
        inputField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                inputFieldKeyReleased(evt);
            }
        });

        jLabel1.setBackground(new java.awt.Color(0, 0, 0));
        jLabel1.setFont(new java.awt.Font("Lucida Console", 0, 13));
        jLabel1.setForeground(new java.awt.Color(215, 215, 215));
        jLabel1.setText("r!");

        javax.swing.GroupLayout inputPanelLayout = new javax.swing.GroupLayout(inputPanel);
        inputPanel.setLayout(inputPanelLayout);
        inputPanelLayout.setHorizontalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createSequentialGroup()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(inputField, javax.swing.GroupLayout.DEFAULT_SIZE, 497, Short.MAX_VALUE))
        );
        inputPanelLayout.setVerticalGroup(
            inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(inputPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(jLabel1)
                .addComponent(inputField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        mainPanel.add(inputPanel, java.awt.BorderLayout.PAGE_END);

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void inputFieldKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_inputFieldKeyReleased
        String command = inputField.getText();
        switch(evt.getKeyCode()) {
            case KeyEvent.VK_ENTER:
                processCommand(command);
                break;
            case KeyEvent.VK_DOWN: //next
                String next = processCommandListCommand(CommandListCommand.NEXT);
                inputField.setText(next);
                break;
            case KeyEvent.VK_UP: //previous
                String previous = processCommandListCommand(CommandListCommand.PREVIOUS);
                inputField.setText(previous);
                break;
            default:
                break;
        }
    }//GEN-LAST:event_inputFieldKeyReleased

    private void outputAreaMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_outputAreaMouseReleased
        if(evt.isPopupTrigger()) {
            showPopup(evt.getPoint());
        } else if(outputArea.getSelectedText() == null || outputArea.getSelectedText().isEmpty()) {
            inputField.requestFocusInWindow();
        }
    }//GEN-LAST:event_outputAreaMouseReleased

    private void outputAreaMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_outputAreaMousePressed
        if(evt.isPopupTrigger()) {
            showPopup(evt.getPoint());
        }
    }//GEN-LAST:event_outputAreaMousePressed

    private void showPopup(Point p) {
        if(errorShowing) {
            popup.show(errorArea, p.x, p.y);
        } else {
            popup.show(outputArea, p.x, p.y);
        }
    }

    private void setInputMap(JComponent c) {
        c.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK), "last.card");
        c.getActionMap().put("last.card", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errorShowing = true;
                ((CardLayout)outputPanel.getLayout()).last(outputPanel);
            }
        });
        c.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_O, KeyEvent.CTRL_DOWN_MASK), "first.card");
        c.getActionMap().put("first.card", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                errorShowing = false;
                ((CardLayout)outputPanel.getLayout()).first(outputPanel);
            }
        });
    }

    private void createPopup() {
        popup = new JPopupMenu();
        AbstractAction colorSchemeA = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color bg = Color.BLUE;
                Color fg = Color.WHITE;
                outputArea.setBackground(bg);
                outputArea.setForeground(fg);
                inputField.setBackground(bg);
                inputField.setForeground(fg);
                inputPanel.setBackground(bg);
                jLabel1.setBackground(bg);
                jLabel1.setForeground(fg);
                errorArea.setBackground(bg);
                errorArea.setForeground(Color.BLACK);
            }
        };
        colorSchemeA.putValue(Action.NAME, "Blue-White");
        AbstractAction colorSchemeB = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color bg = Color.BLACK;
                Color fg = Color.GREEN;
                outputArea.setBackground(bg);
                outputArea.setForeground(fg);
                inputField.setBackground(bg);
                inputField.setForeground(fg);
                inputPanel.setBackground(bg);
                jLabel1.setBackground(bg);
                jLabel1.setForeground(fg);
                errorArea.setBackground(bg);
                errorArea.setForeground(Color.RED);
            }
        };
        colorSchemeB.putValue(Action.NAME, "Black-Green");
        AbstractAction colorSchemeC = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Color bg = Color.BLACK;
                Color fg = new Color(215, 215, 215);
                outputArea.setBackground(bg);
                outputArea.setForeground(fg);
                inputField.setBackground(bg);
                inputField.setForeground(fg);
                inputPanel.setBackground(bg);
                jLabel1.setBackground(bg);
                jLabel1.setForeground(fg);
                errorArea.setBackground(bg);
                errorArea.setForeground(Color.RED);
            }
        };
        colorSchemeC.putValue(Action.NAME, "Standard");
        AbstractAction clear = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(errorShowing) {
                    errorArea.setText("");
                } else {
                    outputArea.setText("");
                }
            }
        };
        clear.putValue(Action.NAME, "Clear this screan");

        popup.add(clear);
        popup.addSeparator();
        popup.add(colorSchemeA);
        popup.add(colorSchemeB);
        popup.add(colorSchemeC);
        popup.addPopupMenuListener(new PopupMenuListener() {
            @Override public void popupMenuWillBecomeVisible(PopupMenuEvent e) {}
            @Override public void popupMenuCanceled(PopupMenuEvent e) {}
            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                SwingUtilities.invokeLater(inputFieldFocusRunner);
            }
        });
    }

    private String processCommandListCommand(CommandListCommand listCommand) {
        String command;
        if(listCommand == CommandListCommand.NEXT) {
            if(commandIterator.hasNext()) {
                command = commandIterator.next();
            } else {
                commandIterator = commandList.listIterator(0);
                command = commandIterator.next(); //This is safe because there will always be at least one element in the list
            }
            if(lastCommandListCommandWas == CommandListCommand.PREVIOUS) {
                // (1)
                lastCommandListCommandWas = CommandListCommand.NEXT;
                command = processCommandListCommand(CommandListCommand.NEXT);
            } else {
                lastCommandListCommandWas = CommandListCommand.NEXT;
            }
        } else {
            if(commandIterator.hasPrevious()) {
                command = commandIterator.previous();
            } else {
                commandIterator = commandList.listIterator(commandList.size());
                command = commandIterator.previous(); //This is safe because there will always be at least one element in the list
            }
            if(lastCommandListCommandWas == CommandListCommand.NEXT) {
                // (1)
                lastCommandListCommandWas = CommandListCommand.PREVIOUS;
                command = processCommandListCommand(CommandListCommand.PREVIOUS);
            } else {
                lastCommandListCommandWas = CommandListCommand.PREVIOUS;
            }
        }
        return command;
        /* (1) explanation:
         * If we say "next" and then "previous" we end up at the same place. To
         * avoid this and "skip" the element we're at, say "next->next" if
         * last list command was previous and vice versa.
         * Ex:
         * [A, B, C]
         *  ^
         * next gives
         * [A, B, C]
         *     ^
         * Since iterators work as they do, a call to previous would now yield
         * [A, B, C]
         *     ^
         * Not what we want!
         */
    }

    private void processCommand(String command) {
        inputField.setText("");
        //Handle command list
        if(!command.isEmpty()) { //Dont add empty commands to command list
            String lastGivenCommand = commandList.get(commandList.size() - 1);
            if(!command.equals(lastGivenCommand)) { //Dont add same command directly after each other
                commandList.add(command);
                if(commandList.size() > 10) { //Dont add more than 10 commands to the command list
                    commandList.remove(0);
                }
                commandIterator = commandList.listIterator(); //Reset iterator upon adding new commands to command list
            }
        }
        executor.execute(command);
    }

    private void output(String s) {
        if(s.equals("runrun:cls")) {
            outputArea.setText("");
        } else {
            StringBuilder builder = new StringBuilder(outputArea.getText());
            builder.append(s);
            outputArea.setText(builder.toString());
        }
    }

    private void error(String s) {
        StringBuilder builder = new StringBuilder(errorArea.getText());
        builder.append(s);
        errorArea.setText(builder.toString());
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea errorArea;
    private javax.swing.JTextField inputField;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextArea outputArea;
    private javax.swing.JPanel outputPanel;
    // End of variables declaration//GEN-END:variables

}
