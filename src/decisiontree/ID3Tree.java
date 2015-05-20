/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

import DataSet.DataPoint;
import DataSet.DataSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * 
 * @author chad
 */
public class ID3Tree {
    
    private HashMap<String, ID3Tree> branches;
    private String nodeName;
    
    ID3Tree(DataSet set) {
        // init membre variables
        branches = new HashMap<String, ID3Tree>();
        if(set.getAttributeCount() > 0 && set.size() > 0) {
            // get the lowest entropy attribute
            nodeName = this.getRootAttribute(set);
            System.out.println(this.getClass().getName() + ":: nodeName == " +nodeName + "\n");

            // get the names of the attribute values
            Set<String> attributeValues = set.getAttributeValues(nodeName);
            Iterator<String> valueIter = attributeValues.iterator();
            while(valueIter.hasNext()) {
                String value = valueIter.next();
                DataSet nextSet = set.getSetWhere(nodeName, value);
                if(!nextSet.isEmpty()) {
                    branches.put(value, new ID3Tree(nextSet));
                } else {
                    // leaf
                    this.nodeName = set.getMaxTarget();
                }
            }
        } 
        
        // all attributes have been accounted for. save the average target value
        if(branches.isEmpty()) {
            nodeName = set.getMaxTarget();
            System.out.println(this.getClass().getName() + ":: Leaf node found");
            System.out.println(this.getClass().getName() + ":: nodeName == " + nodeName + "\n");
        
        }
    }
    
    private ID3Tree(String rootValue) {
        branches = new HashMap<String, ID3Tree>();
        this.nodeName = rootValue;
    }
    
    /**
     * calculate the entropy based on an array of integers
     * @param optionCounts
     * @return 
     */
    public double calculateEntropy(ArrayList<Integer> optionCounts) {
        // get the total
        int totalCount = 0;
        //System.out.println(this.getClass().getName() + ":: optionaCounts.len: " + optionCounts.size());
        for(int i = 0; i < optionCounts.size(); ++i) {
            totalCount += optionCounts.get(i);
        }
        //System.out.println(this.getClass().getName() + ":: totalCount: " + totalCount);
        
        // calculate the entropy
        double entropy = 0;
        for (int i = 0; i < optionCounts.size(); ++i) {
            double optionCount = optionCounts.get(i);
            entropy = entropy -((optionCount/totalCount) * log2(optionCount/totalCount));
            //System.out.println(this.getClass().getName() + ":: cal ent: " + entropy);
        }
        return entropy;
    }
    
    /**
     * calculate the entropy of a specific attribute of a data set
     * @param set
     * @param attributeName
     * @return 
     */
    public double calculateEntropy(DataSet set, String attributeName) {
        double entropy = 0;
        // get the total number of datapoints
        double totalDataPoints = set.size();
        
        // get a set with for each value of this attribute
        Set<String> attributeValues = set.getAttributeValues(attributeName);
            // System.out.println(this.getClass().getName() + ":: at values\n\t"+ attributeValues);
        // itterate through each attributeValue
        Iterator<String> atValIter = attributeValues.iterator();
        while(atValIter.hasNext()) {
            // get an attribute value
            String atVal = atValIter.next();
            // inicialize a counter that will  keep track of the number of counts
            // of the attribute with this given value
            double atValueCount = 0;
            if(atVal == null)
                throw new NullPointerException();
            
            // count each targetValue with this attribute value
            AttributeCounter counter = new AttributeCounter();
            Iterator<DataPoint> iter = set.iterator();
            while(iter.hasNext()) {
                // get a dataPoint
                DataPoint p = iter.next();
                // if the current dataPoint has the searched for attribute value
                if(p.get(attributeName) == atVal) {
                        //System.out.println(this.getClass().getName() + ":: ent point: " + p);
                    // we found a DataPoint with the serched for attribute value
                    // increment counter
                    atValueCount++;
                    
                    // get the associated target value for this data point
                    String tarVal = p.getTargetValue();
                    
                    // count the occurances of each target for searched for dataPoints
                    int temp = counter.count(tarVal);
                        //System.out.println(this.getClass().getName() + ":: current count for " 
                          //  + attributeName + " : " + tarVal + " is " + temp);
                }
                
            }
            // after occurances counting we calculate he weighted entropy for the current
            // attribute value. and app it to a running total weight
            ArrayList<Integer> counts = counter.getCounts();
            /*System.out.println(this.getClass().getName() + ":: entropy calculations"
            + "\n\tNumber of items counted: " + counts
            + "\n\tAttributes with this value: " + atValueCount
            + "\n\tTotal DataPoints: " + totalDataPoints);*/
            entropy += this.calculateEntropy(counter.getCounts()) * atValueCount / totalDataPoints;
            //System.out.println("\tCurrent entropy: " + entropy);
        }
        //System.out.println(this.getClass().getName() + ":: entropy " + entropy);
        return entropy;
    }
    
    
    /**
     * determines the lowest entropy attribute
     * Note: currently this is a very slow algorithm.
     * @param set
     * @return 
     */
    private String getRootAttribute(DataSet set) {
        
        // get the attribute keys
        Set<String> attributeKeys = set.getAttributeNames();
        String best = null;
        double lowestEnt = 100.0; // high value ensures replacement
        // loop through each key and check it's entropy
        Iterator<String> keyIter = attributeKeys.iterator();
        while(keyIter.hasNext()) {
            String attribute = keyIter.next();
            double entropy = this.calculateEntropy(set, attribute);
            if(entropy < lowestEnt) {
                lowestEnt = entropy;
                best = attribute;
            }
        }
        return best;
    }

    /**
     * calculates log base 2
     * @param d
     * @return 
     */
    private double log2(double d) {
        if(d != 0)
            return Math.log(d) / Math.log(2);
        else
            return 0.0;
    }
    
    /**
     * finds the class of the given dataPoint
     * @param point
     * @return 
     */
    public String classify(DataPoint point) {
        // get the attribute value for the point
        String branchName = point.get(this.nodeName);
        // get the correct branch for the point DataPoint's value
        ID3Tree branch = branches.get(branchName);
        if(branch != null) {
            return branch.classify(point);
        } else if(branches.size() > 0) {
            // value is missing. go down each branch
            Set<String> allBranches = branches.keySet();
            ArrayList<String> answers = new ArrayList<String>();
            Iterator<String> branchIter = allBranches.iterator();
            while(branchIter.hasNext()) {
                String b = branchIter.next();
                ID3Tree node = branches.get(b);
                answers.add(node.classify(point));
            }
            return average(answers);
        } else {
            return this.nodeName;
        }
    }

    /**
     * returns the most commonly occurring value
     * @param answers
     * @return 
     */
    private String average(ArrayList<String> answers) {
        AttributeCounter counter = new AttributeCounter();
        String best = null;
        int bestCount = 0;
        Iterator<String> iter = answers.iterator();
        while(iter.hasNext()) {
            String att = iter.next();
            int current = counter.count(att);
            if(current > bestCount) {
                bestCount = current;
                best = att;
            }
        }
        return best;
    }
    
    /**
     * displays the current tree
     */
    public void printTree() {
        // display

        String outputText = this.nodeName + "-" + this.hashCode() + "{ ";
        Set<String> kSet = this.branches.keySet();
        Iterator<String> iter = kSet.iterator();
        while(iter.hasNext()) {
            String branchName = iter.next();
            ID3Tree nextTree = branches.get(branchName);
            outputText += nextTree.nodeName +  "-" + nextTree.hashCode() + "\t";
            nextTree.printTree();
        }
        System.out.println(outputText + " }");
    }
    
}
