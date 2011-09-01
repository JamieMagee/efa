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
import de.nmichael.efa.data.types.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.*;
import javax.swing.event.ChangeEvent;

// @i18n complete
public class SessionGroupEditDialog extends UnversionizedDataEditDialog implements IItemListener {

    public SessionGroupEditDialog(Frame parent, SessionGroupRecord r, boolean newRecord) {
        super(parent, International.getString("Fahrtgruppen"), r, newRecord);
        initListener();
    }

    public SessionGroupEditDialog(JDialog parent, SessionGroupRecord r, boolean newRecord) {
        super(parent, International.getString("Fahrtgruppen"), r, newRecord);
        initListener();
    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    private void initListener() {
        IItemType itemType = null;
        for (IItemType item : items) {
            if (item.getName().equals(SessionGroupRecord.STARTDATE) ||
                item.getName().equals(SessionGroupRecord.ENDDATE)) {
                ((ItemTypeDate)item).registerItemListener(this);
                itemType = item;
            }
        }
        itemListenerAction(itemType, null);
    }

    public void itemListenerAction(IItemType item, AWTEvent event) {
        if (item != null && event instanceof FocusEvent && event.getID() == FocusEvent.FOCUS_LOST &&
             (item.getName().equals(SessionGroupRecord.STARTDATE) ||
              (item.getName().equals(SessionGroupRecord.ENDDATE))) ) {
            Vector<IItemType> items = getItems();
            ItemTypeDate startDate = null;
            ItemTypeDate endDate = null;
            ItemTypeInteger activeDays = null;
            for (int i=0; items != null && i<items.size(); i++) {
                if (items.get(i).getName().equals(SessionGroupRecord.STARTDATE)) {
                    startDate = (ItemTypeDate)items.get(i);
                }
                if (items.get(i).getName().equals(SessionGroupRecord.ENDDATE)) {
                    endDate = (ItemTypeDate)items.get(i);
                }
                if (items.get(i).getName().equals(SessionGroupRecord.ACTIVEDAYS)) {
                    activeDays = (ItemTypeInteger)items.get(i);
                }
            }
            if (startDate != null && endDate != null && activeDays != null) {
                startDate.getValueFromGui();
                endDate.getValueFromGui();
                activeDays.getValueFromGui();
                if (startDate.isSet() && endDate.isSet()) {
                    long days = startDate.getDate().getDifferenceDays(endDate.getDate()) + 1;
                    if (activeDays.getValue() > days || !activeDays.isSet()) {
                        activeDays.parseAndShowValue(Long.toString(days));
                    }
                }
            }
        }
    }

}
