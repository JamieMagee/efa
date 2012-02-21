/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.util;

import de.nmichael.efa.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.IDataAccess;
import java.io.*;
import java.util.*;

// @i18n complete
public class Logger {

    private static final int LOGGING_THRESHOLD     = 1000; // max LOGGING_THRESHOLD logging messages per second
    private static final int LOGGING_THRESHOLD_ERR = 100; // max LOGGING_THRESHOLD logging messages per second

    // Message Types
    public static final String ERROR = "ERROR";
    public static final String INFO = "INFO";
    public static final String WARNING = "WARNING";
    public static final String ACTION = "ACTION";
    public static final String DEBUG = "DEBUG";
    public static final String INPUT = "INPUT";
    public static final String OUTPUT = "OUTPUT";
    // Message Keys
    public static final String MSG_GENERIC = "GEN001";
    public static final String MSG_GENERIC_ERROR = "GEN002";
    // Core Functionality Informations (multiple source files)
    public static final String MSG_INFO_CONFIGURATION = "INF001";
    public static final String MSG_INFO_VERSION = "INF002";
    // Core Functionality (multiple source files)
    public static final String MSG_CORE_HALT = "COR001";
    public static final String MSG_CORE_SETUPDIRS = "COR002";
    public static final String MSG_CORE_SETUPFILES = "COR003";
    public static final String MSG_CORE_INFOFAILED = "COR004";
    public static final String MSG_CORE_BASICCONFIG = "COR005";
    public static final String MSG_CORE_BASICCONFIGFAILEDCREATE = "COR006";
    public static final String MSG_CORE_BASICCONFIGFAILEDOPEN = "COR007";
    public static final String MSG_CORE_LANGUAGESUPPORT = "COR008";
    public static final String MSG_CORE_EFACONFIGCREATEDNEW = "COR009";
    public static final String MSG_CORE_EFACONFIGFAILEDCREATE = "COR010";
    public static final String MSG_CORE_EFACONFIGFAILEDOPEN = "COR011";
    public static final String MSG_CORE_EFACONFIGFAILEDSTVALUES = "COR012";
    public static final String MSG_CORE_EFATYPESCREATEDNEW = "COR013";
    public static final String MSG_CORE_EFATYPESFAILEDCREATE = "COR014";
    public static final String MSG_CORE_EFATYPESFAILEDOPEN = "COR015";
    public static final String MSG_CORE_EFATYPESFAILEDSETVALUES = "COR016";
    public static final String MSG_CORE_EFASECCREATED = "COR017";
    public static final String MSG_CORE_EFASECFAILEDCREATE = "COR018";
    public static final String MSG_CORE_EFASECCORRUPTED = "COR019";
    public static final String MSG_CORE_CONFBACKUPDIRNOTEXIST = "COR020";
    public static final String MSG_CORE_EFAALREADYRUNNING = "COR021";
    public static final String MSG_CORE_UNKNOWNDATAFIELD = "COR022";
    public static final String MSG_CORE_UNSUPPORTEDDATATYPE = "COR023";
    public static final String MSG_CORE_DATATYPEINVALIDVALUE = "COR024";
    public static final String MSG_CORE_MISSINGPLUGIN = "COR025";
    public static final String MSG_CORE_ADMINSCREATEDNEW = "COR026";
    public static final String MSG_CORE_ADMINSFAILEDCREATE = "COR027";
    public static final String MSG_CORE_ADMINSFAILEDOPEN = "COR028";
    public static final String MSG_CORE_ADMINSFAILEDNOSEC = "COR029";
    public static final String MSG_CORE_BASICCONFIGUSERDATACANTWRITE = "COR030";
    // Activities performed in Admin Mode
    public static final String MSG_ADMIN_LOGIN = "ADM001";
    public static final String MSG_ADMIN_LOGINFAILURE = "ADM002";
    public static final String MSG_ADMIN_ADMINMODEEXITED = "ADM003";
    public static final String MSG_ADMIN_ACTION_ADMINS = "ADM004";
    public static final String MSG_ADMIN_ACTION_CONFNEWLOGBOOK = "ADM005";
    public static final String MSG_ADMIN_ACTION_OPENLOGBOOK = "ADM006";
    public static final String MSG_ADMIN_ACTION_EDITLOGBOOK = "ADM007";
    public static final String MSG_ADMIN_ACTION_EDITLOGBOOKDONE = "ADM008";
    public static final String MSG_ADMIN_ACTION_EDITBOATSTATUS = "ADM009";
    public static final String MSG_ADMIN_ACTION_VIEWMESSAGES = "ADM010";
    public static final String MSG_ADMIN_ACTION_VIEWLOGFILE = "ADM011";
    public static final String MSG_ADMIN_ACTION_EDITCONFIG = "ADM012";
    public static final String MSG_ADMIN_ACTION_STATISTICS = "ADM013";
    public static final String MSG_ADMIN_ACTION_EDITBOATLIST = "ADM014";
    public static final String MSG_ADMIN_ACTION_EDITMEMBERLIST = "ADM015";
    public static final String MSG_ADMIN_ACTION_EDITDESTLIST = "ADM016";
    public static final String MSG_ADMIN_ACTION_EDITGROUPS = "ADM017";
    public static final String MSG_ADMIN_ACTION_FULLACCESS = "ADM018";
    public static final String MSG_ADMIN_ACTION_LOCKEFA = "ADM019";
    public static final String MSG_ADMIN_ACTION_EXECCMD = "ADM020";
    public static final String MSG_ADMIN_ACTION_EXECCMDFAILED = "ADM021";
    public static final String MSG_ADMIN_LOGBOOK_ENTRYDELETED = "ADM022";
    public static final String MSG_ADMIN_LOGBOOK_ENTRYADDED = "ADM023";
    public static final String MSG_ADMIN_LOGBOOK_ENTRYMODIFIED = "ADM024";
    public static final String MSG_ADMIN_ACTION_ADMINSMODIFIED = "ADM027";
    public static final String MSG_ADMIN_ACTION_ADMINCREATED = "ADM028";
    public static final String MSG_ADMIN_ACTION_ADMINRENAMED = "ADM029";
    public static final String MSG_ADMIN_ACTION_ADMINCHANGED = "ADM030";
    public static final String MSG_ADMIN_ACTION_ADMINDELETED = "ADM031";
    public static final String MSG_ADMIN_BOATSTATECHANGED = "ADM032";
    public static final String MSG_ADMIN_ALLBOATSTATECHANGED = "ADM033";
    public static final String MSG_ADMIN_NOBOATSTATECHANGED = "ADM034";
    // Data Administration (not only Admin Mode)
    public static final String MSG_DATAADM_NEWMEMBERADDED = "DAD001";
    // de.nmichael.efa.Logger
    public static final String MSG_LOGGER_ACTIVATING = "LOG001";
    public static final String MSG_LOGGER_FAILEDCREATELOG = "LOG002";
    public static final String MSG_LOGGER_DEBUGACTIVATED = "LOG003";
    public static final String MSG_LOGGER_DEBUGDEACTIVATED = "LOG004";
    public static final String MSG_LOGGER_THRESHOLDEXCEEDED = "LOG005";
    public static final String MSG_LOGGER_TRACETOPIC = "LOG006";
    // de.nmichael.efa.EfaErrorPrintStream
    public static final String MSG_ERROR_EXCEPTION = "EXC001";
    // de.nmichael.efa.International
    public static final String MSG_INTERNATIONAL_DEBUG = "INT001";
    public static final String MSG_INTERNATIONAL_FAILEDSETUP = "INT002";
    public static final String MSG_INTERNATIONAL_MISSINGKEY = "INT003";
    public static final String MSG_INTERNATIONAL_INCORRECTKEY = "INT004";
    // de.nmichael.data
    public static final String MSG_DATA_GENERICEXCEPTION = "DAT001";
    public static final String MSG_DATA_FIELDDOESNOTEXIST = "DAT002";
    public static final String MSG_DATA_CREATEFAILED = "DAT003";
    public static final String MSG_DATA_OPENFAILED = "DAT004";
    public static final String MSG_DATA_CLOSEFAILED = "DAT005";
    public static final String MSG_DATA_SAVEFAILED = "DAT006";
    public static final String MSG_DATA_READFAILED = "DAT007";
    public static final String MSG_DATA_WRITEFAILED = "DAT008";
    public static final String MSG_DATA_DELETEFAILED = "DAT009";
    public static final String MSG_DATA_GETLOCKFAILED = "DAT010";
    public static final String MSG_DATA_LOCKTIMEOUT = "DAT011";
    public static final String MSG_DATA_RECORDNOTFOUND = "DAT012";
    public static final String MSG_DATA_DUPLICATERECORD = "DAT013";
    public static final String MSG_DATA_MODIFICATIONFAILED = "DAT014";
    public static final String MSG_DATA_INVALIDVERSIONIZEDDATA = "DAT015";
    public static final String MSG_DATA_VERSIONIZEDDATACONFLICT = "DAT016";
    public static final String MSG_DATA_INCONSISTENTDATA = "DAT017";
    public static final String MSG_DATA_RECORDWRONGTYPE = "DAT018";
    public static final String MSG_DATA_MODIFYEXCEPTION = "DAT019";
    public static final String MSG_DATA_INVALIDHEADER = "DAT020";
    public static final String MSG_DATA_JOURNALOPENFAILED = "DAT021";
    public static final String MSG_DATA_JOURNALWRITEFAILED = "DAT022";
    public static final String MSG_DATA_JOURNALLOGFAILED = "DAT023";
    public static final String MSG_DATA_INVALIDPARAMETER = "DAT024";
    public static final String MSG_DATA_FILESIZEHIGH     = "DAT025";
    public static final String MSG_DATA_FILEARCHIVED     = "DAT026";
    public static final String MSG_DATA_FILEBACKUPFAILED = "DAT027";
    public static final String MSG_DATA_UPDATECONFLICT   = "DAT028";
    public static final String MSG_DATA_TRUNCATEFAILED   = "DAT029";
    public static final String MSG_DATA_COPYFROMDATAACCESSFAILED  = "DAT030";
    public static final String MSG_DATA_ACCESSFAILED  = "DAT031";
    public static final String MSG_DATA_RECOVERYSTART  = "DAT032";
    public static final String MSG_DATA_RECOVERYFINISHED  = "DAT033";
    public static final String MSG_DATA_REPLAYSTART  = "DAT034";
    public static final String MSG_DATA_REPLAYFINISHED  = "DAT035";
    public static final String MSG_DATA_REPLAYNOJOURNAL  = "DAT036";
    public static final String MSG_DATA_RECOVERYORIGMOVED  = "DAT037";
    public static final String MSG_DATA_AUDIT = "DAT038";
    public static final String MSG_DATA_AUDIT_INVALIDREC = "DAT039";
    public static final String MSG_DATA_AUDIT_REFTOTEXT = "DAT040";
    public static final String MSG_DATA_AUDIT_TEXTTOREF = "DAT041";
    public static final String MSG_DATA_AUDIT_INVALIDREFFOUND = "DAT042";
    public static final String MSG_DATA_AUDIT_INVALIDREFDELETED = "DAT043";
    public static final String MSG_DATA_AUDIT_BOATINCONSISTENCY = "DAT044";
    public static final String MSG_DATA_AUDIT_RECPURGED = "DAT045";
    public static final String MSG_DATA_AUDIT_RECNOTFOUND = "DAT046";
    public static final String MSG_DATA_AUDIT_LOGBOOKERROR = "DAT047";
    public static final String MSG_DATA_AUDIT_BOATSTATUSCORRECTED = "DAT048";
    
