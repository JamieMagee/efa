package de.nmichael.efa.drv;

import de.nmichael.efa.Daten;
import de.nmichael.efa.Logger;
import de.nmichael.efa.EfaUtil;
import de.nmichael.efa.Dialog;
import de.nmichael.efa.EnterPasswordFrame;
import java.io.*;

public class CA {

  public static final String DRVCA = "drvca";

  DRVConfig drvConfig;

  public CA(DRVConfig drvConfig) throws Exception {
    this.drvConfig = drvConfig;

    if (!(new File(Daten.efaDataDirectory+"CA").isDirectory())) throw new Exception("Verzeichnis "+Daten.efaDataDirectory+"CA existiert nicht. Bitte erstelle zun�chst eine CA!");

    if (Daten.keyStore.getCertificate(DRVCA) == null) {
      runKeytool("-import"+
                 " -alias "+DRVCA+
                 " -file "+Daten.efaDataDirectory+"CA"+Daten.fileSep+"cacert.pem" +
                 " -trustcacerts -noprompt",null);
    }
  }

  public boolean runKeytool(String cmd, char[] keypass) {
    String showCmd = cmd + " -keystore "+Daten.efaDataDirectory+drvConfig.KEYSTORE_FILE +
           (keypass != null ? " -keypass ***" : "") +
           " -storepass ***";
    cmd += " -keystore "+Daten.efaDataDirectory+drvConfig.KEYSTORE_FILE +
           (keypass != null ? " -keypass " + new String(keypass) : "") +
           " -storepass " + new String(drvConfig.keyPassword);
    Logger.log(Logger.INFO,"Starte Keytool: "+showCmd);
    String[] cmdarr = EfaUtil.kommaList2Arr(cmd,' ');
    for (int i=0; i<cmdarr.length; i++) cmdarr[i] = EfaUtil.replace(cmdarr[i],"\\s"," ",true);
    try {
        sun.security.tools.KeyTool.main(cmdarr);
    }
    catch(Exception ex) {
        Logger.log(Logger.ERROR, "Konnte Keytool nicht starten: " + 
                ex.getMessage());
        return false;
    }
    return true;
  }

  private void printOutput(String stream, InputStream in) {
    try {
      byte[] arr = new byte[16384];
      while (in != null && in.available() > 0) {
        in.read(arr,0,in.available());
        Logger.log(Logger.INFO,"  "+stream+": "+new String(arr));
      }
    } catch(Exception e) {}
  }

  public boolean signRequest(String req, String sigReq, int tage) {
    if (drvConfig.openssl == null || drvConfig.openssl.length() == 0) {
      Dialog.error("Openssl ist nicht konfiguriert!");
      return false;
    }
    try {
      char[] pwd = EnterPasswordFrame.enterPassword(Dialog.frameCurrent(),"Bitte Schl�ssel-Pa�wort f�r CA eingeben:");
      if (pwd == null) return false;
      String openssl = drvConfig.openssl;
      String sigReqTmp = sigReq+".tmp";
      String cmd = openssl + " ca -config "+Daten.efaDataDirectory+"CA"+Daten.fileSep+"openssl.cnf -policy policy_anything -in "+req+" -out "+sigReqTmp+" -batch -days "+tage+" -key ";
      Logger.log(Logger.INFO,"Starte OpenSSL: "+cmd+"***");
      cmd += new String(pwd);
      Process p = Runtime.getRuntime().exec(cmd,null,new File(Daten.efaDataDirectory+"CA"));
      InputStream stdout = p.getInputStream();
      InputStream stderr = p.getErrorStream();
      try {
        Thread.sleep(1000);
      } catch(InterruptedException ee) { EfaUtil.foo(); }
      printOutput("stdout",stdout);
      printOutput("stderr",stderr);
      p.waitFor();
      if (!EfaUtil.canOpenFile(sigReqTmp)) {
        Dialog.error("Zertifizierungsrequest konnte von openssl nicht erstellt werden.");
        return false;
      }
      BufferedReader f = new BufferedReader(new FileReader(sigReqTmp));
      BufferedWriter ff = new BufferedWriter(new FileWriter(sigReq));
      String s;
      boolean start = false;
      while ( (s = f.readLine()) != null) {
        if (s.startsWith("-----BEGIN CERTIFICATE-----")) start = true;
        if (start) ff.write(s+"\n");
      }
      f.close();
      ff.close();
      (new File(sigReqTmp)).delete();
      return start;
    } catch(Exception e) {
      Dialog.error("Fehler: "+e.toString());
      return false;
    }
  }
}