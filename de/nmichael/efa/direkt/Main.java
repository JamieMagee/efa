/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.direkt;

import de.nmichael.efa.core.EfaConfig;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import javax.swing.UIManager;
import java.awt.*;
import de.nmichael.efa.*;
import java.io.*;
import java.util.*;

// @i18n complete
public class Main extends Program {

    public static String STARTARGS = "";

    //Construct the application
    public Main(String[] args) {
        super(args);
        Daten.initialize(Daten.APPL_EFADIREKT);



        // Schriftgröße
        try {
            if (Daten.efaConfig.efaDirekt_fontSize != 0 || Daten.efaConfig.efaDirekt_fontStyle != -1) {
                Dialog.setGlobalFontSize(Daten.efaConfig.efaDirekt_fontSize, Daten.efaConfig.efaDirekt_fontStyle);
            }
        } catch (Exception e) {
            Logger.log(Logger.WARNING, Logger.MSG_WARN_CANTSETFONTSIZE,
                    International.getString("Schriftgröße konnte nicht geändert werden") + ": " + e.toString());
        }

        EfaDirektFrame frame = new EfaDirektFrame();
        frame.validate();
        //Center the window
        Dimension frameSize = frame.getSize();
        if (frameSize.height > Dialog.screenSize.height) {
            frameSize.height = Dialog.screenSize.height;
        }
        if (frameSize.width > Dialog.screenSize.width) {
            frameSize.width = Dialog.screenSize.width;
        }
        Dialog.setDlgLocation(frame);
        frame.setVisible(true);

        Daten.iniSplashScreen(false);
    }

    public void printUsage(String wrongArgument) {
        super.printUsage(wrongArgument);
        printOption("-javaRestart", International.getString("Neustart von efa durch Java statt Shell"));
        System.exit(0);
    }

    public void checkArgs(String[] args) {
        super.checkArgs(args);
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                continue; // argument already handled by super class
            }
            if (args[i].equals("-javaRestart")) {
                Daten.javaRestart = true;
                args[i] = null;
                continue;
            }
            if (args[i].equals("-emulateWin")) {
                System.setProperty("os.name","Windows XP");
                System.setProperty("os.arch","x86");
                System.setProperty("os.version","5.1");
            }
        }
        checkRemainingArgs(args);
    }

    //Main method
    public static void main(String[] args) {
        for (int i = 0; i < args.length; i++) {
            STARTARGS += " " + args[i];
        }
        new Main(args);
    }
}