    public static final String MSG_REFA_SERVERSTATUS                 = "RMT001";
    public static final String MSG_REFA_SERVERERROR                  = "RMT002";
    public static final String MSG_REFA_SERVERLOG                    = "RMT003";
    public static final String MSG_REFA_DEBUGCOMMUNICATION           = "RMT004";
    public static final String MSG_REFA_ERRORCOMMUNICATION           = "RMT005";
    public static final String MSG_REFA_REQUESTFAILED                = "RMT006";
    public static final String MSG_REFA_INVALIDREQUEST               = "RMT007";
    public static final String MSG_REFA_INVALIDRESPONSE              = "RMT008";
    public static final String MSG_REFA_UNEXPECTEDRESPONSE           = "RMT009";
    public static final String MSG_REFA_LOGINFAILURE                 = "RMT010";
    public static final String MSG_REFA_STATISTICS                   = "RMT011";
    public static final String MSG_REFA_TRACECOMMUNICATION           = "RMT012";
    public static final String MSG_REFA_ERRORTIMEOUT                 = "RMT013";

    public static final String MSG_EONL_ERROR                        = "ONL001";
    public static final String MSG_EONL_WARNING                      = "ONL002";
    public static final String MSG_EONL_DEBUG                        = "ONL003";

    // de.nmichael.efa.core.DatenListe (and subclasses)
    public static final String MSG_CSVFILE_FILECONVERTED = "CSV001";
    public static final String MSG_CSVFILE_ERRORCONVERTING = "CSV002";
    public static final String MSG_CSVFILE_ERRORINVALIDFORMAT = "CSV003";
    public static final String MSG_CSVFILE_ERRORREADINGFILE = "CSV004";
    public static final String MSG_CSVFILE_ERRORWRITEFILE = "CSV005";
    public static final String MSG_CSVFILE_ERRORCREATEFILE = "CSV006";
    public static final String MSG_CSVFILE_ERRORCLOSINGFILE = "CSV007";
    public static final String MSG_CSVFILE_INCONSISTENTDATA = "CSV008";
    public static final String MSG_CSVFILE_CHECKSUMERROR = "CSV009";
    public static final String MSG_CSVFILE_CHECKSUMCORRECTED = "CSV010";
    public static final String MSG_CSVFILE_CHECKSUMNOTCORRECTED = "CSV011";
    public static final String MSG_CSVFILE_FILEISBACKUP = "CSV012";
    public static final String MSG_CSVFILE_FILENEWCREATED = "CSV013";
    public static final String MSG_CSVFILE_BACKUPERROR = "CSV014";
    public static final String MSG_CSVFILE_OOMSAVEERROR = "CSV015";
    public static final String MSG_CSVFILE_ERRORINVALIDRECORD = "CSV016";
    public static final String MSG_CSVFILE_EXITONERROR = "CSV017";
    public static final String MSG_CSVFILE_ERRORENCODING = "CSV018";

