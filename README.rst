===================================
JHQL - The JSON HTML Query Language
===================================

Author: Kunshan Wang

Using JHQL, you can easily extract interesting fields from an HTML web
page.

Introduction
============

JHQL is a JSON-based language expressing a mapping from an HTML page to
a JSON-like value.

Suppose you are looking at the HTML source code of a web page and you
are interested in the project name, the author's name and the download
address::

    ....
    <h1>The JHQL Project</h1>
    <div>Author: <span id="author-name">wks</span></div>
    <h2>Download</h2>
    <div id="download-box">
        Please click <a href="http://www.example.com/">here</a>.
    </div>
    ....

With adequate knowledge of Java and HTML/XML, you can use DOM or XPath
to locate and extract those fields.  But directly using the DOM or the
XPath API is boring and your code will soon become unreadable. There is
a more beautiful way to do this.  Write a JHQL expression like this
(You are right.  It is a JSON Value.)::

    {
        "projectName": "text://h1",
        "authorName": "text://*[@id='author-name']",
        "downloadUrl": "text://*[@id='download-box']/a/@href"
    }

Save this script as "myexpression.jhql".  In Java, do this::

    public static void main(String[] args) throws Exception {
        Jhql jhql = new Jhql();
    
        Queryer queryer = jhql.makeQueryer("myexpression.jhql");
    
        Object result = jhql.queryHtml(queryer, "theAboveExampe.html");
    
        System.out.println(result);
    }

