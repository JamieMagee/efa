/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data;

import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.*;
import java.util.*;

// @i18n complete

public class BoatDamageRecord extends DataRecord {

    public static final String SEVERITY_FULLYUSEABLE   = "FULLYUSEABLE";
    public static final String SEVERITY_LIMITEDUSEABLE = "LIMITEDUSEABLE";
    public static final String SEVERITY_NOTUSEABLE     = "NOTUSEABLE";

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String BOATID               = "BoatId";
    public static final String DAMAGE               = "Damage";
    public static final String DESCRIPTION          = "Description";
    public static final String SEVERITY             = "Severity";
    public static final String FIXED                = "Fixed";
    public static final String REPORTDATE           = "ReportDate";
    public static final String REPORTTIME           = "ReportTime";
    public static final String FIXDATE              = "FixDate";
    public static final String FIXTIME              = "FixTime";
    public static final String REPORTEDBYPERSONID   = "ReportedByPersonId";
    public static final String REPORTEDBYPERSONNAME = "ReportedByPersonName";
    public static final String FIXEDBYPERSONID      = "FixedByPersonId";
    public static final String FIXEDBYPERSONNAME    = "FixedByPersonName";
    public static final String NOTES                = "Notes";

    public static final String[] IDX_BOATID = new String[] { BOATID };

    public static final String GUIITEM_REPORTDATETIME = "GUIITEM_REPORTDATETIME";
    public static final String GUIITEM_FIXDATETIME    = "GUIITEM_FIXDATETIME";