    // efa in the Boat House - Events (multiple source files)
    public static final String MSG_EVT_EFASTART = "EVT001";
    public static final String MSG_EVT_LOCKED = "EVT002";
    public static final String MSG_EVT_UNLOCKED = "EVT003";
    public static final String MSG_EVT_EFAEXIT = "EVT004";
    public static final String MSG_EVT_EFAEXITABORTED = "EVT005";
    public static final String MSG_EVT_EFAEXITEXECCMD = "EVT006";
    public static final String MSG_EVT_EFARESTART = "EVT007";
    public static final String MSG_EVT_SUPERADMINCREATED = "EVT008";
    public static final String MSG_EVT_EFASECURE = "EVT009";
    public static final String MSG_EVT_NEWLOGBOOKOPENED = "EVT010";
    public static final String MSG_EVT_PROJECTOPENED = "EVT011";
    public static final String MSG_EVT_LOGBOOKOPENED = "EVT012";
    public static final String MSG_EVT_EFAREADY = "EVT013";
    public static final String MSG_EVT_TRIPUNKNOWNBOAT = "EVT014";
    public static final String MSG_EVT_TRIPSTART = "EVT015";
    public static final String MSG_EVT_TRIPSTART_BNA = "EVT016";
    public static final String MSG_EVT_TRIPSTART_CORR = "EVT017";
    public static final String MSG_EVT_TRIPSTART_CORRUKNW = "EVT018";
    public static final String MSG_EVT_TRIPSTART_CORRSNOT = "EVT019";
    public static final String MSG_EVT_TRIPEND = "EVT020";
    public static final String MSG_EVT_TRIPABORT = "EVT021";
    public static final String MSG_EVT_TRIPLATEREC = "EVT022";
    public static final String MSG_EVT_UNALLOWEDBOATUSAGE = "EVT023";
    public static final String MSG_EVT_AUTOSTARTNEWLOGBOOK = "EVT024";
    public static final String MSG_EVT_AUTOSTARTNEWLBSTEP = "EVT025";
    public static final String MSG_EVT_AUTOSTARTNEWLBDONE = "EVT026";
    public static final String MSG_EVT_AUTONEWLOGROLLBACK = "EVT027";
    public static final String MSG_EVT_CHECKFORWARNINGS = "EVT028";
    public static final String MSG_EVT_RESCHECK_AVAIL = "EVT029";
    public static final String MSG_EVT_RESCHECK_RESFOUND = "EVT030";
    public static final String MSG_EVT_TIMEBASEDEXIT = "EVT031";
    public static final String MSG_EVT_TIMEBASEDEXITDELAY = "EVT032";
    public static final String MSG_EVT_INACTIVITYBASEDEXIT = "EVT033";
    public static final String MSG_EVT_MEMORYSUPERVISOR = "EVT034";
    public static final String MSG_EVT_LOGFILEARCHIVED = "EVT035";
    public static final String MSG_EVT_ERRORCNTMSGCLEAR = "EVT036";
    public static final String MSG_EVT_ERRORADDRECORDOUTOFRANGE = "EVT037";
    public static final String MSG_EVT_TIMEBASEDRESTART = "EVT038";
    public static final String MSG_EVT_TIMEBASEDRESTARTDELAY = "EVT039";
    public static final String MSG_EVT_ERRORSAVELOGBOOKENTRY = "EVT040";
    public static final String MSG_EVT_ERRORNOBOATSTATUSFORBOAT = "EVT041";
    public static final String MSG_EVT_REMOTEEFAEXIT = "EVT042";
    public static final String MSG_EVT_REMOTEONLINEUPDATE = "EVT043";
    public static final String MSG_EVT_REMOTEONLINEUPDATEDOWNLOAD = "EVT044";
    public static final String MSG_EVT_ONLINEUPDATEFINISHED = "EVT045";
    public static final String MSG_EVT_ERRORRECORDINVALIDATTIME = "EVT046";