It will print::

    {projectName=The JHQL Project, authorName=wks, downloadUrl=http://www.example.com/}

The 'result' variable is a Map<String, Object> in Java, although JHQL
was designed to generate a JSON Value roughly equivalent to::

    {
        "projectName": "The JHQL Project",
        "authorName": "wks",
        "downloadUrl": "http://www.example.com/"
    }

More Examples
=============

A JHQL expression is a JSON Value (most probably a JSON Object) like
this::

    {
        "username": "text:.//h1/text()",
        "fullname": "text:.//dd[@class='fn']",
        "membersince": {
            "_type": "date",
            "value": ".//div[@class='first vcard']/dl[2]/dd",
            "dateFormat": "MMM dd, yyyy"
        }
    }

As you can guess, it extracts the username and the fullname
from an HTML page using XPath.  It also extracts a date using a
combination of an XPath and a date format (see SimpleDateFormat).

When you apply this JHQL expression to this page
https://github.com/wks/ , it gives you::

    {
        "username" : "wks",
        "fullname" : "Kunshan Wang",
        "membersince" : "2010-08-18T16:00:00.000+0000"
    }

The result here is in the JSON form.  You actually get a LinkedHashMap
in Java, but you can convert it into JSON if you want. (Try Jackson)

A more complex query::

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
    
"publicRepos" selects multiple nodes.  It will first find all DOM nodes
using the XPath in the "from" part: ".//li[@class='public']".  Then it
does the query in the "select" on each matching node.

Apply this on https://github.com/wks/, you get::

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

Of course you should use the GitHub's native restful API (see 
http://develop.github.com/).  JHQL is only the last resort for you if
the site you need does not give you any such APIs.

JHQL Expression Grammar
=======================

A JHQL expression defines a Queryer.  A Queryer is an object that
converts HTML pages into JSON-like values.

A JHQL expression can be a *complex expression*, a *simple expression*
or an *object expression*.

A **complex expression** is a JSON object with a "_type" field.  This
kind of expression defines a Queryer using a type and many properties.
The type is the value of the "_type" field.  Other fields not beginning
with an underscope '_' define properties.

For example::

    {
        "_type": "text",
        "value": "//div",
        "grep": "(\\d+)"
    }

It will create a Queryer of type "text" with its property "value" set
to "//div" and its property "grep" set to "(\\d+)".

The property value can be null, true, false, number, string, array or
Queryer. When the expected property is a Queryer, it is also expressed
as a (nested) JHQL expression.

A **simple expression** is a JSON string of the form: "type:value".
It is exactly equivalent to the following **complex expression**::

    {
        "_type": (the type in the simple expression),
        "value": (the value in the simple expression)
    }

If there are multiple colons ':', the first colon separates the type
and the value.

An **object expression** is a JSON object without a "_type" field. It
defines a special Object Queryer (see below). Other fields whose name
do not begin with an underscope '_' are the Object Queryer's 
sub-Queryers.  The following expression::

    {
        "foo": "text://h1",
        "bar": "text://h2"
    }

contains two sub-Queryers named "foo" and "bar", defined by two simple
expressions "text://h1" and "text://h2", respectively.

Predefined Queryers
===================

text Queryer
------------

A text Queryer does an XPath query on the current DOM node.  The text
content of all matching nodes are concatenated and returned.

Properties:

- value
    (string, required)
    The XPath expression to apply on the current node.
- grep
    (string, optional)
    A regular expression to apply on the result of the XPath query.
    It must include exactly one capturing group and the content of that
    group will be the result.
- trim
    (boolean, optional, default: false)
    If set to true, the result will be trimmed (the leading and
    trailing spaces will be removed).

This Queryer does XPath querying and then grepping and then trimming,
in this order.

Example::

    "text://p"

Applied on::

    <body><div><p>hello</p></div><p>world</p></body>

Yields::

    "helloworld"

Another example::

    {
        "_type": "text",
        "value": "//p",
        "grep": "(\\d+)"
    }

Applied on::

    <p>The number is 123456!</p>

Yields (NOTE: this is a String)::

    "123456"

Yet another example::

    {
        "_type": "text",
        "value": "//p",
        "trim": true
    }

Applied on::

    <p>    hello world!    </p>

Yields::

    "hello world!"


int Queryer
-----------

Just like the **text** queryer. But it converts the result into an
integer.

Properties:

- value
    see **text** queryer
- grep
    see **text** queryer
- trim
    see **text** queryer

Example::

    {
        "_type": "int",
        "value": "//p",
        "grep": "(\\d+)"
    }

Applied on::

    <p>The number is 123456!</p>

Yields (NOTE: this is an Integer)::

    123456

Object Queryer
==============

Object Queryers are defined by the special **object expression** shown
above.  It has many sub-Queryers.  All sub-Queryers are applied on the
current DOM Node.  The result of the ObjectQueryer is a JSON Object (or
a Java Map<String, Object>). The results from each sub-Queryer is added
as a field of resulting JSON Object.

Example::

    {
        "foo": "text://h1",
        "bar": "text://h2",
        "baz": "text://h3"
    }

Applied on::

    <div><h3>!</h3><h2>world</h2><h1>hello</h1></div>

Yields::

    {
        "foo": "hello",
        "bar": "world",
        "baz": "!"
    }


list Queryer
==============

A list Queryer extracts values from multiple DOM Nodes sharing the same
XPath. It first gets all DOM Nodes that matches the XPath expression
of the "from" property.  Then the Queryer defined by the "select"
property is applied on each node matched by "from".  The result is a
JSON Array (or a Java List) of each result generated by the Queryer
in the "select" property.

Properties:

- from
    (string, required)
    The XPath expression to apply on the current node.
- select
    (Queryer, required)
    A sub-Queryer to apply on each matched node from "from".

Example::

    {
        "_type": "list",
        "from": "//p",
        "select": "text:."
    }

Applied on::

    <div><p>hello</p><p>world</p><p>!</p></div>

Yields::

    ["hello", "world", "!"]

Another Example::

    {
        "_type": "list",
        "from": "//a",
        "select": {
            "name": "text:.",
            "url": "text:./@href"
        }
    }

Applied on::

    <div>
        <a href="http://www.example.com/foo">foo</a>
        <a href="http://www.example.net/bar">bar</a>
        <a href="http://www.example.org/baz">baz</a>
    </div>

Yields::

    [
        {"name": "foo", "url": "http://www.example.com/foo"},
        {"name": "bar", "url": "http://www.example.net/bar"},
        {"name": "baz", "url": "http://www.example.org/baz"}
    ]

date Queryer
------------

Just like the **text** queryer. But it converts the result into a
java.util.Date object.  This is only meaningful in Java.  You can
adjust the date format as defined by java.text.SimpleDateFormat .

Properties:

- value
    see **text** queryer
- grep
    see **text** queryer
- trim
    see **text** queryer
- dateFormat
    (string, required)
    The date format as defined by java.text.SimpleDateFormat

This Queryer does XPath querying, grepping, trimming and then convert
the result into a Date object according to the dateFormat property.

Example::

    {
        "_type": "date",
        "value": "//p",
        "grep": "(\\d+-\\d+-\\d+)",
        "dateFormat": "yyyy-MM-dd"
    }

Applied on::

    <div><p>Today is 2011-12-23.</p></div>

Yields::

    A java.util.Date representing December 23rd, 2011.

literal Queryer
---------------

(TODO)

context Queryer
---------------

(TODO)

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
