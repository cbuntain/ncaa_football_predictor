/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author cbuntain
 */
public class Stats {
    
    private static HashMap<String, Double> MAX_VALUES;
    
    private HashMap<String, Double> mStats;
    private HashMap<String, Double> mRanks;
    
    public Stats() {
        mStats = new HashMap<String, Double>();
        mRanks = new HashMap<String, Double>();
    }
    
    public void addStat(String name, Double value) {
        mStats.put(name, value);
    }
    
    public Double getStat(String name) {
        return mStats.get(name);
    }
    
    public void addRank(String name, Double value) {
        mRanks.put(name, value);
    }
    
    public Double getRank(String name) {
        return mRanks.get(name);
    }
    
    @Override
    public String toString() {
        StringBuilder sBld = new StringBuilder();
        
        for ( String statName : mStats.keySet() ) {
            sBld.append(statName);
            sBld.append(": ");
            sBld.append(mStats.get(statName));
            sBld.append("\n");
        }
        
        for ( String rankName : mRanks.keySet() ) {
            sBld.append(rankName);
            sBld.append(": ");
            sBld.append(mRanks.get(rankName));
            sBld.append("\n");
        }
        
        return sBld.toString();
    }
    
    public static Set<String> getStatKeys() {
        return TableInfo.STATS_COLS.keySet();
    }
    
    public static Stats getStatsByKey(Connection db, int key) {
        
        Stats newStat = new Stats();
        
        try {
            Statement stmt = db.createStatement();
            
            StringBuilder cols = new StringBuilder();
            ArrayList<String[]> colNames = new ArrayList<String[]>();
            
            for ( String statFieldKey : TableInfo.STATS_COLS.keySet() ) {
                
                String value = TableInfo.STATS_COLS.get(statFieldKey);
                String rank = value + "_rank";
                
                cols.append(",");
                cols.append(value);
                cols.append(",");
                cols.append(rank);
                
                colNames.add(new String[]{statFieldKey, value});
            }
            
            String select = 
                    String.format("SELECT %s FROM %s WHERE %s=%d",
                    cols.toString().substring(1),
                    TableInfo.STATS_TABLE_NAME,
                    "rowid",
                    key);
            
            ResultSet statSet = stmt.executeQuery(select);
            
            if ( statSet.next() ) {
                
                for ( String[] col : colNames ) {
                    Double data = statSet.getDouble(col[1]);
                    Integer rankInt = statSet.getInt(col[1] + "_rank");
                    
                    Double rank = new Double(rankInt);
                    
//                    Double scaledData = data / MAX_VALUES.get(col[1]);
                    Double scaledData = data;
                    
                    newStat.addStat(col[0], scaledData);
                    newStat.addRank(col[0] + "_rank", rank);
                }
                
            }
            
        } catch (SQLException sqle) {
            System.err.println("Failure getting stats by key...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        
        return newStat;
    }
    
    public static Double maxValue(String stat) {
        return MAX_VALUES.get(stat);
    }
    
    public static void getMaxStatValues(Connection db) {
        
        MAX_VALUES = new HashMap<String, Double>();
        
        for ( String key : TableInfo.STATS_COLS.keySet() ) {
            
            String column = TableInfo.STATS_COLS.get(key);
            
            Double maxValue = getMaxValue(db, column);
            
            MAX_VALUES.put(column, maxValue);
        }
        
    }
    
    private static Double getMaxValue(Connection db, String column) {
        Double max = 0.0;
        
        try {
            Statement stmt = db.createStatement();
            
            String select = 
                    String.format("SELECT MAX(ABS(%s)) FROM %s",
                    column,
                    TableInfo.STATS_TABLE_NAME);
            
            ResultSet statSet = stmt.executeQuery(select);
            
            if ( statSet.next() ) {
                max = statSet.getDouble(1);
            }
            
        } catch (SQLException sqle) {
            System.err.println("Failure getting max stat for column [" + column + "]...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        
        return max;
    }
}
