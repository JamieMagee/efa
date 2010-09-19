/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2009 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.types.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class NewProjectDialog extends StepwiseDialog {

    private static final String STORAGETYPE_LOCAL  = "LOCAL";
    private static final String STORAGETYPE_SQL    = "SQL";

    private static final String PROJECTNAME        = "PROJECTNAME";
    private static final String PROJECTDESCRIPTION = "PROJECTDESCRIPTION";
    private static final String STORAGETYPE        = "STORAGETYPE";

    public NewProjectDialog(JDialog parent) {
        super(parent, International.getString("Neues Projekt"));
    }

    public NewProjectDialog(JFrame parent) {
        super(parent, International.getString("Neues Projekt"));
    }

    String[] getSteps() {
        return new String[] {
            International.getString("Name und Beschreibung"),
            International.getString("Speicherort festlegen"),
            International.getString("Angaben zum Verein")
        };
    }
    
    String getDescription(int step) {
        return "Das hier ist ein Beschreibungstext.";
    }

    void initializeItems() {
        items = new ArrayList<IItemType>();
        IItemType item;

        // Items for Step 0
        item = new ItemTypeString(PROJECTNAME, "", IItemType.TYPE_PUBLIC, "0", International.getString("Name des Projekts"));
        ((ItemTypeString)item).setAllowedCharacters("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_");
        ((ItemTypeString)item).setReplacementCharacter('_');
        ((ItemTypeString)item).setNotNull(true);
        items.add(item);
        item = new ItemTypeString(PROJECTDESCRIPTION, "", IItemType.TYPE_PUBLIC, "0", International.getString("Beschreibung"));
        items.add(item);

        // Items for Step 1
        item = new ItemTypeStringList(STORAGETYPE, STORAGETYPE_LOCAL,
                new String[] { STORAGETYPE_LOCAL, STORAGETYPE_SQL },
                new String[] { International.getString("lokales Dateisystem"),
                               International.getString("SQL-Datenbank") },
                IItemType.TYPE_PUBLIC, "1", International.getString("Speicherort"));
        items.add(item);
    }

    boolean checkInput() {
        boolean ok = super.checkInput();
        if (!ok) {
            return false;
        }
        
        if (step == 0) {
            ItemTypeString item = (ItemTypeString)getItemByName(PROJECTNAME);
            String name = item.getValue();
            Project prj = new Project(IDataAccess.TYPE_FILE_XML, Daten.efaDataDirectory, name);
            try {
                if (prj.data().existsStorageObject()) {
                    Dialog.error(International.getMessage("Das Projekt '{project}' existiert bereits.",
                            name));
                    item.requestFocus();
                    return false;
                }
            } catch (Exception e) {
            }
        }


        if (step == 1) {
            ItemTypeStringList item = (ItemTypeStringList)getItemByName(STORAGETYPE);
            if (!item.getValue().equals(STORAGETYPE_LOCAL)) {
                Dialog.error(International.getMessage("Die ausgewählte Option '{option}' wird zur Zeit noch nicht unterstützt.",
                        International.getString("SQL-Datenbank")));
                item.requestFocus();
                return false;
            }
        }
        return true;
    }

    void finishButton_actionPerformed(ActionEvent e) {
        super.finishButton_actionPerformed(e);

        ItemTypeString prjName = (ItemTypeString)getItemByName(PROJECTNAME);
        ItemTypeStringList storType = (ItemTypeStringList)getItemByName(STORAGETYPE);
        int storageType = -1;
        if (storType.getValue().equals(STORAGETYPE_LOCAL)) {
            storageType = IDataAccess.TYPE_FILE_XML;
        }
        if (storType.getValue().equals(STORAGETYPE_SQL)) {
            storageType = IDataAccess.TYPE_DB_SQL;
        }
        Project prj = new Project(storageType, Daten.efaDataDirectory, prjName.getValue());
        try {
            prj.open(true);
            prj.setEmptyProject(prjName.getValue());
            prj.close();
        } catch(Exception ee) {
            Dialog.error(ee.toString());
        }
    }

}
