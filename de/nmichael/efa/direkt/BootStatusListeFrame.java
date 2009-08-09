package de.nmichael.efa.direkt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.beans.*;
import java.util.*;
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

public class BootStatusListeFrame extends JDialog implements ActionListener {
  BootStatus bootStatus;
  Admin admin;
  boolean firstclick=false;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  JButton editButton = new JButton();
  JPanel jPanel3 = new JPanel();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable tabelle;
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton resListButton = new JButton();
  JButton schaedenButton = new JButton();


  public BootStatusListeFrame(AdminFrame parent, BootStatus bootStatus, Admin admin) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    this.bootStatus = bootStatus;
    this.admin = admin;
    try {
      jbInit();
      frameIni();
      if (tabelle != null) tabelle.requestFocus();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      cancel(false);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.getHelp(this,this.getClass());
    }
  }


  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    jPanel1.setLayout(borderLayout1);
    okButton.setNextFocusableComponent(editButton);
    okButton.setMnemonic('S');
    okButton.setText("Speichern");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    editButton.setNextFocusableComponent(resListButton);
    editButton.setMnemonic('B');
    editButton.setText("Bearbeiten");
    editButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        editButton_actionPerformed(e);
      }
    });
    this.setTitle("Bootsstatus Liste");
    jPanel2.setLayout(gridBagLayout1);
    resListButton.setNextFocusableComponent(schaedenButton);
    resListButton.setMnemonic('R');
    resListButton.setText("Reservierungsliste");
    resListButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        resListButton_actionPerformed(e);
      }
    });
    schaedenButton.setNextFocusableComponent(okButton);
    schaedenButton.setMnemonic('A');
    schaedenButton.setText("Schadensliste");
    schaedenButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        schaedenButton_actionPerformed(e);
      }
    });
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(okButton, BorderLayout.SOUTH);
    jPanel1.add(jPanel2, BorderLayout.EAST);
    jPanel2.add(editButton,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
    jPanel2.add(resListButton,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
    jPanel2.add(schaedenButton,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 5, 0, 5), 0, 0));
    jPanel1.add(jPanel3, BorderLayout.WEST);
    jPanel1.add(jScrollPane1, BorderLayout.CENTER);
  }

  /**Overridden so we can exit when window is closed*/
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel(false);
    }
    super.processWindowEvent(e);
  }

  /**Close the dialog*/
  void cancel(boolean saved) {
    if (bootStatus.isChanged()) {
      if (Dialog.yesNoDialog("�nderungen speichern",
                             "Die �nderungen an der Bootsstatus-Liste wurden noch nicht gespeichert.\n"+
                             "Sollen sie jetzt gespeichert werden?") == Dialog.YES) {
        if (!this.bootStatus.writeFile()) {
          Logger.log(Logger.INFO,"Admin: Bootsstatus-Liste konnte nicht geschrieben werden.");
          Dialog.error("Bootsstatus-Liste konnte nicht geschrieben werden!");
          return;
        }
        saved = true;
      }
    }
    if (saved) Logger.log(Logger.INFO,"Admin: Bootsstatus-Liste neu geschrieben.");
    else Logger.log(Logger.INFO,"Admin: Alle �nderungen an Bootsstatus-Liste verworfen.");
    Dialog.frameClosed(this);
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  void frameIni() {
    Vector titel = new Vector();
    titel.add("Boot");
    titel.add("Status");
    titel.add("Bemerkung");

    Vector inhalt = new Vector();
    DatenFelder d;
    for (d = bootStatus.getCompleteFirst(); d != null; d = bootStatus.getCompleteNext()) {
      Vector feld = new Vector();
      feld.add(d.get(BootStatus.NAME));
      feld.add(BootStatus.STATUSNAMES[EfaUtil.string2int(d.get(BootStatus.STATUS),0)]);
      feld.add(d.get(BootStatus.BEMERKUNG));
      inhalt.add(feld);
    }
    tabelle = new JTable(inhalt,titel);
    jScrollPane1.getViewport().add(tabelle, null);
    tabelle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    tabelle.setCellEditor(null);
    tabelle.removeEditor();
    tabelle.setColumnSelectionAllowed(false);

    tabelle.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        tabelle_mouseClicked(e);
      }
    });
    tabelle.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        tabelle_propertyChange(e);
      }
    });
  }

  void editButton_actionPerformed(ActionEvent e) {
    if (tabelle.getSelectedRow() < 0) return;
    String name = (String)tabelle.getValueAt(tabelle.getSelectedRow(),0);
    if (name == null) return;
    DatenFelder boot = this.bootStatus.getExactComplete(name);
    if (boot == null) return;

    BootStatusFrame dlg = new BootStatusFrame(this,boot,bootStatus,admin);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  // Edit bei Doppelklick
  void tabelle_mouseClicked(MouseEvent e) {
    firstclick=true;
  }

  // komisch, manchmal scheine diese Methode irgendwie nicht zu ziehen.....
  void tabelle_propertyChange(PropertyChangeEvent e) {
     // System.out.println("hi");
    if (tabelle.isEditing()) {
      if (firstclick) editButton_actionPerformed(null);
      firstclick=false;
    }
  }

  public void editDone(DatenFelder boot) {
    Logger.log(Logger.INFO,"Admin: Bootsstatus f�r Boot '"+boot.get(BootStatus.NAME)+"' auf '"+
                           BootStatus.STATUSNAMES[EfaUtil.string2int(boot.get(BootStatus.STATUS),0)]+"' gesetzt"+
                           ( boot.get(BootStatus.LFDNR).length()>0 ? " mit LfdNr=#"+boot.get(BootStatus.LFDNR) : "") );
    firstclick=false;
    this.bootStatus.delete(boot.get(BootStatus.NAME));
    this.bootStatus.add(boot);
    jScrollPane1.getViewport().remove(tabelle);
    frameIni();
  }

  void okButton_actionPerformed(ActionEvent e) {
    if (!this.bootStatus.writeFile()) {
      Logger.log(Logger.INFO,"Admin: Bootsstatus-Liste konnte nicht geschrieben werden.");
      Dialog.error("Bootsstatus-Liste konnte nicht geschrieben werden!");
      return;
    }
    cancel(true);
  }

  void resListButton_actionPerformed(ActionEvent e) {
    String[] felder  = { "Boot", "Reservierungen" };
    boolean[] select = {  true ,  true            };
    // virtuelle Datenliste erzeugen
    DatenListe dl = new DatenListe("",2,1,false);
    for (DatenFelder d = bootStatus.getCompleteFirst(); d != null; d = bootStatus.getCompleteNext()) {
      if (d.get(BootStatus.RESERVIERUNGEN).length()>0) {
        String s = "";
        Vector res = BootStatus.getReservierungen(d);
        for (int i=0; i<res.size(); i++) {
          Reservierung r = (Reservierung)res.get(i);
          s += (s.length() > 0 ? "<br>" : "") + BootStatus.makeReservierungText(r) + ": "+r.name+" ("+r.grund+")";
        }
        DatenFelder df = new DatenFelder(2);
        df.set(0,d.get(BootStatus.NAME));
        df.set(1,s);
        dl.add(df);
      }
    }
    ListenausgabeFrame dlg = new ListenausgabeFrame(this,"Reservierungsliste",dl,felder,select,0,null,null);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

  void schaedenButton_actionPerformed(ActionEvent e) {
    String[] felder  = { "Boot", "Sch�den" };
    boolean[] select = {  true ,  true            };
    // virtuelle Datenliste erzeugen
    DatenListe dl = new DatenListe("",2,1,false);
    for (DatenFelder d = bootStatus.getCompleteFirst(); d != null; d = bootStatus.getCompleteNext()) {
      if (d.get(BootStatus.BOOTSSCHAEDEN).length()>0) {
        DatenFelder df = new DatenFelder(2);
        df.set(0,d.get(BootStatus.NAME));
        df.set(1,d.get(BootStatus.BOOTSSCHAEDEN));
        dl.add(df);
      }
    }
    ListenausgabeFrame dlg = new ListenausgabeFrame(this,"Schadensliste",dl,felder,select,0,null,null);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(!Dialog.tourRunning);
    dlg.show();
  }

}