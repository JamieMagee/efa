/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.util;

import de.nmichael.efa.*;
import de.nmichael.efa.util.EfaUtil;
import de.nmichael.efa.util.Dialog;
import javax.swing.*;

// @i18n complete
public class Help {

    private static String getURL(String name) {
        String file;

        // this is just for test purposes!!!
        if (Daten.exceptionTest) {
            file = null;
            if (file.length() > 0) {
                EfaUtil.foo();
            }
        }

        // now get the file name for the help page!
        String lang = International.getLanguageID();
        while (lang != null && lang.length() > 0) {
            file = Daten.efaDocDirectory + name + "_" + lang + ".html";
            if (EfaUtil.canOpenFile(file)) {
                return "file:" + file;
            }
            int pos = lang.indexOf("_");
            if (pos > 0) {
                lang = lang.substring(0, pos);
            } else {
                break;
            }
        }
        file = Daten.efaDocDirectory + name + ".html";
        if (EfaUtil.canOpenFile(file)) {
            return "file:" + file;
        }
        file = Daten.efaDocDirectory + "index.html";
        if (EfaUtil.canOpenFile(file)) {
            return "file:" + file;
        }
        return null;
    }

    private static void nohelp() {
        Dialog.infoDialog(International.getString("Hilfe"),
                          International.getString("Keine Hilfe verfügbar."));
    }

    public static void getHelp(JFrame frame, String name) {
        String url = getURL(name);
        if (url == null) {
            nohelp();
        } else {
            Dialog.neuBrowserDlg(frame, International.getString("Online-Hilfe"), url);
        }
    }

    public static void getHelp(JDialog frame, String name) {
        String url = getURL(name);
        if (url == null) {
            nohelp();
        } else {
            Dialog.neuBrowserDlg(frame, International.getString("Online-Hilfe"), url);
        }
    }

    public static void getHelp(JFrame frame, Class c) {
        String name = c.toString();
        if (name == null) {
            return;
        }
        if (name.lastIndexOf(".") >= 0) {
            name = name.substring(name.lastIndexOf(".") + 1, name.length());
        }
        String url = getURL(name);
        if (url == null) {
            nohelp();
        } else {
            Dialog.neuBrowserDlg(frame, International.getString("Online-Hilfe"), url);
        }
    }

    public static void getHelp(JDialog frame, Class c) {
        String name = c.toString();
        if (name == null) {
            return;
        }
        if (name.lastIndexOf(".") >= 0) {
            name = name.substring(name.lastIndexOf(".") + 1, name.length());
        }
        String url = getURL(name);
        if (url == null) {
            nohelp();
        } else {
            Dialog.neuBrowserDlg(frame, International.getString("Online-Hilfe"), url);
        }
    }
}
