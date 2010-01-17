/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.util;

import de.nmichael.efa.*;
import de.nmichael.efa.util.International;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.EfaErrorPrintStream;
import java.io.*;
import java.util.*;
import de.nmichael.efa.direkt.NachrichtenAnAdmin;
import de.nmichael.efa.direkt.Nachricht;

// @i18n complete

public class Logger {

  private static final int LOGGING_THRESHOLD = 1000; // max LOGGING_THRESHOLD logging messages per second

  // Message Types
  public static final String ERROR   = "ERROR  ";
  public static final String INFO    = "INFO   ";
  public static final String WARNING = "WARNING";
  public static final String ACTION  = "ACTION ";
  public static final String DEBUG   = "DEBUG  ";

  // Message Keys
  public static final String MSG_GENERIC                     = "GEN001";
  public static final String MSG_GENERIC_ERROR               = "GEN002";

  // Core Functionality Informations (multiple source files)
  public static final String MSG_INFO_CONFIGURATION          = "INF001";
  public static final String MSG_INFO_VERSION                = "INF002";

  // Core Functionality (multiple source files)
  public static final String MSG_CORE_HALT                   = "COR001";
  public static final String MSG_CORE_SETUPDIRS              = "COR002";
  public static final String MSG_CORE_INFOFAILED             = "COR003";
  public static final String MSG_CORE_BASICCONFIG            = "COR004";
  public static final String MSG_CORE_BASICCONFIGFAILEDCREATE= "COR005";
  public static final String MSG_CORE_BASICCONFIGFAILEDOPEN  = "COR006";
  public static final String MSG_CORE_LANGUAGESUPPORT        = "COR007";
  public static final String MSG_CORE_EFACONFIGCREATEDNEW    = "COR008";
  public static final String MSG_CORE_EFACONFIGFAILEDCREATE  = "COR009";
  public static final String MSG_CORE_EFACONFIGFAILEDOPEN    = "COR010";
  public static final String MSG_CORE_EFATYPESCREATEDNEW     = "COR011";
  public static final String MSG_CORE_EFATYPESFAILEDCREATE   = "COR012";
  public static final String MSG_CORE_EFATYPESFAILEDOPEN     = "COR013";
  public static final String MSG_CORE_EFATYPESFAILEDSETVALUES= "COR014";
  public static final String MSG_CORE_EFASECCORRUPTED        = "COR015";
  public static final String MSG_CORE_CONFBACKUPDIRNOTEXIST  = "COR016";
  public static final String MSG_CORE_EFAALREADYRUNNING      = "COR017";
  public static final String MSG_CORE_EFACONFIGUNKNOWNPARAM  = "COR018";
  public static final String MSG_CORE_EFACONFIGUNSUPPPARMTYPE= "COR019";
  public static final String MSG_CORE_EFACONFIGINVALIDVALUE  = "COR020";

