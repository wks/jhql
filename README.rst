===================================
JHQL - The JSON HTML Query Language
===================================

Author: Kunshan Wang

JHQL is a JSON-based language expressing a mapping from an HTML page to a
JSON-like value.  It simplifies the job of extracting texts from HTML pages.

Introduction
============

A typical scenario consists of three parts: a JHQL expression, an input and an output.

- The JHQL expression defines the rules of converting HTML pages into JSON-like values.
  It is expressed using a JSON value with XPath expressions inside (see below for details).
- The input is an HTML page. More precisely, a DOM node.
- The output is a JSON-like value.  In this Java implementation, it can be null, a boolean
  value, an int, a List<Object> or a Map<String,Object>, all of which can be converted to
  corresponding JSON values. It can also yield Java-specific values like java.util.Date.

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

You can guess that it extracts the "username" and the "fullname" 
from an HTML page using XPath.  It also extracts a date using a combination
of an XPath expression and a date format string.

When you apply this JHQL expression to this page https://github.com/wks/ , it
gives you::

    {
        "username" : "wks",
        "fullname" : "Kunshan Wang",
        "membersince" : "2010-08-18T16:00:00.000+0000"
    }

JHQL also support some complex queries.  The following::

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
(see http://develop.github.com/) whenever possible in real-world applications.

JHQL Grammar
============

A JHQL expression defines a Queryer.  A Queryer is an object that converts HTML
pages into JSON-like values.

A JHQL expression can be a *simple expression*, a *complex expression* or an *object
expression*.

A **simple expression** is a JSON string of the form: "type:value".  It includes at least
one colon ':'.  The substring before the first colon is the type and the substring
after the colon is the value.  It is a short-hand and is exactly equivalent to the
following **complex expression**::

    {
        "_type": (the type in the simple expression),
        "value": (the value in the simple expression)
    }

A **complex expression** is a JSON object with a "_type" field.  It defines a Queryer
whose type is the value of the "_type" field.  Other fields may be included and the
value of each field is assigned to the property of the Queryer with the field name
as the property's name and the field's value as the property's value.

For example::

    {
        "_type": "text",
        "value": "//div",
        "grep": "(\\d+)"
    }

It will create a Queryer of type "text" with its property "value" set to "//div" and
its property "grep" set to "(\\d+)".

The property value can be null, true, false, number, string, array and Queryer.
When the expected property value is a Queryer, it is also expressed as a (nested)
JHQL expression.

An **object expression** is a JSON object without the "_type" field. It defines a
special Object Queryer. The value of each field of this expression defines a nested
sub-Queryer.

Predefined Queryers
===================

text Queryer
------------

A text Queryer does an XPath query on the current DOM node.  The text content
of all matching nodes are concatenated and returned.

Properties:

- value
    (string)
    The XPath expression to apply on the current node.
- grep
    (string, optional)
    A regular expression to apply on the result of the XPath query.
    It must include exactly one capturing group and the content of that
    group will be the result.
- trim
    (boolean, default: false)
    If set to true, the result after XPath querying and
    grepping will be trimmed (the leading and trailing spaces will be removed).

Example::

    "text://p"

Applied on::

    <body><div><p>hello</p></div><p>world</p></body>

Yields:::

    "helloworld"



Java usage
==========

TODO: Add Java usage. 

Background
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
