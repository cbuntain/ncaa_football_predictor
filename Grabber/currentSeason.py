#!/usr/bin/python 

import sqlite3
import sys
import traceback

from StatsClass import NcaaGrabber

TEAM_TABLE_NAME = "teams"
TEAM_TABLE = { "year": "year", "id": "org", "name": "name", "winLoss": "winLoss" }

SCHED_TABLE_NAME = "schedule"
SCHED_TABLE = { "year": "year", "week": "week", "team": "team", "opp": "opponent", 
                "stats": "stats", "teamScore": "teamScore", "oppScore": "oppScore",
                "home": "home", "line": "line" }

STATS_TABLE_NAME = "stats"
STATS_TABLE = { "rush_off": "rush_off", 
                "pass_off": "pass_off",
                "total_off": "total_off",
                "score_off": "score_off",
                "rush_def": "rush_def",
                "pass_edef": "pass_edef",
                "total_def": "total_def",
                "score_def": "score_def",
                "net_punt": "net_punt",
                "punt_ret": "punt_ret",
                "kickoff_ret": "kickoff_ret",
                "turnover": "turnover",
                "pass_def": "pass_def",
                "pass_eff": "pass_eff",
                "sacks": "sacks",
                "tackles": "tackles",
                "sacks_allowed": "sacks_allowed" }

def getTeamName(cursor, year, id):

    cursor.execute("SELECT %s FROM %s WHERE %s=? AND %s=?" % (TEAM_TABLE["name"], 
                                                              TEAM_TABLE_NAME,
                                                              TEAM_TABLE["year"],
                                                              TEAM_TABLE["id"]),
                   (year, id))

    name = cursor.fetchone()

    if ( name != None ):
        name = name[0]

    return name

def insertTeam(cursor, year, id, name):

    cursor.execute("INSERT INTO %s (%s, %s, %s) VALUES (?, ?, ?)" % (TEAM_TABLE_NAME, 
                                                                     TEAM_TABLE["year"], 
                                                                     TEAM_TABLE["id"],
                                                                     TEAM_TABLE["name"]),
                   (year, id, name))

def insertStats(cursor, statsDict):

    statsMap = { "Rushing Offense": "rush_off",
                 "Passing Offense": "pass_off",
                 "Total Offense": "total_off",
                 "Scoring Offense": "score_off",
                 "Rushing Defense": "rush_def",
                 "Pass Efficiency Defense": "pass_edef",
                 "Total Defense": "total_def",
                 "Scoring Defense": "score_def",
                 "Net Punting": "net_punt",
                 "Punt Returns": "punt_ret",
                 "Kickoff Returns": "kickoff_ret",
                 "Turnover Margin": "turnover",
                 "Pass Defense": "pass_def",
                 "Passing Efficiency": "pass_eff",
                 "Sacks": "sacks",
                 "Tackles For Loss": "tackles",
                 "Sacks Allowed": "sacks_allowed" }

    cols = []
    namedParams = []
    params = {}
    for key in STATS_TABLE:
        tempStr = "%s, %s_rank, " % (STATS_TABLE[key], STATS_TABLE[key])
        cols.append(tempStr)

        tempParam = ":%s, :%s_rank, " % (key, key)
        namedParams.append(tempParam)

    for key in statsDict:

        thisStat = statsDict[key]
        mapped = statsMap[key]
        params[mapped] = thisStat[0].strip()
        params["%s_rank" % mapped] = thisStat[1]

    sqlInsertStr = "INSERT INTO %s (%s) VALUES (%s)" % (STATS_TABLE_NAME, 
                                                        ''.join(cols)[:-2],
                                                        ''.join(namedParams)[:-2])

    try:
        cursor.execute(sqlInsertStr, params)
    except:
        print("Error on insert: ", sys.exc_info()[0])
        traceback.print_exc(file=sys.stdout)
        
        print(sqlInsertStr)
        print(params)


    return cursor.lastrowid

def insertGame(cursor, year, week, team1, team2, statKey, teamScore, oppScore, home, line):

    print("Year: %d, Week: %d, Team: %s, Opp: %s, Stat: %d, TScore: %s, OScore: %s, Home: %d, Line: %s" %(year, week, team1, team2, statKey, teamScore, oppScore, home, line))

    schedTableProps = (SCHED_TABLE_NAME, 
                       SCHED_TABLE["year"], 
                       SCHED_TABLE["week"], 
                       SCHED_TABLE["team"], 
                       SCHED_TABLE["opp"], 
                       SCHED_TABLE["stats"], 
                       SCHED_TABLE["teamScore"],
                       SCHED_TABLE["oppScore"],
                       SCHED_TABLE["home"],
                       SCHED_TABLE["line"])

    cursor.execute("INSERT INTO %s (%s, %s, %s, %s, %s, %s, %s, %s, %s) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)" % 
                   schedTableProps,
                   (year, week, team1, team2, statKey, teamScore, oppScore, home, line))

