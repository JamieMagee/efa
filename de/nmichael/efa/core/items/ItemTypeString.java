/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.core.items;

import de.nmichael.efa.core.items.ItemTypeLabelValue;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import javax.swing.*;
import java.util.regex.*;

// @i18n complete

public class ItemTypeString extends ItemTypeLabelTextfield {

    protected String value;
    protected String allowedCharacters;
    protected String notAllowedCharacters;
    protected String replacementCharacter;
    protected Pattern pattern;
    protected boolean toUpperCase = false;
    protected int minChar = 0;

    public ItemTypeString(String name, String value, int type,
            String category, String description) {
        this.name = name;
        this.value = value;
        this.type = type;
        this.category = category;
        this.description = description;
    }

    public IItemType copyOf() {
        return new ItemTypeString(name, value, type, category, description);
    }

    public void parseValue(String value) {
        if (value != null) {
            value = value.trim();
        }
        if (toUpperCase && value != null) {
            value = value.toUpperCase();
        }
        if ((allowedCharacters != null || notAllowedCharacters != null) && value != null) {
            int i = 0;
            while (i < value.length()) {
                char c = value.charAt(i);
                if ((allowedCharacters != null && allowedCharacters.indexOf(c) < 0) ||
                    (notAllowedCharacters != null && notAllowedCharacters.indexOf(c) >= 0) ) {
                    value = (i > 0 ? value.substring(0, i) : "") +
                            (replacementCharacter != null ? replacementCharacter : "") +
                            (i+1 < value.length() ? value.substring(i+1) : "");
                    if (replacementCharacter != null) {
                        i++;
                    }
                } else {
                    i++;
                }
            }
        }
        if (pattern != null && value != null) {
            Matcher m = pattern.matcher(value);
            if (!m.matches()) {
                if (m.lookingAt()) {
                    value = m.group(1);
                } else {
                    value = null;
                }
            }
        }
        this.value = value;
    }

    public String toString() {
        return (value != null ? value : "");
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        showValue();
    }

    public void setAllowedCharacters(String allowedCharacters) {
        this.allowedCharacters = allowedCharacters;
    }

    public void setNotAllowedCharacters(String notAllowedCharacters) {
        this.notAllowedCharacters = notAllowedCharacters;
    }

    public void setReplacementCharacter(char replacementCharacter) {
        this.replacementCharacter = Character.toString(replacementCharacter);
    }

    public void setAllowedRegex(String regex) {
        if (regex != null) {
            pattern = Pattern.compile("(" + regex + ")");
        } else {
            pattern = null;
        }
    }

    public void setToUpperCase(boolean toUpperCase) {
        this.toUpperCase = toUpperCase;
    }

    public boolean isToUpperCase() {
        return toUpperCase;
    }

    public void setMinCharacters(int minChar) {
        this.minChar = minChar;
        if (minChar > 0) {
            setNotNull(true);
        }
    }

    public boolean isValidInput() {
        if (isNotNullSet()) {
            if (value == null || value.length() == 0) {
                lastInvalidErrorText = International.getString("Feld darf nicht leer sein");
                return false;
            }
        }
        if (minChar > 0 && (value == null || value.length() < minChar)) {
            lastInvalidErrorText = International.getMessage("Eingabe muß mindestens {n} Zeichen lang sein", minChar);
            return false;
        }
        return true;
    }
}