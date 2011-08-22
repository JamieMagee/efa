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

public class GroupRecord extends DataRecord implements IItemFactory {

    // =========================================================================
    // Field Names
    // =========================================================================

    public static final String ID                  = "Id";
    public static final String NAME                = "Name";
    public static final String MEMBERIDLIST        = "MemberIdList";

    public static final String[] IDX_NAME = new String[] { NAME };

    private static final String CAT_BASEDATA = "%01%" + International.getString("Gruppe");
    private static final String GUIITEM_MEMBERIDLIST = "GUIITEM_MEMBERIDLIST";

    public static void initialize() {
        Vector<String> f = new Vector<String>();
        Vector<Integer> t = new Vector<Integer>();

        f.add(ID);                                t.add(IDataAccess.DATA_UUID);
        f.add(NAME);                              t.add(IDataAccess.DATA_STRING);
        f.add(MEMBERIDLIST);                      t.add(IDataAccess.DATA_LIST_UUID);
        MetaData metaData = constructMetaData(Groups.DATATYPE, f, t, true);
        metaData.setKey(new String[] { ID }); // plus VALID_FROM
        metaData.addIndex(IDX_NAME);
    }

    public GroupRecord(Groups groups, MetaData metaData) {
        super(groups, metaData);
    }

    public DataRecord createDataRecord() { // used for cloning
        return getPersistence().createNewRecord();
    }

    public DataKey getKey() {
        return new DataKey<UUID,Long,String>(getId(),getValidFrom(),null);
    }

    public static DataKey getKey(UUID id, long validFrom) {
        return new DataKey<UUID,Long,String>(id ,validFrom, null);
    }

    public void setId(UUID id) {
        setUUID(ID, id);
    }
    public UUID getId() {
        return getUUID(ID);
    }

    public void setName(String name) {
        setString(NAME, name);
    }
    public String getName() {
        return getString(NAME);
    }

    public void setMemberIdList(DataTypeList<UUID> list) {
        setList(MEMBERIDLIST, list);
    }
    public DataTypeList<UUID> getMemberIdList() {
        return getList(MEMBERIDLIST, IDataAccess.DATA_UUID);
    }

    public int getNumberOfMembers() {
        DataTypeList list = getMemberIdList();
        return (list == null ? 0 : list.length());
    }

    public String getQualifiedName() {
        String name = getName();
        return (name != null ? name : "");
    }

    public String[] getQualifiedNameFields() {
        return IDX_NAME;
    }

    public Object getUniqueIdForRecord() {
        return getId();
    }

    public IItemType[] getDefaultItems(String itemName) {
        if (itemName.equals(GroupRecord.GUIITEM_MEMBERIDLIST)) {
            IItemType[] items = new IItemType[1];
            items[0] = getGuiItemTypeStringAutoComplete(BoatRecord.ALLOWEDGROUPIDLIST, null,
                    IItemType.TYPE_PUBLIC, CAT_BASEDATA,
                    getPersistence().getProject().getPersons(false), getValidFrom(), getInvalidFrom()-1,
                    International.getString("Mitglied"));
            items[0].setFieldSize(300, -1);
            return items;
        }
        return null;
    }

    public Vector<IItemType> getGuiItems() {
        Vector<IItemType[]> itemList;

        IItemType item;
        Vector<IItemType> v = new Vector<IItemType>();

        v.add(item = new ItemTypeString(CrewRecord.NAME, getName(),
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Gruppenname")));

        DataTypeList<UUID> members = getMemberIdList();
        itemList = new Vector<IItemType[]>();
        for (int i=0; members != null && i<members.length(); i++) {
            IItemType[] items = getDefaultItems(GUIITEM_MEMBERIDLIST);
            ((ItemTypeStringAutoComplete)items[0]).setId(members.get(i));
            itemList.add(items);
        }
        v.add(item = new ItemTypeItemList(GUIITEM_MEMBERIDLIST, itemList, this,
                IItemType.TYPE_PUBLIC, CAT_BASEDATA, International.getString("Mitglieder")));
        ((ItemTypeItemList)item).setAppendPositionToEachElement(true);
        ((ItemTypeItemList)item).setRepeatTitle(false);
        ((ItemTypeItemList) item).setXForAddDelButtons(3);
        ((ItemTypeItemList) item).setPadYbetween(0);

        return v;
    }

    public void saveGuiItems(Vector<IItemType> items) {
        Persons persons = getPersistence().getProject().getPersons(false);

        for(IItemType item : items) {
            String name = item.getName();
            if (name.equals(GUIITEM_MEMBERIDLIST) && item.isChanged()) {
                ItemTypeItemList list = (ItemTypeItemList)item;
                Hashtable<String,UUID> uuidList = new Hashtable<String,UUID>();
                for (int i=0; i<list.size(); i++) {
                    IItemType[] typeItems = list.getItems(i);
                    Object uuid = ((ItemTypeStringAutoComplete)typeItems[0]).getId(typeItems[0].toString());
                    if (uuid != null && uuid.toString().length() > 0) {
                        uuidList.put(uuid.toString(), (UUID)uuid);
                    }
                }
                String[] uuidArr = uuidList.keySet().toArray(new String[0]);
                DataTypeList<UUID> memberList = new DataTypeList<UUID>();
                for (String uuid : uuidArr) {
                    memberList.add(uuidList.get(uuid));
                }
                setMemberIdList(memberList);
            }
        }

        super.saveGuiItems(items);
    }

    public TableItemHeader[] getGuiTableHeader() {
        TableItemHeader[] header = new TableItemHeader[2];
        header[0] = new TableItemHeader(International.getString("Gruppenname"));
        header[1] = new TableItemHeader(International.getString("Anzahl Mitglieder"));
        return header;
    }

    public TableItem[] getGuiTableItems() {
        TableItem[] items = new TableItem[2];
        items[0] = new TableItem(getName());
        items[1] = new TableItem(Integer.toString(getNumberOfMembers()));
        return items;
    }
    
}
