/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa;

import de.nmichael.efa.core.config.EfaBaseConfig;
import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.core.WettDefs;
import de.nmichael.efa.core.Gruppen;
import de.nmichael.efa.core.Fahrtenabzeichen;
import de.nmichael.efa.core.VereinsConfig;
import de.nmichael.efa.core.Synonyme;
import de.nmichael.efa.core.EfaConfig;
import de.nmichael.efa.core.Fahrtenbuch;
import de.nmichael.efa.core.Mannschaften;
import de.nmichael.efa.core.Adressen;
import de.nmichael.efa.core.EfaRunning;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.util.EfaSec;
import de.nmichael.efa.util.TMJ;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.LogString;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.EfaKeyStore;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.Backup;
import de.nmichael.efa.util.HtmlFactory;
import de.nmichael.efa.drv.DRVConfig;
import de.nmichael.efa.statistics.FTPWriter;
import de.nmichael.efa.statistics.PDFWriter;
import de.nmichael.efa.statistics.XMLWriter;
import java.io.*;
import java.util.jar.*;
import java.util.*;
import java.awt.*;
import javax.swing.UIManager;
import java.lang.management.*;

// @i18n complete

public class Daten {


  public       static String EFA_SHORTNAME = "efa";                              // dummy, will be set in International.ininitalize()
  public       static String EFA_LONGNAME  = "efa - elektronisches Fahrtenbuch"; // dummy, will be set in International.ininitalize()

  public final static String VERSION = "v2.0_dev01"; // Version für die Ausgabe (i.d.R. gleich VERSIONID, kann aber auch Zusätze wie "alpha" o.ä. enthalten)
  public final static String VERSIONID = "1.9.0_00";   // VersionsID: Format: "X.Y.Z_MM"; final-Version z.B. 1.4.0_00; beta-Version z.B. 1.4.0_#1
  public final static String VERSIONRELEASEDATE = "17.01.2010";  // Release Date: TT.MM.JJJJ
  public final static String PROGRAMMID = "EFA.190"; // Versions-ID für Wettbewerbsmeldungen
  public final static String PROGRAMMID_DRV = "EFADRV.190"; // Versions-ID für Wettbewerbsmeldungen
  public final static String COPYRIGHTYEAR = "10";   // aktuelles Jahr (Copyright (c) 2001-COPYRIGHTYEAR)

  public final static String EMIL_VERSION = VERSION; // Version
  public final static String EMIL_KENNUNG = "EMIL.190";
  public final static String ELWIZ_VERSION = VERSION; // Version
  public final static String EDDI_VERSION = VERSION; // Version

  public final static String EFA_JAVA_ARGUMENTS = "EFA_JAVA_ARGUMENTS"; // Environment Variable Name containing all arguments passed to the "java" command
  public       static String efa_java_arguments = null;                 // Environment Variable Contents containing all arguments passed to the "java" command
  public final static String EFADIREKT_MAINCLASS = "de.nmichael.efa.direkt.Main";

  public final static String EFAURL = "http://efa.nmichael.de";
  public final static String EFADEVURL = "http://kenai.com/projects/efa";
  public final static String EFAWETTURL = "http://efa.rudern.de";
  public final static String NICOLASURL = "http://www.nmichael.de";
  public final static String EFAEMAIL = "software@nmichael.de";
  public final static String EFAEMAILNAME = "Nicolas Michael";

  public final static String CONFIGFILE = "efa.cfg";                // ./cfg/efa.cfg            Konfigurationsdatei
  public final static String DRVCONFIGFILE = "drv.cfg";             // ./cfg/drv.cfg            DRV-Konfigurationsdatei
  public static final String WETTFILE = "wett.cfg";                 // ./cfg/wett.cfg           Konfiguration für Wettbewerbe
  public static final String EFATYPESFILE = "types.cfg";            // ./cfg/types.cfg          Konfiguration für EfaTypes (Bezeichnungen)
  public final static String VEREINSCONFIG = "verein.efv";          // ./daten/verein.efv       Konfigurationsdatei für Vereinseinstellungen
  public static final String ADRESSENFILE = "adressen.efd";         // ./daten/adressen.efd     gespeicherte Adressen von Teilnehmern
  public static final String MITGLIEDER_SYNONYM = "mitglieder.efs"; // ./daten/mitglieder.efs   Synonymdatei
  public static final String BOOTE_SYNONYM = "boote.efs";           // ./daten/boote.efs        Synonymdatei
  public static final String ZIELE_SYNONYM = "ziele.efs";           // ./daten/ziele.efs        Synonymdatei
  public static final String MANNSCHAFTENFILE = "mannschaften.efm"; // ./daten/mannschaften.efm Standardmannschaften
  public static final String FAHRTENABZEICHEN ="fahrtenabzeichen.eff"; // ./daten/fahrtenabzeichen.eff DRV Fahrtenabzeichen
  public static final String GRUPPEN ="gruppen.efg";                // ./daten/gruppen.efg      Gruppendatei
  public static final String WETTDEFS = "wettdefs.cfg";             // ./cfg/wettdefs.cfg       Wettbewerbs-Definitionen
  public static final String EFA_LICENSE = "license.html";          // ./doc/license.html
  public static final String PUBKEYSTORE = "keystore_pub.dat";      // ./daten/keystore_pub.dat
  public final static String DIREKTBOOTSTATUS = "bootstatus.efdb";  // ./daten/bootstatus.efdb  Status der Boote
  public final static String DIREKTNACHRICHTEN= "nachrichten.efdn"; // ./daten/nachrichten.efdn Nachrichten an Admin
  public static final String EFA_SECFILE = "efa.sec";               // ./program/efa.sec        Hash von efa.jar: für Erstellen des Admins
  public static final String EFA_RUNNUNG = "efa.run";               // ./program/efa.run        Indiz, daß efaDirekt läuft (enthält Port#)

  public static final String CLUBLOGO = "clublogo.gif";             // ./program/clublogo.gif   Clublogo für efaDirekt

  // efa exit codes
  public static final int HALT_BASICCONFIG  =   1;
  public static final int HALT_DIRECTORIES  =   2;
  public static final int HALT_EFACONFIG    =   3;
  public static final int HALT_EFATYPES     =   4;
  public static final int HALT_EFASEC       =   5;
  public static final int HALT_EFARUNNING   =   6;
  public static final int HALT_FILEOPEN     =   7;
  public static final int HALT_EFASECADMIN  =   8;
  public static final int HALT_FILEERROR    =   9;
  public static final int HALT_ERROR        =  10;
  public static final int HALT_INSTALLATION =  11;
  public static final int HALT_ADMIN        =  12;
  public static final int HALT_MISCONFIG    =  12;
  public static final int HALT_PANIC        =  13;
  public static final int HALT_SHELLRESTART =  99;
  public static final int HALT_JAVARESTART  = 199;

