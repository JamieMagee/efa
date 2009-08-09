package de.nmichael.efa;

import java.io.*;
import java.util.*;
import java.security.MessageDigest;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;


public class CertInfos {

  public static String getCertInfos(X509Certificate cert, String filename) {
    String s = "";

    try {
      // Zertifikatinfos
      s += "Eigent�mer: "+cert.getSubjectDN().getName() + "\n";
      s += "Aussteller: "+cert.getIssuerDN().getName() + "\n";
      s += "Seriennummer: "+cert.getSerialNumber().toString(16) + "\n";
      s += "G�ltig f�r Wettbewerbsjahre: "+ getValidityYears(cert) + "\n";
      s += "G�ltig f�r Signaturerstellung: "+getValiditySign(cert) + "\n";

      // Fingerabdruck
      if (filename != null) {
        FileInputStream in = new FileInputStream(filename);
        byte[] certData = new byte[in.available()];
        in.read(certData);
        s += "MD5-Fingerabdruck: " + getFingerprint(certData,"MD5");
        in.close();
      }
    } catch(Exception e) {
      return "Zertifikatsdaten konnten nicht ermittelt werden.\nERROR: "+e.toString();
    }
    return s;
  }

  private static String getFingerprint(byte[] certBytes, String algorithm) throws Exception {
    MessageDigest md = MessageDigest.getInstance(algorithm);
    md.update(certBytes);
    byte[] fp = md.digest();
    String s = "";
    for (int i=0; i<fp.length; i++) s += EfaUtil.hexByte(fp[i] & 0xff)+ (i+1<fp.length ? ":" : "");
    return s;
  }

  public static String getValidityYears(X509Certificate cert) {
    TMJ tmj = EfaUtil.string2date(cert.getSubjectDN().getName(),0,0,0);
    if (tmj.monat <= 0) return "unbekannt";
    else if (tmj.jahr == 0) return Integer.toString(tmj.monat);
    else return tmj.monat + " - " + tmj.jahr;
  }


  // pr�ft, ob das Zertifikat cert f�r das Jahr this.jahr g�ltig ist
  public static boolean sigKeyValidForYear(X509Certificate cert, int jahr) {
    TMJ tmj = EfaUtil.string2date(cert.getSubjectDN().getName(),0,0,0);
    if (tmj.monat <= 0) return false;
    if (tmj.jahr == 0) {
      if (jahr != tmj.monat) return false;
    } else {
      if (jahr < tmj.monat || jahr > tmj.jahr) return false;
    }
    if (isRevokedForYear(cert,jahr)) return false;
    return true;
  }

  // pr�ft, ob der Schl�ssel, der durch das Zertifikat cert angegeben ist, zum Signierzeitpunkt gesperrt ist
  public static boolean isRevokedForDate(X509Certificate cert, Date date) {
    return false; // @todo
  }

  // pr�ft, ob der Schl�ssel, der durch das Zertifikat cert angegeben ist, f�r das Jahr this.jahr gesperrt ist
  public static boolean isRevokedForYear(X509Certificate cert, int jahr) {
    return false; // @todo
  }

  // pr�ft, ob das Zertifikat zum Signierzeitpunkt this.sigDatum g�ltig ist
  public static boolean sigKeyValidOnDate(X509Certificate cert, Date sigDate) {
    if (sigDate == null) return false;
    if (sigDate.before(cert.getNotBefore()) || sigDate.after(cert.getNotAfter())) return false;
    if (isRevokedForDate(cert,sigDate)) return false;
    return true;
  }



  public static String getValiditySign(X509Certificate cert) {
    return EfaUtil.date2String(cert.getNotBefore(),false) + " bis " + EfaUtil.date2String(cert.getNotAfter(),false);
  }

  public static String getAliasName(X509Certificate cert) {
    String alias = cert.getSubjectDN().getName().toLowerCase();

    int pos = alias.indexOf("o=");
    if (pos > 0) alias = alias.substring(pos+2,alias.length());

    pos = alias.indexOf(",");
    if (pos > 0) alias = alias.substring(0,pos);

    pos = alias.indexOf(" ");
    if (pos > 0) alias = alias.substring(0,pos);

    pos = alias.indexOf("_");
    if (pos > 0) return alias.substring(0,pos);

    return alias;
  }

  public static void main(String[] args) throws Exception {
    InputStream inStream = new FileInputStream(args[0]);
    CertificateFactory cf = CertificateFactory.getInstance("X.509");
    X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);

    System.out.println(getCertInfos(cert,args[0]));
  }

}