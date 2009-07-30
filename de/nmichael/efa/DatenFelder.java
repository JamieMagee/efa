package de.nmichael.efa;

/**
 * Title:        efa - Elektronisches Fahrtenbuch
 * Description:  Datenfelder f�r einen Eintrag in der DatenListe
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author Nicolas Michael
 * @version 1.0
 */

public class DatenFelder {

  String[] a;

  // Konstruktor; n: Anzahl der gew�nschten Felder
  public DatenFelder(int n) {
    a = new String[n];
  }


  public DatenFelder(int n, String s) {
    a = new String[n];
    // Splitte Felder anhand Trennzeichen "|"
    int from = 0, to = -1;
    for(int i = 0; i<n; i++) {
      if ( (to = s.indexOf("|",to+1)) != -1 || to<s.length() ) {
        if (to == -1) to = s.length();
        if (from<s.length()) set(i,s.substring(from,to).trim());
        else set(i,"");
        from = to+1;
      }
    }
  }


  // Copy-Konstruktor
  public DatenFelder(DatenFelder d) {
    a = new String[d.a.length];
    for (int i=0; i<a.length; i++) a[i] = d.a[i];
  }

  // Feld n mit String s belegen
  public void set(int n, String s) {
    if (n>=0 && n<a.length) a[n] = s;
  }

  // Wert von Feld n ermitteln
  public String get(int n) {
    if (n>=0 && n<a.length) return ( a[n] != null ? a[n] : "");
    else return null;
  }

}
