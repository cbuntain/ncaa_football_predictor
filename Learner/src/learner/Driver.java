/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package learner;


import learner.perceptron.AveragedPerceptron;
import java.util.ArrayList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;
import learner.ann.AnnProblem;
import learner.ensemble.EnsembleLearner;
import learner.svm.SvmProblemJ;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import org.uncommons.maths.random.MersenneTwisterRNG;

/**
 *
 * @author cbuntain
 */
public class Driver {
    
    private enum LEARNER { PERCEPTRON, SVM, ANN, ENSEMBLE };
    
    private Connection mDb;

    public Driver(Connection database) {
        mDb = database;
    }
    
    private void beginLearning(int testYear, Random prng, LEARNER type) {

        ArrayList<Sample> trainingSet = new ArrayList<Sample>();
        ArrayList<Sample> testingSet = new ArrayList<Sample>();
        
        /* Build the data set */
        for ( Game g : GameList.getGames() ){           
            Sample s = SampleFactory.getSampleFromGame(g);
            
            if ( g.getYear() < testYear ) {
                trainingSet.add(s);
            } else {
                testingSet.add(s);
            }
        }
        
        // Debug
        System.out.printf("Training set of size (year < %d): %d\n", 
                testYear, trainingSet.size());
        System.out.printf("Testing set of size (year >= %d): %d\n", 
                testYear, testingSet.size());
        
        if ( type == LEARNER.PERCEPTRON ) {
            AveragedPerceptron learner = 
                    new AveragedPerceptron(SampleFactory.getDimension(), prng);

            learner.train(trainingSet);

            List<Pair<Sample,Integer>> results = learner.classify(testingSet);

            double acc = Accurator.accuracy(results);
            double prc = Accurator.precision(results);
            double rcl = Accurator.recall(results);

            System.out.printf("Accuracy: %f, Precision: %f, Recall: %f\n", 
                    acc, prc, rcl);
        } else if ( type == LEARNER.SVM ) {
            SvmProblemJ svmLearner = new SvmProblemJ();

            svmLearner.train(trainingSet);

            List<Pair<Sample,Integer>> results = svmLearner.classify(testingSet);

            double acc = Accurator.accuracy(results);
            double prc = Accurator.precision(results);
            double rcl = Accurator.recall(results);

            System.out.printf("Accuracy: %f, Precision: %f, Recall: %f\n", 
                    acc, prc, rcl);
        } else if ( type == LEARNER.ANN ) {
            AnnProblem annLearner = new AnnProblem(SampleFactory.getDimension(), 
                    SampleFactory.getDimension(),
                    prng);

            annLearner.train(trainingSet);

            List<Pair<Sample,Integer>> results = annLearner.classify(testingSet);

            double acc = Accurator.accuracy(results);
            double prc = Accurator.precision(results);
            double rcl = Accurator.recall(results);

            System.out.printf("Accuracy: %f, Precision: %f, Recall: %f\n", 
                    acc, prc, rcl);
        } else if ( type == LEARNER.ENSEMBLE ) {
            EnsembleLearner learner = 
                    new EnsembleLearner(SampleFactory.getDimension(), prng);

            learner.train(trainingSet);

            List<Pair<Sample,Integer>> results = learner.classify(testingSet);

            double acc = Accurator.accuracy(results);
            double prc = Accurator.precision(results);
            double rcl = Accurator.recall(results);

            System.out.printf("Accuracy: %f, Precision: %f, Recall: %f\n", 
                    acc, prc, rcl);
        }
        
        System.out.println("Learning complete.");
    }
    
    public void enumerateGames() {
        boolean useAvg = false;
        
        ArrayList<Integer> years = Schedule.getYears(mDb);
        
        for ( int year : years ) {            
            System.out.printf("Processing year: %d\n", year);
            
            int weeks = Schedule.getNumWeeks(mDb, year);
            System.out.printf("\t Num Weeks: %d\n", weeks);

            for ( int week=2; week<=weeks; week++ ) {
                
                Schedule sched = new Schedule(mDb, year, week);
                
                System.out.printf("\t\t Num Games: %d\n", sched.getGames().size());

                for ( Game g : sched ) {
                    
                    Stats t1Stats = 
                            Schedule.getPreviousStats(mDb, year, week, g.getTeam());
                    Stats t2Stats = 
                            Schedule.getPreviousStats(mDb, year, week, g.getOpponent());
                    
                    g.setLastTeamStats(t1Stats);
                    g.setLastOppStats(t2Stats);

                    if ( useAvg == false ) {
                        if ( t1Stats != null && t2Stats != null ) {
                            GameList.addGame(g);
                            //ChromosomeEvaluator.addGame(g);
                        }
                    } else {
                        Stats t1Avg =
                                Schedule.getAverageStats(mDb, year, week, g.getTeam());
                        Stats t2Avg =
                                Schedule.getAverageStats(mDb, year, week, g.getOpponent());

                        g.setAvgTeamStats(t1Avg);
                        g.setAvgOppStats(t2Avg);

                        if ( t1Avg != null && t2Avg != null ) {
                            GameList.addGame(g);
                            //ChromosomeEvaluator.addGame(g);
                        }
                    }

/*                    
                    System.out.printf("[%d, %d, %s] Team Stat: \n", year, week, g.getTeam().getName());
                    if ( t1Avg != null ) {
                        System.out.printf("\t %s \n", t1Avg.toString());
                    } else {
                        System.out.printf("\t NULL \n");
                    }
                    System.out.printf("[%d, %d, %s] Team Stat: \n", year, week, g.getOpponent().getName());
                    if ( t2Avg != null ) {
                        System.out.printf("\t %s \n", t2Avg.toString());
                    } else {
                        System.out.printf("\t NULL \n");
                    }
*/
                }
            }
        }
    }
    
