/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.importefa1;

import de.nmichael.efa.Daten;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.efa1.*;
import de.nmichael.efa.util.*;
import java.util.*;

public class ImportDestinations extends ImportBase {

    private Ziele ziele;
    private ProjectRecord logbookRec;

    public ImportDestinations(ImportTask task, Ziele ziele, ProjectRecord logbookRec) {
        super(task);
        this.ziele = ziele;
        this.logbookRec = logbookRec;
    }

    public String getDescription() {
        return International.getString("Ziele");
    }

    private boolean isIdentical(Object o, String s) {
        if (o == null && (s == null || s.length() == 0)) {
            return true;
        }
        if (o == null || s == null) {
            return false;
        }
        return (o.toString().equals(s));
    }

    private boolean isChanged(DestinationRecord r, DatenFelder d) {
        if (!isIdentical(r.getName(), d.get(Ziele.NAME))) {
            return true;
        }
        if (!isIdentical(Long.toString(r.getDistance().getValueInMeters()), Integer.toString(EfaUtil.zehntelString2Int(d.get(Ziele.KM))*100))) {
            return true;
        }
        if (!isIdentical(Integer.toString(r.getDestinationArea()), Integer.toString(EfaUtil.string2int(d.get(Ziele.BEREICH), IDataAccess.UNDEFINED_INT)))) {
            return true;
        }
        if (!isIdentical(Boolean.toString(r.getStartIsBoathouse()), Boolean.toString(d.get(Ziele.STEGZIEL).equals("+")))) {
            return true;
        }
        // Well, we skip the comparison for Waters. Actually, we should check this, but I currently just don't feel like implementing this ;-)
        return false;
    }

    public boolean runImport() {
        try {
            logInfo(International.getMessage("Importiere {list} aus {file} ...", getDescription(), ziele.getFileName()));

            Destinations destinations = Daten.project.getDestinations(true);
            Waters waters = Daten.project.getWaters(true);
            long validFrom = logbookRec.getStartDate().getTimestamp(null);

            DatenFelder d = ziele.getCompleteFirst();
            String[] IDXD = new String[] { DestinationRecord.NAME };
            String[] IDXW = new String[] { WatersRecord.NAME };
            while (d != null) {
                // First search, whether we have imported this destination already
                DestinationRecord r = null;
                DataKey[] keys = destinations.data().getByFields(IDXD,
                        new String[] { d.get(Boote.NAME) });
                if (keys != null && keys.length > 0) {
                    // We've found one or more destinations with same Name.
                    // Since we're importing data from efa1, these destinations are all identical, i.e. have the same ID.
                    // Therefore their key is identical, so we can just retrieve one destination record with keys[0], which
                    // is valid for this logbook.
                    r = (DestinationRecord)destinations.data().getValidAt(keys[0], validFrom);
                }

                if (r == null || isChanged(r, d)) {
                    r = destinations.createDestinationRecord((r != null ? r.getId() : UUID.randomUUID()));
                    r.setName(d.get(Ziele.NAME));
                    r.setDistance(DataTypeDistance.parseDistance(d.get(Ziele.KM) + DataTypeDistance.KILOMETERS));
                    if (d.get(Ziele.BEREICH).length() > 0) {
                        r.setDestinationArea(EfaUtil.string2int(d.get(Ziele.BEREICH), 0));
                    }
                    if (d.get(Ziele.STEGZIEL).equals("+")) {
                        r.setStartIsBoathouse(true);
                        r.setRoundtrip(true); // we only assume destinations where "Start und Ziel ist Bootshaus" to be roundtrips (we don't have this info in efa1, so we just guess)
                    }                    
                    if (d.get(Ziele.GEWAESSER).length() > 0) {
                        String[] a = EfaUtil.kommaList2Arr(d.get(Ziele.GEWAESSER),',');
                        DataTypeList<UUID> watersList = new DataTypeList<UUID>();
                        for (int i=0; i<a.length; i++) {
                            String name = a[i].trim();
                            if (name.length() == 0) {
                                continue;
                            }
                            WatersRecord w = null;
                            DataKey[] wkeys = waters.data().getByFields(IDXW,
                                              new String[] { name });
                            if (wkeys != null && wkeys.length > 0) {
                                w = (WatersRecord)waters.data().get(wkeys[0]);
                            }
                            if (w == null || !name.equals(w.getName())) {
                                w = waters.createWatersRecord((w != null ? w.getId() : UUID.randomUUID()));
                                w.setName(name);
                                waters.data().add(w);
                                logDetail(International.getMessage("Importiere Eintrag: {entry}", w.toString()));
                            }
                            watersList.add(w.getId());
                        }
                        if (watersList.length() > 0) {
                            r.setWatersIdList(watersList);
                        }
                    }
                    try {
                        destinations.data().addValidAt(r, validFrom);
                        logDetail(International.getMessage("Importiere Eintrag: {entry}", r.toString()));
                    } catch(Exception e) {
                        logError(International.getMessage("Import von Eintrag fehlgeschlagen: {entry} ({error})", r.toString(), e.toString()));
                    }
                } else {
                    logDetail(International.getMessage("Identischer Eintrag: {entry}", r.toString()));
                }
                d = ziele.getCompleteNext();
            }
        } catch(Exception e) {
            logError(International.getMessage("Import von {list} aus {file} ist fehlgeschlagen.", getDescription(), ziele.getFileName()));
            logError(e.toString());
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