  public static Program program = null;            // this Program
  public static String efaUserHome = null;         // User Home Directory
  public static String efaLogfile = null;          // Logdatei für efa-Konsole
  public static String efaMainDirectory = null;    // Efa-Hauptverzeichnis, immer mit "/" am Ende
  public static String efaProgramDirectory = null; // Programmverzeichnis, immer mit "/" am Ende     ("./program/")
  public static String efaPluginDirectory = null;  // Programmverzeichnis, immer mit "/" am Ende     ("./program/plugins")
  public static String efaDataDirectory = null;    // Efa-Datenverzeichnis, immer mit "/" am Ende    ("./data/")
  public static String efaLogDirectory = null;     // Efa-Log-Verzeichnis, immer mit "/" am Ende     ("./log/")
  public static String efaCfgDirectory = null;     // Efa-Configverzeichnis, immer mit "/" am Ende   ("./cfg/")
  public static String efaDocDirectory = null;     // Efa-Doku-Verzeichnis,  immer mit "/" am Ende   ("./doc/")
  public static String efaAusgabeDirectory = null; // Efa-Ausgabe-Verzeichnis, immer mit "/" am Ende ("./fmt/")
  public static String efaBakDirectory = null;     // Efa-Backupverzeichnis, immer mit "/" am Ende   ("./backup/")
  public static String efaTmpDirectory = null;     // Efa-Tempverzeichnis,   immer mit "/" am Ende   ("./tmp/")
  public static String efaStyleDirectory = null;   // Efa-Stylesheetverzeichnis,   mit "/" am Ende   ("./fmt/layout/")
  public static String fileSep = "/"; // Verzeichnis-Separator (wird in ini() ermittelt)
  public static String javaVersion = "";
  public static String jvmVersion = "";
  public static String osName = "";
  public static String osVersion = "";

  public static final int ZIELFAHRTKM = 200; // nötige Kilometer für eine Zielfahrt (in 100m)
  public static final int WAFAKM = 300;      // nötige Kilometer für eine Eintages-DRV-Wanderfahrt (in 100m)
  public static final int FART_TRAINING = 1;
  public static final int FART_REGATTA = 2;

  public final static String PLUGIN_WWW_URL = "http://efa.nmichael.de/plugins/plugins.url"; // in dieser Datei muß eine gültige Plugin-Download-URL stehen!
  public static String pluginWWWdirectory = "http://efa.nmichael.de/plugins/"; // wird automatisch auf das in der o.g. Datei stehende gesetzt
  public final static String PLUGIN_JAXP_NAME = "JAXP-Plugin";
  public final static String PLUGIN_JAXP_FILE = "jaxp.plugin";
  public final static String PLUGIN_JAXP_HTML = "jaxp.html";
  public final static String PLUGIN_FOP_NAME = "FOP-Plugin";
  public final static String PLUGIN_FOP_FILE = "fop.plugin";
  public final static String PLUGIN_FOP_HTML = "fop.html";
  public final static String PLUGIN_FTP_NAME = "FTP-Plugin";
  public final static String PLUGIN_FTP_FILE = "ftp.plugin";
  public final static String PLUGIN_FTP_HTML = "ftp.html";
  public final static String PLUGIN_EMAIL_NAME = "EMAIL-Plugin";
  public final static String PLUGIN_EMAIL_FILE = "email.plugin";
  public final static String PLUGIN_EMAIL_HTML = "email.html";
  public final static String PLUGIN_JSUNTIMES_NAME = "JSUNTIMES-Plugin";
  public final static String PLUGIN_JSUNTIMES_FILE = "jsuntimes.plugin";
  public final static String PLUGIN_JSUNTIMES_HTML = "jsuntimes.html";

  public final static String ONLINEUPDATE_INFO = "http://efa.nmichael.de/efa2.eou";
  public final static String ONLINEUPDATE_INFO_DRV = "http://efa.nmichael.de/efadrv.eou";
  public final static String EFW_UPDATE_DATA = "http://efa.nmichael.de/efw.data";
  public final static String INTERNET_EFAMAIL = "http://cgi.snafu.de/nmichael/user-cgi-bin/efamail.pl";

  public final static int AUTO_EXIT_MIN_RUNTIME = 60; // Minuten, die efa mindestens gelaufen sein muß, damit es zu einem automatischen Beenden/Restart kommt (60)
  public final static int AUTO_EXIT_MIN_LAST_USED = 5; // Minuten, die efa mindestens nicht benutzt wurde, damit Beenden/Neustart nicht verzögert wird (muß kleiner als AUTO_EXIT_MIN_RUNTIME sein!!!) (5)
  public final static int WINDOWCLOSINGTIMEOUT = 600; // Timeout in Sekunden, nach denen im Direkt-Modus manche Fenster automatisch geschlossen werden

  public final static int MIN_FREEMEM_PERCENTAGE = 90;
  public final static int WARN_FREEMEM_PERCENTAGE = 70;
  public final static int MIN_FREEMEM_COLLECTION_THRESHOLD = 99;
  public static boolean DONT_SAVE_ANY_FILES_DUE_TO_OOME = false;
  public static boolean javaRestart = false;

  public static EfaBaseConfig efaBaseConfig; // efa Base Config
  public static EfaConfig efaConfig;         // Konfigurationsdatei
  public static DRVConfig drvConfig;         // Konfigurationsdatei
  public static EfaTypes efaTypes;           // EfaTypes (Bezeichnungen)
  public static VereinsConfig vereinsConfig; // Konfigurationsdatei für Vereinseinstellungen
  public static Adressen adressen;           // gespeicherte Teilnehmer-Adressen
  public static Synonyme synMitglieder;      // Synonymliste für Mitglieder
  public static Synonyme synBoote;           // Synonymliste für Boote
  public static Synonyme synZiele;           // Synonymliste für Ziele
  public static Mannschaften mannschaften;   // Standardmannschaften
  public static Fahrtenbuch fahrtenbuch;     // Fahrtenbuch
  public static Fahrtenabzeichen fahrtenabzeichen; // DRV Fahrtenabzeichen
  public static Gruppen gruppen;             // Gruppen
  public static WettDefs wettDefs;           // WettDefs
  public static de.nmichael.efa.direkt.NachrichtenAnAdmin nachrichten; // Nachrichten an Admin
  public static EfaKeyStore keyStore;        // KeyStore
  public static EfaSec efaSec;               // efa Security File
  public static EfaRunning efaRunning;       // efa Running (Doppelstarts verhindern)
  private static StartLogo splashScreen;     // Efa Splash Screen

