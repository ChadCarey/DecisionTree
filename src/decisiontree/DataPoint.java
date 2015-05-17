/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author chad
 */
public class DataPoint extends HashMap<String, String> {
    
    String target = null;
  
    DataPoint() {
        ArrayList<String> keys = new ArrayList<String>();
        this.setTarget("not set");
    }
    
    
    public DataPoint(ArrayList<String> keys, String line) {
        if(line.isEmpty()) {
            System.err.println(this.getClass().getName() + "line was empty");
            System.exit(7);
        }
        //System.out.println(this.getClass().getName() + ":: " + line);
        String[] items = line.split(",");
        generateMissingKeys(keys, items.length);
        if(keys.size() != items.length){
            System.err.println(this.getClass().getName() + ":: invalid number of keys");
            System.exit(5);
        }
        
        // add each item to the map
        for(int i = 0; i < items.length; ++i) {
            //System.out.println(this.getClass().getName() + ":: adding " 
            //        + keys.get(i) + " : " + items[i] + "To data set");
            if(items[i] == null) {
                throw new NullPointerException();
            }
            this.put(keys.get(i), items[i]);
        }
        
        // set target to the last attribute by default
        this.setTarget(keys.get(keys.size()-1));
        
        if(this.target == null)
            System.err.println(this.getClass().getName() + ":: target is null!!!!");
        
    }
    
    public DataPoint(String key, String data) {
        this.put(key, data);
        if(key == null || data == null)
            throw new NullPointerException();
    }

    /**
     * copy constructor
     * @param p 
     */
    public DataPoint(DataPoint p) {
        // add all points
        Set<String> keys = p.keySet();
        Iterator<String> iter = keys.iterator();
        while(iter.hasNext()) {
            String key = iter.next();
            this.put(key, p.get(key));
        }
        // ensure target is set
        String tempTarget = p.getTarget();
        if(tempTarget == null)
            throw new NullPointerException();
        this.put(tempTarget, p.getTargetValue());
        this.setTarget(tempTarget);
        if(this.getTarget() == null || this.getTargetValue() == null) {
            System.err.println("Failed to copy correctly");
            System.err.println("target: " + this.getTarget());
            System.err.println("target: " + this.getTargetValue());
            System.exit(2);
        }
    }

    
    /**
     * Counts the number of attributes in this data point
     * (target class does not count as an attribute)
     * @return 
     */
    public int countAttributes() {
        int count = this.size();
        // the current size minus the target attribute if set
        if(count > 0 && target != null)
            count--;
            
        return this.size();
    }
   
    
    /**
     * ensures that there is one key for each value, if missing a key it will
     * generate a UUID as a key. If there are too many it will cut them off
     * the end of the list
     * @param keys
     * @param count 
     */
    private void generateMissingKeys(ArrayList <String> keys, int count) {
        while(keys.size() > count) {
            System.out.println("Too many keys");
            keys.remove(keys.size() - 1);
        }
        while(keys.size() < count) {
            System.out.println("Not enough keys");
            keys.add(this.genRandom32Hex());
        }
    }
    
    /**
     * returns a random string id
     * @return 
     */
    private String genRandom32Hex() {
        long MSB = 0x8000000000000000L;
        SecureRandom ng = new SecureRandom();
        String uuid = Long.toHexString(MSB | ng.nextLong()) + Long.toHexString(MSB | ng.nextLong());
        System.out.println(uuid);
        return uuid;
    }
    
    /**
     * set the target class
     * @param newTarget 
     */
    public void setTarget(String newTarget) {
        if(this.get(newTarget) != null && this.get(newTarget) != null) {
            this.target = newTarget;
        } else {
            System.out.println(this.getClass().getName() + ":: target was invalid, not set");
        }
    }

    /**
     * 
     * @return 
     */
    public String getTarget() {
        return this.target;
    }    

    /**
     * 
     * @return 
     */
    public String getTargetValue() {
        if(target == null) {
            System.err.println(this.getClass().getName() + " Target was null");
            throw new NullPointerException();
        }
        String tarVal = this.get(target);
        if(tarVal == null) {
            System.err.println(this.getClass().getName() + " Target value was null");
            System.err.println(this.getClass().getName() + " target : " + target);
            System.err.println(this.getClass().getName() + " target value : " + tarVal);
            System.err.println(this);
            throw new NullPointerException();
        }
        return tarVal;
    }
    
    @Override
    public String remove(Object key) {
        // if the removed key is the target, set the target to null
        if (target != null) {
            if (target.equals(key)) {
                System.out.println(this.getClass().getName() + ":: removed taget");
                target = null;
            }
        }
       
        return super.remove(key);
    }
    
    /**
     * returns a hard copy of the keySet
     * @return 
     */
   @Override
   public Set<String> keySet() {
       Set<String> keys = super.keySet();
       Set<String> newSet = new HashSet<String>();
       Iterator<String> iter = keys.iterator();
       while(iter.hasNext()) {
           newSet.add(iter.next());
       }
       return newSet;
   }
    
}