  // Activities performed in Admin Mode
  public static final String MSG_ADMIN_LOGIN                 = "ADM001";
  public static final String MSG_ADMIN_LOGINFAILURE          = "ADM002";
  public static final String MSG_ADMIN_ADMINMODEEXITED       = "ADM003";
  public static final String MSG_ADMIN_ACTION_ADMINS         = "ADM004";
  public static final String MSG_ADMIN_ACTION_CONFNEWLOGBOOK = "ADM005";
  public static final String MSG_ADMIN_ACTION_OPENLOGBOOK    = "ADM006";
  public static final String MSG_ADMIN_ACTION_EDITLOGBOOK    = "ADM007";
  public static final String MSG_ADMIN_ACTION_EDITLOGBOOKDONE= "ADM008";
  public static final String MSG_ADMIN_ACTION_EDITBOATSTATUS = "ADM009";
  public static final String MSG_ADMIN_ACTION_VIEWMESSAGES   = "ADM010";
  public static final String MSG_ADMIN_ACTION_VIEWLOGFILE    = "ADM011";
  public static final String MSG_ADMIN_ACTION_EDITCONFIG     = "ADM012";
  public static final String MSG_ADMIN_ACTION_STATISTICS     = "ADM013";
  public static final String MSG_ADMIN_ACTION_EDITBOATLIST   = "ADM014";
  public static final String MSG_ADMIN_ACTION_EDITMEMBERLIST = "ADM015";
  public static final String MSG_ADMIN_ACTION_EDITDESTLIST   = "ADM016";
  public static final String MSG_ADMIN_ACTION_EDITGROUPS     = "ADM017";
  public static final String MSG_ADMIN_ACTION_FULLACCESS     = "ADM018";
  public static final String MSG_ADMIN_ACTION_LOCKEFA        = "ADM019";
  public static final String MSG_ADMIN_ACTION_EXECCMD        = "ADM020";
  public static final String MSG_ADMIN_ACTION_EXECCMDFAILED  = "ADM021";
  public static final String MSG_ADMIN_LOGBOOK_ENTRYDELETED  = "ADM022";
  public static final String MSG_ADMIN_LOGBOOK_ENTRYADDED    = "ADM023";
  public static final String MSG_ADMIN_LOGBOOK_ENTRYMODIFIED = "ADM024";
  public static final String MSG_ADMIN_LOGBOOK_CHANGESSAVED  = "ADM025";
  public static final String MSG_ADMIN_LOGBOOK_CHANGESNOTSVD = "ADM026";
  public static final String MSG_ADMIN_ACTION_ADMINSMODIFIED = "ADM027";
  public static final String MSG_ADMIN_ACTION_ADMINCREATED   = "ADM028";
  public static final String MSG_ADMIN_ACTION_ADMINRENAMED   = "ADM029";
  public static final String MSG_ADMIN_ACTION_ADMINCHANGED   = "ADM030";
  public static final String MSG_ADMIN_ACTION_ADMINDELETED   = "ADM031";
  public static final String MSG_ADMIN_BOATSTATECHANGED      = "ADM032";
  public static final String MSG_ADMIN_ALLBOATSTATECHANGED   = "ADM033";
  public static final String MSG_ADMIN_NOBOATSTATECHANGED    = "ADM034";

  // Data Administration (not only Admin Mode)
  public static final String MSG_DATA_NEWMEMBERADDED         = "DAT001";

  // de.nmichael.efa.Logger
  public static final String MSG_LOGGER_ACTIVATING           = "LOG001";
  public static final String MSG_LOGGER_FAILEDCREATELOG      = "LOG002";
  public static final String MSG_LOGGER_DEBUGACTIVATED       = "LOG003";
  public static final String MSG_LOGGER_THRESHOLDEXCEEDED    = "LOG004";

  // de.nmichael.efa.EfaErrorPrintStream
  public static final String MSG_ERROR_EXCEPTION             = "EXC001";

  // de.nmichael.efa.International
  public static final String MSG_INTERNATIONAL_DEBUG         = "INT001";
  public static final String MSG_INTERNATIONAL_FAILEDSETUP   = "INT002";
  public static final String MSG_INTERNATIONAL_MISSINGKEY    = "INT003";
  public static final String MSG_INTERNATIONAL_INCORRECTKEY  = "INT004";

  // de.nmichael.efa.core.DatenListe (and subclasses)
  public static final String MSG_CSVFILE_FILECONVERTED       = "CSV001";
  public static final String MSG_CSVFILE_ERRORCONVERTING     = "CSV002";
  public static final String MSG_CSVFILE_ERRORINVALIDFORMAT  = "CSV003";
  public static final String MSG_CSVFILE_ERRORREADINGFILE    = "CSV004";
  public static final String MSG_CSVFILE_ERRORWRITEFILE      = "CSV005";
  public static final String MSG_CSVFILE_ERRORCREATEFILE     = "CSV006";
  public static final String MSG_CSVFILE_ERRORCLOSINGFILE    = "CSV007";
  public static final String MSG_CSVFILE_INCONSISTENTDATA    = "CSV008";
  public static final String MSG_CSVFILE_CHECKSUMERROR       = "CSV009";
  public static final String MSG_CSVFILE_CHECKSUMCORRECTED   = "CSV010";
  public static final String MSG_CSVFILE_CHECKSUMNOTCORRECTED= "CSV011";
  public static final String MSG_CSVFILE_FILEISBACKUP        = "CSV012";
  public static final String MSG_CSVFILE_FILENEWCREATED      = "CSV013";
  public static final String MSG_CSVFILE_BACKUPERROR         = "CSV014";
  public static final String MSG_CSVFILE_OOMSAVEERROR        = "CSV015";
  public static final String MSG_CSVFILE_ERRORINVALIDRECORD  = "CSV016";
  public static final String MSG_CSVFILE_EXITONERROR         = "CSV017";
  public static final String MSG_CSVFILE_ERRORENCODING       = "CSV018";

