/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui.util;

import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.util.*;
import java.util.*;

public class AutoCompleteList {

    private IDataAccess dataAccess;
    private long dataAccessSCN = -1;
    private long validFrom = -1;
    private long validUntil = -1;
    private Vector<String> dataVisible = new Vector<String>();;
    private Hashtable<String,String> lower2realVisible = new Hashtable<String,String>();;
    private Hashtable<String,String> lower2realInvisible = new Hashtable<String,String>();;
    private Hashtable<String,String> aliases2realVisible = new Hashtable<String,String>();;
    private int pos = 0;
    private String lastPrefix;
    private long scn = 0;
    private String _searchId;
    private String _foundValue;

    public AutoCompleteList() {
    }

    public AutoCompleteList(IDataAccess dataAccess) {
        setDataAccess(dataAccess);
    }

    public AutoCompleteList(IDataAccess dataAccess, long validFrom, long validUntil) {
        setDataAccess(dataAccess, validFrom, validUntil);
    }

    public synchronized void setDataAccess(IDataAccess dataAccess) {
        setDataAccess(dataAccess, -1, -1);
    }

    public synchronized void setValidRange(long validFrom, long validUntil) {
        setDataAccess(dataAccess, validFrom, validUntil);
    }

    public synchronized void setDataAccess(IDataAccess dataAccess, long validFrom, long validUntil) {
        this.dataAccess = dataAccess;
        this.dataAccessSCN = -1;
        this.validFrom = validFrom;
        this.validUntil = validUntil;
        scn++;
    }

    /**
     * Synchronize this list with the uderlying DataAccess, if necessary
     */
    public synchronized void update() {
        try {
            _foundValue = null;
            if (dataAccess != null && dataAccess.isStorageObjectOpen() && dataAccess.getSCN() != dataAccessSCN) {
                dataAccessSCN = dataAccess.getSCN();
                dataVisible = new Vector<String>();
                lower2realVisible = new Hashtable<String,String>();
                lower2realInvisible = new Hashtable<String,String>();
                aliases2realVisible = new Hashtable<String,String>();
                DataKeyIterator it = dataAccess.getStaticIterator();
                DataKey k = it.getFirst();
                while (k != null) {
                    DataRecord r = dataAccess.get(k);
                    if (r != null) {
                        if (_searchId != null && r.getUniqueIdForRecord() != null && _searchId.equals(r.getUniqueIdForRecord().toString())) {
                            _foundValue = r.getQualifiedName();
                        }
                        String s = r.getQualifiedName();
                        String alias = (r instanceof PersonRecord ? ((PersonRecord)r).getInputShortcut() : null);
                        if (!r.getDeleted() && !r.getInvisible()) {
                            if (s.length() > 0) {
                                add(s, alias, r.isInValidityRange(validFrom, validUntil));
                            }
                        } else {
                            add(s, alias, false);
                        }
                    }
                    k = it.getNext();
                }
                sort();
            }
        } catch (Exception e) {
        }
    }

    public synchronized String getValueForId(String id) {
        _searchId = id;
        update();
        _searchId = null;
        return _foundValue;
    }

    public synchronized void add(String s, String alias, boolean visibleInDropDown) {
        String lowers = s.toLowerCase();
        if (visibleInDropDown) {
            if (lower2realVisible.get(lowers) == null) {
                dataVisible.add(s);
                lower2realVisible.put(lowers, s);
                if (alias != null && alias.length() > 0) {
                    aliases2realVisible.put(alias.toLowerCase(), s);
                }
            }
        } else {
            lower2realInvisible.put(lowers, s);
        }
        scn++;
    }

    public synchronized void delete(String s) {
        dataVisible.remove(s);
        lower2realVisible.remove(s.toLowerCase());
        lower2realInvisible.remove(s.toLowerCase());
        aliases2realVisible.remove(s.toLowerCase());
        scn++;
    }

    public synchronized void sort() {
        String[] a = dataVisible.toArray(new String[0]);
        Arrays.sort(a);
        dataVisible = new Vector(a.length);
        for (int i=0; i<a.length; i++) {
            dataVisible.add(a[i]);
        }
    }

    public synchronized String getExact(String s) {
        s = s.toLowerCase();
        if (lower2realVisible.containsKey(s)) {
            return lower2realVisible.get(s);
        } else {
            if (lower2realInvisible.containsKey(s)) {
                return lower2realInvisible.get(s);
            }
            return null;
        }
    }

    public synchronized String getNext() {
        if (pos < dataVisible.size() - 1) {
            return dataVisible.get(++pos);
        }
        return null;
    }

    public synchronized String getPrev() {
        if (pos > 0) {
            return dataVisible.get(--pos);
        }
        return null;
    }

    public synchronized String getFirst(String prefix) {
        prefix = prefix.toLowerCase();
        lastPrefix = prefix;
        for (pos = 0; pos < dataVisible.size(); pos++) {
            if (dataVisible.get(pos).toLowerCase().startsWith(prefix)) {
                return dataVisible.get(pos);
            }
        }
        return null;
    }

    public synchronized String getLast(String prefix) {
        prefix = prefix.toLowerCase();
        lastPrefix = prefix;
        for (pos = dataVisible.size()-1; pos >= 0; pos--) {
            if (dataVisible.get(pos).toLowerCase().startsWith(prefix)) {
                return dataVisible.get(pos);
            }
        }
        return null;
    }

