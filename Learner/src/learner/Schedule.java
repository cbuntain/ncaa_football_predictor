/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

import java.util.ArrayList;
import java.util.Iterator;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;


/**
 *
 * @author cbuntain
 */
public class Schedule implements Iterable<Game> {
    
    private Connection mDb;
    private int mYear;
    private int mWeek;
    
    public Schedule(Connection db, int year, int week) {
        mDb = db;
        mYear = year;
        mWeek = week;
    }
    
    public ArrayList<Game> getGames() {
        
        ArrayList<Game> games = new ArrayList<Game>();
        
        try {
            Statement stmt = mDb.createStatement();
            
            String select = 
                    String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s "
                    + "WHERE %s=%d AND %s=%d AND %s>-1", 
                    TableInfo.SCHED_COLS.get("team"),
                    TableInfo.SCHED_COLS.get("opp"),
                    TableInfo.SCHED_COLS.get("stats"),
                    TableInfo.SCHED_COLS.get("teamScore"),
                    TableInfo.SCHED_COLS.get("oppScore"),
                    TableInfo.SCHED_COLS.get("home"),
                    TableInfo.SCHED_TABLE_NAME,
                    TableInfo.SCHED_COLS.get("year"),
                    mYear,
                    TableInfo.SCHED_COLS.get("week"),
                    mWeek,
                    TableInfo.SCHED_COLS.get("opp"));
            
            ResultSet gameSet = stmt.executeQuery(select);
            
            while ( gameSet.next() ) {
                
                int team1Key = gameSet.getInt(TableInfo.SCHED_COLS.get("team"));
                int team2Key = gameSet.getInt(TableInfo.SCHED_COLS.get("opp"));
                int statsKey = gameSet.getInt(TableInfo.SCHED_COLS.get("stats"));
                int team1Score = gameSet.getInt(TableInfo.SCHED_COLS.get("teamScore"));
                int team2Score = gameSet.getInt(TableInfo.SCHED_COLS.get("oppScore"));
                int home = gameSet.getInt(TableInfo.SCHED_COLS.get("home"));
                
                Stats stats = null;
                
                if ( statsKey > -1 ) {
                    stats = Stats.getStatsByKey(mDb, statsKey);
                }
                
                Team team1 = Team.getById(mDb, team1Key, mYear);
                Team team2 = Team.getById(mDb, team2Key, mYear);
                
                Game g = new Game(mYear, mWeek, team1, team2, 
                        team1Score, team2Score, home, stats);
                
                games.add(g);
            }
            
        } catch (SQLException sqle) {
            System.err.println("Failure getting games...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        
        return games;
    }
    
    public ArrayList<Game> getAllGames() {
        
        ArrayList<Game> games = new ArrayList<Game>();
        
        try {
            Statement stmt = mDb.createStatement();
            
            String select = 
                    String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s "
                    + "WHERE %s=%d AND %s=%d", 
                    TableInfo.SCHED_COLS.get("team"),
                    TableInfo.SCHED_COLS.get("opp"),
                    TableInfo.SCHED_COLS.get("stats"),
                    TableInfo.SCHED_COLS.get("teamScore"),
                    TableInfo.SCHED_COLS.get("oppScore"),
                    TableInfo.SCHED_COLS.get("home"),
                    TableInfo.SCHED_TABLE_NAME,
                    TableInfo.SCHED_COLS.get("year"),
                    mYear,
                    TableInfo.SCHED_COLS.get("week"),
                    mWeek);
            
            ResultSet gameSet = stmt.executeQuery(select);
            
            while ( gameSet.next() ) {
                
                int team1Key = gameSet.getInt(TableInfo.SCHED_COLS.get("team"));
                int team2Key = gameSet.getInt(TableInfo.SCHED_COLS.get("opp"));
                int statsKey = gameSet.getInt(TableInfo.SCHED_COLS.get("stats"));
                int team1Score = gameSet.getInt(TableInfo.SCHED_COLS.get("teamScore"));
                int team2Score = gameSet.getInt(TableInfo.SCHED_COLS.get("oppScore"));
                int home = gameSet.getInt(TableInfo.SCHED_COLS.get("home"));
                
                Stats stats = null;
                
                if ( statsKey > -1 ) {
                    stats = Stats.getStatsByKey(mDb, statsKey);
                }
                
                Team team1 = Team.getById(mDb, team1Key, mYear);
                Team team2 = Team.getById(mDb, team2Key, mYear);
                
                Game g = new Game(mYear, mWeek, team1, team2, 
                        team1Score, team2Score, home, stats);
                
                games.add(g);
            }
            
        } catch (SQLException sqle) {
            System.err.println("Failure getting games...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        
        return games;
    }
    
    @Override
    public Iterator<Game> iterator() {
        return this.getGames().iterator();
    }
    
    public static ArrayList<Integer> getYears(Connection db) {
        
        ArrayList<Integer> years = new ArrayList<Integer>();
        
        try {
            Statement stmt = db.createStatement();
            
            String select = 
                    String.format("SELECT DISTINCT %s FROM %s ORDER BY %s", 
                    TableInfo.SCHED_COLS.get("year"),
                    TableInfo.SCHED_TABLE_NAME,
                    TableInfo.SCHED_COLS.get("year"));
            
            ResultSet yearSet = stmt.executeQuery(select);
            
            while ( yearSet.next() ) {
                int year = yearSet.getInt(TableInfo.SCHED_COLS.get("year"));
                years.add(year);
            }
            
        } catch (SQLException sqle) {
            System.err.println("Failure getting years...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        
        return years;
    }
    
    public static int getNumWeeks(Connection db, int year) {
        
        int weeks = -1;
        
        try {
            Statement stmt = db.createStatement();
            
            String select = 
                    String.format("SELECT DISTINCT %s FROM %s WHERE %s=%d "
                    + "ORDER BY %s DESC", 
                    TableInfo.SCHED_COLS.get("week"),
                    TableInfo.SCHED_TABLE_NAME,
                    TableInfo.SCHED_COLS.get("year"),
                    year,
                    TableInfo.SCHED_COLS.get("week"));
            
            ResultSet weekSet = stmt.executeQuery(select);
            
            if ( weekSet.next() ) {
                weeks = weekSet.getInt(TableInfo.SCHED_COLS.get("week"));
            }
            
        } catch (SQLException sqle) {
            System.err.println("Failure getting years...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        
        return weeks;
    }
    
    public static Stats getPreviousStats(Connection db, 
            int year, int week, Team team) {
        
        Stats stats = null;
        
        try {
            Statement stmt = db.createStatement();
            
            String select = 
                    String.format("SELECT %s FROM %s WHERE %s=%d AND %s=%d AND "
                    + "%s<%d AND %s>-1 "
                    + "ORDER BY %s DESC", 
                    TableInfo.SCHED_COLS.get("stats"),
                    TableInfo.SCHED_TABLE_NAME,
                    TableInfo.SCHED_COLS.get("year"),
                    year,
                    TableInfo.SCHED_COLS.get("team"),
                    team.getId(),
                    TableInfo.SCHED_COLS.get("week"),
                    week,
                    TableInfo.SCHED_COLS.get("stats"),
                    TableInfo.SCHED_COLS.get("week"));
            
            ResultSet statSet = stmt.executeQuery(select);
            
            if ( statSet.next() ) {
                int statsKey = statSet.getInt(1);
                
                stats = Stats.getStatsByKey(db, statsKey);
            }
            
        } catch (SQLException sqle) {
            System.err.println("Failure getting previous stats...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        
        return stats;        
    }
    
    public static Stats getAverageStats(Connection db, 
            int year, int week, Team team) {
        
        Stats stats = null;
        
        try {
            Statement stmt = db.createStatement();
            
            String select = 
                    String.format("SELECT %s FROM %s WHERE %s=%d AND %s=%d AND "
                    + "%s<%d AND %s>-1 ", 
                    TableInfo.SCHED_COLS.get("stats"),
                    TableInfo.SCHED_TABLE_NAME,
                    TableInfo.SCHED_COLS.get("year"),
                    year,
                    TableInfo.SCHED_COLS.get("team"),
                    team.getId(),
                    TableInfo.SCHED_COLS.get("week"),
                    week,
                    TableInfo.SCHED_COLS.get("stats"));
            
            ResultSet statSet = stmt.executeQuery(select);
            
            ArrayList<Stats> statsList = new ArrayList<Stats>();
            
            while ( statSet.next() ) {
                int statsKey = statSet.getInt(1);
                
                Stats currentStats = Stats.getStatsByKey(db, statsKey);
                
                statsList.add(currentStats);
            }
            
            if ( statsList.size() >0 ) {
                stats = getAvg(statsList, false);
            }
            
        } catch (SQLException sqle) {
            System.err.println("Failure getting previous stats...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        
        return stats;        
    }
    
    private static Stats getAvg(ArrayList<Stats> statsList, boolean useRank) {
        Stats avg = new Stats();
        
        for ( Stats s : statsList ) {
            for ( String stat : TableInfo.STATS_COLS.values() ) {
                Double value = null;
                
                if ( useRank ) 
                    value = s.getRank(stat + "_rank");
                else
                    value = s.getStat(stat);
                
                if ( avg.getStat(stat) == null ) {
                    
                    if ( useRank ) 
                        avg.addRank(stat + "_rank", value);
                    else 
                        avg.addStat(stat, value);
                    
                } else {
                    Double newValue = value;
                    
                    if ( useRank ) {
                        newValue += avg.getRank(stat + "_rank");
                        avg.addRank(stat + "_rank", newValue);
                    }
                    else {
                        newValue += avg.getStat(stat);
                        avg.addStat(stat, newValue);
                    }                    
                }
            }
        }
        
        for ( String stat : TableInfo.STATS_COLS.values() ) {
            
            if ( useRank ) {
                Double v = avg.getRank(stat + "_rank");
                v = v / statsList.size();
                avg.addRank(stat + "_rank", v);
            } else {
                Double v = avg.getStat(stat);
                v = v / statsList.size();
                avg.addStat(stat, v);
            }
        }
        
        return avg;
    }
}
