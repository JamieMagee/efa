package de.nmichael.efa;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.*;
import java.io.*;
import java.awt.event.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.net.*;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JComponent;
import java.security.*;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  Utility-Methoden
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class EfaUtil {

  private static final int ZIP_BUFFER = 2048;

  private static java.awt.Container java_awt_Container = new java.awt.Container();

  // �berpr�fen, ob "c" ein "echtes" Zeichen (Buchstabe, Ziffer, Whitespace) ist
  static boolean isRealChar(KeyEvent e) {
    char c = e.getKeyChar();
    return (Character.isLetter(c) || Character.isDigit(c) || Character.isWhitespace(c) ||
            c=='.' || c=='-' || c=='+' || c=='(' || c==')' || c=='!' || c=='"' || c=='�' || c=='$' || c=='%' ||
            c=='&' || c=='/' || c=='=' || c=='?' || c==';' || c==':' || c==',' || c=='_' || c=='#' || c=='*' ||
            c=='|' || c=='>' || c=='<');
  }


  // �berpr�fen, ob angegebenes Datum ein Wochenende ist
  public static boolean woEnd(String s) {
    SimpleDateFormat df = new SimpleDateFormat();
    Calendar cal = new GregorianCalendar();
    int tag;
    try {
      Date d = df.parse(s);
      cal.setTime(d);
      if ( (tag = cal.get(Calendar.DAY_OF_WEEK)) == Calendar.SATURDAY || tag == Calendar.SUNDAY) return true;
    } catch (Exception e) {
      Logger.log(Logger.ERROR,"Parse error");
    }
    return false;
  }


  // Namen zerlegen
  public static String[] zerlegeNamen(String s, boolean vorNach) {
    String name,vor,nach,ver;
    int from , to;

    // gesamter Name
    if ( (to = s.indexOf("(")) >= 0) name = s.substring(0,to).trim();
    else name = s;

    // Teilnamen
    if (vorNach) { // erst Vorname
      if ( (to = name.indexOf(" v. ")) >= 0 ||
            ( (to = name.indexOf(" van ")) >= 0 && name.indexOf(" van Maren") < 0 )
           ) { // Titel
        vor = name.substring(0,to);
        nach = name.substring(vor.length()+1,name.length());
      } else if ( (to = name.lastIndexOf(" ")) >= 0) { // normal
        vor = name.substring(0,to);
        nach = name.substring(to+1,name.length());
      } else { // kein Vorname
        vor = "";
        nach = name;
      }
    } else { // erst Nachname
      if ( (to = name.indexOf(",")) >= 0) { // normal
        nach = name.substring(0,to);
        if (to+2<=name.length()) vor = name.substring(to+2,name.length());
        else vor = "";
      } else { // kein Vorname
        nach = name;
        vor = "";
      }
    }

    // Verein
    from = s.indexOf("(")+1;
    to = s.indexOf(")");
    if (from <= 0 || to < 0 || from>to) ver = "";
    else ver = s.substring(from,to);

    String[] a = {vor,nach,ver};
    return a;
  }


  // aus einem Namensstring den Vornamen liefern
  public static String getVorname(String s) {
    if (Daten.fahrtenbuch == null) return "";
    return zerlegeNamen(s,Daten.fahrtenbuch.getDaten().erstVorname)[0];
  }


  // aus einem Namensstring den Nachnamen liefern
  public static String getNachname(String s) {
    if (Daten.fahrtenbuch == null) return "";
    return zerlegeNamen(s,Daten.fahrtenbuch.getDaten().erstVorname)[1];
  }


  // aus einem Namensstring den Bootsnamen liefern
  public static String getName(String s) {
    int from , to;
    from = 0;
    to = s.indexOf("(")-1;
    if (to < 0) to = s.length();
    return s.substring(from,to);
  }


  // aus einem Namensstring den Vereinsamen liefern
  public static String getVerein(String s) {
    if (Daten.fahrtenbuch == null) return "";
    return zerlegeNamen(s,Daten.fahrtenbuch.getDaten().erstVorname)[2];
  }


  // vollen Namen aus Teilnamen konstruieren
  public static String getFullName(String vor, String nach, String ver) {
    if (Daten.fahrtenbuch == null) return getFullName(vor,nach,ver,true);
    return getFullName(vor,nach,ver,Daten.fahrtenbuch.getDaten().erstVorname);
  }


  // vollen Namen aus Teilnamen konstruieren
  public static String getFullName(String vor, String nach, String ver, boolean erstVorname) {
    String s = "";
    if (erstVorname) {
      if (!vor.equals("")) s = vor;
      if (!nach.equals("")) s = s + " " + nach;
      s = s.trim();
      if (!ver.equals("")) s = s + " (" + ver + ")";
    } else {
      if (!nach.equals("")) s = nach;
      if (!vor.equals(""))
        if (!s.equals("")) s = s + ", " + vor;
        else s = vor;
      if (!ver.equals("")) s = s + " (" + ver + ")";
    }
    s = s.trim();
    return s;
  }

  // Integer-Division mit einer Nachf,ae,-Stelle (und Rundung)
  public static float div(int a, int b) {
    if (b == 0) return 0;
    return ((float)intdiv(a*10,b))/10;
  }

  // Integer-Division mit Rundung
  public static int intdiv(int a, int b) {
    if (b == 0) return 0;
    int c = (a*10)/b;
    if (c % 10 >= 5) c = c + 10 - (c % 10);
    return c/10;
  }

  // float-Division mit "div 0" Sicherung
  public static float fdiv(int a, int b) {
    if (b == 0) return 0;
    return ((float)a / (float)b);
  }

  // Einzelne Werte aus Datumsstring ermitteln, wobei tt, mm und yy die
  // Vorgabewerte f�r unvollst�ndige Angaben sind
  public static TMJ string2date(String s,int tt, int mm, int yy) {
    String t = "";
    int nr = 0;
    int[] a = new int[3];
    a[0] = tt; a[1] = mm; a[2] = yy;
    for (int i=0; i<s.length(); i++) {
      boolean inNumber = false;
      if (Character.isDigit(s.charAt(i))) {
        inNumber = true;
      }
      if (s.charAt(i) == '-' && t.length()==0 && i+1<s.length() && Character.isDigit(s.charAt(i+1))) {
        inNumber = true;
      }

      if (inNumber) {
        t = t + s.charAt(i);
      }
      if (!inNumber || i+1 == s.length()) {
        if (!t.equals("")) {
          try {
            a[nr++] = Integer.parseInt(t);
          } catch(Exception e) {
          }
        }
        t = "";
      }
    }
    return new TMJ(a[0],a[1],a[2]);
  }

  // aus einem TMJ einen Datumsstring machen
  public static String tmj2datestring(TMJ tmj) {
    return tmj.tag+"."+tmj.monat+"."+tmj.jahr;
  }


  // Aus einem String s (mit Referenzwerten tt.mm.yyyy) ein korrektes (g�ltiges) Datum errechnen
  public static TMJ correctDate(String s,int tt, int mm, int yy) {
    TMJ c = string2date(s,tt,mm,yy);
    int tag = c.tag;
    int monat = c.monat;
    int jahr = c.jahr;
    boolean vierstellig = jahr>=1000 && jahr<=2100;
    if (jahr<0 || jahr>2100) jahr = yy;
    if (jahr<0 || jahr>2100) jahr = 0;
    if (jahr<1900) jahr += 1900;
    if (jahr<1980 && !vierstellig) jahr += 100; // nur urspr�nglich nicht vierstellige Jahre als gr��er als 1980 interpretieren
    if (monat<1 || monat>12) monat = mm;
    if (monat<1 || monat>12) monat = 1;
    if (tag<1 || tag>31) tag = tt;
    if (tag<1 || tag>31) tag = 1;
    switch (monat) {
      case 4: case 6: case 9: case 11: if (tag>30) tag = 30;
              break;
      case 2: if (tag>29) tag = 29;
              if (tag>28 && jahr % 4 != 0) tag = 28;
              break;
      default: ;
    }
    return new TMJ(tag,monat,jahr);
  }

  // Aus einem String s ein korrektes (g�ltiges) Datum machen, ggf. aktuelles Datum als Referenzdatum verwenden
  public static String correctDate(String s) {
    Calendar cal = new GregorianCalendar();
    TMJ tmj = correctDate(s,cal.get(Calendar.DAY_OF_MONTH),cal.get(Calendar.MONTH)+1,cal.get(Calendar.YEAR));
    return tmj.tag+"."+tmj.monat+"."+tmj.jahr;
  }

  // Aus einem String s eine korrekte (g�ltige) Zeit machen
  public static String correctTime(String s) {
    if (s.length()==0) return "";
    TMJ hhmm = EfaUtil.string2date(s,0,0,0); // TMJ mi�braucht f�r die Auswertung von Uhrzeiten
    int hh = hhmm.tag;
    int mm = hhmm.monat;
    String std,min;
    if (hh>100 && mm==0) {
      mm = hh % 100;
      hh = hh / 100;
    }
    if (hh>23) hh=23;
    if (mm>59) mm=59;
    if (hh>9) std = Integer.toString(hh);
    else std = "0"+hh;
    if (mm>9) min = Integer.toString(mm);
    else min = "0"+mm;
    return std+":"+min;
  }



  // Aus einer Nachkommazahl eine Ziffer machen, so da� faktisch nur noch eine
  // Nachkommastelle existiert (90 -> 9; 10 -> 1; 55 -> 6)
  public static int makeDigit(int z) {
    if (z<0) z = z * (-1);
    while (z>9) z = z / 10;
    return z;
  }

  // Wandelt einen int-Wert in einen String um, wobei der int-Wert zuvor durch 10
  // geteilt wird (d.h. 123 -> "12.3")
  public static String zehntelInt2String(int i) {
    String s = Integer.toString(i);
    if (s.length()==1 && i != 0) s = "0"+s;
    if (s.endsWith("0")) s = s.substring(0,s.length()-1);
    else s = s.substring(0,s.length()-1)+"."+s.substring(s.length()-1,s.length());
    if (s.length() == 0) s = "0";
    return s;
  }

  // Wandelt einen String in einen int-Wert, wobei der String-Wert mit 10 multipliziert wird
  // (d.h. "12.3" -> 123)
  public static int zehntelString2Int(String s) {
    if (s == null) return 0;
    TMJ tmj = string2date(s,0,0,0);
    while (tmj.monat > 9) tmj.monat /= 10;
    return tmj.tag*10 + tmj.monat;
  }

  // Split: Einen String anhand von Trennzeichen in einen Vector aufspalten
  public static Vector split(String s, char sep) {
    if (s == null) return null;
    Vector v = new Vector();
    while(s.length() != 0) {
      int pos = s.indexOf(sep);
      if (pos >= 0) {
        v.add(s.substring(0,pos));
        s = s.substring(pos+1,s.length());
        if (s.length()==0) v.add(""); // letztes (leeres) Element hinter letztem Trennzeichen
      } else if (s.length()>0) {
        v.add(s);
        s = "";
      }
    }
    return v;
  }

/*
  // Eine Komma-Liste in ein Array of String umwandeln
  public static String[] kommaList2Arr(String s, char sep, boolean addAndere) {
    if (s == null) return null;
    Vector v;
    if (s.length()>0) v = split(s,sep);
    else v = new Vector();
    String[] aa = new String[v.size()+ (addAndere ? 1 : 0) ];
    for (int ii=0; ii<v.size(); ii++)
      aa[ii] = (String)v.get(ii);
    if (addAndere) aa[aa.length-1] = "andere"; // nur f�r diese Listen soll ein "andere" angef
    return aa;
  }
  public static String[] statusList2Arr(String s) {
    return kommaList2Arr(s,',',true);
  }
*/

  // Eine Komma-Liste in ein Array of String umwandeln
  public static String[] kommaList2Arr(String s, char sep) {
    if (s == null) return null;
    Vector v;
    if (s.length()>0) v = split(s,sep);
    else v = new Vector();
    String[] aa = new String[v.size()];
    for (int ii=0; ii<v.size(); ii++)
      aa[ii] = (String)v.get(ii);
    return aa;
  }

  public static String[] statusList2Arr(String s) {
    if (s == null) return null;
    s = correctStatusList(s);
    String[] stati = kommaList2Arr(s.trim(),',');
    if (Daten.bezeichnungen == null ||
        Daten.bezeichnungen.gast == null || Daten.bezeichnungen.gast.length() == 0 ||
        Daten.bezeichnungen.andere == null || Daten.bezeichnungen.andere.length() == 0) return stati;
    Vector stati2 = new Vector();
    for (int i=0; i<stati.length; i++) {
      if (!stati[i].toLowerCase().equals(Daten.bezeichnungen.gast.toLowerCase()) &&
          !stati[i].toLowerCase().equals(Daten.bezeichnungen.andere.toLowerCase())) {
        stati2.add(stati[i]);
      }
    }
    stati2.add(Daten.bezeichnungen.gast);
    stati2.add(Daten.bezeichnungen.andere);
    String[] a = new String[stati2.size()];
    for (int i=0; i<stati2.size(); i++) {
      a[i] = (String)stati2.get(i);
    }
    return a;
  }


  // Eine Komma-Liste in ein Array of int umwandeln
  public static int[] kommaList2IntArr(String s, char sep) {
    if (s == null) return null;
    Vector v;
    if (s.length()>0) v = split(s,sep);
    else v = new Vector();
    int[] aa = new int[v.size()];
    for (int ii=0; ii<v.size(); ii++) aa[ii] = string2int((String)v.get(ii),0);
    return aa;
  }



  // Array of String in eine Komma-Liste umwandeln
  public static String arr2KommaList(String[] a) {
    String s = "";
    if (a == null) return s;
    for (int i=0; i<a.length; i++) {
      s = s+a[i];
      if (i+1<a.length) s = s+",";
    }
    return s;
  }


  // Array of int in eine Komma-Liste umwandeln
  public static String arr2KommaList(int[] a, int start) {
    String s = "";
    if (a == null) return s;
    for (int i=start; i<a.length; i++) {
      s = s+a[i];
      if (i+1<a.length) s = s+",";
    }
    return s;
  }


  // Pr�ft, ob die angegebene Datei ge�ffnet werden kann
  public static boolean canOpenFile(String d) {
    FileReader f;
    try {
      f = new FileReader(d);
      f.close();
    } catch(Exception e) {
      return false;
    }
    return true;
  }


  // Suchen und Ersetzen in Strings
  public static String replace(String org, String such, String ers) {
    int wo;
    String s = org;
    if ( ( wo = org.indexOf(such)) >= 0) {
      if (wo > 0)
        if (wo+such.length() < org.length()) s = org.substring(0,wo) + ers + org.substring(wo+such.length(),org.length());
        else s = org.substring(0,wo) + ers;
      else
        if (wo+such.length() < org.length()) s = ers + org.substring(wo+such.length(),org.length());
        else s = ers;
    }
    return s;
  }
  public static String replace(String org, String such, String ers, boolean alle) {
    if (org == null || such == null || ers == null) return null;
    if (!alle || (ers.length()>0 && ers.indexOf(such)>=0)) return replace(org,such,ers);
    String s = org;
    while (s.indexOf(such)>=0) {
      s = replace(s,such,ers);
    }
    return s;
  }


  // Vergleich zweier Strings auf numerische Weise, wobei an die Zahl noch eine Buchstabenfolge
  // angeh�ngt sein darf (--> LfdNr). Bsp: 12<34; 12A<12B usw.
  public static int compareIntString(String a, String b) {
    TMJ aa = string2date(a,1,1,1); // TMJ mi�braucht f�r die Auswertung von Zahlen
    TMJ bb = string2date(b,1,1,1); // TMJ mi�braucht f�r die Auswertung von Zahlen
    if (aa.tag == bb.tag) return a.compareTo(b);
    else if (aa.tag < bb.tag) return -1;
    else return 1;
  }

  // Differenz (Anzahl der Tage) zwischen zwei TMJ-Datumsangaben berechnen (von und bis mitgerechnet)
  public static int getDateDiff(TMJ v, TMJ b) {
    GregorianCalendar von = new GregorianCalendar(v.jahr,v.monat-1,v.tag);
    GregorianCalendar bis = new GregorianCalendar(b.jahr,b.monat-1,b.tag);
    int tmp = Math.round((((float)bis.getTime().getTime()-(float)von.getTime().getTime()) / (float)86400000)+1);
    if (tmp > 0) return tmp;
    else return -1 * tmp;
  }
  // Differenz (Anzahl der Tage) zwischen zwei String-Datumsangaben berechnen (von und bis mitgerechnet)
  public static int getDateDiff(String sv, String sb) {
    return getDateDiff(string2date(sv,1,1,1),string2date(sb,1,1,1));
  }

  // Differenz (Anzahl der Tage) zwischen zwei TMJ-Datumsangaben berechnen (von und bis *nicht* mitgerechnet),
  // anh�ngig von der Reihenfolge
  public static int getRealDateDiff(TMJ d1, TMJ d2) {
    GregorianCalendar c1 = new GregorianCalendar(d1.jahr,d1.monat-1,d1.tag);
    GregorianCalendar c2 = new GregorianCalendar(d2.jahr,d2.monat-1,d2.tag);
    return Math.round((((float)c2.getTime().getTime()-(float)c1.getTime().getTime()) / (float)86400000));
  }


  public static boolean secondDateIsAfterFirst(String sv, String sb) {
    TMJ v = string2date(sv,1,1,1);
    TMJ b = string2date(sb,1,1,1);
    GregorianCalendar von = new GregorianCalendar(v.jahr,v.monat-1,v.tag);
    GregorianCalendar bis = new GregorianCalendar(b.jahr,b.monat-1,b.tag);
    return bis.after(von);
  }

  public static boolean secondDateIsEqualOrAfterFirst(String sv, String sb) {
    TMJ v = string2date(sv,1,1,1);
    TMJ b = string2date(sb,1,1,1);
    GregorianCalendar von = new GregorianCalendar(v.jahr,v.monat-1,v.tag);
    GregorianCalendar bis = new GregorianCalendar(b.jahr,b.monat-1,b.tag);
    return bis.equals(von) || bis.after(von);
  }

  public static boolean secondTimeIsAfterFirst(String sv, String sb) {
    TMJ v = string2date(sv,1,1,1);
    TMJ b = string2date(sb,1,1,1);
    if (b.tag > v.tag) return true;
    if (b.tag < v.tag) return false;
    if (b.monat > v.monat) return true;
    if (b.monat < v.monat) return false;
    if (b.jahr > v.jahr) return true;
    if (b.jahr > v.jahr) return false;
    return false;
  }


  public static TMJ incDate(TMJ tmj, int diff) {
    GregorianCalendar cal = new GregorianCalendar(tmj.jahr,tmj.monat-1,tmj.tag);
    cal.add(GregorianCalendar.DATE,diff);
    return new TMJ(cal.get(GregorianCalendar.DAY_OF_MONTH),cal.get(GregorianCalendar.MONTH)+1,cal.get(GregorianCalendar.YEAR));
  }


  // Anzahl eines Zeichens in einem String ermitteln
  public static int countCharInString(String s, char c) {
    int n=0;
    for (int i=0; i<s.length(); i++)
      if (s.charAt(i) == c) n++;
    return n;
  }


  // Dateinamen "fileName" ggf. um Pfadangabe "basePath" vervollst�ndigen, falls "fileName" kein absoluter Pfad
  public static String makeFullPath(String basePath, String fileName) {
    if (basePath == null || fileName == null) return null;
    String s = fileName;
    try {
      String olddir = System.getProperty("user.dir");
      System.setProperty("user.dir",basePath);
      File f = new File(fileName);
      s = f.getAbsolutePath();
      System.setProperty("user.dir",olddir);
    } catch (SecurityException e) {}
    return s;
  }


  // Dateinamen "fileName" relativ zum Namen "efbName" machen, d.h. falls gleiches Verzeichnis, dieses weglassen
  public static String makeRelativePath(String fileName, String efbName) {
    String t;
    if ( (t = EfaUtil.getPathOfFile(fileName)).length() > 0)
      if (t.equals(EfaUtil.getPathOfFile(efbName))) return fileName.substring(t.length()+1,fileName.length());
      else return fileName;
    else return fileName;
  }


  // Pfadangabe aus einem Dateinamen ermitteln
  public static String getPathOfFile(String fileName) {
    if (fileName == null) return null;
    String s = new File(fileName).getName();
    int wo;
    if (s != null && (wo =fileName.lastIndexOf(s)) >= 0)
      if (wo != 0) {
        s = fileName.substring(0,wo-1);
        if (fileName.charAt(wo-1) == ':' && Daten.fileSep.equals("\\")) s += ":"; // Windows Fall "a:2002.efb"
      } else return "";
    else s = System.getProperty("file.separator");
    if (s.length() == 2 && s.charAt(1) == ':' && fileName.charAt(2) == '\\') s=s+"\\";
    return s;
  }

  // lokalen Dateinamen aus kompletter Pfadangabe ermitteln
  public static String getNameOfFile(String fileName) {
    if (fileName == null) return null;
    int l = getPathOfFile(fileName).length();
    return fileName.substring( (l == 0 ? 0 : l+1 ) );
  }

  // Dateinamen ohne jegliche Pfadangaben erhalten
  public static String getFilenameWithoutPath(String fileName) {
    if (fileName == null || fileName.length()==0) return fileName;
    int pos = fileName.lastIndexOf(Daten.fileSep);
    if (pos<0) return fileName;
    return fileName.substring(pos+1);
  }


  // Alle Senkrechtstriche (|) aus String entfernen
  public static String removeSepFromString(String s) {
    return removeSepFromString(s,"|");
  }
  // Alle Separatoren aus String entfernen
  public static String removeSepFromString(String s, String sep) {
    if (s == null) return null;
    s = replace(s,sep,"",true);
    return s;
  }


  // Alle runde Klammern aus String entfernen
  public static String removeBracketsFromString(String s) {
    if (s == null) return null;
    s = EfaUtil.replace(s,"(","",true);
    s = EfaUtil.replace(s,")","",true);
    return s;
  }



  // Dateinamen in Gro�buchstaben umwandelt, falls Windows-System
  public static String upcaseFileName(String s) {
    if (System.getProperty("file.separator").equals("\\")) return s.toUpperCase();
    else return s;
  }


  // Einen String in ein int umwandeln und bei Fehler "vorgabe" zur�ckliefern
  public static int string2int(String s,int vorgabe) {
    try {
      return Integer.parseInt(s);
    } catch (Exception e) {
      return vorgabe;
    }
  }


  // Einen String in ein long umwandeln und bei Fehler "vorgabe" zur�ckliefern
  public static long string2long(String s,int vorgabe) {
    try {
      return Long.parseLong(s);
    } catch (Exception e) {
      return vorgabe;
    }
  }


  // Methode zum kopieren einer Datei
  public static boolean copyFile(String quelle, String ziel) {
    if ((new File(quelle)).equals(new File(ziel))) return false; // Quelle und Ziel sind dieselbe Datei!
    final int BUFSIZE = 4096;
    FileInputStream f1;
    FileOutputStream f2;
    byte[] buf = new byte[BUFSIZE];
    int n;
    if (!canOpenFile(quelle)) return false;
    try {
      f1 = new FileInputStream(quelle);
      f2 = new FileOutputStream(ziel);
      while ((n = f1.read(buf,0,BUFSIZE)) > 0) f2.write(buf,0,n);
      f1.close();
      f2.close();
    } catch(IOException e) {
      return false;
    }
    return true;
  }

  // korrekte Laufende Nummer kreieren
  public static String getLfdNr(String t) {
    t = t.trim().toUpperCase();
    TMJ hhmm = EfaUtil.string2date(t,1,1,1); // TMJ mi�braucht f�r die Auswertung von Zahlen
    String s = Integer.toString(hhmm.tag);
    int wo = t.indexOf(s);
    int l = s.length();
    if (wo>=0 && wo+l<t.length() && t.charAt(wo+l)>='A' && t.charAt(wo+l)<='Z') // auch Buchstaben als Erg�nzung zur LfdNr zulassen
        s = s + t.charAt(wo+l);
    return s;
  }


  // zu einem gegebenen (Synonym-)Namen s aus einer Datenliste l den passenden Originalnamen heraussuchen
  public static String syn2org(Synonyme l, String s) {
    if (l == null) return s;
    if (l.getExact(s) != null) {
      DatenFelder d = (DatenFelder)l.getComplete();
      String orgs=null;
      if (d != null) orgs = d.get(Synonyme.ORIGINAL);
      if (orgs != null && !orgs.equals("")) return orgs;
    }
    return s;
  }


  // zu einem gegebenen OriginalNamen s aus einer Datenliste l alle passenden Synonymnamen heraussuchen
  public static Vector org2syn(Synonyme l, String s) {
    if (l == null || s == null) return null;
    Vector v = new Vector();
    for (DatenFelder d = l.getCompleteFirst(); d != null; d = l.getCompleteNext()) {
      if (d.get(Synonyme.ORIGINAL).equals(s)) v.add(d.get(Synonyme.SYNONYM));
    }
    if (v.size() == 0) return null;
    return v;
  }


  // Datei remote aus dem Internet herunterladen und als local speichern
  // return: true, wenn erfolgreich
  public static boolean getFile(JDialog frame, String remote, String local, boolean wait) {
    try {
      URLConnection conn = new URL(remote).openConnection();
      conn.connect();
      BrowserFrame b = new BrowserFrame();
      return b.runDownload(frame,conn,remote,local,wait);
    } catch(Exception e) {
      Dialog.error("Download fehlgeschlagen: "+e.toString()+"\nEventuell wurde efa durch eine Firewall blockiert.");
      return false;
    }
  }
  public static boolean getFile(JFrame frame, String remote, String local, boolean wait) {
    try {
      URLConnection conn = new URL(remote).openConnection();
      conn.connect();
      BrowserFrame b = new BrowserFrame();
      return b.runDownload(frame,conn,remote,local,wait);
    } catch(Exception e) {
      Dialog.error("Download fehlgeschlagen: "+e.toString()+"\nEventuell wurde efa durch eine Firewall blockiert.");
      return false;
    }
  }
  public static boolean getFile(JFrame frame, String remote, String local, ExecuteAfterDownload afterDownload) {
    try {
      URLConnection conn = new URL(remote).openConnection();
      conn.connect();
      BrowserFrame b = new BrowserFrame();
      return b.runDownload(frame,conn,remote,local,afterDownload);
    } catch(Exception e) {
      Dialog.error("Download fehlgeschlagen: "+e.toString()+"\nEventuell wurde efa durch eine Firewall blockiert.");
      return false;
    }
  }
  public static boolean getFile(JDialog frame, String remote, String local, ExecuteAfterDownload afterDownload) {
    try {
      URLConnection conn = new URL(remote).openConnection();
      conn.connect();
      BrowserFrame b = new BrowserFrame();
      return b.runDownload(frame,conn,remote,local,afterDownload);
    } catch(Exception e) {
      Dialog.error("Download fehlgeschlagen: "+e.toString()+"\nEventuell wurde efa durch eine Firewall blockiert.");
      return false;
    }
  }





  // aus einer Zahl 0<=i<=15 eine Hex-Ziffer machen
  public static char getHexDigit(int i) {
    if (i<10) return (char)(i+'0');
    else return (char)((i-10)+'A');
  }

  // aus einer Zahl 0<=i<=255 eine zweistellige Hex-Zahl machen
  public static String hexByte(int i) {
    return getHexDigit(i / 16) + "" + getHexDigit(i % 16);
  }



  // Auf String s den SHA-Algorithmus anwenden und Ergebnis Hex-Codiert zur�ckliefern
  public static String getSHA(String z) {
    if (z == null || z.length()==0) return null;
    return getSHA(z.getBytes());
  }

  // Auf Byte[] message den SHA-Algorithmus anwenden und Ergebnis Hex-Codiert zur�ckliefern
  public static String getSHA(byte[] message) {
    return getSHA(message,message.length);
  }
  public static String getSHA(byte[] message, int length) {
    if (message == null || length<=0) return null;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA");
      md.update(message,0,length);
      byte[] output = md.digest();
      String s = "";
      for (int i=0; i<output.length; i++)
        s += hexByte(output[i]+128);
      return s;
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  // SHA als long-Wert liefern (die letzten x bytes)
  public static long getSHAlong(byte[] message, int x) {
    return getSHAlong(message,message.length,x);
  }
  public static long getSHAlong(byte[] message, int length, int x) {
    if (message == null || length<=0) return -1;
    try {
      MessageDigest md = MessageDigest.getInstance("SHA");
      md.update(message,0,length);
      byte[] output = md.digest();
      long l = 0;
      for (int i=output.length-1; i>=0 && output.length-i<=x; i--)
        l = l*256 + (((long)output[i])+128);
      return l;
    } catch(Exception e) {
      e.printStackTrace();
      return -1;
    }
  }

  // Auf File f (nur max. erste 2 MB) den SHA-Algorithmus anwenden und Ergebnis Hex-Codiert zur�ckliefern
  public static String getSHA(File f) {
    return getSHA(f,(int)f.length());
  }
  public static String getSHA(File f, int length) {
    if (!f.isFile() || length<=0) return null;
    FileInputStream f1;
    byte[] buf = new byte[length];
    int n;
    try {
      f1 = new FileInputStream(f);
      n = f1.read(buf,0,length);
      f1.close();
    } catch(IOException e) {
      return null;
    }
    return getSHA(buf);
  }


  // aus einem zweistelligen Jahresdatum ggf. ein vierstelliges machen
  public static int yy2yyyy(int jahr) {
    if (jahr<100) jahr+= 1900;
    if (jahr<1850)
      while (jahr<1900) jahr += 100;
    return jahr;
  }


  // aus einem String einen String generieren, der eine korrekte Status-Liste enth�lt
  private static String correctStatusList(String s) {
    s = EfaUtil.replace(s," ,",",",true); // Leerzeichen vor und nach Eintr�gen entfernen
    s = EfaUtil.replace(s,", ",",",true); // Leerzeichen vor und nach Eintr�gen entfernen
    s = EfaUtil.replace(s,",,",",",true); // Leerzeichen vor und nach Eintr�gen entfernen
    return s.trim();
  }


  private static String makeTimeString(int value, int chars) {
    String s = Integer.toString(value);
    while (s.length()<chars) s = "0"+s;
    return s;
  }

  public static String getCurrentTimeStamp() {
    Calendar cal = new GregorianCalendar();
    return        makeTimeString(cal.get(Calendar.DAY_OF_MONTH),2)+"."+
                  makeTimeString(cal.get(Calendar.MONTH)+1,2)+"."+
                  makeTimeString(cal.get(Calendar.YEAR),4)+" "+
                  makeTimeString(cal.get(Calendar.HOUR_OF_DAY),2)+":"+
                  makeTimeString(cal.get(Calendar.MINUTE),2)+":"+
                  makeTimeString(cal.get(Calendar.SECOND),2);
  }

  public static String getTimeStamp(long l) {
    Calendar cal = new GregorianCalendar();
    cal.setTimeInMillis(l);
    return makeTimeString(cal.get(Calendar.DAY_OF_MONTH), 2) + "." +
           makeTimeString(cal.get(Calendar.MONTH) + 1, 2) + "." +
           makeTimeString(cal.get(Calendar.YEAR), 4) + " " +
           makeTimeString(cal.get(Calendar.HOUR_OF_DAY), 2) + ":" +
           makeTimeString(cal.get(Calendar.MINUTE), 2) + ":" +
           makeTimeString(cal.get(Calendar.SECOND), 2);
  }


  public static String getCurrentTimeStampYYYYMMDD_HHMMSS() {
    Calendar cal = new GregorianCalendar();
    return        makeTimeString(cal.get(Calendar.YEAR),4)+
                  makeTimeString(cal.get(Calendar.MONTH)+1,2)+
                  makeTimeString(cal.get(Calendar.DAY_OF_MONTH),2)+
                  "_"+
                  makeTimeString(cal.get(Calendar.HOUR_OF_DAY),2)+
                  makeTimeString(cal.get(Calendar.MINUTE),2)+
                  makeTimeString(cal.get(Calendar.SECOND),2);
  }

  public static String getCurrentTimeStampYYYY_MM_DD_HH_MM_SS() {
    Calendar cal = new GregorianCalendar();
    return        makeTimeString(cal.get(Calendar.YEAR),4)+
                  "/"+
                  makeTimeString(cal.get(Calendar.MONTH)+1,2)+
                  "/"+
                  makeTimeString(cal.get(Calendar.DAY_OF_MONTH),2)+
                  " "+
                  makeTimeString(cal.get(Calendar.HOUR_OF_DAY),2)+
                  ":"+
                  makeTimeString(cal.get(Calendar.MINUTE),2)+
                  ":"+
                  makeTimeString(cal.get(Calendar.SECOND),2);
  }

  public static String getCurrentTimeStampDD_MM_HH_MM() {
    Calendar cal = new GregorianCalendar();
    return        makeTimeString(cal.get(Calendar.DAY_OF_MONTH),2)+"."+
                  makeTimeString(cal.get(Calendar.MONTH)+1,2)+". "+
                  makeTimeString(cal.get(Calendar.HOUR_OF_DAY),2)+":"+
                  makeTimeString(cal.get(Calendar.MINUTE),2);
  }

  public static String getCurrentTimeStampDD_MM_YYYY() {
    Calendar cal = new GregorianCalendar();
    return        makeTimeString(cal.get(Calendar.DAY_OF_MONTH),2)+"."+
                  makeTimeString(cal.get(Calendar.MONTH)+1,2)+"."+
                  makeTimeString(cal.get(Calendar.YEAR),4);
  }

  public static String date2String(Date date) {
    return date2String(date,true);
  }
  public static String date2String(Date date, boolean printTime) {
    Calendar cal = new GregorianCalendar();
    cal.setTime(date);
    return        makeTimeString(cal.get(Calendar.DAY_OF_MONTH),2)+"."+
                  makeTimeString(cal.get(Calendar.MONTH)+1,2)+"."+
                  makeTimeString(cal.get(Calendar.YEAR),4)+
                  (printTime ?
                  " "+
                  makeTimeString(cal.get(Calendar.HOUR_OF_DAY),2)+":"+
                  makeTimeString(cal.get(Calendar.MINUTE),2)+":"+
                  makeTimeString(cal.get(Calendar.SECOND),2)
                  : "");
  }

  public static String getWoTag(String datum) {
    if (datum == null || datum.equals("")) return "";
    TMJ tmj = correctDate(datum,0,0,0);
    Calendar cal = new GregorianCalendar();
    cal.set(tmj.jahr,tmj.monat-1,tmj.tag);
    switch (cal.get(GregorianCalendar.DAY_OF_WEEK)) {
      case Calendar.MONDAY:    return "Montag";
      case Calendar.TUESDAY:   return "Dienstag";
      case Calendar.WEDNESDAY: return "Mittwoch";
      case Calendar.THURSDAY:  return "Donnerstag";
      case Calendar.FRIDAY:    return "Freitag";
      case Calendar.SATURDAY:  return "Samstag";
      case Calendar.SUNDAY:    return "Sonntag";
      default: return "";
    }
  }

  // pr�ft, ob im String s das Zeichen pos ein "+" ist. Falls der String zu kurz ist, wird false geliefert
  public static boolean isOptionSet(String s, int pos) {
    if (s == null || pos < 0 || pos >= s.length()) return false; // out of range == not set
    return s.charAt(pos) == '+';
  }


  public static GregorianCalendar dateTime2Cal(String dateTTMMJJ, String timeHHMMSS) {
    TMJ datum = EfaUtil.correctDate(dateTTMMJJ,1,1,1980);
    TMJ zeit  = EfaUtil.string2date(timeHHMMSS,0,0,0);
    return new GregorianCalendar(datum.jahr,datum.monat-1,datum.tag,zeit.tag,zeit.monat,zeit.jahr);
  }

  public static GregorianCalendar dateTime2Cal(TMJ datum, TMJ zeit) {
    return new GregorianCalendar(datum.jahr,datum.monat-1,datum.tag,zeit.tag,zeit.monat,zeit.jahr);
  }

  public static Color getColor(String s) {
    int cint;
    try {
      cint = Integer.parseInt(s,16);
    } catch(Exception ee) {
      cint = 204*65536 + 204*256 + 204;
    }
    return new Color(cint);
  }

  public static String getColor(Color c) {
    String s = "";
    float[] rgb = c.getRGBColorComponents(null);
    for (int i=0; i<rgb.length; i++)
      s += EfaUtil.hexByte((int)(rgb[i]*255));
    return s;
  }


  public static void gc() {
    long totalMem = Runtime.getRuntime().totalMemory();
    long freeMem  = Runtime.getRuntime().freeMemory();
    Stopwatch w = new Stopwatch();
    w.start();
    System.gc();
    w.stop();
    Dialog.infoDialog("GarbageCollection","GarbageCollection ausgef�hrt in "+w.diff()+"ms.\n"+
                      "Vor GarbageCollection:\n"+
                      "    gesamter VM Speicher: "+totalMem+" Bytes\n"+
                      "    freier Speicher: "+freeMem+" Bytes\n"+
                      "Nach GarbageCollection:\n"+
                      "    gesamter VM Speicher: "+Runtime.getRuntime().totalMemory()+" Bytes\n"+
                      "    freier Speicher: "+Runtime.getRuntime().freeMemory()+" Bytes");
  }

  static class Stopwatch {
    private long time_start, time_stop;
    public void start() { time_start = System.currentTimeMillis(); }
    public void stop()  { time_stop  = System.currentTimeMillis(); }
    public long diff() { return time_stop - time_start; }
  }

  // does nothing.... ;-) Used for Exception-Hanling, so that FindBugs is happy!
  public static void foo() {
  }

  // Entpacken eines Ziparchivs zipFile in einem Verzeichnis destDir
  // R�ckgabe: null, wenn erfolgreich; String != null mit Fehlermeldungen, sonst
  public static String unzip(String zipFile, String destDir) {
    if ( !(new File(zipFile)).isFile() ) return "ZIP-Datei "+zipFile+" nicht gefunden!";
    if ( !(new File(destDir)).isDirectory() ) return "Ziel-Verzeichnis "+destDir+" existiert nicht!";

    if (!destDir.endsWith(Daten.fileSep)) destDir += Daten.fileSep;
    String result = null;

    try {
      ZipFile zip = new ZipFile(zipFile);
      Enumeration files = zip.entries();
      ZipEntry file = (ZipEntry)files.nextElement();
      while (file != null) {
        String filename = file.getName();
        if (file.isDirectory()) {
          // Verzeichnis
          if (!(new File(destDir + filename)).isDirectory()) {
            if (!(new File(destDir + filename)).mkdirs())
              result = (result == null ? "" : result+"\n") + "Fehler beim Entpacken: Erstellen des Verzeichnisses "+destDir+filename+" fehlgeschlagen!\nEntpacken abgebrochen.";
          }
        } else {
          // normale Datei
          try {
            // enth�lter Dateiname einen Verzeichnisnamen eines Verzeichnisses, das m�glicherweise noch nicht existiert?
            String dir = null;
            if (filename.lastIndexOf("/")>=0) dir = filename.substring(0,filename.lastIndexOf("/"));
            if (dir == null && filename.lastIndexOf("\\")>=0) dir = filename.substring(0,filename.lastIndexOf("\\"));
            if (dir != null && !(new File(destDir + dir)).isDirectory()) {
              if (!(new File(destDir + dir)).mkdirs())
                result = (result == null ? "" : result+"\n") + "Fehler beim Entpacken: Erstellen des Verzeichnisses "+destDir+filename+" fehlgeschlagen!\nEntpacken abgebrochen.";
            }

            // jetzt die Datei entpacken
            BufferedInputStream stream = new BufferedInputStream(zip.getInputStream(file));
            BufferedOutputStream f = new BufferedOutputStream(new FileOutputStream(destDir + filename));
            byte[] buf = new byte[ZIP_BUFFER];
            int read;
            while ( (read = stream.read(buf,0,ZIP_BUFFER)) != -1) {
              f.write(buf,0,read);
            }
            f.close();
            stream.close();
          } catch(Exception e) {
            result = (result == null ? "" : result+"\n") + "Fehler beim Entpacken: Entpacken der Datei "+filename+" fehlgeschlagen: "+e.toString();
          }
        }
        file = (files.hasMoreElements() ? (ZipEntry)files.nextElement() : null);
      }
    } catch(Exception ee) {
      result = (result == null ? "" : result+"\n") + "Fehler beim Entpacken: "+ee.toString()+"\nEntpacken abgebrochen.";
    }
    return result;
  }


  public static String createZipArchive(Vector sourceDirs, Vector inclSubdirs, String zipFile) {
    try {
      String warnings = "";
      BufferedInputStream origin = null;
      FileOutputStream dest = new FileOutputStream(zipFile);
      ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
      Hashtable processedDirectories = new Hashtable();
      byte data[] = new byte[ZIP_BUFFER];
      for (int j=0; j<sourceDirs.size(); j++) {
        // get a list of files from current directory
        String dir = (String)sourceDirs.get(j);
        if (!dir.endsWith(Daten.fileSep)) dir += Daten.fileSep;
        if (processedDirectories.get(dir) != null) { continue; }
        processedDirectories.put(dir,"foobar");
        File f = new File(dir);
        String files[] = f.list();

        // relative directory (for storing file in zipfile)
        String reldir = dir;
        if (Daten.efaMainDirectory != null && reldir.startsWith(Daten.efaMainDirectory)) reldir = reldir.substring(Daten.efaMainDirectory.length(),reldir.length());
        if (reldir.startsWith(Daten.fileSep)) reldir = reldir.substring(1,reldir.length());
        if (reldir.length()>2 && reldir.charAt(1) == ':' && reldir.charAt(2) == Daten.fileSep.charAt(0)) reldir = reldir.substring(3,reldir.length());
        if (!Daten.fileSep.equals("/")) reldir = EfaUtil.replace(reldir,Daten.fileSep,"/",true); // Bugfix: in <=160 konnten die unter Windows erstellten ZIP-Archive unter Linux nicht richtig gelesen werden

        for (int i=0; i<files.length; i++) {
          if ((new File(dir+files[i])).isDirectory()) {
            if (j >= inclSubdirs.size() || // j >= inclSubdirs.size() == true, wenn das Verzeichnis zuvor durch folgende Zeile dynamisch hinzugef�gt wurde
                ((Boolean)inclSubdirs.get(j)).booleanValue()) sourceDirs.add(dir+files[i]);
          } else {
            try {
              FileInputStream fi = new FileInputStream(dir+files[i]);
              origin = new BufferedInputStream(fi, ZIP_BUFFER);
              ZipEntry entry = new ZipEntry(reldir+files[i]);
              out.putNextEntry(entry);
              int count;
              while((count = origin.read(data, 0, ZIP_BUFFER)) != -1) {
                out.write(data, 0, count);
              }
            } catch(Exception se) {
              warnings += dir+files[i]+"\n";
            }
            origin.close();
          }
        }
      }
      out.close();
      if (warnings.length()>0) {
        return "Folgende Dateien konnten nicht gesichert werden:\n"+warnings;
      }
    } catch(Exception e) {
      return e.toString();
    }
    return null; // erfolgreich
  }

  public static String moveAndEmptyFile(String fname, String dstDir) {
    try {
      if (fname == null || dstDir == null) return null;
      File f = new File(fname);
      if (!f.isFile()) return null;
      if (!(new File(dstDir)).isDirectory()) return null;
      String newname = dstDir +
                       getNameOfFile(fname) +
                       getCurrentTimeStampYYYYMMDD_HHMMSS();
      if (!copyFile(fname,newname)) return null;
      if (!f.delete()) return null;
      if (!f.createNewFile()) return null;
      return newname;
    } catch(IOException e) {
      return null;
    }
  }


  public static String cent2euro(int cent, boolean currency) {
    String s = Integer.toString(cent);
    while (s.length()<3) s = "0" + s;
    s = s.substring(0,s.length()-2) + "," + s.substring(s.length()-2,s.length());
    if (s.endsWith(",00")) s = s.substring(0,s.length()-2) + "-";
    if (currency) return s + " EUR";
    else return s;
  }
/*
  public static String getCertInfos(String keyStore, String alias, char[] password) {
    PrintStream stdOut = System.out;
    CaptureOutputPrintStream cops = new CaptureOutputPrintStream(new PipedOutputStream());
    System.setOut(cops);
    try {
      sun.security.tools.KeyTool.main(EfaUtil.kommaList2Arr("-list -v -alias "+alias+
                                      " -storepass "+password+
                                      " -keystore "+keyStore,' ',false));
    } catch(Throwable e) {
      return "";
    }
    System.setOut(stdOut);
    String s = "";
    Vector v = cops.getLines();
    for (int i=0; i<v.size(); i++) s += v.get(i).toString()+"\n";
    return s;
  }
*/
  public static boolean deleteFile(String filename) {
    if (filename == null) return false;
    try {
      File f = new File(filename);
      return f.delete();
    } catch(Exception e) {
      return false;
    }
  }

  public static String leadingZeroString(int i, int length) {
    String s = Integer.toString(i);
    while (s.length()<length) s = "0"+s;
    return s;
  }

  public static int sumUpArray(int[] array) {
    if (array == null) return 0;
    int sum = 0;
    for (int i=0; i<array.length; i++) sum += array[i];
    return sum;
  }

  public static String vector2string(Vector v, String sep) {
    if (v == null) return null;
    String s = "";
    for (int i=0; i<v.size(); i++) {
      s += (i>0 ? sep : "") + v.get(i);
    }
    return s;
  }

  // wenn das Datum zwischen 15.07. und 31.07. liegt (Geburtstag),
  // gibt diese Methode zur�ck, der wievielte Geburtstag es ist.
  // wenn das Datum nicht im o.g. Zeitraum liegt, gibt diese Methode -1 zur�ck.
  public static int getEfaBirthday() {
    Calendar cal = new GregorianCalendar();
    boolean birthday = cal.get(Calendar.MONTH)+1 == 7 &&
                       cal.get(Calendar.DAY_OF_MONTH) >= 15;
    if (birthday) return cal.get(Calendar.YEAR) - 2001;
    else return -1;
  }

  public static void pack(JDialog frame) {
    // Bugfix/Workaround f�r Problem, da� Fenster manchmal zu klein
    try {
      setMinimumSize(frame);
      frame.pack();
      frame.setSize(frame.getWidth()+2,frame.getHeight()+2);
    } catch(Exception e) {
    }
  }

  // Setzt f�r die Komponente c und alle ihre Subkomponenten die MinimumSize so, da� sie der PreferredSize entspricht
  public static void setMinimumSize(Component c) {
    try {
      if (java_awt_Container.getClass().isInstance(c)) {
        Container container = (Container)c;
        Component[] components = container.getComponents();
        for (int i=0; components != null && i<components.length; i++) {
          setMinimumSize(components[i]);
        }
      }
      JComponent jcomponent = (JComponent)c;
      jcomponent.setMinimumSize(jcomponent.getPreferredSize());
    } catch(Exception e) {
    }
  }


  public static String genAlias(String aliasFormat, String vorname, String nachname, String verein) {
    String s = "";
    int i=0;
    while (i<aliasFormat.length()) {
      if (aliasFormat.charAt(i) == '{') {
        if (aliasFormat.length() < i+4 || aliasFormat.charAt(i+3) != '}') {
          Logger.log(Logger.INFO,"NeuesMitgliedFrame: Fehler beim Parsen des Eingabe-K�rzel-Formats!");
          return "";
        }
        String feld=null;
        switch (aliasFormat.charAt(i+1)) {
          case 'v': case 'V': feld = vorname.trim().toLowerCase(); break;
          case 'n': case 'N': feld = nachname.trim().toLowerCase(); break;
          case 'c': case 'C': feld = verein.trim().toLowerCase(); break;
        }
        if (feld != null && aliasFormat.charAt(i+2)>'0' && aliasFormat.charAt(i+2)<='9') {
          int pos = aliasFormat.charAt(i+2) - '0';
          if (feld.length()>=pos) s += feld.charAt(pos-1);
        }
        i += 3;
      } else s += aliasFormat.charAt(i);
      i++;
    }
    return s;
  }

  public static void sleep(int ms) {
    try {
      Thread.sleep(ms);
    } catch(InterruptedException e) {
      foo();
    }
  }

  // just for test purpose
  public static void main(String[] args) {
    TMJ tmj = string2date("23.01.1979",0,0,0); System.out.println(tmj.tag+"    "+tmj.monat+"    "+tmj.jahr);
        tmj = string2date("-42",0,0,0); System.out.println(tmj.tag+"    "+tmj.monat+"    "+tmj.jahr);
        tmj = string2date("1979-1994",0,0,0); System.out.println(tmj.tag+"    "+tmj.monat+"    "+tmj.jahr);
        tmj = string2date("1979 -1994",0,0,0); System.out.println(tmj.tag+"    "+tmj.monat+"    "+tmj.jahr);
        tmj = string2date("  19-79-94  ",0,0,0); System.out.println(tmj.tag+"    "+tmj.monat+"    "+tmj.jahr);
        tmj = string2date("  19-",0,0,0); System.out.println(tmj.tag+"    "+tmj.monat+"    "+tmj.jahr);
        tmj = string2date("  19- ",0,0,0); System.out.println(tmj.tag+"    "+tmj.monat+"    "+tmj.jahr);
  }

}
