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

import de.nmichael.efa.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.core.config.*;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.gui.util.*;
import de.nmichael.efa.util.*;
import java.util.*;
import java.util.regex.*;

// @i18n complete

public class PersonRecord extends DataRecord {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String EFBID                 = "EfbId";
    public static final String FIRSTNAME           = "FirstName";
    public static final String LASTNAME            = "LastName";
    public static final String FIRSTLASTNAME       = "FirstLastName";
    public static final String NAMEAFFIX           = "NameAffix";
    public static final String TITLE               = "Title";
    public static final String GENDER              = "Gender";
    public static final String BIRTHDAY            = "Birthday";
    public static final String ASSOCIATION         = "Association";
    public static final String STATUSID            = "StatusId";
    public static final String ADDRESSSTREET       = "AddressStreet";
    public static final String ADDRESSADDITIONAL   = "AddressAdditional";
    public static final String ADDRESSCITY         = "AddressCity";
    public static final String ADDRESSZIP          = "AddressZip";
    public static final String ADDRESSCOUNTRY      = "AddressCountry";
    public static final String MEMBERSHIPNO        = "MembershipNo";
    public static final String PASSWORD            = "Password";
    public static final String EXTERNALID          = "ExternalId";
    public static final String DISABILITY          = "Disability";
    public static final String EXCLUDEFROMCOMPETE  = "ExcludeFromCompetition";
    public static final String INPUTSHORTCUT       = "InputShortcut";
    public static final String FREEUSE1            = "FreeUse1";
    public static final String FREEUSE2            = "FreeUse2";
    public static final String FREEUSE3            = "FreeUse3";

    public static final String[] IDX_NAME_NAMEAFFIX = new String[] { FIRSTLASTNAME, NAMEAFFIX };

