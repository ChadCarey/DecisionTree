/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import decisiontree.DataPoint;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author chad
 */
public class TestDataPoint {
    
    DataPoint point;
    
    public TestDataPoint() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        int numItems = (int) (Math.random() * 100 + 100);
        ArrayList<String> keys = new ArrayList<String>();
        String line = "";
        String targetName = "";
        String targetVal = "";
        
        for(int i = 0; i < numItems; ++i) {
            keys.add((i+1)+"");
            line += i;
            if(i != numItems-1) {
                line += ",";
            } else {
                // save the last values as the targets
                targetName = (i+1)+"";
                targetVal = i+"";
            }
        }
        point = new DataPoint(keys,line);
    }
    
    @After
    public void tearDown() {
    }

    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    // @Test
    // public void hello() {}
    @Test
    public void testConstructors() {
        int numItems = (int) (Math.random() * 100 + 100);
        ArrayList<String> keys = new ArrayList<String>();
        String line = "";
        String targetName = "";
        String targetVal = "";
        
        for(int i = 0; i < numItems; ++i) {
            keys.add((i+1)+"");
            line += i;
            if(i != numItems-1) {
                line += ",";
            } else {
                // save the last values as the targets
                targetName = (i+1)+"";
                targetVal = i+"";
            }
        }
        
        // default
        DataPoint p1 = new DataPoint(keys, line);
        Assert.assertTrue("null",p1!=null);
        Assert.assertTrue("size",p1.size() == numItems);
        Assert.assertTrue("targetName",p1.getTarget().equals(targetName));
        Assert.assertTrue("targetVal",p1.getTargetValue().equals(targetVal));
        
        // copy
        DataPoint p2 = new DataPoint(p1);
        Assert.assertTrue("null",p2!=null);
        Assert.assertTrue("size",p2.size() == numItems);
        Assert.assertTrue("targetName",p2.getTarget().equals(targetName));
        Assert.assertTrue("targetVal",p2.getTargetValue().equals(targetVal));
        
        Set<String> keySet = p2.keySet();
        Iterator<String> keyIter = keySet.iterator();
        while(keyIter.hasNext()) {
            String key = keyIter.next();
            Assert.assertTrue(key, key != null);
            String keyValue = p2.get(key);
            Assert.assertTrue(key, p2.get(key) != null);
            Assert.assertTrue(keyValue, keyValue.equals(p1.get(key)));
        }
    }
    
    @Test
    public void testRemove() {
        DataPoint p = new DataPoint(point);
        
        Set<String> keySet = point.keySet();
        Iterator<String> keyIter = keySet.iterator();
        while(keyIter.hasNext()) {
            String key = keyIter.next();
            Assert.assertTrue("Key is null", key != null);
            String keyValue = p.remove(key);
            Assert.assertTrue("keyValue is null", keyValue != null);
            Assert.assertTrue("keyValue does not equal origonal", keyValue.equals(point.get(key)));
        }
    }
}
