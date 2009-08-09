package de.nmichael.efa.direkt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import de.nmichael.efa.*;
import de.nmichael.efa.Dialog;
import java.beans.*;
import javax.swing.border.*;

public class EfaDirektFrame extends JFrame {
  BootStatus bootStatus = null;
  Vector booteAlle = new Vector();
  EfaFrame efaFrame;
  EfaDirektBackgroundTask efaDirektBackgroundTask = null;
  EfaUhrUpdater efaUhrUpdater = null;
  EfaNewsUpdater efaNewsUpdater = null;
  Vector booteVerfuegbarListData = null;
  Vector booteAufFahrtListData = null;
  Vector booteNichtVerfuegbarListData = null;
  long lastUserInteraction = 0;
  EfaRunning efaRunning = null;
  byte[] largeChunkOfMemory = new byte[1024*1024];

  public static final int EFA_EXIT_REASON_USER = 0;
  public static final int EFA_EXIT_REASON_TIME = 1;
  public static final int EFA_EXIT_REASON_OOME = 2;
  public static final int EFA_EXIT_REASON_AUTORESTART = 3;

  JPanel contentPane;
  BorderLayout borderLayout1 = new BorderLayout();
  JPanel southPanel = new JPanel();
  JPanel centerPanel = new JPanel();
  JLabel statusLabel = new JLabel();
  BorderLayout borderLayout2 = new BorderLayout();
  GridBagLayout gridBagLayout1 = new GridBagLayout();
  JButton fahrtbeginnButton = new JButton();
  JScrollPane jScrollPane1 = new JScrollPane();
  JScrollPane jScrollPane2 = new JScrollPane();
  JList booteVerfuegbar = new JList();
  JList booteAufFahrt = new JList();
  JScrollPane jScrollPane3 = new JScrollPane();
  JButton fahrtendeButton = new JButton();
  JButton nachtragButton = new JButton();
  JButton bootsstatusButton = new JButton();
  JButton adminHinweisButton = new JButton();
  JButton adminButton = new JButton();
  JButton spezialButton = new JButton();
  JLabel verfuegbareBooteLabel = new JLabel();
  JLabel aufFahrtBooteLabel = new JLabel();
  JLabel nichtVerfuegbareBooteLabel = new JLabel();
  JList booteNichtVerfuegbar = new JList();
  JButton efaButton = new JButton();
  JButton fahrtabbruchButton = new JButton();
  JButton hilfeButton = new JButton();
  JButton showFbButton = new JButton();
  JLabel logoLabel = new JLabel();
  JButton statButton = new JButton();
  JPanel westPanel = new JPanel();
  JPanel eastPanel = new JPanel();
  BorderLayout borderLayout3 = new BorderLayout();
  BorderLayout borderLayout4 = new BorderLayout();
  JPanel eastCenterPanel = new JPanel();
  JPanel eastSouthPanel = new JPanel();
  BorderLayout borderLayout5 = new BorderLayout();
  BorderLayout borderLayout6 = new BorderLayout();
  JLabel uhr = new JLabel();
  JPanel sunrisePanel = new JPanel();
  JLabel srSRimage = new JLabel();
  JLabel srSRtext = new JLabel();
  JLabel srSSimage = new JLabel();
  JLabel srSStext = new JLabel();
  JLabel newsLabel = new JLabel();


  //Construct the frame
  public EfaDirektFrame() {
//    System.setProperty("sun.awt.noerasebackground","true"); // removed because of problems with Kubuntu 8.04

    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    Dialog.frameOpened(this);
    try {
      jbInit();
      this.setResizable(false);
    }
    catch(Exception e) {
      e.printStackTrace();
    }

    // Fenster nicht verschiebbar
    if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_fensterNichtVerschiebbar) try {
      this.setUndecorated(true);
      TitledBorder b = new TitledBorder("efa - elektronisches Fahrtenbuch");
      b.setTitleColor(Color.white);
      contentPane.setBackground(new Color(0,0,150));
      contentPane.setBorder(b);
    } catch (NoSuchMethodError e) {
      Logger.log(Logger.WARNING,"Fenstereigenschaft 'nicht verschiebbar' wird erst ab Java 1.4 unterst�tzt.");
    }

    // Fenster immer im Vordergrund
    try {
      if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_immerImVordergrund) {
        if (!de.nmichael.efa.java15.Java15.setAlwaysOnTop(this,true)) {
//          Logger.log(Logger.WARNING,"Fenstereigenschaft 'immer im Vordergrund' wird erst ab Java 1.5 unterst�tzt.");
//          Hier mu� keine Warnung mehr ausgegeben werden, da ab v1.6.0 die Funktionalit�t auch f�r Java < 1.5
//          durch einen Check alle 60 Sekunden nachgebildet wird.
        }
      }
    } catch(UnsupportedClassVersionError e) {
      // Java 1.3 kommt mit der Java 1.5 Klasse nicht klar
      Logger.log(Logger.WARNING,"Fenstereigenschaft 'immer im Vordergrund' wird erst ab Java 1.5 unterst�tzt.");
    } catch(NoClassDefFoundError e) {
      Logger.log(Logger.WARNING,"Fenstereigenschaft 'immer im Vordergrund' wird erst ab Java 1.5 unterst�tzt.");
    }


    appIni();

    packFrame("EfaDirektFrame()");
    this.booteVerfuegbar.requestFocus();

    // Fenster maximiert
    if (Daten.efaConfig.efaDirekt_startMaximized) try {
      this.setSize(Dialog.screenSize);

      Dimension newsize = this.getSize();

      // breite f�r Scrollpanes ist (Fensterbreite - 20) / 2.
      int width = (int)((newsize.getWidth() - this.fahrtbeginnButton.getSize().getWidth() - 20) / 2);
      // die H�he der Scrollpanes ist, da sie CENTER sind, irrelevant; nur f�r jScrollPane3
      // ist die H�he ausschlaggebend.
      jScrollPane1.setPreferredSize(new Dimension(width,500));
      jScrollPane2.setPreferredSize(new Dimension(width,300));
      jScrollPane3.setPreferredSize(new Dimension(width,(int)(newsize.getHeight()/4)));
      int height = (int)(20.0f * (Dialog.getFontSize() < 10 ? 12 : Dialog.getFontSize()) / Dialog.getDefaultFontSize());
      verfuegbareBooteLabel.setPreferredSize(new Dimension(width,height));
      aufFahrtBooteLabel.setPreferredSize(new Dimension(width,height));
      nichtVerfuegbareBooteLabel.setPreferredSize(new Dimension(width,height));
      validate();
    } catch(Exception e) { EfaUtil.foo(); }


    EfaExitFrame.initExitFrame(this);
    // Speicher-�berwachung
    try {
      de.nmichael.efa.java15.Java15.setMemUsageListener(this,Daten.MIN_FREEMEM_COLLECTION_THRESHOLD);
    } catch(UnsupportedClassVersionError e) {
      EfaUtil.foo();
    } catch(NoClassDefFoundError e) {
      EfaUtil.foo();
    }

    if (Daten.efaConfig.efaDirekt_locked) {
      // lock efa NOW
      try {
        new Thread() {
          public void run() {
            try {
              Thread.sleep(1000);
            } catch(Exception e) {
            }
            lockEfa();
          }
        }.start();
      } catch(Exception ee) {
      }
    } else {
      // lock efa later
      if (Daten.efaConfig.efaDirekt_lockEfaFromDatum != null) {
        lockEfaAt(Daten.efaConfig.efaDirekt_lockEfaFromDatum,Daten.efaConfig.efaDirekt_lockEfaFromZeit);
      }
    }

    this.efaDirektBackgroundTask.interrupt(); // damit Frame nochmal gepackt werden kann (Bugfix)
  }


  public void packFrame(String source) {
//    System.out.println(source);
    this.pack();
  }


  // ActionHandler Events
  public void keyAction(ActionEvent evt) {
    if (evt == null || evt.getActionCommand() == null) return;

    alive();

//    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_0")) { // Escape
      // nothing
//    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_1")) { // F1
      Help.getHelp(this,this.getClass());
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_2")) { // F2
      this.fahrtbeginnButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_3")) { // F3
      this.fahrtendeButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_4")) { // F4
      this.fahrtabbruchButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_5")) { // F5
      this.nachtragButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_6")) { // F6
      this.bootsstatusButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_7")) { // F7
      this.showFbButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_8")) { // F8
      this.statButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_9")) { // F9
      this.adminHinweisButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_10")) { // alt-F10
      this.adminButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_11")) { // alt-F11
      this.spezialButton_actionPerformed(null);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_12")) { // F10
      this.booteVerfuegbar.requestFocus();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_13")) { // F11
      this.booteAufFahrt.requestFocus();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_14")) { // F12
      this.booteNichtVerfuegbar.requestFocus();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_15")) { // Shift-F1
      EfaUtil.gc();
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_16")) { // Alt-X
      cancel(null,EFA_EXIT_REASON_USER,false);
    }
    if (evt.getActionCommand().equals("KEYSTROKE_ACTION_17")) { // Shift-F4
      cancel(null,EFA_EXIT_REASON_USER,false);
    }
  }


  //Component initialization
  private void jbInit() throws Exception  {
    ActionHandler ah= new ActionHandler(this);
    try {
      ah.addKeyActions(getRootPane(), JComponent.WHEN_IN_FOCUSED_WINDOW,
                       new String[] {"ESCAPE","F1","F2","F3","F4","F5","F6","F7","F8","F9","alt F10","alt F11","F10","F11","F12","shift F1","alt X","shift F4"},
                       new String[] {"keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction","keyAction"});
    } catch(NoSuchMethodException e) {
      System.err.println("Error setting up ActionHandler");
    }

    this.addMouseListener(new MouseListener() {
      public void mouseClicked(MouseEvent e) { alive(); }
      public void mouseExited(MouseEvent e) { alive(); }
      public void mouseEntered(MouseEvent e) { alive(); }
      public void mouseReleased(MouseEvent e) { alive(); }
      public void mousePressed(MouseEvent e) { alive(); }
    });

    setIconImage(Toolkit.getDefaultToolkit().createImage(EfaDirektFrame.class.getResource("/de/nmichael/efa/img/efa_icon.gif")));
    contentPane = (JPanel) this.getContentPane();
    contentPane.setLayout(borderLayout1);
    this.setSize(new Dimension(708, 489));
    this.setTitle("efa - elektronisches Fahrtenbuch");
    this.addWindowListener(new java.awt.event.WindowAdapter() {
      public void windowActivated(WindowEvent e) {
        this_windowActivated(e);
      }
    });
    statusLabelSetText("Status");
    southPanel.setLayout(borderLayout2);
    centerPanel.setLayout(gridBagLayout1);
    fahrtbeginnButton.setBackground(new Color(204, 255, 204));
    fahrtbeginnButton.setNextFocusableComponent(fahrtendeButton);
    fahrtbeginnButton.setActionCommand("Fahrt beginnen >>>");
    fahrtbeginnButton.setMnemonic('B');
    fahrtbeginnButton.setText("Fahrt beginnen >>>");
    fahrtbeginnButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fahrtbeginnButton_actionPerformed(e);
      }
    });
    fahrtendeButton.setBackground(new Color(204, 255, 204));
    fahrtendeButton.setNextFocusableComponent(fahrtabbruchButton);
    fahrtendeButton.setMnemonic('E');
    fahrtendeButton.setText("<<< Fahrt beenden");
    fahrtendeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fahrtendeButton_actionPerformed(e);
      }
    });
    nachtragButton.setBackground(new Color(204, 255, 255));
    nachtragButton.setNextFocusableComponent(bootsstatusButton);
    nachtragButton.setMnemonic('N');
    nachtragButton.setText("Nachtrag");
    nachtragButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        nachtragButton_actionPerformed(e);
      }
    });
    bootsstatusButton.setBackground(new Color(255, 255, 204));
    bootsstatusButton.setNextFocusableComponent(showFbButton);
    bootsstatusButton.setMnemonic('O');
    bootsstatusButton.setText("Bootsreservierungen");
    bootsstatusButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        bootsstatusButton_actionPerformed(e);
      }
    });
    adminHinweisButton.setBackground(new Color(255, 241, 151));
    adminHinweisButton.setNextFocusableComponent(adminButton);
    adminHinweisButton.setMnemonic('H');
    adminHinweisButton.setText("Nachricht an Admin");
    adminHinweisButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        adminHinweisButton_actionPerformed(e);
      }
    });
    adminButton.setNextFocusableComponent(booteAufFahrt);
    adminButton.setMnemonic('M');
    adminButton.setText("Admin-Modus");
    adminButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        adminButton_actionPerformed(e);
      }
    });
    spezialButton.setNextFocusableComponent(booteAufFahrt);
    spezialButton.setText("Spezial-Button");
    spezialButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        spezialButton_actionPerformed(e);
      }
    });
    Dialog.setPreferredSize(verfuegbareBooteLabel,260,20);
    Dialog.setPreferredSize(aufFahrtBooteLabel,260,20);
    Dialog.setPreferredSize(nichtVerfuegbareBooteLabel,220,20);
    verfuegbareBooteLabel.setDisplayedMnemonic('V');
    verfuegbareBooteLabel.setHorizontalAlignment(SwingConstants.CENTER);
    verfuegbareBooteLabel.setLabelFor(booteVerfuegbar);
    verfuegbareBooteLabel.setText("verf�gbare Boote");
    aufFahrtBooteLabel.setDisplayedMnemonic('F');
    aufFahrtBooteLabel.setHorizontalAlignment(SwingConstants.CENTER);
    aufFahrtBooteLabel.setLabelFor(booteAufFahrt);
    aufFahrtBooteLabel.setText("Boote auf Fahrt");
    nichtVerfuegbareBooteLabel.setDisplayedMnemonic('I');
    nichtVerfuegbareBooteLabel.setHorizontalAlignment(SwingConstants.CENTER);
    nichtVerfuegbareBooteLabel.setLabelFor(booteNichtVerfuegbar);
    nichtVerfuegbareBooteLabel.setText("nicht verf�gbare Boote");
    southPanel.setBorder(BorderFactory.createRaisedBevelBorder());
    booteVerfuegbar.setNextFocusableComponent(fahrtbeginnButton);
    booteVerfuegbar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    // KeyListeners entfernen, damit unter Java 1.4.x nicht automatisch gescrollt wird, sondern durch den eigenen Algorithmus
    try {
     KeyListener[] kl = booteVerfuegbar.getKeyListeners();
     for (int i=0; i<kl.length; i++) booteVerfuegbar.removeKeyListener(kl[i]);
    } catch(NoSuchMethodError e) { /* Java 1.3 kennt diese Methode nicht */ }
    booteVerfuegbar.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        booteVerfuegbar_keyReleased(e);
      }
    });
    booteVerfuegbar.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        booteVerfuegbar_mouseClicked(e);
      }
    });
    booteAufFahrt.setNextFocusableComponent(booteNichtVerfuegbar);
    booteAufFahrt.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // KeyListeners entfernen, damit unter Java 1.4.x nicht automatisch gescrollt wird, sondern durch den eigenen Algorithmus
    try {
      KeyListener[] kl = booteAufFahrt.getKeyListeners();
      for (int i=0; i<kl.length; i++) booteAufFahrt.removeKeyListener(kl[i]);
    } catch(NoSuchMethodError e) { /* Java 1.3 kennt diese Methode nicht */ }
    booteAufFahrt.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        booteAufFahrt_keyReleased(e);
      }
    });
    booteAufFahrt.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        booteAufFahrt_mouseClicked(e);
      }
    });
    booteNichtVerfuegbar.setNextFocusableComponent(booteVerfuegbar);
    booteNichtVerfuegbar.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    // KeyListeners entfernen, damit unter Java 1.4.x nicht automatisch gescrollt wird, sondern durch den eigenen Algorithmus
    try {
      KeyListener[] kl = booteNichtVerfuegbar.getKeyListeners();
      for (int i=0; i<kl.length; i++) booteNichtVerfuegbar.removeKeyListener(kl[i]);
    } catch(NoSuchMethodError e) { /* Java 1.3 kennt diese Methode nicht */ }
    booteNichtVerfuegbar.addKeyListener(new java.awt.event.KeyAdapter() {
      public void keyReleased(KeyEvent e) {
        booteNichtVerfuegbar_keyReleased(e);
      }
    });
    booteNichtVerfuegbar.addMouseListener(new java.awt.event.MouseAdapter() {
      public void mouseClicked(MouseEvent e) {
        booteNichtVerfuegbar_mouseClicked(e);
      }
    });
    efaButton.setPreferredSize(new Dimension(90, 55));
    efaButton.setIcon(new ImageIcon(EfaFrame.class.getResource(Daten.getEfaImage(1))));
    efaButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        efaButton_actionPerformed(e);
      }
    });
    fahrtabbruchButton.setBackground(new Color(255, 204, 204));
    fahrtabbruchButton.setNextFocusableComponent(nachtragButton);
    fahrtabbruchButton.setMnemonic('A');
    fahrtabbruchButton.setText("Fahrt abbrechen");
    fahrtabbruchButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        fahrtabbruchButton_actionPerformed(e);
      }
    });
    hilfeButton.setMnemonic('0');
    hilfeButton.setText("Hilfe mit [F1]");
    hilfeButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        hilfeButton_actionPerformed(e);
      }
    });
    showFbButton.setBackground(new Color(204, 204, 255));
    showFbButton.setNextFocusableComponent(statButton);
    showFbButton.setMnemonic('Z');
    showFbButton.setText("Fahrtenbuch anzeigen");
    showFbButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showFbButton_actionPerformed(e);
      }
    });
    statButton.setBackground(new Color(204, 204, 255));
    statButton.setNextFocusableComponent(adminHinweisButton);
    statButton.setMnemonic('S');
    statButton.setText("Statistik erstellen");
    statButton.addActionListener(new java.awt.event.ActionListener() {
      public void actionPerformed(ActionEvent e) {
        statButton_actionPerformed(e);
      }
    });
    eastPanel.setLayout(borderLayout4);
    westPanel.setLayout(borderLayout3);
    eastSouthPanel.setLayout(borderLayout5);
    eastCenterPanel.setLayout(borderLayout6);
    uhr.setText("12:34");
    contentPane.add(southPanel, BorderLayout.SOUTH);
    contentPane.add(centerPanel, BorderLayout.CENTER);
    southPanel.add(statusLabel, BorderLayout.CENTER);
    int fahrtbeginnTop = (int)(20.0f * (Dialog.getFontSize() < 10 ? 12 : Dialog.getFontSize()) / Dialog.getDefaultFontSize());
    centerPanel.add(fahrtbeginnButton,            new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(fahrtbeginnTop, 0, 0, 0), 0, 0));
