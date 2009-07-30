package de.nmichael.efa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class NeueGruppeFrame extends JDialog implements ActionListener {
  AuswahlFrame auswahlFrame;
  String gruppeOld;
  boolean neu;
  int editnr;
  Vector mitgliederFelder = new Vector();

  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton saveButton = new JButton();
  JPanel jPanel2 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JTextField gruppenname = new JTextField();
  JScrollPane jScrollPane1 = new JScrollPane();
  JPanel mitgliederPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JPanel jPanel3 = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JButton mehrFelderButton = new JButton();


  // Konstruktor (aus AuswahlFrame)
  public NeueGruppeFrame(AuswahlFrame f, String gruppe, boolean neu, int editnr) {
    super(f);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    auswahlFrame = f;
    this.editnr = editnr;

    if (neu) startNeueGruppe();
    else editiereGruppe(gruppe);
    EfaUtil.pack(this);
    gruppenname.requestFocus();
  }

  void startNeueGruppe() {
    this.gruppeOld = "";
    neu = true; // Neue Gruppe
    this.setTitle("Neue Gruppe hinzuf�gen");
    saveButton.setText("Gruppe hinzuf�gen");
    mehrFelderButton_actionPerformed(null);
  }

  void editiereGruppe(String gruppe) {
    Vector mitglieder = Daten.gruppen.getGruppenMitglieder(gruppe);
    if (mitglieder == null) {
      Dialog.error("Eine Gruppe mit dem Namen "+gruppe+" konnte nicht gefunden werden.");
      cancel();
      return;
    }
    this.gruppeOld = gruppe;
    neu = false; // Eintrag �ndern
    gruppenname.setText(gruppe);
    this.setTitle("Gruppe bearbeiten");
    saveButton.setText("Eintrag �bernehmen");
    for (int i=0; i<mitglieder.size(); i++) {
      GruppenMitglied m = (GruppenMitglied)mitglieder.get(i);
      addField(EfaUtil.getFullName(m.vorname,m.nachname,m.verein));
    }
    mehrFelderButton_actionPerformed(null);
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
  }


  // Initialisierung des Frames
  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
      jPanel1.setLayout(borderLayout1);
      saveButton.setNextFocusableComponent(gruppenname);
      saveButton.setText("Speichern");
      saveButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          saveButton_actionPerformed(e);
        }
    });
      jPanel2.setLayout(gridBagLayout1);
      jLabel1.setText("Gruppe: ");
      mitgliederPanel.setLayout(gridBagLayout2);
      Dialog.setPreferredSize(gruppenname,200,19);
      jPanel3.setLayout(gridBagLayout3);
      mehrFelderButton.setText("Mehr Felder");
      mehrFelderButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          mehrFelderButton_actionPerformed(e);
        }
    });
      jScrollPane1.setPreferredSize(new Dimension(400, 400));
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(saveButton, BorderLayout.SOUTH);
      jPanel1.add(jPanel2,  BorderLayout.NORTH);
      jPanel2.add(jLabel1,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel2.add(gruppenname,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      jPanel1.add(jScrollPane1,  BorderLayout.CENTER);
      jScrollPane1.getViewport().add(mitgliederPanel, null);
      jPanel1.add(jPanel3,  BorderLayout.EAST);
      jPanel3.add(mehrFelderButton,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
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
    if (editnr>0) auswahlFrame.update();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  void addField(String name) {
      JLabel jLabel = new JLabel();
      jLabel.setText("Gruppenmitglied: ");
      JTextField jTextField = new JTextField();
      jTextField.setText(name);
      Dialog.setPreferredSize(jTextField,200,19);
      jTextField.addKeyListener(new java.awt.event.KeyAdapter() {
        public void keyReleased(KeyEvent e) {
          mitglied_keyReleased(e);
        }
      });
      jTextField.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          mitglied_focusLost(e);
        }
      });

      mitgliederPanel.add(jLabel, new GridBagConstraints(0, mitgliederFelder.size(), 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      mitgliederPanel.add(jTextField,  new GridBagConstraints(1, mitgliederFelder.size(), 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      mitgliederFelder.add(jTextField);
  }

  void mehrFelderButton_actionPerformed(ActionEvent e) {
    for (int i=0; i<10; i++) {
      addField("");
    }
    updateFocusControl();
    mitgliederPanel.validate();
    jScrollPane1.validate();
  }

  void updateFocusControl() {
    if (mitgliederFelder == null) return;
    for (int i=0; i<mitgliederFelder.size(); i++) {
      if (i == 0) {
        gruppenname.setNextFocusableComponent((JTextField)mitgliederFelder.get(i));
      } else {
        ((JTextField)mitgliederFelder.get(i-1)).setNextFocusableComponent((JTextField)mitgliederFelder.get(i));
      }
    }
    if (mitgliederFelder.size()>0) {
      ((JTextField)mitgliederFelder.get(mitgliederFelder.size()-1)).setNextFocusableComponent(saveButton);
    }
  }

  void mitglied_keyReleased(KeyEvent e) {
    if (Daten.fahrtenbuch != null && e != null)
      EfaFrame.vervollstaendige((JTextField)e.getSource(),null,Daten.fahrtenbuch.getDaten().mitglieder,e,null,true);
  }

  void mitglied_focusLost(FocusEvent e) {
    if (Daten.efaConfig != null && Daten.efaConfig.popupComplete) AutoCompletePopupWindow.hideWindow();
  }

  void saveButton_actionPerformed(ActionEvent e) {
    String gn = gruppenname.getText().trim();
    if (gn.length() == 0) {
      Dialog.error("Bitte gibt einen Namen f�r die Gruppe ein!");
      gruppenname.requestFocus();
      return;
    }
    if (neu) {
      if (Daten.gruppen.getGruppenMitglieder(gn) != null) {
        Dialog.error("Eine Gruppe dieses Namens existiert bereits. Bitte w�hle einen anderen Gruppennamen.");
        gruppenname.requestFocus();
        return;
      }
    } else {
      if (!gn.equals(gruppeOld)) {
        if (Daten.gruppen.getGruppenMitglieder(gn) != null) {
          Dialog.error("Eine Gruppe dieses Namens existiert bereits. Bitte w�hle einen anderen Gruppennamen.");
          gruppenname.requestFocus();
          return;
        }
        Daten.gruppen.deleteGruppe(gruppeOld);
      }
    }
    Vector mitglieder = new Vector();
    for (int i=0; i<this.mitgliederFelder.size(); i++) {
      JTextField field = (JTextField)mitgliederFelder.get(i);
      if (field.getText().trim().length()>0) {
        String name = field.getText().trim();
        GruppenMitglied m = new GruppenMitglied(EfaUtil.getVorname(name),EfaUtil.getNachname(name),EfaUtil.getVerein(name));
        mitglieder.add(m);
      }
    }
    Daten.gruppen.deleteGruppe(gn);
    Daten.gruppen.setGruppenMitglieder(gn,mitglieder);
    auswahlFrame.doEdit(editnr+1);
    editnr = 0;
    cancel();
  }


}
