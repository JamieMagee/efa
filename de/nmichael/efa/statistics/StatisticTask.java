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

import de.nmichael.efa.data.efawett.ZielfahrtFolge;
import de.nmichael.efa.data.efawett.Zielfahrt;
import java.util.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.*;
import de.nmichael.efa.core.Plugins;
import de.nmichael.efa.core.config.AdminRecord;
import de.nmichael.efa.data.efawett.WettDefs;
import de.nmichael.efa.core.config.EfaTypes;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.gui.*;

public class StatisticTask extends ProgressTask {

    private static final int WORK_PER_STATISTIC = 100;
    private static final int WORK_POSTPROCESSING = 50;
    private StatisticsRecord[] statisticsRecords;
    private AdminRecord admin;
    private StatisticsRecord sr;
    private Hashtable<Object, StatisticsData> data;
    private Persons persons = Daten.project.getPersons(false);
    private Boats boats = Daten.project.getBoats(false);
    private Destinations destinations = Daten.project.getDestinations(false);
    private Waters waters = Daten.project.getWaters(false);
    private Groups groups = Daten.project.getGroups(false);
    private Status status = Daten.project.getStatus(false);
    private StatusRecord statusGuest = null;
    private StatusRecord statusOther = null;
    // values from current logbook entry
    private Logbook logbook;
    private DataTypeIntString entryNo;
    private DataTypeDate entryDate;
    private DataTypeDate entryEndDate;
    private long entryNumberOfDays;
    private long entryValidAt;
    // ---
    private UUID entryBoatId;
    private BoatRecord entryBoatRecord;
    private String entryBoatName;
    private String entryBoatType;
    private String entryBoatSeats;
    private String entryBoatRigging;
    private String entryBoatCoxing;
    private String entryBoatOwner;
    private boolean entryBoatExclude;
    // ---
    private UUID entryPersonId;
    private PersonRecord entryPersonRecord;
    private String entryPersonName;
    private UUID entryPersonStatusId;
    private boolean entryPersonIsGuest;
    private boolean entryPersonIsOther;
    private String entryPersonGender;
    private boolean entryPersonExcludeFromPublic;
    // ---
    private UUID entryDestinationId;
    private DestinationRecord entryDestinationRecord;
    private String entryDestinationVariant;
    private String entryDestinationName;
    private String entryDestinationNameAndVariant;
    private ZielfahrtFolge entryDestinationAreas;
    private long entryDistanceInDefaultUnit;
    private String entrySessionType;
    private SessionGroupRecord entrySessionGroup;
    // --- TODO einbinden der Variablen
    private UUID entryClubworkId;
    private ClubworkRecord entryClubworkRecord;
    private UUID entryClubworkPersonId;
    private Date entryClubworkDate;
    private String entryClubworkDescription;
    private int entryClubworkHours;
    // internal variables
    private ArrayList<String> successfulDoneMessages = new ArrayList<String>();

    private StatisticTask(StatisticsRecord[] statisticsRecords, AdminRecord admin) {
        this.statisticsRecords = statisticsRecords;
        this.admin = admin;

        statusGuest = status.getStatusGuest();
        statusOther = status.getStatusOther();
    }

    private void resetEntryValues() {
        entryNo = null;
        entryDate = null;
        entryEndDate = null;
        entryNumberOfDays = 1;
        entryValidAt = -1;
        entryBoatId = null;
        entryBoatRecord = null;
        entryBoatName = null;
        entryBoatType = null;
        entryBoatSeats = null;
        entryBoatRigging = null;
        entryBoatCoxing = null;
        entryBoatOwner = null;
        entryBoatExclude = false;
        entryPersonId = null;
        entryPersonRecord = null;
        entryPersonName = null;
        entryPersonStatusId = null;
        entryPersonIsGuest = false;
        entryPersonIsOther = false;
        entryPersonGender = null;
        entryPersonExcludeFromPublic = false;
        entryDestinationId = null;
        entryDestinationRecord = null;
        entryDestinationVariant = null;
        entryDestinationName = null;
        entryDestinationNameAndVariant = null;
        entryDestinationAreas = null;
        entryDistanceInDefaultUnit = 0;
        entrySessionType = null;
        entrySessionGroup = null;
        // ---
        entryClubworkId = null;
        entryClubworkRecord = null;
        entryClubworkPersonId = null;
        entryClubworkDate = null;
        entryClubworkDescription = null;
        entryClubworkHours = 0;
    }

