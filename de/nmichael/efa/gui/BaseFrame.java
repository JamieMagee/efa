/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.*;
import de.nmichael.efa.core.items.IItemType;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// @i18n complete
public abstract class BaseFrame extends JFrame implements ActionListener {

    protected Window _parent;
    protected String _title;
    protected boolean _prepared = false;
    protected boolean _inCancel = false;

    private ActionHandler ah;
    protected String KEYACTION_ESCAPE;
    protected String KEYACTION_F1;
    protected JPanel basePanel = new JPanel();
    protected JScrollPane mainScrollPane = new JScrollPane();
    protected JPanel mainPanel = new JPanel();
    protected JButton closeButton;
    protected String helpTopic;
    protected IItemType focusItem;
    protected boolean resultSuccess = false;

    public BaseFrame(Window parent, String title) {
        this._parent = parent;
        this._title = title;
    }

    public boolean prepareDialog() {
        if (_prepared) {
            return false;
        }
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            iniDialogCommon(_title);
            iniDialog();
            iniDialogCommonFinish();
            EfaUtil.pack(this);
            _prepared = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void showMe() {
        showFrame();
    }
    
    public void showFrame() {
        if (!_prepared && !prepareDialog()) {
            return;
        }
        Dialog.setDlgLocation(this);
        Dialog.frameOpened(this);
        if (focusItem != null) {
            focusItem.requestFocus();
        }
        this.setVisible(true);
    }

    public void setRequestFocus(IItemType item) {
        if (item != null && isShowing()) {
            item.requestFocus();
        }
        focusItem = item;
    }

    public JDialog getParentJDialog() {
        if (_parent instanceof JDialog) {
            return (JDialog)_parent;
        }
        return null;
    }

    public Frame getParentFrame() {
        if (_parent instanceof Frame) {
            return (Frame)_parent;
        }
        return null;
    }

    public void _keyAction(ActionEvent evt) {
        if (evt == null || evt.getActionCommand() == null) {
            return;
        }
        
        if (evt.getActionCommand().equals(KEYACTION_ESCAPE)) {
            cancel();
        }
        
        if (evt.getActionCommand().equals(KEYACTION_F1)) {
            Help.showHelp(helpTopic);
        }
    }

    public abstract void keyAction(ActionEvent evt);

    public String addKeyAction(String key) {
        if (ah == null) {
            ah = new ActionHandler(this);
        }
        try {
            return ah.addKeyAction(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW, key, "keyAction");
        } catch (NoSuchMethodException e) {
            Logger.log(Logger.ERROR, Logger.MSG_GUI_ERRORACTIONHANDLER, "Error setting up ActionHandler for "+getClass().getCanonicalName()+": "+e.toString()); // no need to translate
            return null;
        }
    }

    protected void iniDialogCommon(String title) throws Exception {
        helpTopic = getClass().getCanonicalName();
        if (Logger.isTraceOn(Logger.TT_BACKGROUND)) {
            Logger.log(Logger.DEBUG, Logger.MSG_HELP_DEBUGHELPTOPIC, "Help Topic: "+helpTopic);
        }

        KEYACTION_ESCAPE = addKeyAction("ESCAPE");
        KEYACTION_F1 = addKeyAction("F1");

        if (title != null) {
            setTitle(title);
        }
        basePanel.setLayout(new BorderLayout());
    }

    protected void iniDialogCommonFinish() {
        getContentPane().add(basePanel, null);
        basePanel.add(mainScrollPane, BorderLayout.CENTER);

        // intelligent sizing of this Dialog:
        // make it as big as necessary for display without scrollbars (plus some margin),
        // as long as it does not exceed the configured screen size.
        Dimension dim = mainPanel.getPreferredSize();
        Dimension minDim = mainPanel.getMinimumSize();
        if (minDim.width > dim.width) {
            dim.width = minDim.width;
        }
        if (minDim.height > dim.height) {
            dim.height = minDim.height;
        }
        if (dim.width < 100) {
            dim.width = 100;
        }
        if (dim.height < 50) {
            dim.height = 50;
        }
        dim.width  += mainScrollPane.getVerticalScrollBar().getPreferredSize().getWidth() + 40;
        dim.height += mainScrollPane.getHorizontalScrollBar().getPreferredSize().getHeight() + 20;
        mainScrollPane.setPreferredSize(Dialog.getMaxSize(dim));

        mainScrollPane.getViewport().add(mainPanel, null);
    }

    //protected abstract void iniDialog() throws Exception;
    protected void iniDialog() throws Exception {
        
    }

    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            if (cancel()) {
                super.processWindowEvent(e);
                return;
            } else {
                return;
            }
        }
        super.processWindowEvent(e);
    }

    public boolean cancel() {
        _inCancel = true;
        Dialog.frameClosed(this);
        dispose();
        return true;
    }

    public void actionPerformed(ActionEvent e) {
    }

    // may be implemented by subclasses to take action when GUI needs to be set up new
    public void updateGui() {
    }

    protected void setDialogResult(boolean success) {
        this.resultSuccess = success;
    }

    public boolean getDialogResult() {
        return resultSuccess;
    }

    public JScrollPane getScrollPane() {
        return mainScrollPane;
    }

    public static ImageIcon getIcon(String name) {
        try {
            if (name.indexOf("/") < 0) {
                name = Daten.IMAGEPATH + name;
            }
            if (Logger.isTraceOn(Logger.TT_GUI)) {
                Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_GUI_ICONS, "getIcon("+name+")");
            }
            return new ImageIcon(BaseFrame.class.getResource(name));
        } catch(Exception e) {
            if (Logger.isTraceOn(Logger.TT_GUI)) {
                Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_GUI_ICONS, "getIcon("+name+"): no icon found!");
            }
            Logger.logdebug(e);
            return null;
        }
    }

    protected void setIcon(AbstractButton button, ImageIcon icon) {
        if (icon != null) {
            button.setIcon(icon);
        }
    }

    public String getHelpTopic() {
        return helpTopic;
    }

}