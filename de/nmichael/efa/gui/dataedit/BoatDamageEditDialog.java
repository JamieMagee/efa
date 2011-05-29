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
public class BoatDamageEditDialog extends UnversionizedDataEditDialog {

    public BoatDamageEditDialog(Frame parent, BoatDamageRecord r, boolean newRecord) {
        super(parent, International.getString("Bootsschaden"), r, newRecord);
    }

    public BoatDamageEditDialog(JDialog parent, BoatDamageRecord r, boolean newRecord) {
        super(parent, International.getString("Bootsschaden"), r, newRecord);
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

}
