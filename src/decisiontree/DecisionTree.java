/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package decisiontree;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author chad
 */
public class DecisionTree {
    
    DataSet trainingSet = null;
    DataSet testingSet = null;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DecisionTree dTree = new DecisionTree();
        dTree.run();
    }

    private DataSet buildSet(String filename) {
        DataSet set = null;
        try {
            set = new DataSet(filename);
        } catch (IOException ex) {
            Logger.getLogger(ID3Tree.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
        set.randomize();
        return set;
    }
    
    private void runLenseSet() {
        
        trainingSet = buildSet("lenses.csv");
        trainingSet.removeAttribute("lense");
        testingSet = trainingSet.removePercent(30);
        // build tree
        ID3Tree tree = new ID3Tree(trainingSet);
        print("\n\n");
        tree.printTree();
        
        // test tree
        evaluate(tree, testingSet);
    }
    
    private void runIrisSet() {
        
        trainingSet = buildSet("irisdata.csv");
        testingSet = trainingSet.removePercent(30);
        // build tree
        ID3Tree tree = new ID3Tree(trainingSet);
        print("\n\n");
        tree.printTree();
        
        // test tree
        evaluate(tree, testingSet);
    }
    
    private void runVoteSet() {
        //dTree.run("house-votes-84.csv");
            
        trainingSet = buildSet("house-votes-84.csv");
        trainingSet.setTarget("Class Name");
        testingSet = trainingSet.removePercent(30);
        // build tree
        ID3Tree tree = new ID3Tree(trainingSet);
        System.out.println("\n\n");
        tree.printTree();
        
        // test tree
        evaluate(tree, testingSet);
    }
    
    private void runAll() {
        runIrisSet();
        runLenseSet();
        runVoteSet();
    }

    private void evaluate(ID3Tree tree, DataSet testingSet) {
        print("\n\nEvaluating");
        double correct = 0;
        double total = 0;
        Iterator<DataPoint> iter = testingSet.iterator();
        while(iter.hasNext()) {
            DataPoint p = iter.next();
            String actualClass = p.getTargetValue();
            String testClass = tree.classify(p);
            print(actualClass + " : " + testClass);
            if(actualClass.equals(testClass)) {
                correct++;
            } 
            total++;
        }
        
        print("accuracy: " + (correct/total)*100 + "%");
    }
    
    private void print(String message) {
        System.out.println(message);
    }
    
    private int getInput() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int value = -1;
        try{
            value = Integer.parseInt(br.readLine());
        }catch(NumberFormatException nfe){
            System.err.println("Invalid Format!");
        } catch (IOException ex) {
            Logger.getLogger(DecisionTree.class.getName()).log(Level.SEVERE, null, ex);
        }
        return value;
    }

    private void run() {
        boolean done = false;
        while(!done) {
            print("Choose an option");
            displayOptions();
            int option = getInput();
            switch(option) {
                case 0:
                    done = true;
                    break;
                case 1:
                    this.runIrisSet();
                    break;
                case 2:
                    this.runLenseSet();
                    break;
                case 3:
                    this.runVoteSet();
                    break;
                default:
                    print("Invalid input");
            }
        }
        
    }

    private void displayOptions() {
        print("\t0. exit");
        print("\t1. run Iris data");
        print("\t2. run lense data");
        print("\t3. run voteing data");
    }
    
}