  // efa in the Boat House - Events (multiple source files)
  public static final String MSG_EVT_EFASTART                = "EVT001";
  public static final String MSG_EVT_LOCKED                  = "EVT002";
  public static final String MSG_EVT_UNLOCKED                = "EVT003";
  public static final String MSG_EVT_EFAEXIT                 = "EVT004";
  public static final String MSG_EVT_EFAEXITABORTED          = "EVT005";
  public static final String MSG_EVT_EFAEXITEXECCMD          = "EVT006";
  public static final String MSG_EVT_EFARESTART              = "EVT007";
  public static final String MSG_EVT_SUPERADMINCREATED       = "EVT008";
  public static final String MSG_EVT_EFASECURE               = "EVT009";
  public static final String MSG_EVT_NEWLOGBOOKOPENED        = "EVT010";
  public static final String MSG_EVT_EFAREADY                = "EVT011";
  public static final String MSG_EVT_LOGBOOKOPENED           = "EVT012";
  public static final String MSG_EVT_TRIPUNKNOWNBOAT         = "EVT013";
  public static final String MSG_EVT_TRIPSTART               = "EVT014";
  public static final String MSG_EVT_TRIPSTART_BNA           = "EVT015";
  public static final String MSG_EVT_TRIPSTART_CORR          = "EVT016";
  public static final String MSG_EVT_TRIPSTART_CORRUKNW      = "EVT017";
  public static final String MSG_EVT_TRIPSTART_CORRSNOT      = "EVT018";
  public static final String MSG_EVT_TRIPEND                 = "EVT019";
  public static final String MSG_EVT_TRIPABORT               = "EVT020";
  public static final String MSG_EVT_TRIPLATEREC             = "EVT021";
  public static final String MSG_EVT_UNALLOWEDBOATUSAGE      = "EVT022";
  public static final String MSG_EVT_AUTOSTARTNEWLOGBOOK     = "EVT023";
  public static final String MSG_EVT_AUTOSTARTNEWLB_LX       = "EVT024";
  public static final String MSG_EVT_AUTOSTARTNEWLBDONE      = "EVT025";
  public static final String MSG_EVT_AUTONEWLOGROLLBACK      = "EVT026";
  public static final String MSG_EVT_CHECKFORWARNINGS        = "EVT027";
  public static final String MSG_EVT_RESCHECK_AVAIL          = "EVT028";
  public static final String MSG_EVT_RESCHECK_RESFOUND       = "EVT029";
  public static final String MSG_EVT_TIMEBASEDEXIT           = "EVT030";
  public static final String MSG_EVT_TIMEBASEDEXITDELAY      = "EVT031";
  public static final String MSG_EVT_MEMORYSUPERVISOR        = "EVT032";
  public static final String MSG_EVT_LOGFILEARCHIVED         = "EVT033";
  public static final String MSG_EVT_ERRORCNTMSGCLEAR        = "EVT034";

  // efa in the Boat House - Errors
  public static final String MSG_ERR_GENERIC                 = "ERR001";
  public static final String MSG_ERR_UNEXPECTED              = "ERR002";
  public static final String MSG_ERR_PANIC                   = "ERR003";
  public static final String MSG_ERR_EFARUNNING_FAILED       = "ERR004";
  public static final String MSG_ERR_SENDMAILFAILED_PLUGIN   = "ERR005";
  public static final String MSG_ERR_SENDMAILFAILED_CFG      = "ERR006";
  public static final String MSG_ERR_SENDMAILFAILED_ERROR    = "ERR007";
  public static final String MSG_ERR_EFAEXITEXECCMD_FAILED   = "ERR008";
  public static final String MSG_ERR_EFARESTARTEXEC_FAILED   = "ERR009";
  public static final String MSG_ERR_EXITLOWMEMORY           = "ERR010";
  public static final String MSG_ERR_EXITONERROR             = "ERR011";
  public static final String MSG_ERR_NOSUPERADMIN            = "ERR012";
  public static final String MSG_ERR_BOATNOTFOUNDINSTATUS    = "ERR013";
  public static final String MSG_ERR_NOLOGENTRYFORBOAT       = "ERR014";
  public static final String MSG_ERR_TRIPSTARTNOTPOSSIBLE1   = "ERR015";
  public static final String MSG_ERR_AUTOSTARTNEWLOGBOOK     = "ERR016";
  public static final String MSG_ERR_AUTONEWLOGROLLBACK      = "ERR017";
  public static final String MSG_ERR_INCONSISTENTSTATE       = "ERR018";
  public static final String MSG_ERR_CHECKFORWARNINGS        = "ERR019";
  public static final String MSG_ERR_STATISTICNOTFOUND       = "ERR020";
  public static final String MSG_ERR_ERRORCREATINGSTATISTIC  = "ERR021";
  public static final String MSG_ERR_WINDOWSTACK             = "ERR022";

