/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chad
 */
public class DecisionTree {
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DecisionTree dTree = new DecisionTree();
        dTree.run("irisdata.csv");
        //dTree.run("lenses.csv");
        //dTree.run("house-votes-84.csv");
    }

    
    private void run(String filename) {
        DataSet trainingSet = null;
        DataSet testingSet = null;
        try {
            trainingSet = new DataSet(filename);
            trainingSet.randomize();
            testingSet = trainingSet.removePercent(30);
        } catch (IOException ex) {
            Logger.getLogger(ID3Tree.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        
        // build tree
        ID3Tree tree = new ID3Tree(trainingSet);
        System.out.println("\n\n");
        tree.printTree();
        
        // test tree
        evaluate(tree, testingSet);
    }

    private void evaluate(ID3Tree tree, DataSet testingSet) {
        System.out.println("\n\nEvaluating");
        double correct = 0;
        double total = 0;
        Iterator<DataPoint> iter = testingSet.iterator();
        while(iter.hasNext()) {
            DataPoint p = iter.next();
            String actualClass = p.getTargetValue();
            String testClass = tree.classify(p);
            System.out.println(actualClass + " : " + testClass);
            if(actualClass.equals(testClass)) {
                correct++;
            } 
            total++;
        }
        
        System.out.println("accuracy: " + (correct/total)*100 + "%");
    }
    
}