  public static String dateiHTML = "";
  public static String dateiTXT = "";

  public static Color colorGreen = new Color(0,150,0);
  public static Color colorOrange = new Color(255,100,0);


  public static String defaultWriteProtectPw = null;

  // move as static variables to SuchFrame!
  public static final int SUCH_NORMAL = 1;
  public static final int SUCH_ERROR = 2;
  public static int suchMode = SUCH_NORMAL;
  public static String such = "";
  public static boolean such_lfdnr=true, such_datum=true, such_stm=true, such_mannsch=true,
                        such_boot=true, such_ziel=true, such_abfahrt=true, such_ankunft=true,
                        such_bootskm=true, such_mannschkm=true, such_bemerk=true, such_fahrtart=true;
  public static boolean such_errUnvollst=true, such_errKm=true, such_errUnbekRuderer=true,
                        such_errUnbekRudererOhneGast=false,
                        such_errUnbekBoot=true, such_errUnbekZiel=true, such_errWafa=true, such_errZielfahrten=true,
                        such_errVieleKm=false,
                        such_errNichtZurueckgetragen=true,such_errNichtKonfMTours=true;
  public static int     such_errVieleKmKm = 0;

  public static Backup backup=null;
  public static long efaStartTime;

  public static boolean exceptionTest = false; // Exceptions beim Drücken von F1 produzieren (für Exception-Test)
  public static boolean watchWindowStack  = false; // Window-Stack überwachen

  // Verhalten, wenn Checksumme nicht stimmt
  public static final int CHECKSUM_LOAD_NO_ACTION = 0;
  public static final int CHECKSUM_LOAD_PRINT_WARNING = 1;
  public static final int CHECKSUM_LOAD_SHOW_WARNING = 2;
  public static final int CHECKSUM_LOAD_REQUIRE_ADMIN = 3;
  public static final int CHECKSUM_LOAD_PRINT_WARNING_AND_AUTO_REWRITE = 4;
  public static final int CHECKSUM_LOAD_HALT_PROGRAM = 5;
  public static int actionOnChecksumLoadError = CHECKSUM_LOAD_PRINT_WARNING_AND_AUTO_REWRITE;
  public static final int CHECKSUM_SAVE_PRINT_ERROR = 0;
  public static final int CHECKSUM_SAVE_HALT_PROGRAM = 1;
  public static final int CHECKSUM_SAVE_NO_ACTION = 2;
  public static int actionOnChecksumSaveError = CHECKSUM_SAVE_PRINT_ERROR;

  // Verhalten, wenn beim Öffnen einer Datenliste diese nicht existiert
  public static final int DATENLISTE_FRAGE_NUTZER = 0;
  public static final int DATENLISTE_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN = 1;
  public static final int DATENLISTE_FRAGE_REQUIRE_ADMIN_RETURN_FALSE_ON_NEIN = 2;
  public static int actionOnDatenlisteNotFound = DATENLISTE_FRAGE_NUTZER;

  // Verhalten, wenn beim Öffnen einer Datenliste diese sich als Backup herausstellt
  public static final int BACKUP_LOAD_WITHOUT_QUESTION = 0;
  public static final int BACKUP_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN = 1;
  public static int actionOnDatenlisteIsBackup = BACKUP_LOAD_WITHOUT_QUESTION;

  // Encoding zum Lesen und Schreiben von Dateien
  public static final String ENCODING_ISO = "ISO-8859-1";
  public static final String ENCODING_UTF = "UTF-8";

  // Applikations-IDs
  public static int applID = -1;
  public static final int APPL_EFA = 1;
  public static final int APPL_EFADIREKT = 2;
  public static final int APPL_EMIL = 3;
  public static final int APPL_ELWIZ = 4;
  public static final int APPL_EDDI = 5;
  public static final int APPL_DRV = 6;
  public static final int APPL_CLI = 7;

  // Applikations-Mode
  public static final int APPL_MODE_NORMAL = 1;
  public static final int APPL_MODE_ADMIN = 2;
  public static int applMode = APPL_MODE_NORMAL;

  // Applikations-PID
  public static String applPID;


  public static void initialize(int applID) {
      iniBase(applID);
      iniScreenSize();
      iniMainDirectory();
      iniEfaBaseConfig();
      iniLanguageSupport();
      iniLogging();
      iniSplashScreen(true);
      iniEnvironmentSettings();
      iniDirectories();
      iniEfaSec();
      iniFileSettings(1);
      iniEfaConfig();
      iniFileSettings(2);
      iniEfaRunning();
      iniBackup();
      iniEfaTypes();
      iniGUI();
}

  public static void haltProgram(int exitCode) {
      if (exitCode != 0) {
          if (exitCode < 99) {
              Logger.log(Logger.ERROR, Logger.MSG_CORE_HALT,
                      International.getString("PROGRAMMENDE") + " (Error Code " + exitCode + ")");
          } else {
              Logger.log(Logger.INFO, Logger.MSG_CORE_HALT,
                      International.getString("PROGRAMMENDE") + " (Exit Code " + exitCode + ")");
          }
      } else {
          Logger.log(Logger.INFO, Logger.MSG_CORE_HALT,
                  International.getString("PROGRAMMENDE"));
      }
      if (program != null) {
          program.exit(exitCode);
      } else {
          System.exit(exitCode);
      }
  }

  private static void iniBase(int _applID) {
      fahrtenbuch = null;
      fileSep = System.getProperty("file.separator");
      javaVersion = System.getProperty("java.version");
      jvmVersion = System.getProperty("java.vm.version");
      osName = System.getProperty("os.name");
      osVersion = System.getProperty("os.version");
      efaUserHome = System.getProperty("user.home");
      applID = _applID;
      efaStartTime = System.currentTimeMillis();

      try {
          // ManagementFactory.getRuntimeMXBean().getName() == "12345@localhost" or similar (not guaranteed by VM Spec!)
          applPID = EfaUtil.int2String(EfaUtil.stringFindInt(ManagementFactory.getRuntimeMXBean().getName(), 0), 5);
      } catch(Exception e) {
          applPID = "00000";
      }
  }

