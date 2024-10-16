package com.garinternal.common.excel.xlsx;

/*
File Name:                      XmlUtils.java

Script Type:                    INCLUDE
Parameter Script:               None
Display Script:                 None

Description:
This script contains XML utility methods to read .xlsx files.

---------------------------------------------------------------------------
 REQ No          | Release Date| Author     | Changes
---------------------------------------------------------------------------
 WO1000000135255 |             | Khalique   | Initial Version
                 |             |            |
---------------------------------------------------------------------------
 */

import com.garinternal.common.excel.xlsx.exceptions.ParseException;
import com.olf.openjvs.PluginCategory;
import com.olf.openjvs.PluginType;
import com.olf.openjvs.enums.SCRIPT_CATEGORY_ENUM;
import com.olf.openjvs.enums.SCRIPT_TYPE_ENUM;

import org.apache.poi.ooxml.util.DocumentHelper;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

@PluginCategory(SCRIPT_CATEGORY_ENUM.SCRIPT_CAT_GENERIC)
@PluginType(SCRIPT_TYPE_ENUM.INCLUDE_SCRIPT)

public final class XmlUtils {

    /**
     * Constructor
     */
    private XmlUtils() {
        // do nothing
    }

    /**
     * Returns Document to read data
     *
     * @param is Input Stream
     * @return Document
     */
    public static Document document(InputStream is) {

        try {
            return DocumentHelper.readDocument(is);
        } catch (SAXException | IOException e) {
            throw new ParseException(e);
        }

    }

    /**
     * Search for node list
     *
     * @param document Document
     * @param xpath    xpath
     * @return NodeList
     */
    public static NodeList searchForNodeList(Document document, String xpath) {

        try {
            XPath                xp = XPathFactory.newInstance().newXPath();
            NamespaceContextImpl nc = new NamespaceContextImpl();
            nc.addNamespace("ss", "http://schemas.openxmlformats.org/spreadsheetml/2006/main");
            xp.setNamespaceContext(nc);
            return (NodeList) xp.compile(xpath).evaluate(document, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new ParseException(e);
        }

    }

    private static class NamespaceContextImpl implements NamespaceContext {
        private Map<String, String> urisByPrefix = new HashMap<>();

        private Map<String, Set<String>> prefixesByURI = new HashMap<>();

        public NamespaceContextImpl() {
            this.addNamespace(XMLConstants.XML_NS_PREFIX, XMLConstants.XML_NS_URI);
            this.addNamespace(XMLConstants.XMLNS_ATTRIBUTE, XMLConstants.XMLNS_ATTRIBUTE_NS_URI);
        }

        public void addNamespace(String prefix, String namespaceURI) {
            this.urisByPrefix.put(prefix, namespaceURI);

            if (this.prefixesByURI.containsKey(namespaceURI)) {
                (this.prefixesByURI.get(namespaceURI)).add(prefix);
            } else {
                Set<String> set = new HashSet<>();
                set.add(prefix);
                this.prefixesByURI.put(namespaceURI, set);
            }

        }

        @Override
        public String getNamespaceURI(String prefix) {

            if (prefix == null) {
                throw new IllegalArgumentException("prefix cannot be null");
            }

            if (this.urisByPrefix.containsKey(prefix)) {
                return this.urisByPrefix.get(prefix);
            } else {
                return XMLConstants.NULL_NS_URI;
            }

        }

        @Override
        public String getPrefix(String namespaceURI) {
            return this.getPrefixes(namespaceURI).next();
        }

        @Override
        @SuppressWarnings("unchecked")
        public Iterator<String> getPrefixes(String namespaceURI) {

            if (namespaceURI == null) {
                throw new IllegalArgumentException("namespaceURI cannot be null");
            }

            if (this.prefixesByURI.containsKey(namespaceURI)) {
                return (this.prefixesByURI.get(namespaceURI)).iterator();
            } else {
                return Collections.EMPTY_SET.iterator();
            }

        }
    }
}
