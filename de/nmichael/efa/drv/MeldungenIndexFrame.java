package de.nmichael.efa.drv;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.border.*;
import java.io.*;
import java.util.*;
import java.security.*;
import java.beans.*;
import de.nmichael.efa.*;
import de.nmichael.efa.Dialog;
import de.nmichael.efa.emil.*;
import java.net.*;


public class MeldungenIndexFrame extends JDialog implements ActionListener {
  public static final int MELD_FAHRTENABZEICHEN = 1;
  public static final int MELD_WANDERRUDERSTATISTIK = 2;

  Frame parent;
  DRVConfig drvConfig;
  int MELDTYP;
  String WETTID;

  boolean firstclick=false;
  JPanel jPanel1 = new JPanel();
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel northPanel = new JPanel();
  JPanel eastPanel = new JPanel();
  JPanel southPanel = new JPanel();
  JPanel centerPanel = new JPanel();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JLabel jLabel1 = new JLabel();
  JComboBox meldungenAuswahl = new JComboBox();
  JScrollPane jScrollPane1 = new JScrollPane();
  JTable meldungen;
  BorderLayout borderLayout2 = new BorderLayout();
  GridBagLayout gridBagLayout2 = new GridBagLayout();
  GridBagLayout gridBagLayout3 = new GridBagLayout();
  JButton closeButton = new JButton();
  JButton downloadButton = new JButton();
  JButton editButton = new JButton();
  JButton deleteButton = new JButton();
  JLabel jLabel2 = new JLabel();
  JLabel anzBestaetigte = new JLabel();
  JButton uploadButton = new JButton();
  JButton rejectButton = new JButton();
  JButton meldestatistikButton = new JButton();
  JButton checkFahrtenheftButton = new JButton();
  JButton printOverviewButton = new JButton();
  JButton exportButton = new JButton();
  JButton addButton = new JButton();


