package de.nmichael.efa.drv;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import de.nmichael.efa.*;
import de.nmichael.efa.Dialog;
import java.beans.*;
import javax.swing.event.*;
import java.security.PrivateKey;


public class MeldungEditFrame extends JDialog implements ActionListener {

  static final int MAX_FAHRTEN = 7;
  private static boolean _hasBeenSaved = false;

  JDialog parent;
  DRVConfig drvConfig;
  EfaWett ew;
  String qnr;
  int MELDTYP;

  int meldegeld;
  Vector data;
  JTextField[][] mFahrten;
  EfaWettMeldung ewmCur = null;
  int ewmNr = -1;
  boolean changed = false;
  boolean vBlocked = true;
  boolean mBlocked = true;

  Hashtable hGeschlecht = new Hashtable();
  Hashtable hGruppe = new Hashtable();
  Hashtable hAbzeichen = new Hashtable();
  Hashtable hAequator = new Hashtable();

  JPanel mainPanel = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel northPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel infoQnrLabel = new JLabel();
  JLabel jLabel2 = new JLabel();
  JLabel jLabel3 = new JLabel();
  JLabel infoVereinLabel = new JLabel();
  JPanel southPanel = new JPanel();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  JTabbedPane jTabbedPane1 = new JTabbedPane();
  JPanel vereinsdatenPanel = new JPanel();
  JPanel teilnehmerPanel = new JPanel();
  JButton bestaetigenButton = new JButton();
  JButton closeButton = new JButton();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JPanel vereinPanel = new JPanel();
  GridBagLayout gridBagLayout4 = new GridBagLayout();
  TitledBorder titledBorderVerein;
  JLabel jLabel1 = new JLabel();
  JTextField vMitgliedsnr = new JTextField();
  JLabel jLabel4 = new JLabel();
  JTextField vVereinsname = new JTextField();
  JTextField vNutzername = new JTextField();
  JLabel jLabel5 = new JLabel();
  JPanel meldenderPanel = new JPanel();
  TitledBorder titledBorderMeldender;
  GridBagLayout gridBagLayout5 = new GridBagLayout();
  JLabel vMeldenderKontoLabel = new JLabel();
  JTextField vMeldenderKonto = new JTextField();
  JLabel jLabel9 = new JLabel();
  JLabel vMeldenderBankLabel = new JLabel();
  JTextField vMeldenderBank = new JTextField();
  JLabel vMeldenderBlzLabel = new JLabel();
  JTextField vMeldenderBlz = new JTextField();
  JTextField vMeldenderName = new JTextField();
  JLabel jLabel12 = new JLabel();
  JTextField vMeldenderEmail = new JTextField();
  JPanel versandPanel = new JPanel();
  TitledBorder titledBorderVersand;
  TitledBorder titledBorderBestellungen;
  GridBagLayout gridBagLayout6 = new GridBagLayout();
  JLabel jLabel6 = new JLabel();
  JLabel jLabel7 = new JLabel();
  JLabel jLabel13 = new JLabel();
  JTextField vVersandName = new JTextField();
  JTextField vVersantStrasse = new JTextField();
  JTextField vVersandOrt = new JTextField();
  JPanel bestellungenPanel = new JPanel();
  GridBagLayout gridBagLayout7 = new GridBagLayout();
  JLabel jLabel14 = new JLabel();
  JLabel jLabel15 = new JLabel();
  JLabel jLabel16 = new JLabel();
  JTextField vBestNadelErwSilber = new JTextField();
  JLabel jLabel17 = new JLabel();
  JTextField vBestNadelErwGold = new JTextField();
  JLabel jLabel18 = new JLabel();
  JTextField vBestNadelJugSilber = new JTextField();
  JLabel jLabel19 = new JLabel();
  JTextField vBestNadelJugGold = new JTextField();
  JLabel jLabel20 = new JLabel();
  JTextField vBestStoffErw = new JTextField();
  JLabel jLabel21 = new JLabel();
  JTextField vBestStoffJug = new JTextField();
  JButton printStoffBestellButton = new JButton();
  JButton vUnblockButton = new JButton();
  JPanel zusammenfassungPanel = new JPanel();
  TitledBorder titledBorderZusammenfassung;
  GridBagLayout gridBagLayout8 = new GridBagLayout();
  JLabel vZusGemTeilnehmer = new JLabel();
  JTextField vZusammenfassungAnzTeilnehmer = new JTextField();
  JLabel vZusTeilnehmerErfuellt = new JLabel();
  JTextField vZusammenfassungAnzTeilnehmerErfuellt = new JTextField();
  JLabel vZusTeilnehmerUngueltig = new JLabel();
  JTextField vZusammenfassungAnzTeilnehmerUngueltig = new JTextField();
  JLabel vBetragMeldLabel = new JLabel();
  JTextField vZusammenfassungMeldegebuehr = new JTextField();
  JLabel vBetragStoffLabel = new JLabel();
  JTextField vZusammenfassungStoffabzeichen = new JTextField();
  JLabel papierFahrtenhefteErforderlichLabel = new JLabel();
  JLabel vBetragGesamtLabel = new JLabel();
  JTextField vZusammenfassungEurGesamt = new JTextField();
  JLabel vBetragLabel = new JLabel();
  JLabel vZusTeilnehmer = new JLabel();
  GridBagLayout gridBagLayout9 = new GridBagLayout();
  JPanel tailnNaviPanel = new JPanel();
  GridBagLayout gridBagLayout10 = new GridBagLayout();
  JButton deleteButton = new JButton();
  JButton newButton = new JButton();
  JButton lastButton = new JButton();
  JButton nextButton = new JButton();
  JButton prevButton = new JButton();
  JButton firstButton = new JButton();
  JPanel teilnDataPanel = new JPanel();
  JPanel fahrtenDataPanel = new JPanel();
  GridBagLayout gridBagLayout11 = new GridBagLayout();
  JLabel jLabel31 = new JLabel();
  JLabel jLabel32 = new JLabel();
  JLabel jLabel33 = new JLabel();
  JLabel jLabel34 = new JLabel();
  JLabel jLabel35 = new JLabel();
  JLabel jLabel36 = new JLabel();
  JLabel jLabel37 = new JLabel();
  JLabel jLabel38 = new JLabel();
  JLabel jLabel39 = new JLabel();
  JLabel jLabel40 = new JLabel();
  JLabel jLabel41 = new JLabel();
  JTextField mNachname = new JTextField();
  JTextField mVorname = new JTextField();
  JTextField mJahrgang = new JTextField();
  JComboBox mGeschlecht = new JComboBox();
  JComboBox mGruppe = new JComboBox();
  JTextField mKilometer = new JTextField();
  JTextField mAbzeichen = new JTextField();
  JTextField mAequatorpreis = new JTextField();
  JTextField mAnzAbzeichen = new JTextField();
  JTextField mGesKm = new JTextField();
  TitledBorder titledBorderTeilnehmer;
  TitledBorder titledBorderFahrten;
  JLabel jLabel30 = new JLabel();
  JLabel jLabel42 = new JLabel();
  JLabel jLabel43 = new JLabel();
  JLabel jLabel44 = new JLabel();
  JLabel jLabel45 = new JLabel();
  JLabel jLabel46 = new JLabel();
  JLabel jLabel47 = new JLabel();
  JLabel jLabel48 = new JLabel();
  JPanel teilnWarnPanel = new JPanel();
  TitledBorder titledBorderWarnungen;
  JScrollPane jScrollPane1 = new JScrollPane();
  JTextArea mWarnungen = new JTextArea();
  BorderLayout borderLayout2 = new BorderLayout();
  JPanel dateiPanel = new JPanel();
  GridBagLayout gridBagLayout12 = new GridBagLayout();
  TitledBorder titledBorderDatei;
  JLabel jLabel49 = new JLabel();
  JTextField vKennung = new JTextField();
  JLabel jLabel50 = new JLabel();
  JTextField vProgramm = new JTextField();
  JLabel jLabel51 = new JLabel();
  JLabel jLabel52 = new JLabel();
  JTextField mAnzAbzeichenAB = new JTextField();
  JTextField mGesKmAB = new JTextField();
  JButton mUnblockButton = new JButton();
  JLabel mFahrtenheft = new JLabel();
  JCheckBox mWirdGewertet = new JCheckBox();
  JLabel jLabel53 = new JLabel();
  JTextField mTeilnNr = new JTextField();
  JButton mTeilnSuchenButton = new JButton();
  JButton aequatorButton = new JButton();
  JLabel mNichtGewertetGrundLabel = new JLabel();
  JTextField mNichtGewertetGrund = new JTextField();
  JCheckBox vMeldegeldEingegangen = new JCheckBox();
  JLabel vEingPapierhefteLabel = new JLabel();
  JTextField vAnzahlPapierFahrtenhefte = new JTextField();
  JLabel jLabel8 = new JLabel();
  JTextField fStartZiel = new JTextField();
  JLabel jLabel10 = new JLabel();
  JTextField fGewaesser = new JTextField();
  JLabel jLabel11 = new JLabel();
  JTextField fKilometer = new JTextField();
  JLabel jLabel55 = new JLabel();
  JTextField fTage = new JTextField();
  JLabel jLabel56 = new JLabel();
  JTextField fTeilnehmer = new JTextField();
  JTextField fMannschKm = new JTextField();
  JLabel jLabel58 = new JLabel();
  JLabel jLabel59 = new JLabel();
  JLabel jLabel60 = new JLabel();
  JLabel jLabel61 = new JLabel();
  JLabel jLabel62 = new JLabel();
  JLabel jLabel63 = new JLabel();
  JLabel jLabel64 = new JLabel();
  JLabel jLabel65 = new JLabel();
  JTextField fMaennerAnz = new JTextField();
  JTextField fJuniorenAnz = new JTextField();
  JTextField fFrauenAnz = new JTextField();
  JTextField fJuniorinnenAnz = new JTextField();
  JTextField fMaennerKm = new JTextField();
  JTextField fJuniorenKm = new JTextField();
  JTextField fFrauenKm = new JTextField();
  JTextField fJuniorinnenKm = new JTextField();
  JLabel jLabel66 = new JLabel();
  JLabel jLabel67 = new JLabel();
  JLabel jLabel68 = new JLabel();
  JLabel jLabel69 = new JLabel();
  JLabel jLabel57 = new JLabel();
  JLabel jLabel70 = new JLabel();
  JButton fUnblockButton = new JButton();
  JCheckBox fWirdGewertet = new JCheckBox();
  JLabel fNichtGewertetGrundLabel = new JLabel();
  JTextField fNichtGewertetGrund = new JTextField();
  JLabel jLabel22 = new JLabel();
  JButton wsListButton = new JButton();
  JCheckBox mFahrtnachweisErbracht = new JCheckBox();


