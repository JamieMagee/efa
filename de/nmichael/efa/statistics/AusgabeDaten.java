/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.statistics;

import java.util.*;

public class AusgabeDaten {

  // globale Daten
  String titel = null;
  String ausgewertetAm = null;
  String ausgewertetVon = null;
  String ausgewertetVonURL = null;
  String auswertungsArt = null;
  String auswertungsZeitraum = null;
  String ausgewerteteEintraege = null;
  String[] auswertungFuer = null;
  String auswertungNurFuer = null;
  String auswertungNurFuerBez = null;
  String auswertungWettNur = null;

  // Tabellen-Titel
  String[] tabellenTitel = null;
  int[] tabellenTitelBreite = null;

  // erster normaler Eintrag
  AusgabeEintrag ae;

  // erster TabellenFolgen-Eintrag
  TabellenFolgenEintrag tfe;

  // Wettbewerbe
  String wett_zeitraumWarnung = null;
  String[] wett_bedingungen = null;
  Hashtable wett_bedingungen_fett = new Hashtable();
  Hashtable wett_bedingungen_kursiv = new Hashtable();

//  String wett_bedingungen = null;
  String[][] wett_gruppennamen = null;
  AusgabeEintrag[] wett_teilnehmerInGruppe = null;

  // weitere Tabelle für Zusatzdaten
  String[][] additionalTable = null;
  boolean additionalTable_1stRowBold = false;
  boolean additionalTable_lastRowBold = false;

  AusgabeZeilen ausgabeZeilenOben = null;
  AusgabeZeilen ausgabeZeilenUnten = null;

}