    // Backup
    public static final String MSG_BCK_BACKUPSTARTED = "BCK001";

    // efa in the Boat House - Errors
    public static final String MSG_ERR_GENERIC = "ERR001";
    public static final String MSG_ERR_UNEXPECTED = "ERR002";
    public static final String MSG_ERR_PANIC = "ERR003";
    public static final String MSG_ERR_EFARUNNING_FAILED = "ERR004";
    public static final String MSG_ERR_SENDMAILFAILED_PLUGIN = "ERR005";
    public static final String MSG_ERR_SENDMAILFAILED_CFG = "ERR006";
    public static final String MSG_ERR_SENDMAILFAILED_ERROR = "ERR007";
    public static final String MSG_ERR_EFAEXITEXECCMD_FAILED = "ERR008";
    public static final String MSG_ERR_EFARESTARTEXEC_FAILED = "ERR009";
    public static final String MSG_ERR_EXITLOWMEMORY = "ERR010";
    public static final String MSG_ERR_EXITONERROR = "ERR011";
    public static final String MSG_ERR_NOSUPERADMIN = "ERR012";
    public static final String MSG_ERR_BOATNOTFOUNDINSTATUS = "ERR013";
    public static final String MSG_ERR_NOLOGENTRYFORBOAT = "ERR014";
    public static final String MSG_ERR_TRIPSTARTNOTPOSSIBLE1 = "ERR015";
    public static final String MSG_ERR_AUTOSTARTNEWLOGBOOK = "ERR016";
    public static final String MSG_ERR_AUTONEWLOGROLLBACK = "ERR017";
    public static final String MSG_ERR_INCONSISTENTSTATE = "ERR018";
    public static final String MSG_ERR_CHECKFORWARNINGS = "ERR019";
    public static final String MSG_ERR_STATISTICNOTFOUND = "ERR020";
    public static final String MSG_ERR_ERRORCREATINGSTATISTIC = "ERR021";
    public static final String MSG_ERR_WINDOWSTACK = "ERR022";
    public static final String MSG_ERR_NOXMLPARSER = "ERR023";
    public static final String MSG_ERR_NOPROJECTOPENED = "ERR024";
    public static final String MSG_ERR_NOLOGBOOKOPENED = "ERR025";
    public static final String MSG_ERR_DATALOCK_FAILED = "ERR026";
    public static final String MSG_ERR_DATALOCK_CONFLICT = "ERR027";

