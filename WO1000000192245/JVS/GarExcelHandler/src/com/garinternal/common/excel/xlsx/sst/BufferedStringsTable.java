package com.garinternal.common.excel.xlsx.sst;

/*
File Name:                      BufferedStringsTable.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
Class to read excel data as a buffer

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public class BufferedStringsTable extends SharedStringsTable {
    private final FileBackedList list;

    private BufferedStringsTable(PackagePart part, File file, int cacheSizeBytes) throws IOException {
        this.list = new FileBackedList(file, cacheSizeBytes);
        this.readFrom(part.getInputStream());
    }

    /**
     * Get shared strings table
     * 
     * @param tmp            File temp
     * @param cacheSizeBytes Cache size in bytes
     * @param pkg            OPCPackage
     * @return BufferedStringsTable
     * @throws IOException {@link IOException}
     */
    public static BufferedStringsTable getSharedStringsTable(File tmp, int cacheSizeBytes, OPCPackage pkg) throws IOException {
        List<PackagePart> parts = pkg.getPartsByContentType(XSSFRelation.SHARED_STRINGS.getContentType());
        return parts.isEmpty() ? null : new BufferedStringsTable(parts.get(0), tmp, cacheSizeBytes);
    }

    @Override
    public void readFrom(InputStream is) throws IOException {

        try {
            XMLEventReader xmlEventReader = XMLHelper.newXMLInputFactory().createXMLEventReader(is);

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();

                if (xmlEvent.isStartElement() && "si".equals(xmlEvent.asStartElement().getName().getLocalPart())) {
                    this.list.add(this.parseCtRst(xmlEventReader));
                }

            }

        } catch (XMLStreamException e) {
            throw new IOException(e);
        }

    }

    /**
     * Parses a {@code <si>} String Item. Returns just the text and drops the formatting. See <a
     * href="https://msdn.microsoft.com/en-us/library/documentformat.openxml.spreadsheet.sharedstringitem.aspx">xmlschema
     * type {@code CT_Rst}</a>.
     */
    private String parseCtRst(XMLEventReader xmlEventReader) throws XMLStreamException {
        // Precondition: pointing to <si>; Post condition: pointing to </si>
        StringBuilder buf = new StringBuilder();
        XMLEvent      xmlEvent;

        while ( (xmlEvent = xmlEventReader.nextTag()).isStartElement()) {

            switch (xmlEvent.asStartElement().getName().getLocalPart()) {
                case "t": // Text
                    buf.append(xmlEventReader.getElementText());
                    break;

                case "r": // Rich Text Run
                    this.parseCtRElt(xmlEventReader, buf);
                    break;

                case "rPh": // Phonetic Run
                case "phoneticPr": // Phonetic Properties
                    this.skipElement(xmlEventReader);
                    break;

                default:
                    throw new IllegalArgumentException(xmlEvent.asStartElement().getName().getLocalPart());
            }

        }

        return buf.toString();
    }

    /**
     * Parses a {@code <r>} Rich Text Run. Returns just the text and drops the formatting. See <a
     * href="https://msdn.microsoft.com/en-us/library/documentformat.openxml.spreadsheet.run.aspx">xmlschema
     * type {@code CT_RElt}</a>.
     */
    private void parseCtRElt(XMLEventReader xmlEventReader, StringBuilder buf) throws XMLStreamException {
        // Precondition: pointing to <r>; Post condition: pointing to </r>
        XMLEvent xmlEvent;

        while ( (xmlEvent = xmlEventReader.nextTag()).isStartElement()) {

            switch (xmlEvent.asStartElement().getName().getLocalPart()) {
                case "t": // Text
                    buf.append(xmlEventReader.getElementText());
                    break;

                case "rPr": // Run Properties
                    this.skipElement(xmlEventReader);
                    break;

                default:
                    throw new IllegalArgumentException(xmlEvent.asStartElement().getName().getLocalPart());
            }

        }

    }

    private void skipElement(XMLEventReader xmlEventReader) throws XMLStreamException {

        // Precondition: pointing to start element; Post condition: pointing to end element
        while (xmlEventReader.nextTag().isStartElement()) {
            this.skipElement(xmlEventReader); // recursively skip over child
        }

    }

    @Override
    public RichTextString getItemAt(int idx) {
        return new XSSFRichTextString(this.list.getAt(idx));
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.list.close();
    }
}
