/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

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
    private String rootName;
    
    ID3Tree(DataSet set) {
        // init membre variables
        branches = new HashMap<String, ID3Tree>();
        if(set.getAttributeCount() > 0 && set.size() > 0) {
            // get the lowest entropy attribute
            rootName = this.getRootAttribute(set);
            System.out.println(this.getClass().getName() + ":: rootName == " +rootName + "\n");

            // get the names of the attribute values
            Set<String> attributeValues = set.getAttributeValues(rootName);
            Iterator<String> valueIter = attributeValues.iterator();
            while(valueIter.hasNext()) {
                String value = valueIter.next();
                DataSet nextSet = set.getSetWhere(rootName, value);
                if(!nextSet.isEmpty()) {
                    branches.put(value, new ID3Tree(nextSet));
                } else {
                    // leaf
                    this.rootName = set.getMaxTarget();
                }
            }
        } 
        
        // all attributes have been accounted for. save the average target value
        if(branches.isEmpty()) {
            rootName = set.getMaxTarget();
            System.out.println(this.getClass().getName() + ":: Leaf node found");
            System.out.println(this.getClass().getName() + ":: Root name == " + rootName + "\n");
        
        }
    }
    
    private ID3Tree(String rootValue) {
        branches = new HashMap<String, ID3Tree>();
        this.rootName = rootValue;
    }
    
    /**
     * calculate the entropy based on an array of integers
     * @param optionCounts
     * @return 
     */
    public double calculateEntropy(ArrayList<Integer> optionCounts) {
        // get the total
        int totalCount = 0;
        System.out.println("optionaCounts.len: " + optionCounts.size());
        for(int i = 0; i < optionCounts.size(); ++i) {
            totalCount += optionCounts.get(i);
        }
        System.out.println("totalCount: " + totalCount);
        
        // calculate the entropy
        double entropy = 0;
        for (int i = 0; i < optionCounts.size(); ++i) {
            double optionCount = optionCounts.get(i);
            entropy = entropy -((optionCount/totalCount) * log2(optionCount/totalCount));
            System.out.println("cal ent: " + entropy);
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
        double entropy = 100;
        // get the total number of datapoints
        double totalDataPoints = set.size();
        
        // get a set with for each value of this attribute
        Set<String> attributeValues = set.getAttributeValues(attributeName);
        
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
                try {
                    if(p.get(attributeName) == atVal) {
                        // we found a DataPoint with the serched for attribute value
                        atValueCount++;
                        // get the associated target value for this data point
                        String tarVal = p.getTargetValue();
                        if(tarVal == null)
                            throw new NullPointerException();
                        // count the occurances each targe value
                        counter.count(tarVal);
                    }
                } catch (Exception ex) {
                    Logger.getLogger(ID3Tree.class.getName()).log(Level.SEVERE, null, ex);
                    System.exit(4);
                }
            }
            // after occurances counting we calculate he weighted entropy for the current
            // attribute value. and app it to a running total weight
            entropy += this.calculateEntropy(counter.getCounts()) * atValueCount / totalDataPoints;
        }
        /*Iterator<DataPoint> pointIter = set.iterator();
        int totalDataPoints = set.size();
        while(pointIter.hasNext()) {
            DataPoint point = pointIter.next();
            String attributeVal = point.get(attributeName);
        }*/
        
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
        String branchName = point.get(this.rootName);
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
            return this.rootName;
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
//        System.out.println(this.rootName);
        String outputText = this.rootName + "{ ";
        Set<String> kSet = this.branches.keySet();
        Iterator<String> iter = kSet.iterator();
        while(iter.hasNext()) {
            String branchName = iter.next();
            ID3Tree nextTree = branches.get(branchName);
            outputText += nextTree.rootName + "\t";
            nextTree.printTree();
        }
        System.out.println(outputText + " }");
    }
    
}