    private static Pattern qnamePattern = Pattern.compile("(.+) \\(([^\\(\\)]+)\\)");

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(EFBID);                             t.add(IDataAccess.DATA_STRING);
        f.add(FIRSTNAME);                         t.add(IDataAccess.DATA_STRING);
        f.add(LASTNAME);                          t.add(IDataAccess.DATA_STRING);
        f.add(FIRSTLASTNAME);                     t.add(IDataAccess.DATA_VIRTUAL);
        f.add(NAMEAFFIX);                         t.add(IDataAccess.DATA_STRING);
        f.add(TITLE);                             t.add(IDataAccess.DATA_STRING);
        f.add(GENDER);                            t.add(IDataAccess.DATA_STRING);
        f.add(BIRTHDAY);                          t.add(IDataAccess.DATA_DATE);
        f.add(ASSOCIATION);                       t.add(IDataAccess.DATA_STRING);
        f.add(STATUSID);                          t.add(IDataAccess.DATA_UUID);
        f.add(ADDRESSSTREET);                     t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSADDITIONAL);                 t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSCITY);                       t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSZIP);                        t.add(IDataAccess.DATA_STRING);
        f.add(ADDRESSCOUNTRY);                    t.add(IDataAccess.DATA_STRING);
        f.add(MEMBERSHIPNO);                      t.add(IDataAccess.DATA_STRING);
        f.add(PASSWORD);                          t.add(IDataAccess.DATA_STRING);
        f.add(EXTERNALID);                        t.add(IDataAccess.DATA_STRING);
        f.add(DISABILITY);                        t.add(IDataAccess.DATA_BOOLEAN);
        f.add(EXCLUDEFROMCOMPETE);                t.add(IDataAccess.DATA_BOOLEAN);
        f.add(INPUTSHORTCUT);                     t.add(IDataAccess.DATA_STRING);
        f.add(FREEUSE1);                          t.add(IDataAccess.DATA_STRING);
        f.add(FREEUSE2);                          t.add(IDataAccess.DATA_STRING);
        f.add(FREEUSE3);                          t.add(IDataAccess.DATA_STRING);
        MetaData metaData = constructMetaData(Persons.DATATYPE, f, t, true);
        metaData.setKey(new String[] { ID }); // plus VALID_FROM
        metaData.addIndex(IDX_NAME_NAMEAFFIX);
    }

    public PersonRecord(Persons persons, MetaData metaData) {
        super(persons, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,Long,String>(getId(),getValidFrom(),null);
    }

    public static DataKey getKey(UUID id, long validFrom) {
        return new DataKey<UUID,Long,String>(id,validFrom,null);
    }

    public void setId(UUID id) {
        setUUID(ID, id);
    }
    public UUID getId() {
        return getUUID(ID);
    }

    public void setEfbId(String id) {
        setString(EFBID, id);
    }
    public String getEfbId() {
        return getString(EFBID);
    }

    public void setFirstName(String name) {
        setString(FIRSTNAME, name);
    }
    public String getFirstName() {
        return getString(FIRSTNAME);
    }

    public void setLastName(String name) {
        setString(LASTNAME, name);
    }
    public String getLastName() {
        return getString(LASTNAME);
    }

    public void setFirstLastName(String name) {
        // nothing to do (this column in virtual)
    }
    public String getFirstLastName() {
        return getFullName(getString(FIRSTNAME), getString(LASTNAME), null, true);
    }

    public void setNameAffix(String affix) {
        setString(NAMEAFFIX, affix);
    }
    public String getNameAffix() {
        return getString(NAMEAFFIX);
    }

    public void setTitle(String title) {
        setString(TITLE, title);
    }
    public String getTitle() {
        return getString(TITLE);
    }

    public void setGender(String gender) {
        setString(GENDER, gender);
    }
    public String getGender() {
        return getString(GENDER);
    }

    public void setBirthday(DataTypeDate date) {
        setDate(BIRTHDAY, date);
    }
    public DataTypeDate getBirthday() {
        return getDate(BIRTHDAY);
    }

    public void setAssocitation(String name) {
        setString(ASSOCIATION, name);
    }
    public String getAssocitation() {
        return getString(ASSOCIATION);
    }

    public void setStatusId(UUID id) {
        setUUID(STATUSID, id);
    }
    public UUID getStatusId() {
        return getUUID(STATUSID);
    }
    public String getStatusName() {
        UUID id = getStatusId();
        if (id != null) {
            StatusRecord r = getPersistence().getProject().getStatus(false).getStatus(id);
            if (r != null) {
                return r.getStatusName();
            }
        }
        return null;
    }

    public void setAddressStreet(String street) {
        setString(ADDRESSSTREET, street);
    }
    public String getAddressStreet() {
        return getString(ADDRESSSTREET);
    }

    public void setAddressAdditional(String addressAdditional) {
        setString(ADDRESSADDITIONAL, addressAdditional);
    }
    public String getAddressAdditional() {
        return getString(ADDRESSADDITIONAL);
    }

    public void setAddressCity(String city) {
        setString(ADDRESSCITY, city);
    }
    public String getAddressCity() {
        return getString(ADDRESSCITY);
    }

    public void setAddressZip(String zip) {
        setString(ADDRESSZIP, zip);
    }
    public String getAddressZip() {
        return getString(ADDRESSZIP);
    }

    public void setAddressCountry(String country) {
        setString(ADDRESSCOUNTRY, country);
    }
    public String getAddressCountry() {
        return getString(ADDRESSCOUNTRY);
    }

    public void setMembershipNo(String no) {
        setString(MEMBERSHIPNO, no);
    }
    public String getMembershipNo() {
        return getString(MEMBERSHIPNO);
    }

    public void setPassword(String password) {
        setString(PASSWORD, password);
    }
    public String getPassword() {
        return getString(PASSWORD);
    }

    public void setExternalId(String id) {
        setString(EXTERNALID, id);
    }
    public String getExternalId() {
        return getString(EXTERNALID);
    }

    public void setDisability(boolean disabled) {
        setBool(DISABILITY, disabled);
    }
    public boolean getDisability() {
        return getBool(DISABILITY);
    }

    public void setExcludeFromCompetition(boolean exclude) {
        setBool(EXCLUDEFROMCOMPETE, exclude);
    }
    public boolean getExcludeFromCompetition() {
        return getBool(EXCLUDEFROMCOMPETE);
    }

    public void setInputShortcut(String shortcut) {
        setString(INPUTSHORTCUT, shortcut);
    }
    public String getInputShortcut() {
        return getString(INPUTSHORTCUT);
    }

    public void setFreeUse1(String s) {
        setString(FREEUSE1, s);
    }
    public String getFreeUse1() {
        return getString(FREEUSE1);
    }

    public void setFreeUse2(String s) {
        setString(FREEUSE2, s);
    }
    public String getFreeUse2() {
        return getString(FREEUSE2);
    }

    public void setFreeUse3(String s) {
        setString(FREEUSE3, s);
    }
    public String getFreeUse3() {
        return getString(FREEUSE3);
    }

    protected Object getVirtualColumn(int fieldIdx) {
        if (getFieldName(fieldIdx).equals(FIRSTLASTNAME)) {
            return getFirstLastName();
        }
        return null;
    }

    public static String getFullName(String first, String last, String affix, boolean firstFirst) {
        String s = "";
        if (firstFirst) {
            if (first != null && first.length() > 0) {
                s = first.trim();
            }
            if (last != null && last.length() > 0) {
                s = s + (s.length() > 0 ? " " : "") + last.trim();
            }
        } else {
            if (last != null && last.length() > 0) {
                s = last.trim() + ",";
            }
            if (first != null && first.length() > 0) {
                s = s + (s.length() > 0 ? " " : "") + first.trim();
            }
        }
        if (affix != null && affix.length() > 0) {
            s = s + " (" + affix + ")";
        }
        return s;
    }

    public static String getFirstLastName(String name) {
        if (name == null || name.length() == 0) {
            return "";
        }
        int pos = name.indexOf(", ");
        if (pos < 0) {
            return name;
        }
        return (name.substring(pos+2) + " " + name.substring(0, pos)).trim();
    }

    public String getQualifiedName(boolean firstFirst) {
        return getFullName(getFirstName(), getLastName(), getNameAffix(), firstFirst);
    }

    public String getQualifiedName() {
        return getQualifiedName(Daten.efaConfig.getValueNameFormat().equals(EfaConfig.NAMEFORMAT_FIRSTLAST));
    }

    public String[] getQualifiedNameFields() {
        return IDX_NAME_NAMEAFFIX;
    }

    public String[] getQualifiedNameFieldsTranslateVirtualToReal() {
        return new String[] { FIRSTNAME, LASTNAME, NAMEAFFIX };
    }

    public String[] getQualifiedNameValues(String qname) {
        Matcher m = qnamePattern.matcher(qname);
        if (m.matches()) {
            return new String[] {
                getFirstLastName(m.group(1).trim()),
                m.group(2).trim()
            };
        } else {
            return new String[] {
                getFirstLastName(qname.trim()),
                null
            };
        }
    }

    public static String[] tryGetFirstLastNameAndAffix(String s) {
        Matcher m = qnamePattern.matcher(s);
        String name = s.trim();
        String affix = null;
        if (m.matches()) {
            name = m.group(1).trim();
            affix = m.group(2).trim();
        }

        String firstName = null;
        String lastName = null;
        int pos = name.indexOf(", ");
        if (pos < 0) {
            pos = name.indexOf(" ");
            if (pos >= 0) {
                firstName = name.substring(0, pos).trim();
                lastName  = name.substring(pos+1).trim();
            }
        } else {
            firstName = name.substring(pos+2);
            lastName = name.substring(0, pos).trim();
        }
        return new String[] { firstName, lastName, affix };
    }

    public static String[] tryGetNameAndAffix(String s) {
        Matcher m = qnamePattern.matcher(s);
        String name = s.trim();
        String affix = null;
        if (m.matches()) {
            name = m.group(1).trim();
            affix = m.group(2).trim();
        }
        return new String[] { name, affix };
    }

    public Object getUniqueIdForRecord() {
        return getId();
    }

    public String getAsText(String fieldName) {
        if (fieldName.equals(GENDER)) {
            String s = getAsString(fieldName);
            if (s != null) {
                return Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER, s);
            }
            return null;
        }
        if (fieldName.equals(STATUSID)) {
            return getStatusName();
        }
        return super.getAsText(fieldName);
    }

    public void setFromText(String fieldName, String value) {
        if (fieldName.equals(GENDER)) {
            String s = Daten.efaTypes.getTypeForValue(EfaTypes.CATEGORY_GENDER, value);
            if (s != null) {
                set(fieldName, s);
            }
            return;
        }
        if (fieldName.equals(STATUSID)) {
            Status status = getPersistence().getProject().getStatus(false);
            StatusRecord sr = status.findStatusByName(value);
            if (sr != null) {
                set(fieldName, sr.getId());
            }
            return;
        }
        set(fieldName, value);
    }

    public Vector<IItemType> getGuiItems() {
        String CAT_BASEDATA = "%01%" + International.getString("Basisdaten");
        String CAT_MOREDATA = "%02%" + International.getString("Weitere Daten");
        String CAT_ADDRESS  = "%03%" + International.getString("Adresse");
        String CAT_FREEUSE  = "%04%" + International.getString("Freie Verwendung");

        Status status = getPersistence().getProject().getStatus(false);
        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();
        v.add(item = new ItemTypeString(PersonRecord.FIRSTNAME, getFirstName(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Vorname")));
        v.add(item = new ItemTypeString(PersonRecord.LASTNAME, getLastName(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Nachname")));
        v.add(item = new ItemTypeString(PersonRecord.NAMEAFFIX, getNameAffix(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Namenszusatz")));
        v.add(item = new ItemTypeString(PersonRecord.TITLE, getTitle(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Titel")));
        v.add(item = new ItemTypeStringList(PersonRecord.GENDER, getGender(),
                EfaTypes.makeGenderArray(EfaTypes.ARRAY_STRINGLIST_VALUES), EfaTypes.makeGenderArray(EfaTypes.ARRAY_STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Geschlecht")));
        v.add(item = new ItemTypeDate(PersonRecord.BIRTHDAY, getBirthday(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Geburtstag")));
        ((ItemTypeDate)item).setAllowYearOnly(true);
        v.add(item = new ItemTypeStringList(PersonRecord.STATUSID, (getStatusId() != null ? getStatusId().toString() : status.getStatusOther().getId().toString()),
                status.makeStatusArray(Status.ARRAY_STRINGLIST_VALUES), status.makeStatusArray(Status.ARRAY_STRINGLIST_DISPLAY),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Status")));
        v.add(item = new ItemTypeLabel(PersonRecord.LASTMODIFIED, IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                International.getMessage("zuletzt geändert am {datetime}", EfaUtil.date2String(new Date(this.getLastModified())))));
        item.setPadding(0, 0, 20, 0);

        v.add(item = new ItemTypeString(PersonRecord.ASSOCIATION, getAssocitation(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Verein")));
        v.add(item = new ItemTypeString(PersonRecord.MEMBERSHIPNO, getMembershipNo(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Mitgliedsnummer")));
        v.add(item = new ItemTypeString(PersonRecord.PASSWORD, getPassword(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Paßwort")));
        v.add(item = new ItemTypeBoolean(PersonRecord.DISABILITY, getDisability(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("50% oder mehr Behinderung")));
        v.add(item = new ItemTypeBoolean(PersonRecord.EXCLUDEFROMCOMPETE, getExcludeFromCompetition(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("von Wettbewerbsmeldungen ausnehmen")));
        v.add(item = new ItemTypeString(PersonRecord.INPUTSHORTCUT, getInputShortcut(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Eingabekürzel")));
        v.add(item = new ItemTypeString(PersonRecord.EXTERNALID, getExternalId(),
                IItemType.TYPE_PUBLIC, CAT_MOREDATA, International.getString("Externe ID")));
        if (Daten.efaConfig.getValueUseFunctionalityCanoeingGermany()) {
            v.add(item = new ItemTypeString(PersonRecord.EFBID, getEfbId(),
                    IItemType.TYPE_EXPERT, CAT_MOREDATA, International.onlyFor("Kanu-eFB ID","de")));
        }

        v.add(item = new ItemTypeString(PersonRecord.ADDRESSSTREET, getAddressStreet(),
                IItemType.TYPE_PUBLIC, CAT_ADDRESS, International.getString("Straße")));
        v.add(item = new ItemTypeString(PersonRecord.ADDRESSADDITIONAL, getAddressAdditional(),
                IItemType.TYPE_PUBLIC, CAT_ADDRESS, International.getString("weitere Adreßzeile")));
        v.add(item = new ItemTypeString(PersonRecord.ADDRESSCITY, getAddressCity(),
                IItemType.TYPE_PUBLIC, CAT_ADDRESS, International.getString("Stadt")));
        v.add(item = new ItemTypeString(PersonRecord.ADDRESSZIP, getAddressZip(),
                IItemType.TYPE_PUBLIC, CAT_ADDRESS, International.getString("Postleitzahl")));
        v.add(item = new ItemTypeString(PersonRecord.ADDRESSCOUNTRY, getAddressCountry(),
                IItemType.TYPE_PUBLIC, CAT_ADDRESS, International.getString("Land")));

        v.add(item = new ItemTypeString(PersonRecord.FREEUSE1, getFreeUse1(),
                IItemType.TYPE_PUBLIC, CAT_FREEUSE, International.getString("Freie Verwendung") + " 1"));
        v.add(item = new ItemTypeString(PersonRecord.FREEUSE2, getFreeUse2(),
                IItemType.TYPE_PUBLIC, CAT_FREEUSE, International.getString("Freie Verwendung") + " 2"));
        v.add(item = new ItemTypeString(PersonRecord.FREEUSE3, getFreeUse3(),
                IItemType.TYPE_PUBLIC, CAT_FREEUSE, International.getString("Freie Verwendung") + " 3"));
        
        return v;
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[4];
        header[0] = new TableItemHeader(International.getString("Nachname"));
        header[1] = new TableItemHeader(International.getString("Vorname"));
        header[2] = new TableItemHeader(International.getString("Geburtstag"));
        header[3] = new TableItemHeader(International.getString("Status"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[4];
        items[0] = new TableItem(getLastName());
        items[1] = new TableItem(getFirstName());
        items[2] = new TableItem(getBirthday());
        items[3] = new TableItem(getStatusName());
        return items;
    }
    
}