/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 *  
 * @author chad
 */
public class DataSet extends ArrayList<DataPoint> {

    
    /**
     * builds an empty DataSet
     * @param filename 
     */
    DataSet() {
        super();
    }
    
    /**
     * builds a DataSet from a csv file
     * @param filename 
     */
    DataSet(String filename) throws IOException {
        super();
        BufferedReader reader = UtilityBelt.getFileReader(filename);
        // get the header
        String[] h = reader.readLine().split(",");
        ArrayList<String> header = new ArrayList<String>();
        for(int i = 0; i < h.length; ++i) {
            header.add(h[i]);
        }
        // read in the data
        String data = reader.readLine();
        while(data != null) {
            if(!data.isEmpty()) {
                this.add(new DataPoint(header, data));
            }
            data = reader.readLine();
        }
        reader.close();
    }

    /**
     * gets the number of attributes
     * @return 
     */
    public int getAttributeCount() {
        DataPoint p = this.getOne();
        return p.countAttributes();
    }
    
    /**
     * removes the attribute from the entire set
     * @param attribute 
     */
    public void removeAttribute(String attributeName) {
        Iterator<DataPoint> iter = this.iterator();
        while(iter.hasNext()) {
            iter.next().remove(attributeName);
        }
    }
    
    /**
     * returns a new data set with the attribute and it's
     * corresponding target values (if set)
     * @param attributeName
     * @return 
     */
    public DataSet getAttributes(String attributeName) {
        DataSet set = new DataSet();
        Iterator<DataPoint> pointIter = this.iterator();
        while(pointIter.hasNext()) {
            DataPoint p = pointIter.next();
            String attributeValue = p.get(attributeName);
            if(attributeValue != null) {
                //System.exit(3);

                DataPoint newPoint = new DataPoint(attributeName, attributeValue);
                String target = p.getTarget();
                if(target == null) {
                    throw new NullPointerException();
                }

                String targetValue = p.getTargetValue();
                if(targetValue == null) {
                    throw new NullPointerException();
                }

                newPoint.put(target, targetValue);
                newPoint.setTarget(target);
                set.add(newPoint);
            } else {
                System.err.println(this.getClass().getName() + "attVal was null");
            }
        }
        return set;
    }
    
    /**
     * returns only the attribute names
     * @return 
     */
    public Set<String> getAttributeNames() {
        if(this.size() > 0) {
            Set<String> keys = this.getOne().keySet();
            keys.remove(this.getOne().getTarget());
            return keys;
        } else {
            return new HashSet<String>();
        }
    }
    
    /**
     * randomizes the contents of the DataSet
     */
    public void randomize() {
        int arraySize = this.size();
        for(int i = 0; i < arraySize; ++i) {
            int index = (int) (Math.random() * arraySize);
            int swap = (int) (Math.random() * arraySize);
            DataPoint p = this.get(index);
            this.set(index, this.get(swap));
            this.set(swap, p);
        }
    }
    
    /**
     * removes the last X percent of the DataSet and returns it as a new DataSet
     * @param percent
     * @return 
     */
    public DataSet removePercent(double percent){
        // check percent formatting
        while (percent > 1) {
            percent = percent / 10;
        }
        DataSet newSet = new DataSet();
        int removeCount = (int) (Math.random() * (double)this.size());
        
        for(int i = 0; i < removeCount; ++i) {
            newSet.add(this.remove(this.size()-1));
        }
        return newSet;
    }
    
    /**
     * adds the DataSet to the current Data set if the DataPoints have the 
     * same number of attributes
     * @param set 
     */
    public void add(DataSet set) {
        if(isLikeSet(set) || this.isEmpty()) {
            for(int i = 0; i < set.size(); ++i) {
                this.add(set.get(i));
            }
        }
    }
    