def checkTables(cursor):
    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name=?", (TEAM_TABLE_NAME,))

    count = cursor.fetchone()

    retVal = True

    if ( count == None ):
        retVal = False

    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name=?", (SCHED_TABLE_NAME,))

    count = cursor.fetchone()

    if ( count == None ):
        retVal = False

    cursor.execute("SELECT name FROM sqlite_master WHERE type='table' AND name=?", (STATS_TABLE_NAME,))

    count = cursor.fetchone()

    if ( count == None ):
        retVal = False

    return retVal

def buildTables(cursor):

    cursor.execute("DROP TABLE IF EXISTS %s" % TEAM_TABLE_NAME)
    cursor.execute("DROP TABLE IF EXISTS %s" % SCHED_TABLE_NAME)
    cursor.execute("DROP TABLE IF EXISTS %s" % STATS_TABLE_NAME)

    # Create team table
    teamTableProps = (TEAM_TABLE_NAME, TEAM_TABLE["year"], TEAM_TABLE["id"], TEAM_TABLE["name"], TEAM_TABLE["winLoss"])
    cursor.execute("CREATE TABLE %s (%s INTEGER, %s INTEGER, %s TEXT, %s REAL)" % teamTableProps)

    # Create schedule table
    schedTableProps = (SCHED_TABLE_NAME, SCHED_TABLE["year"], SCHED_TABLE["week"], SCHED_TABLE["team"], SCHED_TABLE["opp"], SCHED_TABLE["stats"], SCHED_TABLE["teamScore"], SCHED_TABLE["oppScore"], SCHED_TABLE["home"], SCHED_TABLE["line"])
    cursor.execute("CREATE TABLE %s (%s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER, %s INTEGER)" % schedTableProps)

    # Create the stats table
    cols = []
    for key in STATS_TABLE:
        tempStr = "%s REAL, %s_rank INTEGER, " % (STATS_TABLE[key], STATS_TABLE[key])
        cols.append(tempStr)

    statsCreateStr = "CREATE TABLE %s (%s)" % (STATS_TABLE_NAME, ''.join(cols)[:-2])
    cursor.execute(statsCreateStr)

if ( len(sys.argv) < 4 ):
    print("Usage: currentSeason.py <db> <year> <week>")
    exit(-1)

dbPath = sys.argv[1]
targetYear = int(sys.argv[2])
targetWeek = int(sys.argv[3])

conn = sqlite3.connect(dbPath)
cursor = conn.cursor()

if ( not checkTables(cursor) ):
    buildTables(cursor)
    conn.commit()

statsGrabber = NcaaGrabber()

years = [targetYear]
for year in years:

    teams = statsGrabber.getTeams('fcs', year)
    teams.update(statsGrabber.getTeams('fbs', year))

    # Insert the teams
    if ( targetWeek == 1 ):
        for team in teams:
            insertTeam(cursor, year, teams[team], team)
            conn.commit()

    divisions = ['fcs', 'fbs']

    for div in divisions:

        # Build the week's schedule
        for week in [targetWeek]:

            print("In week %d..." % week)

            schedule = statsGrabber.processWeekly(div, year, week, teams)

            for game in schedule:

                team1 = game[0]
                team2 = game[1]
                score = game[2]

                scoreArr = score.split("-")
                teamScore = scoreArr[0].strip()
                oppScore = scoreArr[1].strip()

                line = ""

                team1Name = getTeamName(cursor, year, team1)
                team2Name = "Unknown"

                if ( team2 > -1 ):
                    team2Name = getTeamName(cursor, year, team2)

                try:

                    print("Inserting year %d week %d game between %s and %s..." % 
                          (year, week, team1Name, team2Name))

                    isHome = statsGrabber.isHomeGame(team1, year, week)

                    if ( team2 == None ):
                        team2 = -1

                    team1Stats = statsGrabber.getStats(team1, year, week)

                    if ( len(team1Stats) == 0 ):
                        print("%s has no stats for this week..." % team1Name)
                
                        insertGame(cursor, year, week, team1, team2, -1, teamScore, oppScore, isHome, line)

                    else:

                        statKey = insertStats(cursor, team1Stats)
                        insertGame(cursor, year, week, team1, team2, statKey, teamScore, oppScore, isHome, line)

                    conn.commit()

                except:

                    print("Error on insert: ", sys.exc_info()[0])
                    traceback.print_exc(file=sys.stdout)


conn.close()