  // efa in the Boat House - Warnings
  public static final String MSG_WARN_EFARUNNING_FAILED      = "WRN001";
  public static final String MSG_WARN_JAVA_VERSION           = "WRN002";
  public static final String MSG_WARN_EFAUNSECURE            = "WRN003";
  public static final String MSG_WARN_BOATADDEDWITHSTATUS1   = "WRN004";
  public static final String MSG_WARN_BOATADDEDWITHSTATUS2   = "WRN005";
  public static final String MSG_WARN_BOATDELETEDFROMLIST    = "WRN006";
  public static final String MSG_WARN_CANTEXECCOMMAND        = "WRN007";
  public static final String MSG_WARN_AUTONEWLOGROLLBACK     = "WRN008";
  public static final String MSG_WARN_MEMORYSUPERVISOR       = "WRN009";
  public static final String MSG_WARN_CANTSETLOOKANDFEEL     = "WRN010";
  public static final String MSG_WARN_CANTSETFONTSIZE        = "WRN011";
  public static final String MSG_WARN_CANTGETEFAJAVAARGS     = "WRN012";
  public static final String MSG_WARN_ERRORCNTMSGEXCEEDED    = "WRN013";
  public static final String MSG_WARN_FONTDOESNOTEXIST       = "WRN014";

  // File Operations
  public static final String MSG_FILE_FILEOPENFAILED         = "FLE001";
  public static final String MSG_FILE_FILEREADFAILED         = "FLE002";
  public static final String MSG_FILE_FILENEWCREATED         = "FLE003";
  public static final String MSG_FILE_FILECREATEFAILED       = "FLE004";
  public static final String MSG_FILE_FILEWRITEFAILED        = "FLE005";
  public static final String MSG_FILE_FILECLOSEFAILED        = "FLE006";
  public static final String MSG_FILE_FILENOTFOUND           = "FLE007";
  public static final String MSG_FILE_FILEALREADYEXISTS      = "FLE008";
  public static final String MSG_FILE_ARCHIVINGFAILED        = "FLE009";
  public static final String MSG_FILE_BACKUPFAILED           = "FLE010";
  public static final String MSG_FILE_DIRECTORYNOTFOUND      = "FLE011";

  // GUI Events & Errors
  public static final String MSG_GUI_ERRORACTIONHANDLER      = "GUI001";

  // Debug Logging
  public static final String MSG_DEBUG_GENERIC               = "DBG001";
  public static final String MSG_DEBUG_EFAWETT               = "DBG002";
  public static final String MSG_DEBUG_STATISTICS            = "DBG003";
  public static final String MSG_DEBUG_EFARUNNING            = "DBG004";
  public static final String MSG_DEBUG_EFABACKGROUNDTASK     = "DBG005";
  public static final String MSG_DEBUG_MEMORYSUPERVISOR      = "DBG006";
  public static final String MSG_DEBUG_SIMPLEFILEPRINTER     = "DBG007";
  public static final String MSG_DEBUG_ELWIZ                 = "DBG008";
  public static final String MSG_DEBUG_EFACONFIG             = "DBG009";
  public static final String MSG_DEBUG_TYPES                 = "DBG010";

  public static boolean debugLogging = false;

  private static volatile long lastLog;
  private static volatile long[] logCount;
  private static volatile boolean doNotLog = false;

  private static NachrichtenAnAdmin nachrichten = null;

  private static String createLogfileName(String logfile) {
    return Daten.efaLogDirectory+logfile;
  }

