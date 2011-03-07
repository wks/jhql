===================================
JHQL - The JSON HTML Query Language
===================================

Author: Kunshan Wang

JHQL is a JSON-based language expressing a mapping from an HTML page to a
JSON value.  It simplifies the job of extracting texts from HTML pages.

Introduction
============

If you run crawlers on the Internet, do researches on Web data mining or
write client programs for Web servers that do not provide Web-service
APIs, you may frequently need to extract texts from web-pages.  You may have to crawl
a news site, collect many HTML pages containing news articles and write a parser
that strip out HTML tags, leaving only the title, the main content, the 
keyword tags and the date the article got published, before doing your research
tasks like text categorizing, clustering or page-link analysis.

However, parsing HTML and extracting contents are no trivial tasks.  You can
use regular expressions on HTML, but it is much easier to use XPath expressions
to match DOM nodes.  You also need to parse some numbers or dates.  But if you
write all these directly using Java codes, the code will bloat soon and becomes
ugly, buggy and unmaintainable.

JHQL soothes the pain of parsing HTML.  You define the rule of extraction in an
elegant and powerful language, JHQL.  HTML pages are processed according to your
definition and outputs a JSON Object containing your needed information. 

Example
=======

A JHQL expression is a JSON Value (most probably a JSON Object) like this::

    {
        "username": "text:.//h1/text()",
        "fullname": "text:.//dd[@class='fn']",
        "membersince": {
            "_type": "date",
            "value": ".//div[@class='first vcard']/dl[2]/dd",
            "dateFormat": "MMM dd, yyyy"
        }
    }

You can guess that it extracts the "username" and "fullname" information
from an HTML page using XPath.  It also extracts a date using a combination
of an XPath expression and a date format string.

When you apply this JHQL expression to this page https://github.com/wks/ , it
gives you::

    {
        "username" : "wks",
        "fullname" : "Kunshan Wang",
        "membersince" : "2010-08-18T16:00:00.000+0000"
    }

JHQL also support some complexed queries.  The following::

    {
        "username": "text:.//h1/text()",
        "publicRepos": {
            "_type": "list",
            "from": ".//li[@class='public']",
            "select": {
                "reponame": "text:./h3/a",
                "language": "text:./ul/li[1]",
                "watchers": "int:./ul/li[@class='watchers']/a",
                "forks": "int:./ul/li[@class='forks']/a"
            }
        }
    }
    
will find all DOM nodes at ".//li[@class='public']" and do the query in "select"
on each matching node.  This, when applied also on https://github.com/wks/, will
give you::

    {
        "username" : "wks",
        "publicRepos" : [ {
            "reponame" : "jhql",
            "language" : "Java",
            "watchers" : 1,
            "forks" : 1
        }, {
            "reponame" : "libbyr4j",
            "language" : "Java",
            "watchers" : 1,
            "forks" : 1
        }, {
            "reponame" : "libbyr",
            "language" : "Python",
            "watchers" : 1,
            "forks" : 1
        },...
        ]
    }

Of course it is recommended to use the Web site's native restful API 
(see http://develop.github.com/) whenever possibie in real-world applications.

JHQL Grammar
============

TODO: Add JHQL Grammar.

Java usage
==========

TODO: Add Java usage. 