    private void calculateSessionHistory(LogbookRecord r, Object key, StatisticsData sd, long distanceOfEntry) {
        if (sd.sessionHistory == null) {
            sd.sessionHistory = new SessionHistory();
        }

        boolean splitSessionIntoDays = entryNumberOfDays > 1;
        if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list
                && sr.sIsAggrWanderfahrten) {
            splitSessionIntoDays = false;
        }
        if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.competition
                && (sr.sStatisticType.equals(WettDefs.STR_DRV_FAHRTENABZEICHEN)
                || sr.sStatisticType.equals(WettDefs.STR_DRV_WANDERRUDERSTATISTIK))) {
            splitSessionIntoDays = false;
        }

        if (splitSessionIntoDays) {
            long distancePerDay = distanceOfEntry / entryNumberOfDays;
            long remainingDistance = distanceOfEntry;
            DataTypeDate date = entryDate;
            for (int i = 1; i <= entryNumberOfDays; i++) {
                long dist = distancePerDay;
                if (i > 1 && i == entryNumberOfDays) {
                    dist = remainingDistance;
                    date = entryEndDate;
                }
                DataTypeDistance distance = new DataTypeDistance(dist);
                Zielfahrt destArea = null;
                if (entryDestinationAreas != null && i <= entryDestinationAreas.getAnzZielfahrten()) {
                    destArea = entryDestinationAreas.getZielfahrt(i - 1);
                }
                sd.sessionHistory.addSession(r, i, date, distance, destArea);
                remainingDistance -= dist;
                date.addDays(1);
            }
        } else {
            Zielfahrt destArea = null;
            if (entryDestinationAreas != null && entryDestinationAreas.getAnzZielfahrten() > 0) {
                destArea = entryDestinationAreas.getZielfahrt(0);
                if (entryDestinationAreas.getAnzZielfahrten() > 1) {
                    sr.cWarnings.put("Fahrt #" + entryNo.toString() + " hat zu viele Zielfahrten; überzählige Zielbereiche werden ignoriert.",
                            "foo");
                }
            }
            if (distanceOfEntry != entryDistanceInDefaultUnit) {
                // happens if we have already split the session, for example for calculating waters
                r = (LogbookRecord) r.cloneRecord();
                r.setDistance(new DataTypeDistance(distanceOfEntry));
            }
            if (destArea != null) {
                sd.sessionHistory.addSession(r, destArea);
            } else {
                sd.sessionHistory.addSession(r);
            }
        }
    }

    private int calculateAggregations(LogbookRecord r, Object key, long distance, boolean cox) {
        if (key == null) {
            return 0;
        }
        StatisticsData sd = data.get(key);
        if (sd == null) {
            sd = new StatisticsData(sr, key);
        }

        // aggregate
        if (sr.sIsAggrDistance || sr.sIsAggrAvgDistance || sr.sIsAggrSpeed) {
            sd.distance += distance;
        }
        if (sr.sIsAggrRowDistance) {
            sd.rowdistance += (cox ? 0 : distance);
        }
        if (sr.sIsAggrCoxDistance) {
            sd.coxdistance += (cox ? distance : 0);
        }
        if (sr.sIsAggrSessions || sr.sIsAggrAvgDistance || sr.sIsAggrSpeed) {
            sd.sessions += entryNumberOfDays;
        }
        if (sr.sIsAggrDuration || sr.sIsAggrSpeed) {
            long myDuration = r.getEntryElapsedTimeInMinutes();
            if (r.getDistance() != null && r.getDistance().isSet() &&
                r.getDistance().getValueInDefaultUnit() > distance) {
                myDuration = (myDuration * distance) / r.getDistance().getValueInDefaultUnit();
            }
            sd.duration += myDuration;
        }
        if (sr.sIsAggrZielfahrten
                && entryDestinationAreas != null && entryDestinationAreas.getAnzZielfahrten() > 0) {
            calculateSessionHistory(r, key, sd, distance);
        }
        if (sr.sIsAggrWanderfahrten
                && (CompetitionDRVFahrtenabzeichen.mayBeWafa(r)
                || entrySessionType.equals(EfaTypes.TYPE_SESSION_JUMREGATTA))) {
            calculateSessionHistory(r, key, sd, distance);
        }
        if (sr.sIsAggrWinterfahrten
                && (CompetitionLRVBerlinWinter.mayBeWinterfahrt(r)
                && entryPersonRecord != null)) {
            calculateSessionHistory(r, key, sd, distance);
        }
        if (sr.sIsAggrGigfahrten
                && EfaTypes.isGigBoot(entryBoatType)
                && entryPersonRecord != null) {
            calculateSessionHistory(r, key, sd, distance);
        }

        data.put(key, sd);
        return 1;
    }

    private int calculateAggregations(ClubworkRecord r, Object key, double hours) {
        if (key == null) {
            return 0;
        }
        StatisticsData sd = data.get(key);
        if (sd == null) {
            sd = new StatisticsData(sr, key);
        }

        // aggregate
        if (sr.sIsAggrClubwork || sr.sIsAggrClubworkRelativeToTarget || sr.sIsAggrClubworkOverUnderCarryOver) {
            sd.clubwork += hours;
        }
        if (sr.sIsAggrClubworkCredit && r.getDescription().startsWith(International.getString("Gutschrift"))) {
            sd.clubworkCredit += hours;
        }

        data.put(key, sd);
        return 1;
    }

    private int calculateAggregationsForList(LogbookRecord r, DataTypeList list, boolean cox) {
        if (list == null || list.length() == 0) {
            return 0;
        }
        int cnt = 0;
        int size = list.length();
        long distance = 0;

        for (int i = 0; i < size; i++) {
            Object key = list.get(i);
            long myDistance = entryDistanceInDefaultUnit / size;
            if (i + 1 == size) {
                myDistance = entryDistanceInDefaultUnit - distance;
            }
            cnt += calculateAggregations(r, key, myDistance, cox);
            distance += myDistance;
        }

        return cnt;
    }

    private int calculateAggregationsForHash(LogbookRecord r, Hashtable hash, boolean cox) {
        int cnt = 0;
        Object[] keys = hash.keySet().toArray();
        for (Object k : keys) {
            long dist = (Long) hash.get(k);
            cnt += calculateAggregations(r, k, dist, cox);
        }
        return cnt;
    }

    private int calculateAggregationsForMatrix(LogbookRecord r, Object key, long distance, int seat) {
        if (key == null) {
            return 0;
        }
        calculateAggregations(r, key, distance, seat == 0);
        StatisticsData sd = data.get(key);
        if (sd == null) {
            sd = new StatisticsData(sr, key);
        }
        boolean cox = seat == 0;
        boolean person = seat >= 0; // else: boat

        ArrayList<Object> aggregationKeys = new ArrayList<Object>();
        if (sr.sStatistikKey == StatisticsRecord.StatisticKey.name) {
            for (int i=0; i<=LogbookRecord.CREW_MAX; i++) {
                if (i == seat) {
                    continue; // don't calculate self
                }
                UUID pId = r.getCrewId(i);
                String pName = r.getCrewName(i);
                if (pId == null && pName == null) {
                    continue;
                }
                PersonRecord pRecord = (pId != null ? persons.getPerson(pId, r.getValidAtTimestamp()) : null);
                boolean pGuest = (pRecord != null ? !pRecord.isStatusMember() : false);
                boolean pOther = (pRecord != null ? statusOther.getId().equals(pRecord.getStatusId()) : true);
                Object k = getAggregationKey_persons(pRecord, pId, pName, pGuest, pOther);
                if (k != null) {
                    aggregationKeys.add(k);
                }
            }
        } else {
            Object k = getAggregationKeyForList(r);
            if (k != null) {
                aggregationKeys.add(k);
            }
        }

        for (Object k : aggregationKeys) {
            if (k == null) {
                continue;
            }
            if (sd.matrixData == null) {
                sd.matrixData = new Hashtable<Object, StatisticsData>();
            }
            StatisticsData sdk = sd.matrixData.get(k);
            if (sdk == null) {
                sdk = new StatisticsData(sr, k);
            }

            // aggregate
            if (sr.sIsAggrDistance || sr.sIsAggrAvgDistance || sr.sIsAggrSpeed) {
                sdk.distance += distance;
            }
            if (sr.sIsAggrRowDistance) {
                sdk.rowdistance += (cox ? 0 : distance);
            }
            if (sr.sIsAggrCoxDistance) {
                sdk.coxdistance += (cox ? distance : 0);
            }
            if (sr.sIsAggrSessions || sr.sIsAggrAvgDistance || sr.sIsAggrSpeed) {
                sdk.sessions += entryNumberOfDays;
            }
            if (sr.sIsAggrDuration || sr.sIsAggrSpeed) {
                long myDuration = r.getEntryElapsedTimeInMinutes();
                if (r.getDistance() != null && r.getDistance().isSet()
                        && r.getDistance().getValueInDefaultUnit() > distance) {
                    myDuration = (myDuration * distance) / r.getDistance().getValueInDefaultUnit();
                }
                sdk.duration += myDuration;
            }
            if (sr.sIsAggrZielfahrten
                    && entryDestinationAreas != null && entryDestinationAreas.getAnzZielfahrten() > 0) {
                calculateSessionHistory(r, k, sdk, distance);
            }
            if (sr.sIsAggrWanderfahrten
                    && (CompetitionDRVFahrtenabzeichen.mayBeWafa(r)
                    || entrySessionType.equals(EfaTypes.TYPE_SESSION_JUMREGATTA))) {
                calculateSessionHistory(r, k, sdk, distance);
            }
            if (sr.sIsAggrWinterfahrten
                    && (CompetitionLRVBerlinWinter.mayBeWinterfahrt(r)
                    && entryPersonRecord != null)) {
                calculateSessionHistory(r, k, sdk, distance);
            }
            if (sr.sIsAggrGigfahrten
                    && EfaTypes.isGigBoot(entryBoatType)
                    && entryPersonRecord != null) {
                calculateSessionHistory(r, k, sdk, distance);
            }

            sd.matrixData.put(k, sdk);
        }

        data.put(key, sd);
        return 1;
    }

    private Object getAggregationKey_persons(PersonRecord person, UUID personId, String personName,
            boolean isGuest, boolean isOther) {
        if (sr.sSumGuestsByClub && isGuest) {
            String clubName = International.getString("unbekannt");
            if (person != null && person.getAssocitation() != null
                    && person.getAssocitation().length() > 0) {
                clubName = person.getAssocitation();
            }
            return StatisticsData.SORTTOEND_PREFIX + International.getMessage("Gäste von {club}", clubName);
        }
        if (sr.sSumGuestsAndOthers && (isGuest || isOther)) {
            if (isGuest) {
                return StatisticsData.SORTTOEND_PREFIX + International.getString("Gäste");
            }
            if (isOther) {
                return StatisticsData.SORTTOEND_PREFIX + International.getString("andere");
            }
        }
        if (personId != null) {
            return personId;
        }
        if (personName != null) {
            return personName;
        }
        return null;
    }

    private Object getAggregationKey_boats(BoatRecord boat, UUID boatId, String boatName) {
        if (sr.sSumGuestsByClub && boat != null) {
            String owner = boat.getOwner();
            if (owner != null && owner.length() > 0) {
                return StatisticsData.SORTTOEND_PREFIX
                        + International.getMessage("Fremdboote von {owner}", owner);
            }
        }
        if (boatId != null) {
            return boatId;
        }
        if (boatName != null) {
            return boatName;
        }
        return null;
    }

    private Object getAggregationKey_status(UUID statusId) {
        if (statusId != null) {
            StatusRecord statusRecord = status.getStatus(statusId);
            if (statusRecord != null) {
                return statusRecord.getQualifiedName();
            }
        }
        return EfaTypes.TYPE_STATUS_OTHER;
    }

    private Object getAggregationKey_yearOfBirth(PersonRecord person) {
        if (person != null) {
            DataTypeDate birthday = person.getBirthday();
            if (birthday != null && birthday.getYear() > 0) {
                return birthday.getYear();
            }
        }
        return EfaTypes.TEXT_UNKNOWN;
    }

    private Object getAggregationKey_gender(String gender) {
        if (gender != null) {
            return Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER, gender);
        }
        return EfaTypes.TEXT_UNKNOWN;
    }

    private Object getAggregationKey_boatType(String boatType) {
        if (boatType != null) {
            return Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, boatType);
        }
        return EfaTypes.TEXT_UNKNOWN;
    }

    private Object getAggregationKey_boatSeats(String boatSeats) {
        if (boatSeats != null) {
            return Daten.efaTypes.getValue(EfaTypes.CATEGORY_NUMSEATS, boatSeats);
        }
        return EfaTypes.TEXT_UNKNOWN;
    }

    private Object getAggregationKey_boatTypeDetail(String boatType, String boatSeats, String boatCoxing) {
        if (boatType != null && boatSeats != null && boatCoxing != null) {
            return BoatRecord.getDetailedBoatType(boatType, boatSeats, boatCoxing);
        }
        return EfaTypes.TEXT_UNKNOWN;
    }

    private Object getAggregationKey_destination(String destinationNameAndVariant) {
        if (destinationNameAndVariant != null && destinationNameAndVariant.length() > 0) {
            return destinationNameAndVariant;
        }
        return null;
    }

    private String getAggregationKey_waters(String watersName) {
        return watersName;
    }

    private Object getAggregationKey_distance(long distanceInDefaultUnit) {
        if (distanceInDefaultUnit > 0) {
            DataTypeDistance dist = DataTypeDistance.getDistance(distanceInDefaultUnit);
            dist.truncateToMainDistanceUnit();
            return StatisticsData.SORTING_PREFIX + EfaUtil.long2String(dist.getValueInDefaultUnit(), 9) +
                   StatisticsData.SORTING_POSTFIX + "<= " + dist.getAsFormattedString();
        }
        return null;
    }

    private Object getAggregationKey_month(DataTypeDate date) {
        if (date != null && date.isSet()) {
            return date.getMonthAsStringWithIntMarking(StatisticsData.SORTING_PREFIX, StatisticsData.SORTING_POSTFIX);
        }
        return null;
    }

    private Object getAggregationKey_weekday(DataTypeDate date) {
        if (date != null && date.isSet()) {
            return date.getWeekdayAsStringWithIntMarking(StatisticsData.SORTING_PREFIX, StatisticsData.SORTING_POSTFIX);
        }
        return null;
    }

    private String getAggregationKey_timeOfDay(int hour) {
        return StatisticsData.SORTING_PREFIX + EfaUtil.int2String(hour, 2, true) + StatisticsData.SORTING_POSTFIX +
                International.getMessage("{h} Uhr", hour);
    }

    private Object getAggregationKey_sessionType(String sessionType) {
        if (sessionType != null) {
            return Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, sessionType);
        }
        return null;
    }

    private Object getAggregationKey_year(DataTypeDate date) {
        if (date != null && date.isSet()) {
            return date.getYear();
        }
        return null;
    }

    private Object getAggregationKeyForList(LogbookRecord r) {
        // Note: This method only provides the aggregation key for StatisticCategory=LIST!
        switch (sr.sStatistikKey) {
            case name:
                switch (sr.sStatisticTypeEnum) {
                    case persons:
                        return getAggregationKey_persons(entryPersonRecord, entryPersonId, entryPersonName,
                                entryPersonIsGuest, entryPersonIsOther);
                    case boats:
                        return getAggregationKey_boats(entryBoatRecord, entryBoatId, entryBoatName);
                }
                break;
            case status:
                return getAggregationKey_status(entryPersonStatusId);
            case yearOfBirth:
                return getAggregationKey_yearOfBirth(entryPersonRecord);
            case gender:
                return getAggregationKey_gender(entryPersonGender);
            case boatType:
                return getAggregationKey_boatType(entryBoatType);
            case boatSeats:
                return getAggregationKey_boatSeats(entryBoatSeats);
            case boatTypeDetail:
                return getAggregationKey_boatTypeDetail(entryBoatType, entryBoatSeats, entryBoatCoxing);
            case destination:
                return getAggregationKey_destination(entryDestinationNameAndVariant);
            case waters:
                DataTypeList<String> watersText = new DataTypeList<String>();
                DataTypeList<UUID> watersList = null;
                if (entryDestinationRecord != null) {
                    watersList = entryDestinationRecord.getWatersIdList();
                }
                if (r.getWatersIdList() != null) {
                    if (watersList != null) {
                        watersList.addAll(r.getWatersIdList());
                    } else {
                        watersList = r.getWatersIdList();
                    }
                }
                if (watersList != null && watersList.length() > 0) {
                    for (int i = 0; i < watersList.length(); i++) {
                        WatersRecord w = waters.getWaters(watersList.get(i));
                        if (w != null) {
                            watersText.add(getAggregationKey_waters(w.getQualifiedName()));
                        }
                    }
                }
                if (r.getWatersNameList() != null) {
                    for (int i = 0; i < r.getWatersNameList().length(); i++) {
                        if (!watersText.contains(r.getWatersNameList().get(i))) {
                            watersText.add(getAggregationKey_waters(r.getWatersNameList().get(i)));
                        }
                    }
                }
                if (watersText.length() > 0) {
                    return watersText;
                }
                break;
            case distance:
                return getAggregationKey_distance(entryDistanceInDefaultUnit);
            case month:
                if (entryEndDate == null || !entryEndDate.isSet() ||
                    entryDate == null || !entryDate.isSet() ||
                    entryDate.isAfterOrEqual(entryEndDate)) {
                    return getAggregationKey_month(entryDate);
                }
                DataTypeDate date = new DataTypeDate(entryDate);
                long distPerDay = entryDistanceInDefaultUnit / entryEndDate.getDifferenceDays(entryDate);
                Hashtable<Object,Long> distancePerMonth = new Hashtable<Object,Long>();
                while(date.isBeforeOrEqual(entryEndDate)) {
                    Object k = getAggregationKey_month(date);
                    Long dist = distancePerMonth.get(k);
                    distancePerMonth.put(k, (dist == null ? distPerDay : dist + distPerDay));
                    date.addDays(1);
                }
                return distancePerMonth;
            case weekday:
                if (entryEndDate == null || !entryEndDate.isSet() ||
                    entryDate == null || !entryDate.isSet() ||
                    entryDate.isAfterOrEqual(entryEndDate)) {
                    return getAggregationKey_weekday(entryDate);
                }
                date = new DataTypeDate(entryDate);
                distPerDay = entryDistanceInDefaultUnit / entryEndDate.getDifferenceDays(entryDate);
                Hashtable<Object,Long> distancePerWeekday = new Hashtable<Object,Long>();
                while(date.isBeforeOrEqual(entryEndDate)) {
                    Object k = getAggregationKey_weekday(date);
                    Long dist = distancePerWeekday.get(k);
                    distancePerWeekday.put(k, (dist == null ? distPerDay : dist + distPerDay));
                    date.addDays(1);
                }
                return distancePerWeekday;
            case timeOfDay:
                long elapsed = r.getEntryElapsedTimeInMinutes();
                if (elapsed <= 0) {
                    return null;
                }
                long totalDistance = (r.getDistance() != null ? r.getDistance().getValueInDefaultUnit() : 0);
                if (totalDistance == 0) {
                    return null;
                }
                Hashtable<String,Long> timePerHour = new Hashtable<String,Long>();
                DataTypeTime time = r.getStartTime();
                long remaining = elapsed;
                while (remaining > 0) {
                    long minutes = Math.min(60 - time.getMinute(), remaining);
                    String hour = getAggregationKey_timeOfDay(time.getHour());
                    Long allMinutes = timePerHour.get(hour);
                    if (allMinutes == null) {
                        timePerHour.put(hour, minutes);
                    } else {
                        timePerHour.put(hour, allMinutes + minutes);
                    }
                    time.add(60 * (int)minutes);
                    remaining -= minutes;
                }
                String[] keys = timePerHour.keySet().toArray(new String[0]);
                Hashtable<String,Long> distancePerHour = new Hashtable<String,Long>();
                for (String k : keys) {
                    long minutes = timePerHour.get(k);
                    long distance = (long)((((double)minutes) / ((double)elapsed))  * ((double)totalDistance));
                    distancePerHour.put(k, distance);
                }
                return distancePerHour;
            case sessionType:
                return getAggregationKey_sessionType(entrySessionType);
            case year:
                return getAggregationKey_year(entryDate);
        }
        return null;
    }

    private Object getAggregationKeyForCompetition(LogbookRecord r) {
        // Note: This method only provides the aggregation key for StatisticCategory=COMPETITION!
        if (!sr.sStatisticType.equals(WettDefs.STR_DRV_WANDERRUDERSTATISTIK)) {
            return this.entryPersonId;
        } else {
            return CompetitionDRVWanderruderstatistik.getAggregationKey(r, sr.sValidAt);
        }
    }

    private Object getAggregationKeyForClubwork(ClubworkRecord r) {
        // Note: This method only provides the aggregation key for StatisticCategory=LIST!
        switch (sr.sStatistikKey) {
            case name:
                if (sr.sSumGuestsByClub && entryPersonIsGuest) {
                    String clubName = International.getString("unbekannt");
                    if (entryPersonRecord != null && entryPersonRecord.getAssocitation() != null
                            && entryPersonRecord.getAssocitation().length() > 0) {
                        clubName = entryPersonRecord.getAssocitation();
                    }
                    return StatisticsData.SORTTOEND_PREFIX + International.getMessage("Gäste von {club}", clubName);
                }
                if (sr.sSumGuestsAndOthers && (entryPersonIsGuest || entryPersonIsOther)) {
                    if (entryPersonIsGuest) {
                        return StatisticsData.SORTTOEND_PREFIX + International.getString("Gäste");
                    }
                    if (entryPersonIsOther) {
                        return StatisticsData.SORTTOEND_PREFIX + International.getString("andere");
                    }
                }
                if (entryPersonId != null) {
                    return entryPersonId;
                }
                if (entryPersonName != null) {
                    return entryPersonName;
                }
            case gender:
                if (entryPersonGender != null) {
                    return Daten.efaTypes.getValue(EfaTypes.CATEGORY_GENDER, entryPersonGender);
                }
                return EfaTypes.TEXT_UNKNOWN;
            case month:
                if (entryDate != null && entryDate.isSet()) {
                    return entryDate.getMonthAsStringWithIntMarking(StatisticsData.SORTING_PREFIX, StatisticsData.SORTING_POSTFIX);
                }
                break;
            case year:
                if (entryDate != null && entryDate.isSet()) {
                    return entryDate.getYear();
                }
                break;
        }
        return null;
    }

    private int calculateEntryForList(LogbookRecord r) {
        int cnt = 0;
        if (sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.persons) {
            for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
                getEntryPerson(r, i);
                if (entryPersonId == null && entryPersonName == null) {
                    continue;
                }
                if (isInPersonFilter() && isInGroupFilter()) {
                    Object aggregationKey = getAggregationKeyForList(r);
                    if (aggregationKey != null) {
                        if (!(aggregationKey instanceof Hashtable)) {
                            if (sr.sStatistikKey != StatisticsRecord.StatisticKey.waters) {
                                cnt += calculateAggregations(r, aggregationKey, entryDistanceInDefaultUnit, i == 0);
                            } else {
                                cnt += calculateAggregationsForList(r, (DataTypeList) aggregationKey, i == 0);
                            }
                        } else {
                            calculateAggregationsForHash(r, (Hashtable)aggregationKey, i == 0);
                        }
                    }
                }
            }
        }
        if (sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.boats) {
            if (isAtLeastOneInPersonOrGroupFilter(r)) {
                Object aggregationKey = getAggregationKeyForList(r);
                if (aggregationKey != null) {
                    if (sr.sStatistikKey != StatisticsRecord.StatisticKey.waters) {
                        cnt += calculateAggregations(r, aggregationKey, entryDistanceInDefaultUnit, false);
                    } else {
                        cnt += calculateAggregationsForList(r, (DataTypeList) aggregationKey, false);
                    }
                }
            }
        }

        return cnt;
    }

    private int calculateEntryForMatrix(LogbookRecord r) {
        int cnt = 0;
        if (sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.persons) {
            for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
                getEntryPerson(r, i);
                if (entryPersonId == null && entryPersonName == null) {
                    continue;
                }
                if (isInPersonFilter() && isInGroupFilter()) {
                    Object aggregationKey = getAggregationKey_persons(entryPersonRecord, entryPersonId, entryPersonName,
                                entryPersonIsGuest, entryPersonIsOther);
                    if (aggregationKey != null) {
                        cnt += calculateAggregationsForMatrix(r, aggregationKey,
                                entryDistanceInDefaultUnit, i);
                    }
                }
            }
        }
        if (sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.boats) {
            if (isAtLeastOneInPersonOrGroupFilter(r)) {
                Object aggregationKey = getAggregationKey_boats(entryBoatRecord, entryBoatId, entryBoatName);
                if (aggregationKey != null) {
                    cnt += calculateAggregationsForMatrix(r, aggregationKey,
                            entryDistanceInDefaultUnit, -1);
                }
            }
        }

        return cnt;
    }

    private int calculateEntryForLogbook(LogbookRecord r) {
        if (!isAtLeastOneInPersonOrGroupFilter(r)) {
            return 0;
        }
        StatisticsData sd = new StatisticsData(sr, logbook.getName() + ":" + entryNo.toString());
        sd.entryNo = entryNo;
        sd.date = entryDate;
        sd.sessions = 1; // we count every entry as one session
        int fieldCount = sr.getLogbookFieldCount();
        if (fieldCount < 2) {
            fieldCount = 2; // at least sIsLFieldsEntryNo and sIsLFieldsDate are always enabled
        }
        sd.logbookFields = new String[fieldCount];
        int col = 0;
        if (sr.sIsLFieldsEntryNo) {
            sd.logbookFields[col++] = entryNo.toString();
        }
        if (sr.sIsLFieldsDate) {
            sd.logbookFields[col++] = entryDate.toString();
        }
        if (sr.sIsLFieldsEndDate) {
            sd.logbookFields[col++] = (entryEndDate != null && entryEndDate.isSet() ? entryEndDate.toString() : "");
        }
        if (sr.sIsLFieldsBoat) {
            sd.logbookFields[col++] = (entryBoatRecord != null ? entryBoatRecord.getQualifiedName() : entryBoatName);
        }
        String coxName = "";
        String crewNames = "";
        for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
            getEntryPerson(r, i);
            String name = (entryPersonRecord != null ? entryPersonRecord.getQualifiedName() : entryPersonName);
            if (name == null) {
                name = "";
            }
            if (sr.sPublicStatistic && entryPersonExcludeFromPublic) {
                name = "<" + International.getString("anonym") + ">"; // < and > will be correctly escaped in output!
            }
            if (sr.sOutputType == StatisticsRecord.OutputTypes.html
                    || sr.sOutputType == StatisticsRecord.OutputTypes.internal) {
                if (i == r.getBoatCaptainPosition()) {
                    name = StatisticWriter.TEXTMARK_BOLDSTART + name + StatisticWriter.TEXTMARK_BOLDEND;
                }
            }
            if (i == 0) {
                coxName = name;
            } else {
                crewNames = (name.length() > 0
                        ? (crewNames == null || crewNames.length() == 0 ? name
                        : crewNames + "; " + name) : crewNames);
            }
        }
        if (sr.sIsLFieldsCox) {
            sd.logbookFields[col++] = coxName;
        }
        if (sr.sIsLFieldsCrew) {
            sd.logbookFields[col++] = crewNames;
        }
        if (sr.sIsLFieldsStartTime) {
            sd.logbookFields[col++] = (r.getStartTime() != null ? r.getStartTime().toString(false) : "");
        }
        if (sr.sIsLFieldsEndTime) {
            sd.logbookFields[col++] = (r.getEndTime() != null ? r.getEndTime().toString(false) : "");
        }
        if (sr.sIsLFieldsWaters) {
            String w = null;
            if (entryDestinationRecord != null) {
                w = DestinationRecord.getWatersNamesStringList(
                        r.getPersistence().getProject().getWaters(false),
                        entryDestinationRecord.getWatersIdList(),
                        r.getWatersIdList(), r.getWatersNameList());
            } else {
                w = DestinationRecord.getWatersNamesStringList(
                        r.getPersistence().getProject().getWaters(false),
                        null,
                        r.getWatersIdList(), r.getWatersNameList());
            }
            sd.logbookFields[col++] = (w != null ? w : "");
        }
        if (sr.sIsLFieldsDestination) {
            sd.logbookFields[col++] = r.getDestinationAndVariantName(entryValidAt);
        }
        if (sr.sIsLFieldsDestinationDetails) {
            sd.logbookFields[col++] = (entryDestinationRecord != null
                    ? entryDestinationRecord.getDestinationDetailsAsString() : "");
        }
        if (sr.sIsLFieldsDestinationAreas) {
            sd.logbookFields[col++] = (entryDestinationAreas != null
                    ? entryDestinationAreas.toString() : "");
        }
        if (sr.sIsLFieldsDistance) {
            sd.logbookFields[col++] = (r.getDistance() != null
                    ? r.getDistance().getStringValueInDefaultUnit(sr.sDistanceWithUnit, 0, 3) : "");
            // we update the sd.distance because we use this field to summarize all output data
            sd.distance = (r.getDistance() != null
                    ? r.getDistance().getValueInDefaultUnit() : 0);
        }
        if (sr.sIsLFieldsMultiDay) {
            sd.logbookFields[col++] = (entrySessionGroup != null
                    ? entrySessionGroup.getSessionTypeDescription() : "");
        }
        if (sr.sIsLFieldsSessionType) {
            sd.logbookFields[col++] = (entrySessionType != null
                    ? Daten.efaTypes.getValue(EfaTypes.CATEGORY_SESSION, entrySessionType) : "");
        }
        if (sr.sIsLFieldsNotes) {
            sd.logbookFields[col++] = (r.getComments() != null ? r.getComments() : "");
        }
        data.put(sd.key, sd);
        return 1;
    }

    private int calculateEntryForCompetition(LogbookRecord r) {
        int cnt = 0;
        for (int i = 0; i <= LogbookRecord.CREW_MAX; i++) {
            getEntryPerson(r, i);
            if (entryPersonRecord == null) {
                continue;
            }
            if (entryPersonRecord.getExcludeFromCompetition()) {
                sr.pStatIgnored.put((entryPersonRecord != null
                        ? entryPersonRecord.getQualifiedName() : entryPersonName), "foo");
                continue;
            }
            if (isInPersonFilter() && isInGroupFilter()) {
                // Statistics for Competitions are only calculated based on known Names
                Object aggregationKey = getAggregationKeyForCompetition(r);
                if (aggregationKey != null) {
                    if (!sr.sStatisticType.equals(WettDefs.STR_DRV_WANDERRUDERSTATISTIK)) {
                        cnt += calculateAggregations(r, aggregationKey, entryDistanceInDefaultUnit, i == 0);
                    } else {
                        if (sr.cCompetition != null) {
                            cnt += ((CompetitionDRVWanderruderstatistik) sr.cCompetition).calculateAggregation(data,
                                    r, aggregationKey, entryPersonRecord);
                        }
                    }
                }
            }
        }

        return cnt;
    }

    private void calculateEntry(LogbookRecord r) {
        resetEntryValues();
        getEntryBasic(r);
        if (!isInRange(r)
                || !isInFilter(r)
                || r.getSessionIsOpen()) {
            if (Logger.isTraceOn(Logger.TT_STATISTICS, 5)) {
                Logger.log(Logger.DEBUG, Logger.MSG_STAT_IGNOREDENTRIES, "ignored (1): " + r.toString());
            }
            return;
        }

        // update date range of evaluated entries
        if (entryDate != null && entryDate.isSet()) {
            if (sr.cEntryDateFirst == null || entryDate.isBefore(sr.cEntryDateFirst)) {
                sr.cEntryDateFirst = entryDate;
            }
            if (sr.cEntryDateLast == null || entryDate.isAfter(sr.cEntryDateLast)) {
                sr.cEntryDateLast = entryDate;
            }
            if (entryEndDate != null && entryEndDate.isSet() && (sr.cEntryDateLast == null || entryEndDate.isAfter(sr.cEntryDateLast))) {
                sr.cEntryDateLast = entryEndDate;
            }
        }

        // update entryno for first and last evaluated entry
        if (entryNo != null) {
            if (sr.cEntryNoFirst == null || entryNo.intValue() < sr.cEntryNoFirst.intValue()) {
                sr.cEntryNoFirst = entryNo;
            }
            if (sr.cEntryNoLast == null || entryNo.intValue() > sr.cEntryNoLast.intValue()) {
                sr.cEntryNoLast = entryNo;
            }
        }

        // get further data
        if (sr.sIsAggrZielfahrten
                || sr.sStatisticCategory == StatisticsRecord.StatisticCategory.logbook
                || sr.sStatistikKey == StatisticsRecord.StatisticKey.destination
                || sr.sStatistikKey == StatisticsRecord.StatisticKey.waters) {
            getEntryDestination(r);
        }

        if (!getEntryDistance(r)) {
            if (Logger.isTraceOn(Logger.TT_STATISTICS, 5)) {
                Logger.log(Logger.DEBUG, Logger.MSG_STAT_IGNOREDENTRIES, "ignored (2): " + r.toString());
            }
            return;
        }

        int cnt = 0;
        switch (sr.sStatisticCategory) {
            case list:
                cnt += calculateEntryForList(r);
                break;
            case matrix:
                cnt += calculateEntryForMatrix(r);
                break;
            case logbook:
                cnt += calculateEntryForLogbook(r);
                break;
            case competition:
                cnt += calculateEntryForCompetition(r);
                break;
        }

        if (cnt > 0) {
            // update number of evaluated entries
            sr.cNumberOfEntries++;
            if (Logger.isTraceOn(Logger.TT_STATISTICS, 5)) {
                Logger.log(Logger.DEBUG, Logger.MSG_STAT_CALCULATEDENTRIES, "calculated: " + r.toString());
            }
        } else {
            if (Logger.isTraceOn(Logger.TT_STATISTICS, 5)) {
                Logger.log(Logger.DEBUG, Logger.MSG_STAT_IGNOREDENTRIES, "ignored: " + r.toString());
            }
        }
    }

    private void calculateEntryForClubwork(ClubworkRecord r) {
        resetEntryValues();
        getEntryBasic(r);
        if (!isInRange(r) //||
                /*TODO: !isInFilter(r) ||
                r.getSessionIsOpen()*/) {
            if (Logger.isTraceOn(Logger.TT_STATISTICS, 5)) {
                Logger.log(Logger.DEBUG, Logger.MSG_STAT_IGNOREDENTRIES, "ignored (1): " + r.toString());
            }
            return;
        }

        // update date range of evaluated entries
        if (entryDate != null && entryDate.isSet()) {
            if (sr.cEntryDateFirst == null || entryDate.isBefore(sr.cEntryDateFirst)) {
                sr.cEntryDateFirst = entryDate;
            }
            if (sr.cEntryDateLast == null || entryDate.isAfter(sr.cEntryDateLast)) {
                sr.cEntryDateLast = entryDate;
            }
        }

        // update entryno for first and last evaluated entry
        if (entryNo != null) {
            if (sr.cEntryNoFirst == null/* || entryNo.intValue() < sr.cEntryNoFirst.intValue()*/) {
                sr.cEntryNoFirst = entryNo;
            }
            //if (sr.cEntryNoLast == null || entryNo.intValue() > sr.cEntryNoLast.intValue()) {
            sr.cEntryNoLast = entryNo;
            //}
        }

        getEntryPerson(r);
        if (entryPersonId == null && entryPersonName == null) {
            if (Logger.isTraceOn(Logger.TT_STATISTICS, 5)) {
                Logger.log(Logger.DEBUG, Logger.MSG_STAT_IGNOREDENTRIES, "ignored (2): " + r.toString());
            }
            return;
        }
        int cnt = 0;
        if (isInPersonFilter() && isInGroupFilter()) {
            Object aggregationKey = getAggregationKeyForClubwork(r);
            if (aggregationKey != null) {
                if (sr.sStatistikKey != StatisticsRecord.StatisticKey.waters) {
                    cnt = calculateAggregations(r, aggregationKey, r.getHours());
                }
            }
        }

        if (cnt > 0) {
            // update number of evaluated entries
            sr.cNumberOfEntries++;
            if (Logger.isTraceOn(Logger.TT_STATISTICS, 5)) {
                Logger.log(Logger.DEBUG, Logger.MSG_STAT_CALCULATEDENTRIES, "calculated: " + r.toString());
            }
        }
    }

    private void getEntryBasic(LogbookRecord r) {
        entryNo = r.getEntryId();
        entryValidAt = r.getValidAtTimestamp();
    }

    private void getEntryBasic(ClubworkRecord r) {
        entryNo = new DataTypeIntString((sr.cNumberOfEntries + 1) + ""); // TODO: new DataTypeIntString(r.getQualifiedName());
        entryValidAt = r.getWorkDate().getTimestamp(new DataTypeTime(0, 0, 0));
    }

    private void getEntryDates(LogbookRecord r) {
        entryDate = r.getDate();
        entryEndDate = r.getEndDate();
        entryNumberOfDays = 1;
        getSessionGroup(r);
        if (entryDate != null && entryDate.isSet()
                && entryEndDate != null && entryEndDate.isSet()) {
            entryNumberOfDays = entryEndDate.getDifferenceDays(entryDate) + 1;
            if (entryNumberOfDays > 1) {
                // Session Groups's 'active days' are only considered if this
                // entry is a multi-day entry with same dates as the session group.
                // If this entry does not have an end date, then it's just a single day
                // trip as part of a session group, and therefore active days does not matter.
                if (entrySessionGroup != null
                        && entrySessionGroup.checkLogbookRecordFitsIntoRange(r)
                        && entryDate.equals(entrySessionGroup.getStartDate())
                        && entryEndDate.equals(entrySessionGroup.getEndDate())) {
                    entryNumberOfDays = entrySessionGroup.getActiveDays();
                }
            }
        }
    }

    private void getEntryDates(ClubworkRecord r) {
        entryDate = r.getWorkDate();
        entryNumberOfDays = 1;
    }

    private void getSessionGroup(LogbookRecord r) {
        entrySessionGroup = r.getSessionGroup();
    }

    private void getEntrySessionType(LogbookRecord r) {
        entrySessionType = r.getSessionType();
        if (entrySessionType == null) {
            entrySessionType = EfaTypes.TYPE_SESSION_NORMAL;
        }
    }

    private void getEntryBoat(LogbookRecord r) {
        entryBoatId = r.getBoatId();
        entryBoatRecord = (entryBoatId != null ? boats.getBoat(entryBoatId, entryValidAt) : null);
        entryBoatName = (entryBoatId != null ? null : r.getBoatName());
        int boatVariant = r.getBoatVariant();
        int vidx = -1;
        if (entryBoatRecord != null) {
            if (entryBoatRecord.getNumberOfVariants() == 1) {
                vidx = 0;
            } else {
                vidx = entryBoatRecord.getVariantIndex(boatVariant);
            }
        }
        if (vidx >= 0) {
            entryBoatType = entryBoatRecord.getTypeType(vidx);
            entryBoatSeats = entryBoatRecord.getTypeSeats(vidx);
            entryBoatRigging = entryBoatRecord.getTypeRigging(vidx);
            entryBoatCoxing = entryBoatRecord.getTypeCoxing(vidx);
        }
        if (entryBoatType == null) {
            entryBoatType = EfaTypes.TYPE_BOAT_OTHER;
        }
        if (entryBoatSeats == null) {
            entryBoatSeats = EfaTypes.TYPE_NUMSEATS_OTHER;
        }
        if (entryBoatRigging == null) {
            entryBoatRigging = EfaTypes.TYPE_RIGGING_OTHER;
        }
        if (entryBoatCoxing == null) {
            entryBoatCoxing = EfaTypes.TYPE_COXING_OTHER;
        }
        entryBoatOwner = StatisticsRecord.BOWNER_UNKNOWN;
        if (entryBoatRecord != null) {
            if (entryBoatRecord.getOwner() == null || entryBoatRecord.getOwner().length() == 0) {
                entryBoatOwner = StatisticsRecord.BOWNER_OWN;
            } else {
                entryBoatOwner = StatisticsRecord.BOWNER_OTHER;
            }
        }
        entryBoatExclude = (entryBoatRecord != null && entryBoatRecord.getExcludeFromPublicStatistics()
                && sr.getPubliclyAvailable());
    }

    private void getEntryPerson(LogbookRecord r, int pos) {
        entryPersonId = r.getCrewId(pos);
        entryPersonRecord = (entryPersonId != null ? persons.getPerson(entryPersonId, entryValidAt) : null);
        entryPersonName = (entryPersonId != null ? null : r.getCrewName(pos));
        entryPersonStatusId = (entryPersonRecord != null ? entryPersonRecord.getStatusId() : null);
        entryPersonIsGuest = (entryPersonStatusId != null && entryPersonStatusId.equals(this.statusGuest.getId()));
        entryPersonIsOther = (entryPersonStatusId == null || entryPersonStatusId.equals(this.statusOther.getId()));
        entryPersonGender = (entryPersonRecord != null ? entryPersonRecord.getGender() : null);
        entryPersonExcludeFromPublic = (entryPersonRecord != null && entryPersonRecord.getExcludeFromPublicStatistics()
                && sr.getPubliclyAvailable());
    }

    private void getEntryPerson(ClubworkRecord r) {
        entryPersonId = r.getPersonId();
        entryPersonRecord = (entryPersonId != null ? persons.getPerson(entryPersonId, entryValidAt) : null);
        entryPersonName = (entryPersonId != null ? null : entryPersonRecord.getFirstLastName());
        entryPersonStatusId = (entryPersonRecord != null ? entryPersonRecord.getStatusId() : null);
        entryPersonIsGuest = (entryPersonStatusId != null && entryPersonStatusId.equals(this.statusGuest.getId()));
        entryPersonIsOther = (entryPersonStatusId == null || entryPersonStatusId.equals(this.statusOther.getId()));
        entryPersonGender = (entryPersonRecord != null ? entryPersonRecord.getGender() : null);
        entryPersonExcludeFromPublic = (entryPersonRecord != null && entryPersonRecord.getExcludeFromPublicStatistics()
                && sr.getPubliclyAvailable());
    }

    private void getEntryDestination(LogbookRecord r) {
        entryDestinationId = r.getDestinationId();
        entryDestinationRecord = (entryDestinationId != null ? destinations.getDestination(entryDestinationId, entryValidAt) : null);
        entryDestinationVariant = (entryDestinationId != null ? r.getDestinationVariantName() : null);
        entryDestinationName = (entryDestinationId != null ? null : r.getDestinationName());
        entryDestinationNameAndVariant = r.getDestinationAndVariantName(entryValidAt);
        entryDestinationAreas = (entryDestinationRecord != null ? entryDestinationRecord.getDestinationAreas() : null);
    }

    private boolean getEntryDistance(LogbookRecord r) {
        entryDistanceInDefaultUnit = (r.getDistance() != null
                ? r.getDistance().getValueInDefaultUnit() : 0);
        return entryDistanceInDefaultUnit != 0;
    }

    private boolean isInRange(LogbookRecord r) {
        getEntryDates(r);
        if (entryDate == null || !entryDate.isSet()) {
            return false;
        }
        if (entryEndDate == null || !entryEndDate.isSet()) {
            return entryDate.isInRange(sr.sStartDate, sr.sEndDate);
        } else {
            return entryDate.isInRange(sr.sStartDate, sr.sEndDate) && // both start *and* end date must be in range!
                    entryEndDate.isInRange(sr.sStartDate, sr.sEndDate);
        }
    }

    private boolean isInRange(ClubworkRecord r) {
        getEntryDates(r);
        if (entryDate == null || !entryDate.isSet()) {
            return false;
        } else {
            return entryDate.isInRange(sr.sStartDate, sr.sEndDate);
        }
    }

    private boolean isInFilter(LogbookRecord r) {
        getEntrySessionType(r);
        if (!sr.sFilterSessionTypeAll && !sr.sFilterSessionType.containsKey(entrySessionType)) {
            return false;
        }

        getEntryBoat(r);
        if (!isInBoatFilter(r)) {
            return false;
        }

        if (sr.sFilterFromToBoathouse) {
            getEntryDestination(r);
            if (entryDestinationRecord == null || !entryDestinationRecord.getStartIsBoathouse() ||
                !entryDestinationRecord.getRoundtrip()) {
                return false;
            }
        }

        if (sr.sPublicStatistic && entryBoatExclude && 
                (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.logbook ||
                (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list &&
                 sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.boats))) {
            // exclude in all public logbook or boat list statistics
            // ... but include in person statistics and competitions
            if (isAtLeastOneInPersonOrGroupFilter(r)) {
                sr.pStatIgnored.put((entryBoatRecord != null
                        ? entryBoatRecord.getQualifiedName() : entryBoatName), "foo");
            }
            return false;
        }

        return true;
    }

    private boolean isInBoatFilter(LogbookRecord r) {
        return isInBoatFilter(entryBoatRecord, r.getBoatAsName(),
                entryBoatType, entryBoatSeats, entryBoatRigging,
                entryBoatCoxing, entryBoatOwner);
    }

    private boolean isInBoatFilter(BoatRecord r, String boatName,
            String boatType, String boatSeats, String boatRigging,
            String boatCoxing, String boatOwner) {
        if ((!sr.sFilterBoatTypeAll && !sr.sFilterBoatType.containsKey(boatType))
                || (!sr.sFilterBoatSeatsAll && !sr.sFilterBoatSeats.containsKey(boatSeats))
                || (!sr.sFilterBoatRiggingAll && !sr.sFilterBoatRigging.containsKey(boatRigging))
                || (!sr.sFilterBoatCoxingAll && !sr.sFilterBoatCoxing.containsKey(boatCoxing))
                || (!sr.sFilterBoatOwnerAll && !sr.sFilterBoatOwner.containsKey(boatOwner))) {
            return false;
        }

        if (sr.sFilterByBoatId != null && !sr.sFilterByBoatId.equals(r.getId())) {
            return false;
        }
        if (sr.sFilterByBoatText != null && !sr.sFilterByBoatText.equals(boatName)) {
            return false;
        }

        return true;
    }

    private boolean isInPersonFilter() {
        return isInPersonFilter(entryPersonRecord, entryPersonStatusId, entryPersonGender,
                entryPersonExcludeFromPublic, entryPersonName);
    }

    private boolean isInPersonFilter(PersonRecord p, UUID statusId, String gender,
            boolean excludeFromPublic, String personName) {
        if (p != null) {
            // known person
            if (!sr.sFilterStatusAll
                    && (statusId == null || !sr.sFilterStatus.containsKey(statusId))) {
                return false;
            }
            if (!sr.sFilterGenderAll
                    && (gender == null || !sr.sFilterGender.containsKey(gender))) {
                return false;
            }
            if (sr.sFilterByPersonId != null && !sr.sFilterByPersonId.equals(p.getId())) {
                return false;
            }
            if (sr.sFilterByPersonText != null && !sr.sFilterByPersonText.equals(p.getQualifiedName())) {
                return false;
            }
            if (sr.sPublicStatistic && excludeFromPublic &&
                    (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.competition ||
                     (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list &&
                      sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.persons))) {
                // exclude in all public competitions and person list statistics
                // ... but include in boat statistics and logbooks (as anonymous)
                sr.pStatIgnored.put((p != null
                        ? p.getQualifiedName() : personName), "foo");
                return false;
            }
            return true;
        } else {
            // unknown person
            if (entryPersonName == null) {
                return false;
            }
            if (!sr.sFilterStatusAll && !sr.sFilterStatusOther) {
                return false;
            }
            if (!sr.sFilterGenderAll) {
                return false; // both MALE and FEMALE must be selected
            }
            if (sr.sFilterByPersonId != null) {
                return false;
            }
            if (sr.sFilterByPersonText != null && !sr.sFilterByPersonText.equals(entryPersonName)) {
                return false;
            }
            return true;
        }
    }

    private boolean isInGroupFilter() {
        return isInGroupFilter(entryPersonRecord, entryValidAt);
    }

    private boolean isInGroupFilter(PersonRecord p, long validAt) {
        if (sr.sFilterByGroupId != null) {
            if (p == null) {
                return false;
            }
            GroupRecord gr = groups.findGroupRecord(sr.sFilterByGroupId, validAt);
            if (gr == null) {
                return false;
            }
            DataTypeList<UUID> glist = gr.getMemberIdList();
            if (glist == null) {
                return false;
            }
            return glist.contains(p.getId());
        }
        return true;
    }

    private boolean isAtLeastOneInPersonOrGroupFilter(LogbookRecord r) {
        boolean isAtLeastOnePersonInFilter = false;
        for (int i = 0; !isAtLeastOnePersonInFilter && i <= LogbookRecord.CREW_MAX; i++) {
            getEntryPerson(r, i);
            if (entryPersonId == null && entryPersonName == null) {
                continue;
            }
            if (isInPersonFilter() && isInGroupFilter()) {
                isAtLeastOnePersonInFilter = true;
            }
        }
        return isAtLeastOnePersonInFilter;
    }

    private Vector<Logbook> getAllLogbooks() {
        Vector<Logbook> logbooks = new Vector<Logbook>();
        if (Daten.project == null) {
            return logbooks;
        }
        String[] names = Daten.project.getAllLogbookNames();
        for (int i = 0; names != null && i < names.length; i++) {
            ProjectRecord pr = Daten.project.getLoogbookRecord(names[i]);
            if (pr != null) {
                DataTypeDate lbStart = pr.getStartDate();
                DataTypeDate lbEnd = pr.getEndDate();
                if (lbStart == null || lbEnd == null) {
                    continue; // should never happen
                }
                if (DataTypeDate.isRangeOverlap(lbStart, lbEnd, sr.sStartDate, sr.sEndDate)) {
                    Logbook l = Daten.project.getLogbook(names[i], false);
                    if (l != null) {
                        logbooks.add(l);
                    }
                }
            }
        }
        return logbooks;
    }

    private void runPreprocessing() {
        try {
            DataKeyIterator it;
            if (sr.sListAllNullEntries) {
                if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list ||
                    sr.sStatisticCategory == StatisticsRecord.StatisticCategory.matrix ||
                    sr.sStatisticCategory == StatisticsRecord.StatisticCategory.clubwork) {
                    switch (sr.sStatistikKey) {
                        case name:
                            switch (sr.sStatisticTypeEnum) {
                                case persons:
                                    it = persons.data().getStaticIterator();
                                    for (DataKey k = it.getFirst(); k != null; k = it.getNext()) {
                                        PersonRecord p = (PersonRecord) persons.data().get(k);
                                        if (!p.isValidAt(sr.sValidAt)) {
                                            continue;
                                        }
                                        if (isInPersonFilter(p, p.getStatusId(), p.getGender(),
                                                p.getExcludeFromPublicStatistics(), p.getQualifiedName())) {
                                            Object key = getAggregationKey_persons(p, p.getId(),
                                                    p.getQualifiedName(),
                                                    p.getStatusId().equals(statusGuest.getId()),
                                                    false);
                                            if (key != null) {
                                                data.put(key, new StatisticsData(sr, key));
                                            }
                                        }
                                    }
                                    break;
                                case boats:
                                    it = boats.data().getStaticIterator();
                                    for (DataKey k = it.getFirst(); k != null; k = it.getNext()) {
                                        BoatRecord b = (BoatRecord) boats.data().get(k);
                                        if (!b.isValidAt(sr.sValidAt)) {
                                            continue;
                                        }
                                        if (isInBoatFilter(b, b.getQualifiedName(),
                                                b.getTypeType(0), b.getTypeSeats(0),
                                                b.getTypeRigging(0), b.getTypeCoxing(0),
                                                b.getOwner())) {
                                            Object key = getAggregationKey_boats(b, b.getId(),
                                                    b.getQualifiedName());
                                            if (key != null) {
                                                data.put(key, new StatisticsData(sr, key));
                                            }
                                        }
                                    }
                                    break;
                            }
                            break;
                        case status:
                            UUID[] allStatus = sr.sFilterStatus.keySet().toArray(new UUID[0]);
                            for (UUID id : allStatus) {
                                Object key = this.getAggregationKey_status(id);
                                if (key != null) {
                                    data.put(key, new StatisticsData(sr, key));
                                }
                            }
                            break;
                        case yearOfBirth:
                            it = persons.data().getStaticIterator();
                            for (DataKey k = it.getFirst(); k != null; k = it.getNext()) {
                                PersonRecord p = (PersonRecord) persons.data().get(k);
                                if (!p.isValidAt(sr.sValidAt)) {
                                    continue;
                                }
                                if (isInPersonFilter(p, p.getStatusId(), p.getGender(),
                                        p.getExcludeFromPublicStatistics(), p.getQualifiedName())) {
                                    Object key = this.getAggregationKey_yearOfBirth(p);
                                    if (key != null) {
                                        data.put(key, new StatisticsData(sr, key));
                                    }
                                }
                            }
                            break;
                        case gender:
                            String[] allGender = sr.sFilterGender.keySet().toArray(new String[0]);
                            for (String gender : allGender) {
                                Object key = this.getAggregationKey_gender(gender);
                                if (key != null) {
                                    data.put(key, new StatisticsData(sr, key));
                                }
                            }
                            break;
                        case boatType:
                            for (String t : Daten.efaTypes.getValueArray(EfaTypes.CATEGORY_BOAT)) {
                                data.put(t, new StatisticsData(sr, t));
                            }
                            break;
                        case boatSeats:
                            for (String t : Daten.efaTypes.getValueArray(EfaTypes.CATEGORY_NUMSEATS)) {
                                data.put(t, new StatisticsData(sr, t));
                            }
                            break;
                        case boatTypeDetail:
                            it = boats.data().getStaticIterator();
                            for (DataKey k = it.getFirst(); k != null; k = it.getNext()) {
                                BoatRecord b = (BoatRecord) boats.data().get(k);
                                if (!b.isValidAt(sr.sValidAt)) {
                                    continue;
                                }
                                if (isInBoatFilter(b, b.getQualifiedName(),
                                        b.getTypeType(0), b.getTypeSeats(0),
                                        b.getTypeRigging(0), b.getTypeCoxing(0),
                                        b.getOwner())) {
                                    Object key = getAggregationKey_boatTypeDetail(
                                            b.getTypeType(0), b.getTypeSeats(0), b.getTypeCoxing(0));
                                    if (key != null) {
                                        data.put(key, new StatisticsData(sr, key));
                                    }
                                }
                            }
                            break;
                        case destination:
                            it = destinations.data().getStaticIterator();
                            for (DataKey k = it.getFirst(); k != null; k = it.getNext()) {
                                DestinationRecord d = (DestinationRecord) destinations.data().get(k);
                                if (!d.isValidAt(sr.sValidAt)) {
                                    continue;
                                }
                                Object key = getAggregationKey_destination(d.getQualifiedName());
                                if (key != null) {
                                    data.put(key, new StatisticsData(sr, key));
                                }
                            }
                            break;
                        case waters:
                            it = waters.data().getStaticIterator();
                            for (DataKey k = it.getFirst(); k != null; k = it.getNext()) {
                                WatersRecord w = (WatersRecord) waters.data().get(k);
                                Object key = getAggregationKey_waters(w.getQualifiedName());
                                if (key != null) {
                                    data.put(key, new StatisticsData(sr, key));
                                }
                            }
                            break;
                        case distance:
                            // nothing to do
                            break;
                        case month:
                            for (int i=1; i<=12; i++) {
                                Object key = getAggregationKey_month(new DataTypeDate(1,i,2012));
                                data.put(key, new StatisticsData(sr, key));
                            }
                            break;
                        case weekday:
                            for (int i=1; i<=7; i++) {
                                Object key = getAggregationKey_weekday(new DataTypeDate(i,1,2012));
                                data.put(key, new StatisticsData(sr, key));
                            }
                            break;
                        case timeOfDay:
                            for (int i=0; i<=23; i++) {
                                Object key = getAggregationKey_timeOfDay(i);
                                data.put(key, new StatisticsData(sr, key));
                            }
                            break;
                        case sessionType:
                            for (String t : Daten.efaTypes.getValueArray(EfaTypes.CATEGORY_SESSION)) {
                                data.put(t, new StatisticsData(sr, t));
                            }
                            break;
                        case year:
                            // nothing to do
                            break;
                    }
                }
            }
        } catch (Exception e) {
            Logger.logdebug(e);
        }
    }

    private StatisticsData[] runPostprocessing() {
        logInfo(International.getString("Aufbereiten der Daten") + " ...\n");
        int workBeforePostprocessing = this.getCurrentWorkDone();

        String statusOtherText;
        try {
            statusOtherText = persons.getProject().getStatus(false).getStatusOther().getQualifiedName();
        } catch (Exception eignore) {
            statusOtherText = International.getString("andere");
        }


        StatisticsData sdSummary = new StatisticsData(sr, null);
        sdSummary.isSummary = true;
        sdSummary.sName = International.getString("gesamt") + " (" + data.size() + ")";
        StatisticsData sdMaximum = new StatisticsData(sr, null);
        sdMaximum.isMaximum = true;

        Object[] keys = data.keySet().toArray();
        for (int i = 0; i < keys.length; i++) {
            StatisticsData sd = data.get(keys[i]);
            boolean isUUID = false;

            // replace UUID by Person Name
            if ((sr.sStatisticCategory == StatisticsRecord.StatisticCategory.competition
                    && !sr.sStatisticType.equals(WettDefs.STR_DRV_WANDERRUDERSTATISTIK))
                    || (sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.persons
                    && sr.sStatistikKey == StatisticsRecord.StatisticKey.name)
                    || sr.sStatisticCategory == StatisticsRecord.StatisticCategory.clubwork
                    || (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.matrix &&
                        sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.persons)) {
                PersonRecord pr = null;
                if (sd.key instanceof UUID) {
                    pr = persons.getPerson((UUID) sd.key, sr.sTimestampBegin, sr.sTimestampEnd, sr.sValidAt);
                    isUUID = true;
                    sd.personRecord = pr;
                }
                if (sr.sIsFieldsName) {
                    sd.sName = (pr != null ? pr.getQualifiedName()
                            : (isUUID ? "*** " + International.getString("ungültiger Eintrag") + " ***" : sd.key.toString()));
                }
                if (sr.sIsFieldsGender) {
                    sd.sGender = (pr != null ? pr.getGenderAsString() : null);
                    if (sd.sGender == null) {
                        sd.sGender = International.getString("unbekannt");
                    }
                }
                if (sr.sIsFieldsStatus) {
                    sd.sStatus = (pr != null ? pr.getStatusName() : statusOtherText);
                }
                if (sr.sIsFieldsYearOfBirth) {
                    DataTypeDate birthday = (pr != null ? pr.getBirthday() : null);
                    if (birthday != null && birthday.isSet()) {
                        sd.sYearOfBirth = Integer.toString(birthday.getYear());
                    }
                }
                if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.competition && pr != null) {
                    sd.gender = pr.getGender();
                    sd.disabled = pr.getDisability();
                }
            }

            // replace UUID by Boat Name
            if ((sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.boats
                    && sr.sStatistikKey == StatisticsRecord.StatisticKey.name)
                    || (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.matrix &&
                        sr.sStatisticTypeEnum == StatisticsRecord.StatisticType.boats)) {

                BoatRecord br = null;
                if (sd.key instanceof UUID) {
                    br = boats.getBoat((UUID) sd.key, sr.sTimestampBegin, sr.sTimestampEnd, sr.sValidAt);
                    isUUID = true;
                }
                if (sr.sIsFieldsName) {
                    sd.sName = (br != null ? br.getQualifiedName()
                            : (isUUID ? "*** " + International.getString("ungültiger Eintrag") + " ***" : sd.key.toString()));
                }
                if (sr.sIsFieldsBoatType) {
                    sd.sBoatType = (br != null ? br.getDetailedBoatType(0) : Daten.efaTypes.getValue(EfaTypes.CATEGORY_BOAT, EfaTypes.TYPE_BOAT_OTHER));
                }

            }

            // use Key as Name for any other Data
            if (sd.sName == null) {
                sd.sName = sd.key.toString();
            }

            // Calculate Summary and Maximum
            sdSummary.updateSummary(sd);
            sdMaximum.updateMaximum(sd);
        }
        setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING / 5) * 1);

        // summary for Logbook
        if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.logbook) {
            if (sdSummary.logbookFields == null) {
                sdSummary.logbookFields = new String[sr.getLogbookFieldCount()];
            }
            sdSummary.logbookFields[0] = sdSummary.sName;
            if (sr.sLFieldDistancePos >= 0 && sr.sLFieldDistancePos < sdSummary.logbookFields.length) {
                sdSummary.logbookFields[sr.sLFieldDistancePos] =
                        DataTypeDistance.getDistance(sdSummary.distance).getStringValueInDefaultUnit(sr.sDistanceWithUnit, 0,
                        (sr.sTruncateDistanceToFullValue ? 0 : 1));
            }
        }


        // Create Array and sort
        StatisticsData[] sdArray = new StatisticsData[keys.length + 2];
        for (int i = 0; i < keys.length; i++) {
            sdArray[i] = data.get(keys[i]);
        }
        Arrays.sort(sdArray, 0, keys.length);
        sdArray[sdArray.length - 2] = sdSummary;
        sdArray[sdArray.length - 1] = sdMaximum;
        setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING / 5) * 2);

        // Calculate String Output Values
        for (int i = 0; i < sdArray.length-1; i++) { // don't do for maximum!
            sdArray[i].createStringOutputValues(sr, i,
                    (i > 0 && sdArray[i].compareTo(sdArray[i - 1], false) == 0 ? sdArray[i - 1].sPosition : Integer.toString(i + 1) + "."));
            if (sdArray[i].matrixData != null) {
                Object[] mkeys = sdArray[i].matrixData.keySet().toArray();
                for (Object mk : mkeys) {
                    StatisticsData sdm = sdArray[i].matrixData.get(mk);
                    sdm.createStringOutputValues(sr, -1, null);
                }
            }
        }
        setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING / 5) * 3);

        if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.competition) {
            if (sr.cCompetition != null) {
                sr.cCompetition.calculate(sr, sdArray);
            }
        }

        sr.pParentDialog = this.progressDialog;

        // Statistics Base Data
        String statDescrShort;
        String statDescrLong;
        switch (sr.sStatisticCategory) {
            case list:
            case matrix:
                statDescrShort = sr.getStatisticCategoryDescription() + " "
                        + (sr.sStatistikKey == StatisticsRecord.StatisticKey.name
                        ? sr.getStatisticTypeDescription() : sr.getStatisticKeyDescriptionPlural());
                statDescrLong = statDescrShort
                        + (sr.sStatistikKey != StatisticsRecord.StatisticKey.name
                        ? " (" + sr.getStatisticTypeDescription() + ")" : "");
                break;
            case logbook:
                statDescrShort = sr.getStatisticCategoryDescription();
                statDescrLong = statDescrShort;
                break;
            case competition:
                statDescrShort = sr.getStatisticTypeDescription();
                statDescrLong = statDescrShort;
                break;
            default:
                statDescrShort = International.getString("Statistik");
                statDescrLong = statDescrShort;
        }
        sr.pStatTitle = statDescrShort;
        if (sr.cEntryDateFirst != null && sr.cEntryDateFirst.isSet()) {
            sr.pStatTitle += " " + sr.cEntryDateFirst.getYear();
            if (sr.cEntryDateLast != null && sr.cEntryDateLast.isSet() && sr.cEntryDateFirst.getYear() != sr.cEntryDateLast.getYear()) {
                sr.pStatTitle += " - " + sr.cEntryDateLast.getYear();
            }
        }
        sr.pStatCreationDate = EfaUtil.getCurrentTimeStampDD_MM_YYYY();
        sr.pStatCreatedByUrl = Daten.EFAURL;
        sr.pStatCreatedByName = Daten.EFA_LONGNAME + " " + Daten.VERSION;
        sr.pStatDescription = statDescrLong;
        sr.pStatDateRange = sr.sStartDate.toString() + " - " + sr.sEndDate.toString();
        sr.pStatFilter = sr.getFilterCriteriaAsStringDescription();
        sr.pStatConsideredEntries = International.getMessage("{n} Einträge", sr.cNumberOfEntries);
        if (sr.cNumberOfEntries > 0 && sr.cEntryNoFirst != null && sr.cEntryNoLast != null) {
            sr.pStatConsideredEntries += ": #" + sr.cEntryNoFirst.toString() + " - #" + sr.cEntryNoLast.toString();
            if (sr.cEntryDateFirst != null && sr.cEntryDateFirst.isSet() && sr.cEntryDateLast != null && sr.cEntryDateLast.isSet()) {
                sr.pStatConsideredEntries += " (" + International.getMessage("vom {day_from} bis {day_to}",
                        sr.cEntryDateFirst.toString(), sr.cEntryDateLast.toString()) + ")";
            }
        }

        if (sr.sStatisticCategory != StatisticsRecord.StatisticCategory.competition) {
            // Table Columns
            sr.prepareTableColumns(data);
        }

        setCurrentWorkDone(workBeforePostprocessing + (WORK_POSTPROCESSING / 5) * 4);
        return sdArray;
    }

    private String writeStatistic(StatisticsData[] sd) {
        logInfo(International.getString("Ausgabe der Daten") + " ...\n");
        StatisticWriter writer = StatisticWriter.getWriter(this, sr, sd);
        if (sr.sOutputType == StatisticsRecord.OutputTypes.efawett) {
            setDone();
        }
        if (writer.write()) {
            if (sr.sOutputFtpClient != null) {
                logInfo(International.getString("FTP-Upload") + " ...\n");
                try {
                    return sr.sOutputFtpClient.write();
                } catch (NoClassDefFoundError e) {
                    Dialog.error(International.getString("Fehlendes Plugin") + ": " + Plugins.PLUGIN_FTP);
                    return International.getString("Fehlendes Plugin") + ": " + Plugins.PLUGIN_FTP;
                }
            }
        }
        return writer.getResultMessage();
    }

    private boolean createStatisticLogbook(StatisticsRecord sr, int statisticsNumber) {
        if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.competition) {
            sr.cCompetition = Competition.getCompetition(sr);
        }
        Vector<Logbook> logbooks = getAllLogbooks();
        if (logbooks.size() == 0) {
            Dialog.error(International.getMessage("Keine {items} im Zeitraum {fromdate} bis {todate} gefunden.",
                    International.getString("Fahrten"),
                    sr.sStartDate.toString(), sr.sEndDate.toString()));
            return false;
        }
        int WORK_PER_LOGBOOK = WORK_PER_STATISTIC / logbooks.size();
        for (int i = 0; i < logbooks.size(); i++) {
            try {
                logbook = logbooks.get(i);
                logInfo(International.getString("Fahrtenbuch") + " " + logbook.getName() + " ...\n");
                DataKeyIterator it = logbook.data().getStaticIterator();
                int size = it.size();
                DataKey k = it.getFirst();
                int pos = 0;
                while (k != null) {
                    LogbookRecord r = (LogbookRecord) logbook.data().get(k);
                    if (r != null) {
                        calculateEntry(r);
                    }
                    this.setCurrentWorkDone(((++pos * WORK_PER_LOGBOOK) / size) + (i * WORK_PER_LOGBOOK) + (statisticsNumber * WORK_PER_STATISTIC));
                    k = it.getNext();
                }
            } catch (Exception e) {
                logInfo("ERROR: " + e.toString() + "\n");
            }
        }
        return true;
    }

    private boolean createStatisticClubwork(StatisticsRecord sr, int statisticsNumber) {
        String[] names = Daten.project.getAllLogbookNames();
        if (names == null || names.length == 0) {
            Dialog.error(International.getMessage("Keine {items} im Zeitraum {fromdate} bis {todate} gefunden.",
                    International.getString("Vereinsarbeit"),
                    sr.sStartDate.toString(), sr.sEndDate.toString()));
            return false;
        }
        for (int i = 0; names != null && i < names.length; i++) {
            ProjectRecord pr = Daten.project.getLoogbookRecord(names[i]);
            if (pr != null) {
                logInfo(International.getString("Fahrtenbuch") + " " + pr.getName() + " ...\n");
                sr.sDefaultClubworkTargetHours = pr.getDefaultClubworkTargetHours();
                sr.sTransferableClubworkHours = pr.getTransferableClubworkHours();
                sr.sFineForTooLittleClubwork = pr.getFineForTooLittleClubwork();

                Clubwork clubwork = Daten.project.getClubwork(names[i], false);
                DataTypeDate lbStart = clubwork.getStartDate();
                DataTypeDate lbEnd = clubwork.getEndDate();
                if (lbStart == null || lbEnd == null) {
                    continue; // should never happen
                }

                if (clubwork != null && DataTypeDate.isRangeOverlap(sr.sStartDate, sr.sEndDate, lbStart, lbEnd)) {
                    DataKeyIterator it;
                    try {
                        it = clubwork.data().getStaticIterator();
                        int WORK_PER_LOGBOOK = WORK_PER_STATISTIC / it.size();
                        int size = it.size();
                        DataKey k = it.getFirst();
                        int pos = 0;
                        while (k != null) {
                            ClubworkRecord r = (ClubworkRecord) clubwork.data().get(k);

                            DataTypeDate date = r.getWorkDate();
                            if (sr.sStartDate.compareTo(date) <= 0 && sr.sEndDate.compareTo(date) >= 0) {
                                calculateEntryForClubwork(r);
                            }
                            this.setCurrentWorkDone(((++pos * WORK_PER_LOGBOOK) / size) + (i * WORK_PER_LOGBOOK) + (statisticsNumber * WORK_PER_STATISTIC));
                            k = it.getNext();
                        }
                    } catch (Exception e) {
                        logInfo("ERROR: " + e.toString() + "\n");
                    }
                }
            }
        }
        return true;
    }

    private String createStatistic(StatisticsRecord sr, int statisticsNumber) {
        this.sr = sr;
        data = new Hashtable<Object, StatisticsData>();

        if (!sr.prepareStatisticSettings(admin)) {
            return null;
        }
        logInfo(International.getMessage("Erstelle Statistik für den Zeitraum {from} bis {to} ...",
                sr.sStartDate.toString(), sr.sEndDate.toString()) + "\n", false, true);
        logInfo(International.getString("Erstelle Statistik ..."),
                true, false);

        runPreprocessing();
        if (sr.sStatisticCategory != StatisticsRecord.StatisticCategory.clubwork) {
            createStatisticLogbook(sr, statisticsNumber);
        } else {
            createStatisticClubwork(sr, statisticsNumber);
        }

        StatisticsData[] sd = runPostprocessing();
        return writeStatistic(sd);
    }

    private void createStatistics(ProgressDialog progressDialog) {
        this.start();
        if (progressDialog != null) {
            progressDialog.showDialog();
        } else {
            try {
                this.join();
            } catch (Exception e) {
                Logger.logdebug(e);
            }
        }
    }

    public void run() {
        setRunning(true);
        try {
            // if we finish creating the statistics before progressDialog.showDialog()
            // in createStatistics() has completed, and at the end of creating the statistic
            // we open a new window (like a Browser for the internal statistics), then
            // the ProgressDialog will end up on top of the Window stack above the Browser.
            // When the browser is then closed, it's not top of stack, and efa's WindowStack
            // check will notice that. It's not really bad when that happens, but it's better
            // to avoid this as it will result in a warning.
            // In the time I have written this explanation, I might have coded a better and
            // safer solution, but it's been a long day and I'm tired, so sleeping half a
            // second must do.
            Thread.sleep(500);
        } catch (Exception eignore) {
        }
        for (int i = 0; i < statisticsRecords.length; i++) {
            String msg = createStatistic(statisticsRecords[i], i);
            if (msg != null && msg.length() > 0) {
                successfulDoneMessages.add(msg);
            }
            setCurrentWorkDone((i + 1) * WORK_PER_STATISTIC);
        }
        setDone();
    }

    public int getAbsoluteWork() {
        return (statisticsRecords != null ? statisticsRecords.length : 0) * WORK_PER_STATISTIC + WORK_POSTPROCESSING;
    }

    public String getSuccessfullyDoneMessage() {
        if (successfulDoneMessages != null && successfulDoneMessages.size() > 0) {
            StringBuffer s = new StringBuffer();
            for (int i = 0; i < successfulDoneMessages.size(); i++) {
                if (i == 10) {
                    s.append("\n...");
                    break;
                }
                s.append((s.length() > 0 ? "\n" : "") + successfulDoneMessages.get(i));
            }
            return s.toString();
        }
        return null; // avoid info dialog at the end
    }

    public static void createStatisticsTask(BaseFrame parentFrame, BaseDialog parentDialog, StatisticsRecord[] sr, AdminRecord admin) {
        StatisticTask statisticTask = new StatisticTask(sr, admin);
        ProgressDialog progressDialog = (parentFrame != null
                ? new ProgressDialog(parentFrame, International.getString("Statistik erstellen"), statisticTask, true, true)
                : (parentDialog != null
                ? new ProgressDialog(parentDialog, International.getString("Statistik erstellen"), statisticTask, true, true)
                : null));
        statisticTask.createStatistics(progressDialog);
    }
}