    // efa in the Boat House - Warnings
    public static final String MSG_WARN_EFARUNNING_FAILED = "WRN001";
    public static final String MSG_WARN_JAVA_VERSION = "WRN002";
    public static final String MSG_WARN_EFAUNSECURE = "WRN003";
    public static final String MSG_WARN_BOATADDEDWITHSTATUS1 = "WRN004";
    public static final String MSG_WARN_BOATADDEDWITHSTATUS2 = "WRN005";
    public static final String MSG_WARN_BOATDELETEDFROMLIST = "WRN006";
    public static final String MSG_WARN_CANTEXECCOMMAND = "WRN007";
    public static final String MSG_WARN_AUTONEWLOGROLLBACK = "WRN008";
    public static final String MSG_WARN_MEMORYSUPERVISOR = "WRN009";
    public static final String MSG_WARN_CANTSETLOOKANDFEEL = "WRN010";
    public static final String MSG_WARN_CANTSETFONTSIZE = "WRN011";
    public static final String MSG_WARN_CANTGETEFAJAVAARGS = "WRN012";
    public static final String MSG_WARN_ERRORCNTMSGEXCEEDED = "WRN013";
    public static final String MSG_WARN_FONTDOESNOTEXIST = "WRN014";
    public static final String MSG_WARN_WEATHERUPDATEFAILED = "WRN015";
    public static final String MSG_WARN_SAVEMESSAGE = "WRN016";
    // File Operations
    public static final String MSG_FILE_FILEOPENFAILED = "FLE001";
    public static final String MSG_FILE_FILEREADFAILED = "FLE002";
    public static final String MSG_FILE_FILENEWCREATED = "FLE003";
    public static final String MSG_FILE_FILESUCCESSFULLYCREATED = "FLE004";
    public static final String MSG_FILE_FILECREATEFAILED = "FLE005";
    public static final String MSG_FILE_FILEWRITEFAILED = "FLE006";
    public static final String MSG_FILE_FILECLOSEFAILED = "FLE007";
    public static final String MSG_FILE_FILENOTFOUND = "FLE008";
    public static final String MSG_FILE_FILEALREADYEXISTS = "FLE009";
    public static final String MSG_FILE_ARCHIVINGFAILED = "FLE010";
    public static final String MSG_FILE_BACKUPFAILED = "FLE011";
    public static final String MSG_FILE_DIRECTORYNOTFOUND = "FLE012";
    public static final String MSG_FILE_WRITETHREAD_RUNNING = "FLE113";
    public static final String MSG_FILE_WRITETHREAD_SAVING = "FLE114";
    public static final String MSG_FILE_WRITETHREAD_ERROR = "FLE115";
    public static final String MSG_FILE_WRITETHREAD_EXIT = "FLE116";
    public static final String MSG_FILE_XMLTRACE = "FLE117";
    public static final String MSG_FILE_XMLWARNING = "FLE118";
    public static final String MSG_FILE_XMLERROR = "FLE119";
    public static final String MSG_FILE_XMLFALATERROR = "FLE120";
    public static final String MSG_FILE_XMLPARSER = "FLE121";
    public static final String MSG_FILE_PARSEERROR = "FLE122";
    public static final String MSG_FILE_XMLFILEINCOMPLETE = "FLE123";
    // GUI Events & Errors
    public static final String MSG_GUI_ERRORACTIONHANDLER = "GUI001";
    public static final String MSG_GUI_DEBUGGUI = "GUI002";
    // Help System
    public static final String MSG_HELP_ERRORHELPSET = "HLP001";
    public static final String MSG_HELP_ERRORHELPBROKER = "HLP002";
    public static final String MSG_HELP_DEBUGHELPTOPICFRAMEOPENED = "HLP003";
    public static final String MSG_HELP_DEBUGHELPTOPICTRYHELP = "HLP004";
    public static final String MSG_HELP_DEBUGHELPTOPICTNOTFOUND = "HLP005";
    public static final String MSG_HELP_DEBUGHELPTOPICFALLBACK = "HLP006";
    public static final String MSG_HELP_DEBUGHELPTOPICSHOWHELP = "HLP007";

    // Synchronization
    public static final String MSG_SYNC_ERRORCONFIG = "SNC001";
    public static final String MSG_SYNC_SYNCINFO = "SNC002";
    public static final String MSG_SYNC_ERRORLOGIN = "SNC003";
    public static final String MSG_SYNC_ERRORINVALIDRESPONSE = "SNC004";
    public static final String MSG_SYNC_ERRORABORTSYNC = "SNC005";
    public static final String MSG_SYNC_WARNINCORRECTRESPONSE = "SNC006";
    public static final String MSG_SYNC_SYNCDEBUG = "SNC007";

