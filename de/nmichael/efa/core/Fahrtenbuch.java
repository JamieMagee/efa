package de.nmichael.efa.core;

import de.nmichael.efa.*;
import de.nmichael.efa.core.DatenListe;
import de.nmichael.efa.core.DatenFelder;
import de.nmichael.efa.core.Boote;
import de.nmichael.efa.util.TMJ;
import de.nmichael.efa.util.Mehrtagesfahrt;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.util.Backup;
import java.io.*;
import java.util.Hashtable;
import java.util.Arrays;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  Fahrtenbuch, abgeleitet von DatenListe
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class Fahrtenbuch extends DatenListe {

  public static final int LFDNR = 0;
  public static final int DATUM = 1;
  public static final int BOOT = 2;
  public static final int STM = 3;
  public static final int MANNSCH1 = 4; // ACHTUNG: MANNSCH1 bis MANNSCH16 müssen
  public static final int MANNSCH2 = 5; // unbedingt aufeinanerfolgende Werte haben,
  public static final int MANNSCH3 = 6; // da sie u.a. in Statistik.java mit einer for-Schleife
  public static final int MANNSCH4 = 7; // ausgewertet werden!!!
  public static final int MANNSCH5 = 8;
  public static final int MANNSCH6 = 9;
  public static final int MANNSCH7 = 10;
  public static final int MANNSCH8 = 11;
  public static final int MANNSCH9 = 12; // neu in v0.85
  public static final int MANNSCH10= 13; // neu in v0.85
  public static final int MANNSCH11= 14; // neu in v0.85
  public static final int MANNSCH12= 15; // neu in v0.85
  public static final int MANNSCH13= 16; // neu in v1.0.0
  public static final int MANNSCH14= 17; // neu in v1.0.0
  public static final int MANNSCH15= 18; // neu in v1.0.0
  public static final int MANNSCH16= 19; // neu in v1.0.0
  public static final int MANNSCH17= 20; // neu in v1.4.0
  public static final int MANNSCH18= 21; // neu in v1.4.0
  public static final int MANNSCH19= 22; // neu in v1.4.0
  public static final int MANNSCH20= 23; // neu in v1.4.0
  public static final int MANNSCH21= 24; // neu in v1.4.0
  public static final int MANNSCH22= 25; // neu in v1.4.0
  public static final int MANNSCH23= 26; // neu in v1.4.0
  public static final int MANNSCH24= 27; // neu in v1.4.0
  public static final int OBMANN = 28;   // neu in v1.4.0
  public static final int ABFAHRT = 29;  // bis 1.3.1: 20
  public static final int ANKUNFT = 30;  // bis 1.3.1: 21
  public static final int ZIEL = 31;     // bis 1.3.1: 22
  public static final int BOOTSKM = 32;  // bis 1.3.1: 23
  public static final int MANNSCHKM = 33;// bis 1.3.1: 24
  public static final int BEMERK = 34;   // bis 1.3.1: 25
  public static final int FAHRTART = 35; // bis 1.3.1: 26; neue Bedeutung in 1.3.0;  neu in v0.85

  public static final int ANZ_MANNSCH = 24;

  public static final String KENNUNG060 = "##EFA.060.FAHRTENBUCH##";
  public static final String KENNUNG070 = "##EFA.070.FAHRTENBUCH##";
  public static final String KENNUNG085 = "##EFA.085.FAHRTENBUCH##";
  public static final String KENNUNG090 = "##EFA.090.FAHRTENBUCH##";
  public static final String KENNUNG100 = "##EFA.100.FAHRTENBUCH##";
  public static final String KENNUNG130 = "##EFA.130.FAHRTENBUCH##";
  public static final String KENNUNG135 = "##EFA.135.FAHRTENBUCH##";

  private FBDaten fbDaten = null;
  private Hashtable mehrtagesfahrten = null;
  private String nextFb = null; // nächste Fahrtenbuchdatei
  private String prevFb = null; // vorangehende Fahrtenbuchdatei

  // Konstruktor
  public Fahrtenbuch(String pdat) {
    super(pdat,36,1,true);
    kennung = KENNUNG135;
    fbDaten = new FBDaten();
    mehrtagesfahrten = new Hashtable();
  }


  // Einstellungen aus dem Fahrtenbuch auslesen
  public boolean readEinstellungen() {
    String s;
    fbDaten.bootDatei = "boote.efbb";
    fbDaten.mitgliederDatei = "mitglieder.efbm";
    fbDaten.zieleDatei = "ziele.efbz";
    fbDaten.statistikDatei = "statistik.efbs";
    try {
      while ( (s = freadLine()) != null && !s.startsWith("##ENDE_KONFIG") ) {
        s = s.trim();
        if (s.startsWith("BOOTE="))
          fbDaten.bootDatei=s.substring(6,s.length());
        if (s.startsWith("MITGLIEDER="))
          fbDaten.mitgliederDatei=s.substring(11,s.length());
        if (s.startsWith("ZIELE="))
          fbDaten.zieleDatei=s.substring(6,s.length());
        if (s.startsWith("STATISTIK="))
          fbDaten.statistikDatei=s.substring(10,s.length());
        if (s.startsWith("VORHERIGESFB="))
          prevFb = s.substring(13,s.length());
        if (s.startsWith("NAECHSTESFB="))
          nextFb = s.substring(12,s.length());
        if (s.startsWith("NAMENSANGABEN="))
          fbDaten.erstVorname = s.substring(14,s.length()).equals("VORNACH");
        if (s.startsWith("STATUS1=")) { // aus Kompatibilität zu FBs von vor 090
          String t = s.substring(8,s.length());
          if (!t.endsWith(",Gast")) t = t+",Gast";
          fbDaten.status = EfaUtil.statusList2Arr(t);
        }
        if (s.startsWith("STATUS=")) {
          String t = s.substring(7,s.length());
          fbDaten.status = EfaUtil.statusList2Arr(t);
        }
        if (s.startsWith("ANZMITGLIEDER=")) {
          String t = s.substring(14,s.length());
          fbDaten.anzMitglieder = EfaUtil.string2int(t,0);
        }

        if (s.startsWith("MEHRTAGESFAHRT=")) {
          String t = s.substring(15,s.length());
          DatenFelder d = new DatenFelder(6,t);
          Mehrtagesfahrt m = new Mehrtagesfahrt(d.get(0),d.get(1),d.get(2),EfaUtil.string2int(d.get(3),1),d.get(4),d.get(5).equals("+"));
          mehrtagesfahrten.put(m.name,m);
        }

      }
    } catch(IOException e) {
      Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
      return false;
    }
    return true;
  }


  // Fahrtenbuch incl. aller Daten und Dateien einlesen
  public synchronized boolean readFile() {
    if (super.readFile()) {
      readZusatzdatenbanken(false);
      return true;
    }
    Daten.fahrtenbuch = null;
    return false;
  }


  // Fahrtenbuch schreiben
  public synchronized boolean writeEinstellungen() {
    try {
      fwrite("BOOTE="+fbDaten.bootDatei+"\n");
      fwrite("MITGLIEDER="+fbDaten.mitgliederDatei+"\n");
      fwrite("ZIELE="+fbDaten.zieleDatei+"\n");
      fwrite("STATISTIK="+fbDaten.statistikDatei+"\n");
      if (prevFb != null) fwrite("VORHERIGESFB="+prevFb+"\n");
      else fwrite("VORHERIGESFB=\n");
      if (nextFb != null) fwrite("NAECHSTESFB="+nextFb+"\n");
      else fwrite("NAECHSTESFB=\n");
      if (fbDaten.erstVorname) fwrite("NAMENSANGABEN=VORNACH\n");
      else fwrite("NAMENSANGABEN=NACHVOR\n");
      fwrite("STATUS="+EfaUtil.arr2KommaList(fbDaten.status)+"\n");
      fwrite("ANZMITGLIEDER="+fbDaten.anzMitglieder+"\n");

      Object[] keys = mehrtagesfahrten.keySet().toArray();
      for (int i=0; i<keys.length; i++) {
        Mehrtagesfahrt m = (Mehrtagesfahrt)mehrtagesfahrten.get(keys[i]);
        fwrite("MEHRTAGESFAHRT="+m.name+"|"+m.start+"|"+m.ende+"|"+m.rudertage+"|"+m.gewaesser+"|"+(m.isEtappen?"+":"-")+"\n");
      }

      fwrite("##ENDE_KONFIG\n");
    } catch(IOException e) {
      Dialog.error("Datei '"+dat+"' kann nicht geschrieben werden!");
      return false;
    }
    return true;
  }

  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {

        // KONVERTIEREN v0.60 -> v0.70
        if ( s != null && s.trim().startsWith(KENNUNG060)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"060");
          iniList(this.dat,18,1,true); // Rahmenbedingungen von v0.70 schaffen

          if (!readEinstellungen()) return false;
          int to;
          if ((to = this.dat.toUpperCase().lastIndexOf(".EFB")) >= 0) {
            fbDaten.statistikDatei = this.dat.substring(0,to)+".efbs";
          } else fbDaten.statistikDatei = this.dat+".efbs";

          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              add(s);
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG070;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }

        // KONVERTIEREN: 070 -> 085
        if (s != null && s.trim().startsWith(KENNUNG070)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"070");
          iniList(this.dat,23,1,true); // Rahmenbedingungen von v0.85 schaffen

          if (!readEinstellungen()) return false;
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              int c=0;
              for (int i=0; i<s.length(); i++) {
                if (s.charAt(i) == '|') c++;
                if (c-1 == MANNSCH8) { // 4 neue Felder hinter MANNSCH8 und eines am Ende...
                  s = s.substring(0,i) + "||||" + s.substring(i,s.length())+"|"; // Mannsch9-12 und Fahrtdauer als neue Felder!
                  break;
                }
              }
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              add(s);
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG085;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }


        // KONVERTIEREN: 085 -> 090
        if (s != null && s.trim().startsWith(KENNUNG085)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"085");
          iniList(this.dat,23,1,true); // Rahmenbedingungen von v0.85 schaffen

          if (!readEinstellungen()) return false;
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              add(s);
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG090;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }


        // KONVERTIEREN: 090 -> 100
        if (s != null && s.trim().startsWith(KENNUNG090)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"090");
          iniList(this.dat,28,1,true); // Rahmenbedingungen von v1.00 schaffen

          if (!readEinstellungen()) return false;
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              int c=0;
              for (int i=0; i<s.length(); i++) {
                if (s.charAt(i) == '|') c++;
                if (c-1 == MANNSCH12) { // 4 neue Felder hinter MANNSCH12...
                  s = s.substring(0,i) + "||||" + s.substring(i,s.length()); // Mannsch13-16 als neue Felder!
                  s += "|"; // MTOURTAGE
                  break;
                }
              }
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              add(s);
            }
          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG100;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }


        // KONVERTIEREN: 100 -> 130
        if (s != null && s.trim().startsWith(KENNUNG100)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"100");
          iniList(this.dat,28,1,true); // Rahmenbedingungen von v1.3.0 schaffen

          if (!readEinstellungen()) return false;
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);

              if (d.get(26).length()>0) { // MTOUR(26) vorhanden
                String name = d.get(26).trim();
                if (!Mehrtagesfahrt.isVordefinierteFahrtart(name)) {
                  Mehrtagesfahrt m = null;
                  String ende = null;
                  int pos = name.indexOf("(bis ");
                  if (pos >= 0) {
                    ende = name.substring(pos+5,name.length()-1);
                    name = name.substring(0,pos-1);
                  }
                  int rudertage = EfaUtil.string2int(d.get(27),1);

                  m = (Mehrtagesfahrt)mehrtagesfahrten.get(name);
                  if (m == null) { // neue Mehrtagesfahrt
                    m = new Mehrtagesfahrt(name,
                                           d.get(Fahrtenbuch.DATUM),
                                           (ende == null ? d.get(Fahrtenbuch.DATUM) : ende),
                                           rudertage,
                                           "",
                                           (ende == null ? true : false) );
                  } else {
                    if (ende == null) { // Etappe: Werte aktualisieren
                      if (EfaUtil.secondDateIsAfterFirst(m.ende,d.get(Fahrtenbuch.DATUM))) {
                        m.ende = d.get(Fahrtenbuch.DATUM);
                        m.rudertage++;
                      }
                    }
                  }
                  mehrtagesfahrten.put(m.name,m);
                  d.set(26,m.name);
                  d.set(27,"");
                }
              }
              add(d);
            }

          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG130;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }


        // KONVERTIEREN: 130 -> 135
        if (s != null && s.trim().startsWith(KENNUNG130)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"130");
          iniList(this.dat,36,1,true); // Rahmenbedingungen von v1.4.0 schaffen

          if (!readEinstellungen()) return false;
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              DatenFelder d = constructFields(s);

              for (int i=29; i<=35; i++) d.set(i,d.get(i-9)); // ABFAHRT bis FAHRTART um 9 nach hinten verschieben
              for (int i=20; i<=28; i++) d.set(i,""); // neue Felder MANNSCH17 bis MANNSCH24 und OBMANN auf "" setzen

              add(d);
            }

          } catch(IOException e) {
             Dialog.error("Lesen der Datei '"+dat+"' fehlgeschlagen!");
             return false;
          }
          kennung = KENNUNG135;
          if (closeFile() && writeFile(true) && openFile()) {
            Logger.log(Logger.INFO,dat+" wurde in das neue Format "+kennung+" konvertiert.");
            s = kennung;
          } else Dialog.error("Fehler beim Konvertieren von "+dat);
        }


        // FERTIG MIT KONVERTIEREN
        if (s == null || !s.trim().startsWith(kennung)) {
          Dialog.error("Datei '"+dat+"' hat ungültiges Format!");
          fclose(false);
          return false;
        }
      }
    } catch(IOException e) {
      Dialog.error("Datei '"+dat+"' kann nicht gelesen werden!");
      return false;
    }
    return true;
  }


  // Zusatzdatenbanken lesen und bei neuErstellen=true ggf. ohne Nachfrage neu erstellen
  public void readZusatzdatenbanken(boolean neuErstellen) {
    String fbPath = EfaUtil.getPathOfFile(this.dat);

    // Boote einlesen
    if (fbDaten.bootDatei.equals("")) fbDaten.boote = null;
    else {
      fbDaten.boote = new Boote(EfaUtil.makeFullPath(fbPath,fbDaten.bootDatei));
      if (neuErstellen && !EfaUtil.canOpenFile(fbDaten.boote.getFileName())) fbDaten.boote.writeFile();
      if (!fbDaten.boote.readFile()) fbDaten.bootDatei= null;
    }

    // Mitglieder einlesen
    if (fbDaten.mitgliederDatei.equals("")) fbDaten.mitglieder = null;
    else {
      fbDaten.mitglieder = new Mitglieder(EfaUtil.makeFullPath(fbPath,fbDaten.mitgliederDatei));
      if (neuErstellen && !EfaUtil.canOpenFile(fbDaten.mitglieder.getFileName())) fbDaten.mitglieder.writeFile();
      if (!fbDaten.mitglieder.readFile()) fbDaten.mitgliederDatei= null;
    }

    // Ziele einlesen
    if (fbDaten.zieleDatei.equals("")) fbDaten.ziele = null;
    else {
      fbDaten.ziele = new Ziele(EfaUtil.makeFullPath(fbPath,fbDaten.zieleDatei));
      if (neuErstellen && !EfaUtil.canOpenFile(fbDaten.ziele.getFileName())) fbDaten.ziele.writeFile();
      if (!fbDaten.ziele.readFile()) fbDaten.zieleDatei= null;
    }

    // Statistiken einlesen
    if (fbDaten.statistikDatei.equals("")) fbDaten.statistik = null;
    else {
      fbDaten.statistik = new StatSave(EfaUtil.makeFullPath(fbPath,fbDaten.statistikDatei));
      if (neuErstellen && !EfaUtil.canOpenFile(fbDaten.statistik.getFileName())) fbDaten.statistik.writeFile();
      if (!fbDaten.statistik.readFile()) fbDaten.statistikDatei= null;
    }
  }


  // liefert Namen des nächsten Fahrtenbuchs, oder "" wenn nicht vorhanden
  public String getNextFb(boolean absolut) {
    if (nextFb != null && nextFb.equals("")) return nextFb;
    if (nextFb != null || (openFile() && readEinstellungen() && closeFileWithoutHash() && nextFb != null))
      if (absolut) return EfaUtil.makeFullPath(EfaUtil.getPathOfFile(dat),nextFb);
      else return nextFb;
    return "";
  }


  // liefert Namen des vorherigen Fahrtenbuchs, oder "" wenn nicht vorhanden
  // absolut=true gibt immer einen absoluten Dateinamen zurück; absolut=false gibt den Namen so zurück,
  // wie er im FB gespeichert ist (also nicht *zwangsläufig* relativ!)
  public String getPrevFb(boolean absolut) {
    if (prevFb != null && prevFb.equals("")) return prevFb;
    if (prevFb != null || (openFile() && readEinstellungen() && closeFile() && nextFb != null))
      if (absolut) return EfaUtil.makeFullPath(EfaUtil.getPathOfFile(dat),prevFb);
      else return prevFb;
    return "";
  }


  // nächstes und vorheriges Fahrtenbuchs von außen setzen
  public void setNextFb(String s) {
    nextFb = s;
    changeType = DatenListe.CT_CHANGED;
  }
  public void setPrevFb(String s) {
    prevFb = s;
    changeType = DatenListe.CT_CHANGED;
  }

  // Zusatzdaten ermitteln und festlegen
  public FBDaten getDaten() {
    return fbDaten;
  }
  public void setDaten(FBDaten d) {
    fbDaten = d;
    changeType = DatenListe.CT_CHANGED;
  }


  // Einträge auf Gültigkeit prüfen
  public void validateValues(DatenFelder d) {
    if (d.get(LFDNR).trim().equals("")) return; // überspringen, da leere Keys ohnehin nicht hinzugefügt werden
    d.set(LFDNR,EfaUtil.getLfdNr(d.get(LFDNR)));
    TMJ tmj = EfaUtil.correctDate(d.get(DATUM),1,1,1970);
    d.set(DATUM,tmj.tag+"."+tmj.monat+"."+tmj.jahr);
    d.set(BOOTSKM,EfaUtil.zehntelInt2String(EfaUtil.zehntelString2Int(d.get(BOOTSKM))));
    d.set(MANNSCHKM,EfaUtil.zehntelInt2String(EfaUtil.zehntelString2Int(d.get(MANNSCHKM))));
 }


 public void addMehrtagesfahrt(String name, String start, String ende, int rudertage, String gewaesser, boolean isEtappen) {
   Mehrtagesfahrt m = new Mehrtagesfahrt(name,start,ende,rudertage,gewaesser,isEtappen);
   mehrtagesfahrten.put(m.name,m);
 }

 public void addMehrtagesfahrt(Mehrtagesfahrt m) {
   if (m != null) mehrtagesfahrten.put(m.name,m);
 }

 public void removeMehrtagesfahrt(String name) {
   if (mehrtagesfahrten.get(name) != null) mehrtagesfahrten.remove(name);
 }


 public Mehrtagesfahrt getMehrtagesfahrt(String key) {
   if (key == null) return null;
   return (Mehrtagesfahrt)mehrtagesfahrten.get(key);
 }

 public String[] getAllMehrtagesfahrtNamen() {
   if (mehrtagesfahrten == null || mehrtagesfahrten.size()==0) return null;
   Object[] a = mehrtagesfahrten.keySet().toArray();
   Arrays.sort(a,0,a.length);
   String[] s = new String[a.length];
   for (int i=0; i<s.length; i++) s[i] = (String)a[i];
   return s;
 }

 public String[] getAllMehrtagesfahrtNamenByDate() {
   if (mehrtagesfahrten == null || mehrtagesfahrten.size()==0) return null;
   Object[] a = mehrtagesfahrten.keySet().toArray();
   MtourStrings[] aa = new MtourStrings[a.length];
   for (int i=0; i<a.length; i++) {
     Mehrtagesfahrt m = getMehrtagesfahrt((String)a[i]);
     aa[i] = new MtourStrings(m.name,m.start,m.ende);
   }
   Arrays.sort(aa,0,aa.length);
   String[] s = new String[aa.length];
   for (int i=0; i<a.length; i++) s[i] = aa[i].toString();
   return s;
 }

 public int getAnzahlMehrtagesfahrten() {
   return mehrtagesfahrten.size();
 }

 private class MtourStrings implements Comparable {
   private String name;
   private String start;
   private String ende;

   public MtourStrings(String name, String start, String ende) {
     this.name = name;
     this.start = start;
     this.ende = ende;
   }

   public String toString() {
     return name;
   }

   public int compareTo(Object o) throws ClassCastException {
     MtourStrings b = (MtourStrings)o;
     if (this.start == null || this.ende == null || b.start == null || b.ende == null) return 0;
     if (EfaUtil.secondDateIsAfterFirst(this.start,b.start)) return -1;
     if (!this.start.equals(b.start)) return 1;
     if (EfaUtil.secondDateIsAfterFirst(this.ende,b.ende)) return -1;
     if (!this.ende.equals(b.ende)) return 1;
     if (this.name == null || b.name == null) return 0;
     return this.name.compareTo(b.name);
   }

 }


}