/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.taman;

import java.io.File;
import java.net.URL;
import org.apache.commons.io.FileUtils;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 *
 * @author bencaldwell
 */
public class TamanTest {
    
    static TaSystem ta;
    
    public TamanTest() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}

    @org.testng.annotations.BeforeClass
    public static void setUpClass() throws Exception {
        File outputFolder = new File(System.getProperty("user.dir"), "/output"); 
        FileUtils.cleanDirectory(outputFolder);
        
        URL templateURL = TamanTest.class.getResource("template.xml");
        File templateFile = new File(templateURL.getFile());
        ta = new TaSystem(templateFile);
    }

    @org.testng.annotations.AfterClass
    public static void tearDownClass() throws Exception {
        File outputfolder = new File(System.getProperty("user.dir"), "/output"); 
        File outFile = new File(outputfolder, "ta-output.xml");
        ta.Write(outFile);
    }

    @org.testng.annotations.BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @org.testng.annotations.AfterMethod
    public void tearDownMethod() throws Exception {
    }
    
    @Test
    public void addDeclarations(){
        ta.addVarBool("myTestBool");
        ta.addVarInt("myTestInt");
        ta.addBroadcastChan("myTestBroadcastChan");
        ta.addChan("myTestChan");
        ta.addConstInt("myTestConst", 123);
    }
    
    @Test
    public void addInstance(){
        ta.addInstance("INPUT", "myTestInput", "myTestBroadcastChan", "myTestBool");
    }
    
}