    // Backup
    public static final String MSG_BACKUP_BACKUPSTARTED = "BCK001";
    public static final String MSG_BACKUP_BACKUPINFO = "BCK002";
    public static final String MSG_BACKUP_BACKUPERROR = "BCK003";
    public static final String MSG_BACKUP_BACKUPFINISHEDINFO = "BCK004";
    public static final String MSG_BACKUP_BACKUPFINISHEDWITHERRORS = "BCK005";
    public static final String MSG_BACKUP_BACKUPFINISHED = "BCK006";
    public static final String MSG_BACKUP_BACKUPFAILED = "BCK007";
    public static final String MSG_BACKUP_BACKUPDEBUG = "BCK008";
    public static final String MSG_BACKUP_RESTORESTARTED = "BCK009";
    public static final String MSG_BACKUP_RESTOREINFO = "BCK010";
    public static final String MSG_BACKUP_RESTOREERROR = "BCK011";
    public static final String MSG_BACKUP_RESTOREFINISHEDINFO = "BCK012";
    public static final String MSG_BACKUP_RESTOREFINISHEDWITHERRORS = "BCK013";
    public static final String MSG_BACKUP_RESTOREFINISHED = "BCK014";
    public static final String MSG_BACKUP_RESTOREFAILED = "BCK015";
    public static final String MSG_BACKUP_RESTOREDEBUG = "BCK016";

    // Debug Logging
    public static final String MSG_DEBUG_GENERIC = "DBG001";
    public static final String MSG_DEBUG_EFAWETT = "DBG002";
    public static final String MSG_DEBUG_STATISTICS = "DBG003";
    public static final String MSG_DEBUG_EFARUNNING = "DBG004";
    public static final String MSG_DEBUG_EFABACKGROUNDTASK = "DBG005";
    public static final String MSG_DEBUG_MEMORYSUPERVISOR = "DBG006";
    public static final String MSG_DEBUG_SIMPLEFILEPRINTER = "DBG007";
    public static final String MSG_DEBUG_ELWIZ = "DBG008";
    public static final String MSG_DEBUG_EFACONFIG = "DBG009";
    public static final String MSG_DEBUG_TYPES = "DBG010";
    public static final String MSG_DEBUG_GUI_CONTEXTMENU = "DBG011";
    public static final String MSG_DEBUG_IGNOREDEXCEPTION = "DBG012";
    public static final String MSG_DEBUG_GUI_WINDOWS = "DBG013";
    public static final String MSG_DEBUG_GUI_ICONS = "DBG014";
    public static final String MSG_DEBUG_SENDMAIL = "DBG015";
    public static final String MSG_DEBUG_LOGBOOK = "DBG016";
    // CLI
    public static final String MSG_CLI_INFO  = "CLI001";
    public static final String MSG_CLI_ERROR = "CLI002";
    public static final String MSG_CLI_INPUT = "CLI003";
    public static final String MSG_CLI_OUTPUT = "CLI004";

    // Trace Topics for Debug Logging
    public static final long TT_CORE = Integer.parseInt("0000000000000001", 2); // 0x0001
    public static final long TT_OTHER = Integer.parseInt("0000000000000010", 2); // 0x0002
    public static final long TT_INTERNATIONALIZATION = Integer.parseInt("0000000000000100", 2); // 0x0004
    public static final long TT_EFATYPES = Integer.parseInt("0000000000001000", 2); // 0x0008
    public static final long TT_BACKGROUND = Integer.parseInt("0000000000010000", 2); // 0x0010
    public static final long TT_MEMORYSUPERVISION = Integer.parseInt("0000000000100000", 2); // 0x0020
    public static final long TT_FILEIO = Integer.parseInt("0000000001000000", 2); // 0x0040
    public static final long TT_XMLFILE = Integer.parseInt("0000000010000000", 2); // 0x0080
    public static final long TT_GUI = Integer.parseInt("0000000100000000", 2); // 0x0100
    public static final long TT_PRINTER = Integer.parseInt("0000001000000000", 2); // 0x0200
    public static final long TT_STATISTICS = Integer.parseInt("0000010000000000", 2); // 0x0400
    public static final long TT_EXCEPTIONS = Integer.parseInt("0000100000000000", 2); // 0x0800
    public static final long TT_HELP = Integer.parseInt("0001000000000000", 2); // 0x1000
    public static final long TT_SYNC = Integer.parseInt("0010000000000000", 2); // 0x2000
    public static final long TT_REMOTEEFA = Integer.parseInt("0100000000000000", 2); // 0x4000
    public static final long TT_PDF = Integer.parseInt("1000000000000000", 2); // 0x8000

