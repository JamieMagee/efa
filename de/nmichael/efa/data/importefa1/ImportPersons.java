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
import de.nmichael.efa.core.config.EfaTypes;
import java.util.*;

public class ImportPersons extends ImportBase {

    private Mitglieder mitglieder;
    private ProjectRecord logbookRec;
    private Hashtable<String,UUID> statusKeys = new Hashtable<String,UUID>();

    public ImportPersons(ImportTask task, Mitglieder mitglieder, ProjectRecord logbookRec) {
        super(task);
        this.mitglieder = mitglieder;
        this.logbookRec = logbookRec;
    }

    public String getDescription() {
        return International.getString("Personen");
    }

    private String getStatusKey(String name) {
        UUID id = statusKeys.get(name);
        return (id != null ? id.toString(): "");
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

    private boolean isChanged(PersonRecord r, DatenFelder d) {
        if (!isIdentical(r.getFirstName(), d.get(Mitglieder.VORNAME))) {
            return true;
        }
        if (!isIdentical(r.getLastName(), d.get(Mitglieder.NACHNAME))) {
            return true;
        }
        if (!isIdentical(r.getGender(), d.get(Mitglieder.GESCHLECHT))) {
            return true;
        }
        if (!isIdentical(r.getBirthday(), d.get(Mitglieder.JAHRGANG))) {
            return true;
        }
        if (!isIdentical(r.getNameAffix(), d.get(Mitglieder.VEREIN))) {
            return true;
        }
        if (!isIdentical(r.getStatusId(), getStatusKey(d.get(Mitglieder.STATUS)))) {
            return true;
        }
        if (!isIdentical(r.getMembershipNo(), d.get(Mitglieder.MITGLNR))) {
            return true;
        }
        if (!isIdentical(r.getPassword(), d.get(Mitglieder.PASSWORT))) {
            return true;
        }
        if (!isIdentical(r.getDisability(), Boolean.toString(d.get(Mitglieder.BEHINDERUNG).equals("+")) )) {
            return true;
        }
        if (!isIdentical(r.getExcludeFromCompetition(), Boolean.toString(!d.get(Mitglieder.KMWETT_MELDEN).equals("+")))) {
            return true;
        }
        if (!isIdentical(r.getInputShortcut(), d.get(Mitglieder.ALIAS))) {
            return true;
        }
        if (!isIdentical(r.getFreeUse1(), d.get(Mitglieder.FREI1))) {
            return true;
        }
        if (!isIdentical(r.getFreeUse2(), d.get(Mitglieder.FREI2))) {
            return true;
        }
        if (!isIdentical(r.getFreeUse3(), d.get(Mitglieder.FREI3))) {
            return true;
        }
        return false;
    }

    public boolean runImport() {
        try {
            logInfo(International.getMessage("Importiere {list} aus {file} ...", getDescription(), mitglieder.getFileName()));

            Persons persons = Daten.project.getPersons(true);
            Status status = Daten.project.getStatus(true);
            long validFrom = logbookRec.getStartDate().getTimestamp(null);

            Hashtable<UUID,String> importedPersons = new Hashtable<UUID,String>();
            DatenFelder d = mitglieder.getCompleteFirst();
            while (d != null) {
                // First search, whether we have imported this person already
                PersonRecord r = null;
                DataKey[] keys = persons.data().getByFields(PersonRecord.IDX_NAME_NAMEAFFIX,
                        persons.staticPersonRecord.getQualifiedNameValues(
                            PersonRecord.getFullName(d.get(Mitglieder.VORNAME), d.get(Mitglieder.NACHNAME), d.get(Mitglieder.VEREIN), true)));
                if (keys != null && keys.length > 0) {
                    // We've found one or more persons with same Name and Association.
                    // Since we're importing data from efa1, these persons are all identical, i.e. have the same ID.
                    // Therefore their key is identical, so we can just retrieve one person record with keys[0], which
                    // is valid for this logbook.
                    r = (PersonRecord)persons.data().getValidAt(keys[0], validFrom);
                }

                if (r == null || isChanged(r, d)) {
                    r = persons.createPersonRecord((r != null ? r.getId() : UUID.randomUUID()));

                    if (d.get(Mitglieder.VORNAME).length() > 0) {
                        r.setFirstName(d.get(Mitglieder.VORNAME));
                    }
                    if (d.get(Mitglieder.NACHNAME).length() > 0) {
                        r.setLastName(d.get(Mitglieder.NACHNAME));
                    }
                    // TITLE does not exist in efa1, so we leave it empty
                    if (d.get(Mitglieder.GESCHLECHT).length() > 0) {
                        String gender = d.get(Mitglieder.GESCHLECHT);
                        if (gender.equals(EfaTypes.TYPE_GENDER_MALE) ||
                            gender.equals(EfaTypes.TYPE_GENDER_FEMALE)) {
                            r.setGender(gender);
                        }
                    }
                    if (d.get(Mitglieder.JAHRGANG).length() > 0) {
                        DataTypeDate birthday = new DataTypeDate();
                        birthday.setYear(d.get(Mitglieder.JAHRGANG));
                        r.setBirthday(birthday);
                    }
                    if (d.get(Mitglieder.VEREIN).length() > 0) {
                        r.setNameAffix(d.get(Mitglieder.VEREIN));
                        r.setAssocitation(d.get(Mitglieder.VEREIN));
                    }
                    // always set status
                        String s = d.get(Mitglieder.STATUS).trim();
                        if (s.length() == 0) {
                            s = Daten.efaTypes.getValue(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_OTHER);
                        }
                        UUID statusId = statusKeys.get(s);
                        if (statusId == null && s.equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_GUEST))) {
                            statusId = status.getStatusGuest().getId();
                        }
                        if (statusId == null && s.equals(Daten.efaTypes.getValue(EfaTypes.CATEGORY_STATUS, EfaTypes.TYPE_STATUS_OTHER))) {
                            statusId = status.getStatusOther().getId();
                        }
                        if (statusId == null) {
                            statusId = (status.findStatusByName(s) != null ? status.findStatusByName(s).getId() : null);
                        }
                        if (statusId == null) {
                            statusId = status.addStatus(s, StatusRecord.TYPE_USER);
                        }
                        if (statusId != null) {
                            r.setStatusId(statusId);
                            statusKeys.put(s, statusId);
                        }
                    
                    String address = task.getAddress(r.getFirstName() + " " + r.getLastName());
                    if (address != null && address.length() > 0) {
                        r.setAddressAdditional(address); // there is no such thing as an address format in efa1,
                                                         // so we just put the data into the additional address field.
                    }
                    if (d.get(Mitglieder.MITGLNR).length() > 0) {
                        r.setMembershipNo(d.get(Mitglieder.MITGLNR));
                    }
                    if (d.get(Mitglieder.PASSWORT).length() > 0) {
                        r.setPassword(d.get(Mitglieder.PASSWORT));
                    }
                    // EXTERNALID does not exist in efa1, so we leave it empty
                    if (d.get(Mitglieder.BEHINDERUNG).equals("+")) {
                        r.setDisability(true);
                    }
                    if (!d.get(Mitglieder.KMWETT_MELDEN).equals("+")) {
                        r.setExcludeFromCompetition(true);
                    }
                    if (d.get(Mitglieder.ALIAS).length() > 0) {
                        r.setInputShortcut(d.get(Mitglieder.ALIAS));
                    }
                    if (d.get(Mitglieder.FREI1).length() > 0) {
                        r.setFreeUse1(d.get(Mitglieder.FREI1));
                    }
                    if (d.get(Mitglieder.FREI2).length() > 0) {
                        r.setFreeUse1(d.get(Mitglieder.FREI2));
                    }
                    if (d.get(Mitglieder.FREI3).length() > 0) {
                        r.setFreeUse1(d.get(Mitglieder.FREI3));
                    }
                    try {
                        persons.data().addValidAt(r, validFrom);
                        logDetail(International.getMessage("Importiere Eintrag: {entry}", r.toString()));
                    } catch(Exception e) {
                        logError(International.getMessage("Import von Eintrag fehlgeschlagen: {entry} ({error})", r.toString(), e.toString()));
                        Logger.logdebug(e);
                    }
                } else {
                    logDetail(International.getMessage("Identischer Eintrag: {entry}", r.toString()));
                }
                importedPersons.put(r.getId(), r.getQualifiedName());
                d = mitglieder.getCompleteNext();
            }

            // mark all persons that have *not* been imported with this run, but still have a valid version, as deleted
            DataKeyIterator it = persons.data().getStaticIterator();
            DataKey key = it.getFirst();
            while (key != null) {
                PersonRecord pr = persons.getPerson((UUID)key.getKeyPart1(), validFrom);
                if (pr != null && importedPersons.get(pr.getId()) == null) {
                    try {
                        persons.data().changeValidity(pr, pr.getValidFrom(), validFrom);
                    } catch(Exception e) {
                        logError(International.getMessage("Gültigkeit ändern von Eintrag fehlgeschlagen: {entry} ({error})", pr.toString(), e.toString()));
                        Logger.logdebug(e);
                    }
                }
                key = it.getNext();
            }
        } catch(Exception e) {
            logError(International.getMessage("Import von {list} aus {file} ist fehlgeschlagen.", getDescription(), mitglieder.getFileName()));
            logError(e.toString());
            Logger.logdebug(e);
            return false;
        }
        return true;
    }

}