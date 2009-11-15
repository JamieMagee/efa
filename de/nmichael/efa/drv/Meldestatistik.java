/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.drv;

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.core.*;
import java.io.*;

public class Meldestatistik extends DatenListe {

  public static final int _ANZFELDER = 26;

  public static final int KEY = 0; // VEREINSMITGLNR#VORNAME#NACHNAME#JAHRGANG
  public static final int VEREINSMITGLNR = 1;
  public static final int VEREIN = 2;
  public static final int VORNAME = 3;
  public static final int NACHNAME = 4;
  public static final int JAHRGANG = 5;
  public static final int GESCHLECHT = 6;
  public static final int KILOMETER = 7;
  public static final int GRUPPE = 8;
  public static final int ANZABZEICHEN = 9;
  public static final int ANZABZEICHENAB = 10;
  public static final int AEQUATOR = 11;
  public static final int GESKM = 12;
  public static final int WS_BUNDESLAND = 13;
  public static final int WS_MITGLIEDIN = 14;
  public static final int WS_GEWAESSER = 15;
  public static final int WS_TEILNEHMER = 16;
  public static final int WS_MANNSCHKM = 17;
  public static final int WS_MAENNERKM = 18;
  public static final int WS_JUNIORENKM = 19;
  public static final int WS_FRAUENKM = 20;
  public static final int WS_JUNIORINNENKM = 21;
  public static final int WS_AKT18M = 22;
  public static final int WS_AKT19M = 23;
  public static final int WS_AKT18W = 24;
  public static final int WS_AKT19W = 25;


  public static final String KENNUNG151 = "##EFA.151.MELDESTATISTIK##";
  public static final String KENNUNG160 = "##EFA.160.MELDESTATISTIK##";
  public static final String KENNUNG183 = "##EFA.183.MELDESTATISTIK##";

  // Konstruktor
  public Meldestatistik(String pdat) {
    super(pdat,_ANZFELDER,1,false);
    kennung = KENNUNG183;
  }


  // Dateiformat überprüfen, ggf. konvertieren
  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {

        // KONVERTIEREN: 151 -> 160
        if (s != null && s.trim().startsWith(KENNUNG151)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"151");
          iniList(this.dat,22,1,true); // Rahmenbedingungen von v160 schaffen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              s += "|||||||||";
              add(s);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG160;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // KONVERTIEREN: 160 -> 183
        if (s != null && s.trim().startsWith(KENNUNG160)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"160");
          iniList(this.dat,26,1,true); // Rahmenbedingungen von v183 schaffen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              s += "||||";
              add(s);
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG183;
          if (closeFile() && writeFile(true) && openFile()) {
            infSuccessfullyConverted(dat,kennung);
            s = kennung;
          } else errConvertingFile(dat,kennung);
        }

        // FERTIG MIT KONVERTIEREN
        if (s == null || !s.trim().startsWith(kennung)) {
          errInvalidFormat(dat, EfaUtil.trimto(s, 20));
          fclose(false);
          return false;
        }
      }
    } catch(IOException e) {
      errReadingFile(dat,e.getMessage());
      return false;
    }
    return true;
  }




}
