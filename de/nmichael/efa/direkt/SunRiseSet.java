package de.nmichael.efa.direkt;

import java.util.*;
import de.nmichael.efa.EfaConfig;
import de.nmichael.efa.Daten;
import de.nmichael.efa.EfaUtil;

public class SunRiseSet {

  // Constructor usually not needed (everything static), except for Plungin-Test in Daten.java
  public SunRiseSet() {
    uk.me.jstott.coordconv.LatitudeLongitude ll =
        new uk.me.jstott.coordconv.LatitudeLongitude(uk.me.jstott.coordconv.LatitudeLongitude.NORTH,0,0,0,
                                                     uk.me.jstott.coordconv.LatitudeLongitude.EAST,0,0,0); // just dummy statement
  }

  public static boolean sunrisePluginInstalled() {
    try {
      uk.me.jstott.coordconv.LatitudeLongitude ll =
        new uk.me.jstott.coordconv.LatitudeLongitude(uk.me.jstott.coordconv.LatitudeLongitude.NORTH,0,0,0,
                                                     uk.me.jstott.coordconv.LatitudeLongitude.EAST,0,0,0);
      return true;
    } catch (NoClassDefFoundError e1) {
      return false;
    }
  }

  public static String[] getSunRiseSet() throws Exception {
    return getSunRiseSet(Calendar.getInstance());
  }

  public static String[] getSunRiseSet(Calendar cal) throws Exception {
    if (Daten.efaConfig == null) return null;
    if (Daten.efaConfig.efaDirekt_sunRiseSet_ll == null || Daten.efaConfig.efaDirekt_sunRiseSet_ll.length != 8) return null;

    int lat = uk.me.jstott.coordconv.LatitudeLongitude.NORTH;
    int lon = uk.me.jstott.coordconv.LatitudeLongitude.EAST;
    switch(Daten.efaConfig.efaDirekt_sunRiseSet_ll[0]) {
      case EfaConfig.LL_NORTH: lat = uk.me.jstott.coordconv.LatitudeLongitude.NORTH; break;
      case EfaConfig.LL_SOUTH: lat = uk.me.jstott.coordconv.LatitudeLongitude.SOUTH; break;
    }
    switch(Daten.efaConfig.efaDirekt_sunRiseSet_ll[4]) {
      case EfaConfig.LL_WEST:  lon = uk.me.jstott.coordconv.LatitudeLongitude.WEST;  break;
      case EfaConfig.LL_EAST:  lon = uk.me.jstott.coordconv.LatitudeLongitude.EAST;  break;
    }

    uk.me.jstott.coordconv.LatitudeLongitude ll =
      new uk.me.jstott.coordconv.LatitudeLongitude(lat,
                                                   Daten.efaConfig.efaDirekt_sunRiseSet_ll[1],
                                                   Daten.efaConfig.efaDirekt_sunRiseSet_ll[2],
                                                   Daten.efaConfig.efaDirekt_sunRiseSet_ll[3],
                                                   lon,
                                                   Daten.efaConfig.efaDirekt_sunRiseSet_ll[5],
                                                   Daten.efaConfig.efaDirekt_sunRiseSet_ll[6],
                                                   Daten.efaConfig.efaDirekt_sunRiseSet_ll[7]);

    TimeZone gmt = TimeZone.getDefault();
    double julian = uk.me.jstott.util.JulianDateConverter.dateToJulian(cal);

    boolean dst = gmt.inDaylightTime(new Date(cal.getTimeInMillis())); // gmt.getDSTSavings() > 0;

    uk.me.jstott.sun.Time rise = uk.me.jstott.sun.Sun.sunriseTime(julian, ll, gmt, dst);
    uk.me.jstott.sun.Time set  = uk.me.jstott.sun.Sun.sunsetTime(julian, ll, gmt, dst);
    String[] riseset = new String[2];
    riseset[0] = EfaUtil.leadingZeroString(rise.getHours(),2)+":"+
                 EfaUtil.leadingZeroString(rise.getMinutes(),2);
    riseset[1] = EfaUtil.leadingZeroString(set.getHours(),2)+":"+
                 EfaUtil.leadingZeroString(set.getMinutes(),2);
    return riseset;
  }

  public static String getLonLat() {
    if (Daten.efaConfig == null) return null;
    if (Daten.efaConfig.efaDirekt_sunRiseSet_ll == null || Daten.efaConfig.efaDirekt_sunRiseSet_ll.length != 8) return null;
    return Daten.efaConfig.efaDirekt_sunRiseSet_ll[1]+"�"+Daten.efaConfig.efaDirekt_sunRiseSet_ll[2]+"'"+
           Daten.efaConfig.efaDirekt_sunRiseSet_ll[3]+"\" "+(Daten.efaConfig.efaDirekt_sunRiseSet_ll[0] == EfaConfig.LL_NORTH ? "north" : "south")+" / "+
           Daten.efaConfig.efaDirekt_sunRiseSet_ll[5]+"�"+Daten.efaConfig.efaDirekt_sunRiseSet_ll[6]+"'"+
           Daten.efaConfig.efaDirekt_sunRiseSet_ll[7]+"\" "+(Daten.efaConfig.efaDirekt_sunRiseSet_ll[4] == EfaConfig.LL_WEST  ? "west" : "east");
  }

  public static void printAllDays(int year) {
    Calendar cal = Calendar.getInstance();
    System.out.println("efa - Sunrise and Sunset for "+getLonLat()+"\n=============================================================\n");
    System.out.println("Day          Sunrise    Sunset\n------------------------------");
    for (int month=1; month<=12; month++) {
      for (int day=1; day<=31; day++) {
        if ( (day > 30 && (month == 4 || month == 6 || month == 9 || month == 11)) || (day > 29 && month == 2) || (day > 28 && month == 2 && year % 4 != 0)) continue;
        cal.set(year,month-1,day);
        try {
          String[] riseset = getSunRiseSet(cal);
          if (riseset != null) {
            System.out.println(EfaUtil.leadingZeroString(day, 2) + "." +
                               EfaUtil.leadingZeroString(month, 2) + "." + year +
                               "     " + riseset[0] + "     " + riseset[1]);
          }
        } catch(Exception e) {
        }
      }
    }
  }

  public static void main(String[] args) {
    Daten.ini(Daten.APPL_EFADIREKT);
    Daten.mainDirIni();
    Daten.dirsIni(false);
    Daten.efaConfig = new EfaConfig(Daten.efaCfgDirectory+Daten.CONFIGFILE);
    if (Daten.efaConfig.readFile()) {
      printAllDays(2009);
    }

  }

}