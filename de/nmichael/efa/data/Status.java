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

import de.nmichael.efa.ex.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.data.storage.*;
import java.util.*;

// @i18n complete

public class Status extends StorageObject {

    public static final String DATATYPE = "efa2status";

    public static final int ARRAY_STRINGLIST_VALUES  = 1;
    public static final int ARRAY_STRINGLIST_DISPLAY = 2;

    private UUID idGuest;
    private UUID idOther;

    public Status(int storageType, 
            String storageLocation,
            String storageUsername,
            String storagePassword,
            String storageObjectName) {
        super(storageType, storageLocation, storageUsername, storagePassword, storageObjectName, DATATYPE, International.getString("Status"));
        StatusRecord.initialize();
        dataAccess.setMetaData(MetaData.getMetaData(DATATYPE));
    }

    public DataRecord createNewRecord() {
        return new StatusRecord(this, MetaData.getMetaData(DATATYPE));
    }

    public StatusRecord createStatusRecord(UUID id) {
        StatusRecord r = new StatusRecord(this, MetaData.getMetaData(DATATYPE));
        r.setId(id);
        return r;
    }

    private UUID findStatusId(String type) {
        try {
            DataKeyIterator it = data().getStaticIterator();
            DataKey k = it.getFirst();
            while (k != null) {
                StatusRecord r = (StatusRecord)data().get(k);
                if (r != null && r.getType().equals(type)) {
                    return r.getId();
                }
                k = it.getNext();
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    public synchronized StatusRecord getStatusGuest() {
        if (idGuest == null) {
            idGuest = findStatusId(StatusRecord.TYPE_GUEST);
        }
        return getStatus(idGuest);
    }

    public synchronized StatusRecord getStatusOther() {
        if (idOther == null) {
            idOther = findStatusId(StatusRecord.TYPE_OTHER);
        }
        return getStatus(idOther);
    }

    public StatusRecord getStatus(UUID id) {
        try {
            if (id != null) {
                return (StatusRecord) (data().get(StatusRecord.getKey(id)));
            }
        } catch (Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    public StatusRecord[] getAllStatus() {
        try {
            DataKeyIterator it = data().getStaticIterator();
            StatusRecord[] sr = new StatusRecord[it.size()];
            DataKey k = it.getFirst();
            int i = 0;
            while (k != null) {
                sr[i++] = (StatusRecord)data().get(k);
                k = it.getNext();
            }
            Arrays.sort(sr);
            return sr;
        } catch (Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    public StatusRecord findStatusByName(String name) {
        try {
            DataKey[] keys = data().getByFields(new String[] { StatusRecord.NAME }, new String[] { name });
            if (keys != null && keys.length > 0) {
                return (StatusRecord) data().get(keys[0]);
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    private UUID addStatus(UUID id, String name, String type) {
        try {
            if (findStatusByName(name) != null) {
                return null;
            }
            StatusRecord r = createStatusRecord(id);
            r.setStatusName(name);
            r.setType(type);
            data().add(r);
        } catch(Exception e) {
            Logger.logdebug(e);
            return null;
        }
        return id;
    }

    public UUID addStatus(String name, String type) {
        if ( type.equals(StatusRecord.TYPE_USER) ||
             (type.equals(StatusRecord.TYPE_GUEST) && findStatusId(StatusRecord.TYPE_GUEST) == null) ||
             (type.equals(StatusRecord.TYPE_OTHER) && findStatusId(StatusRecord.TYPE_OTHER) == null) ) {
            return addStatus(UUID.randomUUID(), name, type);
        }
        return null;
    }

    public boolean updateStatus(UUID id, String newName) {
        StatusRecord r = findStatusByName(newName);
        if (r != null) {
            return false;
        }
        r = getStatus(id);
        if (r == null) {
            return false;
        }
        r.setStatusName(newName);
        try {
            data().update(r);
            return true;
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return false;
    }

    public boolean deleteStatus(UUID id) {
        StatusRecord r = getStatus(id);
        if (r == null ||
            r.getType().equals(StatusRecord.TYPE_GUEST) ||
            r.getType().equals(StatusRecord.TYPE_OTHER)) {
            return false;
        }
        try {
            data().delete(r.getKey());
            return true;
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return false;
    }

    public String[] makeStatusArray(int type) {
        StatusRecord[] sr = getAllStatus();
        String[] status = new String[ (sr != null ? sr.length : 0) ];
        for(int i=0; i<status.length; i++) {
            status[i] = (type == ARRAY_STRINGLIST_VALUES ?
                sr[i].getId().toString() :
                sr[i].getStatusName());
        }
        return status;
    }

    public UUID[] makeStatusArrayUUID() {
        StatusRecord[] sr = getAllStatus();
        UUID[] status = new UUID[ (sr != null ? sr.length : 0) ];
        for(int i=0; i<status.length; i++) {
            status[i] = sr[i].getId();
        }
        return status;
    }

    public void open(boolean createNewIfNotExists) throws EfaException {
        super.open(createNewIfNotExists);
        if (isOpen()) {
            if (data().getNumberOfRecords() == 0) {
                addStatus(International.getString("Junior(in)"), StatusRecord.TYPE_USER);
                addStatus(International.getString("Senior(in)"), StatusRecord.TYPE_USER);
            }
            // make sure GUEST and OTHER status types are always present
            addStatus(International.getString("Gast"), StatusRecord.TYPE_GUEST);
            addStatus(International.getString("andere"), StatusRecord.TYPE_OTHER);
        }
    }

    public void preModifyRecordCallback(DataRecord record, boolean add, boolean update, boolean delete) throws EfaModifyException {
        if (add || update) {
            assertFieldNotEmpty(record, StatusRecord.ID);
            assertFieldNotEmpty(record, StatusRecord.NAME);
            assertUnique(record, StatusRecord.NAME);
        }
        if (delete) {
            StatusRecord sr = (StatusRecord) record;
            if (sr.getType() != null && !sr.getType().equals(StatusRecord.TYPE_USER)) {
                throw new EfaModifyException(Logger.MSG_DATA_MODIFYEXCEPTION,
                        International.getMessage("Vordefinierter Status vom Typ '{type}' kann nicht gelöscht werden.", sr.getTypeDescription()),
                        Thread.currentThread().getStackTrace());
            }
            assertNotReferenced(record, getProject().getPersons(false), new String[] { PersonRecord.STATUSID });
        }
    }

}