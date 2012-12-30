/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */
package de.nmichael.efa.statistics;

import java.io.*;
import de.nmichael.efa.data.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.*;
import de.nmichael.efa.ex.EfaException;

public class StatisticCSVWriter extends StatisticWriter {

    private String encoding;
    private String separator;
    private String quotes;
    private int linelength = 0;

    public StatisticCSVWriter(StatisticsRecord sr, StatisticsData[] sd) {
        super(sr, sd);
        this.encoding = sr.sOutputEncoding;
        this.separator = sr.sOutputCsvSeparator;
        this.quotes = sr.sOutputCsvQuotes;
        if (this.encoding == null || this.encoding.length() == 0) {
            this.encoding = Daten.ENCODING_UTF;
        }
        if (this.separator == null || this.separator.length() == 0) {
            this.separator = "|";
        }
        if (this.quotes != null && this.quotes.length() == 0) {
            this.quotes = null;
        }
    }

    protected synchronized void write(BufferedWriter fw, String s) throws IOException {
        if (s == null) {
            s = "";
        }
        if (quotes == null && s.indexOf(separator) >= 0) {
            String repl = (!separator.equals("_") ? "_" : "#");
            s = EfaUtil.replace(s, separator, repl, true);
        }
        fw.write((linelength > 0 ? separator : "") + 
                 (quotes != null ? quotes : "") + s + (quotes != null ? quotes : ""));
        linelength += s.length();
    }

    protected synchronized void writeln(BufferedWriter fw) throws IOException {
        fw.write("\n");
        linelength = 0;
    }

    public boolean write() {
        BufferedWriter f = null;

        if (sr.sFileExecBefore != null && sr.sFileExecBefore.length() > 0) {
            EfaUtil.execCmd(sr.sFileExecBefore);
        }
        try {
            // Create File
            f = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(sr.sOutputFile), encoding));

            // Write normal Output
            if (sr.pTableColumns != null && sr.pTableColumns.size() > 0) {
                for (int i = 0; i < sr.pTableColumns.size(); i++) {
                    write(f, sr.pTableColumns.get(i));
                }
                writeln(f);

                for (int i = 0; i < sd.length; i++) {
                    if (sd[i].isMaximum || sd[i].isSummary) {
                        continue;
                    }
                    if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.list ||
                        sr.sStatisticCategory == StatisticsRecord.StatisticCategory.matrix) {
                        if (sr.sIsFieldsPosition) {
                            write(f, sd[i].sPosition);
                        }
                        if (sr.sIsFieldsName) {
                            write(f, sd[i].sName);
                        }
                        if (sr.sIsFieldsGender) {
                            write(f, sd[i].sGender);
                        }
                        if (sr.sIsFieldsStatus) {
                            write(f, sd[i].sStatus);
                        }
                        if (sr.sIsFieldsYearOfBirth) {
                            write(f, sd[i].sYearOfBirth);
                        }
                        if (sr.sIsFieldsBoatType) {
                            write(f, sd[i].sBoatType);
                        }
                        if (sr.sIsAggrDistance) {
                            write(f, sd[i].sDistance);
                        }
                        if (sr.sIsAggrRowDistance) {
                            write(f, sd[i].sRowDistance);
                        }
                        if (sr.sIsAggrCoxDistance) {
                            write(f, sd[i].sCoxDistance);
                        }
                        if (sr.sIsAggrSessions) {
                            write(f, sd[i].sSessions);
                        }
                        if (sr.sIsAggrAvgDistance) {
                            write(f, sd[i].sAvgDistance);
                        }
                        if (sr.sIsAggrDuration) {
                            write(f, sd[i].sDuration);
                        }
                        if (sr.sIsAggrSpeed) {
                            write(f, sd[i].sSpeed);
                        }
                        if (sr.sIsAggrZielfahrten) {
                            write(f, sd[i].sDestinationAreas);
                        }
                        if (sr.sIsAggrWanderfahrten) {
                            write(f, sd[i].sWanderfahrten);
                        }
                        if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.matrix) {
                            for (int j = sr.pMatrixColumnFirst; j < sr.pTableColumns.size(); j++) {
                                StatisticsData sdm = (sd[i].matrixData != null ?
                                    sd[i].matrixData.get(sr.pMatrixColumns.get(sr.pTableColumns.get(j))) : null);
                                write(f, getMatrixString(sdm));
                            }
                        }
                    }
                    if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.logbook) {
                        if (sd[i].logbookFields != null) {
                            for (int j = 0; j < sd[i].logbookFields.length; j++) {
                                write(f, sd[i].logbookFields[j]);
                            }
                        }
                    }
                    if (sr.sStatisticCategory == StatisticsRecord.StatisticCategory.clubwork) {
                        if (sr.sIsFieldsPosition) {
                            write(f, sd[i].sPosition);
                        }
                        if (sr.sIsFieldsName) {
                            write(f, sd[i].sName);
                        }
                        if (sr.sIsFieldsGender) {
                            write(f, sd[i].sGender);
                        }
                        if (sr.sIsFieldsStatus) {
                            write(f, sd[i].sStatus);
                        }
                        if (sr.sIsAggrClubwork) {
                            write(f, sd[i].sClubwork);
                        }
                        if (sr.sIsAggrClubworkRelativeToTarget) {
                            write(f, sd[i].sClubworkRelativeToTarget);
                        }
                        if (sr.sIsAggrClubworkOverUnderCarryOver) {
                            write(f, sd[i].sClubworkOverUnderCarryOver);
                        }
                        if (sr.sIsAggrClubworkCredit) {
                            write(f, sd[i].sClubworkCredit);
                        }
                    }
                    writeln(f);
                }
            }
            f.close();
        } catch (IOException e) {
            Dialog.error(LogString.fileCreationFailed(sr.sOutputFile, International.getString("Ausgabedatei")));
            LogString.logError_fileCreationFailed(sr.sOutputFile, International.getString("Ausgabedatei"));
            resultMessage = LogString.fileCreationFailed(sr.sOutputFile, International.getString("Statistik"));
            return false;
        } finally {
            try {
                f.close();
            } catch (Exception ee) {
                f = null;
            }
        }
        if (sr.sFileExecAfter != null && sr.sFileExecAfter.length() > 0) {
            EfaUtil.execCmd(sr.sFileExecAfter);
        }
        resultMessage = LogString.fileSuccessfullyCreated(sr.sOutputFile, International.getString("Statistik"));
        return true;
    }
}
