#!/usr/bin/python

import sqlite3
import sys

if ( len(sys.argv) < 2 ):

    print("Usage: ./tableCleaner.py <database>")

    exit()

path = sys.argv[1]

conn = sqlite3.connect(path)
cursor = conn.cursor()

rows = cursor.execute('select distinct s2.year, s2.week, s3.team from (select distinct year from schedule) s1 join schedule s2 on s2.year=s1.year join schedule s3 on s3.year=s1.year and s3.week=s2.week where (select count(*) from schedule where year=s3.year and week=s3.week and team=s3.team) > 1;')

for row in rows:

    c2 = conn.cursor()

    year = row[0]
    week = row[1]
    team = row[2]

    results = c2.execute('SELECT rowid FROM schedule WHERE year=? AND week=? AND team=?',(year, week, team))

    rowIdList = []

    for r in results:

        rowIdList.append(r[0])

    deleteList = rowIdList[1:]

    for rowId in deleteList:

        print('Deleting schedule row: %d' % rowId)

        statsList = c2.execute('SELECT stats FROM schedule WHERE rowid=?', (rowId,))

        c3 = conn.cursor()

        for statKey in statsList:

            c3.execute('DELETE FROM stats WHERE rowid=?', (statKey[0],))

        c2.execute('DELETE FROM schedule WHERE rowid=?', (rowId,))

conn.commit()

conn.close()
