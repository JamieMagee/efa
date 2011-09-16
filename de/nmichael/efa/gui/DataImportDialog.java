/**
 * Title:        efa - elektronisches Fahrtenbuch für Ruderer
 * Copyright:    Copyright (c) 2001-2011 by Nicolas Michael
 * Website:      http://efa.nmichael.de/
 * License:      GNU General Public License v2
 *
 * @author Nicolas Michael
 * @version 2
 */

package de.nmichael.efa.gui;

import de.nmichael.efa.Daten;
import de.nmichael.efa.core.items.*;
import de.nmichael.efa.data.storage.*;
import de.nmichael.efa.data.types.*;
import de.nmichael.efa.util.*;
import de.nmichael.efa.util.Dialog;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

public class DataImportDialog extends BaseDialog implements IItemListener {

    private ItemTypeFile file;
    private ItemTypeStringList encoding;
    private ButtonGroup fileTypeGroup;
    private JRadioButton fileTypeXml;
    private JRadioButton fileTypeCsv;
    private ItemTypeString csvSeparator;
    private ItemTypeString csvQuotes;
    private ItemTypeStringList importMode;
    private ItemTypeStringList importModeUpd;
    private ItemTypeDateTime validAtDateTime;

    private StorageObject persistence;
    private long validAt;

    public DataImportDialog(Frame parent, StorageObject persistence, long validAt) {
        super(parent, International.getMessage("{data} importieren", persistence.getDescription()),
                International.getStringWithMnemonic("Import starten"));
        setPersistence(persistence, validAt);
    }

    public DataImportDialog(JDialog parent, StorageObject persistence, long validAt) {
        super(parent, International.getMessage("{data} importieren", persistence.getDescription()),
                International.getStringWithMnemonic("Import starten"));
        setPersistence(persistence, validAt);
    }

    public void setPersistence(StorageObject persistence, long validAt) {
        this.persistence = persistence;
        this.validAt = validAt;
    }

