/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.statistics;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.EfaWettMeldung;
import de.nmichael.efa.core.WettDef;
import de.nmichael.efa.core.WettDefs;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.types.DataTypeDate;
import de.nmichael.efa.data.types.DataTypeDistance;
import de.nmichael.efa.data.types.DataTypeList;
import de.nmichael.efa.util.EfaUtil;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.UUID;
import java.util.Vector;

public class CompetitionDRVWanderruderstatistik extends Competition {

    // aktive Mitglieder
    protected static final String AKTIV_M_AB19 = "M19";
    protected static final String AKTIV_M_BIS18 = "M18";
    protected static final String AKTIV_W_AB19 = "W19";
    protected static final String AKTIV_W_BIS18 = "W18";
    
    protected Hashtable<String,String> alleWW = new Hashtable<String,String>();
    protected AlleWWArrEl[] alleWWArr;
    protected Hashtable<UUID,String> alleAktive = new Hashtable<UUID,String>();

    protected Waters waters = Daten.project.getWaters(false);

    public static Object getAggregationKey(LogbookRecord r, long validAt) {
        if (!CompetitionDRVFahrtenabzeichen.mayBeWafa(r)) {
            return null;
        }

        SessionGroupRecord sg = r.getSessionGroup();
        if (sg != null) {
            return "##" + sg.getStartDate().toString() +
                   "##" + sg.getName();
        } else {
            return "##" + r.getDate().toString() +
                   "##" + r.getDistance().getStringValueInKilometers() +
                   "##" + r.getDestinationAndVariantName(validAt);
        }
    }