  private static void iniMainDirectory() {
      Daten.efaMainDirectory = System.getProperty("user.dir");
      if (!Daten.efaMainDirectory.endsWith(Daten.fileSep)) {
          Daten.efaMainDirectory += Daten.fileSep;
      }
      if (Daten.efaMainDirectory.endsWith("/program/") && !new File(Daten.efaMainDirectory+"program/").isDirectory()) {
          Daten.efaMainDirectory = Daten.efaMainDirectory.substring(0,Daten.efaMainDirectory.length()-8);
      }
      if (Daten.efaMainDirectory.endsWith("/classes/") && !new File(Daten.efaMainDirectory+"program/").isDirectory()) {
          Daten.efaMainDirectory = Daten.efaMainDirectory.substring(0,Daten.efaMainDirectory.length()-8);
      }
  }

  private static void iniEfaBaseConfig() {
      String efaBaseConfigFile = Daten.efaUserHome + (Daten.fileSep != null && !Daten.efaUserHome.endsWith(Daten.fileSep) ? Daten.fileSep : "");
      EfaBaseConfig.setEfaConfigUserHomeFilename(efaBaseConfigFile);
      Daten.efaBaseConfig = new EfaBaseConfig();
      if (!EfaUtil.canOpenFile(Daten.efaBaseConfig.getFileName())) {
          if (!Daten.efaBaseConfig.writeFile()) {
              String msg = International.getString("efa can't start") + ": " +
                           International.getMessage("Basic Configuration File '{filename}' could not be created.", Daten.efaBaseConfig.getFileName());
              Logger.log(Logger.ERROR, Logger.MSG_CORE_BASICCONFIGFAILEDCREATE, msg);
              if (isGuiAppl()) {
                  Dialog.error(msg);
              }
              haltProgram(HALT_BASICCONFIG);
          }
      }
      if (!Daten.efaBaseConfig.readFile()) {
          String msg = International.getString("efa can't start") + ": " +
                       International.getMessage("Basic Configuration File '{filename}' could not be opened.", Daten.efaBaseConfig.getFileName());
          Logger.log(Logger.ERROR, Logger.MSG_CORE_BASICCONFIGFAILEDOPEN, msg);
          if (isGuiAppl()) {
              Dialog.error(msg);
          }
          haltProgram(HALT_BASICCONFIG);
      }
  }

  private static void iniLanguageSupport() {
      Daten.efaProgramDirectory = Daten.efaMainDirectory+"program"+Daten.fileSep; // just temporary, will be overwritten by iniDirectories()
      International.initialize();
  }

  private static void iniLogging() {
      Daten.efaLogDirectory = Daten.efaMainDirectory+"log"+Daten.fileSep;
      if (!checkAndCreateDirectory(Daten.efaLogDirectory) ) {
          haltProgram(HALT_DIRECTORIES);
      }
      String baklog = null; // backup'ed logfile
      switch(applID) {
          case APPL_EFA:
              baklog = Logger.ini("efa_base.log",false);
              break;
          case APPL_EFADIREKT:
              baklog = Logger.ini("efa_bh.log",true);
              break;
          case APPL_EMIL:
              baklog = Logger.ini("emil.log",false);
              break;
          case APPL_ELWIZ:
              baklog = Logger.ini("elwiz.log",false);
              break;
          case APPL_EDDI:
              baklog = Logger.ini("eddi.log",false);
              break;
          case APPL_DRV:
              baklog = Logger.ini("drv.log",true);
              break;
          case APPL_CLI:
              baklog = Logger.ini(null,true);
              break;
          default:
              baklog = Logger.ini(null,true);
              break;
      }

      Logger.log(Logger.INFO, Logger.MSG_EVT_EFASTART,
              International.getString("PROGRAMMSTART"));
      Logger.log(Logger.INFO, Logger.MSG_INFO_VERSION,
              "Version efa: " + Daten.VERSIONID + " -- Java: " + Daten.javaVersion + " (JVM " + Daten.jvmVersion + ") -- OS: " + Daten.osName + " " + Daten.osVersion);

      if (Logger.debugLogging) {
          Logger.log(Logger.INFO, Logger.MSG_LOGGER_DEBUGACTIVATED,
                  "Debug Logging activated."); // do not internationalize!
      }

      if (baklog != null) {
          Logger.log(Logger.INFO, Logger.MSG_EVT_LOGFILEARCHIVED,
                  International.getMessage("Alte Logdatei wurde nach '{filename}' verschoben.", baklog));
      }
  }

  private static void iniEnvironmentSettings() {
      try {
          if (applID == APPL_EFADIREKT) {
              Daten.efa_java_arguments = System.getenv(Daten.EFA_JAVA_ARGUMENTS);
              Logger.log(Logger.DEBUG, Logger.MSG_DEBUG_GENERIC,
                      Daten.EFA_JAVA_ARGUMENTS + "=" + Daten.efa_java_arguments);
          }
      } catch (Error e) {
          Logger.log(Logger.WARNING, Logger.MSG_WARN_CANTGETEFAJAVAARGS,
                  International.getMessage("Abfragen der Environment-Variable {name} nicht möglich: {msg}",
                  Daten.EFA_JAVA_ARGUMENTS, e.toString()));
      }
  }

  private static void iniDirectories() {
      // ./program
      Daten.efaProgramDirectory = Daten.efaMainDirectory+"program"+Daten.fileSep;
      if (!checkAndCreateDirectory(Daten.efaProgramDirectory) ) {
          haltProgram(HALT_DIRECTORIES);
      }

      // ./program/plugins
      Daten.efaPluginDirectory = Daten.efaProgramDirectory + "plugins" + Daten.fileSep;
      if (!checkAndCreateDirectory(Daten.efaPluginDirectory) ) {
          haltProgram(HALT_DIRECTORIES);
      }

      // ./daten
      Daten.efaDataDirectory = Daten.efaBaseConfig.efaUserDirectory + "data" + Daten.fileSep;
      if (!checkAndCreateDirectory(Daten.efaDataDirectory) ) {
          haltProgram(HALT_DIRECTORIES);
      }

      // ./cfg
      Daten.efaCfgDirectory = Daten.efaBaseConfig.efaUserDirectory + "cfg" + Daten.fileSep;
      if (!checkAndCreateDirectory(Daten.efaCfgDirectory) ) {
          haltProgram(HALT_DIRECTORIES);
      }

      // ./doc
      Daten.efaDocDirectory = Daten.efaMainDirectory + "doc" + Daten.fileSep;
      if (!checkAndCreateDirectory(Daten.efaDocDirectory) ) {
          haltProgram(HALT_DIRECTORIES);
      }

      // ./ausgabe
      Daten.efaAusgabeDirectory = Daten.efaMainDirectory + "fmt" + Daten.fileSep;
      if (!checkAndCreateDirectory(Daten.efaAusgabeDirectory) ) {
          haltProgram(HALT_DIRECTORIES);
      }

      // ./ausgabe/layout
      Daten.efaStyleDirectory = Daten.efaAusgabeDirectory + "layout" + Daten.fileSep;
      if (!checkAndCreateDirectory(Daten.efaStyleDirectory) ) {
          haltProgram(HALT_DIRECTORIES);
      }

      // ./bak
      Daten.efaBakDirectory = Daten.efaBaseConfig.efaUserDirectory + "backup" + Daten.fileSep;
      if (!checkAndCreateDirectory(Daten.efaBakDirectory) ) {
          haltProgram(HALT_DIRECTORIES);
      }

      // ./tmp
      Daten.efaTmpDirectory = Daten.efaBaseConfig.efaUserDirectory + "tmp" + Daten.fileSep;
      if (!checkAndCreateDirectory(Daten.efaTmpDirectory) ) {
          haltProgram(HALT_DIRECTORIES);
      }
  }