    protected void iniDialog() throws Exception {
        mainPanel.setLayout(new GridBagLayout());

        file = new ItemTypeFile("FILE", "",
                    International.getString("Datei"),
                    International.getString("Datei") + " (*.*)",
                    null, ItemTypeFile.MODE_OPEN, ItemTypeFile.TYPE_FILE,
                    IItemType.TYPE_PUBLIC, "",
                    International.getString("Import aus Datei"));
        file.setNotNull(true);
        file.setFieldGrid(2, -1, -1);
        file.setPadding(0, 0, 10, 0);
        file.setFieldSize(450, -1);
        file.registerItemListener(this);
        file.displayOnGui(this, mainPanel, 0, 0);

        fileTypeXml = new JRadioButton();
        Mnemonics.setButton(this, fileTypeXml, International.getStringWithMnemonic("XML-Datei"));
        fileTypeCsv = new JRadioButton();
        fileTypeCsv.setSelected(true);
        Mnemonics.setButton(this, fileTypeCsv, International.getStringWithMnemonic("CSV-Datei"));
        fileTypeGroup = new ButtonGroup();
        fileTypeGroup.add(fileTypeXml);
        fileTypeGroup.add(fileTypeCsv);
        fileTypeXml.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileTypeChanged();
            }
        });
        fileTypeCsv.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                fileTypeChanged();
            }
        });
        mainPanel.add(fileTypeXml, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        mainPanel.add(fileTypeCsv, new GridBagConstraints(2, 1, 1, 1, 0.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        encoding = new ItemTypeStringList("ENCODING", Daten.ENCODING_UTF,
                new String[] { Daten.ENCODING_UTF, Daten.ENCODING_ISO },
                new String[] { Daten.ENCODING_UTF, Daten.ENCODING_ISO },
                IItemType.TYPE_PUBLIC, "",
                International.getStringWithMnemonic("Zeichensatz")
                );
        encoding.setFieldSize(100, -1);
        encoding.displayOnGui(this, mainPanel, 1, 2);

        csvSeparator = new ItemTypeString("CSVSEPARATOR", "|",
                IItemType.TYPE_PUBLIC, "", International.getString("Feldtrenner"));
        csvSeparator.setAllowedRegex("."); // exact one character
        csvSeparator.setFieldSize(100, -1);
        csvSeparator.displayOnGui(this, mainPanel, 1, 3);

        csvQuotes = new ItemTypeString("CSVQUOTED", "\"",
                IItemType.TYPE_PUBLIC, "", International.getString("Texttrenner"));
        csvQuotes.setAllowedRegex("."); // exact one character
        csvQuotes.setFieldSize(100, -1);
        csvQuotes.displayOnGui(this, mainPanel, 1, 4);

        String[] kf = persistence.createNewRecord().getQualifiedNameFieldsTranslateVirtualToReal();
        String kfs = "";
        for (int i=0; i<kf.length; i++) {
            if (!kf[i].equals(DataRecord.VALIDFROM)) {
                kfs = (kfs.length() > 0 ? kfs + ", " : "") + kf[i];
            }
        }
        ItemTypeString keyFields = new ItemTypeString("KEY_FIELDS", kfs,
                IItemType.TYPE_PUBLIC, "", International.getString("Schlüsselfelder"));
        keyFields.setEditable(false);
        keyFields.setFieldSize(450, -1);
        keyFields.setFieldGrid(2, -1, -1);
        keyFields.setPadding(0, 0, 20, 0);
        keyFields.displayOnGui(this, mainPanel, 0, 5);

        importMode = new ItemTypeStringList("IMPORTMODE", DataImport.IMPORTMODE_ADD,
                    new String[]{ DataImport.IMPORTMODE_ADD,
                                  DataImport.IMPORTMODE_UPD,
                                  DataImport.IMPORTMODE_ADDUPD },
                    new String[]{ International.getString("Datensätze neu hinzufügen"),
                                  International.getString("Vorhandene Datensätze aktualisieren"),
                                  International.getString("Datensätze hinzufügen oder aktualisieren") },
                    IItemType.TYPE_PUBLIC, "",
                    International.getStringWithMnemonic("Import-Modus"));
        importMode.registerItemListener(this);
        importMode.setFieldSize(450, -1);
        importMode.setFieldGrid(2, -1, -1);
        importMode.setPadding(0, 0, 20, 0);
        importMode.displayOnGui(this, mainPanel, 0, 10);

        if (persistence != null && persistence.data().getMetaData().isVersionized()) {
            validAtDateTime = new ItemTypeDateTime("VALID_AT",
                    (validAt < 0 ? DataTypeDate.today() : new DataTypeDate(validAt)),
                    (validAt < 0 ? DataTypeTime.now() : new DataTypeTime(validAt)),
                    IItemType.TYPE_PUBLIC, "", International.getString("Gültigkeitszeitpunkt für Import"));
            validAtDateTime.setNotNull(true);
            validAtDateTime.setFieldGrid(2, -1, -1);
            validAtDateTime.setFieldSize(450, -1);
            validAtDateTime.displayOnGui(this, mainPanel, 0, 11);
            importModeUpd = new ItemTypeStringList("IMPORTMODEUPD", DataImport.UPPMODE_CREATENEWVERSION,
                    new String[]{DataImport.UPDMODE_UPDATEVALIDVERSION, DataImport.UPPMODE_CREATENEWVERSION},
                    new String[]{International.getString("aktualisiere die zum angegebenen Zeitpunkt gültige Version"),
                        International.getString("erstelle eine neue Version mit angegebenem Gültigkeitsbeginn")},
                    IItemType.TYPE_PUBLIC, "",
                    International.getStringWithMnemonic("beim Aktualisieren eines Datensatzes"));
            importModeUpd.setFieldGrid(2, -1, -1);
            importModeUpd.setFieldSize(450, -1);
            importModeUpd.displayOnGui(this, mainPanel, 0, 12);
        }

    }

    public void keyAction(ActionEvent evt) {
        _keyAction(evt);
    }

    public void fileTypeChanged() {
        boolean csv = fileTypeCsv.isSelected();
        encoding.setEnabled(csv);
        csvSeparator.setEnabled(csv);
        csvSeparator.setNotNull(csv);
        csvQuotes.setEnabled(csv);
    }

    public void itemListenerAction(IItemType itemType, AWTEvent event) {
        if (itemType == file && (event instanceof ActionEvent || event instanceof FocusEvent)) {
            String fname = file.getValueFromField();
            try {
                BufferedReader f = new BufferedReader(new FileReader(fname));
                String s = f.readLine();
                if (s.startsWith("<?xml")) {
                    this.fileTypeXml.setSelected(true);
                } else {
                    this.fileTypeCsv.setSelected(true);
                }
                f.close();
            } catch(Exception e) {
                // nothing to do
            }
            fileTypeChanged();
        }
        if (itemType == importMode) {
            String mode = importMode.getValueFromField();
            if (importModeUpd != null) {
                importModeUpd.setEnabled(mode.equals(DataImport.IMPORTMODE_UPD) || mode.equals(DataImport.IMPORTMODE_ADDUPD));
            }
        }
    }

    public void closeButton_actionPerformed(ActionEvent e) {
        file.getValueFromField();
        String fname = file.getValue();
        encoding.getValueFromGui();
        csvSeparator.getValueFromGui();
        csvQuotes.getValueFromGui();

        long validAt = -1;
        if (validAtDateTime != null) {
            validAtDateTime.getValueFromGui();
            validAt = validAtDateTime.getTimeStamp();
        }


        if (!file.isValidInput()) {
            Dialog.error(International.getString(file.getInvalidErrorText()));
            return;
        }
        if (!csvSeparator.isValidInput()) {
            Dialog.error(International.getString(csvSeparator.getInvalidErrorText()));
            return;
        }
        if (!csvQuotes.isValidInput()) {
            Dialog.error(International.getString(csvQuotes.getInvalidErrorText()));
            return;
        }

        if (!(new File(fname).exists())) {
            Dialog.error(LogString.logstring_fileNotFound(fname, International.getString("Datei")));
            return;
        }
        char csep = (csvSeparator.getValue() != null ? csvSeparator.getValue().charAt(0) : '\0');
        char cquo = (csvQuotes.getValue() != null ? csvQuotes.getValue().charAt(0) : '\0');

        DataImport dataImport = new DataImport(persistence,
                fname, encoding.getValue(), csep, cquo,
                importMode.getValueFromField(), validAt,
                (importModeUpd != null ? importModeUpd.getValueFromField() : null));
        ProgressDialog progressDialog = new ProgressDialog(this, International.getMessage("{data} importieren", persistence.getDescription()), dataImport, false);
        dataImport.runImport(progressDialog);
    }

}