//    centerPanel.add(jScrollPane1,               new GridBagConstraints(0, 1, 1, 13, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane1.getViewport().add(booteVerfuegbar, null);
//    centerPanel.add(jScrollPane2,            new GridBagConstraints(2, 1, 1, 9, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
//    centerPanel.add(jScrollPane3,            new GridBagConstraints(2, 11, 1, 3, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    jScrollPane3.getViewport().add(booteNichtVerfuegbar, null);
    centerPanel.add(fahrtendeButton,           new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    centerPanel.add(nachtragButton,             new GridBagConstraints(1, 5, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    centerPanel.add(bootsstatusButton,            new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    centerPanel.add(adminHinweisButton,              new GridBagConstraints(1, 9, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    centerPanel.add(adminButton,                 new GridBagConstraints(1, 11, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(30, 0, 30, 0), 0, 0));
    centerPanel.add(spezialButton,                 new GridBagConstraints(1, 12, 1, 1, 0.0, 0.0
            ,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 20, 0), 0, 0));
//    centerPanel.add(verfuegbareBooteLabel,            new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
//    centerPanel.add(aufFahrtBooteLabel,           new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0, 0, 2, 0), 0, 0));
    jScrollPane2.getViewport().add(booteAufFahrt, null);
//    centerPanel.add(nichtVerfuegbareBooteLabel,            new GridBagConstraints(2, 10, 1, 1, 0.0, 0.0
//            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(3, 0, 2, 0), 0, 0));
    centerPanel.add(efaButton,            new GridBagConstraints(1, 14, 1, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0, 0, 5, 0), 0, 0));
    centerPanel.add(fahrtabbruchButton,          new GridBagConstraints(1, 4, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));
    centerPanel.add(hilfeButton,           new GridBagConstraints(1, 13, 1, 1, 0.0, 0.0
            ,GridBagConstraints.SOUTH, GridBagConstraints.NONE, new Insets(0, 0, 20, 0), 0, 0));
    centerPanel.add(showFbButton,       new GridBagConstraints(1, 7, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(10, 0, 0, 0), 0, 0));

    // Logo-Label
    int logoTop = (int)(20.0f * (Dialog.getFontSize() < 10 ? 12 : Dialog.getFontSize()) / Dialog.getDefaultFontSize());
    int logoBottom = 10;
    if (Daten.efaConfig.efaDirekt_startMaximized && Daten.efaConfig.efaDirekt_vereinsLogo != null) {
      logoBottom += (int)((Dialog.screenSize.getHeight()-800)/5);
    }
    centerPanel.add(logoLabel,           new GridBagConstraints(1, 0, 1, 2, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(logoTop, 0, logoBottom, 0), 0, 0));

    centerPanel.add(statButton,   new GridBagConstraints(1, 8, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));
    centerPanel.add(uhr,  new GridBagConstraints(1, 15, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(10, 10, 10, 10), 0, 0));

    srSRimage.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/sunrise.gif")));
    srSSimage.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/sunset.gif")));
    srSRtext.setText("00:00");
    srSStext.setText("23:59");
    sunrisePanel.add(srSRimage);
    sunrisePanel.add(srSRtext);
    sunrisePanel.add(srSSimage);
    sunrisePanel.add(srSStext);
    centerPanel.add(sunrisePanel,  new GridBagConstraints(1, 16, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(5, 10, 10, 10), 0, 0));
    newsLabel.setText("+++ News +++");
    newsLabel.setForeground(Color.white);
    newsLabel.setBackground(Color.red);
    newsLabel.setOpaque(true);
    newsLabel.setHorizontalAlignment(SwingConstants.CENTER);
    newsLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    centerPanel.add(newsLabel,  new GridBagConstraints(1, 17, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL, new Insets(5, 0, 0, 0), 0, 0));



    westPanel.add(verfuegbareBooteLabel, BorderLayout.NORTH);
    westPanel.add(jScrollPane1, BorderLayout.CENTER);
    eastCenterPanel.add(aufFahrtBooteLabel, BorderLayout.NORTH);
    eastCenterPanel.add(jScrollPane2, BorderLayout.CENTER);
    eastSouthPanel.add(nichtVerfuegbareBooteLabel, BorderLayout.NORTH);
    eastSouthPanel.add(jScrollPane3, BorderLayout.CENTER);
    contentPane.add(westPanel,  BorderLayout.WEST);
    contentPane.add(eastPanel,  BorderLayout.EAST);
    eastPanel.add(eastCenterPanel, BorderLayout.CENTER);
    eastPanel.add(eastSouthPanel,  BorderLayout.SOUTH);
    booteVerfuegbar.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        bootslist_focusGained(e);
      }
    });
    booteAufFahrt.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        bootslist_focusGained(e);
      }
    });
    booteNichtVerfuegbar.addFocusListener(new java.awt.event.FocusAdapter() {
      public void focusGained(FocusEvent e) {
        bootslist_focusGained(e);
      }
    });
  }

  //Overridden so we can exit when window is closed
  protected void processWindowEvent(WindowEvent e) {
    if (e.getID() == WindowEvent.WINDOW_CLOSING) {
      // Fenster schlie�en
      cancel(e,EFA_EXIT_REASON_USER,false);
    } else if (e.getID() == WindowEvent.WINDOW_DEACTIVATED) {
      // Fenster deaktivieren
      super.processWindowEvent(e);
    } else if (e.getID() == WindowEvent.WINDOW_ICONIFIED) {
      // Fenster minimiert
      super.processWindowEvent(e);
      this.setState(Frame.NORMAL);
    } else super.processWindowEvent(e);
  }

  void cancel(WindowEvent e, int reason, boolean restart) {
    int exitCode = 0;
    if (efaRunning != null) efaRunning.closeServer();
    Daten.efaConfig.writeFile();
    String wer = "unknown";

    switch(reason) {
     case EFA_EXIT_REASON_USER: // manuelles Beenden von efa
       boolean durchMitglied;
       if (!Daten.efaConfig.efaDirekt_mitgliederDuerfenEfaBeenden) {
         Admin admin = null;
         do {
           admin = AdminLoginFrame.login(this,"Beenden von efa");
           if (admin != null && !admin.allowedEfaBeenden)
             Dialog.error("Du hast nicht die Berechtigung, um efa zu beenden!");
         } while (admin != null && !admin.allowedEfaBeenden);
         if (admin == null) return;
         wer = "Admin="+admin.name;
         durchMitglied = false;
       } else {
         wer = "Nutzer";
         durchMitglied = true;
       }
       if (Daten.efaConfig.efaDirekt_execOnEfaExit.length()>0 && durchMitglied) {
         Logger.log(Logger.INFO,"Programmende veranla�t; versuche, Kommando '"+Daten.efaConfig.efaDirekt_execOnEfaExit+"' auszuf�hren...");
         try {
           Runtime.getRuntime().exec(Daten.efaConfig.efaDirekt_execOnEfaExit);
         } catch(Exception ee) {
           Logger.log(Logger.ERROR,"Kann Kommando '"+Daten.efaConfig.efaDirekt_execOnEfaExit+"' nicht ausf�hren.");
         }
       }
       break;
     case EFA_EXIT_REASON_TIME:
       wer = "Zeitsteuerung";
       if (Daten.efaConfig.efaDirekt_execOnEfaAutoExit.length()>0) {
         Logger.log(Logger.INFO,"Programmende veranla�t; versuche, Kommando '"+Daten.efaConfig.efaDirekt_execOnEfaAutoExit+"' auszuf�hren...");
         try {
           Runtime.getRuntime().exec(Daten.efaConfig.efaDirekt_execOnEfaAutoExit);
         } catch(Exception ee) {
           Logger.log(Logger.ERROR,"Kann Kommando '"+Daten.efaConfig.efaDirekt_execOnEfaAutoExit+"' nicht ausf�hren.");
         }
       }
       break;
     case EFA_EXIT_REASON_OOME:
       wer = "Speicher�berwachung";
       break;
     case EFA_EXIT_REASON_AUTORESTART:
       wer = "Automatischer Neustart";
       break;
    }

    if (restart) {
      if (Daten.javaRestart) {
        exitCode = 199;
        String restartcmd = System.getProperty("java.home") + Daten.fileSep +
            "bin" + Daten.fileSep + "java " +
            (Daten.efa_java_arguments != null ? Daten.efa_java_arguments :
             "-cp " + System.getProperty("java.class.path") +
             " " + Daten.EFADIREKT_MAINCLASS + EfaDirekt.STARTARGS);
        Logger.log(Logger.INFO, "Neustart mit Kommando: " + restartcmd);
        try {
          Runtime.getRuntime().exec(restartcmd);
        }
        catch (Exception ee) {
          Logger.log(Logger.ERROR,
                     "Kann Kommando '" + restartcmd + "' nicht ausf�hren.");
        }
      } else {
        exitCode = 99;
      }
    }

    if (e != null) super.processWindowEvent(e);
    Logger.log(Logger.INFO,"PROGRAMMENDE durch "+wer);
    System.exit(exitCode);
  }

  public synchronized void exitOnLowMemory(String detector, boolean immediate) {
    largeChunkOfMemory = null;
    Logger.log(Logger.ERROR,"Der Arbeitsspeicher wird knapp ["+detector+"]: efa versucht " + (immediate ? "JETZT" : "jetzt") + " einen Neustart ...");
    if (immediate) {
      this.cancel(null,EFA_EXIT_REASON_OOME,true);
    } else {
      EfaExitFrame.exitEfa("Neustart wegen knappen Arbeitsspeichers", true, EFA_EXIT_REASON_OOME);
    }
  }


  void this_windowActivated(WindowEvent e) {
    try {
      if (!isEnabled() && efaFrame != null) {
        efaFrame.toFront();
      }
    } catch(Exception ee) {}
  }

  public static void haltProgram(String s) {
    if (s != null) {
      Dialog.error(s);
      Logger.log(Logger.ERROR,EfaUtil.replace(s,"\n"," ",true));
    }
    Logger.log(Logger.INFO,"PROGRAMMENDE (wegen Fehler)");
    System.exit(1);
  }

  void appIni() {
    // Doppelstarts verhindern?
    if (Daten.efaConfig.efaDirekt_checkRunning) {
      efaRunning = new EfaRunning();
      if (efaRunning.isRunning()) {
        haltProgram("efa l�uft bereits und kann nicht zeitgleich zweimal gestartet werden!");
      }
      efaRunning.run();
    }


    // Backup
    Daten.backup = new Backup(Daten.efaBakDirectory,Daten.efaConfig.bakSave,Daten.efaConfig.bakMonat,Daten.efaConfig.bakTag,Daten.efaConfig.bakKonv);

    // Nachrichten an Admin einlesen
    Daten.nachrichten = new NachrichtenAnAdmin(Daten.efaDataDirectory+Daten.DIREKTNACHRICHTEN);
    if (!EfaUtil.canOpenFile(Daten.nachrichten.getFileName())) {
      if (!Daten.nachrichten.writeFile()) haltProgram("Kann Nachrichten-Datei "+Daten.nachrichten.getFileName()+" nicht erstellen!");
    } else {
      if (!Daten.nachrichten.readFile()) haltProgram("Kann Nachrichten-Datei "+Daten.nachrichten.getFileName()+" nicht �ffnen!");
    }
    Logger.setNachrichtenAnAdmin(Daten.nachrichten);
    updateUnreadMessages();

    // Security File
    EfaSec efaSec = new EfaSec(Daten.efaProgramDirectory+Daten.EFA_SECFILE);

    // Admin-Pa�wort vorhanden?
    boolean neuerSuperAdmin = false;
    if (Daten.efaConfig.admins.get(EfaConfig.SUPERADMIN) == null) {
      Logger.log(Logger.INFO,"Kein Super-Admin gefunden.");
      try {
        // gibt es noch das Sicherheitsfile?
        if (!efaSec.secFileExists()) {
          String s = "efa konnte kein Super-Admin Pa�wort finden!\n"+
                     "Aus Gr�nden der Sicherheit verweigert efa den Dienst.\n"+
                     "Bitte installiere efa neu oder kontaktiere den Entwickler: "+Daten.EFAEMAIL;
          haltProgram(s);
        }
        // stimmen die Sicherheits-Werte �berein?
        if (!efaSec.secValueValid()) {
          String s = "efa konnte kein Super-Admin Pa�wort finden, und die Sicherheitsdatei ist korrupt.!\n"+
                     "Aus Gr�nden der Sicherheit verweigert efa den Dienst.\n"+
                     "Bitte installiere efa neu oder kontaktiere den Entwickler: "+Daten.EFAEMAIL;
          haltProgram(s);
        }
      } catch(Exception e) {
        String s = "efa konnte kein Super-Admin Pa�wort finden, und bei den folgenden Tests trat ein Fehler auf: "+e.toString()+"\n"+
                   "Aus Gr�nden der Sicherheit verweigert efa den Dienst.\n"+
                   "Bitte installiere efa neu oder kontaktiere den Entwickler: "+Daten.EFAEMAIL;
        haltProgram(s);
      }
      String pwd = "";
      Dialog.infoDialog("Willkommen bei der Bootshaus-Version von efa",
                        "Mitglieder d�rfen in efa nur Fahrten eintragen. Alle weiteren\n"+
                        "Aktionen d�rfen nur von einem Administrator ausgef�hrt werden.\n"+
                        "Der Super-Administrator (Haupt-Administrator) hat uneingeschr�nkte\n"+
                        "Rechte, die anderen Administratoren k�nnen eingeschr�nkte Rechte\n"+
                        "besitzen.\n"+
                        "Der Name des Super-Administrators lautet 'admin'. Zur Zeit gibt es\n"+
                        "noch kein Pa�wort f�r 'admin'. Du wirst nun gleich aufgefordert, ein\n"+
                        "neues Pa�wort f�r den Super-Administrator 'admin' einzugeben.");

      pwd = NewPasswordFrame.getNewPassword(this,EfaConfig.SUPERADMIN);
      if (pwd == null) {
        haltProgram("Pa�worteingabe f�r Super-Admin abgebrochen.");
      }

      Admin root = new Admin(EfaConfig.SUPERADMIN,EfaUtil.getSHA(pwd));
      Daten.efaConfig.admins.put(EfaConfig.SUPERADMIN,root);
      if (!Daten.efaConfig.writeFile()) haltProgram(Daten.efaConfig.getFileName()+" konnte nicht geschrieben werden!");
      Logger.log(Logger.INFO,"Neuer Super-Admin erstellt.");
      neuerSuperAdmin = true;
    }


    if (efaSec.secFileExists() && !efaSec.isDontDeleteSet()) {
      switch (Dialog.auswahlDialog("Sicherheits-Frage","Aus Gr�nden der Sicherheit sollte es im Bootshaus nicht m�glich sein,\n"+
                                   "das herk�mmliche efa ohne Pa�wort zu starten, da dort jeder Benutzer auch ohne\n"+
                                   "Admin-Rechte alle Daten manipulieren kann. Es wird daher dringend\n"+
                                   "geraten, das herk�mmliche efa ebenfalls durch das Admin-Pa�wort zu sichern!\n"+
                                   "F�r den Einsatz zu Hause ist es nat�rlich nicht erforderlich, efa\n"+
                                   "zu sperren, da hier i.d.R. keine Mi�brauchgefahr besteht.\n\n"+
                                   "M�chtest Du, da� die herk�mmliche efa-Oberfl�che pa�wortgesch�tzt wird und nur\n"+
                                   "noch f�r Admins zug�nglich ist (f�r Bootshaus-Einsatz dringend empfohlen!)?\n"+
                                   "Herk�mmliche efa-Oberfl�che ...",
                                   "... durch Pa�wort sch�tzen","... nicht sch�tzen")) {
        case 0: // Sperren
          if (efaSec.delete(true)) Dialog.meldung("Der Start des herk�mmlichen efa ist nun nur noch mit Pa�wort m�glich!");
          else haltProgram("efa konnte die Datei "+efaSec.getFilename()+" nicht l�schen und wird daher beendet!");
          // Standardwerte f�r Backups �ndern
          Daten.efaConfig.bakSave = false;
          Daten.efaConfig.bakKonv = true;
          Daten.efaConfig.bakMonat = false;
          Daten.efaConfig.bakTag = true;
          Daten.efaConfig.writeFile();
          Daten.backup = new Backup(Daten.efaBakDirectory,Daten.efaConfig.bakSave,Daten.efaConfig.bakMonat,Daten.efaConfig.bakTag,Daten.efaConfig.bakKonv);
          break;
        case 1:
          if (efaSec.writeSecFile(efaSec.getSecValue(),true)) Dialog.meldung("Der Start des herk�mmlichen efa ist weiterhin m�glich!");
          else haltProgram("efa konnte die Datei "+efaSec.getFilename()+" nicht schreiben und wird daher beendet!");
          break;
        default:
          haltProgram(null);
      }
    }

    // efaSec l�schen (au�er, wenn DontDelete-Flag gesetzt ist)
    if (efaSec.secFileExists() && !efaSec.delete(false)) {
      String s = "efa konnte die Datei "+efaSec.getFilename()+" nicht l�schen und wird daher beendet!";
      haltProgram(s);
    }

    // Bezeichnungen einlesen
    Daten.bezeichnungen = new Bezeichnungen(Daten.efaCfgDirectory+Daten.BEZEICHFILE);
    if (!EfaUtil.canOpenFile(Daten.bezeichnungen.getFileName())) {
      if (Daten.bezeichnungen.createNewIfDoesntExist()) {
        Logger.log(Logger.WARNING,"Liste der Bezeichnungen "+Daten.bezeichnungen.getFileName()+" mit Standard-Werten neu erzeugt.");
      } else {
        haltProgram("Liste der Bezeichnungen "+Daten.bezeichnungen.getFileName()+" wurde nicht gefunden und konnte nicht neu erzeugt werden!");
      }
    }
    if (!Daten.bezeichnungen.readFile()) {
      haltProgram("Liste der Bezeichnungen "+Daten.bezeichnungen.getFileName()+" kann nicht gelesen werden!");
    }

    // Fahrtenbuch �ffnen, falls keines angegeben
    if (Daten.efaConfig.direkt_letzteDatei == null || Daten.efaConfig.direkt_letzteDatei.length()==0) {

      if (neuerSuperAdmin) Dialog.infoDialog("Fahrtenbuch ausw�hlen",
                                             "Bisher wurde noch kein Fahrtenbuch ausgew�hlt, mit dem\n"+
                                             "gearbeitet werden soll. Im folgenden Schritt wirst Du\n"+
                                             "aufgefordert, ein Fahrtenbuch auszuw�hlen.\n"+
                                             "Du mu�t Dich daher zun�chst als Administrator anmelden:\n"+
                                             "Der Name des Super-Administrators lautet 'admin', das\n"+
                                             "Pa�wort hast Du eben selbst gew�hlt.");
      Admin admin = null;
      do {
        admin = AdminLoginFrame.login(this,"Kein Fahrtenbuch ausgew�hlt");
        if (admin == null) haltProgram("Programmende, da kein Fahrtenbuch ausgew�hlt und Admin-Login nicht erfolgreich.");
        if (!admin.allowedFahrtenbuchAuswaehlen) Dialog.error("Du hast als Admin '"+admin.name+"' keine Berechtigung, ein Fahrtenbuch auszuw�hlen!");
      } while (!admin.allowedFahrtenbuchAuswaehlen);
      String dat = null;
      switch (Dialog.auswahlDialog("Fahrtenbuch ausw�hlen","M�chtest Du ein neues Fahrtenbuch erstellen, oder ein vorhandenes �ffnen?","Neues Fahrtenbuch erstellen","Vorhandenes Fahrtenbuch �ffnen")) {
        case 0: // Neues Fahrtenbuch erstellen
          FahrtenbuchNeuFortsetzenFrame dlg = new FahrtenbuchNeuFortsetzenFrame(this,false);
          Dialog.setDlgLocation(dlg,this);
          dlg.setModal(!Dialog.tourRunning);
          dlg.show();
          if (Daten.fahrtenbuch != null) {
            dat = Daten.fahrtenbuch.getFileName();
          }
          break;
        case 1: // Vorhandenes Fahrtenbuch �ffnen
          dat = Dialog.dateiDialog(this,"Fahrtenbuch �ffnen","efa Fahrtenbuch (*.efb)","efb",Daten.efaDataDirectory,false);
          break;
        default:
          haltProgram("Kein Fahrtenbuch ausgew�hlt");
      }
      if (dat == null || dat.length()==0) haltProgram("Kein Fahrtenbuch ausgew�hlt");
      Daten.efaConfig.direkt_letzteDatei = dat;
      Logger.log(Logger.INFO,"Neue Fahrtenbuchdatei "+dat+" ausgew�hlt.");
      if (!Daten.efaConfig.writeFile()) haltProgram(Daten.efaConfig.getFileName()+" konnte nicht geschrieben werden!");
    }

    readFahrtenbuch();

    // WettDefs.cfg
    Daten.wettDefs = new WettDefs(Daten.efaCfgDirectory+Daten.WETTDEFS);
    Daten.wettDefs.createNewIfDoesntExist();
    Daten.wettDefs.readFile();

    // Standardmannschaften einlesen
    Daten.mannschaften = new Mannschaften(Daten.efaDataDirectory+Daten.MANNSCHAFTENFILE);
    if (!EfaUtil.canOpenFile(Daten.mannschaften.getFileName())) {
      if (!Daten.mannschaften.writeFile()) {
        haltProgram(Daten.mannschaften.getFileName()+" kann nicht erstellt werden!");
      }
      Logger.log(Logger.WARNING,"Standardmannschaften-Datei neu erstellt.");
    };
    if (!Daten.mannschaften.readFile()) {
      Logger.log(Logger.WARNING,"Liste der Standardmannschaften "+Daten.mannschaften.getFileName()+" kann nicht gelesen werden!");
    }
    // Synonymdateien einlesen
    Daten.synMitglieder = new Synonyme(Daten.efaDataDirectory+Daten.MITGLIEDER_SYNONYM);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.MITGLIEDER_SYNONYM)) {
      if (Daten.synMitglieder.writeFile())
        Logger.log(Logger.WARNING,"Mitglieder-Synonymdatei '"+Daten.efaDataDirectory+Daten.MITGLIEDER_SYNONYM+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Mitglieder-Synonymdatei '"+Daten.efaDataDirectory+Daten.MITGLIEDER_SYNONYM+"' konnte nicht erstellt werden.");
    }
    if (!Daten.synMitglieder.readFile()) {
      Logger.log(Logger.ERROR,"Mitglieder-Synonymdatei '"+Daten.efaDataDirectory+Daten.MITGLIEDER_SYNONYM+"' konnte nicht gelesen werden.");
    }
    Daten.synBoote = new Synonyme(Daten.efaDataDirectory+Daten.BOOTE_SYNONYM);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.BOOTE_SYNONYM)) {
      if (Daten.synBoote.writeFile())
        Logger.log(Logger.WARNING,"Boots-Synonymdatei '"+Daten.efaDataDirectory+Daten.BOOTE_SYNONYM+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Boots-Synonymdatei '"+Daten.efaDataDirectory+Daten.BOOTE_SYNONYM+"' konnte nicht erstellt werden.");
    }
    if (!Daten.synBoote.readFile()) {
      Logger.log(Logger.ERROR,"Boots-Synonymdatei '"+Daten.efaDataDirectory+Daten.BOOTE_SYNONYM+"' konnte nicht gelesen werden.");
    }
    Daten.synZiele = new Synonyme(Daten.efaDataDirectory+Daten.ZIELE_SYNONYM);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.ZIELE_SYNONYM)) {
      if (Daten.synZiele.writeFile())
        Logger.log(Logger.WARNING,"Ziel-Synonymdatei '"+Daten.efaDataDirectory+Daten.ZIELE_SYNONYM+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Ziel-Synonymdatei '"+Daten.efaDataDirectory+Daten.ZIELE_SYNONYM+"' konnte nicht erstellt werden.");
    }
    if (!Daten.synZiele.readFile()) {
      Logger.log(Logger.ERROR,"Ziel-Synonymdatei '"+Daten.efaDataDirectory+Daten.ZIELE_SYNONYM+"' konnte nicht gelesen werden.");
    }
    Daten.adressen = new Adressen(Daten.efaDataDirectory+Daten.ADRESSENFILE);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.ADRESSENFILE)) {
      if (Daten.adressen.writeFile())
        Logger.log(Logger.WARNING,"Adressendatei '"+Daten.efaDataDirectory+Daten.ADRESSENFILE+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Adressendatei '"+Daten.efaDataDirectory+Daten.ADRESSENFILE+"' konnte nicht erstellt werden.");
    }
    if (!Daten.adressen.readFile()) {
      Logger.log(Logger.ERROR,"Adressendatei '"+Daten.efaDataDirectory+Daten.ADRESSENFILE+"' konnte nicht gelesen werden.");
    }
    Daten.vereinsConfig = new VereinsConfig(Daten.efaDataDirectory+Daten.VEREINSCONFIG);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.VEREINSCONFIG)) {
      if (Daten.vereinsConfig.writeFile())
        Logger.log(Logger.WARNING,"Vereinskonfiguration '"+Daten.efaDataDirectory+Daten.VEREINSCONFIG+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Vereinskonfiguration '"+Daten.efaDataDirectory+Daten.VEREINSCONFIG+"' konnte nicht erstellt werden.");
    }
    if (!Daten.vereinsConfig.readFile()) {
      Logger.log(Logger.ERROR,"Vereinskonfiguration '"+Daten.efaDataDirectory+Daten.VEREINSCONFIG+"' konnte nicht gelesen werden.");
    }
    Daten.fahrtenabzeichen = new Fahrtenabzeichen(Daten.efaDataDirectory+Daten.FAHRTENABZEICHEN);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.FAHRTENABZEICHEN)) {
      if (Daten.fahrtenabzeichen.writeFile())
        Logger.log(Logger.WARNING,"DRV-Fahrtenabezeichendatei '"+Daten.efaDataDirectory+Daten.FAHRTENABZEICHEN+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"DRV-Fahrtenabezeichendatei '"+Daten.efaDataDirectory+Daten.FAHRTENABZEICHEN+"' konnte nicht erstellt werden.");
    }
    if (!Daten.fahrtenabzeichen.readFile()) {
      Logger.log(Logger.ERROR,"DRV-Fahrtenabezeichendatei '"+Daten.efaDataDirectory+Daten.FAHRTENABZEICHEN+"' konnte nicht gelesen werden.");
    }
    Daten.gruppen = new Gruppen(Daten.efaDataDirectory+Daten.GRUPPEN);
    if (!EfaUtil.canOpenFile(Daten.efaDataDirectory+Daten.GRUPPEN)) {
      if (Daten.gruppen.writeFile())
        Logger.log(Logger.INFO,"Gruppendatei '"+Daten.efaDataDirectory+Daten.GRUPPEN+"' wurde neu erstellt.");
      else
        Logger.log(Logger.ERROR,"Gruppendatei '"+Daten.efaDataDirectory+Daten.GRUPPEN+"' konnte nicht erstellt werden.");
    }
    if (!Daten.gruppen.readFile()) {
      Logger.log(Logger.ERROR,"Gruppendatei '"+Daten.efaDataDirectory+Daten.GRUPPEN+"' konnte nicht gelesen werden.");
    }

    setButtonsLookAndFeel();

    iniBootsListen();

    if (!Daten.efaConfig.version.equals(Daten.PROGRAMMID) && Daten.efaConfig.version.compareTo(Daten.PROGRAMMID) != 0) {
      Daten.checkJavaVersion(true);
    } else {
      Daten.checkJavaVersion(false);
    }

    Logger.log(Logger.INFO,"BEREIT");

    // EfaFrame vorbereiten
    efaFrame = new EfaFrame(this,Daten.nachrichten);
    Dimension dlgSize = efaFrame.getSize();
    efaFrame.setFixedLocation((Dialog.screenSize.width - dlgSize.width) / 2, (Dialog.screenSize.height - dlgSize.height) / 2);

    // ReservierungsChecker-Thread starten
    alive();
    efaDirektBackgroundTask = new EfaDirektBackgroundTask(this);
    efaDirektBackgroundTask.start();

    // Uhr-Thread starten
    efaUhrUpdater = new EfaUhrUpdater(this.uhr,this.srSRtext,this.srSStext,Daten.efaConfig.efaDirekt_sunRiseSet_show);
    efaUhrUpdater.start();
    uhr.setVisible(Daten.efaConfig.efaDirekt_showUhr);

    // News Text anzeigen
    updateNews();

    // Sunrise anzeigen oder nicht
    sunrisePanel.setVisible(Daten.efaConfig.efaDirekt_sunRiseSet_show);
  }


  // Fahrtenbuch einlesen
  public void readFahrtenbuch() {
    if (Daten.efaConfig == null || Daten.efaConfig.direkt_letzteDatei == null || Daten.efaConfig.direkt_letzteDatei.length()==0) {
      haltProgram("Oops! Kein Fahrtenbuch zum �ffnen da!");
    } else {
      Daten.fahrtenbuch = new Fahrtenbuch(Daten.efaConfig.direkt_letzteDatei);
      int sveAction = Daten.actionOnDatenlisteNotFound;
      Daten.actionOnDatenlisteNotFound = Daten.DATENLISTE_FRAGE_REQUIRE_ADMIN_RETURN_FALSE_ON_NEIN;
      while (!Daten.fahrtenbuch.readFile()) {
        Dialog.error("Kann Fahrtenbuch-Datei "+Daten.efaConfig.direkt_letzteDatei+" nicht �ffnen!");

        Admin admin = null;
        do {
          admin = AdminLoginFrame.login(this,"Fahrtenbuch konnte nicht ge�ffnet werden");
          if (admin == null) haltProgram("Programmende, da Fahrtenbuch nicht ge�ffnet werden konnte und Admin-Login nicht erfolgreich.");
          if (!admin.allowedFahrtenbuchAuswaehlen) Dialog.error("Du hast als Admin '"+admin.name+"' keine Berechtigung, ein Fahrtenbuch auszuw�hlen!");
        } while (!admin.allowedFahrtenbuchAuswaehlen);

        String dat = Dialog.dateiDialog(this,"Fahrtenbuch �ffnen","efa Fahrtenbuch (*.efb)","efb",Daten.efaDataDirectory,false);
        if (dat == null || dat.length()==0) haltProgram("Kein Fahrtenbuch ausgew�hlt.");
        Daten.efaConfig.direkt_letzteDatei = dat;
        if (!Daten.efaConfig.writeFile()) haltProgram(Daten.efaConfig.getFileName()+" konnte nicht geschrieben werden!");
        Daten.fahrtenbuch = new Fahrtenbuch(Daten.efaConfig.direkt_letzteDatei);
      }
      Daten.actionOnDatenlisteNotFound = sveAction;
      if (Daten.fahrtenbuch != null && Daten.fahrtenbuch.getDaten().mitglieder != null)
        Daten.fahrtenbuch.getDaten().mitglieder.getAliases();

    }
    Logger.log(Logger.INFO,"Fahrtenbuch "+Daten.fahrtenbuch.getFileName()+" ge�ffnet.");
  }

  public boolean sindNochBooteUnterwegs() {
    return bootStatus.getBoote(BootStatus.STAT_UNTERWEGS).size()>0;
  }


  void statusLabelSetText(String s) {
    statusLabel.setText(s);
    // wenn Text zu lang, dann PreferredSize verringern, damit bei pack() die zu gro�e Label-Breite nicht
    // zum Vergr��ern des Fensters f�hrt!
    if (statusLabel.getPreferredSize().getWidth() > this.getSize().getWidth())
      statusLabel.setPreferredSize(new Dimension((int)this.getSize().getWidth()-20,
                                                 (int)statusLabel.getPreferredSize().getHeight()));
  }

  void setButtonsLookAndFeel() {
    // VereinsLogo setzen
    if (Daten.efaConfig.efaDirekt_vereinsLogo.length()>0) try {
      logoLabel.setIcon(new ImageIcon(Daten.efaConfig.efaDirekt_vereinsLogo));
      logoLabel.setMinimumSize(new Dimension(200, 80));
      logoLabel.setPreferredSize(new Dimension(200, 80));
      logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
      logoLabel.setHorizontalTextPosition(SwingConstants.CENTER);
    } catch(Exception e) {}

    // Look & Feel (Buttonfarben, Text) setzen
    this.fahrtbeginnButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtBeginnenFarbe));
    this.fahrtendeButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtBeendenFarbe));
    this.fahrtabbruchButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtAbbrechenFarbe));
    this.nachtragButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butNachtragFarbe));
    this.bootsstatusButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butBootsreservierungenFarbe));
    this.showFbButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butFahrtenbuchAnzeigenFarbe));
    this.statButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butStatistikErstellenFarbe));
    this.adminHinweisButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butNachrichtAnAdminFarbe));
    this.adminButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butAdminModusFarbe));
    this.spezialButton.setBackground(EfaUtil.getColor(Daten.efaConfig.efaDirekt_butSpezialFarbe));
    this.bootsstatusButton.setVisible(Daten.efaConfig.efaDirekt_butBootsreservierungenAnzeigen);
    this.showFbButton.setVisible(Daten.efaConfig.efaDirekt_butFahrtenbuchAnzeigenAnzeigen);
    this.statButton.setVisible(Daten.efaConfig.efaDirekt_butStatistikErstellenAnzeigen);
    this.adminHinweisButton.setVisible(Daten.efaConfig.efaDirekt_butNachrichtAnAdminAnzeigen);
    this.adminButton.setVisible(Daten.efaConfig.efaDirekt_butAdminModusAnzeigen);
    this.spezialButton.setVisible(Daten.efaConfig.efaDirekt_butSpezialAnzeigen);

    setButtonText();
  }


  public void setButtonText() {
    if (Daten.efaConfig == null) return;
    boolean fkey = Daten.efaConfig.efaDirekt_showButtonHotkey;
    this.fahrtbeginnButton.setText(Daten.efaConfig.efaDirekt_butFahrtBeginnenText + (fkey ? " [F2]" : ""));
    this.fahrtendeButton.setText(Daten.efaConfig.efaDirekt_butFahrtBeendenText    + (fkey ? " [F3]" : ""));
    this.fahrtabbruchButton.setText("Fahrt abbrechen"                             + (fkey ? " [F4]" : ""));
    this.nachtragButton.setText("Nachtrag"                                        + (fkey ? " [F5]" : ""));
    this.bootsstatusButton.setText("Bootsreservierungen"                          + (fkey ? " [F6]" : ""));
    this.showFbButton.setText("Fahrtenbuch anzeigen"                              + (fkey ? " [F7]" : ""));
    this.statButton.setText("Statistik erstellen"                                 + (fkey ? " [F8]" : ""));
    this.adminHinweisButton.setText("Nachricht an Admin"                          + (fkey ? " [F9]" : ""));
    this.adminButton.setText("Admin-Modus"                                        + (fkey ? " [Alt-F10]" : ""));
    this.spezialButton.setText(Daten.efaConfig.efaDirekt_butSpezialText           + (fkey ? " [Alt-F11]" : ""));
    this.verfuegbareBooteLabel.setText("verf�gbare Boote"                         + (fkey ? " [F10]" : ""));
    this.aufFahrtBooteLabel.setText("Boote auf Fahrt"                             + (fkey ? " [F11]" : ""));
    this.nichtVerfuegbareBooteLabel.setText("nicht verf�gbare Boote"              + (fkey ? " [F12]" : ""));
    if (!Daten.efaConfig.efaDirekt_startMaximized) packFrame("setButtonText()");
  }


  public void iniBootsListen() {
    // Bootsliste aufbauen
    booteAlle = new Vector();
    if (Daten.fahrtenbuch.getDaten().boote == null) {
      haltProgram("Fahrtenbuch enth�lt keine Bootsliste!");
    }
    for (DatenFelder d = (DatenFelder)Daten.fahrtenbuch.getDaten().boote.getCompleteFirst();
         d != null;  d = (DatenFelder)Daten.fahrtenbuch.getDaten().boote.getCompleteNext()) {
      booteAlle.add(d.get(Boote.NAME) + (d.get(Boote.VEREIN).length()>0 ? " ("+d.get(Boote.VEREIN)+")" : "") );
    }

    // Boot Status einlesen
    bootStatus = new BootStatus(Daten.efaDataDirectory+Daten.DIREKTBOOTSTATUS);
    if (!EfaUtil.canOpenFile(bootStatus.getFileName())) {
      if (!bootStatus.writeFile()) haltProgram("Kann Bootststatus-Datei "+bootStatus.getFileName()+" nicht erstellen!");
    } else {
      if (!bootStatus.readFile()) haltProgram("Kann Bootststatus-Datei "+bootStatus.getFileName()+" nicht �ffnen!");
    }
    for (int i=0; i<booteAlle.size(); i++) {
      DatenFelder d;
      if (bootStatus.getExact((String)booteAlle.get(i)) != null) {
        d = (DatenFelder)bootStatus.getComplete();
      } else {
        d = new DatenFelder(BootStatus._FELDANZ);
        d.set(BootStatus.NAME,(String)booteAlle.get(i));
        if (Daten.fahrtenbuch.getDaten().boote.getExactComplete((String)booteAlle.get(i)).get(Boote.VEREIN).length()>0) {
          d.set(BootStatus.STATUS,Integer.toString(BootStatus.STAT_HIDE));
          d.set(BootStatus.BEMERKUNG,"wird nicht angezeigt");
          Logger.log(Logger.WARNING,"Boot "+booteAlle.get(i)+" in Statusliste nicht gefunden; mit Status '"+BootStatus.STATUSNAMES[BootStatus.STAT_HIDE]+"' hinzugef�gt.");
        } else {
          d.set(BootStatus.STATUS,Integer.toString(BootStatus.STAT_VERFUEGBAR));
          d.set(BootStatus.BEMERKUNG,BootStatus.STATUSNAMES[BootStatus.STAT_VERFUEGBAR]);
          Logger.log(Logger.WARNING,"Boot "+booteAlle.get(i)+" in Statusliste nicht gefunden; mit Status '"+BootStatus.STATUSNAMES[BootStatus.STAT_VERFUEGBAR]+"' hinzugef�gt.");
        }
        bootStatus.add(d);
      }
    }
    // Bootstatus: nicht existierende Boot aus Statusliste entfernen
    Vector remove = new Vector();
    for (DatenFelder d = (DatenFelder)bootStatus.getCompleteFirst();
         d != null; d = (DatenFelder)bootStatus.getCompleteNext()) {
      if (!booteAlle.contains(d.get(BootStatus.NAME)) &&
          EfaUtil.string2int(d.get(BootStatus.STATUS),-1) != BootStatus.STAT_UNTERWEGS) remove.add(d.get(BootStatus.NAME));
    }
    for (int i=0; i<remove.size(); i++) {
      bootStatus.delete((String)remove.get(i));
      Logger.log(Logger.WARNING,"Boot "+remove.get(i)+" existiert in Statusliste, jedoch nicht in Bootsliste, und wurde daher entfernt.");
    }
    // Statusliste speichern und Boote anzeigen
    if (!bootStatus.writeFile()) haltProgram("Kann Bootststatus-Datei "+bootStatus.getFileName()+" nicht schreiben!");
    updateBootsListen();

  }

  class BootsString implements Comparable {
    public String name;
    public int anzahl;
    public boolean sortByAnzahl;

    private String normalizeString(String s) {
      s = s.toLowerCase();
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","a",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","a",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","a",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","a",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","a",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","a",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","ae",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","c",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","e",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","e",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","e",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","e",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","e",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","i",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","i",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","i",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","n",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","o",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","o",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","o",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","o",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","o",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","o",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","o",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","u",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","u",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","u",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","u",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","u",true);
      if (s.indexOf("�") >= 0) s = EfaUtil.replace(s,"�","ss",true);
      return s;
    }

    public int compareTo(Object o) {
      BootsString other = (BootsString)o;
      String sThis  = (sortByAnzahl ? (anzahl < 10 ? "0" : "") + anzahl : "") + normalizeString(name);
      String sOther = (sortByAnzahl ? (other.anzahl < 10 ? "0" : "") + other.anzahl : "") + normalizeString(other.name);
      return sThis.compareTo(sOther);
    }

  }

  Vector sortBootsList(Vector v) {
    if (v == null || v.size() == 0) return v;

    BootsString[] a = new BootsString[v.size()];
    for (int i=0; i<v.size(); i++) {
      a[i] = new BootsString();
      DatenFelder d = Daten.fahrtenbuch.getDaten().boote.getExactComplete(removeDoppeleintragFromBootsname((String)v.get(i)));
      int anz = 99;
      if (d != null) anz = EfaUtil.string2date(d.get(Boote.ANZAHL),99,0,0).tag;
      if (anz<0) anz = 0;
      if (anz>99) anz = 99;
      a[i].anzahl = anz;
      a[i].name = (String)v.get(i);
      a[i].sortByAnzahl = (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_sortByAnzahl);
    }
    Arrays.sort(a);

    Vector vv = new Vector();
    int anz = -1;
    for (int i=0; i<a.length; i++) {
      if (a[i].anzahl != anz) {
        vv.add("---------- " + (a[i].anzahl != 99 ? a[i].anzahl+"er" : "andere") + " ----------");
        anz = a[i].anzahl;
      }
      vv.add(a[i].name);
    }
    return vv;
/*
    if (Daten.efaConfig == null || !Daten.efaConfig.efaDirekt_sortByAnzahl) return v;

    String[] a = new String[v.size()];
    for (int i=0; i<v.size(); i++) {
      DatenFelder d = Daten.fahrtenbuch.getDaten().boote.getExactComplete(removeDoppeleintragFromBootsname((String)v.get(i)));
      int anz = 99;
      if (d != null) anz = EfaUtil.string2date(d.get(Boote.ANZAHL),99,0,0).tag;
      if (anz<0) anz = 0;
      if (anz>99) anz = 99;
      a[i] = (anz<10 ? "0" : "") + anz + v.get(i);
    }
    Arrays.sort(a);

    Vector vv = new Vector();
    int anz = -1;
    for (int i=0; i<a.length; i++) {
      int ii = EfaUtil.string2int(a[i].substring(0,2),99);
      if (ii != anz) {
        vv.add("---------- " + (ii != 99 ? ii+"er" : "andere") + " ----------");
        anz = ii;
      }
      vv.add(a[i].substring(2,a[i].length()));
    }
    return vv;
*/
  }

  void updateBootsListen() {
    booteVerfuegbarListData = sortBootsList(bootStatus.getBoote(BootStatus.STAT_VERFUEGBAR));
    booteVerfuegbarListData.add(0,"<anderes Boot>");
    booteAufFahrtListData = sortBootsList(bootStatus.getBoote(BootStatus.STAT_UNTERWEGS));
    booteNichtVerfuegbarListData = sortBootsList(bootStatus.getBoote(BootStatus.STAT_NICHT_VERFUEGBAR));

    if (Daten.efaConfig != null && bootStatus != null && Daten.fahrtenbuch != null && Daten.efaConfig.efaDirekt_showZielnameFuerBooteUnterwegs) {
      for (int i=0; booteAufFahrtListData != null && i<booteAufFahrtListData.size(); i++) {
        String b = (String)booteAufFahrtListData.get(i);
        if (b != null) {
          DatenFelder d = bootStatus.getExactComplete(b);
          if (d != null) {
            d = Daten.fahrtenbuch.getExactComplete(d.get(BootStatus.LFDNR));
            if (d != null) {
              String ziel = d.get(Fahrtenbuch.ZIEL);
              if (ziel != null && ziel.length()>0) {
                booteAufFahrtListData.set(i,b + "     -> " + ziel);
              }
            }
          }
        }
      }
    }

    // es gibt einen komischen Bug, der manchmal aufzutreten scheint, wenn in gerade selektiertes Boot aus
    // der Statusliste verschwindet. Der Bug l��t sich manchmal reproduzieren, indem als Mitglied ein Boot
    // f�r sofort reserviert wird. W�hrend dann der Focus noch auf dem Boot steht, nimmt der ReservierungsChecker
    // das Boot aus der Liste. Die Folge ist dann manchmal, da� die Liste einen riesigen Freiraum bekommt.
    // Manchmal ver�ndert sich sogar die Gr��e des Frames dadurch. Vermutlich handelt es sich hierbei um einen
    // Java-Bug.
    // Um den Bug zu umgehen, wurden folgende drei Zeilen eingef�gt. Es konnte jedoch leider nicht �berpr�ft
    // werden, ob diese Zeilen den Bug beheben, da er sich leider auch ohne diese Zeilen nicht mehr reproduzieren
    // lie�.
    booteVerfuegbar.setSelectedIndex(-1);
    booteAufFahrt.setSelectedIndex(-1);
    booteNichtVerfuegbar.setSelectedIndex(-1);

    booteVerfuegbar.setListData(booteVerfuegbarListData);
    booteAufFahrt.setListData(booteAufFahrtListData);
    booteNichtVerfuegbar.setListData(booteNichtVerfuegbarListData);

    statusLabelSetText("Kein Boot ausgew�hlt.");
    booteVerfuegbar.setSelectedIndex(0);
    booteVerfuegbar.requestFocus();
  }


  void eintragAendern(String boot) {
    DatenFelder d = (DatenFelder)bootStatus.getExactComplete(boot);
    if (d == null) {
      if (boot.startsWith("----------")) return; // kein Fehler, wenn jemand es geschafft hat, die Trennlinie zu markieren!
      Dialog.error("Programmfehler: Boot "+boot+" nicht in der Statusliste gefunden!");
      Logger.log(Logger.ERROR,"Programmfehler: Boot "+boot+" nicht in der Statusliste gefunden!");
      return;
    }

    if (d.get(BootStatus.LFDNR).trim().length()==0) {
      // keine LfdNr eingetragen: Das kann passieren, wenn der Admin den Status der Bootes manuell ge�ndert hat!
      Dialog.error("Es ist kein angefangener Eintrag im Fahrtenbuch f�r dieses Boot vermerkt!\n"+
                   "Der Eintrag kann nicht ge�ndert werden.");
      return;
    }
    setEnabled(false);
    efaFrame.direktFahrtAnfangKorrektur(boot,d.get(BootStatus.LFDNR));
  }


  void showBootStatus(int listnr, JList list, int direction) {
    String boot = null;

    try { // list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
      if (list != null && !list.isSelectionEmpty()) boot = listGetSelectedValue(list);
    } catch(Exception e) {
      EfaUtil.foo();
    }

    if (bootStatus != null && boot != null && list != null && boot.startsWith("---------- ")) {
      try {
        int i = list.getSelectedIndex() + direction;
        if (i<0) i=1; // i<0 kann nur erreicht werden, wenn vorher i=0 und direction=-1; dann darf nicht auf i=0 gesprungen werden, da wir dort herkommen, sondern auf i=1
        list.setSelectedIndex(i);
        boot = listGetSelectedValue(list);
      } catch(Exception e) { /* just to be sure */ }
    }

    if (bootStatus != null && boot != null) {
      String stat;
      if (bootStatus.getExact(boot) != null) {
        stat = ((DatenFelder)(bootStatus.getComplete())).get(BootStatus.BEMERKUNG);
      } else {
        stat = "anderes oder fremdes Boot";
      }
      String bootstyp = "";
      String rudererlaubnis = "";
      if (listnr == 1) {
        DatenFelder d = Daten.fahrtenbuch.getDaten().boote.getExactComplete(boot);
        if (d != null) {
          bootstyp = " ("+Boote.getDetailBezeichnung(d)+")";
          Vector gr = Boote.getGruppen(d);
          for (int i=0; i<gr.size(); i++)  {
            rudererlaubnis = (rudererlaubnis.length()>0 ? rudererlaubnis + ", " : "; nur f�r ") + (String)gr.get(i);
          }
        }
      }
      statusLabelSetText(boot+": "+stat + bootstyp + rudererlaubnis);
    }
    if (listnr != 1) booteVerfuegbar.setSelectedIndices(new int[0]);
    if (listnr != 2) booteAufFahrt.setSelectedIndices(new int[0]);
    if (listnr != 3) booteNichtVerfuegbar.setSelectedIndices(new int[0]);
  }


  void doppelklick(int listnr, JList list) {
    if (list == null || list.getSelectedIndex() < 0) return;

    String boot = null;
    try { // list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
      if (list != null && !list.isSelectionEmpty()) boot = listGetSelectedValue(list);
    } catch(Exception e) {
      EfaUtil.foo();
    }

    if (boot == null) return;

    // Handelt es sich um ein Boot, das auf Fahrt ist, aber trotzdem bei "nicht verf�gbar" angezeigt wird?
    DatenFelder d = bootStatus.getExactComplete(boot);
    if (d != null && listnr == 3 && EfaUtil.string2date(d.get(BootStatus.LFDNR),0,0,0).tag>0) listnr = 2;

    if (listnr == 1 || listnr == 3) {
      String fahrtBeginnen = EfaUtil.replace(this.fahrtbeginnButton.getText(),">>>","");
      int pos = fahrtBeginnen.indexOf(" [");
      if (pos>0) fahrtBeginnen = fahrtBeginnen.substring(0,pos-1);
      int auswahl = -1;
      if (listnr == 1) {
        if (Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren) {
          auswahl = Dialog.auswahlDialog("Boot "+boot,"Was m�chtest Du mit dem Boot '"+boot+"' machen?",
                                   fahrtBeginnen,"Boot reservieren","Nichts");
        } else {
          auswahl = Dialog.auswahlDialog("Boot "+boot,"Was m�chtest Du mit dem Boot '"+boot+"' machen?",
                                   fahrtBeginnen,"Nichts",false);
        }
      } else {
        auswahl = Dialog.auswahlDialog("Boot "+boot,"Was m�chtest Du mit dem Boot '"+boot+"' machen?",
                                   fahrtBeginnen,"Bootsreservierungen anzeigen","Nichts");
      }
      switch (auswahl) {
        case 0: this.fahrtbeginnButton_actionPerformed(null); break;
        case 1: if (listnr != 1 || Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren) {
                  this.bootsstatusButton_actionPerformed(null);
                } // else: nothing to do!
                break;
        default: return;
      }
    }
    if (listnr == 2) {
      String fahrtBeenden = EfaUtil.replace(this.fahrtendeButton.getText(),"<<<","");
      int pos = fahrtBeenden.indexOf(" [");
      if (pos>0) fahrtBeenden = fahrtBeenden.substring(0,pos);
      int auswahl = -1;
      if (Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren) {
        auswahl = Dialog.auswahlDialog("Boot "+boot,"Was m�chtest Du mit dem Boot '"+boot+"' machen?",
                                   fahrtBeenden,"Eintrag �ndern","Fahrt abbrechen","Boot reservieren","Nichts");
      } else {
        auswahl = Dialog.auswahlDialog("Boot "+boot,"Was m�chtest Du mit dem Boot '"+boot+"' machen?",
                                   fahrtBeenden,"Eintrag �ndern","Fahrt abbrechen","Nichts");
      }
      switch (auswahl) {
        case 0: this.fahrtendeButton_actionPerformed(null); break;
        case 1: this.eintragAendern(boot); break;
        case 2: this.fahrtabbruchButton_actionPerformed(null); break;
        case 3: if (Daten.efaConfig.efaDirekt_mitgliederDuerfenReservieren) {
                  this.bootsstatusButton_actionPerformed(null);
                } // else: nothing to do!
                break;
        default: return;
      }
    }
  }

  void booteVerfuegbar_mouseClicked(MouseEvent e) {
    showBootStatus(1,booteVerfuegbar,1);

    if (e != null && e.getClickCount() == 2) { // Doppelklick
      doppelklick(1,booteVerfuegbar);
    }
  }
  void booteAufFahrt_mouseClicked(MouseEvent e) {
    showBootStatus(2,booteAufFahrt,1);

    if (e != null && e.getClickCount() == 2) { // Doppelklick
      doppelklick(2,booteAufFahrt);
    }
  }
  void booteNichtVerfuegbar_mouseClicked(MouseEvent e) {
    showBootStatus(3,booteNichtVerfuegbar,1);

    if (e != null && e.getClickCount() == 2) { // Doppelklick
      doppelklick(3,booteNichtVerfuegbar);
    }
  }
  void booteVerfuegbar_keyReleased(KeyEvent e) {
    if (e != null) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
        doppelklick(1,booteVerfuegbar);
        return;
      }
      scrollToBoot(booteVerfuegbar,booteVerfuegbarListData,String.valueOf(e.getKeyChar()),15);
    }
    showBootStatus(1,booteVerfuegbar,(e != null && e.getKeyCode() == 38 ? -1 : 1)); // KeyCode 38 == Cursor Up
  }
  void booteAufFahrt_keyReleased(KeyEvent e) {
    if (e != null) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
        doppelklick(2,booteAufFahrt);
        return;
      }
      scrollToBoot(booteAufFahrt,booteAufFahrtListData,String.valueOf(e.getKeyChar()),10);
    }
    showBootStatus(2,booteAufFahrt,(e != null && e.getKeyCode() == 38 ? -1 : 1)); // KeyCode 38 == Cursor Up
  }
  void booteNichtVerfuegbar_keyReleased(KeyEvent e) {
    if (e != null) {
      if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_SPACE) {
        doppelklick(3,booteNichtVerfuegbar);
        return;
      }
      scrollToBoot(booteNichtVerfuegbar,booteNichtVerfuegbarListData,String.valueOf(e.getKeyChar()),5);
    }
    showBootStatus(3,booteNichtVerfuegbar,(e != null && e.getKeyCode() == 38 ? -1 : 1)); // KeyCode 38 == Cursor Up
  }

  // scrolle in der Liste list (deren Inhalt der Vector boote ist), zu dem Eintrag
  // mit dem Namen such und selektiere ihn. Zeige unterhalb des Boote bis zu plus weitere Eintr�ge.
  private void scrollToBoot(JList list, Vector boote, String such, int plus) {
    if (list == null || boote == null || such == null || such.length()==0) return;
    try {
      int start = 0;
      if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_sortByAnzahl) {
        if (such.charAt(0)>='0' && such.charAt(0)<='9') {
          such = "---------- "+such;
        } else {
          start = list.getSelectedIndex();
          if (start<0) start = 0;
          while (start>0 && !((String)boote.get(start)).startsWith("---------- ")) start--;
        }
      }

      int index = -1;
      for (int i=start; i<boote.size(); i++) {
        if (((String)boote.get(i)).toLowerCase().startsWith(such)) {
          index = i;
          break;
        }
      }
      if (index < 0) return;

      list.setSelectedIndex(index);
      list.scrollRectToVisible(list.getCellBounds(index, (index+plus >= boote.size() ? boote.size()-1 : index+plus) ));
    } catch(Exception ee) { /* just to be sure */ }


