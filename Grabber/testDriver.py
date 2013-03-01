#!/usr/bin/python 

import sqlite3

from StatsClass import NcaaGrabber

stats = NcaaGrabber()

#teams  = stats.getTeams('fcs', 2008)

# teams.update(stats.getTeams('fbs', 2008))

# for team in teams:
# 	print("[%s], [%s]" % (team, teams[team]))

numWeeks = stats.getNumWeeks('fbs', 2011)
print("Num weeks: " + str(numWeeks))

for week in range(1,numWeeks+1):

    isHome = stats.isHomeGame(8, 2011, week)

    print(isHome)

# myStats = stats.getStats(8, 2011, 1)

# print(myStats)
# print("\n")

#sched = stats.processWeekly('fbs', 2008, 1, teams)
#print(sched)

#sched = stats.processWeekly('fcs', 2008, 1, teams)
#print(sched)
