/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This is a factory that handles building various objects that I commonly use
 * the idea is to create uniformity in my code and preventing the need to write 
 * the same thing twice. Note: unfinished
 * @author chad
 */
public class UtilityBelt {
    
    /**
     * returns a Buffered reader for the file indicated by the filename
     * @param filename
     * @return 
     */
    public static BufferedReader getFileReader(String filename) {
        File file = new File(filename);
        BufferedReader reader = null;
        if(file.exists()) {
            try {
                reader = new BufferedReader(new FileReader(file));
            } catch (FileNotFoundException ex) {
                System.err.println("Failed to create BufferedReader");
                Logger.getLogger(UtilityBelt.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return reader;
    }
}