  public static void iniSplashScreen(boolean show) {
      if (!isGuiAppl()) {
          return;
      }
      if (show) {
          splashScreen = new StartLogo("/de/nmichael/efa/img/efaIntro.gif");
          splashScreen.show();
          try {
              Thread.sleep(1000); // Damit nach automatischem Restart genügend Zeit vergeht
          } catch(InterruptedException e) {
          }
      } else {
          if (splashScreen != null) {
              splashScreen.remove();
          }
      }
  }

  public static void iniEfaSec() {
      efaSec = new EfaSec(Daten.efaProgramDirectory+Daten.EFA_SECFILE);
      if (efaSec.secFileExists() && !efaSec.secValueValid()) {
          String msg = International.getString("Die Sicherheitsdatei ist korrupt! Aus Gründen der Sicherheit verweigert efa daher den Dienst. " +
                                               "Um efa zu reaktivieren, wende Dich bitte an den Entwickler: ") + Daten.EFAEMAIL;
          Logger.log(Logger.ERROR, Logger.MSG_CORE_EFASECCORRUPTED, msg);
          if (isGuiAppl()) {
              Dialog.error(msg);
          }
          haltProgram(HALT_EFASEC);
      }
  }

  public static void iniFileSettings(int stage) {
      switch(applID) {
          case APPL_EFA:
              switch (stage) {
                  case 1: // before EfaConfig is opened
                      if (!efaSec.secFileExists()) { // efa Secure Mode
                          // Stop on Checksum Errors
                          // Eigentlich darf efa bei fehlerhafter Konfigurationsdatei überhaupt nicht mehr Starten, denn ein
                          // Angreifer könnte ja das Paßwort der admins ausgetauscht haben. Wenn efa dann beim Start die
                          // modifizierte Konfigurationsdatei einliest, wird das Abnicken der geänderten Datei durch das
                          // geänderte Paßwort möglich!
                          // Gleiches gilt für efa im Bootshaus.
                          // Um sicher zu sein, muß efa jegliche Änderungen an der Konfigurationsdatei künftig verbieten.
                          //      Daten.actionOnChecksumError = Daten.CHECKSUM_HALT_PROGRAM;
                          Daten.actionOnChecksumLoadError = Daten.CHECKSUM_LOAD_REQUIRE_ADMIN;
                          // Admin erforderlich, um nicht vorhandene Datenliste neu zu erstellen
                          Daten.actionOnDatenlisteNotFound = Daten.DATENLISTE_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN;
                          // Admin erforderlich, wenn Backupdatei geladen werden soll
                          Daten.actionOnDatenlisteIsBackup = Daten.BACKUP_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN;
                      }
                      break;
                  case 2: // after EfaConfig is opened
                      if (!efaSec.secFileExists()) { // efa Secure Mode: Jetzt, da Config gelesen wurde: Nur noch require Admin
                          // Admit on Checksum Errors
                          Daten.actionOnChecksumLoadError = Daten.CHECKSUM_LOAD_REQUIRE_ADMIN;
                      }
                      break;
              }
              break;
          case APPL_EFADIREKT:
              switch (stage) {
                  case 1: // before EfaConfig is opened
                      // Stop on Checksum Errors
                      Daten.actionOnChecksumLoadError = Daten.CHECKSUM_LOAD_REQUIRE_ADMIN;
                      Daten.actionOnChecksumSaveError = Daten.CHECKSUM_SAVE_HALT_PROGRAM;
                      // Admin erforderlich, um nicht vorhandene Datenliste neu zu erstellen
                      Daten.actionOnDatenlisteNotFound = Daten.DATENLISTE_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN;
                      // Admin erforderlich, wenn Backupdatei geladen werden soll
                      Daten.actionOnDatenlisteIsBackup = Daten.BACKUP_FRAGE_REQUIRE_ADMIN_EXIT_ON_NEIN;
                      break;
                  case 2: // after EfaConfig is opened
                      // nothing to do
                      break;
              }
              break;
      }
  }