    private boolean showOnlyAddDamageFields = false;

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(BOATID);                   t.add(IDataAccess.DATA_UUID);
        f.add(DAMAGE);                   t.add(IDataAccess.DATA_INTEGER);
        f.add(DESCRIPTION);              t.add(IDataAccess.DATA_STRING);
        f.add(SEVERITY);                 t.add(IDataAccess.DATA_STRING);
        f.add(FIXED);                    t.add(IDataAccess.DATA_BOOLEAN);
        f.add(REPORTDATE);               t.add(IDataAccess.DATA_DATE);
        f.add(REPORTTIME);               t.add(IDataAccess.DATA_TIME);
        f.add(FIXDATE);                  t.add(IDataAccess.DATA_DATE);
        f.add(FIXTIME);                  t.add(IDataAccess.DATA_TIME);
        f.add(REPORTEDBYPERSONID);       t.add(IDataAccess.DATA_UUID);
        f.add(REPORTEDBYPERSONNAME);     t.add(IDataAccess.DATA_STRING);
        f.add(FIXEDBYPERSONID);          t.add(IDataAccess.DATA_UUID);
        f.add(FIXEDBYPERSONNAME);        t.add(IDataAccess.DATA_STRING);
        f.add(NOTES);                    t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(BoatDamages.DATATYPE, f, t, false);
        metaData.setKey(new String[] { BOATID, DAMAGE });
        metaData.addIndex(IDX_BOATID);
    }

    public BoatDamageRecord(BoatDamages boatDamage, MetaData metaData) {
        super(boatDamage, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,Integer,String>(getBoatId(),getDamage(),null);
    }

    public boolean getDeleted() {
        return getPersistence().getProject().getBoats(false).isBoatDeleted(getBoatId());
    }

    public void setBoatId(UUID id) {
        setUUID(BOATID, id);
    }
    public UUID getBoatId() {
        return getUUID(BOATID);
    }

    public String getBoatAsName() {
        try {
            Boats boats = getPersistence().getProject().getBoats(false);
            return boats.getBoat(getBoatId(), getReportDate().getTimestamp(getReportTime())).getQualifiedName();
        } catch (Exception e) {
            Logger.logdebug(e);
            return null;
        }
    }
    
    public void setDamage(int no) {
        setInt(DAMAGE, no);
    }
    public int getDamage() {
        return getInt(DAMAGE);
    }

    public void setDescription(String description) {
        setString(DESCRIPTION, description);
    }
    public String getDescription() {
        return getString(DESCRIPTION);
    }

    public void setSeverity(String severity) {
        setString(SEVERITY, severity);
    }
    public String getSeverity() {
        return getString(SEVERITY);
    }
    public String getSeverityDescription() {
        String s = getSeverity();
        if (s != null && s.equals(SEVERITY_FULLYUSEABLE)) {
            return International.getString("Boot voll benutzbar");
        }
        if (s != null && s.equals(SEVERITY_LIMITEDUSEABLE)) {
            return International.getString("Boot eingeschränkt benutzbar");
        }
        if (s != null && s.equals(SEVERITY_NOTUSEABLE)) {
            return International.getString("Boot nicht benutzbar");
        }
        return International.getString("unbekannt");
    }

    public void setFixed(boolean isFixed) {
        setBool(FIXED, isFixed);
    }
    public boolean getFixed() {
        return getBool(FIXED);
    }

    public int getPriority() {
        if (getFixed()) {
            return 9;
        }
        String severity = getSeverity();
        if (severity != null && severity.equals(SEVERITY_NOTUSEABLE)) {
            return 1;
        }
        if (severity != null && severity.equals(SEVERITY_LIMITEDUSEABLE)) {
            return 2;
        }
        if (severity != null && severity.equals(SEVERITY_FULLYUSEABLE)) {
            return 3;
        }
        return 5;
    }

    public void setReportDate(DataTypeDate date) {
        setDate(REPORTDATE, date);
    }
    public DataTypeDate getReportDate() {
        return getDate(REPORTDATE);
    }

    public void setReportTime(DataTypeTime time) {
        setTime(REPORTTIME, time);
    }
    public DataTypeTime getReportTime() {
        return getTime(REPORTTIME);
    }

    public void setFixDate(DataTypeDate date) {
        setDate(FIXDATE, date);
    }
    public DataTypeDate getFixDate() {
        return getDate(FIXDATE);
    }

    public void setFixTime(DataTypeTime time) {
        setTime(FIXTIME, time);
    }
    public DataTypeTime getFixTime() {
        return getTime(FIXTIME);
    }

    public void setReportedByPersonId(UUID id) {
        setUUID(REPORTEDBYPERSONID, id);
    }
    public UUID getReportedByPersonId() {
        return getUUID(REPORTEDBYPERSONID);
    }

    public void setReportedByPersonName(String name) {
        setString(REPORTEDBYPERSONNAME, name);
    }
    public String getReportedByPersonName() {
        return getString(REPORTEDBYPERSONNAME);
    }

    public String getReportedByPersonAsName() {
        UUID id = getReportedByPersonId();
        if (id != null) {
            try {
                Persons persons = getPersistence().getProject().getPersons(false);
                return persons.getPerson(id, getReportDate().getTimestamp(getReportTime())).getQualifiedName();
            } catch(Exception e) {
                Logger.logdebug(e);
                return null;
            }
        } else {
            return getReportedByPersonName();
        }
    }

    public void setFixedByPersonId(UUID id) {
        setUUID(FIXEDBYPERSONID, id);
    }
    public UUID getFixedByPersonId() {
        return getUUID(FIXEDBYPERSONID);
    }

    public void setFixedByPersonName(String name) {
        setString(FIXEDBYPERSONNAME, name);
    }
    public String getFixedByPersonName() {
        return getString(FIXEDBYPERSONNAME);
    }

    public String getFixedByPersonAsName() {
        UUID id = getFixedByPersonId();
        if (id != null) {
            try {
                Persons persons = getPersistence().getProject().getPersons(false);
                return persons.getPerson(id, getFixDate().getTimestamp(getFixTime())).getQualifiedName();
            } catch(Exception e) {
                Logger.logdebug(e);
                return null;
            }
        } else {
            return getFixedByPersonName();
        }
    }

    public void setNotes(String reason) {
        setString(NOTES, reason);
    }
    public String getNotes() {
        return getString(NOTES);
    }

    public String getCompleteDamageInfo() {
        StringBuffer s = new StringBuffer();
        s.append(International.getMessage("Bootsschaden für {boat}", getBoatAsName()) + "\n==============================================\n");
        s.append(International.getString("Beschreibung") + ": " + getDescription() + "\n");
        s.append(International.getString("Schwere des Schadens") + ": " + getSeverityDescription() + "\n");
        s.append(International.getString("gemeldet am") + ": " + DataTypeDate.getDateTimeString(getReportDate(), getReportTime()) + "\n");
        s.append(International.getString("gemeldet von") + ": " + getReportedByPersonAsName() + "\n");
        return s.toString();
    }

    public String getAsText(String fieldName) {
        if (fieldName.equals(BOATID)) {
            return getBoatAsName();
        }
        if (fieldName.equals(REPORTEDBYPERSONID)) {
            if (get(REPORTEDBYPERSONID) != null) {
                return this.getReportedByPersonAsName();
            } else {
                return null;
            }
        }
        if (fieldName.equals(FIXEDBYPERSONID)) {
            if (get(FIXEDBYPERSONID) != null) {
                return this.getFixedByPersonAsName();
            } else {
                return null;
            }
        }
        return super.getAsText(fieldName);
    }

    public void setFromText(String fieldName, String value) {
        if (fieldName.equals(BOATID)) {
            Boats boats = getPersistence().getProject().getBoats(false);
            BoatRecord br = boats.getBoat(value, -1);
            if (br != null) {
                set(fieldName, br.getId());
            }
            return;
        }
        if (fieldName.equals(REPORTEDBYPERSONID) ||
            fieldName.equals(FIXEDBYPERSONID)) {
            Persons persons = getPersistence().getProject().getPersons(false);
            PersonRecord pr = persons.getPerson(value, -1);
            if (pr != null) {
                set(fieldName, pr.getId());
            }
            return;
        }
        set(fieldName, value);
    }

    public Vector<IItemType> getGuiItems() {
        String CAT_BASEDATA     = "%01%" + International.getString("Bootsschaden");
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();
        v.add(item = new ItemTypeLabel("GUI_BOAT_NAME",
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getMessage("Bootsschaden für {boat}", getBoatAsName())));
        item.setPadding(0, 0, 0, 10);
        v.add(item = new ItemTypeString(BoatDamageRecord.DESCRIPTION, getDescription(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Beschreibung")));
        v.add(item = new ItemTypeStringList(SEVERITY, getSeverity(),
                new String[] { SEVERITY_NOTUSEABLE, SEVERITY_LIMITEDUSEABLE, SEVERITY_FULLYUSEABLE },
                new String[] { International.getString("Boot nicht benutzbar"),
                               International.getString("Boot eingeschränkt benutzbar"),
                               International.getString("Boot voll benutzbar")
                },
                IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                International.getString("Schwere des Schadens")));
        v.add(item = new ItemTypeDateTime(GUIITEM_REPORTDATETIME, getReportDate(), getReportTime(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("gemeldet am")));
        if (showOnlyAddDamageFields) {
            item.setEnabled(false);
        }
        v.add(item = getGuiItemTypeStringAutoComplete(BoatDamageRecord.REPORTEDBYPERSONID, getReportedByPersonId(),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    getPersistence().getProject().getPersons(false), System.currentTimeMillis(), System.currentTimeMillis(),
                    International.getString("gemeldet von")));
        ((ItemTypeStringAutoComplete)item).setNotNull(true);
        ((ItemTypeStringAutoComplete)item).setAlternateFieldNameForPlainText(BoatDamageRecord.REPORTEDBYPERSONNAME);
        if (!showOnlyAddDamageFields) {
            v.add(item = new ItemTypeDateTime(GUIITEM_FIXDATETIME, getFixDate(), getFixTime(),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("behoben am")));
            v.add(item = getGuiItemTypeStringAutoComplete(BoatDamageRecord.FIXEDBYPERSONID, getFixedByPersonId(),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    getPersistence().getProject().getPersons(false), System.currentTimeMillis(), System.currentTimeMillis(),
                    International.getString("behoben von")));
            ((ItemTypeStringAutoComplete) item).setAlternateFieldNameForPlainText(BoatDamageRecord.FIXEDBYPERSONNAME);
            v.add(item = new ItemTypeString(BoatDamageRecord.NOTES, getNotes(),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Bemerkungen")));
            v.add(item = new ItemTypeBoolean(BoatDamageRecord.FIXED, getFixed(),
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Schaden wurde behoben")));
        }
        return v;
    }

    public void saveGuiItems(Vector<IItemType> items) {
        for (IItemType item : items) {
            if (item.getName().equals(GUIITEM_REPORTDATETIME)) {
                setReportDate(((ItemTypeDateTime)item).getDate());
                setReportTime(((ItemTypeDateTime)item).getTime());
            }
            if (item.getName().equals(GUIITEM_FIXDATETIME)) {
                setFixDate(((ItemTypeDateTime)item).getDate());
                setFixTime(((ItemTypeDateTime)item).getTime());
            }
        }
        super.saveGuiItems(items);
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[5];
        header[0] = new TableItemHeader(International.getString("Boot"));
        header[1] = new TableItemHeader(International.getString("Schaden"));
        header[2] = new TableItemHeader(International.getString("gemeldet am"));
        header[3] = new TableItemHeader(International.getString("behoben am"));
        header[4] = new TableItemHeader(International.getString("Priorität"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[5];
        items[0] = new TableItem(getBoatAsName());
        items[1] = new TableItem(getDescription());
        items[2] = new TableItem(DataTypeDate.getDateTimeString(getReportDate(), getReportTime()));
        items[3] = new TableItem(DataTypeDate.getDateTimeString(getFixDate(), getFixTime()));
        items[4] = new TableItem(Integer.toString(getPriority()));
        if (!getFixed()) {
            items[0].setMarked(true);
            items[1].setMarked(true);
            items[2].setMarked(true);
            items[3].setMarked(true);
            items[4].setMarked(true);
        }
        return items;
    }

    public String getQualifiedName() {
        return International.getMessage("Schaden für {boat}", getBoatAsName());
    }

    public void setShowOnlyAddDamageFields(boolean showOnlyAddDamageFields) {
        this.showOnlyAddDamageFields = showOnlyAddDamageFields;
    }

}