#!/usr/bin/python 

import StringIO
import urllib
import urllib2

from lxml import etree 

class NcaaGrabber:

    def __init__(self):

        self.ncaaUrl = 'http://web1.ncaa.org'

        self.ncaaStatsSite = self.ncaaUrl+'/football/exec/rankingSummary'

#        self.ncaaTeamList2008 = self.ncaaUrl+'/mfb/%d/Internet/ranking_summary/DIVISIONB.HTML'
#        self.ncaaWeeklyBase2008 = self.ncaaUrl+'/mfb/%d/Internet/worksheets'
#        self.ncaaWeekly2008 = self.ncaaWeeklyBase2008+'/DIVISIONB.HTML'

        self.ncaaTeamListBase = self.ncaaUrl+'/mfb/%d/Internet/ranking_summary'
        self.ncaaWeeklyBase = self.ncaaUrl+'/mfb/%d/Internet/worksheets'

        self.fbsDiv = '/DIVISIONB.HTML'
        self.fcsDiv = '/DIVISIONC.HTML'

    def getTeams(self, division, year):
        
        fullUrl = self.ncaaTeamListBase % year

        if ( division == 'fbs' ):
            fullUrl = fullUrl + self.fbsDiv
        else:
            fullUrl = fullUrl + self.fcsDiv

        response = urllib2.urlopen(fullUrl)
        responseHtml = response.read()

        htmlParser = etree.HTMLParser()
        htmlTree = etree.parse(StringIO.StringIO(responseHtml), htmlParser)

        mainTablePaths = htmlTree.xpath('//body/table')

        linkPaths = mainTablePaths[0].xpath('.//td/a')

        data = {}

        for link in linkPaths:

            team = link.text
            org = -1

            linkStr = link.get('href')
            linkStrArr = linkStr.split('&')

            for linkStrPart in linkStrArr:

                if ( linkStrPart.startswith('org=') ):
                    
                    linkStrPart = linkStrPart.replace('org=', '')

                    if ( linkStrPart.isdigit() ):
                        org = linkStrPart

            data[team] = org

        return data

#    def  getTeams(self, year):

#         data = {}

#         data['year'] = year
#         data['org'] = 8
#         data['week'] = 1

#         getData = urllib.urlencode(data)

#         fullUrl = self.ncaaStatsSite + '?' + getData

#         response = urllib2.urlopen(fullUrl)

#         responseHtml = response.read()

#         htmlParser = etree.HTMLParser()

#         htmlTree = etree.parse(StringIO.StringIO(responseHtml), htmlParser)

#         optionRows = htmlTree.xpath('/html/body/span[@class="noprint"]/select[@name="teamSelection"]/option')

#         teams = {}

#         for teamOption in optionRows:

#             teamName = teamOption.text
#             teamValue = int(teamOption.get("value"))

#             if ( teamValue > -1 ):
#                 teams[teamName] = teamValue

#         return teams

    def getStats(self, team, year, week):
        data = {}

        data['org'] = team
        data['week'] = week
        data['year'] = year

        getData = urllib.urlencode(data)

        fullUrl = self.ncaaStatsSite + '?' + getData

        response = urllib2.urlopen(fullUrl)

        responseHtml = response.read()

        htmlParser = etree.HTMLParser()

        htmlTree = etree.parse(StringIO.StringIO(responseHtml), htmlParser)

        teamTableRows = htmlTree.xpath('//table[@id="teamRankings"]/tr[position()>4]')

        stats = {}

        for statRow in teamTableRows:

            dataCells = statRow.xpath('./td')

            if ( len(dataCells) < 1 ):
                continue

            category = dataCells[0].xpath('./a')[0].text
            value = dataCells[2].text
            rank = dataCells[1].text.lstrip('T-')

            stats[category] = (value, rank)

        return stats

    def isHomeGame(self, team, year, week):
        data = {}

        data['org'] = team
        data['week'] = week
        data['year'] = year

        getData = urllib.urlencode(data)

        fullUrl = self.ncaaStatsSite + '?' + getData
        print(fullUrl)

        response = urllib2.urlopen(fullUrl)

        responseHtml = response.read()

        htmlParser = etree.HTMLParser()

        htmlTree = etree.parse(StringIO.StringIO(responseHtml), htmlParser)

        scheduleTableRows = htmlTree.xpath('//table[@id="schedule"]/tr/td[position()=1]/a/../../td[position()=2]')

        lastScheduleRow = scheduleTableRows[-1]

        isHome = False

        if ( lastScheduleRow is not None ):

            if ( lastScheduleRow.text is None ):

                linkElement = lastScheduleRow.xpath('./a')[0]

                gameLocation = linkElement.text

                if ( gameLocation.isupper() and gameLocation.find("@") < 0 ):

                    if ( gameLocation.find("^") < 0 ):
                        isHome = 1
                    else:
                        isHome = 2

                else:

                    isHome = 0

            else:

                gameLocation = lastScheduleRow.text

                if ( gameLocation.isupper() and gameLocation.find("@") < 0 ):
                    if ( gameLocation.find("^") < 0 ):
                        isHome = 1
                    else:
                        isHome = 2

                else:

                    isHome = 0

        return isHome

    def getNumWeeks(self, division, year):

        fullUrl = self.ncaaWeeklyBase % year

        if ( division == 'fbs' ):
            fullUrl = fullUrl + self.fbsDiv
        else:
            fullUrl = fullUrl + self.fcsDiv

        response = urllib2.urlopen(fullUrl)
        responseHtml = response.read()

        htmlParser = etree.HTMLParser()
        htmlTree = etree.parse(StringIO.StringIO(responseHtml), htmlParser)

        tableRowArr = htmlTree.xpath('//body/table/tr')

        count = len(tableRowArr) - 1

        return count

    def processWeekly(self, year, week, teams):

        return self.processWeekly("fbs", year, week, team)

    def processWeekly(self, division, year, week, teams):

        schedule = []

        week = week - 1

        fullUrl = self.ncaaWeeklyBase % year

        if ( division == 'fbs' ):
            fullUrl = fullUrl + self.fbsDiv
        else:
            fullUrl = fullUrl + self.fcsDiv

        response = urllib2.urlopen(fullUrl)
        responseHtml = response.read()

        htmlParser = etree.HTMLParser()
        htmlTree = etree.parse(StringIO.StringIO(responseHtml), htmlParser)

        tableRowArr = htmlTree.xpath('//body/table/tr')

        weekRow = tableRowArr[week+1]

        weekLinkCol = weekRow.find('td')
        weekLink = weekLinkCol.find('a')

        weekUrl = (self.ncaaWeeklyBase + '/' + weekLink.values()[0]) % year

        response = urllib2.urlopen(weekUrl)
        responseHtml = response.read()

        htmlTree = etree.parse(StringIO.StringIO(responseHtml), htmlParser)

        trList = htmlTree.xpath('//body/table[@width="80%"]/tr')

        for tr in trList[1:]:

            tds = tr.findall('td')

            if(len(tds) > 2):

                team1 = tds[0].find('a').text
                team2 = tds[1].text
                result = ""

                if ( len(tds) > 3 ):
                    result = tds[3].text

                if ( team1 not in teams ):
                    continue
                
                org1 = teams[team1]
                org2 = None

                if ( team2 in teams ):
                    org2 = teams[team2]

                schedule.append((org1, org2, result))

        return schedule
