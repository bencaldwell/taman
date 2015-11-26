/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.taman;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Modify an existing UPPAAL template by adding variables, channels, instances etc.
 * Write the UPPAAL system to an xml file for input to UPPAAL.
 * 
 * @author bencaldwell
 */
public class TaSystem {
    
    private final Document document;
    
    public TaSystem(File templateFile) throws Exception {
        
        DocumentBuilderFactory docbf = DocumentBuilderFactory.newInstance();
        docbf.setNamespaceAware(true);
        DocumentBuilder docbuilder;
        Document _document = null;
        try {
            docbuilder = docbf.newDocumentBuilder();
            _document = docbuilder.parse(templateFile);
        } catch (ParserConfigurationException | SAXException | IOException ex) {
            Logger.getLogger(TaSystem.class.getName()).log(Level.SEVERE, null, ex);
            _document = null;
            throw new Exception("Could not create the base Uppaal system.", ex);
        } finally {
            this.document = _document;
        }
    }
    
    /**
     * Write the uppaal TA to an xml file.
     * 
     * @param outFile
     * @throws Exception 
     */
    public void Write(File outFile) throws Exception {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, "roles.dtd");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            
            // send DOM to file
            tr.transform(new DOMSource(document),
                    new StreamResult(new FileOutputStream(outFile)));
        } catch (TransformerException | FileNotFoundException ex) {
            Logger.getLogger(TaSystem.class.getName()).log(Level.SEVERE, null, ex);
            throw new Exception("Could not write Uppaal file.", ex);
        }
    }
    
    /**
     * Add a freehand declaration as a string.
     * @param declaration 
     */
    public void addDeclaration(String declaration) {
        // get the declaration section
        NodeList nodes = document.getElementsByTagName("declaration");
        String text = nodes.item(0).getTextContent();
        
        // add the variable declaration to the end of the section
        // no other structure can be enforced/expected in the file format
        text = text + System.lineSeparator() + declaration + ";";
        
        nodes.item(0).setTextContent(text);
    }
    
    /** 
     * Add a boolean variable to the declarations.
     * @param name 
     */
    public void addVarBool(String name) {
        String declaration = "int[0,1] " + name;
        this.addDeclaration(declaration);
    }
    
    /**
     * Add an integer variable to the declarations.
     * @param name 
     */
    public void addVarInt(String name) {
        String declaration = "int " + name;
        this.addDeclaration(declaration);
    }
    
    /**
     * Add a broadcast channel to the declarations.
     * @param name 
     */
    public void addBroadcastChan(String name) {
        String declaration = "broadcast chan " + name;
        this.addDeclaration(declaration);
    }
    
    /**
     * Add a channel to the declarations
     * @param name 
     */
    public void addChan(String name) {
        String declaration = "chan " + name;
        this.addDeclaration(declaration);
    }
    
    /**
     * Add a constant to the Declarations
     * @param name
     * @param value 
     */
    public void addConstInt(String name, int value) {
        String declaration = "const int " + name + " = " + value;
        this.addDeclaration(declaration);
    }
    
    /**
     * Add an instance of a template that exists in the base template file to the System declarations.
     * @param templateName
     * @param instanceName
     * @param args 
     */
    public void addInstance(String templateName, String instanceName, String... args) {
        // get the system declarations section
        NodeList nodes = document.getElementsByTagName("system");
        String text = nodes.item(0).getTextContent();
        
        // add the new instance
        String instanceDecl = instanceName + " = " + templateName + "(";
        instanceDecl = instanceDecl + StringUtils.join(args,",") + ");" + System.lineSeparator();
        text = instanceDecl + text;
        
        // find the "system ...;" line and append the new instance
        Pattern pattern = Pattern.compile("^system.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(text);
        matcher.find();
        String systemDecl = matcher.group();
        systemDecl = systemDecl.replace(";", ", " + instanceName + ";");
        text = matcher.replaceAll(systemDecl);
        nodes.item(0).setTextContent(text);
    }
    

}