  public MeldungEditFrame(JDialog parent, DRVConfig drvConfig, EfaWett ew, String qnr, int meldTyp) {
    super(parent);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    this.parent = parent;
    this.drvConfig = drvConfig;
    this.ew = ew;
    this.qnr = qnr;
    this.MELDTYP = meldTyp;
    log(false,"START Bearbeiten der Meldung");
    _hasBeenSaved = false;
    try {
      jbInit();
      iniFields();
      ew.durchDRVbearbeitet = true;
      readMeldedatei();
      setVFields();
      this.changed = false;
      calcOverallValues();
      setMFields(0,false);

      if (ew.allg_programm != null && ew.allg_programm.equals(Daten.PROGRAMMID_DRV) &&
          (ew.verein_name == null || ew.verein_name.length() == 0)) {
        vBlock(false);
        mBlock(true);
        this.vVereinsname.requestFocus();
        this.changed = true;
      }

    }
    catch(Exception e) {
      e.printStackTrace();
    }
    EfaUtil.pack(this);
    // this.requestFocus();
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
      titledBorderVerein = new TitledBorder("Verein");
      titledBorderMeldender = new TitledBorder("Meldender");
      titledBorderVersand = new TitledBorder("Versand");
      titledBorderBestellungen = new TitledBorder("Bestellungen");
      titledBorderZusammenfassung = new TitledBorder("Zusammenfassung");
      titledBorderTeilnehmer = new TitledBorder("Teilnehmer");
      titledBorderFahrten = new TitledBorder("Fahrten");
      titledBorderWarnungen = new TitledBorder("Warnungen / Fehler");
      titledBorderDatei = new TitledBorder("Datei");
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
      mainPanel.setLayout(borderLayout1);
      this.setTitle("Meldung bearbeiten");
      northPanel.setLayout(gridBagLayout1);
      infoQnrLabel.setForeground(Color.black);
      infoQnrLabel.setText("1234567890");
      jLabel2.setText("Verein: ");
      jLabel3.setText("Quittungsnummer: ");
      infoVereinLabel.setForeground(Color.black);
      infoVereinLabel.setText("Vereinsname");
      southPanel.setLayout(gridBagLayout2);
      bestaetigenButton.setActionCommand("Bearbeitung abschlie�en");
      bestaetigenButton.setText("Bearbeitung abschlie�en");
      bestaetigenButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          bestaetigenButton_actionPerformed(e);
        }
      });
      closeButton.setText("Bearbeitung unterbrechen");
      closeButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          closeButton_actionPerformed(e);
        }
      });
      vereinsdatenPanel.setLayout(gridBagLayout3);
      vereinPanel.setLayout(gridBagLayout4);
      vereinPanel.setBorder(titledBorderVerein);
      vereinPanel.setName("Verein");
      jLabel1.setText("Mitgliedsnummer: ");
      jLabel4.setText("Vereinsname: ");
      jLabel5.setText("Benutzername: ");
      vMitgliedsnr.setMinimumSize(new Dimension(70, 17));
      vMitgliedsnr.setPreferredSize(new Dimension(100, 17));
      vVereinsname.setMinimumSize(new Dimension(200, 17));
      vVereinsname.setPreferredSize(new Dimension(300, 17));
      vNutzername.setMinimumSize(new Dimension(50, 17));
      vNutzername.setPreferredSize(new Dimension(100, 17));
      meldenderPanel.setBorder(titledBorderMeldender);
      meldenderPanel.setLayout(gridBagLayout5);
      vMeldenderKontoLabel.setText("Konto: ");
      jLabel9.setText("Name: ");
      vMeldenderBankLabel.setText("Bank: ");
      vMeldenderBlzLabel.setText("BLZ: ");
      jLabel12.setText("email: ");
      vMeldenderName.setMinimumSize(new Dimension(200, 17));
      vMeldenderName.setPreferredSize(new Dimension(200, 17));
      vMeldenderKonto.setMinimumSize(new Dimension(200, 17));
      vMeldenderKonto.setPreferredSize(new Dimension(200, 17));
      vMeldenderBank.setMinimumSize(new Dimension(200, 17));
      vMeldenderBank.setPreferredSize(new Dimension(200, 17));
      vMeldenderBlz.setMinimumSize(new Dimension(150, 17));
      vMeldenderBlz.setPreferredSize(new Dimension(150, 17));
      vMeldenderEmail.setMinimumSize(new Dimension(150, 17));
      vMeldenderEmail.setPreferredSize(new Dimension(150, 17));
      versandPanel.setBorder(titledBorderVersand);
      versandPanel.setLayout(gridBagLayout6);
      jLabel6.setText("Ort: ");
      jLabel7.setText("Stra�e: ");
      jLabel13.setText("Name: ");
      vVersantStrasse.setMinimumSize(new Dimension(300, 17));
      vVersantStrasse.setPreferredSize(new Dimension(300, 17));
      vVersandOrt.setMinimumSize(new Dimension(200, 17));
      vVersandOrt.setPreferredSize(new Dimension(200, 17));
      bestellungenPanel.setLayout(gridBagLayout7);
      bestellungenPanel.setBorder(titledBorderBestellungen);
      jLabel14.setText("Erw. (gold): ");
      jLabel15.setText("Anstecknadeln: ");
      jLabel16.setText("Erw. (silber): ");
      jLabel17.setText("Erw. (gold): ");
      jLabel18.setToolTipText("");
      jLabel18.setText("Jug. (silber): ");
      vAnzahlPapierFahrtenhefte.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      vBestNadelJugSilber.setMinimumSize(new Dimension(70, 17));
      vBestNadelJugSilber.setPreferredSize(new Dimension(70, 17));
      vBestNadelJugSilber.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      jLabel19.setText("Jug. (gold): ");
      vBestNadelJugGold.setMinimumSize(new Dimension(70, 17));
      vBestNadelJugGold.setPreferredSize(new Dimension(70, 17));
      vBestNadelJugGold.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      jLabel20.setText("Stoffabzeichen: ");
      vBestStoffErw.setMinimumSize(new Dimension(70, 17));
      vBestStoffErw.setPreferredSize(new Dimension(70, 17));
      vBestStoffErw.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      jLabel21.setText("Jugend: ");
      vBestStoffJug.setMinimumSize(new Dimension(70, 17));
      vBestStoffJug.setPreferredSize(new Dimension(70, 17));
      vBestStoffJug.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      printStoffBestellButton.setBackground(Color.orange);
      printStoffBestellButton.setText("Soffabzeichen-Bestellungen drucken");
      printStoffBestellButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          printStoffBestellButton_actionPerformed(e);
        }
    });
      vBestNadelErwSilber.setMinimumSize(new Dimension(70, 17));
      vBestNadelErwSilber.setPreferredSize(new Dimension(70, 17));
      vBestNadelErwSilber.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      vBestNadelErwGold.setMinimumSize(new Dimension(70, 17));
      vBestNadelErwGold.setPreferredSize(new Dimension(70, 17));
      vBestNadelErwGold.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      vUnblockButton.setText("Felder zum Bearbeiten freigeben");
      vUnblockButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          vUnblockButton_actionPerformed(e);
        }
    });
      zusammenfassungPanel.setBorder(titledBorderZusammenfassung);
      zusammenfassungPanel.setLayout(gridBagLayout8);
      vZusGemTeilnehmer.setText("gemeldete Teilnehmer: ");
      vZusammenfassungAnzTeilnehmer.setPreferredSize(new Dimension(90, 17));
      vZusammenfassungAnzTeilnehmer.setEditable(false);
      vZusTeilnehmerErfuellt.setText("... davon erf�llt: ");
      vZusammenfassungAnzTeilnehmerErfuellt.setPreferredSize(new Dimension(90, 17));
      vZusammenfassungAnzTeilnehmerErfuellt.setEditable(false);
      vZusTeilnehmerUngueltig.setText("... davon ung�ltig: ");
      vZusammenfassungAnzTeilnehmerUngueltig.setForeground(Color.red);
      vZusammenfassungAnzTeilnehmerUngueltig.setPreferredSize(new Dimension(90, 17));
      vZusammenfassungAnzTeilnehmerUngueltig.setEditable(false);
      vBetragMeldLabel.setText("... davon Meldegeb�hr+Anstecknadeln: ");
      vZusammenfassungMeldegebuehr.setPreferredSize(new Dimension(90, 17));
      vZusammenfassungMeldegebuehr.setEditable(false);
      vBetragStoffLabel.setText("... davon Stoffabzeichen: ");
      vZusammenfassungStoffabzeichen.setPreferredSize(new Dimension(90, 17));
      vZusammenfassungStoffabzeichen.setEditable(false);
      papierFahrtenhefteErforderlichLabel.setForeground(Color.red);
      papierFahrtenhefteErforderlichLabel.setHorizontalAlignment(SwingConstants.CENTER);
      papierFahrtenhefteErforderlichLabel.setHorizontalTextPosition(SwingConstants.CENTER);
      papierFahrtenhefteErforderlichLabel.setText("Papier-Fahrtenhefte erforderlich!");
      vBetragGesamtLabel.setText("gesamt: ");
      vZusammenfassungEurGesamt.setPreferredSize(new Dimension(90, 17));
      vZusammenfassungEurGesamt.setEditable(false);
      vBetragLabel.setHorizontalAlignment(SwingConstants.CENTER);
      vBetragLabel.setText("zu zahlender Betrag:");
      vZusTeilnehmer.setHorizontalAlignment(SwingConstants.CENTER);
      vZusTeilnehmer.setText("Teilnehmer");
      teilnehmerPanel.setLayout(gridBagLayout9);
      tailnNaviPanel.setLayout(gridBagLayout10);
      deleteButton.setPreferredSize(new Dimension(140, 23));
      deleteButton.setText("L�schen");
      deleteButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          deleteButton_actionPerformed(e);
        }
    });
      newButton.setPreferredSize(new Dimension(140, 23));
      newButton.setText("Neu");
      newButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          newButton_actionPerformed(e);
        }
    });
      lastButton.setPreferredSize(new Dimension(140, 23));
      lastButton.setText("Letzter");
      lastButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          lastButton_actionPerformed(e);
        }
    });
      nextButton.setPreferredSize(new Dimension(140, 23));
      nextButton.setText("N�chster >>");
      nextButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          nextButton_actionPerformed(e);
        }
    });
      prevButton.setPreferredSize(new Dimension(140, 23));
      prevButton.setText("<< Vorheriger");
      prevButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          prevButton_actionPerformed(e);
        }
    });
      firstButton.setPreferredSize(new Dimension(140, 23));
      firstButton.setText("Erster");
      firstButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          firstButton_actionPerformed(e);
        }
    });
      teilnDataPanel.setLayout(gridBagLayout11);
      fahrtenDataPanel.setLayout(gridBagLayout11);
      jLabel31.setPreferredSize(new Dimension(81, 22));
      jLabel31.setText("Nachname: ");
      jLabel32.setPreferredSize(new Dimension(71, 22));
      jLabel32.setText("Vorname: ");
      jLabel33.setPreferredSize(new Dimension(73, 22));
      jLabel33.setText("Jahrgang: ");
      jLabel34.setPreferredSize(new Dimension(120, 22));
      jLabel34.setText("Kilomter bisher: ");
      jLabel35.setPreferredSize(new Dimension(92, 22));
      jLabel35.setText("Fahrtenheft: ");
      jLabel36.setPreferredSize(new Dimension(86, 22));
      jLabel36.setText("Geschlecht: ");
      jLabel37.setPreferredSize(new Dimension(60, 22));
      jLabel37.setText("Gruppe: ");
      jLabel38.setPreferredSize(new Dimension(78, 22));
      jLabel38.setText("Kilometer: ");
      jLabel39.setPreferredSize(new Dimension(82, 22));
      jLabel39.setText("Abzeichen: ");
      jLabel40.setPreferredSize(new Dimension(132, 22));
      jLabel40.setText("Abzeichen bisher: ");
      jLabel41.setPreferredSize(new Dimension(102, 22));
      jLabel41.setText("�quatorpreis: ");
      mNachname.setPreferredSize(new Dimension(200, 17));
      mVorname.setPreferredSize(new Dimension(200, 17));
      mJahrgang.setPreferredSize(new Dimension(200, 17));
      mKilometer.setPreferredSize(new Dimension(200, 17));
      mAnzAbzeichen.setPreferredSize(new Dimension(70, 17));
      mGesKm.setPreferredSize(new Dimension(70, 17));
      mGeschlecht.setPreferredSize(new Dimension(200, 22));
      mGruppe.setPreferredSize(new Dimension(200, 22));
      mAbzeichen.setEditable(false);
      mAbzeichen.setPreferredSize(new Dimension(200, 22));
      mAequatorpreis.setEditable(false);
      mAequatorpreis.setPreferredSize(new Dimension(200, 22));
      teilnDataPanel.setBorder(titledBorderTeilnehmer);
      fahrtenDataPanel.setBorder(titledBorderFahrten);
      jLabel30.setText(" ");
      jLabel42.setHorizontalAlignment(SwingConstants.CENTER);
      jLabel42.setHorizontalTextPosition(SwingConstants.CENTER);
      jLabel42.setText("Nachweis der Fahrten");
      jLabel43.setText("LfdNr.");
      jLabel44.setText("Startdatum");
      jLabel45.setText("Enddatum");
      jLabel46.setText("Ziel");
      jLabel47.setText("Km");
      jLabel48.setText("Bemerk.");
      mFahrtnachweisErbracht.setText("Fahrtnachweis erbracht (keine Pr�fung)");
      mFahrtnachweisErbracht.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          mFahrtnachweisErbracht_actionPerformed(e);
        }
      });
      teilnWarnPanel.setBorder(titledBorderWarnungen);
      teilnWarnPanel.setLayout(borderLayout2);
      mWarnungen.setFont(new java.awt.Font("Dialog", 1, 12));
      mWarnungen.setForeground(Color.red);
      mWarnungen.setCaretColor(Color.red);
      mWarnungen.setEditable(false);
      jScrollPane1.setMinimumSize(new Dimension(22, 50));
      jScrollPane1.setPreferredSize(new Dimension(80, 50));
      dateiPanel.setLayout(gridBagLayout12);
      dateiPanel.setBorder(titledBorderDatei);
      jLabel49.setText("Datei-Kennung: ");
      vKennung.setMinimumSize(new Dimension(150, 17));
      vKennung.setPreferredSize(new Dimension(150, 17));
      vKennung.setEditable(false);
      jLabel50.setText("Programm: ");
      vProgramm.setMinimumSize(new Dimension(150, 17));
      vProgramm.setPreferredSize(new Dimension(150, 17));
      vProgramm.setEditable(false);
      jLabel51.setText("davon Jug A/B: ");
      jLabel52.setText("davon Jug A/B: ");
      mAnzAbzeichenAB.setPreferredSize(new Dimension(70, 17));
      mGesKmAB.setPreferredSize(new Dimension(70, 17));
      mUnblockButton.setText("Felder zum Bearbeiten freigeben");
      mUnblockButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          mUnblockButton_actionPerformed(e);
        }
    });
      mFahrtenheft.setText("Fahrtenheft ist ...");
      mWirdGewertet.setText("Dieser Teilnehmer wird gewertet");
      mWirdGewertet.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          mWirdGewertet_actionPerformed(e);
        }
    });
      jLabel53.setPreferredSize(new Dimension(114, 22));
      jLabel53.setText("Teilnehmer-Nr: ");
      mTeilnSuchenButton.setPreferredSize(new Dimension(155, 22));
      mTeilnSuchenButton.setText("Teilnehmer-Nr. suchen");
      mTeilnSuchenButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          mTeilnSuchenButton_actionPerformed(e);
        }
    });
      aequatorButton.setForeground(Color.blue);
      aequatorButton.setHorizontalTextPosition(SwingConstants.CENTER);
      aequatorButton.setText("5 �quatorpreistr�ger");
      aequatorButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          aequatorButton_actionPerformed(e);
        }
      });
      jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
        public void stateChanged(ChangeEvent e) {
          jTabbedPane1_stateChanged(e);
        }
    });
      mNichtGewertetGrundLabel.setText("Grund bei Nicht-Wertung: ");
    mNichtGewertetGrund.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        mNichtGewertetGrund_focusLost(e);
      }
    });
      vMeldegeldEingegangen.setForeground(Color.red);
      vMeldegeldEingegangen.setText("eingegangen");
      vMeldegeldEingegangen.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          vMeldegeldEingegangen_actionPerformed(e);
        }
    });
      vEingPapierhefteLabel.setText("Eingesandte Papier-Fahrtenhefte: ");
      jLabel8.setText("Weg und Ziel der Fahrt: ");
      jLabel10.setText("Befahrene Gew�sser: ");
      jLabel11.setToolTipText("");
      jLabel11.setText("Dauer und L�nge: ");
      jLabel55.setText("Tage: ");
      jLabel56.setText("Anzahl: ");
      fKilometer.setMinimumSize(new Dimension(50, 17));
      fKilometer.setPreferredSize(new Dimension(60, 17));
      fTage.setMinimumSize(new Dimension(60, 17));
      fTage.setPreferredSize(new Dimension(60, 17));
      fTeilnehmer.setMinimumSize(new Dimension(60, 17));
      fTeilnehmer.setPreferredSize(new Dimension(60, 17));
      fMannschKm.setMinimumSize(new Dimension(60, 17));
      fMannschKm.setPreferredSize(new Dimension(60, 17));
      jLabel58.setText("M�nner: ");
      jLabel59.setText("Junioren: ");
      jLabel60.setText("Frauen: ");
      jLabel61.setText("Juniorinnen: ");
      jLabel62.setText("Anzahl: ");
      jLabel63.setText("Anzahl: ");
      jLabel64.setText("Anzahl: ");
      jLabel65.setText("Anzahl: ");
      fJuniorinnenAnz.setMinimumSize(new Dimension(60, 17));
      fJuniorinnenAnz.setPreferredSize(new Dimension(60, 17));
      fFrauenAnz.setMinimumSize(new Dimension(60, 17));
      fFrauenAnz.setPreferredSize(new Dimension(60, 17));
      fJuniorenAnz.setMinimumSize(new Dimension(60, 17));
      fJuniorenAnz.setPreferredSize(new Dimension(60, 17));
      fMaennerAnz.setMinimumSize(new Dimension(60, 17));
      fMaennerAnz.setPreferredSize(new Dimension(60, 17));
      jLabel66.setText("Kilometer: ");
      jLabel67.setText("Kilometer: ");
      jLabel68.setText("Kilometer: ");
      jLabel69.setText("Kilometer: ");
      fJuniorinnenKm.setMinimumSize(new Dimension(60, 17));
      fJuniorinnenKm.setPreferredSize(new Dimension(60, 17));
      fFrauenKm.setMinimumSize(new Dimension(60, 17));
      fFrauenKm.setPreferredSize(new Dimension(60, 17));
      fJuniorenKm.setMinimumSize(new Dimension(60, 17));
      fJuniorenKm.setPreferredSize(new Dimension(60, 17));
      fMaennerKm.setMinimumSize(new Dimension(60, 17));
      fMaennerKm.setPreferredSize(new Dimension(60, 17));
      jLabel57.setText("Kilometer: ");
      jLabel70.setText("Streckenl�nge (Kilomter): ");
      fStartZiel.setPreferredSize(new Dimension(600, 17));
      fGewaesser.setPreferredSize(new Dimension(600, 17));
      fUnblockButton.setText("Felder zum Bearbeiten freigeben");
      fUnblockButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fUnblockButton_actionPerformed(e);
        }
    });
      fWirdGewertet.setText("Diese Fahrt wird gewertet");
      fWirdGewertet.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          fWirdGewertet_actionPerformed(e);
        }
    });
      fNichtGewertetGrundLabel.setText("Grund bei Nicht-Wertung: ");
    fNichtGewertetGrund.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusLost(FocusEvent e) {
        fNichtGewertetGrund_focusLost(e);
      }
    });
      fTage.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      fKilometer.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumberWithComma_focusLost(e);
        }
      });
      fMaennerAnz.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      fMaennerKm.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumberWithComma_focusLost(e);
        }
      });
      fJuniorenAnz.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      fJuniorenKm.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumberWithComma_focusLost(e);
        }
      });
      fFrauenAnz.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      fFrauenKm.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumberWithComma_focusLost(e);
        }
      });
      fJuniorinnenAnz.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      fJuniorinnenKm.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumberWithComma_focusLost(e);
        }
      });
      fTeilnehmer.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumber_focusLost(e);
        }
      });
      fMannschKm.addFocusListener(new java.awt.event.FocusAdapter() {
        public void focusLost(FocusEvent e) {
          makeSureIsANumberWithComma_focusLost(e);
        }
      });

      jLabel22.setText("Teilnehmer insgesamt:");
      wsListButton.setText("Liste aller Wanderfahrten anzeigen");
      wsListButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          wsListButton_actionPerformed(e);
        }
    });
      this.getContentPane().add(mainPanel, BorderLayout.CENTER);
      mainPanel.add(northPanel, BorderLayout.NORTH);
      northPanel.add(infoQnrLabel,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      northPanel.add(jLabel2,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      northPanel.add(jLabel3,   new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      northPanel.add(infoVereinLabel,   new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      mainPanel.add(southPanel, BorderLayout.SOUTH);
      mainPanel.add(jTabbedPane1, BorderLayout.CENTER);
      jTabbedPane1.add(vereinsdatenPanel,   "Vereinsdaten");
      vereinsdatenPanel.add(vereinPanel,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 0, 10), 0, 0));
      String teilnehmerPanelTitle = null;
      switch(MELDTYP) {
        case MeldungenIndexFrame.MELD_FAHRTENABZEICHEN:
          teilnehmerPanelTitle = "Teilnehmer";
          break;
        case MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK:
          teilnehmerPanelTitle = "Fahrten";
          break;
      }
      jTabbedPane1.add(teilnehmerPanel,teilnehmerPanelTitle);
      teilnehmerPanel.add(tailnNaviPanel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      tailnNaviPanel.add(deleteButton,         new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      tailnNaviPanel.add(newButton,        new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      tailnNaviPanel.add(lastButton,       new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      tailnNaviPanel.add(nextButton,      new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      tailnNaviPanel.add(prevButton,     new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      tailnNaviPanel.add(firstButton,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      tailnNaviPanel.add(wsListButton,  new GridBagConstraints(4, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      southPanel.add(bestaetigenButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      southPanel.add(closeButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      vereinPanel.add(jLabel1,         new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      vereinPanel.add(vMitgliedsnr,       new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      vereinPanel.add(jLabel4,       new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      vereinPanel.add(vVereinsname,     new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      vereinPanel.add(vNutzername,     new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      vereinPanel.add(jLabel5,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      vereinsdatenPanel.add(meldenderPanel,      new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
      meldenderPanel.add(jLabel9,     new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      meldenderPanel.add(vMeldenderKonto,     new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      meldenderPanel.add(vMeldenderKontoLabel,          new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      meldenderPanel.add(vMeldenderBankLabel,      new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      meldenderPanel.add(vMeldenderBank,        new GridBagConstraints(3, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      meldenderPanel.add(vMeldenderBlzLabel,       new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      meldenderPanel.add(vMeldenderBlz,      new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      meldenderPanel.add(vMeldenderName,       new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      meldenderPanel.add(jLabel12,        new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      meldenderPanel.add(vMeldenderEmail,       new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      vereinsdatenPanel.add(versandPanel,      new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
      versandPanel.add(jLabel6,      new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      versandPanel.add(jLabel7,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      versandPanel.add(jLabel13,   new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      versandPanel.add(vVersandName,   new GridBagConstraints(1, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      versandPanel.add(vVersantStrasse,  new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      versandPanel.add(vVersandOrt,  new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      vereinsdatenPanel.add(bestellungenPanel,      new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
      bestellungenPanel.add(jLabel14,     new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      bestellungenPanel.add(jLabel15,  new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      bestellungenPanel.add(jLabel16,   new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      bestellungenPanel.add(vBestNadelErwSilber,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      bestellungenPanel.add(jLabel17,   new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      bestellungenPanel.add(vBestNadelErwGold,  new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      bestellungenPanel.add(jLabel18,   new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      bestellungenPanel.add(vBestNadelJugSilber,  new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      bestellungenPanel.add(jLabel19,   new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      bestellungenPanel.add(vBestNadelJugGold,  new GridBagConstraints(8, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      bestellungenPanel.add(jLabel20,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      bestellungenPanel.add(vBestStoffErw,   new GridBagConstraints(4, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      bestellungenPanel.add(jLabel21,   new GridBagConstraints(5, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      bestellungenPanel.add(vBestStoffJug,  new GridBagConstraints(6, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      bestellungenPanel.add(printStoffBestellButton,    new GridBagConstraints(5, 2, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      vereinsdatenPanel.add(vUnblockButton,      new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 0, 10, 0), 0, 0));
      vereinsdatenPanel.add(zusammenfassungPanel,    new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 10, 0, 10), 0, 0));
      zusammenfassungPanel.add(vZusGemTeilnehmer,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(vZusammenfassungAnzTeilnehmer,    new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(vZusTeilnehmerErfuellt,      new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(vZusammenfassungAnzTeilnehmerErfuellt,    new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(vZusTeilnehmerUngueltig,       new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(vZusammenfassungAnzTeilnehmerUngueltig,     new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(vBetragMeldLabel,      new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
      zusammenfassungPanel.add(vZusammenfassungMeldegebuehr,    new GridBagConstraints(3, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(vBetragStoffLabel,       new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
      zusammenfassungPanel.add(vZusammenfassungStoffabzeichen,     new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(papierFahrtenhefteErforderlichLabel,             new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 5, 0));
      zusammenfassungPanel.add(vBetragGesamtLabel,     new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));
      zusammenfassungPanel.add(vZusammenfassungEurGesamt,   new GridBagConstraints(3, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(vBetragLabel,   new GridBagConstraints(2, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(vZusTeilnehmer,   new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(aequatorButton,  new GridBagConstraints(0, 6, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(vMeldegeldEingegangen,   new GridBagConstraints(6, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 5, 0, 0), 0, 0));
      zusammenfassungPanel.add(vEingPapierhefteLabel,   new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
      zusammenfassungPanel.add(vAnzahlPapierFahrtenhefte,    new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(8, 0, 0, 0), 0, 0));
      vereinsdatenPanel.add(dateiPanel,    new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 10, 10, 10), 0, 0));
      dateiPanel.add(jLabel49, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      dateiPanel.add(vKennung, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      dateiPanel.add(jLabel50,  new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      dateiPanel.add(vProgramm, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      switch(MELDTYP) {
        case MeldungenIndexFrame.MELD_FAHRTENABZEICHEN:
          teilnehmerPanel.add(teilnDataPanel,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 0, 0), 0, 0));
          wsListButton.setVisible(false);
          break;
        case MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK:
          teilnehmerPanel.add(fahrtenDataPanel,  new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
                ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(20, 0, 0, 0), 0, 0));
          break;
      }

      fahrtenDataPanel.add(jLabel8,      new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fStartZiel,       new GridBagConstraints(1, 0, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel10,     new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fGewaesser,      new GridBagConstraints(1, 1, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel11,     new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fKilometer,        new GridBagConstraints(5, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel55,      new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(fTage,    new GridBagConstraints(2, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel56,      new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(fTeilnehmer,     new GridBagConstraints(2, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fMannschKm,      new GridBagConstraints(4, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel31,                      new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel32,                     new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel33,                    new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel34,                    new GridBagConstraints(0, 10, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel35,                   new GridBagConstraints(0, 11, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel36,                 new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel37,                new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel38,               new GridBagConstraints(0, 6, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel39,              new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel40,              new GridBagConstraints(0, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel41,             new GridBagConstraints(0, 8, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mNachname,            new GridBagConstraints(2, 1, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mVorname,            new GridBagConstraints(2, 2, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mJahrgang,            new GridBagConstraints(2, 3, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mGeschlecht,            new GridBagConstraints(2, 4, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mGruppe,            new GridBagConstraints(2, 5, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mKilometer,            new GridBagConstraints(2, 6, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mAbzeichen,         new GridBagConstraints(2, 7, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mAequatorpreis,          new GridBagConstraints(2, 8, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mAnzAbzeichen,          new GridBagConstraints(2, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mGesKm,          new GridBagConstraints(2, 10, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel30,        new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 10), 0, 0));
      teilnDataPanel.add(jLabel42,           new GridBagConstraints(7, 0, 6, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel43,          new GridBagConstraints(7, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel44,          new GridBagConstraints(8, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel45,          new GridBagConstraints(9, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel46,           new GridBagConstraints(10, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel47,           new GridBagConstraints(11, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel48,           new GridBagConstraints(12, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel51,        new GridBagConstraints(3, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel52,      new GridBagConstraints(4, 10, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      teilnDataPanel.add(mAnzAbzeichenAB,    new GridBagConstraints(5, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mGesKmAB,   new GridBagConstraints(6, 10, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mUnblockButton,     new GridBagConstraints(1, 12, 13, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mFahrtenheft,     new GridBagConstraints(2, 11, 8, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mWirdGewertet,   new GridBagConstraints(0, 13, 4, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(jLabel53,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mTeilnNr,  new GridBagConstraints(2, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      teilnehmerPanel.add(teilnWarnPanel,   new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
      teilnWarnPanel.add(jScrollPane1, BorderLayout.CENTER);
      jScrollPane1.getViewport().add(mWarnungen, null);
      teilnDataPanel.add(mTeilnSuchenButton,   new GridBagConstraints(4, 0, 3, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mNichtGewertetGrundLabel,    new GridBagConstraints(4, 13, 4, 1, 0.0, 0.0
            ,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      teilnDataPanel.add(mNichtGewertetGrund,   new GridBagConstraints(8, 13, 5, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel58,    new GridBagConstraints(0, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel59,     new GridBagConstraints(0, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel60,     new GridBagConstraints(0, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel61,     new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel62,     new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel63,     new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel64,     new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel65,     new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(fMaennerAnz,    new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fJuniorenAnz,    new GridBagConstraints(2, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fFrauenAnz,    new GridBagConstraints(2, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fJuniorinnenAnz,    new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fMaennerKm,     new GridBagConstraints(4, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fJuniorenKm,     new GridBagConstraints(4, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fFrauenKm,     new GridBagConstraints(4, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fJuniorinnenKm,     new GridBagConstraints(4, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel66,    new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel67,     new GridBagConstraints(3, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel68,     new GridBagConstraints(3, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel69,     new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel57,     new GridBagConstraints(3, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel70,     new GridBagConstraints(3, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 10, 0, 0), 0, 0));
      fahrtenDataPanel.add(fUnblockButton,        new GridBagConstraints(0, 8, 7, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fWirdGewertet,     new GridBagConstraints(0, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fNichtGewertetGrundLabel,   new GridBagConstraints(3, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(fNichtGewertetGrund,   new GridBagConstraints(5, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      fahrtenDataPanel.add(jLabel22,   new GridBagConstraints(0, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
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
    if (!checkAndSaveChangedMeldung()) return;
    if (changed) {
      switch(Dialog.yesNoCancelDialog("�nderungen speichern","Sollen die �nderungen an der Meldedatei gespeichert werden?")) {
        case Dialog.YES:
          if (!saveMeldedatei()) {
            Logger.log(Logger.ERROR,"Speichern der Meldedatei ist fehlgeschlagen!");
            Dialog.error("Speichern der Meldedatei ist fehlgeschlagen!");
            return;
          }
        case Dialog.NO:
          break;
        default: return;
      }
    }
    log(false,"ENDE Bearbeiten der Meldung");
    Dialog.frameClosed(this);
    dispose();
  }

  /**Close the dialog on a button event*/
  public void actionPerformed(ActionEvent e) {
  }

  void iniFields() {
    switch(MELDTYP) {
      case MeldungenIndexFrame.MELD_FAHRTENABZEICHEN:
        this.mGeschlecht.addItem("ung�ltig"); hGeschlecht.put("",new Integer(0));
        this.mGeschlecht.addItem("m�nnlich"); hGeschlecht.put(EfaWettMeldung.GESCHLECHT_M,new Integer(1));
        this.mGeschlecht.addItem("weiblich"); hGeschlecht.put(EfaWettMeldung.GESCHLECHT_W,new Integer(2));

        this.mGruppe.addItem("ung�ltig");             hGruppe.put("",new Integer(0));
        this.mGruppe.addItem("1a (M�nner 19-30)");    hGruppe.put("1a",new Integer(1));
        this.mGruppe.addItem("1b (M�nner 31-60)");    hGruppe.put("1b",new Integer(2));
        this.mGruppe.addItem("1c (M�nner 61-??)");    hGruppe.put("1c",new Integer(3));
        this.mGruppe.addItem("1x (M�nner 50% Beh.)"); hGruppe.put("1 (50% Behinderung)",new Integer(4));
        this.mGruppe.addItem("2a (Frauen 19-30)");    hGruppe.put("2a",new Integer(5));
        this.mGruppe.addItem("2b (Frauen 31-60)");    hGruppe.put("2b",new Integer(6));
        this.mGruppe.addItem("2c (Frauen 61-??)");    hGruppe.put("2c",new Integer(7));
        this.mGruppe.addItem("2x (Frauen 50% Beh.)"); hGruppe.put("2 (50% Behinderung)",new Integer(8));
        this.mGruppe.addItem("3a (Jugend 8-10)");     hGruppe.put("3a",new Integer(9));
        this.mGruppe.addItem("3b (Jugend 11-12)");    hGruppe.put("3b",new Integer(10));
        this.mGruppe.addItem("3c (Jugend 13-14)");    hGruppe.put("3c",new Integer(11));
        this.mGruppe.addItem("3d (Jugend 15-16)");    hGruppe.put("3d",new Integer(12));
        this.mGruppe.addItem("3e (Jugend 17-18)");    hGruppe.put("3e",new Integer(13));

        hAbzeichen.put("","ung�ltig");
        hAbzeichen.put(EfaWettMeldung.ABZEICHEN_ERW_EINF,"Erwachsene einfach");
        hAbzeichen.put(EfaWettMeldung.ABZEICHEN_ERW_GOLD_PRAEFIX,"Erwachsene gold");
        hAbzeichen.put(EfaWettMeldung.ABZEICHEN_JUG_EINF,"Jugend einfach");
        hAbzeichen.put(EfaWettMeldung.ABZEICHEN_JUG_GOLD_PRAEFIX,"Jugend gold");
        for (int i=1; i<EfaWettMeldung.ABZEICHEN_ERW_GOLD_LIST.length; i++) {
          hAbzeichen.put(EfaWettMeldung.ABZEICHEN_ERW_GOLD_LIST[i],"Erwachsene gold ("+(i*5)+")");
        }
        for (int i=1; i<EfaWettMeldung.ABZEICHEN_JUG_GOLD_LIST.length; i++) {
          hAbzeichen.put(EfaWettMeldung.ABZEICHEN_JUG_GOLD_LIST[i],"Jugend gold ("+(i*5)+")");
        }

        hAequator.put("","nein");
        hAequator.put("1","1. Erreichen");
        hAequator.put("2","(2. Erreichen)");
        hAequator.put("3","3. Erreichen");
        hAequator.put("4","(4. Erreichen)");
        hAequator.put("5","(5. Erreichen)");
        hAequator.put("6","(6. Erreichen)");
        hAequator.put("7","(7. Erreichen)");
        hAequator.put("8","(8. Erreichen)");
        hAequator.put("9","(9. Erreichen)");
        hAequator.put("10","(10. Erreichen)");
        JTextField t;
        mFahrten = new JTextField[MAX_FAHRTEN][6];
        for (int i=0; i<MAX_FAHRTEN; i++) {
          t = new JTextField();
          t.setPreferredSize(new Dimension(40,17));
          teilnDataPanel.add(t,  new GridBagConstraints(7, 2+i, 1, 1, 0.0, 0.0
                ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
          mFahrten[i][0] = t;

          t = new JTextField();
          t.setPreferredSize(new Dimension(80,17));
          teilnDataPanel.add(t,  new GridBagConstraints(8, 2+i, 1, 1, 0.0, 0.0
                ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
          mFahrten[i][1] = t;

          t = new JTextField();
          t.setPreferredSize(new Dimension(80,17));
          teilnDataPanel.add(t,  new GridBagConstraints(9, 2+i, 1, 1, 0.0, 0.0
                ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
          mFahrten[i][2] = t;

          t = new JTextField();
          t.setPreferredSize(new Dimension(200,17));
          teilnDataPanel.add(t,  new GridBagConstraints(10, 2+i, 1, 1, 0.0, 0.0
                ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
          mFahrten[i][3] = t;

          t = new JTextField();
          t.setPreferredSize(new Dimension(50,17));
          teilnDataPanel.add(t,  new GridBagConstraints(11, 2+i, 1, 1, 0.0, 0.0
                ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
          mFahrten[i][4] = t;

          t = new JTextField();
          t.setPreferredSize(new Dimension(70,17));
          teilnDataPanel.add(t,  new GridBagConstraints(12, 2+i, 1, 1, 0.0, 0.0
                ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
          mFahrten[i][5] = t;
        }

        teilnDataPanel.add(mFahrtnachweisErbracht,  new GridBagConstraints(7, 2+MAX_FAHRTEN, 6, 1, 0.0, 0.0
                ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
        break;
      case MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK:
        bestellungenPanel.setVisible(false);

        vMeldenderKonto.setVisible(false);
        vMeldenderKontoLabel.setVisible(false);
        vMeldenderBank.setVisible(false);
        vMeldenderBankLabel.setVisible(false);
        vMeldenderBlz.setVisible(false);
        vMeldenderBlzLabel.setVisible(false);

        this.vZusTeilnehmer.setText("Fahrten");
        this.vZusGemTeilnehmer.setText("gemeldete Fahrten: ");
        this.papierFahrtenhefteErforderlichLabel.setVisible(false);
        this.vBetragLabel.setVisible(false);
        this.vBetragGesamtLabel.setVisible(false);
        this.vBetragMeldLabel.setVisible(false);
        this.vBetragStoffLabel.setVisible(false);
        this.vZusammenfassungEurGesamt.setVisible(false);
        this.vZusammenfassungMeldegebuehr.setVisible(false);
        this.vZusammenfassungStoffabzeichen.setVisible(false);
        this.vEingPapierhefteLabel.setVisible(false);
        this.vAnzahlPapierFahrtenhefte.setVisible(false);
        this.aequatorButton.setVisible(false);
        this.vMeldegeldEingegangen.setVisible(false);

        break;
    }
  }

  void log(boolean teilnehmerspezifisch, String s) {
    if (teilnehmerspezifisch && ewmCur != null) {
      switch(MELDTYP) {
        case MeldungenIndexFrame.MELD_FAHRTENABZEICHEN:
          s = "QNr "+qnr+" - Teilnehmer "+ewmCur.vorname+" "+ewmCur.nachname+": "+s;
          break;
        case MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK:
          s = "QNr "+qnr+" - Fahrt "+ewmCur.drvWS_StartZiel+": "+s;
          break;
      }
    }
    else s = "QNr "+qnr+": "+s;
    Logger.log(Logger.INFO,s);
  }

  void warnung(String s) {
    mWarnungen.append(s+"\n");
    log(true,s);
  }

  String notNull(String s) {
   if (s == null) return "";
   else return s;
  }

  void printDiff(String field, String olds, String news) {
    if (olds == null && news == null) return;
    if ((olds == null || news == null) ||
        !olds.equals(news))
      log(true,"Wert f�r Feld '"+field+"' ge�ndert: alt='"+(olds == null ? "" : olds)+"', neu='"+(news == null ? "" : news)+"'");
  }

  void readMeldedatei() {
    data = new Vector();
    for (EfaWettMeldung ewm = ew.meldung; ewm != null; ewm = ewm.next) {
      try {
        if (ewm.drv_fahrtenheft != null && ewm.drv_fahrtenheft.length() > 0) {
          ewm.drvSignatur = new DRVSignatur(ewm.drv_fahrtenheft);
          ewm.drvSignatur.checkSignature();
          if (ewm.drvSignatur.getSignatureState() == DRVSignatur.SIG_VALID) {
            ewm.drv_anzAbzeichen = Integer.toString(ewm.drvSignatur.getAnzAbzeichen());
            ewm.drv_gesKm = Integer.toString(ewm.drvSignatur.getGesKm());
            ewm.drv_anzAbzeichenAB = Integer.toString(ewm.drvSignatur.getAnzAbzeichenAB());
            ewm.drv_gesKmAB = Integer.toString(ewm.drvSignatur.getGesKmAB());
            ewm.drv_teilnNr = ewm.drvSignatur.getTeilnNr();
            if (ewm.drvSignatur.getJahr() >= drvConfig.aktJahr) {
              ewm.sigError = (ewm.sigError == null ? "" : ewm.sigError+"\n") +
                             "Fahrtenheft des Teilnehmers wurde f�r eine Meldung des Jahres "+ewm.drvSignatur.getJahr()+" ausgestellt und kann daher im aktuellen Meldejahr "+drvConfig.aktJahr+" nicht bearbeitet werden.";
              ewm.sigValid = false;
            } else {
              ewm.sigValid = true;
            }
          } else {
            ewm.sigError = (ewm.sigError == null ? "" : ewm.sigError+"\n") +
                           "Signatur des Fahrtenhefts ist ung�ltig: "+ewm.drvSignatur.getSignatureError();
            ewm.sigValid = false;
          }
        }
      } catch(Exception e) {
        ewm.sigError = (ewm.sigError == null ? "" : ewm.sigError+"\n") +
                       "Fehler beim �berpr�fen der Signatur des Fahrtenhefts: "+e.getMessage();
        ewm.sigValid = false;
      }
      switch(MELDTYP) {
        case MeldungenIndexFrame.MELD_FAHRTENABZEICHEN:
          if (!ewm.drvint_wirdGewertetExplizitGesetzt || ewm.drvint_wirdGewertet) {
            // Die Eigenschaft "wirdGewertet" kann auch vom DRV durch eine fr�here Bearbeitung bereits in die Datei
            // geschrieben worden sein. In diesem Fall hat "wirdGewertetExplizitGesetzt" den Wert "true".
            // Beim Einlesen der Meldedatei wird die Eigenschaft "wirdGewertet" nur dann von efa neu bestimmt, wenn
            // sie nicht zuvor explizit gesetzt war, oder wenn sie "true" ist (in diesem Fall bleibt das "true" nur
            // erhalten, wenn die Meldung wirklich g�ltig ist).
            ewm.drvint_wirdGewertet = ((ewm.sigValid || ewm.drvSignatur == null) && isErfuellt(ewm,false));
          }
          break;
        case MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK:
          if (!ewm.drvint_wirdGewertetExplizitGesetzt || ewm.drvint_wirdGewertet) {
            // Die Eigenschaft "wirdGewertet" kann auch vom DRV durch eine fr�here Bearbeitung bereits in die Datei
            // geschrieben worden sein. In diesem Fall hat "wirdGewertetExplizitGesetzt" den Wert "true".
            // Beim Einlesen der Meldedatei wird die Eigenschaft "wirdGewertet" nur dann von efa neu bestimmt, wenn
            // sie nicht zuvor explizit gesetzt war, oder wenn sie "true" ist (in diesem Fall bleibt das "true" nur
            // erhalten, wenn die Meldung wirklich g�ltig ist).
            ewm.drvint_wirdGewertet = isErfuellt(ewm,false);
          }
          ewm.drvint_geprueft = (checkAndCorrectWSAnz(ewm,false) == null) && (checkAndCorrectWSKm(ewm,false) == null) && ewm.drvint_wirdGewertet;
          break;
      }
      data.add(ewm);
    }
  }

  void replaceMeldung() {
    ewmCur.changed = false;
    EfaWettMeldung ewmOld = (EfaWettMeldung)data.get(ewmNr);
    if (ewmOld != null) {
      printDiff("Vorname",ewmOld.vorname,ewmCur.vorname);
      printDiff("Nachname",ewmOld.nachname,ewmCur.nachname);
      printDiff("Jahrgang",ewmOld.jahrgang,ewmCur.jahrgang);
      printDiff("Geschlecht",ewmOld.geschlecht,ewmCur.geschlecht);
      printDiff("Gruppe",ewmOld.gruppe,ewmCur.gruppe);
      printDiff("Kilometer",ewmOld.kilometer,ewmCur.kilometer);
      printDiff("Abzeichen",ewmOld.abzeichen,ewmCur.abzeichen);
      printDiff("AnzAbzeichen",ewmOld.drv_anzAbzeichen,ewmCur.drv_anzAbzeichen);
      printDiff("GesKm",ewmOld.drv_gesKm,ewmCur.drv_gesKm);
      printDiff("AnzAbzeichenAB",ewmOld.drv_anzAbzeichenAB,ewmCur.drv_anzAbzeichenAB);
      printDiff("GesKmAB",ewmOld.drv_gesKmAB,ewmCur.drv_gesKmAB);
      printDiff("Fahrtenheft",ewmOld.drv_fahrtenheft,ewmCur.drv_fahrtenheft);
      printDiff("�quatorpreis",ewmOld.drv_aequatorpreis,ewmCur.drv_aequatorpreis);
      printDiff("Weg und Ziel der Fahrt",ewmOld.drvWS_StartZiel,ewmCur.drvWS_StartZiel);
      printDiff("Gew�sser",ewmOld.drvWS_Gewaesser,ewmCur.drvWS_Gewaesser);
      printDiff("Anzahl Teilnehmer",ewmOld.drvWS_Teilnehmer,ewmCur.drvWS_Teilnehmer);
      printDiff("Kilometer",ewmOld.drvWS_Km,ewmCur.drvWS_Km);
      printDiff("Tage",ewmOld.drvWS_Tage,ewmCur.drvWS_Tage);
      printDiff("Mannschafs-Kilometer",ewmOld.drvWS_MannschKm,ewmCur.drvWS_MannschKm);
      printDiff("M�nner (Anzahl)",ewmOld.drvWS_MaennerAnz,ewmCur.drvWS_MaennerAnz);
      printDiff("M�nner (Kilometer)",ewmOld.drvWS_MaennerKm,ewmCur.drvWS_MaennerKm);
      printDiff("Junioren (Anzahl)",ewmOld.drvWS_JuniorenAnz,ewmCur.drvWS_JuniorenAnz);
      printDiff("Junioren (Kilometer)",ewmOld.drvWS_JuniorenKm,ewmCur.drvWS_JuniorenKm);
      printDiff("Frauen (Anzahl)",ewmOld.drvWS_FrauenAnz,ewmCur.drvWS_FrauenAnz);
      printDiff("Frauen (Kilometer)",ewmOld.drvWS_FrauenKm,ewmCur.drvWS_FrauenKm);
      printDiff("Juniorinnen (Anzahl)",ewmOld.drvWS_JuniorinnenAnz,ewmCur.drvWS_JuniorinnenAnz);
      printDiff("Juniorinnen (Kilometer)",ewmOld.drvWS_JuniorinnenKm,ewmCur.drvWS_JuniorinnenKm);
      for (int i=0; i<ewmOld.fahrt.length && i<ewmCur.fahrt.length; i++)
        for (int j=0; j<ewmOld.fahrt[i].length && j<ewmCur.fahrt[i].length; j++)
          printDiff("Fahrt["+i+"]["+j+"]",ewmOld.fahrt[i][j],ewmCur.fahrt[i][j]);
    }
    data.remove(ewmNr);
    data.add(ewmNr,ewmCur);
    changed = true;
  }

  int getGeschlechtIndex(String s) {
    if (s == null) {
      warnung("Ung�ltiger Wert f�r Geschlecht: "+s);
      return 0;
    }
    Integer i = (Integer)hGeschlecht.get(s);
    if (i == null) {
      warnung("Ung�ltiger Wert f�r Geschlecht: "+s);
      return 0;
    }
    else return i.intValue();
  }

  int getGruppeIndex(EfaWettMeldung ewm) {
    if (ewm.gruppe == null) {
      warnung("Ung�ltiger Wert f�r Gruppe: "+ewm.gruppe);
      return 0;
    }
    Integer i = (Integer)hGruppe.get(ewm.gruppe);
    if (i == null) {
      warnung("Ung�ltiger Wert f�r Gruppe: "+ewm.gruppe);
      return 0;
    }
    else return i.intValue();
  }

  String getAbzeichen(EfaWettMeldung ewm) {
    if (ewm.abzeichen == null) {
      warnung("Ung�ltiger Wert f�r Abzeichen: "+ewm.abzeichen);
      return "";
    }
    String s = (String)hAbzeichen.get(ewm.abzeichen);
    if (s == null) {
      warnung("Ung�ltiger Wert f�r Abzeichen: "+ewm.abzeichen);
      return "";
    }
    else return s;
  }

  String getAequator(EfaWettMeldung ewm) {
    if (ewm.drv_aequatorpreis == null) return (String)hAequator.get("");
    String s = (String)hAequator.get(ewm.drv_aequatorpreis);
    if (s == null) return (String)hAequator.get("");
    else return s;
  }

  void setFahrtenheftState(EfaWettMeldung ewm) {
    if (ewm.drvSignatur == null) {
      if (ewm.drv_anzAbzeichen != null && EfaUtil.string2int(ewm.drv_anzAbzeichen,0)>0) {
        mFahrtenheft.setText("Papier-Fahrtenheft erforderlich!");
        mFahrtenheft.setForeground(Color.red);
      } else {
        mFahrtenheft.setText("Kein Fahrtenheft erforderlich!");
        mFahrtenheft.setForeground(Daten.colorGreen);
      }
    } else {
      if (ewm.sigValid) {
        mFahrtenheft.setText("Das elektronische Fahrtenheft ist g�ltig! (Letzte Meldung: "+ewm.drvSignatur.getJahr()+")");
        mFahrtenheft.setForeground(Daten.colorGreen);
      } else {
        mFahrtenheft.setText("Das elektronische Fahrtenheft ist ung�ltig!");
        mFahrtenheft.setForeground(Color.red);
      }
    }
  }

  String findValue(Hashtable h, Object search) {
    if (h == null) return null;
    Object[] keys = h.keySet().toArray();
    for (int j=0; j<keys.length; j++) {
      Object found = h.get(keys[j]);
      if (found != null && found.equals(search)) return (String)keys[j];
    }
    return null;
  }

  String notEmpty(JTextField t) {
    String s = t.getText().trim();
    if (s.length() == 0) return null;
    return s;
  }

  String field2int(JTextField t) {
    int i = EfaUtil.string2date(t.getText(),-1,0,0).tag;
    if (i < 0) return null;
    return Integer.toString(i);
  }

  String field2jahr(JTextField t) {
    int i = EfaUtil.string2date(t.getText(),-1,0,0).tag;
    if (i < 0) return null;
    if (i < 100) i += 1900;
    return Integer.toString(i);
  }

  String checkAndCorrectAbzeichen(EfaWettMeldung ewm) {
    if (ewm == null) return "null";
    boolean erwachsen = true;
    if (ewm.gruppe != null && ewm.gruppe.startsWith("3")) erwachsen = false;
    int anzAbzeichen = EfaUtil.string2int(ewm.drv_anzAbzeichen,0);
    int anzAbzeichenAB = EfaUtil.string2int(ewm.drv_anzAbzeichenAB,0);
    String abzeichen = WettDefs.getDRVAbzeichen(erwachsen,anzAbzeichen,anzAbzeichenAB,drvConfig.aktJahr);
    if (abzeichen != null && ewm.abzeichen != null && !abzeichen.equals(ewm.abzeichen)) {
      if (ewm.abzeichen.length()==2 && abzeichen.startsWith(ewm.abzeichen)) {
        // ok (altes Format), nothing to do
      } else {
        String gem = ewm.abzeichen;
        ewm.abzeichen = abzeichen;
        ewm.changed = true;
        return "Teilnehmer hat f�r Abzeichen '"+gem+"' gemeldet, hat aber Abzeichen '"+abzeichen+"' erlangt. Abzeichen wurde korrigiert!";
      }
    }
    return null;
  }

  String checkAndCorrectAequator(EfaWettMeldung ewm) {
    int gesKm = EfaUtil.string2int(ewm.drv_gesKm,0);
    int gesKmAB = EfaUtil.string2int(ewm.drv_gesKmAB,0);
    int aeqKm = gesKm; // - gesKmAB; (seit 2007 z�hlen auch die AB-Kilometer zum �quatorpreis)
    int anzAeqBefore = aeqKm / WettDefs.DRV_AEQUATOR_KM;
    int anzAeqJetzt  = (aeqKm + EfaUtil.string2int(ewm.kilometer,0)) / WettDefs.DRV_AEQUATOR_KM;
    String aeq = null;
    if (anzAeqJetzt > anzAeqBefore) aeq = Integer.toString(anzAeqJetzt);
    if (ewm.drv_aequatorpreis != null && ewm.drv_aequatorpreis.length() == 0) ewm.drv_aequatorpreis = null;
    if (aeq != null || ewm.drv_aequatorpreis != null) {
      if (aeq == null) {
        String gem = ewm.drv_aequatorpreis;
        ewm.drv_aequatorpreis = aeq;
        ewm.changed = true;
        return "Teilnehmer hat f�r �quatorpreis '"+gem+"' gemeldet, hat ihn aber nicht erf�llt. �quatorpreis wurde korrigiert!";
      } else if (ewm.drv_aequatorpreis == null) {
        ewm.drv_aequatorpreis = aeq;
        ewm.changed = true;
        return "Teilnehmer hat f�r �quatorpreis nicht gemeldet, hat ihn aber zum "+aeq+". Mal erf�llt. �quatorpreis wurde korrigiert!";
      } else if (!aeq.equals(ewm.drv_aequatorpreis)) {
        String gem = ewm.drv_aequatorpreis;
        ewm.drv_aequatorpreis = aeq;
        ewm.changed = true;
        return "Teilnehmer hat f�r �quatorpreis '"+gem+"' gemeldet, hat ihn aber zum "+aeq+". Mal erf�llt. �quatorpreis wurde korrigiert!";
      }
    }
    return null;
  }

  String checkAnzAbzeichenUndKm(EfaWettMeldung ewm) {
    if (ewm == null) return "null";
    int anzAbzeichen = EfaUtil.string2int(ewm.drv_anzAbzeichen,0);
    int anzAbzeichenAB = EfaUtil.string2int(ewm.drv_anzAbzeichenAB,0);
    int gesKm = EfaUtil.string2int(ewm.drv_gesKm,0);
    int gesKmAB = EfaUtil.string2int(ewm.drv_gesKmAB,0);

    if (anzAbzeichenAB > anzAbzeichen) return "Der Teilnehmer darf nicht mehr A/B-Abzeichen als Gesamt-Abzeichen haben! Bitte �berpr�fen!";
    if (gesKmAB > gesKm) return "Der Teilnehmer darf nicht mehr A/B-Kilometer als Gesamt-Kilometer haben! Bitte �berpr�fen!";
    return null;
  }

  String checkAndCorrectWSAnz(EfaWettMeldung ewm, boolean korrigiere) {
    if (ewm == null) return "null";

    int maenner = EfaUtil.string2int(ewm.drvWS_MaennerAnz,0);
    int frauen = EfaUtil.string2int(ewm.drvWS_FrauenAnz,0);
    int junioren = EfaUtil.string2int(ewm.drvWS_JuniorenAnz,0);
    int juniorinnen = EfaUtil.string2int(ewm.drvWS_JuniorinnenAnz,0);
    int gesamt = EfaUtil.string2int(ewm.drvWS_Teilnehmer,0);
    if (maenner + frauen + junioren + juniorinnen != gesamt) {
      String gem = ewm.drvWS_Teilnehmer;
      if (korrigiere) {
        ewm.drvWS_Teilnehmer = Integer.toString(maenner+frauen+junioren+juniorinnen);
        ewm.changed = true;
      }
      return "Gemeldete Teilnehmer-Anzahl '"+gem+"' stimmt nicht mit Summe der einzelnen Altersklassen �berein. Teilnehmer-Anzahl wurde korrigiert!";
    }
    return null;
  }

  String checkAndCorrectWSKm(EfaWettMeldung ewm, boolean korrigiere) {
    if (ewm == null) return "null";

    int maenner = EfaUtil.zehntelString2Int(ewm.drvWS_MaennerKm);
    int frauen = EfaUtil.zehntelString2Int(ewm.drvWS_FrauenKm);
    int junioren = EfaUtil.zehntelString2Int(ewm.drvWS_JuniorenKm);
    int juniorinnen = EfaUtil.zehntelString2Int(ewm.drvWS_JuniorinnenKm);
    int gesamt = EfaUtil.zehntelString2Int(ewm.drvWS_MannschKm);
    if (maenner + frauen + junioren + juniorinnen != gesamt) {
      String gem = ewm.drvWS_MannschKm;
      if (korrigiere) {
        ewm.drvWS_MannschKm = EfaUtil.zehntelInt2String(maenner+frauen+junioren+juniorinnen);
        ewm.changed = true;
      }
      return "Gemeldete Mannschafts-Kilometer '"+gem+"' stimmen nicht mit Summe der einzelnen Altersklassen �berein. Mannschafts-Kilometer wurde korrigiert!";
    }
    return null;
  }

  void setMeldegeldEingegangen(boolean eingegangen) {
    if (eingegangen) this.vMeldegeldEingegangen.setForeground(Color.blue);
    else this.vMeldegeldEingegangen.setForeground(Color.red);
    this.vMeldegeldEingegangen.setSelected(eingegangen);
  }

  void setVFields() {
    this.infoVereinLabel.setText(ew.verein_name);
    this.infoQnrLabel.setText(qnr);

    this.vKennung.setText(ew.kennung);
    this.vProgramm.setText(ew.allg_programm);

    this.vNutzername.setText(notNull(ew.verein_user));
    this.vVereinsname.setText(notNull(ew.verein_name));
    this.vMitgliedsnr.setText(notNull(ew.verein_mitglnr));

    this.vMeldenderName.setText(notNull(ew.meld_name));
    this.vMeldenderEmail.setText(notNull(ew.meld_email));

    this.vVersandName.setText(notNull(ew.versand_name));
    this.vVersantStrasse.setText(notNull(ew.versand_strasse));
    this.vVersandOrt.setText(notNull(ew.versand_ort));

    switch (MELDTYP) {
      case MeldungenIndexFrame.MELD_FAHRTENABZEICHEN:
        this.vMeldenderKonto.setText(notNull(ew.meld_kto));
        this.vMeldenderBank.setText(notNull(ew.meld_bank));
        this.vMeldenderBlz.setText(notNull(ew.meld_blz));

        this.vBestNadelErwSilber.setText(notNull(ew.drv_nadel_erw_silber));
        if (ew.drv_nadel_erw_gold != null) {
          this.vBestNadelErwGold.setText(Integer.toString(EfaUtil.sumUpArray(EfaUtil.kommaList2IntArr(ew.drv_nadel_erw_gold,','))));
        }
        this.vBestNadelJugSilber.setText(notNull(ew.drv_nadel_jug_silber));
        if (ew.drv_nadel_jug_gold != null) {
          this.vBestNadelJugGold.setText(Integer.toString(EfaUtil.sumUpArray(EfaUtil.kommaList2IntArr(ew.drv_nadel_jug_gold,','))));
        }
        this.vBestStoffErw.setText(notNull(ew.drv_stoff_erw));
        this.vBestStoffJug.setText(notNull(ew.drv_stoff_jug));

        setMeldegeldEingegangen(ew.drvint_meldegeldEingegangen);
        if (ew.drvint_anzahlPapierFahrtenhefte>=0) this.vAnzahlPapierFahrtenhefte.setText(Integer.toString(ew.drvint_anzahlPapierFahrtenhefte));
        break;
      case MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK:
        // nothing to do
        break;
    }
    vBlock(true);
  }

  boolean checkAndSaveChangedMeldung() {
    if (ewmCur != null && (ewmCur.changed || !mBlocked)) {
      switch (Dialog.yesNoCancelDialog("�nderungen speichern",
                                       "�nderungen an Eintrag "+(ewmNr+1)+" wurden noch nicht gespeichert. Jetzt speichern?")) {
        case Dialog.YES:
          if (!mBlocked) getMFields(ewmCur);
          replaceMeldung();
          return true;
        case Dialog.NO:
          // nothing to do
          return true;
        default: // Cancel
          return false;
      }
    }
    return true;
  }

  void setMFields(int nr, boolean bestimmeWirdGewertetNeu) {
    if (nr < 0 || nr >= data.size()) return;

    if (!checkAndSaveChangedMeldung()) return;

    mWarnungen.setText("");
    mBlock(true);

    EfaWettMeldung tmpefw = (EfaWettMeldung)data.get(nr);

    if (tmpefw == null) {
      warnung("Meldung #"+(nr+1)+" ist nicht vorhanden!");
      return;
    }
    tmpefw.drvint_geprueft = true;
    ewmCur = new EfaWettMeldung(tmpefw);
    ewmNr = nr;

    switch(MELDTYP) {
      case MeldungenIndexFrame.MELD_FAHRTENABZEICHEN:
        if (ewmCur.sigError != null) warnung(ewmCur.sigError);
        if (isErfuellt(ewmCur,true)) {
          if (bestimmeWirdGewertetNeu) {
            ewmCur.drvint_wirdGewertet = true;
            tmpefw.drvint_wirdGewertet = true;
            mNichtGewertetGrund.setText("");
          }
        } else {
          if (bestimmeWirdGewertetNeu) {
            ewmCur.drvint_wirdGewertet = false;
            tmpefw.drvint_wirdGewertet = false;
          }
          if (ewmCur.drvint_nichtGewertetGrund == null) {
            ewmCur.drvint_nichtGewertetGrund = "Wettbewerbsbedingungen nicht erf�llt";
            tmpefw.drvint_nichtGewertetGrund = ewmCur.drvint_nichtGewertetGrund;
          }
          warnung("Der Teilnehmer hat die Bedingungen f�r den Wettbewerb nicht erf�llt!");
        }
        String s;
        s = checkAndCorrectAbzeichen(ewmCur); if (s != null) warnung(s);
        s = checkAndCorrectAequator(ewmCur); if (s != null) warnung(s);
        s = checkAnzAbzeichenUndKm(ewmCur); if (s != null) warnung(s);

        this.mTeilnNr.setText(notNull(ewmCur.drv_teilnNr));
        this.mNachname.setText(notNull(ewmCur.nachname));
        this.mVorname.setText(notNull(ewmCur.vorname));
        this.mJahrgang.setText(notNull(ewmCur.jahrgang));
        this.mGeschlecht.setSelectedIndex(getGeschlechtIndex(ewmCur.geschlecht));
        this.mGruppe.setSelectedIndex(getGruppeIndex(ewmCur));
        this.mKilometer.setText(notNull(ewmCur.kilometer));
        this.mAbzeichen.setText(getAbzeichen(ewmCur));
        String aeq = getAequator(ewmCur);
        this.mAequatorpreis.setText(aeq);
        mAequatorpreis.setForeground( (!aeq.startsWith("(") && !aeq.equals("nein") ? Color.blue : Color.black ) );
        this.mAnzAbzeichen.setText(notNull(ewmCur.drv_anzAbzeichen));
        this.mGesKm.setText(notNull(ewmCur.drv_gesKm));
        this.mAnzAbzeichenAB.setText(notNull(ewmCur.drv_anzAbzeichenAB));
        this.mGesKmAB.setText(notNull(ewmCur.drv_gesKmAB));
        this.mWirdGewertet.setSelected(ewmCur.drvint_wirdGewertet);
        updateStatusNichtGewertetGrund();
        this.mNichtGewertetGrund.setText((ewmCur.drvint_nichtGewertetGrund == null ? "" : ewmCur.drvint_nichtGewertetGrund));
        setFahrtenheftState(ewmCur);

        if (ewmCur.fahrt != null) {
          for (int i=0; i<ewmCur.fahrt.length && i<MAX_FAHRTEN; i++) {
            for (int j=0; ewmCur.fahrt[i] != null && j<ewmCur.fahrt[i].length && j<6; j++) {
              mFahrten[i][j].setText(ewmCur.fahrt[i][j]);
            }
          }
          mFahrtnachweisErbracht.setSelected(ewmCur.drvint_fahrtErfuellt);
          int anzFahrten = 0;
          for (int i=0; i<ewmCur.fahrt.length; i++) {
            if (ewmCur.fahrt[i] != null && ewmCur.fahrt[i].length>0 && ewmCur.fahrt[i][0] != null) anzFahrten = i;
          }

          if (anzFahrten>=MAX_FAHRTEN) warnung("F�r den Teilnehmer sind mehr als "+MAX_FAHRTEN+" Fahrten angegeben; die restlichen Fahrten werden ignoriert.");
        }
        this.titledBorderTeilnehmer.setTitle("Teilnehmer " + (nr+1)+" / "+data.size());
        this.teilnDataPanel.repaint();
        break;
      case MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK:
        if (isErfuellt(ewmCur,true)) {
          if (bestimmeWirdGewertetNeu) {
            ewmCur.drvint_wirdGewertet = true;
            tmpefw.drvint_wirdGewertet = true;
            fNichtGewertetGrund.setText("");
          }
        } else {
          if (bestimmeWirdGewertetNeu) {
            ewmCur.drvint_wirdGewertet = false;
            tmpefw.drvint_wirdGewertet = false;
          }
          if (ewmCur.drvint_nichtGewertetGrund == null) {
            ewmCur.drvint_nichtGewertetGrund = "Wettbewerbsbedingungen nicht erf�llt";
            tmpefw.drvint_nichtGewertetGrund = ewmCur.drvint_nichtGewertetGrund;
          }
          warnung("Die Fahrt entspricht nicht den geforderten Bedingungen f�r eine Wanderfahrt!");
        }
        s = checkAndCorrectWSAnz(ewmCur,true); if (s != null) warnung(s);
        s = checkAndCorrectWSKm(ewmCur,true); if (s != null) warnung(s);

        this.fStartZiel.setText(notNull(ewmCur.drvWS_StartZiel));
        this.fGewaesser.setText(notNull(ewmCur.drvWS_Gewaesser));
        this.fTage.setText(notNull(ewmCur.drvWS_Tage));
        this.fKilometer.setText(notNull(ewmCur.drvWS_Km));
        this.fMaennerAnz.setText(notNull(ewmCur.drvWS_MaennerAnz));
        this.fMaennerKm.setText(notNull(ewmCur.drvWS_MaennerKm));
        this.fJuniorenAnz.setText(notNull(ewmCur.drvWS_JuniorenAnz));
        this.fJuniorenKm.setText(notNull(ewmCur.drvWS_JuniorenKm));
        this.fFrauenAnz.setText(notNull(ewmCur.drvWS_FrauenAnz));
        this.fFrauenKm.setText(notNull(ewmCur.drvWS_FrauenKm));
        this.fJuniorinnenAnz.setText(notNull(ewmCur.drvWS_JuniorinnenAnz));
        this.fJuniorinnenKm.setText(notNull(ewmCur.drvWS_JuniorinnenKm));
        this.fTeilnehmer.setText(notNull(ewmCur.drvWS_Teilnehmer));
        this.fMannschKm.setText(notNull(ewmCur.drvWS_MannschKm));

        this.fWirdGewertet.setSelected(ewmCur.drvint_wirdGewertet);
        updateStatusNichtGewertetGrund();
        this.fNichtGewertetGrund.setText((ewmCur.drvint_nichtGewertetGrund == null ? "" : ewmCur.drvint_nichtGewertetGrund));

        this.titledBorderFahrten.setTitle("Fahrt " + (nr+1)+" / "+data.size());
        this.fahrtenDataPanel.repaint();
        break;
    }

    ewmNr = nr;
  }

  void getMFields(EfaWettMeldung ewm) {
    switch(MELDTYP) {
      case MeldungenIndexFrame.MELD_FAHRTENABZEICHEN:
        // Teilnnr �berpr�fen
        if (mTeilnNr.getText().trim().length() > 0 && drvConfig.teilnehmer != null) {
          DatenFelder d = drvConfig.teilnehmer.getExactComplete(mTeilnNr.getText().trim());
          if (d != null) {
            if (!d.get(Teilnehmer.VORNAME).equals(mVorname.getText().trim()) ||
                !d.get(Teilnehmer.NACHNAME).equals(mNachname.getText().trim()) ||
                !d.get(Teilnehmer.JAHRGANG).equals(mJahrgang.getText().trim())) {
              if (Dialog.yesNoDialog("Warnung","Es existiert bereits ein Teilnehmer mit der Nummer "+d.get(Teilnehmer.TEILNNR)+",\n"+
                                     "jedoch hat dieser Teilnehmer andere Daten:\n"+
                                     "'"+d.get(Teilnehmer.VORNAME)+" "+d.get(Teilnehmer.NACHNAME)+" ("+d.get(Teilnehmer.JAHRGANG)+")'\n"+
                                     "Soll der aktuell gew�hlte Teilnehmer dennoch diese Teilnehmernummer erhalten?\n"+
                                     "(Bitte w�hle nur JA, wenn es sich bei beiden Teilnehmern um denselben Teilnehmer handelt!)") != Dialog.YES) {
                return;
              }
            }
          }
        }

        // Felder speichern
        ewm.drvint_wirdGewertet = mWirdGewertet.isSelected();
        if (ewm.drvint_wirdGewertet) ewm.drvint_nichtGewertetGrund = null;
        else ewm.drvint_nichtGewertetGrund = mNichtGewertetGrund.getText().trim();
        ewm.drv_teilnNr = notEmpty(mTeilnNr);
        ewm.nachname = notEmpty(mNachname);
        ewm.vorname = notEmpty(mVorname);
        ewm.jahrgang = field2jahr(mJahrgang);
        ewm.geschlecht = findValue(hGeschlecht,new Integer(mGeschlecht.getSelectedIndex()));
        ewm.gruppe = findValue(hGruppe,new Integer(mGruppe.getSelectedIndex()));
        ewm.kilometer = field2int(mKilometer);
        ewm.drv_aequatorpreis = findValue(hAequator,mAequatorpreis.getText());
        ewm.drv_anzAbzeichen = field2int(mAnzAbzeichen);
        ewm.drv_gesKm = field2int(mGesKm);
        ewm.drv_anzAbzeichenAB = field2int(mAnzAbzeichenAB);
        ewm.drv_gesKmAB = field2int(mGesKmAB);

        if (ewm.drvSignatur != null && ewm.drvSignatur.getSignatureState() == DRVSignatur.SIG_VALID) {
          if (!ewm.drv_anzAbzeichen.equals(Integer.toString(ewm.drvSignatur.getAnzAbzeichen())) ||
              !ewm.drv_gesKm.equals(Integer.toString(ewm.drvSignatur.getGesKm())) ||
              !ewm.drv_anzAbzeichenAB.equals(Integer.toString(ewm.drvSignatur.getAnzAbzeichenAB())) ||
              !ewm.drv_gesKmAB.equals(Integer.toString(ewm.drvSignatur.getGesKmAB()))) {
            if (Dialog.auswahlDialog("Achtung",
                                     "Du hast die durch das elektronische Fahrtenheft nachgewiesene Anzahl an\n"+
                                     "bereits erbrachten Abzeichen und Kilometern ver�ndert, so da� diese nicht\n"+
                                     "mehr mit dem elektronischen Fahrtenheft des Vorjahres �bereinstimmen.\n"+
                                     "Wenn Du diese �nderungen �bernehmen m�chtest, wird das elektronische Fahrtenheft\n"+
                                     "des Vorjahres ignoriert.\n\n"+
                                     "Was m�chtest Du tun?",
                                     "�nderungen verwerfen und elektr. Fahrtenheft beibehalten",
                                     "�nderungen �bernehmen und elektr. Fahrtenheft ignorieren",
                                     false) == 0) {
              // �nderungen verwerfen und elektr. Fahrtenheft beibehalten
              ewm.drv_anzAbzeichen = Integer.toString(ewm.drvSignatur.getAnzAbzeichen());
              ewm.drv_gesKm = Integer.toString(ewm.drvSignatur.getGesKm());
              ewm.drv_anzAbzeichenAB = Integer.toString(ewm.drvSignatur.getAnzAbzeichenAB());
              ewm.drv_gesKmAB = Integer.toString(ewm.drvSignatur.getGesKmAB());
            } else {
              // �nderungen �bernehmen und elektr. Fahrtenheft ignorieren
              ewm.drvSignatur = null;
              ewm.drv_fahrtenheft = null;
            }
          }
        }

        if (mAbzeichen.getText().trim().length()>0) {
          ewm.abzeichen = findValue(hAbzeichen,mAbzeichen.getText());
        } else {
          ewm.abzeichen = this.getAbzeichen(ewm);
          mAbzeichen.setText(ewm.abzeichen);
        }
        ewm.fahrt = new String[EfaWettMeldung.FAHRT_ANZ_X][EfaWettMeldung.FAHRT_ANZ_Y];
        for (int i=0; i<mFahrten.length; i++) {
          boolean empty = true;
          for (int j=0; j<mFahrten[i].length; j++) {
            if (mFahrten[i][j].getText().trim().length() > 0) empty = false;
          }
          if (!empty) {
            for (int j=0; j<mFahrten[i].length; j++) {
              ewm.fahrt[i][j] = mFahrten[i][j].getText().trim();
            }
          }
        }
        ewm.drvint_fahrtErfuellt = mFahrtnachweisErbracht.isSelected();

        break;
      case MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK:
        ewm.drvint_wirdGewertet = fWirdGewertet.isSelected();
        if (ewm.drvint_wirdGewertet) ewm.drvint_nichtGewertetGrund = null;
        else ewm.drvint_nichtGewertetGrund = fNichtGewertetGrund.getText().trim();

        ewm.drvWS_StartZiel = notEmpty(this.fStartZiel);
        ewm.drvWS_Gewaesser = notEmpty(this.fGewaesser);
        ewm.drvWS_Tage = notEmpty(this.fTage);
        ewm.drvWS_Km = notEmpty(this.fKilometer);
        ewm.drvWS_MaennerAnz = notEmpty(this.fMaennerAnz);
        ewm.drvWS_MaennerKm = notEmpty(this.fMaennerKm);
        ewm.drvWS_JuniorenAnz = notEmpty(this.fJuniorenAnz);
        ewm.drvWS_JuniorenKm = notEmpty(this.fJuniorenKm);
        ewm.drvWS_FrauenAnz = notEmpty(this.fFrauenAnz);
        ewm.drvWS_FrauenKm = notEmpty(this.fFrauenKm);
        ewm.drvWS_JuniorinnenAnz = notEmpty(this.fJuniorinnenAnz);
        ewm.drvWS_JuniorinnenKm = notEmpty(this.fJuniorinnenKm);
        ewm.drvWS_Teilnehmer = notEmpty(this.fTeilnehmer);
        ewm.drvWS_MannschKm = notEmpty(this.fMannschKm);
        break;
    }
    ewm.changed = true;
  }

  void calcOverallValues() {
    if (data == null) return;
    int erfuellt = 0;
    meldegeld = 0;
    int stoffabzeichen = 0;
    int papierFahrtenhefteErforderlich = 0;
    int aequator = 0;

    for (int i=0; i<data.size(); i++) {
      EfaWettMeldung ewm = (EfaWettMeldung)data.get(i);
      if (isErfuellt(ewm,false)) {
        erfuellt++;
        if (ewm.gruppe != null) {
          if(ewm.gruppe.startsWith("1") || ewm.gruppe.startsWith("2")) meldegeld += drvConfig.eur_meld_erw;
          if(ewm.gruppe.startsWith("3")) meldegeld += drvConfig.eur_meld_jug;
        }
      }
      if (ewm.drv_fahrtenheft == null || ewm.drv_fahrtenheft.length()==0) {
        if (ewm.drv_anzAbzeichen != null && ewm.drv_anzAbzeichen.length()>0 &&
            EfaUtil.string2int(ewm.drv_anzAbzeichen,0)>0) papierFahrtenhefteErforderlich++;
      }
      if (ewm.drv_aequatorpreis != null && ewm.drv_aequatorpreis.length() > 0 &&
          (ewm.drv_aequatorpreis.equals("1") || ewm.drv_aequatorpreis.equals("3"))) aequator++;
    }

    int ungueltig = data.size()-erfuellt;
    if (ungueltig > 0) this.vZusammenfassungAnzTeilnehmerUngueltig.setForeground(Color.red);
    else this.vZusammenfassungAnzTeilnehmerUngueltig.setForeground(Color.black);

    this.vZusammenfassungAnzTeilnehmer.setText(Integer.toString(data.size()));
    this.vZusammenfassungAnzTeilnehmerErfuellt.setText(Integer.toString(erfuellt));
    this.vZusammenfassungAnzTeilnehmerUngueltig.setText(Integer.toString(ungueltig));

    if (MELDTYP != MeldungenIndexFrame.MELD_FAHRTENABZEICHEN) return;

    meldegeld += EfaUtil.string2int(ew.drv_nadel_erw_silber,0) * drvConfig.eur_nadel_erw_silber;
    meldegeld += EfaUtil.sumUpArray(EfaUtil.kommaList2IntArr(ew.drv_nadel_erw_gold,','))  * drvConfig.eur_nadel_erw_gold;
    meldegeld += EfaUtil.string2int(ew.drv_nadel_jug_silber,0) * drvConfig.eur_nadel_jug_silber;
    meldegeld += EfaUtil.sumUpArray(EfaUtil.kommaList2IntArr(ew.drv_nadel_jug_gold,','))  * drvConfig.eur_nadel_jug_gold;

    stoffabzeichen += EfaUtil.string2int(ew.drv_stoff_erw,0) * drvConfig.eur_stoff_erw;
    stoffabzeichen += EfaUtil.string2int(ew.drv_stoff_jug,0) * drvConfig.eur_stoff_jug;

    if (stoffabzeichen == 0) this.printStoffBestellButton.setVisible(false);

    this.aequatorButton.setVisible(aequator > 0);
    this.aequatorButton.setText(aequator+" �quatorpreistr�ger");
    this.papierFahrtenhefteErforderlichLabel.setVisible(papierFahrtenhefteErforderlich > 0);

    this.vZusammenfassungMeldegebuehr.setText(EfaUtil.cent2euro(meldegeld,true));
    this.vZusammenfassungStoffabzeichen.setText(EfaUtil.cent2euro(stoffabzeichen,true));
    this.vZusammenfassungEurGesamt.setText(EfaUtil.cent2euro(meldegeld+stoffabzeichen,true));

    if (ew.drvint_anzahlPapierFahrtenhefte<0) {
      this.vAnzahlPapierFahrtenhefte.setText(Integer.toString(papierFahrtenhefteErforderlich));
      ew.drvint_anzahlPapierFahrtenhefte = papierFahrtenhefteErforderlich;
    }
  }

  boolean isErfuellt(EfaWettMeldung ewm, boolean testeUndKorrigiere) {
    switch(MELDTYP) {
      case MeldungenIndexFrame.MELD_FAHRTENABZEICHEN:
        String gruppe = getGruppe(ewm);
        if (gruppe != null && ewm.gruppe != null) {
          if (!ewm.gruppe.startsWith(gruppe) && testeUndKorrigiere) {
            warnung("Teilnehmer hat f�r Gruppe '"+ewm.gruppe+"' gemeldet, hat aber f�r Gruppe '"+gruppe+"' erf�llt. Gruppe wurde korrigiert!");
            ewm.gruppe = gruppe;
            ewm.changed = true;
          }
        }
        return gruppe != null;
      case MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK:
        int tage = EfaUtil.string2int(ewm.drvWS_Tage,0);
        int km = EfaUtil.zehntelString2Int(ewm.drvWS_Km);
        return (tage > 0 && ((tage == 1 && km >= 300) || (tage > 1 && km >= 400)));
    }
    return false;
  }

  String getGruppe(EfaWettMeldung ewm) {
    int jahrgang = EfaUtil.string2int(ewm.jahrgang,0);
    if (jahrgang <= 0) return null;

    int geschlecht = -1;
    if (ewm.geschlecht != null && ewm.geschlecht.equals(EfaWettMeldung.GESCHLECHT_M)) geschlecht = 0;
    if (ewm.geschlecht != null && ewm.geschlecht.equals(EfaWettMeldung.GESCHLECHT_W)) geschlecht = 1;
    if (geschlecht < 0) return null;

    boolean behind = ewm.gruppe != null && ewm.gruppe.endsWith("(50% Behinderung)");

    int km = EfaUtil.zehntelString2Int(ewm.kilometer);

    int wafaKm = 0;
    int wafaAnzTage = 0;
    int jumAnz = 0;
    if (ewm.fahrt != null) {
      for (int i=0; i<ewm.fahrt.length && i<MAX_FAHRTEN; i++) {
        boolean jum = ewm.fahrt[i][5] != null && ewm.fahrt[i][5].equals(EfaWettMeldung.JUM);

        // Bugfix f�r EFA.150
        if (ew.allg_programm != null && ew.allg_programm.equals("EFA.150") &&
            ewm.fahrt[i][3] != null && ewm.fahrt[i][3].endsWith(" (JuM-Regatta)")) jum = true;

        if (ewm.fahrt[i][4] != null && !jum) wafaKm += EfaUtil.zehntelString2Int(ewm.fahrt[i][4]);

        if (ewm.fahrt[i][1] != null && ewm.fahrt[i][2] != null && !jum) {
          TMJ von = EfaUtil.string2date(ewm.fahrt[i][1],0,0,0);
          TMJ bis = EfaUtil.string2date(ewm.fahrt[i][2],0,0,0);
          if (von.tag != 0 && von.monat != 0 && von.jahr != 0 && bis.tag != 0 && bis.monat != 0 && bis.jahr != 0)
            wafaAnzTage += EfaUtil.getDateDiff(von,bis);
        }
        if (jum) jumAnz++;
      }
    }
    if (ewm.drvint_fahrtErfuellt) { // nicht Zusatzbedingungen pr�fen, sondern als erf�llt betrachten
      wafaKm = 99999;
      wafaAnzTage = 99999;
      jumAnz = 99999;
    }
    return Daten.wettDefs.erfuellt(WettDefs.DRV_FAHRTENABZEICHEN,drvConfig.aktJahr,jahrgang,geschlecht,behind,km,wafaKm/10,wafaAnzTage,jumAnz,0);
  }

  void bestaetigenButton_actionPerformed(ActionEvent e) {
    if (data == null) return;
    if (!checkAndSaveChangedMeldung()) return;
    if (MELDTYP == MeldungenIndexFrame.MELD_FAHRTENABZEICHEN && drvConfig.teilnehmer == null) {
      Dialog.error("Keine Teilnehmerdatei geladen!");
      return;
    }

    if (MELDTYP == MeldungenIndexFrame.MELD_FAHRTENABZEICHEN && !drvConfig.readOnlyMode) {
      if (drvConfig.keyPassword == null) KeysAdminFrame.enterKeyPassword(drvConfig);
      if (drvConfig.keyPassword == null) return;
      if (!loadKeys()) return;
    }

    // schaue, ob es ungepr�fte Meldungen gibt
    int c = 0;
    int teilnErw = 0;
    int teilnJug = 0;
    for (int i=0; i<data.size(); i++) {
      EfaWettMeldung ewm = (EfaWettMeldung)data.get(i);
      if (!ewm.drvint_geprueft && !drvConfig.readOnlyMode) {
        Dialog.error("Meldung "+(i+1)+" wurde noch nicht gepr�ft! Es m�ssen zuerst alle Meldungen gepr�ft werden!");
        setMFields(i,false);
        this.jTabbedPane1.setSelectedIndex(1);
        return;
      } else {
        if (ewm.drvint_wirdGewertet) c++;
      }
    }

    // schaue, ob es Meldungen gibt, die nicht gewertet werden sollen
    if (c < data.size()) {
      if (Dialog.yesNoDialog("Warnung","Es sind nur "+c+" von "+data.size()+" Teilnehmern als 'wird gewertet' markiert.\nIst das korrekt?") != Dialog.YES) return;
    }

    // pr�fen, ob Signaturdatei bereits existiert
    if (MELDTYP == MeldungenIndexFrame.MELD_FAHRTENABZEICHEN) {
      if ((new File(ew.datei+"sig")).exists()) {
        if (Dialog.yesNoDialog("Warnung","Eine Best�tigungsdatei f�r diese Meldung existiert bereits.\nSoll trotzdem eine neue erstellt werden?") != Dialog.YES) return;
      }
    }

    // Alle Meldungen dieses Vereins vorsichtshalber aus der Meldestatistik entfernen
    for (DatenFelder d = drvConfig.meldestatistik.getCompleteFirst(); d != null; d = drvConfig.meldestatistik.getCompleteNext()) {
      if (d.get(Meldestatistik.VEREINSMITGLNR).equals(ew.verein_mitglnr)) drvConfig.meldestatistik.delete(d.get(Meldestatistik.KEY));
    }

    String errors = "";
    String warnings = "";

    ESigFahrtenhefte f = null;
    Vector nichtGewerteteTeilnehmer = null;
    if (MELDTYP == MeldungenIndexFrame.MELD_FAHRTENABZEICHEN) {
      if (!drvConfig.readOnlyMode) {
        f = new ESigFahrtenhefte(ew.datei+"sig");
        f.verein_user = ew.verein_user;
        f.verein_name = ew.verein_name;
        f.verein_mitglnr = ew.verein_mitglnr;
        f.quittungsnr = this.qnr;

        int itmp = EfaUtil.string2date(drvConfig.schluessel,0,0,0).tag;
        String pubkey_alias = "drv"+(itmp < 10 ? "0" : "")+itmp;
        String certFile = Daten.efaDataDirectory+pubkey_alias+".cert";
        if (EfaUtil.canOpenFile(certFile)) {
          try {
            int filesize = (int)(new File(certFile)).length();
            byte[] buf = new byte[filesize];
            FileInputStream ff = new FileInputStream(certFile);
            ff.read(buf,0,filesize);
            ff.close();
            String data = Base64.encodeBytes(buf);
            f.keyName = pubkey_alias;
            f.keyDataBase64 = EfaUtil.replace(data,"\n","",true);;
          } catch(Exception ee) {
            EfaUtil.foo();
          }
        }
      }

      c = 0;
      teilnErw = 0;
      teilnJug = 0;
      nichtGewerteteTeilnehmer = new Vector();
      for (int i=0; i<data.size(); i++) {
        EfaWettMeldung m = (EfaWettMeldung)data.get(i);

        // pr�fen, ob diese Meldung gewertet werden soll
        if (!m.drvint_wirdGewertet) {
          nichtGewerteteTeilnehmer.add(m.vorname+" "+m.nachname+
                     (m.drvint_nichtGewertetGrund != null && m.drvint_nichtGewertetGrund.length()>0 ? " (Grund: "+m.drvint_nichtGewertetGrund+")" : ""));
          continue;
        }

        // aktuelle Anzahl der Abzeichen
        int anzAbz = EfaUtil.string2int(m.drv_anzAbzeichen,0);
        int anzAbzAB = EfaUtil.string2int(m.drv_anzAbzeichenAB,0);
        int gesKm = EfaUtil.string2int(m.drv_gesKm,0);
        int gesKmAB = EfaUtil.string2int(m.drv_gesKmAB,0);

        // Gruppe, f�r die der Teilnehmer erf�llt hat
        String gruppe = getGruppe(m);
        if (gruppe == null) {
          errors += "Teilnehmer "+(i+1)+" ("+m.vorname+" "+m.nachname+") wurde nicht gewertet, da er/sie die Bedingungen nicht erf�llt hat.\n";
          nichtGewerteteTeilnehmer.add(m.vorname+" "+m.nachname+" (Grund: Wettbewerbsbedingungen nicht erf�llt)");
          continue;
        }

        // pr�fen, ob Gruppe der Meldung entspricht
        if (!gruppe.equals(m.gruppe)) {
          warnings += "Teilnehmer "+(i+1)+" ("+m.vorname+" "+m.nachname+") wurde f�r Gruppe '"+m.gruppe+"' gemeldet, hat aber in Gruppe '"+gruppe+"' erf�llt. Gruppe wurde korrigiert.\n";
        }

        // Abzeichen und Kilometer hochz�hlen
        anzAbz++;
        gesKm += EfaUtil.string2int(m.kilometer,0);
        boolean isAB = (gruppe.startsWith("3a") || gruppe.startsWith("3b"));
        if (isAB) {
          anzAbzAB++;
          gesKmAB += EfaUtil.string2int(m.kilometer,0);
        }

        // Anzahl Abzeichen und Kilometer pr�fen
        if (anzAbz < anzAbzAB || gesKm < gesKmAB) {
          errors += "Teilnehmer "+(i+1)+" ("+m.vorname+" "+m.nachname+") wurde nicht gewertet, da er/sie ung�ltige Werte der Abzeichen/Kilometer (mehr AB als normal) hat.\n";
          nichtGewerteteTeilnehmer.add(m.vorname+" "+m.nachname+" (Grund: Unstimmige Werte der Abzeichen/Kilometer f�r Jugend-A/B)");
          continue;
        }

        if (!drvConfig.readOnlyMode) {
          // ggf. neue Teilnehmernummer generieren
          if (m.drv_teilnNr == null || m.drv_teilnNr.length() == 0) {
            long l = EfaUtil.getSHAlong((m.vorname+"#"+m.nachname+"#"+m.jahrgang).getBytes(),3);
            if (l < 0) {
              errors += "F�r Teilnehmer "+(i+1)+" ("+m.vorname+" "+m.nachname+") konnte keine Teilnehmernummer berechnet werden (-1).\n";
              nichtGewerteteTeilnehmer.add(m.vorname+" "+m.nachname+" (Grund: Es konnte keine Teilnehmernummer berechnet werden)");
              continue;
            }
            while (drvConfig.teilnehmer.getExact(Long.toString(l)) != null) l++;
            m.drv_teilnNr = Long.toString(l);
          }
        }

        int jahr = drvConfig.aktJahr;
        byte keynr = (byte)EfaUtil.string2date(drvConfig.schluessel,0,0,0).tag;

        boolean opSuccess = false;
        if (!drvConfig.readOnlyMode) {
          try {
            PrivateKey privKey = Daten.keyStore.getPrivateKey(drvConfig.schluessel);
            if (privKey == null) {
              errors += "Privater Schl�ssel "+drvConfig.schluessel+" nicht gefunden: "+Daten.keyStore.getLastError()+"\n";
            }
            DRVSignatur sig = new DRVSignatur(m.drv_teilnNr,m.vorname,m.nachname,m.jahrgang,
                                              anzAbz,gesKm,anzAbzAB,gesKmAB,jahr,EfaUtil.string2int(m.kilometer,0),null,
                                              drvConfig.VERSION,keynr,
                                              privKey);
            sig.checkSignature();
            if (sig.getSignatureState() != DRVSignatur.SIG_VALID) {
              errors += "Teilnehmer "+(i+1)+" ("+m.vorname+" "+m.nachname+") wurde nicht gewertet, da die f�r ihr erstellte Signatur ung�ltig ist: "+sig.getSignatureError()+"\n";
              nichtGewerteteTeilnehmer.add(m.vorname+" "+m.nachname+" (Grund: Erstellte Signatur ist ung�ltig)");
              continue;
            } else {
              // elektronisches Fahrtenheft
              f.addFahrtenheft(sig);
              drvConfig.teilnehmer.delete(m.drv_teilnNr);
              DatenFelder d = new DatenFelder(Teilnehmer._ANZFELDER);
              d.set(Teilnehmer.TEILNNR,m.drv_teilnNr);
              d.set(Teilnehmer.VORNAME,m.vorname);
              d.set(Teilnehmer.NACHNAME,m.nachname);
              d.set(Teilnehmer.JAHRGANG,m.jahrgang);
              d.set(Teilnehmer.FAHRTENHEFT,sig.toString());
              drvConfig.teilnehmer.add(d);
              log(false,"Elektronisches Fahrtenheft erstellt: "+sig.toString());
              opSuccess = true;
            }
          } catch(Exception ee) {
            errors += "Fehler beim Erstellen des elektronischen Fahrtenhefts f�r Teilnehmer "+(i+1)+" ("+m.vorname+" "+m.nachname+"): "+ee.getMessage()+"\n";
          }
        } else opSuccess = true;

        if (opSuccess) {
          c++;
          if (m.gruppe.startsWith("1") || m.gruppe.startsWith("2")) teilnErw++;
          if (m.gruppe.startsWith("3")) teilnJug++;

          // Meldestatistik
          DatenFelder d = new DatenFelder(Meldestatistik._ANZFELDER);
          d.set(Meldestatistik.KEY,ew.verein_mitglnr+"#"+m.vorname+"#"+m.nachname+"#"+m.jahrgang);
          d.set(Meldestatistik.VEREINSMITGLNR,ew.verein_mitglnr);
          d.set(Meldestatistik.VEREIN,ew.verein_name);
          d.set(Meldestatistik.VORNAME,m.vorname);
          d.set(Meldestatistik.NACHNAME,m.nachname);
          d.set(Meldestatistik.JAHRGANG,m.jahrgang);
          d.set(Meldestatistik.GESCHLECHT,m.geschlecht);
          d.set(Meldestatistik.KILOMETER,m.kilometer);
          d.set(Meldestatistik.GRUPPE,m.gruppe);
          d.set(Meldestatistik.ANZABZEICHEN,Integer.toString(anzAbz));
          d.set(Meldestatistik.ANZABZEICHENAB,Integer.toString(anzAbzAB));
          d.set(Meldestatistik.GESKM,Integer.toString(gesKm));
          if (m.drv_aequatorpreis != null && (m.drv_aequatorpreis.equals("1") || m.drv_aequatorpreis.equals("3"))) d.set(Meldestatistik.AEQUATOR,m.drv_aequatorpreis);
          drvConfig.meldestatistik.add(d);
        }
      }
    }

    if (MELDTYP == MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK) {
      Vector gewaesser = new Vector();
      int teilnehmer = 0;
      int maennerkm = 0;
      int juniorenkm = 0;
      int frauenkm = 0;
      int juniorinnenkm = 0;

      for (int i=0; i<data.size(); i++) {
        EfaWettMeldung m = (EfaWettMeldung)data.get(i);

        // pr�fen, ob diese Meldung gewertet werden soll
        if (!m.drvint_wirdGewertet) {
          continue;
        }

        Vector g = null;
        if (m.drvWS_Gewaesser != null && m.drvWS_Gewaesser.length()>0) g = EfaUtil.split(EfaUtil.replace(m.drvWS_Gewaesser,";",",",true),',');
        for (int j=0; g != null && j<g.size(); j++) {
          if (!gewaesser.contains(g.get(j))) gewaesser.add(g.get(j));
        }
        teilnehmer += EfaUtil.string2int(m.drvWS_Teilnehmer,0);
        maennerkm += EfaUtil.zehntelString2Int(m.drvWS_MaennerKm);
        juniorenkm += EfaUtil.zehntelString2Int(m.drvWS_JuniorenKm);
        frauenkm += EfaUtil.zehntelString2Int(m.drvWS_FrauenKm);
        juniorinnenkm += EfaUtil.zehntelString2Int(m.drvWS_JuniorinnenKm);
      }

      Object[] gewaesser_arr = gewaesser.toArray();
      Arrays.sort(gewaesser_arr);
      String gewaesser_string = "";
      for (int i=0; i<gewaesser_arr.length; i++) {
        gewaesser_string += gewaesser_arr[i] + (i+1<gewaesser_arr.length ? ", " : "");
      }

      maennerkm = maennerkm / 10;
      juniorenkm = juniorenkm / 10;
      frauenkm = frauenkm / 10;
      juniorinnenkm = juniorinnenkm / 10;
      int mannschkm = maennerkm + juniorenkm + frauenkm + juniorinnenkm;

      // Meldestatistik
      DatenFelder d = new DatenFelder(Meldestatistik._ANZFELDER);
      d.set(Meldestatistik.KEY,ew.verein_mitglnr);
      d.set(Meldestatistik.VEREINSMITGLNR,ew.verein_mitglnr);
      d.set(Meldestatistik.VEREIN,ew.verein_name);
      d.set(Meldestatistik.WS_BUNDESLAND,ew.verein_lrv);
      d.set(Meldestatistik.WS_MITGLIEDIN,ew.verein_mitgl_in);
      d.set(Meldestatistik.WS_GEWAESSER,gewaesser_string);
      d.set(Meldestatistik.WS_TEILNEHMER,Integer.toString(teilnehmer));
      d.set(Meldestatistik.WS_MANNSCHKM,Integer.toString(mannschkm));
      d.set(Meldestatistik.WS_MAENNERKM,Integer.toString(maennerkm));
      d.set(Meldestatistik.WS_JUNIORENKM,Integer.toString(juniorenkm));
      d.set(Meldestatistik.WS_FRAUENKM,Integer.toString(frauenkm));
      d.set(Meldestatistik.WS_JUNIORINNENKM,Integer.toString(juniorinnenkm));
      drvConfig.meldestatistik.add(d);
    }

    try {
      if (c <= 0) {
        errors += "Es wurden keine " + (MELDTYP == MeldungenIndexFrame.MELD_FAHRTENABZEICHEN ? "Teilnehmer" : "Fahrten") + " gewertet!\n";
      } else {
        if (MELDTYP == MeldungenIndexFrame.MELD_FAHRTENABZEICHEN && !drvConfig.readOnlyMode) f.writeFile();
      }
    } catch(Exception ee) {
      errors += "Fehler beim Erstellen der Best�tigungsdatei: "+e.toString()+"\n";
      c = 0;
    }
    if (MELDTYP == MeldungenIndexFrame.MELD_FAHRTENABZEICHEN && !drvConfig.teilnehmer.writeFile()) {
      errors += "DRV-Teilnehmerdatei konnte nicht geschrieben werden!\n";
    }

    if (errors.length() > 0) {
      Dialog.error("Beim Erstellen der Best�tigungsdatei traten Fehler auf:\n"+errors);
    }
    if (warnings.length() > 0) {
      Dialog.infoDialog("Warnung","Beim Erstellen der Best�tigungsdatei kam es zu Warnungen:\n"+warnings);
    }

    if (c > 0) {
      if (MELDTYP == MeldungenIndexFrame.MELD_FAHRTENABZEICHEN && !drvConfig.readOnlyMode) {
        Dialog.infoDialog("Es wurden "+c+" von "+data.size()+" Teilnehmern gewertet und ein elektronisches Fahrtenbuch\n"+
                          "f�r sie generiert.\n"+
                          "Best�tigungsdatei: "+f.getDateiname()+
                          "\n\nEs wird nun ein PDF-Dokument mit den elektronischen Fahrtenheften erzeugt.");
        try {
          PDFOutput.printPDFbestaetigung(drvConfig,ew,qnr,meldegeld,data.size(),c,teilnErw,teilnJug,f,nichtGewerteteTeilnehmer);
        } catch (NoClassDefFoundError ee) {
          Dialog.error("Das PDF-Plugin ist nicht installiert.\n"+
                       "Die Meldung kann nicht best�tigt werden.");
          return;
        }
      }
      if (!saveMeldedatei()) {
        Logger.log(Logger.ERROR,"Speichern der Meldedatei ist fehlgeschlagen!");
        Dialog.error("Speichern der Meldedatei ist fehlgeschlagen!");
        return;
      }
      if (!setMeldedateiBearbeitet( (f != null ? f.getDateiname() : "ok") )) {
        Dialog.error("Fehler beim Aktualisieren des Status f�r die vorliegende Meldedatei");
        return;
      }
      if (!drvConfig.meldestatistik.writeFile()) {
        Logger.log(Logger.ERROR,"Speichern der Meldestatistik ist fehlgeschlagen!");
        Dialog.error("Speichern der Meldesatistik ist fehlgeschlagen!");
      }
      cancel();
    }
  }

  boolean saveMeldedatei() {
    if (data == null) return false;
    for (int i=0; i<data.size(); i++) {
      EfaWettMeldung ewm = (EfaWettMeldung)data.get(i);
      if (i == 0) this.ew.meldung = ewm;
      if (i+1 < data.size()) ewm.next = (EfaWettMeldung)data.get(i+1);
      else ewm.next = null;
    }
    try {
      if (this.ew.writeFile()) {
        log(false,"Alle �nderungen erfolgreich in der Meldedatei gespeichert.");
        changed = false;
        _hasBeenSaved = true;
        return true;
      }
    } catch(Exception e) {
      Dialog.error("Fehler beim Speichern der Meldedatei: "+e.getMessage());
    }
    return false;
  }

  boolean setMeldedateiBearbeitet(String bestaetigungsdatei) {
    DatenFelder d = drvConfig.meldungenIndex.getExactComplete(this.qnr);
    if (d == null) return false;
    d.set(MeldungenIndex.STATUS,Integer.toString(MeldungenIndex.ST_BEARBEITET));
    d.set(MeldungenIndex.BESTAETIGUNGSDATEI,bestaetigungsdatei);
    if (drvConfig.meldungenIndex.writeFile()) {
      log(false,"Status f�r Meldung auf 'Bearbeitet' gesetzt.");
      return true;
    }
    return false;
  }

  void closeButton_actionPerformed(ActionEvent e) {
    cancel();
  }

  void vUnblockButton_actionPerformed(ActionEvent e) {
    vBlock(!vBlocked);
  }

  void vBlock(boolean blocked) {
    this.vBestNadelErwGold.setEditable(!blocked);
    this.vBestNadelErwSilber.setEditable(!blocked);
    this.vBestNadelJugGold.setEditable(!blocked);
    this.vBestNadelJugSilber.setEditable(!blocked);
    this.vBestStoffErw.setEditable(!blocked);
    this.vBestStoffJug.setEditable(!blocked);
    this.vMeldenderBank.setEditable(!blocked);
    this.vMeldenderBlz.setEditable(!blocked);
    this.vMeldenderEmail.setEditable(!blocked);
    this.vMeldenderKonto.setEditable(!blocked);
    this.vMeldenderName.setEditable(!blocked);
    this.vMitgliedsnr.setEditable(!blocked);
    this.vNutzername.setEditable(!blocked);
    this.vVereinsname.setEditable(!blocked);
    this.vVersandName.setEditable(!blocked);
    this.vVersandOrt.setEditable(!blocked);
    this.vVersantStrasse.setEditable(!blocked);
    this.vAnzahlPapierFahrtenhefte.setEditable(!blocked);
    if (blocked) this.vUnblockButton.setText("Felder zum Bearbeiten freigeben");
    else this.vUnblockButton.setText("�nderungen speichern und Felder sch�tzen");
    this.vBlocked = blocked;

    if (blocked) {
      // �nderungen speichern
      if (EfaUtil.string2int(this.vBestNadelErwGold.getText().trim(),0) != EfaUtil.sumUpArray(EfaUtil.kommaList2IntArr(ew.drv_nadel_erw_gold,','))) {
        ew.drv_nadel_erw_gold = this.vBestNadelErwGold.getText().trim();
      }
      ew.drv_nadel_erw_silber = this.vBestNadelErwSilber.getText().trim();
      if (EfaUtil.string2int(this.vBestNadelJugGold.getText().trim(),0) != EfaUtil.sumUpArray(EfaUtil.kommaList2IntArr(ew.drv_nadel_jug_gold,','))) {
        ew.drv_nadel_jug_gold = this.vBestNadelJugGold.getText().trim();
      }
      ew.drv_nadel_jug_silber = this.vBestNadelJugSilber.getText().trim();
      ew.drv_stoff_erw = this.vBestStoffErw.getText().trim();
      ew.drv_stoff_jug = this.vBestStoffJug.getText().trim();
      ew.meld_bank = this.vMeldenderBank.getText().trim();
      ew.meld_blz = this.vMeldenderBlz.getText().trim();
      ew.meld_email = this.vMeldenderEmail.getText().trim();
      ew.meld_kto = this.vMeldenderKonto.getText().trim();
      ew.meld_name = this.vMeldenderName.getText().trim();
      ew.verein_mitglnr = this.vMitgliedsnr.getText().trim();
      ew.verein_user = this.vNutzername.getText().trim();
      ew.verein_name = this.vVereinsname.getText().trim();
      ew.versand_name = this.vVersandName.getText().trim();
      ew.versand_ort= this.vVersandOrt.getText().trim();
      ew.versand_strasse = this.vVersantStrasse.getText().trim();
      ew.drvint_anzahlPapierFahrtenhefte = EfaUtil.string2int(this.vAnzahlPapierFahrtenhefte.getText().trim(),-1);
      calcOverallValues();
      changed = true;
    }
  }

  void mBlock(boolean blocked) {
    // Fahrtenabzeichen
    this.mTeilnNr.setEditable(!blocked);
    this.mTeilnSuchenButton.setEnabled(!blocked);
    this.mVorname.setEditable(!blocked);
    this.mNachname.setEditable(!blocked);
    this.mJahrgang.setEditable(!blocked);
    this.mGeschlecht.setEnabled(!blocked);
    this.mGruppe.setEnabled(!blocked);
    this.mKilometer.setEditable(!blocked);
    this.mAnzAbzeichen.setEditable(!blocked);
    this.mGesKm.setEditable(!blocked);
    this.mAnzAbzeichenAB.setEditable(!blocked);
    this.mGesKmAB.setEditable(!blocked);
    for (int i=0; this.mFahrten != null && i<this.mFahrten.length; i++)
      for (int j=0; this.mFahrten[i] != null && j<this.mFahrten[i].length; j++)
        this.mFahrten[i][j].setEditable(!blocked);
    this.mFahrtnachweisErbracht.setEnabled(!blocked);

    // Wanderruderstatistik
    this.fStartZiel.setEditable(!blocked);
    this.fGewaesser.setEditable(!blocked);
    this.fTage.setEditable(!blocked);
    this.fKilometer.setEditable(!blocked);
    this.fMaennerAnz.setEditable(!blocked);
    this.fMaennerKm.setEditable(!blocked);
    this.fJuniorenAnz.setEditable(!blocked);
    this.fJuniorenKm.setEditable(!blocked);
    this.fFrauenAnz.setEditable(!blocked);
    this.fFrauenKm.setEditable(!blocked);
    this.fJuniorinnenAnz.setEditable(!blocked);
    this.fJuniorinnenKm.setEditable(!blocked);
    this.fTeilnehmer.setEditable(!blocked);
    this.fMannschKm.setEditable(!blocked);

    if (blocked) {
      this.mUnblockButton.setText("Felder zum Bearbeiten freigeben");
      this.fUnblockButton.setText("Felder zum Bearbeiten freigeben");
    } else {
      this.mUnblockButton.setText("�nderungen speichern und Felder sch�tzen");
      this.fUnblockButton.setText("�nderungen speichern und Felder sch�tzen");
    }
    this.mBlocked = blocked;
  }

  void firstButton_actionPerformed(ActionEvent e) {
    setMFields(0,false);
  }

  void prevButton_actionPerformed(ActionEvent e) {
    setMFields(ewmNr-1,false);
  }

  void nextButton_actionPerformed(ActionEvent e) {
    setMFields(ewmNr+1,false);
  }

  void lastButton_actionPerformed(ActionEvent e) {
    setMFields(data.size()-1,false);
  }

  void newButton_actionPerformed(ActionEvent e) {
    EfaWettMeldung m = new EfaWettMeldung();
    data.add(m);
    setMFields(data.size()-1,false);
    mBlock(false);
  }

  void deleteButton_actionPerformed(ActionEvent e) {
    Dialog.infoDialog("Noch nicht implementiert","Diese Funktion ist noch nicht implementiert."); // @todo
  }

  void mUnblockButton_actionPerformed(ActionEvent e) {
    if (ewmCur == null) {
      Dialog.error("Es ist kein Eintrag ausgew�hlt. Bitte gehe zu einem vorhandenen Eintrag oder klicke 'Neu'.");
      return;
    }
    if (!mBlocked) {
      getMFields(ewmCur);
      replaceMeldung();
      mBlock(true);
      setMFields(ewmNr,true);
    } else {
      mBlock(!mBlocked);
    }
  }

  void fUnblockButton_actionPerformed(ActionEvent e) {
    if (ewmCur == null) {
      Dialog.error("Es ist kein Eintrag ausgew�hlt. Bitte gehe zu einem vorhandenen Eintrag oder klicke 'Neu'.");
      return;
    }
    if (!mBlocked) {
      getMFields(ewmCur);
      replaceMeldung();
      mBlock(true);
      setMFields(ewmNr,true);
    } else {
      mBlock(!mBlocked);
    }
  }

  boolean loadKeys() {
    if (Daten.keyStore != null && Daten.keyStore.isKeyStoreReady()) return true;
    Daten.keyStore = new EfaKeyStore(Daten.efaDataDirectory+drvConfig.KEYSTORE_FILE,drvConfig.keyPassword);
    if (!Daten.keyStore.isKeyStoreReady()) {
      Dialog.error("KeyStore kann nicht geladen werden:\n"+Daten.keyStore.getLastError());
    }
    return Daten.keyStore.isKeyStoreReady();
  }

  void mWirdGewertet_actionPerformed(ActionEvent e) {
    if (ewmCur != null) ewmCur.drvint_wirdGewertet = mWirdGewertet.isSelected();
    if (data != null && ewmNr >= 0 && ewmNr < data.size()) {
      EfaWettMeldung m = (EfaWettMeldung)data.get(ewmNr);
      if (m != null) m.drvint_wirdGewertet = mWirdGewertet.isSelected();
    }
    updateStatusNichtGewertetGrund();
  }

  void fWirdGewertet_actionPerformed(ActionEvent e) {
    if (ewmCur != null) ewmCur.drvint_wirdGewertet = fWirdGewertet.isSelected();
    if (data != null && ewmNr >= 0 && ewmNr < data.size()) {
      EfaWettMeldung m = (EfaWettMeldung)data.get(ewmNr);
      if (m != null) m.drvint_wirdGewertet = fWirdGewertet.isSelected();
    }
    updateStatusNichtGewertetGrund();
  }

  void mNichtGewertetGrund_focusLost(FocusEvent e) {
    String s = mNichtGewertetGrund.getText().trim();
    if (s.length() == 0) s = null;
    if (ewmCur != null) ewmCur.drvint_nichtGewertetGrund = s;
    if (data != null && ewmNr >= 0 && ewmNr < data.size()) {
      EfaWettMeldung m = (EfaWettMeldung)data.get(ewmNr);
      if (m != null) m.drvint_nichtGewertetGrund = s;
    }
  }

  void fNichtGewertetGrund_focusLost(FocusEvent e) {
    String s = fNichtGewertetGrund.getText().trim();
    if (s.length() == 0) s = null;
    if (ewmCur != null) ewmCur.drvint_nichtGewertetGrund = s;
    if (data != null && ewmNr >= 0 && ewmNr < data.size()) {
      EfaWettMeldung m = (EfaWettMeldung)data.get(ewmNr);
      if (m != null) m.drvint_nichtGewertetGrund = s;
    }
  }

  void updateStatusNichtGewertetGrund() {
    switch(MELDTYP) {
      case MeldungenIndexFrame.MELD_FAHRTENABZEICHEN:
        mNichtGewertetGrund.setEnabled(!mWirdGewertet.isSelected());
        mNichtGewertetGrund.setEditable(!mWirdGewertet.isSelected());
        this.mNichtGewertetGrundLabel.setEnabled(!mWirdGewertet.isSelected());
        break;
      case MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK:
        fNichtGewertetGrund.setEnabled(!fWirdGewertet.isSelected());
        fNichtGewertetGrund.setEditable(!fWirdGewertet.isSelected());
        this.fNichtGewertetGrundLabel.setEnabled(!fWirdGewertet.isSelected());
        break;
    }
  }

  void mTeilnSuchenButton_actionPerformed(ActionEvent e) {
    if (drvConfig.teilnehmer == null) {
      Dialog.error("Es wurde keine Teilnehmer-Datei geladen!");
      return;
    }

    String vorname = mVorname.getText().trim();
    String nachname = mNachname.getText().trim();
    String jahrgang = mJahrgang.getText().trim();
    if (vorname.length() == 0 || nachname.length() == 0 || jahrgang.length() == 0) {
      Dialog.error("Bitte f�lle die Felder 'Vorname', 'Nachname' und 'Jahrgang' aus!");
      return;
    }

    Vector teilnnr = new Vector();
    for (DatenFelder d = drvConfig.teilnehmer.getCompleteFirst(); d != null; d = drvConfig.teilnehmer.getCompleteNext()) {
      if (vorname.equals(d.get(Teilnehmer.VORNAME)) &&
          nachname.equals(d.get(Teilnehmer.NACHNAME)) &&
          jahrgang.equals(d.get(Teilnehmer.JAHRGANG))) teilnnr.add(d.get(Teilnehmer.TEILNNR));
    }

    if (teilnnr.size() == 0) {
      Dialog.error("Es konnte kein entsprechender Teilnehmer gefunden werden!");
      return;
    }
    if (teilnnr.size() == 1) {
      mTeilnNr.setText((String)teilnnr.get(0));
      return;
    }
    String s = "";
    for (int i=0; i<teilnnr.size(); i++) s += (i>0 ? ", " : "") + (String)teilnnr.get(i);
    Dialog.error("Es wurden mehrere Teilnehmer '"+vorname+" "+nachname+" ("+jahrgang+")' gefunden.\nTeilnehmernummern: "+s);
  }


  void printStoffBestellButton_actionPerformed(ActionEvent e) {
    String tmpdatei = Daten.efaTmpDirectory+"stoffabzeichen.html";
    try {
      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpdatei),Daten.ENCODING));
      f.write("<html>\n");
      f.write("<head><META http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"></head>\n");
      f.write("<body>\n");
      f.write("<h1 align=\"center\">Bestellung von Stoffabzeichen</h1>\n");
      f.write("<h2 align=\"center\">"+ew.verein_name+"</h2>\n");
      f.write("<table align=\"center\" border=\"3\" width=\"100%\">\n");
      f.write("<tr><td colspan=\"2\" align=\"center\"><big>Vereinsdaten</big></tt></td></tr>\n");
      f.write("<tr><td>Vereinsname:</td><td><tt><b>"+ew.verein_name+"</b></tt></td></tr>\n");
      f.write("<tr><td>DRV-Mitgliedsnummer:</td><td><tt><b>"+ew.verein_mitglnr+"</b></tt></td></tr>\n");
      f.write("<tr><td colspan=\"2\" align=\"center\"><big>Meldende Person</big></tt></td></tr>\n");
      f.write("<tr><td>Name:</td><td><tt><b>"+ew.meld_name+"</b></tt></td></tr>\n");
      f.write("<tr><td>Kontonr:</td><td><tt><b>"+ew.meld_kto+"</b></tt></td></tr>\n");
      f.write("<tr><td>Bank:</td><td><tt><b>"+ew.meld_bank+"</b></tt></td></tr>\n");
      f.write("<tr><td>BLZ:</td><td><tt><b>"+ew.meld_blz+"</b></tt></td></tr>\n");
      f.write("<tr><td colspan=\"2\" align=\"center\"><big>Versandanschrift</big></tt></td></tr>\n");
      f.write("<tr><td>Name:</td><td><tt><b>"+ew.versand_name+"</b></tt></td></tr>\n");
      f.write("<tr><td>Stra�e:</td><td><tt><b>"+ew.versand_strasse+"</b></tt></td></tr>\n");
      f.write("<tr><td>Ort:</td><td><tt><b>"+ew.versand_ort+"</b></tt></td></tr>\n");
      f.write("<tr><td colspan=\"2\" align=\"center\"><big>Bestellung</big></tt></td></tr>\n");
      f.write("<tr><td>Stoffabzeichen Jugend:</td><td><tt><b>"+ew.drv_stoff_jug+"</b></tt></td></tr>\n");
      f.write("<tr><td>Stoffabzeichen Erwachsene gold:</td><td><tt><b>"+ew.drv_stoff_erw+"</b></tt></td></tr>\n");
      int cent = EfaUtil.string2int(ew.drv_stoff_erw,0)*drvConfig.eur_stoff_erw +
                 EfaUtil.string2int(ew.drv_stoff_jug,0)*drvConfig.eur_stoff_jug;
      f.write("<tr><td>Bestellwert:</td><td><tt><b>"+EfaUtil.cent2euro(cent,true)+"</b></tt></td></tr>\n");
      f.write("</table>\n");
      f.write("</body></html>\n");
      f.close();
      JEditorPane out = new JEditorPane();
      out.setContentType("text/html; charset="+Daten.ENCODING);
      out.setPage("file:"+tmpdatei);
      out.setSize(600,800);
      out.doLayout();
      SimpleFilePrinter sfp = new SimpleFilePrinter(out);
      if (sfp.setupPageFormat()) {
        if (sfp.setupJobOptions()) {
          sfp.printFile();
        }
      }
      EfaUtil.deleteFile(tmpdatei);
    } catch(Exception ee) {
      Dialog.error("Druckdatei konnte nicht erstellt werden: "+ee.toString());
      return;
    }

  }

  void jTabbedPane1_stateChanged(ChangeEvent e) {
   if (jTabbedPane1 != null && jTabbedPane1.getSelectedIndex() == 0) this.calcOverallValues();
  }

  void vMeldegeldEingegangen_actionPerformed(ActionEvent e) {
    if (vMeldegeldEingegangen.isSelected()) vMeldegeldEingegangen.setForeground(Color.blue);
    else vMeldegeldEingegangen.setForeground(Color.red);
    ew.drvint_meldegeldEingegangen = this.vMeldegeldEingegangen.isSelected();
    changed = true;
  }

  void makeSureIsANumber_focusLost(FocusEvent e) {
    if (e == null) return;
    try {
      JTextField f = (JTextField)e.getComponent();
      f.setText(Integer.toString(EfaUtil.string2date(f.getText().trim(),0,0,0).tag));
    } catch(Exception ee) {
      EfaUtil.foo();
    }
  }

  void makeSureIsANumberWithComma_focusLost(FocusEvent e) {
    if (e == null) return;
    try {
      JTextField f = (JTextField)e.getComponent();
      f.setText(EfaUtil.zehntelInt2String(EfaUtil.zehntelString2Int(f.getText().trim())));
    } catch(Exception ee) {
      EfaUtil.foo();
    }
  }

  void wsListButton_actionPerformed(ActionEvent e) {
    if (MELDTYP != MeldungenIndexFrame.MELD_WANDERRUDERSTATISTIK) return;
    WSFahrtenUebersichtFrame dlg = new WSFahrtenUebersichtFrame(this,data);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    if (dlg.getResult() >= 0) {
      setMFields(dlg.getResult(),false);
    }
  }

  public static boolean hasBeenSaved() {
    return _hasBeenSaved;
  }

  void aequatorButton_actionPerformed(ActionEvent e) {
    Vector aequator = new Vector();
    int warnings = 0;

    for (int i=0; i<data.size(); i++) {
      EfaWettMeldung ewm = (EfaWettMeldung)data.get(i);
      if (ewm.drv_aequatorpreis != null && ewm.drv_aequatorpreis.length() > 0 &&
          (ewm.drv_aequatorpreis.equals("1") || ewm.drv_aequatorpreis.equals("3"))) {
        if (isErfuellt(ewm,false) && ewm.drvint_wirdGewertet) {
          int km = EfaUtil.string2int(ewm.drv_gesKm,0) + EfaUtil.string2int(ewm.kilometer,0);
          int anzAeq  = km / WettDefs.DRV_AEQUATOR_KM;
          aequator.add("<p><b>"+ewm.vorname+" "+ewm.nachname+"</b><br>Jahrgang: "+ewm.jahrgang+"<br>Kilometer: "+km+"<br>"+anzAeq+". �quatorpreis</p>");
        } else {
          warnings++;
        }
      }
    }
    if (warnings>0) {
      Dialog.error("Es gibt "+warnings+" Teilnehmer, die den �quatorpreis bekommen w�rden, aber die Bedingungen f�r dieses\n"+
                   "Jahr nicht erf�llt haben oder nicht gewertet werden sollen. Diese Teilnehmer werden NICHT ausgegeben.\n\n"+
                   "Bitte pr�fen Sie erst alle Meldungen dieses Vereins auf G�ltigkeit und Drucken Sie anschlie�end erst\n"+
                   "die �quatorpreistr�ger aus.");
    }
    if (aequator.size() == 0) {
      Dialog.error("Es gibt keine �quatorpreistr�ger in diesem Verein.");
      return;
    }

    String tmpdatei = Daten.efaTmpDirectory+"aequator.html";
    try {
      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpdatei),Daten.ENCODING));
      f.write("<html>\n");
      f.write("<head><META http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"></head>\n");
      f.write("<body>\n");
      f.write("<h1 align=\"center\">�quatorpreistr�ger</h1>\n");
      f.write("<p><b>Verein: "+ew.verein_name+"</b><br>Mitgliedsnummer: "+ew.verein_mitglnr+"<br><br>Anschrift:<br>"+ew.versand_name+"<br>"+ew.versand_strasse+"<br>"+ew.versand_ort+"</p>\n");
      for (int i=0; i<aequator.size(); i++) {
        f.write(((String)aequator.get(i))+"\n");
      }
      f.write("</body></html>\n");
      f.close();
      JEditorPane out = new JEditorPane();
      out.setContentType("text/html; charset="+Daten.ENCODING);
      out.setPage("file:"+tmpdatei);
      SimpleFilePrinter.sizeJEditorPane(out);
      SimpleFilePrinter sfp = new SimpleFilePrinter(out);
      if (sfp.setupPageFormat()) {
        if (sfp.setupJobOptions()) {
          sfp.printFile();
        }
      }
      EfaUtil.deleteFile(tmpdatei);
    } catch(Exception ee) {
      Dialog.error("Druckdatei konnte nicht erstellt werden: "+ee.toString());
      return;
    }


  }

  void mFahrtnachweisErbracht_actionPerformed(ActionEvent e) {

  }


}