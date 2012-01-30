/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.data.storage;

import de.nmichael.efa.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.ex.*;
import java.util.*;
import org.xml.sax.*;
import org.xml.sax.helpers.*;

// @i18n complete

public class XMLFileReader extends XmlHandler {

    private XMLFile data;
    private long globalLock;

    private boolean inDataSection = false;
    private boolean inHeaderSection = false;
    private boolean inRecord = false;
    private DataRecord dataRecord = null;
    private String documentReadError = null;

    public XMLFileReader(XMLFile data, long globalLock) {
        super(XMLFile.FIELD_GLOBAL);
        this.data = data;
        this.globalLock = globalLock;
    }

    public void startElement(String uri, String localName, String qname, Attributes atts) {
        super.startElement(uri, localName, qname, atts);

        if (inDataSection && localName.equals(XMLFile.FIELD_DATA_RECORD)) {
            inRecord = true;
            dataRecord = data.getPersistence().createNewRecord();
        }
        if (localName.equals(XMLFile.FIELD_HEADER)) {
            inHeaderSection = true;
        }
        if (localName.equals(XMLFile.FIELD_DATA)) {
            inDataSection = true;
        }
    }

    public void endElement(String uri, String localName, String qname) {
        super.endElement(uri, localName, qname);

        if (localName.equals(XMLFile.FIELD_HEADER)) {
            inHeaderSection = false;
        }
        if (localName.equals(XMLFile.FIELD_DATA)) {
            inDataSection = false;
        }
        if (inDataSection && localName.equals(XMLFile.FIELD_DATA_RECORD)) {
            try {
                data.add(dataRecord,globalLock);
            } catch(Exception e) {
                Logger.log(Logger.ERROR,Logger.MSG_FILE_PARSEERROR,getLocation() + "Parse Error for Data Record "+dataRecord.toString()+": "+e.toString());
                Logger.logdebug(e);
            }
            dataRecord = null;
            inRecord = false;
            return;
        }

        if (inDataSection) {
            try {
                dataRecord.set(fieldName, fieldValue, false);
            } catch(Exception e) {
                Logger.log(Logger.ERROR,Logger.MSG_FILE_PARSEERROR,
                        getLocation() + "Parse Error for Field "+fieldName+" = "+fieldValue+": "+e.toString());
            }
            if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
                Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,
                        "Field "+fieldName+" = "+fieldValue);
            }
        }
        if (inHeaderSection) {
            try {
                if (fieldName.equals(XMLFile.FIELD_HEADER_PROGRAM)) {
                    if (!fieldValue.equals(Daten.EFA)) {
                        documentReadError = getLocation() + "Unexpected Value for Header Field " + fieldName + ": " + fieldValue;
                    }
                }
                if (fieldName.equals(XMLFile.FIELD_HEADER_VERSION)) {
                    // version handling, if necessary
                }
                if (fieldName.equals(XMLFile.FIELD_HEADER_NAME)) {
                    if (!fieldValue.equals(data.getStorageObjectName())) {
                        documentReadError = getLocation() + "Unexpected Value for Header Field " + fieldName + ": " + fieldValue;
                    }
                }
                if (fieldName.equals(XMLFile.FIELD_HEADER_TYPE)) {
                    if (!fieldValue.equals(data.getStorageObjectType())) {
                        documentReadError = getLocation() + "Unexpected Value for Header Field " + fieldName + ": " + fieldValue;
                    }
                }
                if (fieldName.equals(XMLFile.FIELD_HEADER_SCN)) {
                    data.setSCN(Long.parseLong(fieldValue));
                }
            } catch (Exception e) {
                documentReadError = getLocation() + "Parse Error for Header Field " + fieldName + ": " + fieldValue;
                Logger.log(Logger.ERROR, Logger.MSG_FILE_PARSEERROR, "Parse Error for Field " + fieldName + " = " + fieldValue + ": " + e.toString());
            }
            if (Logger.isTraceOn(Logger.TT_XMLFILE)) {
                Logger.log(Logger.DEBUG,Logger.MSG_FILE_XMLTRACE,"Field "+fieldName+" = "+fieldValue);
            }
        }
    }

    public String getDocumentReadError() {
        return documentReadError;
    }
    
}
