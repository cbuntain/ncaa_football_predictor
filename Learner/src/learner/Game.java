/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;

/**
 *
 * @author cbuntain
 */
public class Game {
    
    private int mYear;
    private int mWeek;
    private Team mTeam;
    private Team mOpponent;
    
    private int mTeamScore;
    private int mOppScore;
    private int mIsHome;
    
    private Stats mStats;
    
    private Stats mLastTeamStats;
    private Stats mLastOppStats;
    
    private Stats mAvgTeamStats;
    private Stats mAvgOppStats;
    
    public Game(int year, int week, Team team, Team opp, 
            int t1Score, int t2Score, int home, Stats s) {
        mYear = year;
        mWeek = week;
        mTeam = team;
        mOpponent = opp;
        
        mTeamScore = t1Score;
        mOppScore = t2Score;
        
        mIsHome = home;
        
        mStats = s;
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

    /**
     * @return the mWeek
     */
    public int getWeek() {
        return mWeek;
    }

    /**
     * @param mWeek the mWeek to set
     */
    public void setWeek(int week) {
        this.mWeek = week;
    }

    /**
     * @return the mTeam
     */
    public Team getTeam() {
        return mTeam;
    }

    /**
     * @param mTeam the mTeam to set
     */
    public void setTeam(Team team) {
        this.mTeam = team;
    }

    /**
     * @return the mOpponent
     */
    public Team getOpponent() {
        return mOpponent;
    }

    /**
     * @param mOpponent the mOpponent to set
     */
    public void setOpponent(Team opponent) {
        this.mOpponent = opponent;
    }
    
    public int getTeamScore() {
        return mTeamScore;
    }
    
    public int getOppScore() {
        return mOppScore;
    }

    /**
     * @return the mStats
     */
    public int isHomeGame() {
        return mIsHome;
    }

    /**
     * @param isHome the home status to set
     */
    public void setIsHome(int isHome) {
        this.mIsHome = isHome;
    }

    /**
     * @return the mStats
     */
    public Stats getStats() {
        return mStats;
    }

    /**
     * @param mStats the mStats to set
     */
    public void setStats(Stats stats) {
        this.mStats = stats;
    }

    /**
     * @return the mLastTeamStats
     */
    public Stats getLastTeamStats() {
        return mLastTeamStats;
    }

    /**
     * @param mLastTeamStats the mLastTeamStats to set
     */
    public void setLastTeamStats(Stats last) {
        this.mLastTeamStats = last;
    }

    /**
     * @return the mLastOppStats
     */
    public Stats getLastOppStats() {
        return mLastOppStats;
    }

    /**
     * @param mLastOppStats the mLastOppStats to set
     */
    public void setLastOppStats(Stats last) {
        this.mLastOppStats = last;
    }
    
    

    /**
     * @return the mLastTeamStats
     */
    public Stats getAvgTeamStats() {
        return mAvgTeamStats;
    }

    /**
     * @param avg the average stats to set
     */
    public void setAvgTeamStats(Stats avg) {
        this.mAvgTeamStats = avg;
    }

    /**
     * @return the mLastOppStats
     */
    public Stats getAvgOppStats() {
        return mAvgOppStats;
    }

    /**
     * @param avg the average stats to set
     */
    public void setAvgOppStats(Stats avg) {
        this.mAvgOppStats = avg;
    }
    
    @Override
    public String toString() {
        return String.format("%s vs %s, Score: %d-%d", 
                mTeam, mOpponent, mTeamScore, mOppScore);
    }
    
}