/*    try {
      bootStatus.ignoreCase(true);
      String boot;
      for (boot = bootStatus.getFirst(such); boot != null && !boote.contains(boot); boot = bootStatus.getNext(such));
      if (boot != null && boote.contains(boot)) {
        int i = boote.indexOf(boot)+increment;
        list.setSelectedIndex(i);
        list.scrollRectToVisible(list.getCellBounds(i, (i+plus >= boote.size()+increment ? boote.size()+increment-1 : i+plus) ));
      }
    } catch(Exception ee) {}
    bootStatus.ignoreCase(false);*/
  }

  private String listGetSelectedValue(JList list) {
    if (list == null || list.isSelectionEmpty()) return null;
    String boot = (String)list.getSelectedValue();
    int pos = boot.indexOf("->");
    if (pos > 0) {
      boot = boot.substring(0,pos).trim();
    }
    return boot;
  }

  private static String removeDoppeleintragFromBootsname(String boot) {
    if (boot == null || boot.length()==0) return boot;
    if (boot.charAt(boot.length()-1) != ']') return boot;
    int anf = boot.indexOf(" [");
    if (anf<0) return boot;
    return boot.substring(0,anf);
  }

  void fahrtbeginnButton_actionPerformed(ActionEvent e) {
    alive();
    int status = BootStatus.STAT_VERFUEGBAR;

    String boot = null;
    try {
      if (!booteVerfuegbar.isSelectionEmpty()) boot = listGetSelectedValue(booteVerfuegbar);
      if (boot == null) {
        if (!booteAufFahrt.isSelectionEmpty()) boot = listGetSelectedValue(booteAufFahrt);
        if (boot == null) {
          if (!booteNichtVerfuegbar.isSelectionEmpty()) boot = listGetSelectedValue(booteNichtVerfuegbar);
        }
      }
    } catch(Exception ee) {
      EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
    }

    if (boot == null) {
      Dialog.error("Bitte w�hle zuerst ein Boot aus!");
      this.booteVerfuegbar.requestFocus();
      this.efaDirektBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
      return;
    }

    if (booteVerfuegbar.getSelectedIndex()==0) { // <anderes Boot> ausgew�hlt!
      boot = null;
    } else {
      if (!checkFahrtbeginnFuerBoot(boot,1)) return;
      boot = removeDoppeleintragFromBootsname(boot);
    }

    setEnabled(false);
    efaFrame.direktFahrtAnfang(boot);
  }

  // mode bestimmt die Art der Checks
  // mode==1 - alle Checks durchf�hren
  // mode==2 - nur solche Checks durchf�hren, bei denen es egal ist, ob das Boot aus der Liste direkt ausgew�hlt wurde
  //           oder manuell �ber <anders Boot> eingegeben wurde. Der Aufruf von checkFahrtbeginnFuerBoot mit mode==2
  //           erfolgt aus EfaFrame.java.
  public boolean checkFahrtbeginnFuerBoot(String boot, int mode) {
    if (boot == null) return true;
    DatenFelder d = bootStatus.getExactComplete(boot);
    String bootsname = removeDoppeleintragFromBootsname(boot);
    if (d == null) {
      if (boot.startsWith("----------")) return false; // kein Fehler, wenn jemand es geschafft hat, die Trennlinie zu markieren!
      if (mode == 2) return true; // anderes Boot
      Dialog.error("Programmfehler: Boot "+boot+" nicht in der Statusliste gefunden!");
      Logger.log(Logger.ERROR,"Programmfehler: Boot "+boot+" nicht in der Statusliste gefunden!");
      return false;
    }
    int status = EfaUtil.string2int(d.get(BootStatus.STATUS),-1);

    if (status == BootStatus.STAT_UNTERWEGS) {
      if (mode == 1) {
        switch (Dialog.auswahlDialog("Boot bereits unterwegs",
                                     "Das Boot " + bootsname +
                                     " ist laut Liste bereits unterwegs:\n" +
                                     (d != null ?
                                      "Bemerkung: " +
                                      d.get(BootStatus.BEMERKUNG) + "\n" : "") +
                                     "\n" +
                                     "Was m�chtest Du tun?",
                                     "Neue Fahrt beginnen",
                                     "Vorhandenen Eintrag �ndern", "Nichts")) {
          case 0:
            break;
          case 1:
            eintragAendern(boot);
            return false;
          case 2:
            return false;
          default:
            return false;
        }
      } else {
        if (Dialog.yesNoCancelDialog("Boot bereits unterwegs",
                                               "Das Boot "+bootsname+" ist laut Liste bereits unterwegs:\n"+
                                               (d != null ? "Bemerkung: "+d.get(BootStatus.BEMERKUNG)+"\n" : "")+
                                               "\n"+
                                               "M�chtest Du trotzdem eine neue Fahrt mit diesem Boot beginnen?")
                      != Dialog.YES) return false;
      }
    }
    if (status == BootStatus.STAT_NICHT_VERFUEGBAR && !d.get(BootStatus.LFDNR).equals(BootStatus.RES_LFDNR)) {
      if (Dialog.yesNoCancelDialog("Boot ist gesperrt",
                                             "Das Boot "+bootsname+" ist laut Liste nicht verf�gbar:\n"+
                                             (d != null ? "Bemerkung: "+d.get(BootStatus.BEMERKUNG)+"\n" : "")+
                                             "\n"+
                                             "M�chtest Du trotzdem eine neue Fahrt mit diesem Boot beginnen?")
                    != Dialog.YES) return false;
    }


    DatenFelder d2 = bootStatus.getExactComplete(removeDoppeleintragFromBootsname(boot));
    Reservierung res = BootStatus.getReservierung(d,System.currentTimeMillis(),Daten.efaConfig.efaDirekt_resLookAheadTime);
    if (res == null && d2 != null) res = BootStatus.getReservierung(d2,System.currentTimeMillis(),Daten.efaConfig.efaDirekt_resLookAheadTime);
    if (res != null) {
      if (Dialog.yesNoCancelDialog("Boot reserviert","Das Boot "+bootsname+" ist "+
                                             (res.gueltigInMinuten == 0 ? "zur Zeit" : "in "+res.gueltigInMinuten+" Minuten")+"\n"+
                                             "f�r "+res.name+" reserviert" + (res.grund.equals ("k.A.") ? "" : " (Grund: "+res.grund+")") + ".\n"+
                                             "Die Reservierung liegt "+BootStatus.makeReservierungText(res)+" vor.\n"+
                                             "\n"+
                                             "M�chtest Du trotzdem eine neue Fahrt mit diesem Boot beginnen?")
                    != Dialog.YES) return false;
    }
    if (d.get(BootStatus.BOOTSSCHAEDEN).trim().length() > 0 || (d2 != null && d2.get(BootStatus.BOOTSSCHAEDEN).trim().length() > 0)) {
      if (Dialog.yesNoDialog("Bootsschaden gemeldet","F�r das Boot "+bootsname+" wurde folgender Bootsschaden gemeldet:\n"+
                                                "\""+
                                                (d.get(BootStatus.BOOTSSCHAEDEN).trim().length() > 0 ? d.get(BootStatus.BOOTSSCHAEDEN).trim() : d2.get(BootStatus.BOOTSSCHAEDEN).trim())
                                                +"\"\n\n"+
                                                "M�chtest Du trotzdem mit dem Boot rudern?")
                             != Dialog.YES) return false;
    }
    return true;
  }

  public void fahrtBegonnen(String boot, String lfdNr, String datum, String zeit, String person, String fahrtart, String ziel) {
    int status = BootStatus.STAT_VERFUEGBAR;
    DatenFelder d = bootStatus.getExactComplete(boot);
    if (d == null) {
      Logger.log(Logger.INFO,"Fahrtbeginn eines unbekannten Bootes: "+boot);
      d = new DatenFelder(BootStatus._FELDANZ);
      d.set(BootStatus.NAME,boot);
      d.set(BootStatus.UNBEKANNTESBOOT,"+");
    } else {
      status = EfaUtil.string2int(d.get(BootStatus.STATUS),-1);
    }

    if (status != BootStatus.STAT_VERFUEGBAR && !d.get(BootStatus.LFDNR).equals(BootStatus.RES_LFDNR)) {
      String tmp = null;

      // Bootsnamen um Timestamp erweitern, der bislang noch nicht verwendet wurde
      for (char c = 'A'-1; c<='Z' && tmp == null; c++) {
        tmp = boot + " ["+EfaUtil.getCurrentTimeStampDD_MM_HH_MM() + (c<'A' ? "" : ""+c) + "]";
        if (bootStatus.getExact(tmp) != null) tmp = null;
      }

      if (tmp == null) { // alle 27 Timestamps f�r dieses Boot schon vergeben: sollte niemals passieren ...
        Logger.log(Logger.ERROR,"Fahrtbeginn des Bootes '"+boot+"' nicht m�glich!");
        return;
      }

      boot = tmp;
      Logger.log(Logger.INFO,"Fahrtbeginn eines Bootes, welches laut Liste nicht verf�gbar ist (Status ["+status+"]: "+
                                  d.get(BootStatus.BEMERKUNG) +"); Neuer Eintrag als Boot: "+boot);
      DatenFelder old_d = d;
      d = new DatenFelder(BootStatus._FELDANZ);
      d.set(BootStatus.NAME,boot);
      d.set(BootStatus.UNBEKANNTESBOOT,"+"); // Doppeleintr�ge sind immer "unbekannte" Boote!!
      // Bugfix: auch Bootssch�den und Reservierungen m�ssen �bernommen werden, da sonst diese Daten u.U. verloren gehen
      // k�nnten, wenn der andere Eintrag als erstes und dieser als zweites beendet wird.
      d.set(BootStatus.BOOTSSCHAEDEN,old_d.get(BootStatus.BOOTSSCHAEDEN));
      d.set(BootStatus.RESERVIERUNGEN,old_d.get(BootStatus.RESERVIERUNGEN));
    }

    bootStatus.delete(boot);
    d.set(BootStatus.LFDNR,lfdNr);
    d.set(BootStatus.STATUS,Integer.toString(BootStatus.STAT_UNTERWEGS));
    String aufFahrtart = "";
    if (Daten.bezeichnungen != null && Daten.bezeichnungen.fahrtart != null && fahrtart != null) {
      if (fahrtart.equals(Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_REGATTA))) aufFahrtart = " auf "+Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_REGATTA);
      if (fahrtart.equals(Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_JUMREGATTA))) aufFahrtart = " auf "+Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_JUMREGATTA);
      if (fahrtart.equals(Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_TRAININGSLAGER))) aufFahrtart = " auf "+Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_TRAININGSLAGER);
      if (fahrtart.equals(Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_MEHRTAGESFAHRT))) aufFahrtart = " auf "+Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_MEHRTAGESFAHRT);
      if (Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar && aufFahrtart.length()>0) d.set(BootStatus.STATUS,Integer.toString(BootStatus.STAT_NICHT_VERFUEGBAR));
    }
    String nachZiel = "";
    if (aufFahrtart.length() == 0 && ziel.length() > 0) {
      nachZiel = " nach "+ziel;
    }
    d.set(BootStatus.BEMERKUNG,"unterwegs"+aufFahrtart+nachZiel+" seit "+datum+ (zeit.trim().length()>0 ? " um "+zeit : "") + " mit "+person);
    bootStatus.add(d);
    setKombiBootStatus(boot,"",BootStatus.STAT_VORUEBERGEHEND_VERSTECKEN,"vor�bergehend von efa versteckt");
    if (!bootStatus.writeFile()) {
      Dialog.error("Statusliste f�r Boote konnte nicht geschrieben werden! Bitte den Administrator benachrichtigen!");
      Logger.log(Logger.ERROR,"Statusliste f�r Boote konnte nicht geschrieben werden!");
    }
    updateBootsListen();
  }

  public void fahrtBeginnKorrigiert(String boot, String lfdNr, String datum, String zeit, String person, String fahrtart, String ziel, String ursprBoot) {
    if (!boot.equals(ursprBoot)) {
      // Bootsname wurde ge�ndert
      Logger.log(Logger.INFO,"Fahrtbeginn korrigiert: #"+lfdNr+" - �nderung des Bootsnamens von "+ursprBoot+" in "+boot);
      fahrtBeendet(ursprBoot,true);
      fahrtBegonnen(boot,lfdNr,datum,zeit,person,fahrtart,ziel);
      return;
    }

    // Bootsname wurde nicht ge�ndert
    int status = BootStatus.STAT_VERFUEGBAR;
    DatenFelder d = bootStatus.getExactComplete(boot);
    if (d == null) {
      Logger.log(Logger.INFO,"Korrektur des Fahrtbeginns eines unbekannten Bootes: "+boot);
      return;
    }
    status = EfaUtil.string2int(d.get(BootStatus.STATUS),-1);
    if (status != BootStatus.STAT_UNTERWEGS) {
      Logger.log(Logger.INFO,"Korrektur des Fahrtbeginns eines Bootes, das nicht unterwegs ist [Status: "+status+"]");
      return;
    }
    bootStatus.delete(boot);
    d.set(BootStatus.LFDNR,lfdNr);
    d.set(BootStatus.STATUS,Integer.toString(BootStatus.STAT_UNTERWEGS));
    String aufFahrtart = "";
    if (Daten.bezeichnungen != null && Daten.bezeichnungen.fahrtart != null && fahrtart != null) {
      if (fahrtart.equals(Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_REGATTA))) aufFahrtart = " auf "+Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_REGATTA);
      if (fahrtart.equals(Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_JUMREGATTA))) aufFahrtart = " auf "+Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_JUMREGATTA);
      if (fahrtart.equals(Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_TRAININGSLAGER))) aufFahrtart = " auf "+Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_TRAININGSLAGER);
      if (fahrtart.equals(Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_MEHRTAGESFAHRT))) aufFahrtart = " auf "+Daten.bezeichnungen.fahrtart.get(Bezeichnungen.FAHRT_MEHRTAGESFAHRT);
    }
    d.set(BootStatus.BEMERKUNG,"unterwegs"+aufFahrtart+" seit "+datum+ (zeit.trim().length()>0 ? " um "+zeit : "") + " mit "+person);
    bootStatus.add(d);
    setKombiBootStatus(boot,"",BootStatus.STAT_VORUEBERGEHEND_VERSTECKEN,"vor�bergehend von efa versteckt");
    if (!bootStatus.writeFile()) {
      Dialog.error("Statusliste f�r Boote konnte nicht geschrieben werden! Bitte den Administrator benachrichtigen!");
      Logger.log(Logger.ERROR,"Statusliste f�r Boote konnte nicht geschrieben werden!");
    }
    updateBootsListen();
  }

  void fahrtendeButton_actionPerformed(ActionEvent e) {
    alive();
    String boot = null;
    try {
      if (!booteAufFahrt.isSelectionEmpty()) boot = listGetSelectedValue(booteAufFahrt);
      if (boot == null && Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar && !booteNichtVerfuegbar.isSelectionEmpty()) {
        // pr�fen, ob vielleicht ein Boot in der Liste "nicht verf�gbar" auf Regatta oder Wanderfahrt unterwegs ist
        boot = listGetSelectedValue(booteNichtVerfuegbar);
        if (boot != null  && bootStatus.getExact(boot) != null) {
          DatenFelder d = (DatenFelder)bootStatus.getComplete();
          if (EfaUtil.string2int(d.get(BootStatus.LFDNR),0) == 0) boot = null; // keine g�ltige LfdNr
        }
      }
    } catch(Exception ee) {
      EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
    }
    if (boot == null) {
      Dialog.error("Bitte w�hle zuerst"+(Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar ? " " : " aus der rechten oberen Liste ") +"ein Boot aus, welches unterwegs ist!");
      this.booteAufFahrt.requestFocus();
      this.efaDirektBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
      return;
    }
    if (bootStatus.getExact(boot) != null) {
      DatenFelder d = (DatenFelder)bootStatus.getComplete();
      if (d.get(BootStatus.LFDNR).trim().length()==0) {
        // keine LfdNr eingetragen: Das kann passieren, wenn der Admin den Status der Bootes manuell ge�ndert hat!
        Logger.log(Logger.ERROR,"Oops: Keine LfdNr f�r Boot "+boot+" vermerkt! Beenden der Fahrt nicht m�glich.");
        Dialog.error("Es ist kein angefangener Eintrag im Fahrtenbuch f�r dieses Boot vermerkt!\n"+
                     "Die Fahrt kann nicht beendet werden.");
        return;
      }
      if (Daten.fahrtenbuch.getExact(d.get(BootStatus.LFDNR)) == null) {
        Logger.log(Logger.ERROR,"Oops: Beenden der Fahrt "+d.get(BootStatus.LFDNR)+" nicht m�glich, da diese im Fahrtenbuch nicht gefunden wurde!");
        Dialog.error("Es konnte kein angefangener Eintrag im Fahrtenbuch f�r dieses Boot gefunden werden!\n"+
                     "Die Fahrt kann nicht zur�ckgetragen werden.");
        return;
      }
      setEnabled(false);
      efaFrame.direktFahrtEnde(boot,d.get(BootStatus.LFDNR));
    } else {
      if (boot.startsWith("----------")) return; // kein Fehler, wenn jemand es geschafft hat, die Trennlinie zu markieren!
      Dialog.error("Programmfehler: Boot "+boot+" nicht in der Statusliste gefunden!");
      Logger.log(Logger.ERROR,"Programmfehler: Boot "+boot+" nicht in der Statusliste gefunden!");
    }
  }

  public void fahrtBeendet(String boot, boolean interaktiv) {
    DatenFelder d = (DatenFelder)bootStatus.getExactComplete(boot);
    if (d == null) {
      if (boot.startsWith("----------")) return; // kein Fehler, wenn jemand es geschafft hat, die Trennlinie zu markieren!
      if (interaktiv) Dialog.error("Programmfehler: Boot "+boot+" nicht in der Statusliste gefunden!");
      Logger.log(Logger.ERROR,"Programmfehler: Boot "+boot+" nicht in der Statusliste gefunden!");
      return;
    }

    // Boot aus Statustliste l�schen
    bootStatus.delete(boot);

    if (!d.get(BootStatus.UNBEKANNTESBOOT).equals("+")) {
      // es handelt sich um *kein* unbekanntes Boot, also auch nicht um einen Doppelaustrag

      // gibt es einen zu diesem Bootsnamen passenden Doppelaustrag?
      DatenFelder dd = bootStatus.getCompleteFirst(boot+" [");
      if (dd != null) {
        // Ja, Doppeleintrag existiert: Dann eckige Klammern dort l�schen und Boot *nicht* als verf�gbar markieren
        bootStatus.delete(dd.get(BootStatus.NAME));
        dd.set(BootStatus.NAME,boot);
        dd.set(BootStatus.UNBEKANNTESBOOT,"-");
        bootStatus.add(dd);
      } else {
        // Nein, Doppeleintrag existiert nicht: Boot als verf�gbar markieren
        d.set(BootStatus.STATUS,Integer.toString(BootStatus.STAT_VERFUEGBAR));
        d.set(BootStatus.BEMERKUNG,BootStatus.STATUSNAMES[BootStatus.STAT_VERFUEGBAR]);
        d.set(BootStatus.LFDNR,"");
        bootStatus.add(d);
        setKombiBootStatus(boot,"",BootStatus.STAT_VERFUEGBAR,BootStatus.STATUSNAMES[BootStatus.STAT_VERFUEGBAR]);
      }
    }

    if (interaktiv && !bootStatus.writeFile()) {
      Dialog.error("Statusliste f�r Boote konnte nicht geschrieben werden! Bitte den Administrator benachrichtigen!");
      Logger.log(Logger.ERROR,"Statusliste f�r Boote konnte nicht geschrieben werden!");
    }
    if (interaktiv) {
      updateBootsListen();
      efaDirektBackgroundTask.interrupt();
    }
  }

  void fahrtabbruchButton_actionPerformed(ActionEvent e) {
    alive();
    String boot = null;
    try {
      if (!booteAufFahrt.isSelectionEmpty()) boot = listGetSelectedValue(booteAufFahrt);
      if (boot == null && Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar && !booteNichtVerfuegbar.isSelectionEmpty()) {
        // pr�fen, ob vielleicht ein Boot in der Liste "nicht verf�gbar" auf Regatta oder Wanderfahrt unterwegs ist
        boot = listGetSelectedValue(booteNichtVerfuegbar);
        if (boot != null  && bootStatus.getExact(boot) != null) {
          DatenFelder d = (DatenFelder)bootStatus.getComplete();
          if (EfaUtil.string2int(d.get(BootStatus.LFDNR),0) == 0) boot = null; // keine g�lte LfdNr
        }
      }
    } catch(Exception ee) {
      EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
    }
    if (boot == null) {
      Dialog.error("Bitte w�hle zuerst"+(Daten.efaConfig.efaDirekt_wafaRegattaBooteAufFahrtNichtVerfuegbar ? " " : " aus der rechten oberen Liste ") +"ein Boot aus, welches unterwegs ist!");
      this.booteAufFahrt.requestFocus();
      this.efaDirektBackgroundTask.interrupt(); // Falls requestFocus nicht funktioniert hat, setzt der Thread ihn richtig!
      return;
    }
    fahrtAbbruch(boot,true);
  }

  void fahrtAbbruch(String boot, boolean interaktiv) {
    DatenFelder d = bootStatus.getExactComplete(boot);
    if (d == null) {
      if (boot.startsWith("----------")) return; // kein Fehler, wenn jemand es geschafft hat, die Trennlinie zu markieren!
      if (interaktiv) Dialog.error("Programmfehler: Boot "+boot+" nicht in der Statusliste gefunden!");
      Logger.log(Logger.ERROR,"Programmfehler: Boot "+boot+" nicht in der Statusliste gefunden!");
      return;
    }
    if (interaktiv && Dialog.yesNoDialog("Fahrt wirklich abbrechen",
                                           "Die Fahrt des Bootes "+removeDoppeleintragFromBootsname(boot)+" sollte nur abgebrochen werden,\n"+
                                           "wenn sie nie stattgefunden hat. In diesem Fall wird der begonnene Eintrag wieder entfernt.\n"+
                                           "M�chtest Du die Fahrt wirklich abbrechen?") != Dialog.YES) return;
    if (d.get(BootStatus.LFDNR).trim().length() == 0) {
      Logger.log(Logger.ERROR,"Oops: Fahrtabbruch f�r Boot "+d.get(BootStatus.NAME)+", aber keine LfdNr f�r Boot "+d.get(BootStatus.NAME)+" vermerkt! Fahrt wird NICHT abgebrochen.");
      if (interaktiv) Dialog.error("Die Fahrt kann nicht abgebrochen werden, da im Fahrtenbuch kein Eintrag f�r diese Fahrt existiert.");
      return;
    }
    if (Daten.fahrtenbuch.getExact(d.get(BootStatus.LFDNR)) == null) {
      Logger.log(Logger.ERROR,"Oops: Fahrtabbruch f�r Boot "+d.get(BootStatus.NAME)+", aber LfdNr #"+d.get(BootStatus.LFDNR)+" existiert im Fahrtenbuch nicht! Fahrt wird NICHT abgebrochen.");
      if (interaktiv) Dialog.error("Die Fahrt kann nicht abgebrochen werden, da im Fahrtenbuch kein Eintrag f�r diese Fahrt existiert.");
      return;
    }
    Logger.log(Logger.INFO,"Fahrtabbruch: #"+d.get(BootStatus.LFDNR)+" - "+d.get(BootStatus.NAME)+" ("+d.get(BootStatus.BEMERKUNG)+")");
    Daten.fahrtenbuch.delete(d.get(BootStatus.LFDNR));
    if (interaktiv && !Daten.fahrtenbuch.writeFile()) {
      Logger.log(Logger.ERROR,"Fahrtenbuch kann nicht geschrieben werden!");
      if (interaktiv) Dialog.error("Fahrtenbuch kann nicht geschrieben werden! Bitte dem Administration bescheid sagen!");
    }
    fahrtBeendet(boot,interaktiv);
  }

  void nachtragButton_actionPerformed(ActionEvent e) {
    alive();
    setEnabled(false);

    String boot = null;
    try {
      if (!booteVerfuegbar.isSelectionEmpty() && booteVerfuegbar.getSelectedIndex() != 0) boot = listGetSelectedValue(booteVerfuegbar);
      if (boot == null) {
        if (!booteAufFahrt.isSelectionEmpty()) boot = listGetSelectedValue(booteAufFahrt);
        if (boot == null) {
          if (!booteNichtVerfuegbar.isSelectionEmpty()) boot = listGetSelectedValue(booteNichtVerfuegbar);
        }
      }
    } catch(Exception ee) {}
    if (boot != null && boot.startsWith("-")) boot = null;
    efaFrame.direktFahrtNachtrag(boot);
  }

  public void fahrtNachgetragen() {
  }

  void bootsstatusButton_actionPerformed(ActionEvent e) {
    alive();
    String boot = null;
    try {
      if (!booteVerfuegbar.isSelectionEmpty()) boot = listGetSelectedValue(booteVerfuegbar);
      if (boot == null) {
        if (!booteAufFahrt.isSelectionEmpty()) boot = listGetSelectedValue(booteAufFahrt);
        if (boot == null) {
          if (!booteNichtVerfuegbar.isSelectionEmpty()) boot = listGetSelectedValue(booteNichtVerfuegbar);
        }
      } else {
        if (booteVerfuegbar.getSelectedIndex()==0) {
          Dialog.error("Dieses Boot kann nicht reserviert werden!");
          return;
        }
      }
    } catch(Exception ee) {
      EfaUtil.foo();// list.getSelectedValue() wirft bei Frederik Hoppe manchmal eine Exception (Java-Bug?)
    }
    if (boot == null) {
      Dialog.error("Bitte w�hle zuerst ein Boot aus!");
      return;
    }

    boot = removeDoppeleintragFromBootsname(boot);

    DatenFelder d = bootStatus.getExactComplete(boot);
    if (d == null) {
      Logger.log(Logger.ERROR,"Boot '"+boot+"' konnte nicht in der Statusliste gefunden werden (Benutzer: Bootstatus bearbeiten).");
      return;
    }
    if (d.get(BootStatus.UNBEKANNTESBOOT).equals("+")) {
      Dialog.error("Das Boot "+boot+" ist ein fremdes Boot und kann nicht reserviert werden.");
      return;
    }
    BootStatusFrame dlg = new BootStatusFrame(this,d,bootStatus);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
  }

  void showFbButton_actionPerformed(ActionEvent e) {
    alive();
    if (FahrtenbuchAnzeigenFrame.wirdBereitsAngezeigt) return;
    FahrtenbuchAnzeigenFrame dlg = new FahrtenbuchAnzeigenFrame(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    dlg = null;
  }

  void statButton_actionPerformed(ActionEvent e) {
    alive();
    if (Daten.fahrtenbuch == null || Daten.fahrtenbuch.getDaten().statistik == null) {
      Dialog.error("Es sind keine Statistiken verf�gbar!\n\n"+
                   "Hinweis f�r Administratoren:\n"+
                   "Damit Statistiken im Bootshaus von jedem Mitglied aufrufbar sind,\n"+
                   "m�ssen sie zuerst im Admin-Modus vorbereitet und als Statistikeinstellungen\n"+
                   "abgespeichert werden. Beim Abspeichern mu� zus�tzliche die Option\n"+
                   "'Statistik auch im Bootshaus verf�gbar machen' aktiviert werden.");
      return;
    }

    Vector stats = new Vector();
    DatenFelder d = Daten.fahrtenbuch.getDaten().statistik.getCompleteFirst();
    while (d != null) {
      if (d.get(StatSave.AUCHINEFADIREKT).equals("+")) stats.add(d.get(StatSave.NAMESTAT));
      d = Daten.fahrtenbuch.getDaten().statistik.getCompleteNext();
    }
    if (stats.size() == 0) {
      Dialog.error("Es sind keine Statistiken verf�gbar!\n\n"+
                   "Hinweis f�r Administratoren:\n"+
                   "Damit Statistiken im Bootshaus von jedem Mitglied aufrufbar sind,\n"+
                   "m�ssen sie zuerst im Admin-Modus vorbereitet und als Statistikeinstellungen\n"+
                   "abgespeichert werden. Beim Abspeichern mu� zus�tzliche die Option\n"+
                   "'Statistik auch im Bootshaus verf�gbar machen' aktiviert werden.");
      return;
    }

    try {
      StatistikDirektFrame dlg = new StatistikDirektFrame(this,stats);
      Dialog.setDlgLocation(dlg,this);
      dlg.setModal(true);
      dlg.show();
    } catch(Exception ee) {
      // HTML Reder Exception reported by Thilo Coblenzer (01.06.06)
    }
  }

  void adminHinweisButton_actionPerformed(ActionEvent e) {
    alive();
    NachrichtAnAdminFrame dlg = new NachrichtAnAdminFrame(this,Daten.nachrichten);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
    updateUnreadMessages();
  }

  void adminButton_actionPerformed(ActionEvent e) {
    alive();

    // Pr�fe, ob bereits ein Admin-Modus-Fenster offen ist
    Stack s = Dialog.frameStack;
    boolean adminOnStack = false;
    try {
      for (int i=0; i<s.size(); i++) {
        if (s.elementAt(i).getClass().getName().equals("de.nmichael.efa.direkt.AdminFrame")) adminOnStack = true;
      }
    } catch(Exception ee) {}
    if (adminOnStack) {
      Dialog.error("Es ist bereits ein Admin-Fenster ge�ffnet.");
      return;
    }

    Admin admin = AdminLoginFrame.login(this,"Admin-Modus");
    if (admin == null) return;
    AdminFrame dlg = new AdminFrame(this,admin,bootStatus);
    Dialog.setDlgLocation(dlg,this);
    // Folgende Zeile *mu�* auskommentiert sein; statt dessen wird "this.setEnabled(false)" verwendet.
    //    dlg.setModal(!Dialog.tourRunning);
    // Folgende Zeile *mu�* auskommentiert sein, da unter Java 1.5 sonst in den im Admin-Modus ge�ffneten Dialogen
    // keine Eingaben m�glich sind. Dies scheint ein Bug in 1.5 zu sein. Da EfaDirektFrame aktiviert bleibt, ist
    // ein Navigieren im Admin-Modus im EfaDirektFrame m�glich, was aber eine vertretbare Unsch�nheit ist... ;-)
    //    this.setEnabled(false); //!!!
    dlg.show();
  }

  void spezialButton_actionPerformed(ActionEvent e) {
    alive();
    String cmd = Daten.efaConfig.efaDirekt_butSpezialCmd.trim();
    if (cmd.length() > 0) {
      try {
        if (cmd.toLowerCase().startsWith("browser:")) {
          Dialog.neuBrowserDlg(this,"efa-Browser",cmd.substring(8));
        } else {
          Runtime.getRuntime().exec(cmd);
        }
      } catch(Exception ee) {
        Logger.log(Logger.WARNING,"Kann Kommando f�r Spezial-Button (\""+cmd+"\") nicht ausf�hren: "+ee.toString());
      }
    } else {
      Dialog.error("Kein Kommando f�r diesen Button konfiguriert!");
    }
  }

  void efaButton_actionPerformed(ActionEvent e) {
    alive();
    EfaFrame_AboutBox dlg = new EfaFrame_AboutBox(this);
    Dialog.setDlgLocation(dlg,this);
    dlg.setModal(true);
    dlg.show();
  }

  void hilfeButton_actionPerformed(ActionEvent e) {
    Help.getHelp(this,this.getClass());
  }



  public int checkUnreadMessages() {
    boolean admin = false;
    boolean bootswart = false;

    if (Daten.nachrichten != null) {
      // durchsuche die letzten 50 Nachrichten nach ungelesenen (aus Performancegr�nden immer nur die letzen 50)
      for (int i=Daten.nachrichten.size()-1; i>=0 && i>Daten.nachrichten.size()-50; i--) {
        if (!Daten.nachrichten.get(i).gelesen && Daten.nachrichten.get(i).empfaenger == Nachricht.ADMIN) admin = true;
        if (!Daten.nachrichten.get(i).gelesen && Daten.nachrichten.get(i).empfaenger == Nachricht.BOOTSWART) bootswart = true;
        if (admin && bootswart) return Nachricht.ALLE;
      }
    }
    if (admin) return Nachricht.ADMIN;
    if (bootswart) return Nachricht.BOOTSWART;
    return -1;
  }


  void updateUnreadMessages() {
    try {
    switch(checkUnreadMessages()) {
      case Nachricht.ADMIN:
        adminButton.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/mailAdmin.gif")));
        break;
      case Nachricht.BOOTSWART:
        adminButton.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/mailBootswart.gif")));
        break;
      case Nachricht.ALLE:
        adminButton.setIcon(new ImageIcon(EfaFrame.class.getResource("/de/nmichael/efa/img/mail.gif")));
        break;
      default: adminButton.setIcon(null);
    }
    } catch(Exception e) { EfaUtil.foo(); }
    if (!Daten.efaConfig.efaDirekt_startMaximized) packFrame("updateUnredMessages()");
  }


  void alive() {
    lastUserInteraction = System.currentTimeMillis();
  }


  void bootslist_focusGained(FocusEvent e) {
    JList list = null;
    if (e != null) list = ((JList)e.getSource());
    if (list != null && list.getFirstVisibleIndex()>=0 && list.getSelectedIndex()<0) list.setSelectedIndex(0);
    if (list != null) {
      int nr = 0;
      if (list == this.booteVerfuegbar) nr = 1;
      if (list == this.booteAufFahrt) nr = 2;
      if (list == this.booteNichtVerfuegbar) nr = 3;
      showBootStatus(nr,list,1);
    }
  }


  public void lockEfaAt(TMJ datum, TMJ zeit) {
    efaDirektBackgroundTask.setEfaLockBegin(datum,zeit);
  }

  public void lockEfa() {
    if (Daten.efaConfig == null) return;

    String endeDerSperrung = (Daten.efaConfig.efaDirekt_lockEfaUntilDatum != null ? " Ende der Sperrung: "+
                Daten.efaConfig.efaDirekt_lockEfaUntilDatum.tag+"."+Daten.efaConfig.efaDirekt_lockEfaUntilDatum.monat+"."+Daten.efaConfig.efaDirekt_lockEfaUntilDatum.jahr+
                (Daten.efaConfig.efaDirekt_lockEfaUntilZeit != null ? " "+
                 (Daten.efaConfig.efaDirekt_lockEfaUntilZeit.tag < 10 ? "0" : "") + Daten.efaConfig.efaDirekt_lockEfaUntilZeit.tag+":"+
                 (Daten.efaConfig.efaDirekt_lockEfaUntilZeit.monat < 10 ? "0" : "") + Daten.efaConfig.efaDirekt_lockEfaUntilZeit.monat : "") : "");

    String html = Daten.efaConfig.efaDirekt_lockEfaShowHtml;
    if (html == null || !EfaUtil.canOpenFile(html)) {
      html = Daten.efaTmpDirectory+"locked.html";
      try {
        BufferedWriter f = new BufferedWriter(new FileWriter(html));
        f.write("<html><body><h1 align=\"center\">efa ist f�r die Benutzung gesperrt</h1>\n");
        f.write("<p>efa wurde vom Administrator vor�bergehend f�r die Benutzung gesperrt.</p>\n");
        if (endeDerSperrung.length() > 0) f.write("<p>"+endeDerSperrung+"</p>\n");
        f.write("</body></html>\n");
        f.close();
      } catch(Exception e) {
        EfaUtil.foo();
      }
    }
    BrowserFrame browser = new BrowserFrame(this,
                                            Daten.efaConfig.efaDirekt_lockEfaVollbild,
                                            "file:" + html);
    browser.setModal(true);
    if (Daten.efaConfig.efaDirekt_lockEfaVollbild) {
      browser.setSize(Dialog.screenSize);
    }
    Dialog.setDlgLocation(browser, this);
    browser.setClosingTimeout(10); // nur um Lock-Ende zu �berwachen
    Logger.log(Logger.INFO,"efa wurde f�r die Benutzung gesperrt."+endeDerSperrung);
    Daten.efaConfig.efaDirekt_lockEfaFromDatum = null; // damit nach Entsperren nicht wiederholt gelockt wird
    Daten.efaConfig.efaDirekt_lockEfaFromZeit = null;  // damit nach Entsperren nicht wiederholt gelockt wird
    Daten.efaConfig.efaDirekt_locked = true;
    Daten.efaConfig.writeFile();
    browser.show();
  }

  void setKombiBootStatus(String boot, String lfdnr, int status, String bemerk) {
    if (bootStatus == null) return;

    String org = EfaUtil.syn2org(Daten.synBoote,boot);
    if (org == null || org.equals(boot)) return; // kein Kombiboot

    Vector syn = EfaUtil.org2syn(Daten.synBoote,org);
    if (syn == null) return; // kein Kombiboot

    for (int i=0; i<syn.size(); i++) {
      String s = (String)syn.get(i);
      if (!boot.equals(s)) {
        DatenFelder d = bootStatus.getExactComplete(s);
        if (d != null && EfaUtil.string2int(d.get(BootStatus.STATUS),-1) != BootStatus.STAT_HIDE) {
          d.set(BootStatus.LFDNR,lfdnr);
          d.set(BootStatus.STATUS,Integer.toString(status));
          d.set(BootStatus.BEMERKUNG,bemerk);
          bootStatus.delete(s);
          bootStatus.add(d);
        }
      }
    }
  }

  public void bringFrameToFront() {
    this.toFront();
  }

  public void setBootstatusSchaden(String boot, String s) {
    if (boot == null || s == null || boot.length() == 0 || s.length() == 0) return;

    String org = EfaUtil.syn2org(Daten.synBoote,boot);
    if (org == null) org = boot;
    Vector syn = EfaUtil.org2syn(Daten.synBoote,org);
    if (syn == null) syn = new Vector();
    if (syn.size() == 0) syn.add(org);

    for (int i=0; i<syn.size(); i++) {
      String b = (String)syn.get(i);
      DatenFelder d = bootStatus.getExactComplete(b);
      if (d != null) {
        d.set(BootStatus.BOOTSSCHAEDEN,s);
        bootStatus.delete(d.get(BootStatus.NAME));
        bootStatus.add(d);
      }
    }

    bootStatus.writeFile();
  }

  String makeSureFileDoesntExist(String f) {
    int cnt = 0;
    int punkt = f.lastIndexOf(".");
    String head;
    String tail;
    if (punkt >= 0) {
      head = f.substring(0,punkt);
      tail = f.substring(punkt+1);
    } else {
      head = f;
      tail = "";
    }
    while ((new File(f)).exists()) {
      cnt++;
      f = head + "_" + cnt + "." + tail;
    }
    return f;
  }

  void autoCreateNewFb() {
    String fnameEfb = Daten.efaConfig.efaDirekt_autoNewFb_datei.trim();
    Daten.efaConfig.efaDirekt_autoNewFb_datum = null;
    Daten.efaConfig.efaDirekt_autoNewFb_datei = "";

    fnameEfb = EfaUtil.makeFullPath(EfaUtil.getPathOfFile(Daten.fahrtenbuch.getFileName()),fnameEfb);
    Logger.log(Logger.INFO,"Automatisches Anlegen eines neuen Fahrtenbuchs wird begonnen ...");


    FBDaten fbDaten = null;
    Fahrtenbuch neuesFb = null;
    String oldNextFb = null;
    String oldFnameEfbb = null;
    String oldFnameEfbm = null;
    String oldFnameEfbz = null;
    String oldFnameEfbs = null;

    boolean abgebrocheneFahrten = false;

    int level = 0;
    try {
      if (!fnameEfb.toUpperCase().endsWith(".EFB")) fnameEfb = fnameEfb + ".efb";
      fnameEfb = makeSureFileDoesntExist(fnameEfb);
      String fnameBase = fnameEfb.substring(0,fnameEfb.lastIndexOf("."));
      String fnameEfbb = makeSureFileDoesntExist(fnameBase+".efbb");
      String fnameEfbm = makeSureFileDoesntExist(fnameBase+".efbm");
      String fnameEfbz = makeSureFileDoesntExist(fnameBase+".efbz");
      String fnameEfbs = makeSureFileDoesntExist(fnameBase+".efbs");
      Logger.log(Logger.INFO,"Name f�r neue Fahrtenbuchdatei      : "+fnameEfb);
      Logger.log(Logger.INFO,"Name f�r neue Bootsliste            : "+fnameEfbb);
      Logger.log(Logger.INFO,"Name f�r neue Mitgliederliste       : "+fnameEfbm);
      Logger.log(Logger.INFO,"Name f�r neue Zielliste             : "+fnameEfbz);
      Logger.log(Logger.INFO,"Name f�r neue Statistikeinstellungen: "+fnameEfbs);

      oldFnameEfbb = Daten.fahrtenbuch.getDaten().boote.getFileName();
      oldFnameEfbm = Daten.fahrtenbuch.getDaten().mitglieder.getFileName();
      oldFnameEfbz = Daten.fahrtenbuch.getDaten().ziele.getFileName();
      oldFnameEfbs = Daten.fahrtenbuch.getDaten().statistik.getFileName();
      fbDaten = new FBDaten(Daten.fahrtenbuch.getDaten());
      fbDaten.boote.setFileName(fnameEfbb);
      fbDaten.mitglieder.setFileName(fnameEfbm);
      fbDaten.ziele.setFileName(fnameEfbz);
      fbDaten.statistik.setFileName(fnameEfbs);
      fbDaten.bootDatei = EfaUtil.makeRelativePath(fnameEfbb,fnameEfb);
      fbDaten.mitgliederDatei = EfaUtil.makeRelativePath(fnameEfbm,fnameEfb);
      fbDaten.zieleDatei = EfaUtil.makeRelativePath(fnameEfbz,fnameEfb);
      fbDaten.statistikDatei = EfaUtil.makeRelativePath(fnameEfbs,fnameEfb);

      // Neue Datenlisten erstellen
      level = 1;
      Logger.log(Logger.INFO,"Erstelle neue Datenlisten ...");
      if (!fbDaten.boote.writeFile()) {
        Logger.log(Logger.ERROR,"Neue Bootsliste konnte nicht erstellt werden (Schreiben der Datei schlug fehl).");
        throw new Exception("Level 1");
      }
      if (!fbDaten.mitglieder.writeFile()) {
        Logger.log(Logger.ERROR,"Neue Mitgliederliste konnte nicht erstellt werden (Schreiben der Datei schlug fehl).");
        throw new Exception("Level 1");
      }
      if (!fbDaten.ziele.writeFile()) {
        Logger.log(Logger.ERROR,"Neue Zielliste konnte nicht erstellt werden (Schreiben der Datei schlug fehl).");
        throw new Exception("Level 1");
      }
      if (!fbDaten.statistik.writeFile()) {
        Logger.log(Logger.ERROR,"Neue Statistikeinstellungen konnten nicht erstellt werden (Schreiben der Datei schlug fehl).");
        throw new Exception("Level 1");
      }
      Logger.log(Logger.INFO,"Fertig mit dem Erstellen der Datenlisten.");

      // Neue Fahrtenbuchdatei erstellen
      level = 2;
      Logger.log(Logger.INFO,"Erstelle neues Fahrtenbuch ...");
      neuesFb = new Fahrtenbuch(fnameEfb);
      neuesFb.setDaten(fbDaten);
      neuesFb.setPrevFb(EfaUtil.makeRelativePath(Daten.fahrtenbuch.getFileName(),neuesFb.getFileName()));
      neuesFb.setNextFb("");
      if (!neuesFb.writeFile()) {
        Logger.log(Logger.ERROR,"Neues Fahrtenbuch konnte nicht erstellt werden (Schreiben der Datei schlug fehl).");
        throw new Exception("Level 2");
      }
      Logger.log(Logger.INFO,"Fertig mit dem Erstellen des Fahrtenbuchs.");

      // Fahrten f�r Boote, die noch unterwegs sind, abbrechen
      level = 3;
      Vector unterwegs = bootStatus.getBoote(BootStatus.STAT_UNTERWEGS);
      if (unterwegs.size()>0) {
        abgebrocheneFahrten = true;
        Logger.log(Logger.INFO,"Breche bestehende Fahrten ab ...");
        if (!bootStatus.writeFile()) {
          Logger.log(Logger.ERROR,"Aktueller Bootstatus konnte nicht gesichert werden (Schreiben der Datei schlug fehl).");
          throw new Exception("Level 3");
        }
        for (int i=0; i<unterwegs.size(); i++) {
          fahrtAbbruch((String)unterwegs.get(i),false);
        }
        level = 4;
        if (!bootStatus.writeFile()) {
          Logger.log(Logger.ERROR,"Neuer Bootstatus konnte nicht gesichert werden (Schreiben der Datei schlug fehl).");
          throw new Exception("Level 4");
        }
        Logger.log(Logger.INFO,"Abbrechen der Fahrten beendet.");
      }

      // �nderungen an altem Fahrtenbuch speichern
      Logger.log(Logger.INFO,"Speichere �nderungen an altem Fahrtenbuch ...");
      oldNextFb = Daten.fahrtenbuch.getNextFb(false);
      level = 5;
      Daten.fahrtenbuch.setNextFb(EfaUtil.makeRelativePath(neuesFb.getFileName(),Daten.fahrtenbuch.getFileName()));
      if (!Daten.fahrtenbuch.writeFile()) {
        Logger.log(Logger.ERROR,"�nderungen an altem Fahrtenbuch konnte nicht gesichert werden (Schreiben der Datei schlug fehl).");
        throw new Exception("Level 5");
      }
      Logger.log(Logger.INFO,"�nderungen an Fahrtenbuch gespeichert.");

      level = 6;
      Daten.fahrtenbuch = neuesFb;
      Daten.efaConfig.direkt_letzteDatei = Daten.fahrtenbuch.getFileName();
      Daten.efaConfig.writeFile();

      level = 7;
      Logger.log(Logger.INFO,"Automatisches Anlegen des neuen Fahrtenbuchs erfolgreich abgeschlossen.");
      Logger.log(Logger.INFO,"Aktuelles Fahrtenbuch ist jetzt: "+Daten.fahrtenbuch.getFileName());

      Nachricht n = new Nachricht();
      n.name = "efa";
      n.empfaenger = Nachricht.ADMIN;
      n.betreff = "Neues Fahrtenbuch angelegt";
      n.nachricht = "efa hat soeben wie konfiguriert ein neues Fahrtenbuch angelegt.\n"+
                    "Die neue Fahrtenbuchdatei ist: "+Daten.fahrtenbuch.getFileName()+"\n"+
                    "Der Vorgang wurde ERFOLGREICH abgeschlossen.\n\n"+
                    (abgebrocheneFahrten ? "Zum Zeitpunkt des Fahrtenbuchwechsels befanden sich noch einige Boote\n"+
                                           "auf dem Wasser. Diese Fahrten wurden ABGEBROCHEN. Die abgebrochenen\n"+
                                           "Fahrten sind in der Logdatei verzeichnet.\n\n" : "") +
                    "Ein Protokoll ist in der Logdatei (Admin-Modus: Logdatei anzeigen) zu finden.";
      Daten.nachrichten.add(n);
      Daten.nachrichten.writeFile();

//      this.efaButton.requestFocus();
//      this.booteVerfuegbar.setSelectedIndex(-1);
      EfaUtil.sleep(500);
      updateBootsListen();
      EfaUtil.sleep(500);
      efaDirektBackgroundTask.interrupt();
    } catch(Exception e) {
      Logger.log(Logger.ERROR,"Beim Versuch, ein neues Fahrtenbuch anzulegen, trat ein Fehler auf. Alle �nderungen werden r�ckg�ngig gemacht ...");
      switch (level) {
        case 0: break; // nothing to do
        case 7: break; // nothing to do
        case 6: break; // nothing to do
        case 5: Logger.log(Logger.WARNING,"Rollback von Level 5 ...");
                Daten.fahrtenbuch.setNextFb(oldNextFb);
                Daten.fahrtenbuch.writeFile(); // egal, ob dies fehlschl�gt oder nicht
                Logger.log(Logger.INFO,"Rollback von Level 5 erfolgreich.");
        case 4: Logger.log(Logger.WARNING,"Rollback von Level 4 ...");
                // nothing to do
                Logger.log(Logger.INFO,"Rollback von Level 4 erfolgreich.");
        case 3: Logger.log(Logger.WARNING,"Rollback von Level 3 ...");
                if (!bootStatus.readFile()) {
                  Logger.log(Logger.ERROR,"Rollback von Level 3 fehlgeschlagen (Bootsstatus konnte nicht wiederhergestellt werden).");
                } else {
                  Logger.log(Logger.INFO,"Rollback von Level 3 erfolgreich.");
                }
        case 2: Logger.log(Logger.WARNING,"Rollback von Level 2 ...");
                // nothing to do
                Logger.log(Logger.INFO,"Rollback von Level 2 erfolgreich.");
        case 1: Logger.log(Logger.WARNING,"Rollback von Level 1 ...");
                Daten.fahrtenbuch.getDaten().boote.setFileName(oldFnameEfbb);
                Daten.fahrtenbuch.getDaten().mitglieder.setFileName(oldFnameEfbm);
                Daten.fahrtenbuch.getDaten().ziele.setFileName(oldFnameEfbz);
                Daten.fahrtenbuch.getDaten().statistik.setFileName(oldFnameEfbs);
                int errors = 0;
                if (!Daten.fahrtenbuch.getDaten().boote.writeFile()) {
                  Logger.log(Logger.ERROR,"Schreiben der originalen Bootsliste ("+Daten.fahrtenbuch.getDaten().boote.getFileName()+") fehlgeschlagen.");
                  errors++;
                }
                if (!Daten.fahrtenbuch.getDaten().mitglieder.writeFile()) {
                  Logger.log(Logger.ERROR,"Schreiben der originalen Mitgliederliste ("+Daten.fahrtenbuch.getDaten().mitglieder.getFileName()+") fehlgeschlagen.");
                  errors++;
                }
                if (!Daten.fahrtenbuch.getDaten().ziele.writeFile()) {
                  Logger.log(Logger.ERROR,"Schreiben der originalen Zielliste ("+Daten.fahrtenbuch.getDaten().ziele.getFileName()+") fehlgeschlagen.");
                  errors++;
                }
                if (!Daten.fahrtenbuch.getDaten().statistik.writeFile()) {
                  Logger.log(Logger.ERROR,"Schreiben der originalen Statistikeinstellungen ("+Daten.fahrtenbuch.getDaten().statistik.getFileName()+") fehlgeschlagen.");
                  errors++;
                }
                if (errors == 0) {
                  Logger.log(Logger.INFO,"Rollback von Level 1 erfolgreich.");
                } else {
                  Logger.log(Logger.ERROR,"Rollback von Level 1 mit "+errors+" Fehlern abgeschlossen.");
                }
                break;
        default: Logger.log(Logger.ERROR,"Rollback nicht m�glich: efa kann den Originalzustand nicht wiederherstellen!");
                 Logger.log(Logger.ERROR,"Kritischer Fehler: efa befindet sich in einem undefinierten Zustand! �berpr�fung durch Administrator erforderlich!");
      }
      Nachricht n = new Nachricht();
      n.name = "efa";
      n.empfaenger = Nachricht.ADMIN;
      n.betreff = "FEHLER beim Anlegen eines neuen Fahrtenbuchs";
      n.nachricht = "efa hat soeben versucht, wie konfiguriert ein neues Fahrtenbuch anzulegen.\n"+
                    "Bei diesem Vorgang traten jedoch FEHLER auf.\n\n"+
                    "Ein Protokoll ist in der Logdatei (Admin-Modus: Logdatei anzeigen) zu finden.";
      Daten.nachrichten.add(n);
      Daten.nachrichten.writeFile();

      Daten.efaConfig.writeFile();
      Logger.log(Logger.INFO,"R�ckg�ngigmachen aller �nderungen abgeschlossen.");
    }

  }

  void updateNews() {
    if (efaNewsUpdater != null) {
      efaNewsUpdater.stopRunning();
    }
    efaNewsUpdater = new EfaNewsUpdater(this.newsLabel,Daten.efaConfig.efaDirekt_newsText);
    efaNewsUpdater.start();
    newsLabel.setVisible(Daten.efaConfig.efaDirekt_newsText.length()>0);
  }


  class EfaDirektBackgroundTask extends Thread {
    private static final int CHECK_INTERVAL = 60;
    private static final int ONCE_AN_HOUR = 60;
    EfaDirektFrame efaDirektFrame;
    int onceAnHour;
    Date date;
    Calendar cal;
    Calendar lockEfa;
    boolean framePacked;

    public EfaDirektBackgroundTask(EfaDirektFrame efaDirektFrame) {
      this.efaDirektFrame = efaDirektFrame;
      this.onceAnHour = 5; // initial nach 5 Schleifendurchl�ufen zum ersten Mal hier reingehen
      this.cal = new GregorianCalendar();
      this.lockEfa = null;
      this.date = new Date();
      this.framePacked = false;
    }

    private void lockEfaThread() {
      new Thread() {
        public void run() {
          efaDirektFrame.lockEfa();
        }
      }.start();
    }

    public void setEfaLockBegin(TMJ datum, TMJ zeit) {
      if (datum == null) {
        lockEfa = null;
      } else {
        if (zeit != null) lockEfa = new GregorianCalendar(datum.jahr,datum.monat-1,datum.tag,zeit.tag,zeit.monat);
        else lockEfa = new GregorianCalendar(datum.jahr,datum.monat-1,datum.tag);
      }
    }

    private void mailWarnings() {
      try {
        BufferedReader f = new BufferedReader(new FileReader(Daten.efaLogfile));
        String s;
        Vector warnings = new Vector();
        while ( (s = f.readLine()) != null) {
          if (Logger.isWarningLine(s) && Logger.getLineTimestamp(s) > Daten.efaConfig.efaDirekt_bnrWarning_lasttime) {
            warnings.add(s);
          }
        }
        f.close();
        if (warnings.size() == 0) {
          Logger.log(Logger.INFO,"Seit "+EfaUtil.getTimeStamp(Daten.efaConfig.efaDirekt_bnrWarning_lasttime)+" sind keinerlei Warnungen in efa verzeichnet worden.");
        } else {
          Logger.log(Logger.INFO,"Seit "+EfaUtil.getTimeStamp(Daten.efaConfig.efaDirekt_bnrWarning_lasttime)+" sind "+warnings.size()+" Warnungen in efa verzeichnet worden.");
          String txt = "Folgende Warnungen sind seit "+EfaUtil.getTimeStamp(Daten.efaConfig.efaDirekt_bnrWarning_lasttime)+" in efa verzeichnet worden:\n"+
                       warnings.size()+" Warnungen\n\n";
          for (int i=0; i<warnings.size(); i++) {
            txt += ((String)warnings.get(i)) + "\n";
          }
          if (Daten.nachrichten != null && Daten.efaConfig != null) {
            if (Daten.efaConfig.efaDirekt_bnrWarning_admin) {
              Daten.nachrichten.createNachricht("efa", Nachricht.ADMIN,"Warnungen", txt);
            }
            if (Daten.efaConfig.efaDirekt_bnrWarning_bootswart) {
              Daten.nachrichten.createNachricht("efa", Nachricht.BOOTSWART,"Warnungen", txt);
            }
          }
        }
        if (Daten.efaConfig != null) {
          Daten.efaConfig.efaDirekt_bnrWarning_lasttime = System.currentTimeMillis();
          Daten.efaConfig.writeFile();
        }

      } catch(Exception e) {
        Logger.log(Logger.ERROR,"Benachrichtigung �ber WARNING's im Logfile ist fehlgeschlagen: "+e.toString());
      }
    }

    public void run() {
      // Diese Schleife l�uft i.d.R. einmal pro Minute
      while(true) {

        // Reservierungs-Checker
        if (Dialog.frameCurrent() == efaDirektFrame // aktueller Frame ist EfaDirektFrame!
            && bootStatus != null) {
          boolean changes = false;
          DatenFelder d;
          for (d = bootStatus.getCompleteFirst(); d != null; d = bootStatus.getCompleteNext()) {
            // pr�fen, ob f�r dieses Boot Reservierungen m�glich sind
            if (EfaUtil.string2int(d.get(BootStatus.STATUS),-1) == BootStatus.STAT_HIDE) continue;
            if (d.get(BootStatus.UNBEKANNTESBOOT).equals("+")) continue;

            // derzeit g�ltige Reservierungen finden
            Reservierung reservierung = BootStatus.getReservierung(d,System.currentTimeMillis(),0);

            // verfallene Reservierungen l�schen
            if (BootStatus.deleteObsoleteReservierungen(d)) {
              // Ok, alte Reservierungen wurden gel�scht: Jetzt pr�fen, ob das Boot zur Zeit reserviert
              // ist. Falls ja, mu� es verf�gbar gemacht werden, damit ggf. neue Reservierungen zum Tragen
              // kommen k�nnen.
              if ( (EfaUtil.string2int(d.get(BootStatus.STATUS),-1) == BootStatus.STAT_VERFUEGBAR ||
                    EfaUtil.string2int(d.get(BootStatus.STATUS),-1) == BootStatus.STAT_NICHT_VERFUEGBAR) &&
                  d.get(BootStatus.LFDNR).equals(BootStatus.RES_LFDNR)) {
                d.set(BootStatus.STATUS,Integer.toString(BootStatus.STAT_VERFUEGBAR));
                d.set(BootStatus.BEMERKUNG,BootStatus.STATUSNAMES[BootStatus.STAT_VERFUEGBAR]);
                d.set(BootStatus.LFDNR,"");
                Logger.log(Logger.INFO,"ReservierungsChecker: Boot '"+d.get(BootStatus.NAME)+"' auf '"+
                                       BootStatus.STATUSNAMES[BootStatus.STAT_VERFUEGBAR]+
                                       "' gesetzt: Alte Reservierungen gel�scht.");
              }
              changes = true;
            }

            if (reservierung != null) {
              // Reservierung liegt vor: Jetzt pr�fen, ob das Boot zur Zeit *nicht* reserviert ist; nur
              // in diesem Fall kommt die gefundene Reservierung zum Tragen
              if (EfaUtil.string2int(d.get(BootStatus.STATUS),-1) == BootStatus.STAT_VERFUEGBAR &&
                  !d.get(BootStatus.LFDNR).equals(BootStatus.RES_LFDNR)) {
                if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_resBooteNichtVerfuegbar) {
                  d.set(BootStatus.STATUS,Integer.toString(BootStatus.STAT_NICHT_VERFUEGBAR));
                }
                d.set(BootStatus.BEMERKUNG,"reserviert f�r "+reservierung.name+" ("+reservierung.grund+") "+BootStatus.makeReservierungText(reservierung));
                d.set(BootStatus.LFDNR,BootStatus.RES_LFDNR); // Kennzeichnung daf�r, da� es sich um eine *Reservierung* handelt (und nicht Sperrung des Bootes o.�.)
                Logger.log(Logger.INFO,"ReservierungsChecker: F�r Boot '"+d.get(BootStatus.NAME)+"' wurde eine Reservierung gefunden "+
                                       "(neuer Status: "+BootStatus.STATUSNAMES[EfaUtil.string2int(d.get(BootStatus.STATUS),0)]+"): "+
                                       d.get(BootStatus.BEMERKUNG));
                changes = true;
              }
            } else {
              // Reservierung liegt nicht vor: Jetzt pr�fen, ob das Boot zur Zeit reserviert ist; nur
              // in diesem Fall wird die aktuelle Reservierung gel�scht
              if ( (EfaUtil.string2int(d.get(BootStatus.STATUS),-1) == BootStatus.STAT_VERFUEGBAR ||
                    EfaUtil.string2int(d.get(BootStatus.STATUS),-1) == BootStatus.STAT_NICHT_VERFUEGBAR) &&
                  d.get(BootStatus.LFDNR).equals(BootStatus.RES_LFDNR)) {
                d.set(BootStatus.STATUS,Integer.toString(BootStatus.STAT_VERFUEGBAR));
                d.set(BootStatus.BEMERKUNG,BootStatus.STATUSNAMES[BootStatus.STAT_VERFUEGBAR]);
                d.set(BootStatus.LFDNR,"");
                Logger.log(Logger.INFO,"ReservierungsChecker: Boot '"+d.get(BootStatus.NAME)+"' auf '"+
                                       BootStatus.STATUSNAMES[BootStatus.STAT_VERFUEGBAR]+
                                       "' gesetzt: Reservierungszeitraum beendet.");
                changes = true;
              }
            }
          } // end: for all boats
          if (changes) {
            if (!bootStatus.writeFile()) {
              Logger.log(Logger.ERROR,"ReservierungsChecker kann die Bootsstatus-Datei '"+bootStatus.getFileName()+"' nicht schreiben.");
            }
            efaDirektFrame.updateBootsListen();
          }
        }

        // Nach ungelesenen Nachrichten f�r den Admin suchen
        updateUnreadMessages();

        // automatisches, zeitgesteuertes Beenden von efa ?
        if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_exitTime != null && Daten.efaConfig.efaDirekt_exitTime.tag>=0
            && System.currentTimeMillis() > Daten.efaStartTime + (Daten.AUTO_EXIT_MIN_RUNTIME+1)*60*1000
            ) {
          date.setTime(System.currentTimeMillis());
          cal.setTime(date);
          int now = cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE);
          int exitTime = Daten.efaConfig.efaDirekt_exitTime.tag*60 + Daten.efaConfig.efaDirekt_exitTime.monat;
          if ( (now >= exitTime && now < exitTime+Daten.AUTO_EXIT_MIN_RUNTIME) || (now+(24*60) >= exitTime && now+(24*60) < exitTime+Daten.AUTO_EXIT_MIN_RUNTIME) ) {
            Logger.log(Logger.INFO,"Eingestellte Uhrzeit zum Beenden von efa erreicht!");
            if (System.currentTimeMillis() - efaDirektFrame.lastUserInteraction < Daten.AUTO_EXIT_MIN_LAST_USED*60*1000) {
              Logger.log(Logger.INFO,"Beenden von efa wird verz�gert, da efa innerhalb der letzten "+Daten.AUTO_EXIT_MIN_LAST_USED+" Minuten noch benutzt wurde ...");
            } else {
              EfaExitFrame.exitEfa("Zeitgesteuertes Beenden von efa",false,EFA_EXIT_REASON_TIME);
            }
          }
        }

        // automatischer, zeitgesteuerter Neustart von efa ?
        if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_restartTime != null && Daten.efaConfig.efaDirekt_restartTime.tag>=0
            && System.currentTimeMillis() > Daten.efaStartTime + (Daten.AUTO_EXIT_MIN_RUNTIME +1)*60*1000
            ) {
          date.setTime(System.currentTimeMillis());
          cal.setTime(date);
          int now = cal.get(Calendar.HOUR_OF_DAY)*60 + cal.get(Calendar.MINUTE);
          int restartTime = Daten.efaConfig.efaDirekt_restartTime.tag*60 + Daten.efaConfig.efaDirekt_restartTime.monat;
          if ( (now >= restartTime && now < restartTime+Daten.AUTO_EXIT_MIN_RUNTIME) || (now+(24*60) >= restartTime && now+(24*60) < restartTime+Daten.AUTO_EXIT_MIN_RUNTIME) ) {
            Logger.log(Logger.INFO,"Automatischer Neustart von efa (einmal t�glich).");
            if (System.currentTimeMillis() - efaDirektFrame.lastUserInteraction < Daten.AUTO_EXIT_MIN_LAST_USED*60*1000) {
              Logger.log(Logger.INFO,"Neustart von efa wird verz�gert, da efa innerhalb der letzten "+Daten.AUTO_EXIT_MIN_LAST_USED+" Minuten noch benutzt wurde ...");
            } else {
              EfaExitFrame.exitEfa("Automatischer Neustart von efa",true,EFA_EXIT_REASON_AUTORESTART);
            }
          }
        }

        // efa zeitgesteuert sperren
        if (lockEfa != null) {
          date.setTime(System.currentTimeMillis());
          cal.setTime(date);
          if (cal.after(lockEfa)) {
            lockEfaThread();
            lockEfa = null;
          }
        }

        // automatisches Beginnen eines neuen Fahrtenbuchs (z.B. zum Jahreswechsel)
        if (Daten.applMode == Daten.APPL_MODE_NORMAL &&
            Daten.efaConfig != null && Daten.efaConfig.efaDirekt_autoNewFb_datum != null &&
            Daten.efaConfig.efaDirekt_autoNewFb_datei.length() > 0) {
          if (EfaUtil.secondDateIsEqualOrAfterFirst(EfaUtil.tmj2datestring(Daten.efaConfig.efaDirekt_autoNewFb_datum),EfaUtil.getCurrentTimeStampDD_MM_YYYY())) {
            efaDirektFrame.autoCreateNewFb();
          }
        }

        // immer im Vordergrund
        if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_immerImVordergrund && this.efaDirektFrame != null &&
            Dialog.frameCurrent() == this.efaDirektFrame) {
          Window[] windows = this.efaDirektFrame.getOwnedWindows();
          boolean topWindow = true;
          if (windows != null) {
            for (int i=0; i<windows.length; i++) {
              if (windows[i] != null && windows[i].isVisible()) topWindow = false;
            }
          }
          if (topWindow && Daten.efaConfig.efaDirekt_immerImVordergrundBringToFront) {
            this.efaDirektFrame.bringFrameToFront();
          }
        }

        // Fokus-Kontrolle
        if (this.efaDirektFrame != null && this.efaDirektFrame.getFocusOwner() == this.efaDirektFrame) {
          // das Frame selbst hat den Fokus: Das soll nicht sein! Gib einer Liste den Fokus!
          if (this.efaDirektFrame.booteVerfuegbar != null && this.efaDirektFrame.booteVerfuegbar.getSelectedIndex()>=0) this.efaDirektFrame.booteVerfuegbar.requestFocus();
          else if (this.efaDirektFrame.booteAufFahrt != null && this.efaDirektFrame.booteAufFahrt.getSelectedIndex()>=0) this.efaDirektFrame.booteAufFahrt.requestFocus();
          else if (this.efaDirektFrame.booteNichtVerfuegbar != null && this.efaDirektFrame.booteNichtVerfuegbar.getSelectedIndex()>=0) this.efaDirektFrame.booteNichtVerfuegbar.requestFocus();
          else if (this.efaDirektFrame.booteVerfuegbar != null) this.efaDirektFrame.booteVerfuegbar.requestFocus();
        }

        // Aktivit�ten einmal pro Stunde
        if (--onceAnHour <= 0) {
          System.gc(); // Damit Speicher�berwachung funktioniert (anderenfalls wird CollectionUsage nicht aktualisiert; Java-Bug)
          onceAnHour = ONCE_AN_HOUR;
          Logger.log(Logger.DEBUG,"EfaDirektBackgroundTask: alive!");

          // WARNINGs aus Logfile an Admins verschicken
          if (Daten.efaConfig != null && System.currentTimeMillis() >= Daten.efaConfig.efaDirekt_bnrWarning_lasttime + 7l*24l*60l*60l*1000l &&
              (Daten.efaConfig.efaDirekt_bnrWarning_admin || Daten.efaConfig.efaDirekt_bnrWarning_bootswart) && Daten.efaLogfile != null) {
            mailWarnings();
          }
        }

        // Speicher-�berwachung
        try {
//          System.gc(); // !!! ONLY ENABLE FOR DEBUGGING PURPOSES !!!
          if (de.nmichael.efa.java15.Java15.isMemoryLow(Daten.MIN_FREEMEM_PERCENTAGE,Daten.WARN_FREEMEM_PERCENTAGE)) {
            efaDirektFrame.exitOnLowMemory("EfaDirektBackgroundTask:MemoryLow",false);
          }
        } catch(UnsupportedClassVersionError e) {
          EfaUtil.foo();
        } catch(NoClassDefFoundError e) {
          EfaUtil.foo();
        }

        try {
          Thread.sleep(CHECK_INTERVAL * 1000);
        } catch(Exception e) {
          // wenn unterbrochen, dann versuch nochmal, 2 Sekunden zu schlafen, und arbeite dann weiter!! ;-)
          try {
            Thread.sleep(2 * 1000);
          } catch(Exception ee) { EfaUtil.foo(); }

          // Bugfix, da efa unter manchen Versionen beim Start nicht richtig gepackt wird.
          if (!framePacked) {
            if (/*Daten.javaVersion.startsWith("1.3")  && */ efaDirektFrame != null) {
              if (Daten.efaConfig != null) {
                if (!Daten.efaConfig.efaDirekt_startMaximized) efaDirektFrame.packFrame("EfaDirektBackgroundTask");
                else {
                  if (efaDirektFrame.jScrollPane1 != null && efaDirektFrame.westPanel != null && efaDirektFrame.contentPane != null) {
                    efaDirektFrame.jScrollPane1.setSize(efaDirektFrame.jScrollPane1.getPreferredSize());
                    efaDirektFrame.westPanel.validate();
                    efaDirektFrame.contentPane.validate();
                  }
                }
              }
            }
            if (efaDirektFrame != null && efaDirektFrame.efaFrame != null) {
              efaDirektFrame.efaFrame.packFrame("EfaDirektBackgroundTask.run()");
            }
            framePacked = true; // nicht nochmal machen, sondern nur einmal beim Start
          }

        }
      } // end: while(true)
    } // end: run
  }


  class EfaUhrUpdater extends Thread {
    JLabel uhr;
    JLabel sunrise;
    JLabel sunset;
    boolean updateSunrise;
    int sunriseUpdated;

    public EfaUhrUpdater(JLabel uhr, JLabel sunrise, JLabel sunset, boolean updateSunrise) {
      this.uhr = uhr;
      this.sunrise = sunrise;
      this.sunset = sunset;
      this.updateSunrise = updateSunrise;
      this.sunriseUpdated = -1;
    }

    private TMJ getTime() {
      GregorianCalendar cal = new GregorianCalendar();
      cal.setTime(new Date(System.currentTimeMillis()));
      return new TMJ(cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),cal.get(Calendar.SECOND));
    }

    private String twoDigits(int i) {
      if (i<10) return "0"+i;
      else return Integer.toString(i);
    }

    private String getTimeString(TMJ tmj) {
      return twoDigits(tmj.tag)+":"+twoDigits(tmj.monat);
    }

    public void updateSunriseNow() {
      try {
        String sun[] = SunRiseSet.getSunRiseSet();
        sunrise.setText(sun[0]);
        sunset.setText(sun[1]);
      } catch (NoClassDefFoundError e) {
        sunrise.setText("--:--");
        sunset.setText("--:--");
      } catch (Exception ee) {
        sunrise.setText("--:--");
        sunset.setText("--:--");
      }
      sunriseUpdated = new GregorianCalendar().get(Calendar.HOUR_OF_DAY);
    }

    public void run() {
      uhr.setText(getTimeString(getTime()));
      if (updateSunrise) updateSunriseNow();
      while (getTime().jahr != 1) {
        try { Thread.sleep(900); } catch(Exception e) { EfaUtil.foo(); }
      }
      while(true) {
        TMJ hhmmss = getTime();
        uhr.setText(getTimeString(hhmmss));
        if (updateSunrise && (sunriseUpdated == -1 || (hhmmss.tag==0 && hhmmss.monat<=1 ))) updateSunriseNow();
        try {
          Thread.sleep(60000);
        } catch(Exception e) {
          EfaUtil.foo();
        }
      }
    } // end: run
  }

  class EfaNewsUpdater extends Thread {
    private static final int MAX = 20;
    JLabel news;
    String text;
    int showing;
    int length;
    volatile boolean running;

    public EfaNewsUpdater(JLabel news, String text) {
      this.news = news;
      this.text = text;
      this.length = text.length();
      this.showing = 0;
      this.running = true;
    }

    private String getText(String s, int pos) {
      String t;
      if (pos == length+2) {
        t = " ";
      } else if (pos == length+1) {
        t = "  ";
      } else if (pos == length) {
        t = "   ";
      } else {
        t = s.substring(pos, Math.min(pos + MAX, length));
      }
      int l = t.length();
      if (l+3 < MAX) {
        t = t + (pos < length ? "   " : "") + s.substring(0,MAX-l-3);
      }
//      System.out.println(">>"+t+"<< (pos: "+pos+"; length: "+t.length()+")");
      return t;
    }

    public void stopRunning() {
      this.running = false;
    }

    public void run() {
      if (length <= MAX) {
        news.setText(text);
        return;
      }
      while(running) {
        try {
          news.setText(getText(text,showing));
          showing = (showing + 1) % (length+3);
          Thread.sleep(250);
        } catch(Exception e) {
          EfaUtil.foo();
        }
      }
    } // end: run
  }

}