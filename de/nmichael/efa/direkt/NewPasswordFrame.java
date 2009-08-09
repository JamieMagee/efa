package de.nmichael.efa.direkt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import de.nmichael.efa.Dialog;
import de.nmichael.efa.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class NewPasswordFrame extends JDialog implements ActionListener {
  static String result;
  Window parent;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JPasswordField pwd1 = new JPasswordField();
  JLabel jLabel2 = new JLabel();
  JPasswordField pwd2 = new JPasswordField();
  JLabel titleLabel = new JLabel();


  public NewPasswordFrame(Frame parent, String admin) {
    super(parent);
    this.parent = parent;
    construct(admin);
  }
  public NewPasswordFrame(JDialog parent, String admin) {
    super(parent);
    this.parent = parent;
    construct(admin);
  }
  public NewPasswordFrame(String admin) {
    construct(admin);
  }

  void construct(String admin) {
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    if (admin != null) {
      if (admin.equals(EfaConfig.SUPERADMIN)) titleLabel.setText("Bitte gib ein neues Pa�wort f�r den Super-Administrator '"+admin+"' ein!");
      else titleLabel.setText("Bitte gib ein neues Pa�wort f�r den Administrator '"+admin+"' ein!");
    } else {
      titleLabel.setText("Bitte gib ein neues Pa�wort ein!");
    }

    EfaUtil.pack(this);
    pwd1.requestFocus();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.getHelp(this,this.getClass());
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_2")) { // ENTER
      okButton_actionPerformed(null);
    }
  }

  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1","ENTER"}, new String[] {"keyAction","keyAction","keyAction"});
      jPanel1.setLayout(borderLayout1);
      okButton.setNextFocusableComponent(pwd1);
      okButton.setMnemonic('O');
      okButton.setText("Ok");
      okButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          okButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      jLabel1.setDisplayedMnemonic('P');
      jLabel1.setLabelFor(pwd1);
      jLabel1.setText("Pa�wort (mind. 6 Zeichen): ");
      jLabel2.setDisplayedMnemonic('W');
      jLabel2.setLabelFor(pwd2);
      jLabel2.setText("Pa�wort (Wiederholung): ");
      titleLabel.setText("Bitte gib ein neues Pa�wort f�r ... ein!");
      pwd1.setNextFocusableComponent(pwd2);
      Dialog.setPreferredSize(pwd1,120,17);
      pwd2.setNextFocusableComponent(okButton);
      Dialog.setPreferredSize(pwd2,120,17);
      this.setTitle("Neues Pa�wort eingeben");
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(okButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2, BorderLayout.CENTER);
      jPanel2.add(jLabel1,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));
      jPanel2.add(pwd1,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(jLabel2,       new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 40, 20, 0), 0, 0));
      jPanel2.add(pwd2,      new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
      jPanel2.add(titleLabel,    new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 20, 20, 20), 0, 0));
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  /**Close the dialog*/
  void cancel() {
    Dialog.frameClosed(this);
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  public static String getNewPassword(Window parent, String admin) {
    NewPasswordFrame dlg = null;
    if (parent == null) dlg = new NewPasswordFrame(admin);
    else try {
      dlg = new NewPasswordFrame((JDialog)parent,admin);
    } catch(ClassCastException e) {
      dlg = new NewPasswordFrame((JFrame)parent,admin);
    }
    Dialog.setDlgLocation(dlg,parent);
    return getNewPassword(dlg,admin);
  }

  public static String getNewPassword(Frame parent, String admin) {
    NewPasswordFrame dlg = null;
    if (parent != null) dlg = new NewPasswordFrame(parent,admin);
    else dlg = new NewPasswordFrame(admin);
    Dialog.setDlgLocation(dlg,parent);
    return getNewPassword(dlg,admin);
  }

  public static String getNewPassword(NewPasswordFrame dlg, String admin) {
    result = null;
    dlg.setModal(true);
    dlg.show();
    return result;
  }

  void okButton_actionPerformed(ActionEvent e) {
    String p1 = new String (pwd1.getPassword()).trim();
    String p2 = new String (pwd2.getPassword()).trim();

    if (p1.length()<6) {
      Dialog.error("Das Pa�wort mu� mindestens 6 Zeichen lang sein!");
      pwd1.requestFocus();
      return;
    }

    if (!p1.equals(p2)) {
      Dialog.error("Das Pa�wort im zweiten Feld mu� mit dem im ersten Feld identisch sein!");
      pwd2.requestFocus();
      return;
    }

    result = p1;
    cancel();
  }


}