    // Debug Logging and Trace Topics
    private static boolean debugLogging = false;
    private static long globalTraceTopic = 0;
    private static int globalTraceLevel = 1;
    private static boolean debugLoggingActivatedByCommandLine = false; // if set by Command Line, this overwrites any configuration in EfaConfig
    private static boolean globalTraceTopicSetByCommandLine = false;   // if set by Command Line, this overwrites any configuration in EfaConfig
    private static boolean globalTraceLevelSetByCommandLine = false;   // if set by Command Line, this overwrites any configuration in EfaConfig
    private static volatile long lastLog;
    private static volatile long[] logCount;
    private static volatile boolean doNotLog = false;
    private static volatile boolean inMailError = false;
    private static volatile boolean inLogging = false;
    private static boolean alsoLogToStdOut;

    private static String createLogfileName(String logfile) {
        return Daten.efaLogDirectory + logfile;
    }

    public static String ini(String logfile, boolean append, boolean alsoStdOut) {
        lastLog = 0;
        logCount = new long[2];
        alsoLogToStdOut = alsoStdOut;
        for (int i = 0; i < logCount.length; i++) {
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
                        International.getString("Fehler") + ": "
                        + LogString.fileCreationFailed(Daten.efaLogfile, International.getString("Logdatei")));
            }
        }

        return baklog;
    }

    /**
     * Log a message.
     * Use this method for loggin!
     * @param type the type of the message, see Logger: Message Types
     * @param key the key for this message, see Logger: Message Keys
     * @param txt the message to be logged
     */
    public static void log(String type, String key, String txt) {
        log(type, key, txt, type != null && type.equals(ERROR));
    }

    /**
     * Log a message.
     * Use this method for loggin!
     * @param type the type of the message, see Logger: Message Types
     * @param key the key for this message, see Logger: Message Keys
     * @param txt the message to be logged
     * @param sendmail
     */
    public static void log(String type, String key, String txt, boolean msgToAdmin) {
        if (inLogging) {
            return; // avoid recursion
        }
        inLogging = true;
        try {
            if (type != null && type.equals(DEBUG) && !debugLogging) {
                inLogging = false;
                return;
            }

            // Error Threshold exceeded?
            if (logCount != null) {
                long now = System.currentTimeMillis() / 1000;
                if (now != lastLog) {
                    logCount[(int) (lastLog % logCount.length)] = 0;
                    doNotLog = false;
                }
                logCount[(int) (now % logCount.length)]++;
                lastLog = now;
                if (logCount[(int) (now % logCount.length)] >= LOGGING_THRESHOLD
                        || (type.equals(ERROR) && logCount[(int) (now % logCount.length)] >= LOGGING_THRESHOLD_ERR)) {
                    if (doNotLog) {
                        // nothing
                    } else {
                        doNotLog = true;
                        Logger.log(ERROR, MSG_LOGGER_THRESHOLDEXCEEDED, "Logging Threshold exceeded.");
                        inLogging = false;
                        return;
                    }
                }
            }

            if (alsoLogToStdOut && !type.equals(DEBUG)) {
                if (type != null && !type.equals(INPUT))  {
                    System.out.println(EfaUtil.getString(type, 7) + " - " + key + " - " + txt);
                } else {
                    System.out.print(EfaUtil.getString(type, 7) + " - " + key + " - " + txt);
                }
            }
            String t = "[" + EfaUtil.getCurrentTimeStamp() + "] - " + EfaUtil.getString(Daten.applName, 7) + " - " + Daten.applPID + " - " + EfaUtil.getString(type, 7) + " - " + key + " - " + txt;
            if (type != null && !type.equals(INPUT) && !type.equals(OUTPUT))  {
                Calendar cal = new GregorianCalendar();
                EfaErrorPrintStream.ignoreExceptions = true; // Damit Exception-Ausschriften nicht versehentlich als echte Exceptions gemeldet werden
                System.err.println(EfaUtil.replace(t, "\n", " ", true));
                EfaErrorPrintStream.ignoreExceptions = false;
            }

            if (msgToAdmin && Daten.project != null) {

                Messages messages = (Daten.project != null && 
                        !Daten.project.isInOpeningProject() &&
                        Daten.project.getProjectStorageType() != IDataAccess.TYPE_EFA_REMOTE ?
                           Daten.project.getMessages(false) : null);
                if (messages == null || !messages.isOpen()) {
                    inLogging = false;
                    return;
                }
                if ((Daten.efaConfig == null || Daten.efaConfig.getValueEfaDirekt_bnrError_admin())) {
                    mailError(key, t, MessageRecord.TO_ADMIN);
                }
                if ((Daten.efaConfig.getValueEfaDirekt_bnrError_bootswart())) {
                    mailError(key, t, MessageRecord.TO_BOATMAINTENANCE);
                }
            }
        } catch (Exception eignore) {
        } finally {
            inLogging = false;
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

    public static void logdebug(Exception e) {
        log(DEBUG, MSG_DEBUG_IGNOREDEXCEPTION, e);
    }

    public static void log(Exception e) {
        log(ERROR, MSG_ERROR_EXCEPTION, e);
    }

    public static void log(String type, String key, Exception e) {
        if (e == null) {
            return;
        }
        StringBuilder s = new StringBuilder();
        s.append(e.toString());
        StackTraceElement[] stack = e.getStackTrace();
        for (int i = 0; stack != null && i < stack.length; i++) {
            s.append("\n" + stack[i].toString());
        }
        log(type, key, s.toString());
        EfaErrorPrintStream.ignoreExceptions = true;
        if (!DEBUG.equals(type) || isTraceOn(TT_EXCEPTIONS)) {
            e.printStackTrace();
        }
        EfaErrorPrintStream.ignoreExceptions = false;
    }

    public static void logStackTrace(String type, String key, String msg, StackTraceElement[] stack) {
        StringBuilder s = new StringBuilder();
        s.append(msg + ":");
        for (int i = 0; stack != null && i < stack.length; i++) {
            s.append("\n" + stack[i].toString());
        }
        log(type, key, s.toString());
    }


    public static boolean setDebugLogging(boolean activate, boolean setFromCommandLine) {
        if (debugLogging == activate) {
            return true; // nothing to do
        }
        if (debugLoggingActivatedByCommandLine) {
            return false; // don't allow to change value if it has been set from command line
        }
        debugLogging = activate;
        debugLoggingActivatedByCommandLine = setFromCommandLine;
        if (debugLogging) {
            Logger.log(Logger.INFO, Logger.MSG_LOGGER_DEBUGACTIVATED,
                    "Debug Logging activated."); // do not internationalize!
        } else {
            Logger.log(Logger.INFO, Logger.MSG_LOGGER_DEBUGDEACTIVATED,
                    "Debug Logging deactivated."); // do not internationalize!
        }
        return true;
    }

    public static boolean setTraceTopic(String topic, boolean setFromCommandLine) {
        long newTraceTopic = -1;
        if (globalTraceTopicSetByCommandLine) {
            return false; // don't allow to change value if it has been set from command line
        }
        if (topic == null || topic.length() == 0) {
            newTraceTopic = 0;
        } else {
            topic = topic.trim();
            if (topic.startsWith("0x")) {
                topic = topic.substring(2);
            }
            if (topic.length() == 0) {
                return false;
            }
            try {
                newTraceTopic = Long.parseLong(topic, 16);
            } catch (Exception e) {
                return false;
            }
        }
        if (newTraceTopic >= 0) {
            globalTraceTopicSetByCommandLine = setFromCommandLine;
            if (globalTraceTopic != newTraceTopic) {
                globalTraceTopic = newTraceTopic;
                if (debugLogging) {
                    Logger.log(Logger.INFO, Logger.MSG_LOGGER_TRACETOPIC,
                            "Trace Topic set to 0x" + Long.toString(globalTraceTopic, 16) + "."); // do not internationalize!
                }
            }

        }
        return true;
    }

    public static boolean setTraceLevel(int level, boolean setFromCommandLine) {
        if (globalTraceLevelSetByCommandLine) {
            return false; // don't allow to change value if it has been set from command line
        }
        globalTraceLevelSetByCommandLine = setFromCommandLine;
        if (globalTraceLevel != level) {
            globalTraceLevel = level;
            if (debugLogging) {
                Logger.log(Logger.INFO, Logger.MSG_LOGGER_TRACETOPIC,
                        "Trace Level set to " + Integer.toString(globalTraceLevel) + "."); // do not internationalize!
            }
        }
        return true;
    }

    public static boolean isDebugLoggin() {
        return debugLogging;
    }

    public static long getTraceTopic() {
        return globalTraceTopic;
    }

    public static boolean isTraceOn(long traceTopic) {
        return isTraceOn(traceTopic, 1);
    }

    public static boolean isTraceOn(long traceTopic, int level) {
        return debugLogging && (globalTraceTopic & traceTopic) != 0 && globalTraceLevel >= level;
    }

    private static void mailError(String key, String msg, String to) {
        if (inMailError) {
            return; // avoid recursion
        }
        inMailError = true;
        try {
            String txt = International.getString("Dies ist eine automatisch erstellte Fehlermeldung von efa.") + "\n"
                    + International.getString("Folgender Fehler ist aufgetreten:") + "\n" + msg;
            if (key != null && key.equals(Logger.MSG_ERROR_EXCEPTION)) {
                txt += "\n\n" + International.getString("Programm-Information") + ":\n============================================\n";
                Vector info = Daten.getEfaInfos();
                for (int i = 0; info != null && i < info.size(); i++) {
                    txt += (String) info.get(i) + "\n";
                }
            }
            Messages messages = (Daten.project != null ? Daten.project.getMessages(false) : null);
            if (messages != null && messages.isOpen()) {
                messages.createAndSaveMessageRecord(Daten.EFA_SHORTNAME, to, International.getString("FEHLER"), txt);
            }
        } catch (Exception e) {
            Logger.logdebug(e);
        } finally {
            inMailError = false;
        }
    }

    public static boolean isWarningLine(String s) {
        return (s != null && s.indexOf(Logger.WARNING) == 42);
    }

    public static long getLineTimestamp(String s) {
        if (s == null || s.length() < 21) {
            return 0;
        }
        TMJ datum = EfaUtil.string2date(s.substring(1, 11), 1, 1, 1980);
        TMJ zeit = EfaUtil.string2date(s.substring(12, 20), 0, 0, 0);
        return EfaUtil.dateTime2Cal(datum, zeit).getTimeInMillis();
    }
}
