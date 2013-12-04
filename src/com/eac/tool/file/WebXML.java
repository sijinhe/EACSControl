/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eac.tool.file;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;

/**
 *
 * @author Sijin
 */
public class WebXML {

    //public static final String path = "C:\\Users\\Sijin\\Desktop\\web.xml";

    public WebXML() {
    }

    public void addJavamelodyListener(String path) {

        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = null;
        try {
            docBuilder = docFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(WebXML.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            Document doc = docBuilder.parse(path);

            Element webapp = doc.getDocumentElement();

            Node first = webapp.getFirstChild();

            Element filter = doc.createElement("filter");
            Element filtername = doc.createElement("filter-name");
            filtername.setTextContent("monitoring");
            Element filterclass = doc.createElement("filter-class");
            filterclass.setTextContent("net.bull.javamelody.MonitoringFilter");

            filter.appendChild(filtername);
            filter.appendChild(filterclass);

            webapp.insertBefore(filter, first);

            Element filtermapping = doc.createElement("filter-mapping");

            Element filtername1 = doc.createElement("filter-name");
            filtername1.setTextContent("monitoring");

            Element urlpattern = doc.createElement("url-pattern");

            urlpattern.setTextContent("/*");

            filtermapping.appendChild(filtername1);
            filtermapping.appendChild(urlpattern);

            webapp.appendChild(filtermapping);

            webapp.insertBefore(filtermapping, first);

            Element listener = doc.createElement("listener");
            Element listenerclass = doc.createElement("listener-class");
            listenerclass.setTextContent("net.bull.javamelody.SessionListener");

            listener.appendChild(listenerclass);

            webapp.appendChild(listener);

            webapp.insertBefore(listener, first);

            Transformer transformer = null;
            try {
                transformer = TransformerFactory.newInstance().newTransformer();
            } catch (TransformerConfigurationException ex) {
                Logger.getLogger(WebXML.class.getName()).log(Level.SEVERE, null, ex);
            }
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

//initialize StreamResult with File object to save to file
            StreamResult result = new StreamResult(new StringWriter());
            DOMSource source = new DOMSource(doc);
            try {
                transformer.transform(source, result);
            } catch (TransformerException ex) {
                Logger.getLogger(WebXML.class.getName()).log(Level.SEVERE, null, ex);
            }

            String xmlString = result.getWriter().toString();

            try {
                // Create file
                FileWriter fstream = new FileWriter(path);
                BufferedWriter out = new BufferedWriter(fstream);
                out.write(xmlString);
                //Close the output stream
                out.close();
            } catch (Exception e) {//Catch exception if any
                System.err.println("Error: " + e.getMessage());
            }


            System.out.println(xmlString);

        } catch (SAXException ex) {
            Logger.getLogger(WebXML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WebXML.class.getName()).log(Level.SEVERE, null, ex);
        }


    }

    public static void main(String[] args) {

        WebXML wx = new WebXML();
      //  wx.addJavamelodyListener(path);
    }
}
