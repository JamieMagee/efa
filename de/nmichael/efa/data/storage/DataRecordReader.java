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

import de.nmichael.efa.Daten;
import de.nmichael.efa.util.Logger;
import de.nmichael.efa.util.XmlHandler;

public class DataRecordReader extends XmlHandler {

    private DataRecord dataRecord;

    public DataRecordReader(DataRecord dataRecord) {
        super(DataRecord.ENCODING_RECORD);
        this.dataRecord = dataRecord;
    }

    public void endElement(String uri, String localName, String qname) {
        super.endElement(uri, localName, qname);

        if (!localName.equals(DataRecord.ENCODING_RECORD)) {
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
    }

}