/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui.dataedit;

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.event.ChangeEvent;

// @i18n complete
public class DestinationEditDialog extends VersionizedDataEditDialog {

    public DestinationEditDialog(Frame parent, DestinationRecord r, boolean newRecord) {
        super(parent, 
                International.getString("Ziel") + " / " +
                International.getString("Strecke"),
                r, newRecord);
    }

    public DestinationEditDialog(JDialog parent, DestinationRecord r, boolean newRecord) {
        super(parent,
                International.getString("Ziel") + " / " +
                International.getString("Strecke"),
                r, newRecord);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    protected void iniDefaults() {
        if (newRecord) {
            ((DestinationRecord)dataRecord).setStartIsBoathouse(true);
            ((DestinationRecord)dataRecord).setRoundtrip(true);
            
        }
    }


}