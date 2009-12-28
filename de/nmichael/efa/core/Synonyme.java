/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core;

import de.nmichael.efa.core.DatenListe;
import java.io.*;
import java.util.Hashtable;
import de.nmichael.efa.*;
import de.nmichael.efa.util.*;

// @i18n complete

public class Synonyme extends DatenListe {

  public static final int _FELDERANZAHL = 2; // Anzahl der Felder für DatenListe

  public static final int SYNONYM = 0;
  public static final int ORIGINAL = 1;

  public static final String KENNUNG091 = "##EFA.091.SYNONYME##";
  public static final String KENNUNG190 = "##EFA.190.SYNONYME##";

  // Konstruktor
  public Synonyme(String pdat) {
    super(pdat,_FELDERANZAHL,1,false);
    kennung = KENNUNG190;
  }


  // alle Einträge löschen
  public void removeAllSyns() {
    this.l = new SortedList(false);
  }

  public boolean checkFileFormat() {
    String s;
    try {
      s = freadLine();
      if ( s == null || !s.trim().startsWith(kennung) ) {
        // KONVERTIEREN: 091 -> 190
        if (s != null && s.trim().startsWith(KENNUNG091)) {
          if (Daten.backup != null) Daten.backup.create(dat,Backup.CONV,"091");
          iniList(this.dat,2,1,false); // Rahmenbedingungen von v1.9.0 schaffen
          // Datei lesen
          try {
            while ((s = freadLine()) != null) {
              s = s.trim();
              if (s.equals("") || s.startsWith("#")) continue; // Kommentare ignorieren
              add(constructFields(s));
            }
          } catch(IOException e) {
             errReadingFile(dat,e.getMessage());
             return false;
          }
          kennung = KENNUNG190;
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
