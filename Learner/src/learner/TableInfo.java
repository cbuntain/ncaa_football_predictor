/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author cbuntain
 */
public class TableInfo {
    
    public static final String TEAM_TABLE_NAME = "teams";
    public static final String SCHED_TABLE_NAME = "schedule";
    public static final String STATS_TABLE_NAME = "stats";
    
    public static final Map<String, String> TEAM_COLS;
    public static final Map<String, String> SCHED_COLS;
    public static final Map<String, String> STATS_COLS;
    
    static {
        HashMap<String, String> teamsMap = new HashMap<String, String>();
        
        teamsMap.put("year", "year");
        teamsMap.put("id", "org");
        teamsMap.put("name", "name");
        
        TEAM_COLS = Collections.unmodifiableMap(teamsMap);
        
        HashMap<String, String> schedMap = new HashMap<String, String>();
        
        schedMap.put("year", "year");
        schedMap.put("week", "week");
        schedMap.put("team", "team");
        schedMap.put("opp", "opponent");
        schedMap.put("stats", "stats");
        schedMap.put("teamScore", "teamScore");
        schedMap.put("oppScore", "oppScore");
        schedMap.put("home", "home");
        
        SCHED_COLS = Collections.unmodifiableMap(schedMap);
        
        HashMap<String, String> statsMap = new HashMap<String, String>();
        
        statsMap.put("rush_off", "rush_off");
        statsMap.put("pass_off", "pass_off");
        statsMap.put("total_off", "total_off");
        statsMap.put("score_off", "score_off");
        statsMap.put("rush_def", "rush_def");
        statsMap.put("pass_edef", "pass_edef");
        statsMap.put("total_def", "total_def");
        statsMap.put("score_def", "score_def");
        statsMap.put("net_punt", "net_punt");
        statsMap.put("punt_ret", "punt_ret");
        statsMap.put("kickoff_ret", "kickoff_ret");
        statsMap.put("turnover", "turnover");
        statsMap.put("pass_def", "pass_def");
        statsMap.put("pass_eff", "pass_eff");
        statsMap.put("sacks", "sacks");
        statsMap.put("tackles", "tackles");
        statsMap.put("sacks_allowed", "sacks_allowed");
        
        STATS_COLS = Collections.unmodifiableMap(statsMap);
    }
}
