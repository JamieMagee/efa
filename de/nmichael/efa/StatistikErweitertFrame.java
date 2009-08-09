package de.nmichael.efa;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class StatistikErweitertFrame extends JDialog implements ActionListener {
  JPanel allgemeinPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JButton okButton = new JButton();
  JPanel jPanel2 = new JPanel();
  JPanel balkenPanel = new JPanel();
  JLabel jLabel1 = new JLabel();
  JLabel jLabel2 = new JLabel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JCheckBox zusammenGaesteAndere = new JCheckBox();
  JCheckBox teilzieleEinzeln = new JCheckBox();
  JLabel maxKmLabel = new JLabel();
  JTextField maxKm = new JTextField();
  JLabel maxRudKmLabel = new JLabel();
  JLabel maxStmKmLabel = new JLabel();
  JLabel maxFahrtenLabel = new JLabel();
  JLabel maxKmFahrtLabel = new JLabel();
  JTextField maxRudKm = new JTextField();
  JTextField maxStmKm = new JTextField();
  JTextField maxFahrten = new JTextField();
  JTextField maxKmFahrt = new JTextField();
  Border border1;
  Border border2;
  JCheckBox crop = new JCheckBox();
  JCheckBox kilometerGruppiert = new JCheckBox();
  JCheckBox gaesteVereinsweiseZusammen = new JCheckBox();
  JCheckBox horiz_alle = new JCheckBox();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel dateiPanel = new JPanel();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JCheckBox overwrite = new JCheckBox();
  JCheckBox nurTabelle = new JCheckBox();
  JLabel jLabel3 = new JLabel();
  JCheckBox auchNullWerte = new JCheckBox();
  JLabel maxDauerLabel = new JLabel();
  JLabel maxKmHLabel = new JLabel();
  JTextField maxDauer = new JTextField();
  JTextField maxKmH = new JTextField();
  JPanel weiterePanel = new JPanel();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  JLabel jLabel4 = new JLabel();
  JTextField nurBemerk = new JTextField();
  JLabel jLabel5 = new JLabel();
  JTextField nurBemerkNicht = new JTextField();
  JLabel jLabel6 = new JLabel();
  JPanel wettPanel = new JPanel();
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  JLabel jLabel7 = new JLabel();
  JComboBox wett1 = new JComboBox();
  JLabel jLabel8 = new JLabel();
  JTextField wettjahr1 = new JTextField();
  JComboBox wett2 = new JComboBox();
  JLabel jLabel9 = new JLabel();
  JTextField wettjahr2 = new JTextField();
  JComboBox wett3 = new JComboBox();
  JLabel jLabel10= new JLabel();
  JTextField wettjahr3 = new JTextField();
  JCheckBox wettMitAnforderungen = new JCheckBox();
  JCheckBox nurStegKm = new JCheckBox();
  JCheckBox xmlImmerAlle = new JCheckBox();
  JLabel jLabel11 = new JLabel();
  JTextField nurMindKm = new JTextField();
  JLabel jLabel12 = new JLabel();
  JPanel fahrtenbuchPanel = new JPanel();
  GridBagLayout gridBagLayout6 = new GridBagLayout();
  JLabel jLabel13 = new JLabel();
  JCheckBox fbLfdNrCheckBox = new JCheckBox();
  JCheckBox fbDatumCheckBox = new JCheckBox();
  JCheckBox fbBootCheckBox = new JCheckBox();
  JCheckBox fbStmCheckBox = new JCheckBox();
  JCheckBox fbMannschCheckBox = new JCheckBox();
  JCheckBox fbAbfahrtCheckBox = new JCheckBox();
  JCheckBox fbAnkunftCheckBox = new JCheckBox();
  JCheckBox fbZielCheckBox = new JCheckBox();
  JCheckBox fbBootsKmCheckBox = new JCheckBox();
  JCheckBox fbMannschKmCheckBox = new JCheckBox();
  JCheckBox fbBemerkungenCheckBox = new JCheckBox();
  JCheckBox zusammengefassteWerteOhneBalken = new JCheckBox();
  JCheckBox fbFahrtartInBemerkungenCheckBox = new JCheckBox();
  JLabel jLabel14 = new JLabel();
  JLabel jLabel15 = new JLabel();
  JTextField fileExecBefore = new JTextField();
  JTextField fileExecAfter = new JTextField();
  JCheckBox mitglnrStattName = new JCheckBox();
  JCheckBox fbZielbereichInBemerkungenCheckBox = new JCheckBox();
  JLabel jLabel16 = new JLabel();
  JTextField nurFb1 = new JTextField();
  JTextField nurFb2 = new JTextField();
  JButton nurFb1Button = new JButton();
  JButton nurFb2Button = new JButton();
  JLabel jLabel17 = new JLabel();
  JTextField nurBooteFuerGruppe = new JTextField();
  DatenListe gruppen = null;
  JCheckBox alleZielfahrtenAusgeben = new JCheckBox();


  public StatistikErweitertFrame(StatistikFrame parent) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
//    Dialog.frameOpened(this); // hier nicht, da Frame konstruiert wird, ohne sichtbar zu sein
    try {
      jbInit();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    this.setSize((int)this.getSize().getWidth()+50,(int)this.getSize().getHeight()+50);
//    okButton.requestFocus(); (wird bei show() auf StatistikFrame aufgerufen)
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

    border1 = BorderFactory.createEmptyBorder(10,10,10,10);
    border2 = BorderFactory.createEmptyBorder(10,10,10,10);
    this.setTitle("Erweiterte Ausgabeeinstellungen");
    allgemeinPanel.setLayout(borderLayout1);
    okButton.setNextFocusableComponent(jTabbedPane1);
    okButton.setMnemonic('O');
    okButton.setText("OK");
    okButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        okButton_actionPerformed(e);
      }
    });
    jLabel1.setText("Ausgabevarianten");
    jLabel2.setText("maximale Balkengr��e");
    jPanel2.setLayout(gridBagLayout1);
    balkenPanel.setLayout(gridBagLayout2);
    zusammenGaesteAndere.setText("G�ste & Andere zusammenfassen (am Ende)");
    zusammenGaesteAndere.setNextFocusableComponent(gaesteVereinsweiseZusammen);
    zusammenGaesteAndere.setToolTipText("\'G�ste\' und \'Andere\' zu je einer Person zusammenfassen");
    zusammenGaesteAndere.setMnemonic('G');
    teilzieleEinzeln.setText("Teilziele einzeln");
    teilzieleEinzeln.setNextFocusableComponent(kilometerGruppiert);
    teilzieleEinzeln.setToolTipText("Mit \'+\' getrennte Teilziele einzeln auflisten");
    teilzieleEinzeln.setMnemonic('T');
    maxKmLabel.setForeground(Color.black);
    maxKmLabel.setDisplayedMnemonic('K');
    maxKmLabel.setLabelFor(maxKm);
    maxKmLabel.setText("Kilometer: ");
    maxRudKmLabel.setForeground(Color.black);
    maxRudKmLabel.setDisplayedMnemonic('R');
    maxRudKmLabel.setLabelFor(maxRudKm);
    maxRudKmLabel.setText("Ruderkilometer: ");
    maxStmKmLabel.setForeground(Color.black);
    maxStmKmLabel.setDisplayedMnemonic('S');
    maxStmKmLabel.setLabelFor(maxStmKm);
    maxStmKmLabel.setText("Steuerkilometer: ");
    maxFahrtenLabel.setForeground(Color.black);
    maxFahrtenLabel.setDisplayedMnemonic('F');
    maxFahrtenLabel.setLabelFor(maxFahrten);
    maxFahrtenLabel.setText("Fahrten: ");
    maxKmFahrtLabel.setForeground(Color.black);
    maxKmFahrtLabel.setDisplayedMnemonic('M');
    maxKmFahrtLabel.setLabelFor(maxKmFahrt);
    maxKmFahrtLabel.setText("Km / Fahrt: ");
    maxKmFahrt.setNextFocusableComponent(maxDauer);
    maxKmFahrt.setPreferredSize(new Dimension(50, 19));
    maxKmFahrt.setToolTipText("maximale Balkengr��e in Pixel");
    maxKmFahrt.setText("200");
    maxFahrten.setNextFocusableComponent(maxKmFahrt);
    maxFahrten.setPreferredSize(new Dimension(50, 19));
    maxFahrten.setToolTipText("maximale Balkengr��e in Pixel");
    maxFahrten.setText("200");
    maxStmKm.setNextFocusableComponent(maxFahrten);
    maxStmKm.setPreferredSize(new Dimension(50, 19));
    maxStmKm.setToolTipText("maximale Balkengr��e in Pixel");
    maxStmKm.setText("200");
    maxRudKm.setNextFocusableComponent(maxStmKm);
    maxRudKm.setPreferredSize(new Dimension(50, 19));
    maxRudKm.setToolTipText("maximale Balkengr��e in Pixel");
    maxRudKm.setText("200");
    maxKm.setNextFocusableComponent(maxRudKm);
    maxKm.setPreferredSize(new Dimension(50, 19));
    maxKm.setToolTipText("maximale Balkengr��e in Pixel");
    maxKm.setText("200");
    maxKm.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        validateInt(maxKm);
      }
    });
    maxRudKm.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        validateInt(maxRudKm);
      }
    });
    maxStmKm.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        validateInt(maxStmKm);
      }
    });
    maxFahrten.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        validateInt(maxFahrten);
      }
    });
    maxKmFahrt.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        validateInt(maxKmFahrt);
      }
    });
    jPanel2.setBorder(border1);
    balkenPanel.setBorder(border2);
    balkenPanel.setNextFocusableComponent(crop);
    balkenPanel.setToolTipText("Balken, die zu gro� werden, auf eine geringere Gr��e stutzen");
    crop.setNextFocusableComponent(maxKm);
    crop.setToolTipText("Balken, die bei 100% l�nger als der angegebene Wert sind, werden " +
    "umgebrochen");
    crop.setMnemonic('G');
    crop.setText("zu gro�e Balken spalten");
    kilometerGruppiert.setSelected(true);
    kilometerGruppiert.setText("Entfernungen gruppieren");
    kilometerGruppiert.setNextFocusableComponent(auchNullWerte);
    kilometerGruppiert.setToolTipText("Kilometerentfernungen bei der Ausgabe zu 5-Km-Gruppen zusammenfassen");
    kilometerGruppiert.setMnemonic('E');
    gaesteVereinsweiseZusammen.setText("G�ste vereinsweise zusammenfassen");
    gaesteVereinsweiseZusammen.setNextFocusableComponent(teilzieleEinzeln);
    gaesteVereinsweiseZusammen.setToolTipText("G�ste unter ihren Vereinsnamen als zusammenfassen");
    gaesteVereinsweiseZusammen.setMnemonic('V');
    horiz_alle.setText("horizontal immer alle (Wer mit Wem)");
    horiz_alle.setNextFocusableComponent(xmlImmerAlle);
    horiz_alle.setToolTipText("auf der Horizontalen immer alle Namen anzeigen");
    horiz_alle.setMnemonic('A');
    dateiPanel.setLayout(gridBagLayout3);
    overwrite.setNextFocusableComponent(nurTabelle);
    overwrite.setToolTipText("beim �berschreiben von Dateien warnen");
    overwrite.setMnemonic('W');
    overwrite.setText("vor �berschreiben vorhandener Dateien warnen");
    nurTabelle.setNextFocusableComponent(fileExecBefore);
    nurTabelle.setToolTipText("in bestehenden HTML-Dateien das Ger�st der Datei �bernehmen");
    nurTabelle.setMnemonic('E');
    nurTabelle.setText("in existierenden HTML-Dateien nur Tabelle ersetzen");
    jLabel3.setText("Ausgabe in Dateien");
    auchNullWerte.setNextFocusableComponent(horiz_alle);
    auchNullWerte.setToolTipText("auch Eintr�ge, die im gew�hlten Zeitraum nicht vorkommen, mit ausgeben");
    auchNullWerte.setMnemonic('N');
    auchNullWerte.setText("immer auch alle Null-Werte ausgeben");
    maxDauerLabel.setForeground(Color.black);
    maxDauerLabel.setDisplayedMnemonic('D');
    maxDauerLabel.setLabelFor(maxDauer);
    maxDauerLabel.setText("Dauer (Stunden): ");
    maxKmHLabel.setForeground(Color.black);
    maxKmHLabel.setDisplayedMnemonic('H');
    maxKmHLabel.setLabelFor(maxKmH);
    maxKmHLabel.setText("Km / h: ");
    maxDauer.setNextFocusableComponent(maxKmH);
    maxDauer.setPreferredSize(new Dimension(50, 17));
    maxDauer.setText("200");
    maxKmH.setNextFocusableComponent(zusammengefassteWerteOhneBalken);
    maxKmH.setPreferredSize(new Dimension(50, 17));
    maxKmH.setText("200");
    weiterePanel.setLayout(gridBagLayout4);
    jLabel4.setDisplayedMnemonic('E');
    jLabel4.setLabelFor(nurBemerk);
    jLabel4.setText("Bemerkungsfeld enth�lt: ");
    nurBemerk.setNextFocusableComponent(nurBemerkNicht);
    nurBemerk.setPreferredSize(new Dimension(200, 17));
    nurBemerk.setToolTipText("Text, den das Bemerkungsfeld enthalten mu�");
    nurBemerk.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        nurBemerk_keyPressed(e);
      }
    });
    jLabel5.setDisplayedMnemonic('N');
    jLabel5.setLabelFor(nurBemerkNicht);
    jLabel5.setText("Bemerkungsfeld enth�lt nicht: ");
    jLabel6.setText("Nur Fahrten mit folgenden Eigenschaften auswerten:");
    nurBemerkNicht.setNextFocusableComponent(nurStegKm);
    nurBemerkNicht.setPreferredSize(new Dimension(200, 17));
    nurBemerkNicht.setToolTipText("Text, den das Bemerkungsfeld nicht enthalten darf");
    nurBemerkNicht.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyPressed(KeyEvent e) {
        nurBemerkNicht_keyPressed(e);
      }
    });
    allgemeinPanel.setNextFocusableComponent(zusammenGaesteAndere);
    dateiPanel.setNextFocusableComponent(overwrite);
    weiterePanel.setNextFocusableComponent(nurBemerk);
    wettPanel.setLayout(gridBagLayout5);
    jLabel7.setText("Zus�tzlich noch folgende Wettbewerbe auswerten:");
    jLabel8.setText("Wettbewerbsjahr: ");
    jLabel9.setText("Wettbewerbsjahr: ");
    jLabel10.setText("Wettbewerbsjahr: ");
    wettjahr1.setNextFocusableComponent(wett2);
    wettjahr1.setPreferredSize(new Dimension(100, 17));
    wettjahr1.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        wettjahr1_focusLost(e);
      }
    });
    wettjahr2.setNextFocusableComponent(wett3);
    wettjahr2.setPreferredSize(new Dimension(100, 17));
    wettjahr2.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        wettjahr2_focusLost(e);
      }
    });
    wettjahr3.setNextFocusableComponent(wettMitAnforderungen);
    wettjahr3.setPreferredSize(new Dimension(100, 17));
    wettjahr3.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        wettjahr3_focusLost(e);
      }
    });
    wett1.setNextFocusableComponent(wettjahr1);
    wett1.setPreferredSize(new Dimension(250, 22));
    wett1.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        wett1_itemStateChanged(e);
      }
    });
    wett2.setNextFocusableComponent(wettjahr2);
    wett2.setPreferredSize(new Dimension(250, 22));
    wett2.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        wett2_itemStateChanged(e);
      }
    });
    wett3.setNextFocusableComponent(wettjahr3);
    wett3.setPreferredSize(new Dimension(250, 22));
    wett3.addItemListener(new java.awt.event.ItemListener() {
      public void itemStateChanged(ItemEvent e) {
        wett3_itemStateChanged(e);
      }
    });
    wettMitAnforderungen.setNextFocusableComponent(alleZielfahrtenAusgeben);
    wettMitAnforderungen.setText("auch geforderte Kilometer und Zusatzbedingungen ausgeben");
    nurStegKm.setText("nur Fahrten mit Start und Ziel Bootshaus (\'Steg-Km\')");
    nurStegKm.setNextFocusableComponent(nurMindKm);
    nurStegKm.setToolTipText("nur Fahrten auswerten, deren Start und Ziel das eigene Bootshaus ist");
    nurStegKm.setMnemonic('Z');
    xmlImmerAlle.setNextFocusableComponent(mitglnrStattName);
    xmlImmerAlle.setToolTipText("Zur Weiterverarbeitung aller ausgewerteten Daten");
    xmlImmerAlle.setMnemonic('X');
    xmlImmerAlle.setText("in XML-Ausgabe immer alle Felder ausgeben");
    jLabel11.setDisplayedMnemonic('F');
    jLabel11.setLabelFor(nurMindKm);
    jLabel11.setText("nur Fahrten mit mind. ");
    jLabel12.setText(" Kilometern");
    nurMindKm.setNextFocusableComponent(nurBooteFuerGruppe);
    nurMindKm.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        nurMindKm_focusLost(e);
      }
    });
    fahrtenbuchPanel.setLayout(gridBagLayout6);
    jLabel13.setText("Folgende Felder bei Statistikart \'Fahrtenbuch\' ausgeben:");
    fbLfdNrCheckBox.setNextFocusableComponent(fbDatumCheckBox);
    fbLfdNrCheckBox.setSelected(true);
    fbLfdNrCheckBox.setText("Lfd.Nr.");
    fbDatumCheckBox.setNextFocusableComponent(fbBootCheckBox);
    fbDatumCheckBox.setSelected(true);
    fbDatumCheckBox.setText("Datum");
    fbBootCheckBox.setNextFocusableComponent(fbStmCheckBox);
    fbBootCheckBox.setSelected(true);
    fbBootCheckBox.setText("Boot");
    fbStmCheckBox.setNextFocusableComponent(fbMannschCheckBox);
    fbStmCheckBox.setSelected(true);
    fbStmCheckBox.setText("Steuermann");
    fbMannschCheckBox.setNextFocusableComponent(fbAbfahrtCheckBox);
    fbMannschCheckBox.setSelected(true);
    fbMannschCheckBox.setText("Mannschaft");
    fbAbfahrtCheckBox.setNextFocusableComponent(fbAnkunftCheckBox);
    fbAbfahrtCheckBox.setSelected(true);
    fbAbfahrtCheckBox.setText("Abfahrt");
    fbAnkunftCheckBox.setNextFocusableComponent(fbZielCheckBox);
    fbAnkunftCheckBox.setSelected(true);
    fbAnkunftCheckBox.setText("Ankunft");
    fbZielCheckBox.setNextFocusableComponent(fbBootsKmCheckBox);
    fbZielCheckBox.setSelected(true);
    fbZielCheckBox.setText("Ziel");
    fbBootsKmCheckBox.setNextFocusableComponent(fbMannschKmCheckBox);
    fbBootsKmCheckBox.setSelected(true);
    fbBootsKmCheckBox.setText("Boots-Km");
    fbMannschKmCheckBox.setNextFocusableComponent(fbBemerkungenCheckBox);
    fbMannschKmCheckBox.setSelected(true);
    fbMannschKmCheckBox.setText("Mannsch.-Km");
    fbBemerkungenCheckBox.setNextFocusableComponent(fbFahrtartInBemerkungenCheckBox);
    fbBemerkungenCheckBox.setSelected(true);
    fbBemerkungenCheckBox.setText("Bemerkungen");
    jTabbedPane1.setMinimumSize(new Dimension(540, 244));
    jTabbedPane1.setPreferredSize(new Dimension(540, 244));
    zusammengefassteWerteOhneBalken.setNextFocusableComponent(okButton);
    zusammengefassteWerteOhneBalken.setText("zusammengefa�te Werte ohne Balken");
    fbFahrtartInBemerkungenCheckBox.setNextFocusableComponent(fbZielbereichInBemerkungenCheckBox);
    fbFahrtartInBemerkungenCheckBox.setText("Im Feld Bemerkungen auch die Fahrtart ausgeben");
    jLabel14.setDisplayedMnemonic('V');
    jLabel14.setLabelFor(fileExecBefore);
    jLabel14.setText("vor Erstellen der Datei Kommando ausf�hren: ");
    jLabel15.setDisplayedMnemonic('N');
    jLabel15.setLabelFor(fileExecAfter);
    jLabel15.setText("nach Erstellen der Datei Kommando ausf�hren: ");
    fileExecBefore.setNextFocusableComponent(fileExecAfter);
    fileExecBefore.setPreferredSize(new Dimension(200, 17));
    fileExecAfter.setNextFocusableComponent(okButton);
    fileExecAfter.setPreferredSize(new Dimension(200, 17));
    mitglnrStattName.setNextFocusableComponent(okButton);
    mitglnrStattName.setMnemonic('M');
    mitglnrStattName.setText("Mitgliedsnummern anstelle von Namen ausgeben");
    fbZielbereichInBemerkungenCheckBox.setNextFocusableComponent(okButton);
    fbZielbereichInBemerkungenCheckBox.setSelected(true);
    fbZielbereichInBemerkungenCheckBox.setText("Im Feld Bemerkungen auch den Zielbereich ausgeben");
    jLabel16.setDisplayedMnemonic('F');
    jLabel16.setLabelFor(nurFb1);
    jLabel16.setText("nur die folgenden Fahrtenb�cher auswerten:");
    nurFb1Button.setText("");
    nurFb1Button.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nurFb1Button_actionPerformed(e);
      }
    });
    nurFb2Button.setText("");
    nurFb2Button.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nurFb2Button_actionPerformed(e);
      }
    });
    nurFb1Button.setMinimumSize(new Dimension(40, 19));
    nurFb1Button.setPreferredSize(new Dimension(40, 19));
    nurFb1Button.setIcon(new ImageIcon(EfaConfigFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));
    nurFb2Button.setMinimumSize(new Dimension(40, 19));
    nurFb2Button.setPreferredSize(new Dimension(40, 19));
    nurFb2Button.setIcon(new ImageIcon(EfaConfigFrame.class.getResource("/de/nmichael/efa/img/prog_open.gif")));

    nurFb1.setNextFocusableComponent(nurFb2);
    nurFb2.setNextFocusableComponent(okButton);
    jLabel17.setDisplayedMnemonic('B');
    jLabel17.setLabelFor(nurBooteFuerGruppe);
    jLabel17.setText("nur Boote f�r Gruppe: ");
    nurBooteFuerGruppe.setNextFocusableComponent(nurFb1);
    nurBooteFuerGruppe.setText("");
    nurBooteFuerGruppe.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        gruppe_keyReleased(e);
      }
    });
    nurBooteFuerGruppe.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        gruppe_focusLost(e);
      }
    });
    alleZielfahrtenAusgeben.setNextFocusableComponent(okButton);
    alleZielfahrtenAusgeben.setMnemonic('Z');
    alleZielfahrtenAusgeben.setText("alle Zielfahrten ausgeben (LRV Berlin Sommer)");
    allgemeinPanel.add(jPanel2,  BorderLayout.CENTER);
    jPanel2.add(jLabel1,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 20, 0), 0, 0));
    jPanel2.add(zusammenGaesteAndere,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(teilzieleEinzeln,   new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(kilometerGruppiert,   new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(gaesteVereinsweiseZusammen,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(horiz_alle,   new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(auchNullWerte,     new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(xmlImmerAlle,    new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jPanel2.add(mitglnrStattName,  new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(jLabel2,   new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    balkenPanel.add(maxKmLabel, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxKm, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxRudKmLabel, new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxStmKmLabel, new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxFahrtenLabel, new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxKmFahrtLabel, new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxRudKm, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxStmKm, new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxFahrten, new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxKmFahrt, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(crop, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxDauerLabel,  new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxKmHLabel,  new GridBagConstraints(0, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxDauer, new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(maxKmH, new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    balkenPanel.add(zusammengefassteWerteOhneBalken,   new GridBagConstraints(0, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    this.getContentPane().add(jTabbedPane1,  BorderLayout.CENTER);
    this.getContentPane().add(okButton, BorderLayout.SOUTH);
    jTabbedPane1.add(allgemeinPanel,   "Ausgabevarianten");
    jTabbedPane1.add(balkenPanel,  "Balken");

    jTabbedPane1.add(fahrtenbuchPanel,  "Fahrtenbuch");
    fahrtenbuchPanel.add(jLabel13,     new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    fahrtenbuchPanel.add(fbLfdNrCheckBox,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbDatumCheckBox,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbBootCheckBox,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbStmCheckBox,    new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbMannschCheckBox,    new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbAbfahrtCheckBox,   new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbAnkunftCheckBox,   new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbZielCheckBox,   new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbBootsKmCheckBox,    new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbMannschKmCheckBox,    new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbBemerkungenCheckBox,    new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbFahrtartInBemerkungenCheckBox,    new GridBagConstraints(0, 8, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    fahrtenbuchPanel.add(fbZielbereichInBemerkungenCheckBox,    new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

    jTabbedPane1.add(wettPanel,   "Wettbewerbe");
    wettPanel.add(jLabel7,    new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    wettPanel.add(wett1,    new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    wettPanel.add(jLabel8,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    wettPanel.add(wettjahr1,  new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    wettPanel.add(wett2,    new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    wettPanel.add(jLabel9,    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    wettPanel.add(wettjahr2,  new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    wettPanel.add(wett3,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    wettPanel.add(jLabel10,   new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
    wettPanel.add(wettjahr3,  new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    wettPanel.add(wettMitAnforderungen,    new GridBagConstraints(0, 4, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    wettPanel.add(alleZielfahrtenAusgeben,  new GridBagConstraints(0, 5, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    jTabbedPane1.add(dateiPanel,  "Datei");
    dateiPanel.add(overwrite,     new GridBagConstraints(0, 1, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    dateiPanel.add(nurTabelle,      new GridBagConstraints(0, 2, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    dateiPanel.add(jLabel3,     new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    dateiPanel.add(jLabel14,     new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    dateiPanel.add(jLabel15,     new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    dateiPanel.add(fileExecBefore,    new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 0, 0), 0, 0));
    dateiPanel.add(fileExecAfter,  new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    jTabbedPane1.add(weiterePanel,   "Weitere");
    weiterePanel.add(jLabel4,     new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(nurBemerk,     new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(jLabel5,      new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(nurBemerkNicht,     new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(jLabel6,     new GridBagConstraints(0, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 10, 0), 0, 0));
    weiterePanel.add(nurStegKm,     new GridBagConstraints(0, 3, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(jLabel11,     new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(nurMindKm,    new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(jLabel12,    new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(jLabel16,     new GridBagConstraints(0, 6, 3, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
    weiterePanel.add(nurFb1,    new GridBagConstraints(0, 7, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(nurFb2,    new GridBagConstraints(0, 8, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(nurFb1Button,  new GridBagConstraints(3, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(nurFb2Button,   new GridBagConstraints(3, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(jLabel17,  new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
    weiterePanel.add(nurBooteFuerGruppe,   new GridBagConstraints(1, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

    iniFelder();
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
    hide();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  void iniWett(JComboBox combo) {
    combo.addItem("--- keine Auswahl ---");
    if (Daten.wettDefs != null) {
      for (int i=0; i<WettDefs.ANZWETT; i++)
        if (i==WettDefs.LRVBERLIN_BLAUERWIMPEL || i==WettDefs.DRV_WANDERRUDERSTATISTIK) {
          combo.addItem("---");
        } else {
          WettDef w = Daten.wettDefs.getWettDef(i,9999);
          if (w != null) combo.addItem(w.name);
        }
    }
    combo.setSelectedIndex(0);
    combo.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        wettselect_focusLost(e);
      }
    });
  }

  void iniFelder() {
    iniWett(wett1);
    iniWett(wett2);
    iniWett(wett3);
  }

  void okButton_actionPerformed(ActionEvent e) {
    hide();
  }

  void validateInt(JTextField field) {
    field.setText(Integer.toString(EfaUtil.string2date(field.getText().trim(),0,0,0).tag));
  }

  void nurBemerk_keyPressed(KeyEvent e) {
    if (Daten.efaConfig != null && Daten.efaConfig.keys != null) {
      Object[] k = Daten.efaConfig.keys.keySet().toArray();
      if (k != null && k.length>0) {
        for (int i=0; i<k.length; i++) {
          if (((String)k[i]).equals("F6")  && e.getKeyCode() == KeyEvent.VK_F6  && Daten.efaConfig.keys.get(k[i]) != null) nurBemerk.setText(nurBemerk.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F7")  && e.getKeyCode() == KeyEvent.VK_F7  && Daten.efaConfig.keys.get(k[i]) != null) nurBemerk.setText(nurBemerk.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F8")  && e.getKeyCode() == KeyEvent.VK_F8  && Daten.efaConfig.keys.get(k[i]) != null) nurBemerk.setText(nurBemerk.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F9")  && e.getKeyCode() == KeyEvent.VK_F9  && Daten.efaConfig.keys.get(k[i]) != null) nurBemerk.setText(nurBemerk.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F10") && e.getKeyCode() == KeyEvent.VK_F10 && Daten.efaConfig.keys.get(k[i]) != null) nurBemerk.setText(nurBemerk.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F11") && e.getKeyCode() == KeyEvent.VK_F11 && Daten.efaConfig.keys.get(k[i]) != null) nurBemerk.setText(nurBemerk.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F12") && e.getKeyCode() == KeyEvent.VK_F12 && Daten.efaConfig.keys.get(k[i]) != null) nurBemerk.setText(nurBemerk.getText()+Daten.efaConfig.keys.get(k[i]));
        }
      }
    }
  }

  void nurBemerkNicht_keyPressed(KeyEvent e) {
    if (Daten.efaConfig != null && Daten.efaConfig.keys != null) {
      Object[] k = Daten.efaConfig.keys.keySet().toArray();
      if (k != null && k.length>0) {
        for (int i=0; i<k.length; i++) {
          if (((String)k[i]).equals("F6")  && e.getKeyCode() == KeyEvent.VK_F6  && Daten.efaConfig.keys.get(k[i]) != null) nurBemerkNicht.setText(nurBemerkNicht.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F7")  && e.getKeyCode() == KeyEvent.VK_F7  && Daten.efaConfig.keys.get(k[i]) != null) nurBemerkNicht.setText(nurBemerkNicht.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F8")  && e.getKeyCode() == KeyEvent.VK_F8  && Daten.efaConfig.keys.get(k[i]) != null) nurBemerkNicht.setText(nurBemerkNicht.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F9")  && e.getKeyCode() == KeyEvent.VK_F9  && Daten.efaConfig.keys.get(k[i]) != null) nurBemerkNicht.setText(nurBemerkNicht.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F10") && e.getKeyCode() == KeyEvent.VK_F10 && Daten.efaConfig.keys.get(k[i]) != null) nurBemerkNicht.setText(nurBemerkNicht.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F11") && e.getKeyCode() == KeyEvent.VK_F11 && Daten.efaConfig.keys.get(k[i]) != null) nurBemerkNicht.setText(nurBemerkNicht.getText()+Daten.efaConfig.keys.get(k[i]));
          if (((String)k[i]).equals("F12") && e.getKeyCode() == KeyEvent.VK_F12 && Daten.efaConfig.keys.get(k[i]) != null) nurBemerkNicht.setText(nurBemerkNicht.getText()+Daten.efaConfig.keys.get(k[i]));
        }
      }
    }
  }

  void wettChanged(JComboBox wett, JTextField wettjahr) {
    if (wett.getSelectedIndex() <= 0 || Daten.wettDefs == null) {
      wettjahr.setText("");
      return;
    }

    int year = EfaUtil.string2date(wettjahr.getText(),-1,0,0).tag;
    if (year>=0 && year<100) year += 1900;
    if (year>=0 && year<1980) year += 100;


    WettDef w;
    if (year>=0) {
      w = Daten.wettDefs.getWettDef(wett.getSelectedIndex()-1,year);
    } else {
      // Wettjahr ermitteln: Immer bis 2 Monate nach Ende des Wettzeitraums noch vorigen Wettbewerb vorschlagen!
      Calendar cal = GregorianCalendar.getInstance();
      year = cal.get(Calendar.YEAR);
      w = Daten.wettDefs.getWettDef(wett.getSelectedIndex()-1,year);
      year = StatistikFrame.getBestWettJahr(w,year,cal);
    }


    if (w != null && w.von.jahr != w.bis.jahr) wettjahr.setText(year+"/"+(year+1));
    else wettjahr.setText(Integer.toString(year));
  }

  void wett1_itemStateChanged(ItemEvent e) {
    wettChanged(wett1,wettjahr1);
  }

  void wett2_itemStateChanged(ItemEvent e) {
    wettChanged(wett2,wettjahr2);
  }

  void wett3_itemStateChanged(ItemEvent e) {
    wettChanged(wett3,wettjahr3);
  }

  void wettjahr1_focusLost(FocusEvent e) {
    wettChanged(wett1,wettjahr1);
  }

  void wettjahr2_focusLost(FocusEvent e) {
    wettChanged(wett2,wettjahr2);
  }

  void wettjahr3_focusLost(FocusEvent e) {
    wettChanged(wett3,wettjahr3);
  }

  void nurMindKm_focusLost(FocusEvent e) {
    if (nurMindKm.getText().trim().length()>0)
      nurMindKm.setText(EfaUtil.zehntelInt2String(EfaUtil.string2date(nurMindKm.getText(),0,0,0).tag*10 + EfaUtil.string2date(nurMindKm.getText(),0,0,0).monat));
    else nurMindKm.setText("");
  }

  void wettselect_focusLost(FocusEvent e) {
    if (e == null) return;
    if (e.getComponent() == null) return;
    JComboBox combo = (JComboBox)e.getComponent();
    if (((String)combo.getSelectedItem()).equals("---")) combo.setSelectedIndex(0);
  }

  void nurFb1Button_actionPerformed(ActionEvent e) {
    String startdir = nurFb1.getText().trim();
    if (startdir.length() == 0 || startdir.indexOf(Daten.fileSep)<0) startdir = Daten.fahrtenbuch.getFileName();
    String dat = Dialog.dateiDialog(this,"Fahrtenbuch ausw�hlen","efa-Fahrtenbuch (*.efb)","efb",startdir,false);
    if (dat != null) {
      if (EfaUtil.getPathOfFile(dat).equals(EfaUtil.getPathOfFile(Daten.fahrtenbuch.getFileName()))) {
        dat = EfaUtil.getFilenameWithoutPath(dat);
      }
      nurFb1.setText(dat);
    }
  }

  void nurFb2Button_actionPerformed(ActionEvent e) {
    String startdir = nurFb2.getText().trim();
    if (startdir.length() == 0 || startdir.indexOf(Daten.fileSep)<0) startdir = Daten.fahrtenbuch.getFileName();
    String dat = Dialog.dateiDialog(this,"Fahrtenbuch ausw�hlen","efa-Fahrtenbuch (*.efb)","efb",startdir,false);
    if (dat != null) {
      if (EfaUtil.getPathOfFile(dat).equals(EfaUtil.getPathOfFile(Daten.fahrtenbuch.getFileName()))) {
        dat = EfaUtil.getFilenameWithoutPath(dat);
      }
      nurFb2.setText(dat);
    }

  }

  void gruppe_keyReleased(KeyEvent e) {
    if (Daten.gruppen == null) return;
    if (e == null) return;
    JTextField field;
    try {
      field = (JTextField)e.getSource();
    } catch(Exception ee) {
      return;
    }
    if (gruppen == null) {
      Vector g = Daten.gruppen.getGruppen();
      gruppen = new DatenListe("foo",1,1,false);
      for (int i=0; i<g.size(); i++) {
        DatenFelder d = new DatenFelder(1);
        d.set(0,(String)g.get(i));
        gruppen.add(d);
      }
    }
    if (field.getText().trim().equals("")) return;
    EfaFrame.vervollstaendige(field,null,gruppen,e,null,true);
  }

  void gruppe_focusLost(FocusEvent e) {
    if (Daten.efaConfig != null && Daten.efaConfig.popupComplete) AutoCompletePopupWindow.hideWindow();
  }

}