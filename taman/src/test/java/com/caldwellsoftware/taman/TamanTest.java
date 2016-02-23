/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.caldwellsoftware.taman;

import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
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
    @Test(enabled=true)
    public static void setUpClass() throws Exception {
        Path outputPath = Paths.get(System.getProperty("user.dir"), "output");
        File outputFolder = outputPath.toFile(); 
        if (outputFolder.exists()) {
            FileUtils.cleanDirectory(outputFolder);
        }
        
        URL templateURL = TamanTest.class.getResource("template.xml");
        File templateFile = new File(templateURL.toURI());
        ta = new TaSystem(templateFile);
    }

    @org.testng.annotations.AfterClass
    @Test(enabled=true)
    public static void tearDownClass() throws Exception {
        Path outputPath = Paths.get(System.getProperty("user.dir"), "output", "ta-output.xml");
        outputPath.getParent().toFile().mkdirs();
        File outFile = outputPath.toFile();
        ta.Write(outFile);
    }

    @org.testng.annotations.BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @org.testng.annotations.AfterMethod
    public void tearDownMethod() throws Exception {
    }
    
    @Test(enabled=true)
    public void addDeclarations(){
        ta.addVarBool("myTestBool");
        ta.addVarInt("myTestInt");
        ta.addBroadcastChan("myTestBroadcastChan");
        ta.addChan("myTestChan");
        ta.addConstInt("N_GENES", 4);
    }
    
    @Test(enabled=true)
    public void addInstance(){
        ta.addInstance("INPUT", "myTestInput", "myTestBroadcastChan", "myTestBool");
    }
    
}