    public static void iniEfaConfig() {
        if (applID != APPL_DRV) {
            Daten.efaConfig = new EfaConfig(Daten.efaCfgDirectory + Daten.CONFIGFILE);
            if (!EfaUtil.canOpenFile(Daten.efaConfig.getFileName())) {
                if (!Daten.efaConfig.writeFile()) {
                    String msg = LogString.logstring_fileCreationFailed(Daten.efaConfig.getFileName(),
                            International.getString("Konfigurationsdatei"));
                    Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGFAILEDCREATE, msg);
                    if (isGuiAppl()) {
                        Dialog.error(msg);
                    }
                    haltProgram(HALT_EFACONFIG);
                }
                String msg = LogString.logstring_fileNewCreated(Daten.efaConfig.getFileName(),
                        International.getString("Konfigurationsdatei"));
                Logger.log(Logger.WARNING, Logger.MSG_CORE_EFACONFIGCREATEDNEW, msg);
            }
            if (!Daten.efaConfig.readFile()) {
                String msg = LogString.logstring_fileOpenFailed(Daten.efaConfig.getFileName(),
                        International.getString("Konfigurationsdatei"));
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGFAILEDOPEN, msg);
                if (isGuiAppl()) {
                    Dialog.error(msg);
                }
                haltProgram(HALT_EFACONFIG);
            }
        } else {
            Daten.drvConfig = new DRVConfig(Daten.efaCfgDirectory + Daten.DRVCONFIGFILE);
            if (!EfaUtil.canOpenFile(Daten.drvConfig.getFileName())) {
                if (!Daten.drvConfig.writeFile()) {
                    String msg = LogString.logstring_fileCreationFailed(Daten.drvConfig.getFileName(),
                            International.getString("Konfigurationsdatei"));
                    Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGFAILEDCREATE, msg);
                    if (isGuiAppl()) {
                        Dialog.error(msg);
                    }
                    haltProgram(HALT_EFACONFIG);
                }
                String msg = LogString.logstring_fileNewCreated(Daten.drvConfig.getFileName(),
                        International.getString("Konfigurationsdatei"));
                Logger.log(Logger.WARNING, Logger.MSG_CORE_EFACONFIGCREATEDNEW, msg);
            }
            if (!Daten.drvConfig.readFile()) {
                String msg = LogString.logstring_fileOpenFailed(Daten.drvConfig.getFileName(),
                        International.getString("Konfigurationsdatei"));
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFACONFIGFAILEDOPEN, msg);
                if (isGuiAppl()) {
                    Dialog.error(msg);
                }
                haltProgram(HALT_EFACONFIG);
            }
        }
    }

    public static void iniEfaTypes() {
        Daten.efaTypes = new EfaTypes(Daten.efaCfgDirectory + Daten.EFATYPESFILE);
        if (!EfaUtil.canOpenFile(Daten.efaTypes.getFileName())) {
            if (!Daten.efaTypes.createNewIfDoesntExist()) {
                String msg = LogString.logstring_fileCreationFailed(Daten.efaTypes.getFileName(),
                        International.getString("Bezeichnungen"));
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFATYPESFAILEDCREATE, msg);
                if (isGuiAppl()) {
                    Dialog.error(msg);
                }
                haltProgram(HALT_EFATYPES);
            }
            String msg = LogString.logstring_fileNewCreated(Daten.efaTypes.getFileName(),
                    International.getString("Bezeichnungen"));
            Logger.log(Logger.WARNING, Logger.MSG_CORE_EFATYPESCREATEDNEW, msg);
        }
        if (!Daten.efaTypes.readFile()) {
            String msg = LogString.logstring_fileOpenFailed(Daten.efaTypes.getFileName(),
                    International.getString("Bezeichnungen"));
            Logger.log(Logger.ERROR, Logger.MSG_CORE_EFATYPESFAILEDOPEN, msg);
            if (isGuiAppl()) {
                Dialog.error(msg);
            }
            haltProgram(HALT_EFATYPES);
        }
    }

    public static void iniEfaRunning() {
        if (Daten.efaConfig.efaDirekt_checkRunning) {
            efaRunning = new EfaRunning();
            if (efaRunning.isRunning()) {
                String msg = International.getString("efa läuft bereits und kann nicht zeitgleich zweimal gestartet werden!");
                Logger.log(Logger.ERROR, Logger.MSG_CORE_EFAALREADYRUNNING, msg);
                if (isGuiAppl()) {
                    Dialog.error(msg);
                }
                haltProgram(Daten.HALT_EFARUNNING);
            }
            efaRunning.run();
        }
    }

    public static void iniBackup() {
        if (efaConfig.bakDir != null && efaConfig.bakDir.length() > 0) {
            String dir = efaConfig.bakDir;
            if (!dir.endsWith(Daten.fileSep)) {
                dir = dir + Daten.fileSep;
            }
            if (new File(dir).isDirectory()) {
                Daten.efaBakDirectory = dir;
            } else {
                Logger.log(Logger.WARNING, Logger.MSG_CORE_CONFBACKUPDIRNOTEXIST,
                        LogString.logstring_directoryDoesNotExist(International.getString("Backup-Verzeichnis"), dir));
                Logger.log(Logger.INFO, Logger.MSG_CORE_CONFBACKUPDIRNOTEXIST,
                        International.getString("Backup-Verzeichnis") + ": " + Daten.efaBakDirectory);
            }
        }
        Daten.backup = new Backup(Daten.efaBakDirectory,Daten.efaConfig.bakSave,Daten.efaConfig.bakMonat,Daten.efaConfig.bakTag,Daten.efaConfig.bakKonv);
    }

  public static void iniScreenSize() {
      if (!isGuiAppl()) {
          return;
      }
      Dialog.initializeScreenSize();
  }

  public static void iniGUI() {
      if (!isGuiAppl()) {
          return;
      }
      iniScreenSize();

      // Look&Feel
      try {
          if (Daten.efaConfig.lookAndFeel.equals(EfaConfig.DEFAULT)) {
              UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
          } else {
              UIManager.setLookAndFeel(Daten.efaConfig.lookAndFeel);
          }
      } catch (Exception e) {
          Logger.log(Logger.WARNING, Logger.MSG_WARN_CANTSETLOOKANDFEEL,
                     International.getString("Konnte Look&Feel nicht setzen") + ": " + e.toString());
      }
  }

    public static void iniDataFile(DatenListe f, boolean autoNewIfDoesntExist, String s) {
        if (autoNewIfDoesntExist) {
            f.createNewIfDoesntExist();
        } else {
            if (!EfaUtil.canOpenFile(f.getFileName())) {
                if (f.writeFile()) {
                    LogString.logInfo_fileNewCreated(f.getFileName(), s);
                } else {
                    LogString.logError_fileCreationFailed(f.getFileName(), s);
                }
            }
        }
        if (!f.readFile()) {
            LogString.logError_fileOpenFailed(f.getFileName(), s);
        }


    }

    public static void iniAllDataFiles() {
        Daten.wettDefs = new WettDefs(Daten.efaCfgDirectory + Daten.WETTDEFS);
        iniDataFile(Daten.wettDefs, true, International.onlyFor("Wettbewerbskonfiguration", "de"));

        Daten.vereinsConfig = new VereinsConfig(Daten.efaDataDirectory + Daten.VEREINSCONFIG);
        iniDataFile(Daten.vereinsConfig, false, International.getString("Vereinskonfiguration"));

        Daten.adressen = new Adressen(Daten.efaDataDirectory + Daten.ADRESSENFILE);
        iniDataFile(Daten.adressen, false, International.getString("Adreßliste"));

        Daten.synMitglieder = new Synonyme(Daten.efaDataDirectory + Daten.MITGLIEDER_SYNONYM);
        iniDataFile(Daten.synMitglieder, false, International.getString("Mitglieder-Synonymliste"));

        Daten.synBoote = new Synonyme(Daten.efaDataDirectory + Daten.BOOTE_SYNONYM);
        iniDataFile(Daten.synBoote, false, International.getString("Boots-Synonymliste"));

        Daten.synZiele = new Synonyme(Daten.efaDataDirectory + Daten.ZIELE_SYNONYM);
        iniDataFile(Daten.synZiele, false, International.getString("Ziel-Synonymliste"));

        Daten.mannschaften = new Mannschaften(Daten.efaDataDirectory + Daten.MANNSCHAFTENFILE);
        iniDataFile(Daten.mannschaften, false, International.getString("Mannschaften-Liste"));

        Daten.fahrtenabzeichen = new Fahrtenabzeichen(Daten.efaDataDirectory + Daten.FAHRTENABZEICHEN);
        iniDataFile(Daten.fahrtenabzeichen, false, International.onlyFor("Fahrtenabzeichen-Liste","de"));

        Daten.gruppen = new Gruppen(Daten.efaDataDirectory + Daten.GRUPPEN);
        iniDataFile(Daten.gruppen, false, International.getString("Gruppenliste"));

        Daten.keyStore = new EfaKeyStore(Daten.efaDataDirectory+Daten.PUBKEYSTORE,"efa".toCharArray());
    }

  public static boolean isGuiAppl() {
      return (applID == APPL_EFA ||
              applID == APPL_EFADIREKT ||
              applID == APPL_EMIL ||
              applID == APPL_ELWIZ ||
              applID == APPL_EDDI ||
              applID == APPL_DRV);
  }

  private static boolean checkAndCreateDirectory(String dir) {
      File f = new File(dir);
      if (!f.isDirectory()) {
          boolean result = f.mkdirs();
          if (result == true) {
              Logger.log(Logger.WARNING, Logger.MSG_CORE_SETUPDIRS,
                         International.getMessage("Verzeichnis '{directory}' konnte nicht gefunden werden und wurde neu erstellt.", dir));
          } else {
              Logger.log(Logger.ERROR, Logger.MSG_CORE_SETUPDIRS,
                         International.getMessage("Verzeichnis '{directory}' konnte weder gefunden, noch neu erstellt werden.", dir));
          }
          return result;
      }
      return true;
  }


  public static Vector getEfaInfos() {
    Vector infos = new Vector();

    // efa-Infos
    infos.add("efa.version="+Daten.VERSIONID);
    if (applID != APPL_EFADIREKT) {
      infos.add("efa.dir.main="+Daten.efaMainDirectory);
      infos.add("efa.dir.user="+Daten.efaBaseConfig.efaUserDirectory);
      infos.add("efa.dir.program="+Daten.efaProgramDirectory);
      infos.add("efa.dir.plugin="+Daten.efaPluginDirectory);
      infos.add("efa.dir.doc="+Daten.efaDocDirectory);
      infos.add("efa.dir.ausgabe="+Daten.efaAusgabeDirectory);
      infos.add("efa.dir.layout="+Daten.efaStyleDirectory);
      infos.add("efa.dir.data="+Daten.efaDataDirectory);
      infos.add("efa.dir.cfg="+Daten.efaCfgDirectory);
      infos.add("efa.dir.bak="+Daten.efaBakDirectory);
      infos.add("efa.dir.tmp="+Daten.efaTmpDirectory);
    }

    // efa Plugin-Infos
    try {
      File dir = new File(Daten.efaPluginDirectory);
      if (applID != APPL_EFADIREKT) {
        File[] files = dir.listFiles();
        for (int i=0; i<files.length; i++)
          if (files[i].isFile())
            infos.add("efa.plugin.file="+files[i].getName()+":"+files[i].length());
      }
      try {
        XMLWriter tmp = new XMLWriter(null,null);
        infos.add("efa.plugin.xml=INSTALLED");
      } catch(NoClassDefFoundError e) {
        infos.add("efa.plugin.xml=NOT INSTALLED");
      }
      try {
        PDFWriter tmp = new PDFWriter(null,null);
        infos.add("efa.plugin.fop=INSTALLED");
      } catch(NoClassDefFoundError e) {
        infos.add("efa.plugin.fop=NOT INSTALLED");
      }
      try {
        FTPWriter tmp = new FTPWriter(null);
        infos.add("efa.plugin.ftp=INSTALLED");
      } catch(NoClassDefFoundError e) {
        infos.add("efa.plugin.ftp=NOT INSTALLED");
      }
      try {
        de.nmichael.efa.direkt.EmailSender tmp = new de.nmichael.efa.direkt.EmailSender();
        infos.add("efa.plugin.email=INSTALLED");
      } catch(NoClassDefFoundError e) {
        infos.add("efa.plugin.email=NOT INSTALLED");
      }
      try {
        de.nmichael.efa.direkt.SunRiseSet tmp = new de.nmichael.efa.direkt.SunRiseSet();
        infos.add("efa.plugin.jsuntimes=INSTALLED");
      } catch(NoClassDefFoundError e) {
        infos.add("efa.plugin.jsuntimes=NOT INSTALLED");
      }
    } catch(Exception e) {
      Logger.log(Logger.ERROR,Logger.MSG_CORE_INFOFAILED,International.getString("Programminformationen konnten nicht ermittelt werden")+": "+e.toString());
      return null;
    }

    // Java Infos
    infos.add("java.version="+System.getProperty("java.version"));
    infos.add("java.vendor="+System.getProperty("java.vendor"));
    infos.add("java.home="+System.getProperty("java.home"));
    infos.add("java.vm.version="+System.getProperty("java.vm.version"));
    infos.add("java.vm.vendor="+System.getProperty("java.vm.vendor"));
    infos.add("java.vm.name="+System.getProperty("java.vm.name"));
    infos.add("os.name="+System.getProperty("os.name"));
    infos.add("os.arch="+System.getProperty("os.arch"));
    infos.add("os.version="+System.getProperty("os.version"));
    if (applID != APPL_EFADIREKT) {
      infos.add("user.dir="+System.getProperty("user.dir"));
      infos.add("java.class.path="+System.getProperty("java.class.path"));
    }

    // JAR methods
    if (Logger.debugLogging) {
      try {
        String cp = System.getProperty("java.class.path");
        while (cp != null && cp.length() > 0) {
          int pos = cp.indexOf(";");
          if (pos<0) pos = cp.indexOf(":");
          String jarfile;
          if (pos>=0) {
            jarfile = cp.substring(0,pos);
            cp = cp.substring(pos+1);
          } else {
            jarfile = cp;
            cp = null;
          }
          if (jarfile != null && jarfile.length()>0 && new File(jarfile).isFile()) {
            try {
              infos.add("java.jar.filename="+jarfile);
              JarFile jar = new JarFile(jarfile);
              Enumeration _enum = jar.entries();
              Object o;
              while ( _enum.hasMoreElements() && ( o = _enum.nextElement()) != null ) {
                infos.add("java.jar.content="+o+":"+ ( jar.getEntry(o.toString()) == null ? "null" : Long.toString(jar.getEntry(o.toString()).getSize()) ) );
              }
            } catch (Exception e) {
              Logger.log(Logger.ERROR,Logger.MSG_CORE_INFOFAILED,e.toString());
              return null;
            }
          }
        }
      } catch(Exception e) {
        Logger.log(Logger.ERROR,Logger.MSG_CORE_INFOFAILED,International.getString("Programminformationen konnten nicht ermittelt werden")+": "+e.toString());
        return null;
      }
    }
    return infos;
  }

  public static void printEfaInfos() {
    Vector infos = getEfaInfos();
    for (int i=0; infos != null && i<infos.size(); i++) {
        Logger.log(Logger.INFO,Logger.MSG_INFO_CONFIGURATION,(String)infos.get(i));
    }
  }

  public static String getEfaImage(int size) {
    int birthday = EfaUtil.getEfaBirthday();
    switch(size) {
      case 1:  if (birthday == 5) return "/de/nmichael/efa/img/efa_small_5jahre.gif";
               else return "/de/nmichael/efa/img/efa_small.gif";
      default: if (birthday == 5) return "/de/nmichael/efa/img/efa_logo_5jahre.gif";
               else return "/de/nmichael/efa/img/efa_logo.gif";
    }
  }

  public static void checkEfaVersion(boolean interactive) {
    // @todo: how to best implement this in efa2??
/*
    // Bei 1 Jahr alten Versionen alle 90 Tage prüfen, ob eine neue Version vorliegt
    if (EfaUtil.getDateDiff(Daten.VERSIONRELEASEDATE,EfaUtil.getCurrentTimeStampDD_MM_YYYY()) > 365 &&
        (Daten.efaConfig.efaVersionLastCheck == null || Daten.efaConfig.efaVersionLastCheck.length() == 0 ||
         EfaUtil.getDateDiff(Daten.efaConfig.efaVersionLastCheck,EfaUtil.getCurrentTimeStampDD_MM_YYYY()) > 90) ) {
      if (Dialog.yesNoDialog(InternationalXX.getString("Prüfen, ob neue efa-Version verfügbar"),
                             InternationalXX.getMessage("Die von Dir verwendete Version von efa ({versionid}) ist bereits "+
                             "über ein Jahr alt. Soll efa jetzt für Dich prüfen, ob eine "+
                             "neue Version von efa vorliegt?",Daten.VERSIONID)) == Dialog.YES) {
        OnlineUpdateFrame.runOnlineUpdate(this,Daten.ONLINEUPDATE_INFO);
      }
      Daten.efaConfig.efaVersionLastCheck = EfaUtil.getCurrentTimeStampDD_MM_YYYY();
    }

*/
  }

  public static void checkJavaVersion(boolean interactive) {
    // @todo: how to best implement this in efa2??
/*
    if (Daten.javaVersion == null) return;

    TMJ tmj = EfaUtil.string2date(Daten.javaVersion,0,0,0);
    int version = tmj.tag*100 + tmj.monat*10 + tmj.jahr;

    if (version < 140) {
      if (Dialog.yesNoDialog(InternationalXX.getString("Java-Version zu alt"),
              InternationalXX.getMessage("Die von Dir verwendete Java-Version {version} wird von efa "+
                             "offiziell nicht mehr unterstützt. Einige Funktionen von efa stehen "+
                             "unter dieser Java-Version nicht zur Verfügung oder funktionieren nicht "+
                             "richtig. Vom Einsatz von efa mit dieser Java-Version wird dringend abgeraten. "+
                             "Für den optimalen Einsatz von efa wird Java-Version 5 oder neuer empfohlen.\n\n"+
                             "Sollen jetzt die Download-Anleitung für eine neue Java-Version "+
                             "angezeigt werden?",Daten.javaVersion)) == Dialog.YES) {
        showJavaDownloadHints();
      }
      return;
    }

    if (!alsoCheckForOptimalVersion) return;

    if (version < 150) {
      if (Dialog.yesNoDialog(InternationalXX.getString("Java-Version alt"),
              InternationalXX.getMessage("Die von Dir verwendete Java-Version {version} ist bereits relativ alt. "+
                             "Für den optimalen Einsatz von efa wird Java 5 (Version 1.5.0) oder neuer empfohlen. "+
                             "efa funktioniert zwar auch mit älteren Java-Versionen weiterhin, jedoch gibt es einige "+
                             "Funktionen, die nur unter neueren Java-Versionen unterstützt werden. Außerdem werden "+
                             "Java-Fehler oft nur noch in den neueren Versionen korrigiert, so daß auch aus diesem "+
                             "Grund immer der Einsatz einer möglichst neuen Java-Version empfohlen ist.\n\n"+
                             "Sollen jetzt die Download-Anleitung für eine neue Java-Version "+
                             "angezeigt werden?",Daten.javaVersion)) == Dialog.YES) {
        showJavaDownloadHints();
      }
    }
 */
  }

  public static void checkRegister() {
      if (PROGRAMMID.equals(Daten.efaConfig.registeredProgramID)) {
          return; // already registered
      }
      Daten.efaConfig.registrationChecks++;

      boolean promptForRegistration = false;
      if (Daten.efaConfig.registeredProgramID == null || Daten.efaConfig.registeredProgramID.length() == 0) {
          // never before registered
          if (Daten.efaConfig.registrationChecks <= 30 && Daten.efaConfig.registrationChecks % 10 == 0) {
              promptForRegistration = true;
          }
      } else {
          // previous version already registered
          if (Daten.efaConfig.registrationChecks <= 10 && Daten.efaConfig.registrationChecks % 10 == 0) {
              promptForRegistration = true;
          }
      }

      if (promptForRegistration) {
          if (Dialog.neuBrowserDlg((javax.swing.JDialog)null, Daten.EFA_SHORTNAME,
                  "file:" + HtmlFactory.createRegister(),
                  750, 600, (int) Dialog.screenSize.getWidth() / 2 - 375, (int) Dialog.screenSize.getHeight() / 2 - 300).endsWith(".pl")) {
              // registration complete
              Daten.efaConfig.registeredProgramID = Daten.PROGRAMMID;
              Daten.efaConfig.registrationChecks = 0;
              Daten.efaConfig.writeEinstellungen();
          }

      }
  }


  private static void showJavaDownloadHints() {
    if (Daten.efaDocDirectory == null) return;
    Dialog.infoDialog(International.getString("Download-Anleitung"),
                      International.getString("Bitte folge in der folgenden Anleitung den Hinweisen unter Punkt 5, "+
                      "um eine neue Java-Version zu installieren."));
    Dialog.neuBrowserDlg((javax.swing.JFrame)null,International.getString("Java-Installation"),"file:"+Daten.efaDocDirectory+"installation.html");
  }

}
