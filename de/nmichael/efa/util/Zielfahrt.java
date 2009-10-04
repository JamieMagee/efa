package de.nmichael.efa.util;

import de.nmichael.efa.*;
import java.util.*;


public class Zielfahrt {

  public static final int ANZ_ZIELBEREICHE = 13;
  public static String SEPARATOR;

  private String datum;
  private String ziel;
  private String km;
  private boolean[] zb;

  public Zielfahrt() {
    ini();
  }

  public Zielfahrt(String datum, String ziel, String km, String zb) {
    ini();
    setDatum(datum);
    setZiel(ziel);
    setKm(km);
    setBereich(zb);
  }

  private void ini() {
    zb = new boolean[ANZ_ZIELBEREICHE + 1]; // Element 0 ist Dummy
    Arrays.fill(zb,false);
    if (Daten.efaConfig != null && Daten.efaConfig.zielfahrtSeparatorBereiche != null &&
        Daten.efaConfig.zielfahrtSeparatorBereiche.length() == 1) {
      SEPARATOR = Daten.efaConfig.zielfahrtSeparatorBereiche;
    } else {
      SEPARATOR = ",";
    }
  }

  public void setBereich(int bereich, boolean erreicht) {
    if (bereich>=1 && bereich<=ANZ_ZIELBEREICHE) zb[bereich] = erreicht;
  }

  public void setDatum(String datum) {
    this.datum = datum;
  }

  public void setZiel(String ziel) {
    this.ziel = ziel;
  }

  public void setKm(String km) {
    this.km = km;
  }

  public void setBereich(String zb) {
    if (zb != null) {
      String number = "";
      for (int i=0; i<zb.length(); i++) {
        if (zb.charAt(i) >= '0' && zb.charAt(i) <= '9') {
          number += zb.charAt(i);
        } else {
          if (number.length() > 0) {
            try {
              setBereich(Integer.parseInt(number),true);
            } catch(Exception e) {}
            number = "";
          }
        }
      }
      if (number.length() > 0) {
        try {
          setBereich(Integer.parseInt(number),true);
        } catch(Exception e) {}
      }
    }
  }

  public String getDatum() {
    return (datum != null ? datum : "");
  }

  public String getZiel() {
    return (ziel != null ? ziel : "");
  }

  public String getKm() {
    return (km != null ? km : "");
  }

  public String getBereiche() {
    String s = "";
    for (int i=1; i<zb.length; i++) if (zb[i]) s += (s.length()>0 ? SEPARATOR : "") + i;
    return s;
  }

  public String[] getBereicheAsArray() {
    String[] a = new String[getAnzBereiche()];
    int c=0;
    for (int i=1; i<zb.length; i++) if (zb[i]) a[c++] = Integer.toString(i);
    return a;
  }

  public int getAnzBereiche() {
    int c=0;
    for (int i=1; i<zb.length; i++) if (zb[i]) c++;
    return c;
  }

  public boolean isErreicht(int bereich) {
    return ( (bereich>=1 && bereich<=ANZ_ZIELBEREICHE) ? zb[bereich] : false);
  }

  public String[] toStringArray() {
    String[] a = new String[4];
    a[0] = getDatum();
    a[1] = getZiel();
    a[2] = getKm();
    a[3] = getBereiche();
    return a;
  }

}