  public MeldungenIndexFrame(Frame parent, DRVConfig drvConfig, int meldTyp) {
    super(parent);
    this.drvConfig = drvConfig;
    this.MELDTYP = meldTyp;
    switch(meldTyp) {
      case MELD_FAHRTENABZEICHEN:
        WETTID = "DRV.FAHRTENABZEICHEN";
        break;
      case MELD_WANDERRUDERSTATISTIK:
        WETTID = "DRV.WANDERRUDERSTATISTIK";
        break;
    }
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      frameIni();
    }
    catch(Exception e) {
      e.printStackTrace();
    }
    this.setTitle("Meldungen f�r das Jahr "+drvConfig.aktJahr);
    EfaUtil.pack(this);
    this.parent = parent;
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
      this.setTitle("Meldungen f�r das Jahr ????");
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1"}, new String[] {"keyAction","keyAction"});
      jPanel1.setLayout(borderLayout1);
      northPanel.setLayout(gridBagLayout1);
      jLabel1.setText("folgende Meldungen anzeigen: ");
      centerPanel.setLayout(borderLayout2);
      southPanel.setLayout(gridBagLayout2);
      eastPanel.setLayout(gridBagLayout3);
      closeButton.setText("Schlie�en");
      closeButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          closeButton_actionPerformed(e);
        }
    });
      downloadButton.setText("Neue Meldungen downloaden");
      downloadButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          downloadButton_actionPerformed(e);
        }
    });
      editButton.setText("Meldung bearbeiten");
      editButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          editButton_actionPerformed(e);
        }
    });
      deleteButton.setText("Meldung l�schen");
      deleteButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          deleteButton_actionPerformed(e);
        }
    });
      jLabel2.setText("Zu best�tigende Meldungen: ");
      anzBestaetigte.setForeground(Color.blue);
      anzBestaetigte.setText("0");
      uploadButton.setText("Meldungen best�tigen");
      uploadButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          uploadButton_actionPerformed(e);
        }
    });
      rejectButton.setText("Meldung zur�ckweisen");
      rejectButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          rejectButton_actionPerformed(e);
        }
    });
      meldestatistikButton.setText("Meldestatistik erzeugen");
      meldestatistikButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          meldestatistikButton_actionPerformed(e);
        }
    });
      checkFahrtenheftButton.setText("Einzelnes eFahrtenheft pr�fen");
      checkFahrtenheftButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          checkFahrtenheftButton_actionPerformed(e);
        }
    });
      printOverviewButton.setText("�bersicht drucken");
      printOverviewButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          printOverviewButton_actionPerformed(e);
        }
    });
      exportButton.setText("Meldung exportieren");
      exportButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          exportButton_actionPerformed(e);
        }
    });
      addButton.setText("Meldung von Hand erfassen");
      addButton.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(ActionEvent e) {
          addButton_actionPerformed(e);
        }
    });
      this.getContentPane().add(jPanel1, BorderLayout.CENTER);
      jPanel1.add(northPanel, BorderLayout.NORTH);
      jPanel1.add(eastPanel, BorderLayout.EAST);
      eastPanel.add(downloadButton,        new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 10, 0), 0, 0));
      eastPanel.add(editButton,       new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      eastPanel.add(deleteButton,       new GridBagConstraints(0, 3, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      eastPanel.add(jLabel2,         new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      eastPanel.add(anzBestaetigte,       new GridBagConstraints(1, 5, 1, 2, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(50, 0, 0, 0), 0, 0));
      jPanel1.add(southPanel, BorderLayout.SOUTH);
      jPanel1.add(centerPanel, BorderLayout.CENTER);
      northPanel.add(jLabel1, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      northPanel.add(meldungenAuswahl,  new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      centerPanel.add(jScrollPane1, BorderLayout.CENTER);
      southPanel.add(closeButton, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
      eastPanel.add(uploadButton,      new GridBagConstraints(0, 7, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      eastPanel.add(rejectButton,      new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      eastPanel.add(meldestatistikButton,          new GridBagConstraints(0, 9, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(50, 0, 0, 0), 0, 0));
      eastPanel.add(checkFahrtenheftButton,     new GridBagConstraints(0, 8, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(50, 0, 0, 0), 0, 0));
      eastPanel.add(printOverviewButton,     new GridBagConstraints(0, 10, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      eastPanel.add(exportButton,      new GridBagConstraints(0, 5, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      eastPanel.add(addButton,    new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
      meldungenAuswahl.addItemListener(new java.awt.event.ItemListener() {
        public void itemStateChanged(ItemEvent e) {
          meldungenAuswahl_itemStateChanged(e);
        }
      });
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

  void meldungenAuswahl_itemStateChanged(ItemEvent e) {
    showMeldungen();
  }

  void frameIni() {
    meldungenAuswahl.addItem("alle");
    meldungenAuswahl.addItem("unbearbeitete");
    meldungenAuswahl.addItem("bearbeitete");
    meldungenAuswahl.addItem("zur�ckgewiesene");
    meldungenAuswahl.addItem("gel�schte");
    meldungenAuswahl.setSelectedIndex(1);

    if (MELDTYP == MELD_WANDERRUDERSTATISTIK) {
      checkFahrtenheftButton.setVisible(false);
    }

    showMeldungen();
  }

  void showMeldungen() {
    if (meldungen != null) jScrollPane1.getViewport().remove(meldungen);

    Vector m = new Vector();
    int auswahl = meldungenAuswahl.getSelectedIndex();
    if (auswahl < 0) auswahl = 0;

    // z�hlen der Meldungen, f�r die eine Best�tigungsdatei vorliegt, die noch nicht hochgeladen wurde
    int countBestaetigung = 0;

    // Alle Meldungen einlesen und relevante Meldungen merken
    for (DatenFelder d = drvConfig.meldungenIndex.getCompleteFirst(); d != null; d = drvConfig.meldungenIndex.getCompleteNext()) {
      int status = EfaUtil.string2int(d.get(MeldungenIndex.STATUS),MeldungenIndex.ST_UNBEKANNT);
      if (d.get(MeldungenIndex.STATUS).equals(Integer.toString(MeldungenIndex.ST_BEARBEITET)) &&
          d.get(MeldungenIndex.BESTAETIGUNGSDATEI).length()>0) countBestaetigung++;
      if (auswahl == 0 || auswahl == status) m.add(d);
    }

    this.anzBestaetigte.setText(Integer.toString(countBestaetigung));
    this.uploadButton.setEnabled(countBestaetigung > 0);

    int numberOfColumns = 0;
    switch(this.MELDTYP) {
      case MELD_FAHRTENABZEICHEN:
        numberOfColumns = 7;
        break;
      case MELD_WANDERRUDERSTATISTIK:
        numberOfColumns = 6;
        break;
    }

    String[][] tableData = new String[m.size()][numberOfColumns];
    for (int i=0; i<m.size(); i++) {
      int j=0;
      tableData[i][j++] = ((DatenFelder)m.get(i)).get(MeldungenIndex.QNR);
      tableData[i][j++] = ((DatenFelder)m.get(i)).get(MeldungenIndex.VEREIN);
      tableData[i][j++] = ((DatenFelder)m.get(i)).get(MeldungenIndex.MITGLNR);
      tableData[i][j++] = ((DatenFelder)m.get(i)).get(MeldungenIndex.DATUM);
      int stat = EfaUtil.string2int(((DatenFelder)m.get(i)).get(MeldungenIndex.STATUS),MeldungenIndex.ST_UNBEKANNT);
      if (stat < 0 || stat >= MeldungenIndex.ST_NAMES.length) stat = MeldungenIndex.ST_UNBEKANNT;
      tableData[i][j++] = MeldungenIndex.ST_NAMES[stat];
      if (this.MELDTYP == this.MELD_FAHRTENABZEICHEN) {
        int fh = EfaUtil.string2int(((DatenFelder)m.get(i)).get(MeldungenIndex.FAHRTENHEFTE),MeldungenIndex.FH_UNBEKANNT);
        switch(fh) {
          case MeldungenIndex.FH_UNBEKANNT:               tableData[i][j++] = ""; break;
          case MeldungenIndex.FH_KEINE:                   tableData[i][j++] = "keine"; break;
          case MeldungenIndex.FH_PAPIER:                  tableData[i][j++] = "Papier"; break;
          case MeldungenIndex.FH_ELEKTRONISCH:            tableData[i][j++] = "nur elektr."; break;
          case MeldungenIndex.FH_PAPIER_UND_ELEKTRONISCH: tableData[i][j++] = "elektr. / Papier"; break;
          default:                                        tableData[i][j++] = "";
        }
      }
      String best = "";
      if (((DatenFelder)m.get(i)).get(MeldungenIndex.STATUS).equals(Integer.toString(MeldungenIndex.ST_BEARBEITET))) {
        if (((DatenFelder)m.get(i)).get(MeldungenIndex.BESTAETIGUNGSDATEI).length()==0) best = "ja";
        else best = "noch nicht";
      }
      tableData[i][j++] = best;
    }

    String[] tableHeaderFA = { "Quittungsnr." , "Verein" , "Mitgliedsnr." , "Datum" , "Status" , "Fahrtenhefte" , "Best�tigt" };
    String[] tableHeaderWS = { "Quittungsnr." , "Verein" , "Mitgliedsnr." , "Datum" , "Status" , "Best�tigt" };
    String[] tableHeader = null;
    switch(this.MELDTYP) {
      case MELD_FAHRTENABZEICHEN:
        tableHeader = tableHeaderFA;
        break;
      case MELD_WANDERRUDERSTATISTIK:
        tableHeader = tableHeaderWS;
        break;
    }

    TableSorter sorter = new TableSorter(new DefaultTableModel(tableData,tableHeader));
    meldungen = new JTable(sorter);
    sorter.addMouseListenerToHeaderInTable(meldungen);
    meldungen.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        meldungen_mouseClicked(e);
      }
    });
    meldungen.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
      public void propertyChange(PropertyChangeEvent e) {
        meldungen_propertyChange(e);
      }
    });

    jScrollPane1.getViewport().add(meldungen, null);

    // intelligente Spaltenbreiten
    int width = meldungen.getSize().width;
    if (width < this.centerPanel.getSize().width-20 || width > this.getSize().width) { // beim ersten Aufruf steht Tabellenbreite noch nicht (korrekt) zur Verf�gung, daher dieser Plausi-Check
      width = this.centerPanel.getSize().width-10;
    }
    for (int i=0; i<numberOfColumns; i++) {
      TableColumn column = meldungen.getColumnModel().getColumn(i);
      switch(this.MELDTYP) {
        case MELD_FAHRTENABZEICHEN:
          switch (i) {
            case 0: column.setPreferredWidth(13 * width / 100); break; // QNr
            case 1: column.setPreferredWidth(24 * width / 100); break; // Verein
            case 2: column.setPreferredWidth(10 * width / 100); break; // Mitgliedsnr
            case 3: column.setPreferredWidth(17 * width / 100); break; // Datum
            case 4: column.setPreferredWidth(13 * width / 100); break; // Status
            case 5: column.setPreferredWidth(13 * width / 100); break; // Fahrtenheft
            case 6: column.setPreferredWidth(10 * width / 100); break; // Best�tigungsdatei
          }
          break;
        case MELD_WANDERRUDERSTATISTIK:
          switch (i) {
            case 0: column.setPreferredWidth(17 * width / 100); break; // QNr
            case 1: column.setPreferredWidth(32 * width / 100); break; // Verein
            case 2: column.setPreferredWidth(11 * width / 100); break; // Mitgliedsnr
            case 3: column.setPreferredWidth(17 * width / 100); break; // Datum
            case 4: column.setPreferredWidth(13 * width / 100); break; // Status
            case 5: column.setPreferredWidth(10 * width / 100); break; // Best�tigungsdatei
          }
          break;
      }
    }
  }

  void closeButton_actionPerformed(ActionEvent e) {
    cancel();
  }

  void downloadButton_actionPerformed(ActionEvent e) {
    if (drvConfig.efw_script == null || drvConfig.efw_script.length() == 0) {
      Dialog.error("Kein EFW-Script konfiguriert. Bitte vervollst�ndige zun�chst die Konfiguration!");
      return;
    }
    if (drvConfig.efw_user == null || drvConfig.efw_user.length() == 0) {
      Dialog.error("Kein EFW-Nutzer konfiguriert. Bitte vervollst�ndige zun�chst die Konfiguration!");
      return;
    }
    if (drvConfig.efw_password == null || drvConfig.efw_password.length() == 0) {
      Dialog.error("Kein EFW-Pa�wort konfiguriert. Bitte vervollst�ndige zun�chst die Konfiguration!");
      return;
    }
    if (drvConfig.testmode) {
      if (Dialog.yesNoDialog("Testmodus",
                             "Du befindest Dich im Testmodus!\n"+
                             "Dieser Modus ist nur f�r Testzwecke gedacht.\n"+
                             "M�chtest Du weitermachen?") != Dialog.YES) return;
    }
    if (!Dialog.okAbbrDialog("Internet-Verbindung","Bitte stelle eine Verbindung zum Internet her\nund klicke dann OK.")) return;

    String errorLog = "";

    Logger.log(Logger.INFO,"START Neue Meldungen aus dem Internet abrufen");
    String listFile = Daten.efaTmpDirectory+"meldungen.list";
    String url = drvConfig.makeScriptRequestString(DRVConfig.ACTION_LIST,null,null,null,null);
    if ((new File(listFile)).exists() && !(new File(listFile)).delete()) {
      Dialog.error("Datei\n"+listFile+"\nkann nicht gel�scht werden.");
      Logger.log(Logger.ERROR,"Datei\n"+listFile+"\nkann nicht gel�scht werden.");
      Logger.log(Logger.INFO,"ENDE Neue Meldungen aus dem Internet abrufen");
      return;
    }
    if (!EfaUtil.getFile(this,url,listFile,true) || !EfaUtil.canOpenFile(listFile)) {
      Dialog.error("Download der Meldungen-Indexdatei fehlgeschlagen.");
      Logger.log(Logger.ERROR,"Download der Meldungen-Indexdatei fehlgeschlagen.");
      Logger.log(Logger.INFO,"ENDE Neue Meldungen aus dem Internet abrufen");
      return;
    }

    int count = 0;
    BufferedReader f = null;
    try {
      f = new BufferedReader(new InputStreamReader(new FileInputStream(listFile),Daten.ENCODING));
      String s;
      while ( (s = f.readLine()) != null) {
        if (s.startsWith("ERROR")) {
          Dialog.error("Fehler beim Download:\n"+s);
          Logger.log(Logger.ERROR,"Fehler beim Download: "+s);
          Logger.log(Logger.INFO,"ENDE Neue Meldungen aus dem Internet abrufen");
          f.close();
          return;
        }
        Vector v = EfaUtil.split(s,'|');
        if (v.size() != 6) {
          Dialog.error("Meldungen-Indexdatei hat ung�ltiges Format!");
          Logger.log(Logger.ERROR,"Fehler beim Lesen der Meldungen-Indexdatei: Datei hat ung�ltiges Format ("+v.size()+" Felder).");
          Logger.log(Logger.INFO,"ENDE Neue Meldungen aus dem Internet abrufen");
          f.close();
          return;
        }
        String wettid = (String)v.get(0);
        if (!wettid.equals(WETTID+"."+drvConfig.aktJahr)) continue; // nur Meldungen des eingestellten Jahres abrufen
        String qnr = (String)v.get(1);
        DatenFelder dtmp = drvConfig.meldungenIndex.getExactComplete(qnr);
        if (dtmp == null || dtmp.get(MeldungenIndex.STATUS).equals(Integer.toString(MeldungenIndex.ST_GELOESCHT))) {
          url = drvConfig.makeScriptRequestString(DRVConfig.ACTION_GET,"item="+qnr,"verein="+(String)v.get(5),(drvConfig.testmode ? "testmode=true" : null),null);
          String localFile = Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+qnr+".efw";
          Logger.log(Logger.INFO,"Download der neuen Meldung "+qnr+" ...");
          if (!EfaUtil.getFile(this,url,localFile,true) || !EfaUtil.canOpenFile(localFile)) {
            errorLog += "Download der Meldung "+qnr+" fehlgeschlagen.\n";
            Logger.log(Logger.ERROR,"Download der Meldung "+qnr+" fehlgeschlagen.");
          } else {
            boolean meldungOk = true;
            boolean papierFahrtenhefteErforderlich = false;
            boolean elektronischeFahrtenhefteVorhanden = false;
            try {
              EfaWett efw = new EfaWett(localFile);
              if (!efw.readFile()) {
                errorLog += "Heruntergeladene Meldung "+qnr+" kann nicht gelesen werden.";
                Logger.log(Logger.ERROR,"Heruntergeladene Meldung "+qnr+" kann nicht gelesen werden.");
                meldungOk = false;
              } else {
                efw.resetDrvIntern(); // Interne Felder entsprechend zur�cksetzen (zur Sicherheit)
                for (EfaWettMeldung ew = efw.meldung; ew != null; ew = ew.next) {
                  if (ew.drv_fahrtenheft == null || ew.drv_fahrtenheft.length()==0) {
                    if (ew.drv_anzAbzeichen != null && ew.drv_anzAbzeichen.length()>0 &&
                        EfaUtil.string2int(ew.drv_anzAbzeichen,0)>0) papierFahrtenhefteErforderlich = true;
                  }
                  if (ew.drv_fahrtenheft != null) elektronischeFahrtenhefteVorhanden = true;
                }
                if (!efw.writeFile()) {
                  errorLog += "Heruntergeladene Meldung "+qnr+" kann nicht geschrieben werden.";
                  Logger.log(Logger.ERROR,"Heruntergeladene Meldung "+qnr+" kann nicht geschrieben werden.");
                }
              }
            } catch(IOException ee) {
              errorLog += "Heruntergeladene Meldung "+qnr+" kann nicht gelesen werden.";
              Logger.log(Logger.ERROR,"Heruntergeladene Meldung "+qnr+" kann nicht gelesen werden: "+ee.toString());
              meldungOk = false;
            }
            if (meldungOk) {
              DatenFelder d = new DatenFelder(MeldungenIndex._ANZFELDER);
              d.set(MeldungenIndex.QNR,qnr);
              d.set(MeldungenIndex.VEREIN,(String)v.get(4));
              d.set(MeldungenIndex.MITGLNR,(String)v.get(2));
              d.set(MeldungenIndex.DATUM,(String)v.get(3));
              d.set(MeldungenIndex.STATUS,Integer.toString(MeldungenIndex.ST_UNBEARBEITET));
              if (papierFahrtenhefteErforderlich && !elektronischeFahrtenhefteVorhanden) {
                d.set(MeldungenIndex.FAHRTENHEFTE,Integer.toString(MeldungenIndex.FH_PAPIER));
              } else if (!papierFahrtenhefteErforderlich && elektronischeFahrtenhefteVorhanden) {
                d.set(MeldungenIndex.FAHRTENHEFTE,Integer.toString(MeldungenIndex.FH_ELEKTRONISCH));
              } else if (papierFahrtenhefteErforderlich && elektronischeFahrtenhefteVorhanden) {
                d.set(MeldungenIndex.FAHRTENHEFTE,Integer.toString(MeldungenIndex.FH_PAPIER_UND_ELEKTRONISCH));
              } else if (!papierFahrtenhefteErforderlich && !elektronischeFahrtenhefteVorhanden) {
                d.set(MeldungenIndex.FAHRTENHEFTE,Integer.toString(MeldungenIndex.FH_KEINE));
              } else d.set(MeldungenIndex.FAHRTENHEFTE,Integer.toString(MeldungenIndex.FH_UNBEKANNT));

              drvConfig.meldungenIndex.add(d);
              count++;
              Logger.log(Logger.INFO,"Meldung "+qnr+" erfolgreich heruntergeladen.");
            }
          }
        }
      }
      f.close();
    } catch(Exception ee) {
      Dialog.error("Konnte Meldungs-Indexdatei nicht laden:\n"+ee.getMessage());
      Logger.log(Logger.ERROR,"Konnte Meldungs-Indexdatei nicht laden:\n"+ee.getMessage());
      return;
    }

    if (!drvConfig.meldungenIndex.writeFile()) {
      errorLog += "Meldungen-Indexdatei konnte nicht gespeichert werden.\n";
      Logger.log(Logger.ERROR,"Meldungen-Indexdatei konnte nicht gespeichert werden.");
    }

    if (errorLog.length()>0) {
      Dialog.error("Folgende Fehler sind aufgetreten:\n\n"+errorLog);
    }
    showMeldungen();
    Dialog.infoDialog(count+" neue Meldungen heruntergeladen!");
    Logger.log(Logger.INFO,count+" neue Meldungen heruntergeladen.");
    Logger.log(Logger.INFO,"ENDE Neue Meldungen aus dem Internet abrufen");
  }

  void exportField(BufferedWriter f, String field, String label) throws Exception {
    if (field != null) f.write(label+";"+field+"\n");
  }



  void exportButton_actionPerformed(ActionEvent e) {
    int row = meldungen.getSelectedRow();
    if (row < 0) {
      Dialog.error("Bitte w�hle zun�chst eine Meldung zum Exportieren aus!");
      return;
    }
    String qnr = null;
    try {
      qnr = (String)meldungen.getValueAt(row,0);
    } catch(Exception ee) {
      return;
    }
    if (qnr == null) return;

    DatenFelder d = drvConfig.meldungenIndex.getExactComplete(qnr);
    if (d == null) {
      Dialog.error("Meldung mit Quittungsnummer "+qnr+" nicht gefunden.");
      return;
    }
    EfaWett efw = new EfaWett(Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+qnr+".efw");
    String csv = Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+qnr+".csv";
    try {
      if (efw.readFile()) {
        BufferedWriter f = new BufferedWriter(new FileWriter(csv));
        exportField(f,efw.allg_wett,"Wettbewerb");
        exportField(f,efw.allg_wettjahr,"Jahr");
        f.write("\n");
        exportField(f,efw.verein_name,"Verein");
        exportField(f,efw.verein_ort,"Ort");
        exportField(f,efw.verein_lrv,"Bundesland");
        exportField(f,efw.verein_mitglnr,"Mitgliedsnummer");
        exportField(f,efw.verein_mitglieder,"Mitglieder");
        exportField(f,efw.verein_mitgl_in,"Verb�nde");
        exportField(f,efw.verein_user,"Benutzername");
        f.write("\n");
        exportField(f,efw.meld_name,"Meldende Person");
        exportField(f,efw.meld_email,"email");
        exportField(f,efw.meld_kto,"Konto");
        exportField(f,efw.meld_blz,"BLZ");
        exportField(f,efw.meld_bank,"Bank");
        f.write("\n");
        exportField(f,efw.versand_name,"Versand an");
        exportField(f,efw.versand_strasse,"Stra�e");
        exportField(f,efw.versand_ort,"Ort");
        f.write("\n");
        exportField(f,efw.drv_nadel_erw_silber,"Bestellung Nadel Erwachsene Silber");
        exportField(f,efw.drv_nadel_erw_gold,"Bestellung Nadel Erwachsene Gold");
        exportField(f,efw.drv_nadel_jug_silber,"Bestellung Nadel Jugendliche Silber");
        exportField(f,efw.drv_nadel_jug_gold,"Bestellung Nadel Jugendliche Gold");
        exportField(f,efw.drv_stoff_erw,"Bestellung Stoffabzeichen Erwachsene");
        exportField(f,efw.drv_stoff_jug,"Bestellung Stoffabzeichen Jugendliche");
        f.write("\n");
        exportField(f,efw.wimpel_mitglieder,"Gewertete Mitglieder f�r Blauer Wimpel");
        exportField(f,efw.wimpel_km,"Kilometer f�r Blauer Wimpel");
        exportField(f,efw.wimpel_schnitt,"Durchschnitt f�r Blauer Wimpel");
        f.write("\n");
        f.write("\n");

        boolean teilnHeader = true;
        if (WettDefs.STR_DRV_WANDERRUDERSTATISTIK.equals(efw.allg_wett)) {
          f.write("Start und Ziel;Gew�sser;Kilometer;Tage;Teilnehmer;Mannsch-Km;M�nner Anz;M�nner Km;Junioren Anz;Junioren Km;Frauen Anz;Frauen Km;Juniorinnen Anz;Juniorinnen Km\n");
          teilnHeader = false;
        }
        int i = 0;
        for (EfaWettMeldung m = efw.meldung; m != null; m = m.next) {
          if (teilnHeader) f.write("Meldung "+(++i)+"\n");
          exportField(f,m.nachname,"Nachname");
          exportField(f,m.vorname,"Vorname");
          exportField(f,m.jahrgang,"Jahrgang");
          exportField(f,m.geschlecht,"Geschlecht");
          exportField(f,m.gruppe,"Gruppe");
          exportField(f,m.kilometer,"Kilometer");
          exportField(f,m.restkm,"Rest-Km");
          exportField(f,m.anschrift,"Anschrift");
          exportField(f,m.abzeichen,"Abzeichen");
          exportField(f,m.drv_anzAbzeichen,"Abzeichen bisher");
          exportField(f,m.drv_gesKm,"Kilometer bisher");
          exportField(f,m.drv_anzAbzeichenAB,"davon Abzeichen A/B bisher");
          exportField(f,m.drv_gesKmAB,"davon Kilometer A/B bisher");
          exportField(f,m.drv_fahrtenheft,"Elektronisches Fahrtenheft");
          exportField(f,m.drv_aequatorpreis,"�quatorpreis");
          if (m.drvWS_StartZiel != null) {
            f.write(m.drvWS_StartZiel + ";" +
                    m.drvWS_Gewaesser + ";" +
                    m.drvWS_Km + ";" +
                    m.drvWS_Tage + ";" +
                    m.drvWS_Teilnehmer + ";" +
                    m.drvWS_MannschKm + ";" +
                    m.drvWS_MaennerAnz + ";" +
                    m.drvWS_MaennerKm + ";" +
                    m.drvWS_JuniorenAnz + ";" +
                    m.drvWS_JuniorenKm + ";" +
                    m.drvWS_FrauenAnz + ";" +
                    m.drvWS_FrauenKm + ";" +
                    m.drvWS_JuniorinnenAnz + ";" +
                    m.drvWS_JuniorinnenKm + "\n");
          }
          for (int x=0; m.fahrt != null && x<m.fahrt.length; x++) {
            String s = "";
            for (int y=0; m.fahrt[x] != null && y<m.fahrt[x].length; y++) {
              if (m.fahrt[x][y] != null) s += ";"+m.fahrt[x][y];
            }
            if (s.length() > m.fahrt[x].length) f.write("Fahrt "+(x+1)+s+"\n");
          }
          if (teilnHeader) f.write("\n");
        }
        f.close();
        Dialog.infoDialog("Meldung exportiert","Die Meldung wurde erfolgreich exportiert:\n"+csv);
      }
    } catch(Exception ee) {
      Dialog.error("Fehler beim Exportieren der Meldung: "+ee.getMessage());
    }
  }


  void editButton_actionPerformed(ActionEvent e) {
    int row = meldungen.getSelectedRow();
    if (row < 0) {
      Dialog.error("Bitte w�hle zun�chst eine Meldung zum Bearbeiten aus!");
      return;
    }
    String qnr = null;
    try {
      qnr = (String)meldungen.getValueAt(row,0);
    } catch(Exception ee) {
      return;
    }
    if (qnr == null) return;

    DatenFelder d = drvConfig.meldungenIndex.getExactComplete(qnr);
    if (d == null) {
      Dialog.error("Meldung mit Quittungsnummer "+qnr+" nicht gefunden.");
      return;
    }
    switch (EfaUtil.string2int(d.get(MeldungenIndex.STATUS),-1)) {
      case MeldungenIndex.ST_GELOESCHT:
        Dialog.error("Die gew�hlte Meldung wurde bereits gel�scht und kann nicht mehr bearbeitet werden.");
        return;
      case MeldungenIndex.ST_ZURUECKGEWIESEN:
        Dialog.error("Die gew�hlte Meldung wurde bereits zur�ckgewiesen und kann nicht mehr bearbeitet werden.");
        return;
    }

    EfaWett efw = new EfaWett(Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+qnr+".efw");
    try {
      if (efw.readFile()) {
        if (MELDTYP == MELD_FAHRTENABZEICHEN && !drvConfig.readOnlyMode) {
          if (drvConfig.keyPassword == null) KeysAdminFrame.enterKeyPassword(drvConfig);
          if (drvConfig.keyPassword == null) return;
          if (!loadKeys()) return;
        }

        MeldungEditFrame dlg = new MeldungEditFrame(this,drvConfig,efw,qnr,MELDTYP);
        Dialog.setDlgLocation(dlg,this);
        dlg.setModal(true);
        dlg.show();
      } else Dialog.error("Fehler beim Lesen der Meldungsdatei.");
    } catch(Exception eee) {
      Dialog.error("Fehler beim Lesen der Meldungsdatei: "+eee.toString());
    }
    showMeldungen();
  }

  // Edit bei Doppelklick
  void meldungen_mouseClicked(MouseEvent e) {
    firstclick=true;
  }

  // komisch, manchmal scheint diese Methode irgendwie nicht zu ziehen.....
  void meldungen_propertyChange(PropertyChangeEvent e) {
    if (meldungen.isEditing()) {
      if (firstclick) {
        firstclick=false;
        editButton_actionPerformed(null);
      }
    }
  }

  void deleteButton_actionPerformed(ActionEvent e) {
    if (drvConfig.readOnlyMode) {
      Dialog.error("Diese Funktion ist in diesem Modus nicht erlaubt.");
      return;
    }
    int row = meldungen.getSelectedRow();
    if (row < 0) {
      Dialog.error("Bitte w�hle zun�chst eine Meldung zum L�schen aus!");
      return;
    }
    String qnr = null;
    try {
      qnr = (String)meldungen.getValueAt(row,0);
    } catch(Exception ee) {
      return;
    }
    if (qnr == null) return;
    if (Dialog.yesNoDialog("Meldung l�schen","M�chtest Du die Meldung mit Quittungsnummer "+qnr+" wirklich l�schen?") != Dialog.YES) return;

    DatenFelder d = drvConfig.meldungenIndex.getExactComplete(qnr);
    if (d == null) {
      Dialog.error("Meldung mit Quittungsnummer "+qnr+" nicht gefunden.");
      return;
    }
    if (EfaUtil.string2int(d.get(MeldungenIndex.STATUS),-1) != MeldungenIndex.ST_UNBEARBEITET &&
        EfaUtil.string2int(d.get(MeldungenIndex.STATUS),-1) != MeldungenIndex.ST_ZURUECKGEWIESEN) {
        if (Dialog.yesNoDialog("Meldung wirklich l�schen",
                               "Nur unbearbeitete oder zur�ckgewiesene Meldungen sollten gel�scht werden.\n"+
                               "Eine Meldung, die bereits bearbeitet wurde, sollte nur in Ausnahmef�llen\n"+
                               "gel�scht werden (bspw. wenn sie wegen fehlerhafter Bearbeitung erneut aus\n"+
                               "dem Internet heruntergeladen werden soll in der Form, in der sie vom\n"+
                               "Verein eingeschickt wurde).\n"+
                               "M�chtest Du die Meldung wirklich l�schen?") != Dialog.YES) return;
    }

    d.set(MeldungenIndex.STATUS,Integer.toString(MeldungenIndex.ST_GELOESCHT));
    if (!drvConfig.meldungenIndex.writeFile()) {
      Logger.log(Logger.ERROR,"Meldungen-Indexdatei konnte nicht geschrieben werden!");
    }
    Logger.log(Logger.INFO,"Meldung "+qnr+" gel�scht!");
    showMeldungen();
  }

  void uploadButton_actionPerformed(ActionEvent e) {
    if (drvConfig.readOnlyMode) {
      Dialog.error("Diese Funktion ist in diesem Modus nicht erlaubt.");
      return;
    }
    Vector bestaetigungen = new Vector();
    for (DatenFelder d = drvConfig.meldungenIndex.getCompleteFirst(); d != null; d = drvConfig.meldungenIndex.getCompleteNext()) {
      if (d.get(MeldungenIndex.STATUS).equals(Integer.toString(MeldungenIndex.ST_BEARBEITET)) &&
          d.get(MeldungenIndex.BESTAETIGUNGSDATEI).length() > 0) {
        if (MELDTYP == MELD_FAHRTENABZEICHEN) {
          bestaetigungen.add(d.get(MeldungenIndex.BESTAETIGUNGSDATEI));
        }
        if (MELDTYP == MELD_WANDERRUDERSTATISTIK) {
          bestaetigungen.add(EfaUtil.getPathOfFile(drvConfig.meldungenIndex.getFileName())+Daten.fileSep+d.get(MeldungenIndex.QNR)+".efw");
        }
      }
    }
    if (bestaetigungen.size() == 0) {
      Dialog.error("Es liegen keine Meldungen zum Best�tigen vor.");
      return;
    }
    boolean einzeln = false;
    switch(Dialog.auswahlDialog("Meldungen best�tigen",
                                "Es liegen "+bestaetigungen.size()+" Meldungen zum Best�tigen vor.\n"+
                                "M�chtest Du jetzt alle Meldungen oder nur einzelne Meldungen best�tigen?",
                                "Alle best�tigen","Einzeln best�tigen",true)) {
      case 0: einzeln = false; break;
      case 1: einzeln = true;  break;
      default: return;
    }
    if (!Dialog.okAbbrDialog("Internet-Verbindung","Bitte stelle eine Verbindung zum Internet her\nund klicke dann OK.")) return;

    Logger.log(Logger.INFO,"START Meldungen best�tigen");
    String errors = "";
    int cok = 0;
    for (int i=0; i<bestaetigungen.size(); i++) {
      String filename = (String)bestaetigungen.get(i);
      if (!EfaUtil.canOpenFile(filename)) {
        Logger.log(Logger.ERROR,"(Best�tigungs-)Datei "+filename+" existiert nicht.");
        errors += "(Best�tigungs-)Datei "+filename+" existiert nicht!\n";
        continue;
      }

      String verein = null;
      String vereinsname = null;
      String qnr = null;
      String wettid = WETTID+"."+drvConfig.aktJahr;
      try {

        if (MELDTYP == MELD_FAHRTENABZEICHEN) {
          ESigFahrtenhefte esfh = new ESigFahrtenhefte(filename);
          if (!esfh.readFile()) {
            errors += "Best�tigungsdatei "+filename+" kann nicht gelesen werden!\n";
            Logger.log(Logger.ERROR,"Best�tigungsdatei "+filename+" kann nicht gelesen werden.");
            continue;
          }
          verein = esfh.verein_user;
          vereinsname = esfh.verein_name;
          qnr = esfh.quittungsnr;
        }

        if (MELDTYP == MELD_WANDERRUDERSTATISTIK) {
          EfaWett efw = new EfaWett(filename);
          if (!efw.readFile()) {
            errors += "Meldungsdatei "+filename+" kann nicht gelesen werden!\n";
            Logger.log(Logger.ERROR,"Meldungsdatei "+filename+" kann nicht gelesen werden.");
            continue;
          }
          verein = efw.verein_user;
          vereinsname = efw.verein_name;
          qnr = EfaUtil.getFilenameWithoutPath(filename); qnr = qnr.substring(0,qnr.length()-4);
        }

        if (einzeln && Dialog.yesNoDialog("Meldung "+qnr+" best�tigen",
                                          "Soll die Meldung "+qnr+" des Vereins\n"+
                                          vereinsname+" best�tigt werden?") != Dialog.YES) continue;

        String data = "";

        if (MELDTYP == MELD_FAHRTENABZEICHEN) {
          BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(filename),Daten.ENCODING));
          String z;
          while ( (z = f.readLine()) != null) {
            z = EfaUtil.replace(z,"\"","**2**",true); // " als **2** maskieren
            z = EfaUtil.replace(z,"=","**0**",true);  // = als **0** maskieren
            data += z+"**#**"; // Zeilenumbr�che als **#** maskieren
          }
          f.close();
        }

        if (MELDTYP == MELD_WANDERRUDERSTATISTIK) {
          data = "##DRV.WANDERRUDERSTATISTIK##";
        }

        String request = drvConfig.makeScriptRequestString(DRVConfig.ACTION_UPLOAD,"verein="+verein,"qnr="+qnr,"wettid="+wettid,"data="+data);
        int pos = request.indexOf("?");
        if (pos < 0) {
          Logger.log(Logger.ERROR,"efaWett-Anfrage f�r Best�tigungsdatei "+filename+" kann nicht erstellt werden.");
          errors += "efaWett-Anfrage f�r Best�tigungsdatei "+filename+" kann nicht erstellt werden.\n";
          continue;
        }
        String url = request.substring(0,pos);
        String content = request.substring(pos+1,request.length());

        HttpURLConnection conn = (HttpURLConnection)(new URL(url)).openConnection();
        conn.setRequestMethod("POST");
        conn.setAllowUserInteraction(false);
        conn.setDoInput(true);
        conn.setDoOutput(true);
        conn.setUseCaches(false);
        conn.setRequestProperty("Content-type","application/x-www-form-urlencoded");
        conn.setRequestProperty("Content-length",Integer.toString(content.length()));
        DataOutputStream out = new DataOutputStream(conn.getOutputStream ());
        out.writeBytes(content);
        out.flush();
        out.close();
        conn.disconnect();
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        boolean ok = true;
        String z;
        while((z = in.readLine()) != null) {
          if (!z.equals("OK")) {
            Logger.log(Logger.ERROR,"Fehler beim Best�tigen von Meldung "+qnr+": "+z);
            errors += "Fehler beim Best�tigen von Meldung "+qnr+": "+z+"\n";
            ok = false;
          }
        }
        if (ok) {
          DatenFelder d = drvConfig.meldungenIndex.getExactComplete(qnr);
          if (d == null) {
            Logger.log(Logger.ERROR,"Konnte Status f�r Meldung "+qnr+" nicht aktualisieren: Meldung nicht gefunden!");
            errors += "Konnte Status f�r Meldung "+qnr+" nicht aktualisieren: Meldung nicht gefunden!\n";
          } else {
            d.set(MeldungenIndex.BESTAETIGUNGSDATEI,"");
            drvConfig.meldungenIndex.delete(qnr);
            drvConfig.meldungenIndex.add(d);
          }
          Logger.log(Logger.INFO,"Meldung "+qnr+" von Verein "+verein+" wurde erfolgreich best�tigt!�");
          cok++;
        }
      } catch(Exception ee) {
        Logger.log(Logger.ERROR,"Fehler beim Best�tigen von Meldung "+qnr+": "+ee.toString());
        errors += "Fehler beim Best�tigen von Meldung "+qnr+": "+ee.getMessage()+"\n";
      }
    }
    if (!drvConfig.meldungenIndex.writeFile()) {
      errors += "Meldungen-Indexdatei konnte nicht geschrieben werden!\n";
      Logger.log(Logger.ERROR,"Meldungen-Indexdatei konnte nicht geschrieben werden!");
    }
    if (errors.length() > 0) {
      Dialog.error("Bei der Best�tigung der Meldungen traten folgende Fehler auf:\n"+errors);
    }
    Dialog.infoDialog("Meldungen best�tigt","Es wurden "+cok+" Meldungen erfolgreich best�tigt.");
    Logger.log(Logger.INFO,"ENDE Meldungen best�tigen");
    showMeldungen();
  }

  void rejectButton_actionPerformed(ActionEvent e) {
    if (drvConfig.readOnlyMode) {
      Dialog.error("Diese Funktion ist in diesem Modus nicht erlaubt.");
      return;
    }
    int row = meldungen.getSelectedRow();
    if (row < 0) {
      Dialog.error("Bitte w�hle zun�chst eine Meldung zum Zur�ckweisen aus!");
      return;
    }
    String qnr = null;
    try {
      qnr = (String)meldungen.getValueAt(row,0);
    } catch(Exception ee) {
      return;
    }
    if (qnr == null) return;

    DatenFelder d = drvConfig.meldungenIndex.getExactComplete(qnr);
    if (d == null) {
      Dialog.error("Meldung mit Quittungsnummer "+qnr+" nicht gefunden.");
      return;
    }
    if (EfaUtil.string2int(d.get(MeldungenIndex.STATUS),-1) != MeldungenIndex.ST_UNBEARBEITET) {
      if (EfaUtil.string2int(d.get(MeldungenIndex.STATUS),-1) == MeldungenIndex.ST_ZURUECKGEWIESEN) {
        Dialog.error("Die Meldung wurde bereits zur�ckgewiesen. Sie kann nicht nochmals zur�ckgewiesen werden.");
        return;
      }
      if (Dialog.yesNoDialog("Meldung wirklich zur�ckweisen",
                             "Nur unbearbeitete Meldungen sollten zur�ckgewiesen werden.\n"+
                             "Eine Meldung, die bereits bearbeitet wurde, sollte nur in Ausnahmef�llen\n"+
                             "zur�ckgewiesen werden (etwa nach R�cksprache mit dem Verein, falls bspw. eine\n"+
                             "korrigierte Meldung eingeschickt werden soll).\n"+
                             "M�chtest Du die Meldung wirklich zur�ckweisen?") != Dialog.YES) return;
    }

    String verein = null;
    EfaWett ew = new EfaWett(Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+qnr+".efw");
    try {
      if (ew.readFile()) {
        verein = ew.verein_user;
      } else {
        Dialog.error("Fehler beim Lesen der Meldungsdatei.");
        return;
      }
    } catch(Exception eee) {
      Dialog.error("Fehler beim Lesen der Meldungsdatei: "+eee.toString());
      return;
    }
    if (verein == null || verein.length() == 0) {
      Dialog.error("Konnte Vereinsnamen aus Meldedatei nicht ermitteln!");
      return;
    }

    if (Dialog.yesNoDialog("Meldung zur�ckweisen","M�chtest Du die Meldung "+qnr+" des Vereins "+verein+" wirklich zur�ckweisen?") != Dialog.YES) return;
    String grund = Dialog.inputDialog("Grund f�r Zur�ckweisen","Bitte gib einen Grund ein, aus dem die Meldung zur�ckgewiesen wurde.\n"+
                                                               "Dieser wird dem Verein per email mitgeteilt.");
    if (grund == null) return;
    if (d == null) {
      Dialog.error("Meldung mit Quittungsnummer "+qnr+" nicht gefunden.");
      return;
    }

    if (!Dialog.okAbbrDialog("Internet-Verbindung","Bitte stelle eine Verbindung zum Internet her\nund klicke dann OK.")) return;

    String url = drvConfig.makeScriptRequestString(DRVConfig.ACTION_REJECT,"verein="+verein,"qnr="+qnr,"grund="+EfaUtil.replace(grund," ","+",true),null);
    String localFile = Daten.efaTmpDirectory+"efwstatus.tmp";
    if (!EfaUtil.getFile(this,url,localFile,true) || !EfaUtil.canOpenFile(localFile)) {
      Logger.log(Logger.ERROR,"Zur�ckweisen der Meldung "+qnr+" von Verein "+verein+" fehlgeschlagen: Kann efaWett nicht erreichen");
      Dialog.error("Aktion fehlgeschlagen: Kann efaWett nicht erreichen");
      return;
    }
    try {
      BufferedReader f = new BufferedReader(new FileReader(localFile));
      String s = f.readLine();
      if (s == null || !s.equals("OK")) {
        Logger.log(Logger.ERROR,"Zur�ckweisen der Meldung "+qnr+" von Verein "+verein+" fehlgeschlagen: "+s);
        Dialog.error("Aktion fehlgeschlagen: "+s);
        return;
      }
      f.close();
      EfaUtil.deleteFile(localFile);
    } catch(Exception ee) {
      Logger.log(Logger.ERROR,"Zur�ckweisen der Meldung "+qnr+" von Verein "+verein+" fehlgeschlagen: "+ee.getMessage());
      Dialog.error("Aktion fehlgeschlagen: "+ee.getMessage());
      return;
    }

    d.set(MeldungenIndex.STATUS,Integer.toString(MeldungenIndex.ST_ZURUECKGEWIESEN));
    d.set(MeldungenIndex.BESTAETIGUNGSDATEI,"");
    if (!drvConfig.meldungenIndex.writeFile()) {
      Logger.log(Logger.ERROR,"Meldungen-Indexdatei konnte nicht geschrieben werden!");
    }
    Logger.log(Logger.INFO,"Meldung "+qnr+" von Verein "+verein+" zur�ckgewiesen. Grund: "+grund);
    Dialog.infoDialog("Meldung zur�ckgewiesen!");
    showMeldungen();
  }

  void meldestatistikButton_actionPerformed(ActionEvent e) {
    if (MELDTYP == MELD_FAHRTENABZEICHEN) createMeldestatistikFA();
    if (MELDTYP == MELD_WANDERRUDERSTATISTIK) createMeldestatistikWS();
  }

  void createMeldestatistikFA() {
    try {
      Hashtable mitglnr_hash = new Hashtable();
      String stat_complete = Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+"meldestatistik_komplett.csv";
      BufferedWriter f;
      f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(stat_complete),Daten.ENCODING));
      f.write("Verein;Vorname;Nachname;Jahrgang;Geschlecht;Kilometer;Gruppe;AnzAbzeichen(ges);AnzAbzeichen(AB);�quator\n");
      for (DatenFelder d = drvConfig.meldestatistik.getCompleteFirst(); d != null; d = drvConfig.meldestatistik.getCompleteNext()) {
        f.write(d.get(Meldestatistik.VEREIN)+";"+d.get(Meldestatistik.VORNAME)+";"+d.get(Meldestatistik.NACHNAME)+";"+
                d.get(Meldestatistik.JAHRGANG)+";"+d.get(Meldestatistik.GESCHLECHT)+";"+d.get(Meldestatistik.KILOMETER)+";"+
                d.get(Meldestatistik.GRUPPE)+";"+d.get(Meldestatistik.ANZABZEICHEN)+";"+d.get(Meldestatistik.ANZABZEICHENAB)+";"+
                d.get(Meldestatistik.AEQUATOR)+"\n");
        mitglnr_hash.put(d.get(Meldestatistik.VEREINSMITGLNR),"foo");
      }
      f.close();

      String stat_div = Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+"meldestatistik_einzeln.csv";
      f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(stat_div),Daten.ENCODING));

      // Tabelle 2: �bersicht M�nner Frauen Junioren Juniorinnen
      f.write("Statistik 2: �bersicht M�nner / Frauen / Junioren / Junioninnen\n");
      f.write("M�nner 18-30;M�nner 31-60;M�nner �ber 60;Frauen 18-30;Frauen 31-60;Frauen �ber 60;Junioren 13/14;Junioren 15/16;Junioren 17/18;Juniorinnen 13/14;Juniorinnen 15/16;Juniorinnen 17/18;\n");
      int data[] = new int[12];
      for (DatenFelder d = drvConfig.meldestatistik.getCompleteFirst(); d != null; d = drvConfig.meldestatistik.getCompleteNext()) {
        if (d.get(Meldestatistik.GESCHLECHT).equals(EfaWettMeldung.GESCHLECHT_M)) {
          int jahrgang = EfaUtil.string2int(d.get(Meldestatistik.JAHRGANG),0);
          if (drvConfig.aktJahr-jahrgang>=18 && drvConfig.aktJahr-jahrgang<=30) data[0]++;
          if (drvConfig.aktJahr-jahrgang>=31 && drvConfig.aktJahr-jahrgang<=60) data[1]++;
          if (drvConfig.aktJahr-jahrgang>=61                                  ) data[2]++;
          if (drvConfig.aktJahr-jahrgang>=13 && drvConfig.aktJahr-jahrgang<=14) data[6]++;
          if (drvConfig.aktJahr-jahrgang>=15 && drvConfig.aktJahr-jahrgang<=16) data[7]++;
          if (drvConfig.aktJahr-jahrgang>=17 && drvConfig.aktJahr-jahrgang<=18) data[8]++;
        }
        if (d.get(Meldestatistik.GESCHLECHT).equals(EfaWettMeldung.GESCHLECHT_W)) {
          int jahrgang = EfaUtil.string2int(d.get(Meldestatistik.JAHRGANG),0);
          if (drvConfig.aktJahr-jahrgang>=18 && drvConfig.aktJahr-jahrgang<=30) data[3]++;
          if (drvConfig.aktJahr-jahrgang>=31 && drvConfig.aktJahr-jahrgang<=60) data[4]++;
          if (drvConfig.aktJahr-jahrgang>=61                                  ) data[5]++;
          if (drvConfig.aktJahr-jahrgang>=13 && drvConfig.aktJahr-jahrgang<=14) data[9]++;
          if (drvConfig.aktJahr-jahrgang>=15 && drvConfig.aktJahr-jahrgang<=16) data[10]++;
          if (drvConfig.aktJahr-jahrgang>=17 && drvConfig.aktJahr-jahrgang<=18) data[11]++;
        }
      }
      for (int i=0; i<data.length; i++) f.write((i > 0 ? ";" : "") + data[i]);
      f.write("\n\n\n");

      // Tabelle 3: 75 Jahre und �lter
      SortedStatistic sStat = new SortedStatistic();
      f.write("Statistik 3: Jahrgang "+(drvConfig.aktJahr - 75)+" und �lter (75 Jahre und �lter)\n");
      f.write("Jahrgang;Name, Verein;Km\n");
      for (DatenFelder d = drvConfig.meldestatistik.getCompleteFirst(); d != null; d = drvConfig.meldestatistik.getCompleteNext()) {
        int jahrgang = EfaUtil.string2int(d.get(Meldestatistik.JAHRGANG),9999);
        if (jahrgang <= drvConfig.aktJahr - 75) {
          int key = jahrgang * 100000 + (999999 - EfaUtil.string2int(d.get(Meldestatistik.KILOMETER),0));
          sStat.add(key,null,
                    d.get(Meldestatistik.JAHRGANG),
                    d.get(Meldestatistik.NACHNAME)+" "+d.get(Meldestatistik.VORNAME)+", "+d.get(Meldestatistik.VEREIN),
                    d.get(Meldestatistik.KILOMETER));
        }
      }
      sStat.sort(true);
      for (int i=0; i<sStat.sortedSize(); i++) {
        String[] sdata = sStat.getSorted(i);
        f.write(sdata[0]+";"+sdata[1]+";"+sdata[2]+"\n");
      }
      f.write("\n\n\n");

      // Tabelle 4: �ber 4000 Km
      sStat = new SortedStatistic();
      f.write("Statistik 4: �ber 4000 Km haben gerudert:\n");
      f.write("Platz;Km;Name/Jahrgang/Verein\n");
      for (DatenFelder d = drvConfig.meldestatistik.getCompleteFirst(); d != null; d = drvConfig.meldestatistik.getCompleteNext()) {
        int km = EfaUtil.string2int(d.get(Meldestatistik.KILOMETER),0);
        if (km >= 4000) {
          sStat.add(km,null,
                    d.get(Meldestatistik.KILOMETER),
                    d.get(Meldestatistik.NACHNAME)+", "+d.get(Meldestatistik.VORNAME)+" ("+d.get(Meldestatistik.JAHRGANG)+"), "+d.get(Meldestatistik.VEREIN),
                    null);
        }
      }
      sStat.sort(false);
      for (int i=0; i<sStat.sortedSize(); i++) {
        String[] sdata = sStat.getSorted(i);
        f.write((i+1)+".;"+sdata[0]+";"+sdata[1]+"\n");
      }
      f.write("\n\n\n");

      // Tabelle 5: Fahrtenabzeichen in Gold
      sStat = new SortedStatistic();
      f.write("Statistik 5: Fahrtenabzeichen in Gold\n");
      f.write("Platz;Km;Name/Jahrgang/Verein\n");
      for (int _anzAbz = 0; _anzAbz <= 95; _anzAbz += 5) {
        for (DatenFelder d = drvConfig.meldestatistik.getCompleteFirst(); d != null; d = drvConfig.meldestatistik.getCompleteNext()) {
          int anzAbz = EfaUtil.string2int(d.get(Meldestatistik.ANZABZEICHEN),0);
          int anzAbzAB = EfaUtil.string2int(d.get(Meldestatistik.ANZABZEICHENAB),0);
          boolean erwachsen = !d.get(Meldestatistik.GRUPPE).startsWith("3");
          if ( (_anzAbz == 0 && !erwachsen && anzAbz % 5 == 0) || // Jugend-Gold
               (_anzAbz >  0 && (anzAbz - anzAbzAB) == _anzAbz) ) { // Erw-Gold

            String abz = null;
            if (_anzAbz == 0) abz = "99";
            else if (_anzAbz < 10) abz = "0"+_anzAbz;
            else abz = Integer.toString(_anzAbz);

            String name = d.get(Meldestatistik.NACHNAME)+", "+d.get(Meldestatistik.VORNAME)+" ("+d.get(Meldestatistik.JAHRGANG)+"), "+d.get(Meldestatistik.VEREIN);

            sStat.add(-1,abz+name,abz,name,null);
          }
        }
      }
      sStat.sort(true);
      int c=0;
      String lastAbz = "xx";
      for (int i=0; i<sStat.sortedSize(); i++) {
        String[] sdata = sStat.getSorted(i);
        if (!sdata[0].equals(lastAbz)) {
          c = 0;
          lastAbz = sdata[0];
          if (!sdata[0].equals("99")) {
            f.write("Zum "+sdata[0]+". Mal erf�llt:\n");
          } else {
            f.write("Jugendfahrtenabzeichen in Gold:\n");
          }
        }
        f.write((++c)+".;"+sdata[1]+"\n");
      }
      f.write("\n\n\n");

      // Tabelle 6: �quatorpreistr�ger
      f.write("Statistik 6: �quatorpreistr�ger:\n");
      f.write("Verein;Vorname;Nachname;Kilometer;�quatorpreis\n");
      for (DatenFelder d = drvConfig.meldestatistik.getCompleteFirst(); d != null; d = drvConfig.meldestatistik.getCompleteNext()) {
        if (d.get(Meldestatistik.AEQUATOR).length() > 0) {
          f.write(d.get(Meldestatistik.VEREIN)+";"+d.get(Meldestatistik.VORNAME)+";"+d.get(Meldestatistik.NACHNAME)+";"+
                d.get(Meldestatistik.GESKM)+";"+d.get(Meldestatistik.AEQUATOR)+"\n");
        }
      }
      f.write("\n\n\n");


      f.close();
      String stat_info = "";
      if (mitglnr_hash.size() < drvConfig.meldungenIndex.countElements()) {
        int differenz = drvConfig.meldungenIndex.countElements() - mitglnr_hash.size();
        stat_info = "ACHTUNG: Die Statistik enth�lt nur die Daten von "+mitglnr_hash.size()+" bereits fertig\n"+
                    "bearbeiteten Vereinen. "+differenz+" nicht bearbeitete Vereine wurden NICHT ber�cksichtigt.\n\n";
      }
      Dialog.infoDialog("Statistiken exportiert",
                        "Die Statistiken wurden erfolgreich erstellt:\n"+
                        stat_complete+"\n"+
                        stat_div+"\n\n"+
                        stat_info);
      if (Dialog.yesNoDialog("Statistiken kopieren",
                             "Sollen die erzeugten Statistiken kopiert\n"+
                             "werden (z.B. auf Diskette)?") == Dialog.YES) {
        String ziel = Dialog.inputDialog("Ziel","Wohin sollen die Dateien kopiert werden?","A:\\");
        if (ziel != null && ziel.trim().length()>0) {
          ziel = ziel.trim();
          if (!ziel.endsWith(Daten.fileSep)) ziel += Daten.fileSep;
          if (!(new File(ziel)).isDirectory()) {
            Dialog.error("Statistiken k�nnen nicht kopiert werden:\nZielverzeichnis "+ziel+" existiert nicht.");
          } else {
            boolean b1 = EfaUtil.copyFile(stat_complete,ziel+EfaUtil.getFilenameWithoutPath(stat_complete));
            boolean b2 = EfaUtil.copyFile(stat_div,ziel+EfaUtil.getFilenameWithoutPath(stat_div));
            if (b1 && b2) Dialog.infoDialog("Statistiken erfolgreich kopiert.");
            else Dialog.error("Statistiken konnten nicht kopiert werden.");
          }
        }
      }
    } catch(Exception ee) {
      Dialog.error("Fehler beim Erstellen der Statistik: "+ee.toString());
      Logger.log(Logger.ERROR,"Fehler beim Erstellen der Statistik: "+ee.toString());
    }
  }

  void createMeldestatistikWS() {
    try {
      String stat_complete = Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+"wanderruderstatistik.csv";
      BufferedWriter f;
      f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(stat_complete),Daten.ENCODING));
      f.write("Vereinsnummer;Verein;Bundesland;SRV/ADH;Befahrene Gew�sser;Teilnehmer insg.;Mannschafts-Km;M�nner Km; Junioren Km; Frauen Km; Juniorinnen Km\n");
      Hashtable mitglnr_hash = new Hashtable();
      for (DatenFelder d = drvConfig.meldestatistik.getCompleteFirst(); d != null; d = drvConfig.meldestatistik.getCompleteNext()) {
        String mitgl_in = "";
        if (d.get(Meldestatistik.WS_MITGLIEDIN).indexOf("SRV")>=0) mitgl_in = "SRV";
        if (d.get(Meldestatistik.WS_MITGLIEDIN).indexOf("ADH")>=0) mitgl_in += (mitgl_in.length() > 0 ? ", " : "") + "ADH";
        f.write(d.get(Meldestatistik.VEREINSMITGLNR)+";"+
                d.get(Meldestatistik.VEREIN)+";"+
                d.get(Meldestatistik.WS_BUNDESLAND)+";"+
                mitgl_in+";"+
                d.get(Meldestatistik.WS_GEWAESSER)+";"+
                d.get(Meldestatistik.WS_TEILNEHMER)+";"+
                d.get(Meldestatistik.WS_MANNSCHKM)+";"+
                d.get(Meldestatistik.WS_MAENNERKM)+";"+
                d.get(Meldestatistik.WS_JUNIORENKM)+";"+
                d.get(Meldestatistik.WS_FRAUENKM)+";"+
                d.get(Meldestatistik.WS_JUNIORINNENKM)+"\n");
        mitglnr_hash.put(d.get(Meldestatistik.VEREINSMITGLNR),"foo");
      }
      f.close();

      String stat_info = "";
      if (mitglnr_hash.size() < drvConfig.meldungenIndex.countElements()) {
        int differenz = drvConfig.meldungenIndex.countElements() - mitglnr_hash.size();
        stat_info = "ACHTUNG: Die Statistik enth�lt nur die Daten von "+mitglnr_hash.size()+" bereits fertig\n"+
                    "bearbeiteten Vereinen. "+differenz+" nicht bearbeitete Vereine wurden NICHT ber�cksichtigt.\n\n";
      }

      Dialog.infoDialog("Statistik exportiert",
                        "Die Statistik wurde erfolgreich erstellt:\n"+
                        stat_complete+"\n\n"+
                        stat_info+
                        "Um die Statistik in Excel zu �ffnen:\n"+
                        "1. Excel starten\n"+
                        "2. ->Datei->�ffnen w�hlen\n"+
                        "3. In den Ordner \""+EfaUtil.getPathOfFile(stat_complete)+"\" wechseln\n"+
                        "4. Bei der Auswahl der anzuzeigenden Dateien \"Alle Dateien\" w�hlen\n"+
                        "5. Die Datei \""+EfaUtil.getFilenameWithoutPath(stat_complete)+"\" �ffnen");
    } catch(Exception ee) {
      Dialog.error("Fehler beim Erstellen der Statistik: "+ee.toString());
      Logger.log(Logger.ERROR,"Fehler beim Erstellen der Statistik: "+ee.toString());
    }
  }


  boolean loadKeys() {
    if (Daten.keyStore != null && Daten.keyStore.isKeyStoreReady()) return true;
    Daten.keyStore = new EfaKeyStore(Daten.efaDataDirectory+drvConfig.KEYSTORE_FILE,drvConfig.keyPassword);
    if (!Daten.keyStore.isKeyStoreReady()) {
      Dialog.error("KeyStore kann nicht geladen werden:\n"+Daten.keyStore.getLastError());
      if (drvConfig != null) drvConfig.keyPassword = null;
    }
    return Daten.keyStore.isKeyStoreReady();
  }

  void checkFahrtenheftButton_actionPerformed(ActionEvent e) {
    if (drvConfig.keyPassword == null) KeysAdminFrame.enterKeyPassword(drvConfig);
    if (drvConfig.keyPassword == null) return;
    if (!loadKeys()) return;

    DRVSignaturFrame dlg = new DRVSignaturFrame(this,null);
    dlg.setCloseButtonText("Schlie�en");
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
  }

  void printOverviewButton_actionPerformed(ActionEvent e) {
    String tmpdatei = Daten.efaTmpDirectory+"uebersicht.html";
    try {
      BufferedWriter f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tmpdatei),Daten.ENCODING));
      f.write("<html>\n");
      f.write("<head><META http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\"></head>\n");
      f.write("<body>\n");
      f.write("<h1 align=\"center\">Melde�bersicht</h1>\n");
      f.write("<table align=\"center\" border=\"3\" width=\"100%\">\n");
      f.write("<tr><th>Verein</th><th>Status</th>"+
              (MELDTYP == MELD_WANDERRUDERSTATISTIK ? "<th>Gew�sser</th>" : "<th>Fahrtenhefte</th>")+"</tr>\n");

      for (DatenFelder d = drvConfig.meldungenIndex.getCompleteFirst(); d != null; d = drvConfig.meldungenIndex.getCompleteNext()) {
        String verein = d.get(MeldungenIndex.VEREIN);
        String status = MeldungenIndex.ST_NAMES[EfaUtil.string2int(d.get(MeldungenIndex.STATUS),MeldungenIndex.ST_UNBEKANNT)];
        String feld3 = "";
        if (MELDTYP == MELD_WANDERRUDERSTATISTIK) {
          DatenFelder ms = drvConfig.meldestatistik.getExactComplete(d.get(MeldungenIndex.MITGLNR));
          if (ms != null) feld3 = ms.get(Meldestatistik.WS_GEWAESSER);
        } else {
          int fh = EfaUtil.string2int(d.get(MeldungenIndex.FAHRTENHEFTE),MeldungenIndex.FH_UNBEKANNT);
          switch(fh) {
            case MeldungenIndex.FH_UNBEKANNT:               feld3 = ""; break;
            case MeldungenIndex.FH_KEINE:                   feld3 = "keine"; break;
            case MeldungenIndex.FH_PAPIER:                  feld3 = "Papier"; break;
            case MeldungenIndex.FH_ELEKTRONISCH:            feld3 = "nur elektr."; break;
            case MeldungenIndex.FH_PAPIER_UND_ELEKTRONISCH: feld3 = "elektr. / Papier"; break;
            default:                                        feld3 = "";
          }
        }
        f.write("<tr><td>"+verein+"</td><td>"+status+"</td><td>"+feld3+"</td></tr>\n");
      }
      f.write("</table>\n");
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

  void addButton_actionPerformed(ActionEvent e) {
    String qnr = Long.toString((System.currentTimeMillis() / 1000l) * 100l + 99l);
    if (drvConfig.meldungenIndex.getExactComplete(qnr) != null) { // sollte nie passieren
      Dialog.error("Die Quittungsnummer "+qnr+" existiert bereits.");
      return;
    }
    Logger.log(Logger.INFO,"Neue Meldungen mit Quittungsnummer "+qnr+" wird von Hand erfa�t.");
    DatenFelder d = new DatenFelder(MeldungenIndex._ANZFELDER);
    d.set(MeldungenIndex.QNR,qnr);
    d.set(MeldungenIndex.DATUM,EfaUtil.getCurrentTimeStampYYYY_MM_DD_HH_MM_SS());
    d.set(MeldungenIndex.STATUS,Integer.toString(MeldungenIndex.ST_UNBEARBEITET));
    d.set(MeldungenIndex.FAHRTENHEFTE,Integer.toString(MeldungenIndex.FH_PAPIER));
    drvConfig.meldungenIndex.add(d);

    EfaWett efw = new EfaWett(Daten.efaDataDirectory+drvConfig.aktJahr+Daten.fileSep+qnr+".efw");
    try {
      if (MELDTYP == MELD_FAHRTENABZEICHEN && !drvConfig.readOnlyMode) {
        if (drvConfig.keyPassword == null) KeysAdminFrame.enterKeyPassword(drvConfig);
        if (drvConfig.keyPassword == null) return;
        if (!loadKeys()) return;
      }
      efw.allg_programm = Daten.PROGRAMMID_DRV;
      efw.allg_wett = WETTID;
      efw.allg_wettjahr = Integer.toString(drvConfig.aktJahr);
      efw.kennung = EfaWett.EFAWETT;

      MeldungEditFrame dlg = new MeldungEditFrame(this,drvConfig,efw,qnr,MELDTYP);
      Dialog.setDlgLocation(dlg,this);
      dlg.setModal(true);
      dlg.show();
      if (dlg.hasBeenSaved()) {
        d = drvConfig.meldungenIndex.getExactComplete(qnr);
        if (d != null) { // mu� immer != null sein
          d.set(MeldungenIndex.VEREIN,efw.verein_name);
          d.set(MeldungenIndex.MITGLNR,efw.verein_mitglnr);
          drvConfig.meldungenIndex.delete(qnr);
          drvConfig.meldungenIndex.add(d);
        }
      } else {
        drvConfig.meldungenIndex.delete(qnr);
      }
      drvConfig.meldungenIndex.writeFile();
    } catch(Exception eee) {
      Dialog.error("Fehler beim Schreiben der Meldungsdatei: "+eee.toString());
    }
    showMeldungen();
  }



}