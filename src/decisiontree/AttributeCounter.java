/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author chad
 */
public class AttributeCounter {
    
    private HashMap<String,Integer> counts = new HashMap<String,Integer>();
    
    /**
     * counts the number of times that the attribute is encountered
     * @param attribute
     * @return 
     */
    public int count(String attribute) throws NullPointerException {
        if(attribute == null) {
            throw new NullPointerException();
        }
        Integer value = counts.get(attribute);
        if(value == null) {
            value = 1;
        } else {
            value += 1;
        }
        System.out.println("counting " + attribute + ": " + value);
        counts.put(attribute, value);
        return value;
    }
    
    /**
     * resets the counter
     */
    public void reset() {
        counts.clear();
    }
    
    /**
     * returns an Integer array of the attribute counts
     * @return 
     */
    public ArrayList<Integer> getCounts() {
        ArrayList<Integer> output = new ArrayList();
        Set<String> keySet = counts.keySet();
        Iterator<String> keysIter = keySet.iterator();
        while(keysIter.hasNext()) {
            output.add(counts.get(keysIter.next()));
        }
        return output;
    }
}