    public void close() {
        try {
            mDb.close();
        } catch ( Exception e ) {
            // Do nothing
        }
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Options opts = buildOpts();
        
        String statsPath = null;
        String storePath = null;
        
        boolean useRank = false;
        
        try {
            CommandLineParser parser = new GnuParser();
            CommandLine line = parser.parse( opts, args );
            
            statsPath = line.getOptionValue("s");
            storePath = line.getOptionValue("o");
        }
        catch( ParseException exp ) {
            // oops, something went wrong
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() + "\n" );
            
            // automatically generate the help statement
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "java -jar Learner.jar [options] -s <stats> -o <results>", opts );
            
            System.exit(-1);
        }

        System.out.println("Stats Path: " + statsPath);
        System.out.println("Results Path: " + storePath);
        System.out.println("Use Rank: " + useRank);
        
        Driver learningDriver = null;

        try {
            Class.forName("org.sqlite.JDBC");

            Connection conn = createConnection(statsPath);

            if ( verifyTables(conn) == false ) {
                throw new Exception("Table verification failed...\n");
            }

            learningDriver = new Driver(conn); 
            
            init(conn);
        }
        catch ( ClassNotFoundException cnfe ) {
            System.err.println("Unable to load JDBC class...");
            System.err.print(cnfe.getLocalizedMessage());

            System.exit(1);
        }
        catch ( SQLException sqle ) {
            System.err.println("Unable to connect to sqlite file...");
            System.err.print(sqle.getLocalizedMessage());

            System.exit(1);
        }
        catch ( Exception e ) {
            System.err.println("Verification failed...");
            System.err.print(e.getLocalizedMessage());

            System.exit(1);
        }
        
        MersenneTwisterRNG mtrng = new MersenneTwisterRNG();

        System.out.println("Adding games...");
        learningDriver.enumerateGames();
        
        System.out.println("Being learning...");
        learningDriver.beginLearning(2012, mtrng, LEARNER.ENSEMBLE);

        learningDriver.close();
    }
    
    public static Options buildOpts() {
        Options opts = new Options();
        
        Option statsOpt = new Option("s", "stats", true, "Stats file path.");
        statsOpt.setRequired(true);
        
        Option storeOpt = new Option("o", "store", true, "Chromosome storage path.");
        storeOpt.setRequired(true);
        
        opts.addOption(statsOpt);
        opts.addOption(storeOpt);
        
        return opts;
    }
    
    private static Connection createConnection(String path) 
            throws SQLException {
        
        Connection conn = null;
        
        conn = DriverManager.getConnection("jdbc:sqlite:" + path);
        
        return conn;
    }
    
    public static boolean verifyTables(Connection db) throws SQLException {
        boolean verified = true;
        
        ArrayList<String> tables = new ArrayList<String>();
        tables.add(TableInfo.TEAM_TABLE_NAME);
        tables.add(TableInfo.STATS_TABLE_NAME);
        tables.add(TableInfo.SCHED_TABLE_NAME);
        
        PreparedStatement stmt = db.prepareStatement("SELECT name FROM "
                    + "sqlite_master WHERE type='table' AND name=?");
        
        for ( String tableName : tables ) {
        
            stmt.setString(1, tableName);
            
            if ( stmt.executeQuery().next() == false ) {
                System.err.printf("Table %s does not exist...\n", tableName);
                
                verified = false;
                
                break;
            }
            
            stmt.clearParameters();
        }
        
        return verified;
    }
    
    public static void init(Connection db) {
        System.out.println("Getting maximum stat values...");
        Stats.getMaxStatValues(db);
    }
    
}
