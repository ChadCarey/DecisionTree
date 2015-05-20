package decisiontree;

import DataSet.DataPoint;
import DataSet.DataSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author chad
 */
public class DataNode {
    
    private HashMap<String, DataNode> branches;
    private String nodeName;
    
    DataNode(DataSet set, String attribute) {
        nodeName = attribute;
        branches = new HashMap<String,DataNode>();
    }
    
    /**
     * 
     * @param branchName
     * @return 
     */
    public DataNode getBranch(String branchName) {
        return branches.get(branchName);
    }
    
    /**
     * 
     * @param branchName
     * @param branch 
     */
    public void addBranch(String branchName, DataNode branch) {
        branches.put(branchName, branch);
    }   
    
    /**
     * finds the class of the given dataPoint
     * @param point
     * @return 
     */
    public String getClass(DataPoint point) {
        String branchName = point.get(this.nodeName);
        DataNode branch = branches.get(branchName);
        if(branch != null) {
            return branch.getClass(point);
        } else if(branches.size() > 0) {
            // value is missing. go down each branch
            Set<String> allBranches = branches.keySet();
            ArrayList<String> answers = new ArrayList<String>();
            Iterator<String> branchIter = allBranches.iterator();
            while(branchIter.hasNext()) {
                String b = branchIter.next();
                DataNode node = branches.get(b);
                answers.add(node.getClass(point));
            }
            return average(answers);
        }
        return this.nodeName;
    }

    private String average(ArrayList<String> answers) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