    public synchronized String getNext(String prefix) {
        prefix = prefix.toLowerCase();
        if (lastPrefix == null || !prefix.equals(lastPrefix)) {
            return getFirst(prefix);
        }
        if (pos < dataVisible.size() - 1) {
            String s = dataVisible.get(++pos);
            if (s.toLowerCase().startsWith(prefix)) {
                return s;
            }
        }
        return null;
    }

    public synchronized String getPrev(String prefix) {
        prefix = prefix.toLowerCase();
        if (lastPrefix == null || !prefix.equals(lastPrefix)) {
            return getFirst(prefix);
        }
        if (pos > 0) {
            String s = dataVisible.get(--pos);
            if (s.toLowerCase().startsWith(prefix)) {
                return s;
            }
        }
        return null;
    }

    public synchronized String getAlias(String s) {
        s = s.toLowerCase();
        if (aliases2realVisible.containsKey(s)) {
            return aliases2realVisible.get(s);
        }
        return null;
    }

    public String[] getData() {
        return dataVisible.toArray(new String[0]);
    }

    public synchronized Object getId(String qname) {
        try {
            if (dataAccess != null && dataAccess.isStorageObjectOpen()) {
                DataRecord dummyRec = dataAccess.getPersistence().createNewRecord();
                DataKey[] keys = dataAccess.getByFields(dummyRec.getQualifiedNameFields(), dummyRec.getQualifiedNameValues(qname));
                if (keys == null || keys.length < 1) {
                    return null;
                }
                return dataAccess.get(keys[0]).getUniqueIdForRecord();
            }
        } catch(Exception e) {
            Logger.logdebug(e);
        }
        return null;
    }

    public long getSCN() {
        update();
        return scn;
    }

    /**
     * Creates a vector containing all neigbours of a String. The distance
     * is measured by using EditDistance - number of keboard-hits to transform
     * name into neighbour.

     * @param name String who's neighbours are searched
     * @param radius
     * @return Vector containing neighbour strings
     * @author Thilo A. Coblenzer (original implementation)
     */
    public synchronized Vector<String> getNeighbours(String name, int radius, int maxPermutations) {
        Vector<String> neighbours = new Vector<String>();
        name = name.toLowerCase();
        Vector<String> namePerm = null;
        if (maxPermutations > 0) {
            namePerm = getPermutations(name, maxPermutations);
        }

        int lowestDist = Integer.MAX_VALUE;
        for (int i=dataVisible.size()-1; i>=0; i--) {
            String neighbour = dataVisible.get(i);
            String neighbourlc = neighbour.toLowerCase();

            int dist = EditDistance.getDistance(neighbour.toLowerCase(), name);
            if (dist <= radius) {
                if (dist < lowestDist) {
                    neighbours.add(0, neighbour);
                } else {
                    neighbours.add(neighbour);
                }
                lowestDist = dist;
            } else {
                if (namePerm != null) {
                    // check for neighbours for each of the name parts
                    Vector<String> neighbourPerm = getPermutations(neighbourlc, maxPermutations);
                    boolean found = false;
                    for (int x = 0; !found && x < namePerm.size() && x < maxPermutations; x++) {
                        for (int y = 0; !found && y < neighbourPerm.size() && y < maxPermutations; y++) {
                            dist = EditDistance.getDistance(neighbourPerm.get(y).toLowerCase(), namePerm.get(x));
                            if (dist <= radius) {
                                if (dist < lowestDist) {
                                    neighbours.add(0, neighbour);
                                } else {
                                    neighbours.add(neighbour);
                                }
                                lowestDist = dist;
                                found = true;
                            }
                        }
                    }
                }
            }
        }

        if (neighbours.size() == 0) {
            return null;
        } else {
            return neighbours;
        }
    }

    private static Vector<String> getPermutations(String s, int maxPermutations) {
        Vector<String> parts = splitString(s);
        Vector<String> perms = new Vector<String>();
        addPermutation(perms, parts, "", maxPermutations);
        return perms;
    }

    private static void addPermutation(Vector<String> perms, Vector<String> parts, String perm, int maxPermutations) {
        for (int i=0; i<parts.size(); i++) {
            if (perms.size() >= maxPermutations) {
                return;
            }
            String newPerm = (perm.length() > 0 ? perm + " " : "") + parts.get(i);
            if (parts.size() == 1) {
                perms.add(newPerm);
            } else {
                Vector<String> remainingParts = new Vector<String>(parts);
                remainingParts.remove(i);
                addPermutation(perms, remainingParts, newPerm, maxPermutations);
            }
        }
    }

    private static Vector<String> splitString(String s) {
        Vector<String> v = new Vector<String>();
        StringBuilder sb = new StringBuilder();
        for (int i=0; s != null && i<s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isLetter(c)) {
                sb.append(c);
            } else {
                if (sb.length() > 0) {
                    v.add(sb.toString());
                    sb = new StringBuilder();
                }
            }
        }
        if (sb.length() > 0) {
            v.add(sb.toString());
        }
        return v;
    }

    public static void main(String[] args) {
        Vector<String> v = getPermutations("a b c", 7);
        for (int i=0; i<v.size(); i++) {
            System.out.println(v.get(i));
        }
    }
    
}
