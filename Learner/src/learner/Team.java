/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.SQLException;

import java.util.ArrayList;


/**
 *
 * @author cbuntain
 */
public class Team {
    
    private String mName;
    private int mId;
    private int mYear;
    
    public Team(String name, int id, int year) {
        mName = name;
        mId = id;
        mYear = year;
    }

    /**
     * @return the mName
     */
    public String getName() {
        return mName;
    }

    /**
     * @param mName the mName to set
     */
    public void setName(String name) {
        this.mName = name;
    }

    /**
     * @return the mId
     */
    public int getId() {
        return mId;
    }

    /**
     * @param mId the mId to set
     */
    public void setId(int id) {
        this.mId = id;
    }

    /**
     * @return the mYear
     */
    public int getYear() {
        return mYear;
    }

    /**
     * @param mYear the mYear to set
     */
    public void setYear(int year) {
        this.mYear = year;
    }
    
    @Override
    public String toString() {
        return this.mName;
    }
    
    public static Team getById(Connection db, int id, int year) {
        
        Team team = null;
        
        try {
            Statement stmt = db.createStatement();
            
            String select = 
                    String.format("SELECT %s FROM %s WHERE %s=%d AND %s=%d", 
                    TableInfo.TEAM_COLS.get("name"),
                    TableInfo.TEAM_TABLE_NAME,
                    TableInfo.TEAM_COLS.get("year"),
                    year,
                    TableInfo.TEAM_COLS.get("id"),
                    id);
            
            ResultSet teamSet = stmt.executeQuery(select);
            
            if ( teamSet.next() ) {
                String name = teamSet.getString(1);
                
                team = new Team(name, id, year);
            }
            
        } catch (SQLException sqle) {
            System.err.println("Failure getting team by ID...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        
        return team;
    }
    
    public static Team getByName(Connection db, String name, int year) {
        
        
        Team team = null;
        
        try {
            Statement stmt = db.createStatement();
            
            String select = 
                    String.format("SELECT %s FROM %s WHERE %s=%d AND %s='%s'", 
                    TableInfo.TEAM_COLS.get("id"),
                    TableInfo.TEAM_TABLE_NAME,
                    TableInfo.TEAM_COLS.get("year"),
                    year,
                    TableInfo.TEAM_COLS.get("name"),
                    name);
            
            ResultSet teamSet = stmt.executeQuery(select);
            
            if ( teamSet.next() ) {
                Integer id = teamSet.getInt(1);
                
                team = new Team(name, id, year);
            }
            
        } catch (SQLException sqle) {
            System.err.println("Failure getting team by name...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        
        return team;
    }
    
    public static ArrayList<Team> getTeams(Connection db, int year) {
        
        
        ArrayList<Team> teams = new ArrayList<Team>();
        
        try {
            Statement stmt = db.createStatement();
            
            String select = 
                    String.format("SELECT %s, %s FROM %s WHERE %s=%d "
                    + "ORDER BY %s", 
                    TableInfo.TEAM_COLS.get("id"),
                    TableInfo.TEAM_COLS.get("name"),
                    TableInfo.TEAM_TABLE_NAME,
                    TableInfo.TEAM_COLS.get("year"),
                    year,
                    TableInfo.TEAM_COLS.get("name"));
            
            ResultSet teamsSet = stmt.executeQuery(select);
            
            while ( teamsSet.next() ) {
                Integer id = teamsSet.getInt(1);
                String name = teamsSet.getString(2);
                
                Team t = new Team(name, id, year);
                
                teams.add(t);
            }
            
        } catch (SQLException sqle) {
            System.err.println("Failure getting all teams in year " + year + "...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        
        return teams;
    }
    
}
