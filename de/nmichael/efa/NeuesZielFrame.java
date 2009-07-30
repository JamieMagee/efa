package de.nmichael.efa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileFilter;
import java.io.*;
import java.util.Arrays;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class NeuesZielFrame extends JDialog implements ActionListener {
  BorderLayout borderLayout1 = new BorderLayout();
  JButton SaveButton = new JButton();
  JPanel jPanel1 = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel bereichLabel = new JLabel();
  JTextField ziel = new JTextField();
  JTextField kilometer = new JTextField();
  JTextField bereich = new JTextField();

  EfaFrame efaFrame;
  AuswahlFrame auswahlFrame;
  boolean neu;
  boolean efamain;
  String oldKey;
  String oldkm;
  int editnr=0;
  JCheckBox stegziel = new JCheckBox();
  JLabel gewaesserLabel = new JLabel();
  JTextField gewaesser = new JTextField();

  void startNeuesZiel(String ziel) {
    // �berpr�fen, ob wirklich noch nicht vorhanden
    if (Daten.fahrtenbuch.getDaten().ziele.getExact(ziel) != null) {
      editiereZiel((DatenFelder)Daten.fahrtenbuch.getDaten().ziele.getComplete());
      return;
    }

    neu = true; // Neues Boot
    oldKey = "";
    oldkm = "";
    this.ziel.setText(ziel);
    this.setTitle("Neues Ziel hinzuf�gen");
    SaveButton.setText("Ziel hinzuf�gen");
    this.gewaesser.setVisible(false);
    this.gewaesserLabel.setVisible(false);
  }

  void editiereZiel(DatenFelder d) {
    if (d == null) { // sollte eigentlich nicht passieren
      Logger.log(Logger.ERROR,"NeuesZielFrame.editiereZiel(null): Ziel nicht gefunden --- diesen Fehler bitte an software@nmichael.de melden! Danke!");
      cancel();
      return;
    }

    this.neu = false; // Eintrag �ndern
    oldKey = d.get(Ziele.NAME);
    // Bugfix: Kann passieren, wenn in EfaFrame der Button noch gr�n ist, inzwischen das Ziel aber gel�scht wurde
    if (oldKey == null) { // @neu
      oldKey = "";
      neu = true;
    }

    oldkm = d.get(Ziele.KM);
    ziel.setText(d.get(Ziele.NAME));
    kilometer.setText(d.get(Ziele.KM));
    gewaesser.setText(d.get(Ziele.GEWAESSER));
    bereich.setText(d.get(Ziele.BEREICH));
    stegziel.setSelected(d.get(Ziele.STEGZIEL).equals("+"));
    this.setTitle("Ziel bearbeiten");
    SaveButton.setText("Eintrag �bernehmen");
    if (EfaUtil.zehntelString2Int(d.get(Ziele.KM))>=Daten.WAFAKM) {
      this.gewaesser.setVisible(true);
      this.gewaesserLabel.setVisible(true);
    } else {
      this.gewaesser.setVisible(false);
      this.gewaesserLabel.setVisible(false);
    }
  }

  // Konstruktor (aus EfaFrame)
  public NeuesZielFrame(EfaFrame parent, String ziel, boolean neu) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    efamain = true;
    efaFrame = parent;
    if (neu) startNeuesZiel(ziel);
    else {
      if (Daten.fahrtenbuch.getDaten().ziele.getExact(ziel) != null)
        editiereZiel((DatenFelder)Daten.fahrtenbuch.getDaten().ziele.getComplete());
      else {
        // Bugfix: Kann passieren, wenn in EfaFrame der Button noch gr�n ist, inzwischen das Ziel aber gel�scht wurde
        startNeuesZiel(ziel);
      }
    }
    EfaUtil.pack(this);
    this.ziel.requestFocus();
  }


  // Konstruktor (aus AuswahlFrame)
  public NeuesZielFrame(AuswahlFrame f, DatenFelder d, boolean neu, int editnr) {
    super(f);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    efamain = false;
    auswahlFrame = f;
    this.editnr = editnr;
    if (neu) startNeuesZiel("");
    else editiereZiel(d);
    EfaUtil.pack(this);
    ziel.requestFocus();
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


  private void jbInit() throws Exception {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    SaveButton.setNextFocusableComponent(ziel);
    SaveButton.setMnemonic('H');
    SaveButton.setText("Ziel hinzuf�gen");
    SaveButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        SaveButton_actionPerformed(e);
      }
    });
    this.setTitle("Neues Ziel hinzuf�gen");
    this.getContentPane().setLayout(borderLayout1);
    jPanel1.setLayout(gridBagLayout1);
    jLabel1.setDisplayedMnemonic('Z');
    jLabel1.setLabelFor(ziel);
    jLabel1.setText("Ziel: ");
    jLabel2.setDisplayedMnemonic('K');
    jLabel2.setLabelFor(kilometer);
    jLabel2.setText("Kilometer: ");
    bereichLabel.setDisplayedMnemonic('B');
    bereichLabel.setLabelFor(bereich);
    bereichLabel.setText("Zielbereiche: ");
    ziel.setNextFocusableComponent(kilometer);
    Dialog.setPreferredSize(ziel,200,19);
    kilometer.setNextFocusableComponent(gewaesser);
    Dialog.setPreferredSize(kilometer,200,19);
    kilometer.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        kilometer_focusLost(e);
      }
    });
    bereich.setNextFocusableComponent(stegziel);
    Dialog.setPreferredSize(bereich,200,19);
    bereich.setToolTipText("Liste von Ziffern der Zielbereiche, durch Kommata getrennt.");
    bereich.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        bereich_focusLost(e);
      }
    });
    stegziel.setNextFocusableComponent(SaveButton);
    stegziel.setSelected(true);
    stegziel.setText("Start und Ziel ist eigenes Bootshaus");
    gewaesserLabel.setText("Gew�sser: ");
    gewaesser.setNextFocusableComponent(bereich);
    Dialog.setPreferredSize(gewaesser,200,19);
    gewaesser.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gewaesser_focusLost(e);
      }
    });
    this.getContentPane().add(SaveButton, BorderLayout.SOUTH);
    this.getContentPane().add(jPanel1, BorderLayout.CENTER);
    jPanel1.add(bereichLabel,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    jPanel1.add(ziel,    new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 10), 0, 0));
    jPanel1.add(kilometer,       new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
    jPanel1.add(bereich,    new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));
    jPanel1.add(jLabel2,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    jPanel1.add(jLabel1,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 10, 0, 0), 0, 0));
    jPanel1.add(stegziel,       new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
    jPanel1.add(gewaesserLabel,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel1.add(gewaesser,     new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 10), 0, 0));


   if (Daten.efaConfig != null && !Daten.efaConfig.showBerlinOptions) {
     this.bereich.setVisible(false);
     this.bereichLabel.setVisible(false);
     this.stegziel.setVisible(false);
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
    if (!efamain && editnr>0) auswahlFrame.update();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }


  // Fahrtenbuch aktualisieren, d.h. Ziel "alt" durch Ziel "neu" in
  // allen Eintr�gen ab�ndern
  void updateFb(String oldname, String newname, String oldkm, String newkm) {
    DatenFelder d;
    boolean changed;
    int c=0;
    d = (DatenFelder)Daten.fahrtenbuch.getCompleteFirst();
    if (d != null) do {
      changed = false;

      if (oldname != null && newname != null) { // Name aktualisieren
        if (d.get(Fahrtenbuch.ZIEL).equals(oldname)) {
          d.set(Fahrtenbuch.ZIEL,newname);
          changed = true;
        }
      }

      if (newname != null && oldkm != null && newkm != null) { // Kilometer aktualisieren
        if (d.get(Fahrtenbuch.ZIEL).equals(newname) && d.get(Fahrtenbuch.BOOTSKM).equals(oldkm)) {
          d.set(Fahrtenbuch.BOOTSKM,newkm);
          int mannschkm = EfaUtil.zehntelString2Int(d.get(Fahrtenbuch.MANNSCHKM));
          mannschkm = (mannschkm / EfaUtil.zehntelString2Int(oldkm)) * EfaUtil.zehntelString2Int(newkm); // neue MannschKm
          d.set(Fahrtenbuch.MANNSCHKM,EfaUtil.zehntelInt2String(mannschkm));
          changed = true;
        }
      }


      if (changed) {
        Daten.fahrtenbuch.delete(d.get(Fahrtenbuch.LFDNR));
        Daten.fahrtenbuch.add(d);
        c++;
      }
    } while( (d = (DatenFelder)Daten.fahrtenbuch.getCompleteNext()) != null);

    Dialog.meldung(c+" Eintr�ge angepa�t.");
  }


  // �nderungen �bernehmen, speichern
  void SaveButton_actionPerformed(ActionEvent e) {
    if (this.ziel.getText().trim().length()==0) {
      Dialog.error("Bitte gib einen Zielnamen ein!");
      this.ziel.requestFocus();
      return;
    }

    if (Daten.efaConfig != null && Daten.efaConfig.showBerlinOptions) {
      if (new ZielfahrtFolge(bereich.getText()).getAnzZielfahrten() > 1 && stegziel.isSelected()) {
        Dialog.error("Die Eigenschaft 'Start und Ziel ist eigenes Bootshaus' ist nur f�r\n"+
                     "eint�gige Fahrten gedacht. Du hast aber Zielbereiche f�r mehrere Tage\n"+
                     "(getrennt durch '"+Daten.efaConfig.zielfahrtSeparatorFahrten+"') eingegeben.\n"+
                     "Bitte deaktiviere die Option 'Start und Ziel ist eigenes Bootshaus'.");
        stegziel.requestFocus();
        return;
      }

      if (stegziel.isSelected() && Daten.vereinsConfig != null && Daten.vereinsConfig.zielbereich != null && Daten.vereinsConfig.zielbereich.length() > 0) {
        ZielfahrtFolge zff = new ZielfahrtFolge(bereich.getText().trim());
        boolean eigenerBereichErreicht = false;
        for (int i=0; i<zff.getAnzZielfahrten(); i++) {
          if (zff.getZielfahrt(i).isErreicht(EfaUtil.string2int(Daten.vereinsConfig.zielbereich,0))) eigenerBereichErreicht = true;
        }
        if (eigenerBereichErreicht) {
          Dialog.error(
              "Bei einer Fahrt mit Start und Ziel am eigenen Bootshaus gilt der eigene\n" +
              "Zielbereich " + Daten.vereinsConfig.zielbereich +
              " NICHT als erreicht und darf daher auch NICHT als\n" +
              "Zielbereich angegeben werden. Bitte entferne ihn aus der Aufz�hlung der\n" +
              "Zielbereiche!");
          bereich.requestFocus();
          return;
        }
      }
    }

    String k = ziel.getText().trim();
    String newkm = kilometer.getText().trim();

    if (neu && Daten.fahrtenbuch.getDaten().ziele.getExact(k) != null) {
      Dialog.infoDialog("Fehler","Es existiert bereits ein Ziel gleichen Namens!");
      return;
    }

    if (!neu && !k.equals(oldKey)) {
      if (Daten.fahrtenbuch != null) {

        if (Daten.fahrtenbuch.getDaten().ziele.getExact(k) != null)
          switch(Dialog.yesNoCancelDialog("Frage","Ein Eintrag mit gleichem Namen existiert bereits.\nSoll dieser durch den aktuellen Eintrag ersetzt werden?")) {
            case Dialog.YES: Daten.fahrtenbuch.getDaten().ziele.delete(k); break;
            default: return;
          }

        switch(Dialog.yesNoCancelDialog("Name des Ziels ge�ndert","Sollen im Fahrtenbuch die Zielnamen aller Eintr�ge von Ziel '"+oldKey+"'\n"+
                                                  "nach Ziel '"+k+"' angepa�t werden?")) {
          case Dialog.YES: updateFb(oldKey,k,null,null); break;
          case Dialog.CANCEL: return;
          default: break;
        }
      } else {
        switch(Dialog.yesNoCancelDialog("Warnung","Durch die �nderung kann eine Inkonsistenz zu bestehenden Fahrtenb�chern entstehen.\n"+
                                                  "Soll der Eintrag trotzdem ge�ndert werden?")) {
          case Dialog.YES: break;
          case Dialog.CANCEL: return;
          default: cancel(); return;
        }
      }
    }

    if (!neu && !newkm.equals(oldkm) && Daten.fahrtenbuch != null) {
      switch(Dialog.yesNoCancelDialog("Kilometer ge�ndert","Sollen im Fahrtenbuch die Kilometer aller Eintr�ge mit Ziel '"+k+"'\n"+
                                                "von "+oldkm+" Km nach "+newkm+" Km angepa�t werden?")) {
        case Dialog.YES: updateFb(null,k,oldkm,newkm); break;
        case Dialog.CANCEL: return;
        default: break;
      }
    }

    if (!neu) Daten.fahrtenbuch.getDaten().ziele.delete(oldKey);

    Daten.fahrtenbuch.getDaten().ziele.add(
                    EfaUtil.removeSepFromString(ziel.getText())+"|"+
                    EfaUtil.removeSepFromString(kilometer.getText())+"|"+
                    EfaUtil.removeSepFromString(bereich.getText())+"|"+
                    (stegziel.isSelected() ? "+" : "-")+"|"+
                    EfaUtil.removeSepFromString(gewaesser.getText()));

    if (efamain) {
      efaFrame.ziel.setText(k);
      efaFrame.vervollstaendige(efaFrame.ziel,efaFrame.zielButton,Daten.fahrtenbuch.getDaten().ziele,null,null,false);
      efaFrame.setZielKm();
      efaFrame.ziel.getNextFocusableComponent().requestFocus();
    } else {
      auswahlFrame.doEdit(editnr+1);
      editnr = 0;
    }
    cancel();
  }


  // Kilometer korrigieren
  int kilometer() {
    TMJ hhmm = EfaUtil.string2date(kilometer.getText().trim(),0,0,0); // TMJ mi�braucht f�r die Auswertung von Kilometern
    if (hhmm.monat == 0) kilometer.setText(Integer.toString(hhmm.tag));
    else kilometer.setText(Integer.toString(hhmm.tag)+"."+Integer.toString(EfaUtil.makeDigit(hhmm.monat)));
    return hhmm.tag*10 + hhmm.monat; // Km zur�ckliefern, damit gepr�ft werden kann, ob dies eine mm�gliche Zielfahrt ist (km >= 200)
  }

  void kilometer_focusLost(FocusEvent e) {
    boolean packme = false;
    if (kilometer()<Daten.ZIELFAHRTKM) bereich.setText(""); // nur Zielfahrten mit >= 20 Km zulassen
    if (kilometer()<Daten.WAFAKM) {
      if (this.gewaesser.isVisible()) packme = true;
      this.gewaesser.setVisible(false);
      this.gewaesserLabel.setVisible(false);
      if (packme) this.bereich.requestFocus();
    } else {
      if (!this.gewaesser.isVisible()) packme = true;
      this.gewaesser.setVisible(true);
      this.gewaesserLabel.setVisible(true);
      if (packme) this.gewaesser.requestFocus();
    }
    if (packme) EfaUtil.pack(this);
  }

  void bereich_focusLost(FocusEvent e) {
    String s = bereich.getText().trim();

    // Eingabekorrektur, falls noch aus Gewohnheit der alte Trenner "/" eingegeben wurde
    if (Daten.efaConfig != null && Daten.efaConfig.zielfahrtSeparatorFahrten != null &&
        Daten.efaConfig.zielfahrtSeparatorFahrten.length() == 1 &&
        !Daten.efaConfig.zielfahrtSeparatorFahrten.equals("/")) s = EfaUtil.replace(s,"/",Daten.efaConfig.zielfahrtSeparatorFahrten,true);
    if (kilometer()<Daten.ZIELFAHRTKM) bereich.setText(""); // nur Zielfahrten mit >= 20 Km zulassen
    bereich.setText(new ZielfahrtFolge(s).toString());
    if (new ZielfahrtFolge(bereich.getText()).getAnzZielfahrten() > 1) this.stegziel.setSelected(false);
  }

  void gewaesser_focusLost(FocusEvent e) {
    String g = gewaesser.getText().trim();
    g = EfaUtil.replace(g,";",",",true);
    g = EfaUtil.replace(g,":",",",true);
    String[] a = EfaUtil.kommaList2Arr(g,',');
    for (int i=0; i<a.length; i++) a[i] = a[i].trim();
    Arrays.sort(a);
    g = "";
    for (int i=0; i<a.length; i++) if (a[i].length()>0) g += a[i] + (i+1<a.length ? "," : "");
    gewaesser.setText(g);
  }

}
