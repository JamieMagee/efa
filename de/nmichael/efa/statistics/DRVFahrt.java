/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.nmichael.efa.statistics;

/**
 *
 * @author nick
 */
// Daten f�r DRV-Fahrten
public class DRVFahrt {

  public String lfdnr,datumStart,datumEnde,ziel,bemerk;
  int km,anzTage;
  boolean ok; // g�ltige Wanderfahrt, oder nicht (MTour < 40 Km)
  boolean jum; // ob JuM-Regatta

}
