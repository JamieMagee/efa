package de.nmichael.efa.util;

import de.nmichael.efa.*;
import java.io.*;

public class EfaSec {

  private String filename;

  public EfaSec(String filename) {
    this.filename = filename;
  }

  public boolean secFileExists() {
    return (new File(filename)).isFile();
  }

  private String read() {
    try {
      BufferedReader fsec = new BufferedReader(new InputStreamReader(new FileInputStream(filename),Daten.ENCODING));
      String efaSecSHA = fsec.readLine(); fsec.close();
      return efaSecSHA;
    } catch(Exception e) {
      return null;
    }
  }

  public String getSecValue() {
    String efaSecSHA = read();
    if (efaSecSHA != null && efaSecSHA.startsWith("#")) efaSecSHA = efaSecSHA.substring(1,efaSecSHA.length());
    return efaSecSHA;
  }

  public boolean isDontDeleteSet() {
    String efaSecSHA = read();
    return (efaSecSHA != null && efaSecSHA.startsWith("#"));
  }

  public boolean secValueValid() {
    String efaJarSHA = EfaUtil.getSHA(new File(Daten.efaProgramDirectory+"efa.jar"));
    String efaSecSHA = getSecValue();
    return efaJarSHA != null && efaSecSHA != null && efaJarSHA.equals(efaSecSHA);
  }

  public boolean writeSecFile(String sha, boolean dontDelete) {
    try {
      BufferedWriter fsec = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename),Daten.ENCODING));
      fsec.write( (dontDelete ? "#" : "") + sha);
      fsec.close();
      return true;
    } catch(Exception e) {
      return false;
    }
  }

  public boolean delete(boolean force) {
    if (force || !isDontDeleteSet()) {
      boolean secDeleted = false;
      try {
        secDeleted = (new File(filename)).delete();
      } catch(Exception e) { secDeleted = false; }
      return secDeleted;
    }
    return true;
  }

  public String getFilename() {
    return filename;
  }
}