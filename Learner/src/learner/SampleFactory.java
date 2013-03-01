/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 *
 * @author cbuntain
 */
public class SampleFactory {
    
    public static int getDimension() {
        
        /* + 1 for the home game stat */
        return TableInfo.STATS_COLS.size() + 1;
    }
    
    public static Sample getSampleFromGame(Game g) {
        
        Stats teamStats1 = g.getLastTeamStats();
        Stats teamStats2 = g.getLastOppStats();

        int isHome = g.isHomeGame();
        
        int sampleSize = getDimension();
        
        /* Get the differences between the teams stats */
        SortedMap<String,Double> stats = getStatDelta(teamStats1, teamStats2);
        
        double[] gameVector = new double[sampleSize];
        
        /* Default initialization just to be safe */
        for ( int i=0; i<sampleSize; i++ ) {
            gameVector[i] = 0;
        }
        
        /* Populate the game vector */
        int c = 0;
        for ( String key : TableInfo.STATS_COLS.keySet() ) {
            gameVector[c++] = stats.get(key).doubleValue();
        }
        gameVector[c] = isHome;
        
        /* Use the game vector to populate our sample */
        Sample s = new Sample(gameVector);
        s.setLabel(makeLabel(g));
        
        return s;
    }
    
    public static SortedMap<String,Double> getStatDelta(Stats s1, Stats s2) {
        TreeMap<String,Double> map = new TreeMap<String,Double>();
        
        for ( String stat : TableInfo.STATS_COLS.keySet() ) {
            
            Double statValue1 = s1.getStat(stat);
            Double statValue2 = s2.getStat(stat);
            
            Double delta = statValue1 - statValue2;
            
            map.put(stat, delta);
        }
        
        return map;
    }
    
    public static int makeLabel(Game g) {
        
        int label = 0;
        
        if ( g.getTeamScore() > g.getOppScore() ) {
            label = 1;
        } else {
            label = -1;
        }
        
        return label;
    }
    
}