    static long getSumOfAllHashEntries(Hashtable h) {
        try {
            Object[] keys = h.keySet().toArray();
            int sum = 0;
            for (int i = 0; i < keys.length; i++) {
                sum += ((Long) h.get(keys[i])).longValue();
            }
            return sum;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    static String printWatersHash(Hashtable<String,String> whash) {
        String[] waters = whash.keySet().toArray(new String[0]);
        Arrays.sort(waters);
        StringBuilder s = new StringBuilder();
        for (int i=0; i<waters.length; i++) {
            s.append( (s.length() > 0 ? ", " : "") + waters[i]);
        }
        return s.toString();
    }

    public void calculateAggregation(Hashtable<Object,StatisticsData> data,
            LogbookRecord r, Object key,
            PersonRecord person) {
        if (key == null) {
            return;
        }
        StatisticsData sd = data.get(key);
        if (sd == null) {
            sd = new StatisticsData(sr);
            sd.key = key;
        }

        // Alter des Teilnehmers
        DataTypeDate birthday = person.getBirthday();
        int jahrgang = (birthday != null && birthday.getYear() > 1900 ? birthday.getYear() : 0);
        if (jahrgang == 0) {
            // @i18n only Germany, no need to translate
            String wtext = "Das Alter des Teilnehmers '" + person.getQualifiedName() + "' konnte nicht ermittelt werden, da sein/ihr Jahrgang "
                    + "nicht in efa erfaßt ist! Fahrten dieses Teilnehmers werden ignoriert.\n";
            sr.cWarnings.put(wtext, "foo");
            return;
        }
        int alter = sr.sCompYear - jahrgang;

        long distanceInMeters = (r.getDistance() != null ? r.getDistance().getValueInMeters() : 0);
        UUID personId = person.getId();

        // Anzahl der aktiven Ruderer ermitteln
        if (distanceInMeters >= 1000) { // mind. 1 Km gerudert
            if (alter > 18) { // über 18 Jahre
                if (person.getGender().equals(EfaTypes.TYPE_GENDER_MALE)) { // männlich
                    alleAktive.put(personId, AKTIV_M_AB19);
                } else { // weiblich
                    alleAktive.put(personId, AKTIV_W_AB19);
                }
            } else { // bis 18 Jahre
                if (person.getGender().equals(EfaTypes.TYPE_GENDER_MALE)) { // männlich
                    alleAktive.put(personId, AKTIV_M_BIS18);
                } else { // weiblich
                    alleAktive.put(personId, AKTIV_W_BIS18);
                }
            }
        }

        SessionGroupRecord sg = r.getSessionGroup();
        DestinationRecord destination = r.getDestinationRecord(r.getValidAtTimestamp());
        if (sd.compData == null) {
            sd.compData = new CompetitionData();
        }
        if (sg != null) { // gefundene Mehrtagesfahrt
            sd.compData.activeDays = sg.getActiveDays();
        } else { // keine Mehrtagestour (oder nicht gefunden)
            sd.compData.activeDays = r.getNumberOfDays();
        }
        if (destination != null && destination.getWatersIdList() != null) {
            DataTypeList<UUID> waterList = destination.getWatersIdList();
            for (int i = 0; i < waterList.length(); i++) {
                WatersRecord w = waters.getWaters(waterList.get(i));
                if (w != null) {
                    sd.compData.waters.put(w.getQualifiedName(), "foo");
                }
            }
        }

        // Daten der Fahrt füllen
        Long meters;
        String fahrtName = (sg != null ? sg.getName() : r.getDestinationAndVariantName(sr.sValidAt));
        String fahrtDetails = (sg != null ? sg.getRoute() : null);
        String etappenName = r.getDate().toString() + "##" + r.getDestinationAndVariantName(sr.sValidAt);
        sd.sName = fahrtName;
        sd.sAdditional = fahrtDetails;

        // Kilometer dieser Etappe (Etappe kann auch gesamte Fahrt sein)
        meters = (Long) sd.compData.etappen.get(etappenName);
        if (meters == null) {
                sd.compData.etappen.put(etappenName, new Long(distanceInMeters));
            } else {
                if (meters.longValue() != distanceInMeters) {
                    // Dies kann vorkommen, wenn eine Etappe aufgespalten wurde:
                    // Zwei Boote rudern eine Ruderfahrt
                    // Boot 1 rudert Etappe A  80 Km
                    // Boot 2 rudert Etappe A1 50 Km und wechselt dann
                    // Boot 2 rudert Etappe A2 30 Km nach dem Wechsel

                    // immer den größeren Km-Wert verwenden (falls eine Etappe wegen Landdienstwechsel aufgeteilt wurde)
                    int oldvalue = meters.intValue();
                    if (distanceInMeters > meters.intValue()) {
                        meters = new Long(distanceInMeters);
                        sd.compData.etappen.put(etappenName, new Long(distanceInMeters));
                    }

                    // @i18n only Germany, no need to translate
                    String newWarn = "Etappe '" + fahrtName + ": " + etappenName + "' kommt mit unterschiedlichen Entfernungen ("
                            + DataTypeDistance.getDistanceFromMeters(oldvalue).getStringValueInKilometers(true, 0, 1)
                            + " und " +
                            DataTypeDistance.getDistanceFromMeters(distanceInMeters).getStringValueInKilometers(true, 0, 1)
                            + " Km) vor (Wert '" + 
                            DataTypeDistance.getDistanceFromMeters(meters).getStringValueInKilometers(true, 0, 1)
                            + " Km' wird verwendet)!";
                    sr.cWarnings.put(newWarn, "foo"); // nur neue Warnungen hinzufügen
                }
            }

            // Berechnung der Mannschaftskilometer
            sd.compData.totalDistanceInMeters += distanceInMeters;

            // Kilometer für einzelne Altersgruppen hinzufügen
            if (alter > 18) { // über 18 Jahre
                if (person.getGender().equals(EfaTypes.TYPE_GENDER_MALE)) { // männlich
                    meters = sd.compData.teilnMueber18.get(personId);
                    if (meters == null) {
                        sd.compData.teilnMueber18.put(personId, new Long(distanceInMeters));
                    } else {
                        sd.compData.teilnMueber18.put(personId, new Long(meters + distanceInMeters));
                    }
                } else { // weiblich
                    meters = sd.compData.teilnFueber18.get(personId);
                    if (meters == null) {
                        sd.compData.teilnFueber18.put(personId, new Long(distanceInMeters));
                    } else {
                        sd.compData.teilnFueber18.put(personId, new Long(meters + distanceInMeters));
                    }
                }
            } else { // bis 18 Jahre
                if (person.getGender().equals(EfaTypes.TYPE_GENDER_MALE)) { // männlich
                    meters = sd.compData.teilnMbis18.get(personId);
                    if (meters == null) {
                        sd.compData.teilnMbis18.put(personId, new Long(distanceInMeters));
                    } else {
                        sd.compData.teilnMbis18.put(personId, new Long(meters + distanceInMeters));
                    }
                } else { // weiblich
                    meters = sd.compData.teilnFbis18.get(personId);
                    if (meters == null) {
                        sd.compData.teilnFbis18.put(personId, new Long(distanceInMeters));
                    } else {
                        sd.compData.teilnFbis18.put(personId, new Long(meters + distanceInMeters));
                    }
                }
            }

        data.put(key, sd);
    }


    // Ausgabedaten für Kilometerwettbewerbe erstellen
    // @i18n Methode wird nicht internationalisiert
    public void calculate(StatisticsRecord sr, StatisticsData[] sd) {
        WettDef wett = Daten.wettDefs.getWettDef(WettDefs.DRV_WANDERRUDERSTATISTIK, sr.sCompYear);
        if (wett == null) {
            return;
        }
        sr.pTableColumns = null;

        if (sr.sIsOutputCompRules) {
            sr.pCompRules = createAusgabeBedingungen(sr, wett.key, sr.pCompRulesBold, sr.pCompRulesItalics);
        }

        if (!checkWettZeitraum(sr.sCompYear, sr.sStartDate, sr.sEndDate, WettDefs.DRV_WANDERRUDERSTATISTIK)) {
            sr.pCompWarning = "Achtung: Der gewählte Zeitraum entspricht nicht der Ausschreibung!";
        }

        if (sr.getOutputTypeEnum() == StatisticsRecord.OutputTypes.efawett) {
            efaWett.verein_mitglnr = Daten.project.getClubGlobalAssociationMemberNo();
            efaWett.verein_ort = Daten.project.getClubAddressCity();
            efaWett.verein_lrv = Daten.project.getClubRegionalAssociationName();
            efaWett.verein_mitgl_in = (Daten.project.getClubMemberOfDRV() ? "DRV" : "") + ";"
                    + (Daten.project.getClubMemberOfSRV() ? "SRV" : "") + ";"
                    + (Daten.project.getClubMemberOfADH() ? "ADH" : "");
        }

        sr.pOutputLinesAbove = new StatOutputLines();
        sr.pOutputLinesAbove.addLine("Name des Vereins:|" + Daten.project.getClubName(), 2, StatOutputLines.FONT_BOLD);
        sr.pOutputLinesAbove.addLine("Ort:|" + Daten.project.getClubAddressCity(), 2, StatOutputLines.FONT_BOLD);
        sr.pOutputLinesAbove.addLine("DRV Mitgliedsnummer:|" + Daten.project.getClubGlobalAssociationMemberNo(), 2, StatOutputLines.FONT_BOLD);
        sr.pOutputLinesAbove.addLine("LRV:|" + Daten.project.getClubRegionalAssociationName(), 2, StatOutputLines.FONT_BOLD);
        String mitgl = "";
        if (Daten.project.getClubMemberOfDRV()) {
            mitgl = "DRV";
        }
        if (Daten.project.getClubMemberOfSRV()) {
            mitgl += (mitgl.length() > 0 ? ", " : "") + "SRV";
        }
        if (Daten.project.getClubMemberOfADH()) {
            mitgl += (mitgl.length() > 0 ? ", " : "") + "ADH";
        }
        sr.pOutputLinesAbove.addLine("Mitglied im:|" + mitgl, 2, StatOutputLines.FONT_BOLD);

        sr.pOutputLinesBelow = new StatOutputLines();
        sr.pOutputLinesBelow.addLine("Anschrift des Wanderruderwartes bzw. des Ausfüllers: ", 1, StatOutputLines.FONT_BOLD);
        sr.pOutputLinesBelow.addLine(Daten.project.getClubName(), 1, StatOutputLines.FONT_NORMAL);
        sr.pOutputLinesBelow.addLine("c/o " + Daten.project.getAdminName(), 1, StatOutputLines.FONT_NORMAL);
        sr.pOutputLinesBelow.addLine(Daten.project.getClubAddressStreet(), 1, StatOutputLines.FONT_NORMAL);
        sr.pOutputLinesBelow.addLine(Daten.project.getClubAddressCity(), 1, StatOutputLines.FONT_NORMAL);
        sr.pOutputLinesBelow.addLine("", 1, StatOutputLines.FONT_NORMAL);
        sr.pOutputLinesBelow.addLine("Unterschrift, Vereinsstempel: ", 1, StatOutputLines.FONT_BOLD);

        sr.pAdditionalTable1Title = new String[14];
        sr.pAdditionalTable1Title[ 0] = "Start + Ziel der Fahrt";
        sr.pAdditionalTable1Title[ 1] = "Gewässer";
        sr.pAdditionalTable1Title[ 2] = "Gesamt Km";
        sr.pAdditionalTable1Title[ 3] = "Gesamt Tage";
        sr.pAdditionalTable1Title[ 4] = "Anz. Teilnehmer";
        sr.pAdditionalTable1Title[ 5] = "Mannsch-Km";
        sr.pAdditionalTable1Title[ 6] = "Männer (Anz)";
        sr.pAdditionalTable1Title[ 7] = "Männer (Km)";
        sr.pAdditionalTable1Title[ 8] = "Junioren (Anz)";
        sr.pAdditionalTable1Title[ 9] = "Junioren (Km)";
        sr.pAdditionalTable1Title[10] = "Frauen (Anz)";
        sr.pAdditionalTable1Title[11] = "Frauen (Km)";
        sr.pAdditionalTable1Title[12] = "Juniorinnen (Anz)";
        sr.pAdditionalTable1Title[13] = "Juniorinnen (Km)";

        // lösche Mehrtagesfahrten, die gar keine sind
        int anzMtours = 0;
        for (int i = 0; i < sd.length; i++) {
            if (sd[i] != null && sd[i].compData != null) {
                long totalMeters = getSumOfAllHashEntries(sd[i].compData.etappen);
                if ((sd[i].compData.activeDays <= 1 && totalMeters < 30*1000) || // mind. 30 Km
                        (sd[i].compData.activeDays > 1 && totalMeters < 40*1000)) { // mind. 40 Km
                    sd[i] = null;
                } else {
                    anzMtours++;
                }
            } else {
                sd[i] = null;
            }
        }

        final int SPALTENTITEL_UNTEN_AB_EINTRAEGEN = 15;
        sr.pAdditionalTable1 = new String[anzMtours + 2 + (anzMtours > SPALTENTITEL_UNTEN_AB_EINTRAEGEN ? 1 : 0)][14];
        sr.pAdditionalTable1FirstRowBold = true;
        sr.pAdditionalTable1[0] = sr.pAdditionalTable1Title;
        if (anzMtours > SPALTENTITEL_UNTEN_AB_EINTRAEGEN) { // Tabellentitel unten wiederholen
            sr.pAdditionalTable1[sr.pAdditionalTable1.length - 1] = sr.pAdditionalTable1Title;
            sr.pAdditionalTable1LastRowBold = true;
        }

        long _gesMeters = 0, _gesCrewMeters = 0;
        int _gesTage = 0, 
                _gesTeilnMueber18 = 0, _gesTeilnMbis18 = 0, _gesTeilnFueber18 = 0, _gesTeilnFbis18 = 0;
        long _gesMeterTeilnMueber18 = 0, _gesMeterTeilnMbis18 = 0, _gesMeterTeilnFueber18 = 0, _gesMeterTeilnFbis18 = 0;
        Hashtable<String,String> _gesWaters = new Hashtable<String,String>();
        int pos = 0; // Position in sr.pAdditionalTable1
        int nichtGewerteteEintraege = 0;
        for (int i = 0; i < sd.length; i++) {
            if (sd[i] == null) {
                continue; // Wafa gelöscht, da sie die Kriterien nicht erfüllte!
            }
            pos++;
            long tmp;
            sr.pAdditionalTable1[pos][ 0] = sd[i].sName +
                    (sd[i].sAdditional != null && sd[i].sAdditional.length() > 0 ? " (" + sd[i].sAdditional + ")": "");

            if (sd[i].compData != null) {
                boolean wirdGewertet = true;

                if (sr.pAdditionalTable1[pos][0].trim().length() == 0) {
                    wirdGewertet = false;
                    sr.pAdditionalTable1[pos][0] = "KEIN ZIELNAME (WIRD NICHT ANERKANNT)";
                }

                sr.pAdditionalTable1[pos][ 1] = printWatersHash(sd[i].compData.waters);
                _gesWaters.putAll(sd[i].compData.waters);

                long tmpTotalMeters = getSumOfAllHashEntries(sd[i].compData.etappen);
                _gesMeters += tmpTotalMeters;
                sr.pAdditionalTable1[pos][ 2] = DataTypeDistance.getDistanceFromMeters(tmpTotalMeters).getStringValueInKilometers();

                _gesTage += sd[i].compData.activeDays;
                sr.pAdditionalTable1[pos][ 3] = Integer.toString(sd[i].compData.activeDays);

                int tmpAnzTeiln = sd[i].compData.teilnFbis18.size() + sd[i].compData.teilnFueber18.size()
                        + sd[i].compData.teilnMbis18.size() + sd[i].compData.teilnMueber18.size();
                sr.pAdditionalTable1[pos][ 4] = Integer.toString(tmpAnzTeiln);

                _gesCrewMeters += sd[i].compData.totalDistanceInMeters;
                sr.pAdditionalTable1[pos][ 5] = DataTypeDistance.getDistanceFromMeters(sd[i].compData.totalDistanceInMeters).getStringValueInKilometers();

                // Plausi-Test
                if (tmpTotalMeters * tmpAnzTeiln < sd[i].compData.totalDistanceInMeters) {
                    sr.cWarnings.put("Die berechneten Gesamt- und Mannschaftskilometer für die Mehrtagesfahrt '" + sd[i].sName + "'\n"
                            + "sind unstimmig. Bitte überprüfe, ob alle Einträge zu dieser Fahrt korrekt sind, insb. ob\n"
                            + "alle Etappennamen unterschiedlich sind und kein Ruderer auf einer Etappe mehrfach vorkommt!\n", "foo");
                    sr.pAdditionalTable1[pos][ 0] += " (UNSTIMMIG - WIRD NICHT ANERKANNT)";
                    wirdGewertet = false;
                }

                _gesTeilnMueber18 += sd[i].compData.teilnMueber18.size();
                sr.pAdditionalTable1[pos][ 6] = Integer.toString(sd[i].compData.teilnMueber18.size());

                tmp = getSumOfAllHashEntries(sd[i].compData.teilnMueber18);
                _gesMeterTeilnMueber18 += tmp;
                sr.pAdditionalTable1[pos][ 7] = DataTypeDistance.getDistanceFromMeters(tmp).getStringValueInKilometers();

                _gesTeilnMbis18 += sd[i].compData.teilnMbis18.size();
                sr.pAdditionalTable1[pos][ 8] = Integer.toString(sd[i].compData.teilnMbis18.size());

                tmp = getSumOfAllHashEntries(sd[i].compData.teilnMbis18);
                _gesMeterTeilnMbis18 += tmp;
                sr.pAdditionalTable1[pos][ 9] = DataTypeDistance.getDistanceFromMeters(tmp).getStringValueInKilometers();

                _gesTeilnFueber18 += sd[i].compData.teilnFueber18.size();
                sr.pAdditionalTable1[pos][10] = Integer.toString(sd[i].compData.teilnFueber18.size());

                tmp = getSumOfAllHashEntries(sd[i].compData.teilnFueber18);
                _gesMeterTeilnFueber18 += tmp;
                sr.pAdditionalTable1[pos][11] = DataTypeDistance.getDistanceFromMeters(tmp).getStringValueInKilometers();

                _gesTeilnFbis18 += sd[i].compData.teilnFbis18.size();
                sr.pAdditionalTable1[pos][12] = Integer.toString(sd[i].compData.teilnFbis18.size());

                tmp = getSumOfAllHashEntries(sd[i].compData.teilnFbis18);
                _gesMeterTeilnFbis18 += tmp;
                sr.pAdditionalTable1[pos][13] = DataTypeDistance.getDistanceFromMeters(tmp).getStringValueInKilometers();

                if (sr.getOutputTypeEnum() == StatisticsRecord.OutputTypes.efawett && wirdGewertet) {
                    EfaWettMeldung ewm = new EfaWettMeldung();
                    ewm.drvWS_StartZiel = sr.pAdditionalTable1[pos][0];
                    ewm.drvWS_Gewaesser = sr.pAdditionalTable1[pos][1];
                    ewm.drvWS_Km = sr.pAdditionalTable1[pos][2];
                    ewm.drvWS_Tage = sr.pAdditionalTable1[pos][3];
                    ewm.drvWS_Teilnehmer = sr.pAdditionalTable1[pos][4];
                    ewm.drvWS_MannschKm = sr.pAdditionalTable1[pos][5];
                    ewm.drvWS_MaennerAnz = sr.pAdditionalTable1[pos][6];
                    ewm.drvWS_MaennerKm = sr.pAdditionalTable1[pos][7];
                    ewm.drvWS_JuniorenAnz = sr.pAdditionalTable1[pos][8];
                    ewm.drvWS_JuniorenKm = sr.pAdditionalTable1[pos][9];
                    ewm.drvWS_FrauenAnz = sr.pAdditionalTable1[pos][10];
                    ewm.drvWS_FrauenKm = sr.pAdditionalTable1[pos][11];
                    ewm.drvWS_JuniorinnenAnz = sr.pAdditionalTable1[pos][12];
                    ewm.drvWS_JuniorinnenKm = sr.pAdditionalTable1[pos][13];
                    if (efaWett.meldung == null) {
                        efaWett.meldung = ewm;
                    } else {
                        efaWett.letzteMeldung().next = ewm;
                    }
                }

                if (!wirdGewertet) {
                    nichtGewerteteEintraege++;
                }

            }
        }

        if (nichtGewerteteEintraege > 0) {
            sr.cWarnings.put(nichtGewerteteEintraege + " Fahrten enthalten ungültige Eintragungen "
                    + "und können daher nicht gewertet werden.", "foo");
        }

        Object[] ga = _gesWaters.keySet().toArray();
        Arrays.sort(ga);
        String tmp = "";
        for (int i = 0; i < ga.length; i++) {
            tmp += (i > 0 ? ", " : "") + ga[i];
        }// DataTypeDistance.getDistanceFromMeters(tmp).getStringValueInKilometers()
        sr.pAdditionalTable1[anzMtours + 1][ 0] = "--- Zusammenfassung ---";
        sr.pAdditionalTable1[anzMtours + 1][ 1] = tmp;
        sr.pAdditionalTable1[anzMtours + 1][ 2] = DataTypeDistance.getDistanceFromMeters(_gesMeters).getStringValueInKilometers();
        sr.pAdditionalTable1[anzMtours + 1][ 3] = Integer.toString(_gesTage);
        sr.pAdditionalTable1[anzMtours + 1][ 4] = Integer.toString(_gesTeilnMueber18 + _gesTeilnMbis18 + _gesTeilnFueber18 + _gesTeilnFbis18);
        sr.pAdditionalTable1[anzMtours + 1][ 5] = DataTypeDistance.getDistanceFromMeters(_gesCrewMeters).getStringValueInKilometers();
        sr.pAdditionalTable1[anzMtours + 1][ 6] = Integer.toString(_gesTeilnMueber18);
        sr.pAdditionalTable1[anzMtours + 1][ 7] = DataTypeDistance.getDistanceFromMeters(_gesMeterTeilnMueber18).getStringValueInKilometers();
        sr.pAdditionalTable1[anzMtours + 1][ 8] = Integer.toString(_gesTeilnMbis18);
        sr.pAdditionalTable1[anzMtours + 1][ 9] = DataTypeDistance.getDistanceFromMeters(_gesMeterTeilnMbis18).getStringValueInKilometers();
        sr.pAdditionalTable1[anzMtours + 1][10] = Integer.toString(_gesTeilnFueber18);
        sr.pAdditionalTable1[anzMtours + 1][11] = DataTypeDistance.getDistanceFromMeters(_gesMeterTeilnFueber18).getStringValueInKilometers();
        sr.pAdditionalTable1[anzMtours + 1][12] = Integer.toString(_gesTeilnFbis18);
        sr.pAdditionalTable1[anzMtours + 1][13] = DataTypeDistance.getDistanceFromMeters(_gesMeterTeilnFbis18).getStringValueInKilometers();

        // Anzahl der aktiven Mitglieder
        if (sr.getOutputTypeEnum() == StatisticsRecord.OutputTypes.efawett && alleAktive != null) {
            int aktMab19 = 0;
            int aktMbis18 = 0;
            int aktWab19 = 0;
            int aktWbis18 = 0;
            Object[] aktive = alleAktive.keySet().toArray();
            for (int i = 0; i < aktive.length; i++) {
                String s = (String) alleAktive.get(aktive[i]);
                if (s != null) {
                    if (s.equals(AKTIV_M_AB19)) {
                        aktMab19++;
                    }
                    if (s.equals(AKTIV_M_BIS18)) {
                        aktMbis18++;
                    }
                    if (s.equals(AKTIV_W_AB19)) {
                        aktWab19++;
                    }
                    if (s.equals(AKTIV_W_BIS18)) {
                        aktWbis18++;
                    }
                }
            }
            efaWett.aktive_M_ab19 = Integer.toString(aktMab19);
            efaWett.aktive_M_bis18 = Integer.toString(aktMbis18);
            efaWett.aktive_W_ab19 = Integer.toString(aktWab19);
            efaWett.aktive_W_bis18 = Integer.toString(aktWbis18);
        }
    }

}