  public static String ini(String logfile, boolean append) {
      lastLog = 0;
      logCount = new long[2];
      for (int i=0; i<logCount.length; i++) {
          logCount[i] = 0;
      }

      Daten.efaLogfile = (logfile != null ? createLogfileName(logfile) : null);

      String baklog = null;
      if (logfile != null) {
          try {
              // ggf. alte, zu große Logdatei archivieren
              try {
                  // Wenn Logdatei zu groß ist, die alte Logdatei verschieben
                  File log = new File(Daten.efaLogfile);
                  if (log.exists() && log.length() > 1048576) {
                      baklog = EfaUtil.moveAndEmptyFile(Daten.efaLogfile, Daten.efaBaseConfig.efaUserDirectory + "backup" + Daten.fileSep);
                  }
              } catch (Exception e) {
                  LogString.logError_fileArchivingFailed(Daten.efaLogfile, International.getString("Logdatei"));
              }

              Logger.log(Logger.DEBUG, Logger.MSG_LOGGER_ACTIVATING,
                      "Logfile being set to: " + Daten.efaLogfile);

              System.setErr(new EfaErrorPrintStream(new FileOutputStream(Daten.efaLogfile, append)));
          } catch (FileNotFoundException e) {
              Logger.log(Logger.ERROR,
                      Logger.MSG_LOGGER_FAILEDCREATELOG,
                      International.getString("Fehler") + ": " +
                      LogString.logstring_fileCreationFailed(Daten.efaLogfile, International.getString("Logdatei")));
          }
      }

      return baklog;
  }

  /**
   * Log a message.
   * Use this method for loggin!
   * @param type the type of the message, see Logger: Message Types
   * @param key the key for this message, see Logger: Message Keys
   * @param msg the message to be logged
   */
  public static void log(String type, String key, String msg) {
    if (type != null && type.equals(DEBUG) && !debugLogging) return;

    // Error Threshold exceeded?
    if (logCount != null) {
        long now = System.currentTimeMillis() / 1000;
        if (now != lastLog) {
            logCount[(int)(lastLog % logCount.length)] = 0;
            doNotLog = false;
        }
        logCount[(int)(now % logCount.length)]++;
        lastLog = now;
        if (logCount[(int)(now % logCount.length)] >= LOGGING_THRESHOLD) {
            if (doNotLog) {
                // nothing
            } else {
                doNotLog = true;
                Logger.log(ERROR, MSG_LOGGER_THRESHOLDEXCEEDED, "Logging Threshold exceeded.");
                return;
            }
        }
    }

    Calendar cal = new GregorianCalendar();
    String t = "[" + EfaUtil.getCurrentTimeStamp() + "] - " + Daten.applPID + " - " + type + " - " + key +  " - " + msg;
    EfaErrorPrintStream.ignoreExceptions = true; // Damit Exception-Ausschriften nicht versehentlich als echte Exceptions gemeldet werden
    System.err.println(EfaUtil.replace(t,"\n"," ",true));
    EfaErrorPrintStream.ignoreExceptions = false;

    if (type != null && type.equals(ERROR) && nachrichten != null) {
      if (Daten.efaConfig == null || Daten.efaConfig.efaDirekt_bnrError_admin) {
        mailError(key, t, Nachricht.ADMIN);
      }
      if (Daten.efaConfig != null && Daten.efaConfig.efaDirekt_bnrError_bootswart) {
        mailError(key, t, Nachricht.BOOTSWART);
      }
    }
  }

  /**
   * Log a message with the key "GENERIC".
   * @deprecated use log(String, String, String) instead!
   * @param type the type of the message, see Logger: Message Types
   * @param msg the message to be logged
   */
  public static void log(String type, String msg) {
      log(type, Logger.MSG_GENERIC, msg);
  }

  private static void mailError(String key, String msg, int to) {
    String txt = International.getString("Dies ist eine automatisch erstellte Fehlermeldung von efa.") + "\n" +
            International.getString("Folgender Fehler ist aufgetreten:") + "\n" + msg;
    if (key != null && key.equals(Logger.MSG_ERROR_EXCEPTION)) {
      txt += "\n\n" + International.getString("Programm-Information") + ":\n============================================\n";
      Vector info = Daten.getEfaInfos();
      for (int i=0; info != null && i<info.size(); i++) txt += (String)info.get(i)+"\n";
    }
    nachrichten.createNachricht(Daten.EFA_SHORTNAME,to,International.getString("FEHLER"),txt);
  }

  public static void setNachrichtenAnAdmin(NachrichtenAnAdmin _nachrichten) {
    nachrichten = _nachrichten;
  }

  public static boolean isWarningLine(String s) {
    return (s != null && s.indexOf(Logger.WARNING) == 32);
  }

  public static long getLineTimestamp(String s) {
    if (s == null || s.length() < 21) return 0;
    TMJ datum = EfaUtil.string2date(s.substring(1,11),1,1,1980);
    TMJ zeit = EfaUtil.string2date(s.substring(12,20),0,0,0);
    return EfaUtil.dateTime2Cal(datum,zeit).getTimeInMillis();
  }

}