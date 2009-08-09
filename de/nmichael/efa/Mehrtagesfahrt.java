package de.nmichael.efa;

public class Mehrtagesfahrt {

  private String displayName;
  public String name;
  public String start;
  public String ende;
  public int rudertage;
  public String gewaesser;
  public boolean isEtappen;

  public Mehrtagesfahrt(String name, String start, String ende, int rudertage, String gewaesser, boolean isEtappen) {
    this.name = EfaUtil.removeSepFromString(name);
    this.start = EfaUtil.removeSepFromString(start);
    this.ende = EfaUtil.removeSepFromString(ende);
    this.rudertage = rudertage;
    this.gewaesser = EfaUtil.removeSepFromString(gewaesser);
    this.isEtappen = isEtappen;
    this.displayName = this.name; // + " ("+ this.start + " bis " + this.ende + ")";
  }

  public String getDisplayName() {
    return this.displayName;
  }

  public static String getNameFromDisplayName(String displayName) {
    return displayName; // zur Zeit derselbe Name wie der wirkliche
  }

  // pr�ft, ob es sich bei "s" um eine vordefinierte Fahrtart handelt, d.h. eine, die in dem Daten.fahrtart-Array steht
  public static boolean isVordefinierteFahrtart(String s) {
    if (Daten.bezeichnungen == null || Daten.bezeichnungen.fahrtart == null) return false;
    for (int i=0; i<Daten.bezeichnungen.fahrtart.size(); i++)
      if (Daten.bezeichnungen.fahrtart.get(i).equals(s)) return true;
    return false;
  }


}