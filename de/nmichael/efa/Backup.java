package de.nmichael.efa;

import java.io.*;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class Backup {

  public final static int SAVE = 0; // Typen von Aktionen, die ein Backup.create()
  public final static int CONV = 1; // ausl�sen k�nnen

  public final static String BACKUP = "%%BACKUP%%"; // Kennung im Dateikopf, da� es sich um ein Backup handelt

  private String dir = null;
  private boolean save  = false;
  private boolean month = false;
  private boolean day   = false;
  private boolean conv  = false;

  public Backup(String dir, boolean save, boolean month, boolean day, boolean conv) {
    this.dir = dir;
    this.save = save;
    this.month = month;
    this.day = day;
    this.conv = conv;
  }

  public boolean create(String fname, int typ, String version) {
    if (!new File(dir).isDirectory()) return false;
    if (fname == null) return false;
    if (!EfaUtil.canOpenFile(fname)) return false;

    boolean success = true;

    String fbak; int n;
    if ( (n = fname.lastIndexOf(Daten.fileSep))>=0) fbak = Daten.efaBakDirectory + fname.substring(n+1,fname.length()) + ".bak";
    else fbak = Daten.efaBakDirectory + fname + ".bak";

    if (typ == SAVE && save) {
      if (!backupFile(fname,fbak)) {
        Logger.log(Logger.ERROR,"Backup: Backup von "+fname+" fehlgeschlagen.");
        success = false;
      }
    }

    if (typ == SAVE && month) {
      Calendar cal = GregorianCalendar.getInstance();
      int monat = cal.get(GregorianCalendar.MONTH)+1;
      String bakname = fbak + cal.get(GregorianCalendar.YEAR)+"-"+(monat<10 ? "0"+monat : ""+monat);
      if (! new File(bakname).isFile()) {
        if (!backupFile(fname,bakname)) {
          Logger.log(Logger.ERROR,"Backup: monatliches Backup von "+fname+" fehlgeschlagen.");
          success = false;
        }
      }
    }

    if (typ == SAVE && day) {
      Calendar cal = GregorianCalendar.getInstance();
      int monat = cal.get(GregorianCalendar.MONTH)+1;
      int tag   = cal.get(GregorianCalendar.DAY_OF_MONTH);
      String bakname = fbak + cal.get(GregorianCalendar.YEAR)+"-"+(monat<10 ? "0"+monat : ""+monat)+"-"+(tag<10 ? "0"+tag : ""+tag);
      if (! new File(bakname).isFile()) {
        if (!backupFile(fname,bakname)) {
          Logger.log(Logger.ERROR,"Backup: t�gliches Backup von "+fname+" fehlgeschlagen.");
          success = false;
        }
      }
    }

    if (typ == CONV && conv && version != null) {
      String bakname = fbak + "_v" + version;
      if (!backupFile(fname,bakname)) {
        Logger.log(Logger.ERROR,"Backup: Backup beim Konvertieren von "+fname+" fehlgeschlagen.");
        success = false;
      }
    }

    return success;
  }


  private boolean backupFile(String orgName, String bakName) {
    try {
      BufferedReader f = new BufferedReader(new InputStreamReader(new FileInputStream(orgName),Daten.ENCODING));
      BufferedWriter ff = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bakName),Daten.ENCODING));

      int c = 0;
      String s;
      while ( (s = f.readLine()) != null && !s.startsWith("##CHECKSUM=")) {
        if (c == 0 && s.startsWith("##")) ff.write(s+" "+BACKUP+"\n");
        else ff.write(s+"\n");
        c++;
      }
      f.close();
      ff.close();
      String hash = EfaUtil.getSHA(new File(bakName));
      ff = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(bakName,true),Daten.ENCODING));
      ff.write("##CHECKSUM="+hash);
      ff.close();
      return true;
    } catch(Exception e) {
      return false;
    }
  }

}