    /**
     * returns whether these two sets are alike, (same number of attributes)
     * if one or both are empty
     * @param set
     * @return 
     */
    public boolean isLikeSet(DataSet set) {
        int thisCount = this.getAttributeCount();
        int thatCount = set.getAttributeCount();
        if(thisCount == thatCount 
                && this.getAttributeNames().containsAll(set.getAttributeNames())) {
            return true;
        }
        else
            return false;
    }
    
    /**
     * adds the DataPoint to the object if the DataPoint has the correct
     * number of attributes
     * @param point
     * @return 
     */
    @Override
    public boolean add(DataPoint point) {
        int thisCount = this.getAttributeCount();
        if (thisCount == 0 || point.countAttributes() == thisCount) {
            return super.add(point);
        }
        else {
            return false;
        }
    }
    
    /**
     * returns a set of all of the different values for the given attribute
     * in the dataSet
     */
    public Set<String> getAttributeValues(String attribute){
        Set<String> set = new HashSet<String>();
        Iterator<DataPoint> iter = this.iterator();
        while(iter.hasNext()) {
            DataPoint p = iter.next();
            String atVal = p.get(attribute);
            if(atVal != null) {
              //  System.err.println(this.getClass().getName() + ":: atVal was null\n" + this);
            //} else {
                set.add(atVal);    
            }
        }
        return set;
    }
    
    /**
     * returns the target
     * @return 
     */
    public String getTarget() {
        DataPoint point = this.getOne();
        if(point != null) {
            return point.getTarget();
        } else {
            System.err.println(this.getClass().getName() + ":: point in getTarget() was null");
            return null;
        }
    }
    
    /**
     * sets the target attribute to the given string if the string is a valid
     * attribute in this set, else is sets it to null
     * @param newTarget 
     */
    public void setTarget(String newTarget) {
        Iterator<DataPoint> points = this.iterator();
        while(points.hasNext()) {
            points.next().setTarget(newTarget);
        }
    }
    
    /**
     * returns a new data set consisting of only the items where the attribute
     * has the specified value
     * @param attribute
     * @param value
     * @return 
     */
    public DataSet getSetWhere(String attribute, String value) {
        DataSet set = new DataSet();
        Iterator<DataPoint> iter = this.iterator();
        while(iter.hasNext()) {
            DataPoint p = iter.next();
            if(p.getTargetValue() == null)
                throw new NullPointerException();
            String attributeVal = p.get(attribute);
            if(attributeVal == null) {
                throw new NullPointerException();
            }
                // if the attributeVal is the value create a new point and add it
            if(attributeVal.equals(value)) {
                DataPoint newPoint = new DataPoint(p);
                // since this attibute now is of only this value, remove the column
                newPoint.remove(attribute);
                set.add(newPoint);
            }
        }
        
        return set;
    }
    
    /**
     * 
     * @param attribute
     * @param value
     * @return 
     */
    public int getValueCount(String attribute, String value) {
        Iterator<DataPoint> iter = this.iterator();
        int count = 0;
        while(iter.hasNext()) {
            DataPoint point = iter.next();
            if(point.get(attribute).equals(value))
                count++;
        }
        return count;
    }

    public Set<String> getTargetValues() {
        return this.getAttributeValues(this.getTarget());
    }

    /**
     * returns the maximum occurring target value
     * @return 
     */
    String getMaxTarget() {

        Iterator<DataPoint> valueIter = this.iterator();
        AttributeCounter counter = new AttributeCounter();
        String maxVal = null;
        int maxCount = 0;
        while(valueIter.hasNext()) {
            String value = valueIter.next().getTargetValue();
            int count = counter.count(value);
            if(count > maxCount) {
                maxCount = count;
                maxVal = value;
            }
        }
        System.out.println(this.getClass().getName() + ":: " + maxVal);
        return maxVal;
    }
    
    /**
     * gets a single data point, returns an empty point if there are no points.
     * @return 
     */
    private DataPoint getOne() {
        DataPoint p;
        if(!this.isEmpty())
            p = this.get(0);
        else
            p = new DataPoint();
        
        return p;
    }

}
