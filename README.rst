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
    
        Queryer queryer = jhql.makeQueryer(new File("myexpression.jhql"));
    
        Object result = jhql.queryHtml(queryer, new File("theAboveExampe.html"));
    
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

    "int://*[@id='age']"

Applied on::

    <p>Age: <span id="age">12</span></p>

Yields (NOTE: this is an Integer)::

    12

Another example::

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
--------------

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
------------

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

A queryer that always return to a specified string.

Properties:

- value
    (string, required)
    The value which this queryer always return.

Example::

    "literal:Hello world!"

Applied on::

    ..... whatever .....

Yields::

    "Hello world!"

Note: Currently the return value can only be a String.

context Queryer
---------------

A queryer that returns a value in the **context**.

The **context** is a set of key-value pairs. The key is always a string
and the value can be any kind of value, as long as the implement
supports.  In Java, the Context is implemented as a Map<String, Object>
.  When a Queryer is to be applied on a DOM Node, the **context** is
also supplied.

All the examples above assume that the context is empty, which means
there is not any key-value pairs in the context. (i.e. an empty Map)

See the **Java Usage** section about how to apply a context.

Properties:

- value
    (string, required)
    The key of the key-value pair in the context whose value is
    supposed be returned.

Example::

    "context:hello_message"

Applied on::

    .... whatever ....

In this context::

    {
        "hello_message": "Hello world!",
        "user_name": "wks"
    }

Yields::

    "Hello world!"

zip Queryer
-----------

A queryer that "zips" multiple "list" Queryer's result into one list
of "big" objects.

This is useful when related informations are not contained in one big
structure.  A typical situation is sections interlaced by <h1>s and
<p>s.

    <h1>title1</h1>
    <p>hello</p>
    <h1>title2</h2>
    <p>world</p>
    ...

The neighboring <h1> and <p> are not nested into another element like
this::

    <div>
        <h1>title1</h1>
        <p>hello</p>
    </div>
    <div>...

A single "list" Queryer only work for the latter case, but the former
case need you to use a "zip" queryer combined with several "list"
Queryers.

The "zip" queryer is an analog to the "zip" function found in many
functional programming languages like Haskell as well as Python.

Properties:

- from
    (An "Object" Queryer, required)
    An Object Queryer containing many "list" queryers.
- alignTo
    (String, must be "shortest" or "longest", default: "shortest")
    Defines the final length if the result of each "list" queryer is
    not of the same length.  If "shortest", the final list will be as
    long as the shortest result and longer results are trimmed.  If
    "longest", all lists shorter than the longest will be padded with
    nulls to the length of the longest subresult before zipped.

Example::

    {
    	"_type": "zip",
    	"from": {
    	    "title": {
    	        "_type": "list",
    	        "from": "//h1",
    	        "select": "text:."
    	    },
    	    "content": {
    	        "_type": "list",
    	        "from": "//p",
    	        "select": "text:."
    	    }    	    
    	}
    }

Applied on::

    <h1>title1</h1>
    <p>hello</p>
    <h1>title2</h1>
    <p>world</p>

Yields::

    [
        { "title": "title1", "content": "hello" },
        { "title": "title2", "content": "world" },
    ]
    
Java Usage
==========

There is a facade class, org.github.wks.jhql.Jhql, which gives you
access to most of JHQL's functionalities.

As you have seen in the Introduction section, you should instantiate a
Jhql object in order to use JHQL.

    Jhql jhql = new Jhql();

To create a Queryer, use the overloaded Jhql.makeQueryer(...) methods.
The following example creates a Queryer from a text file encoded in
UTF-8.

    Queryer queryer = jhql.makeQueryer(new File("myexpression.jhql"));

The org.github.wks.jhql.query.Queryer interface is the parent of all
other Queryers. You can use Queryer.query(node, content) to make
queries, but it is recommended to use the Jhql facade methods, instead.

    Object result = jhql.queryHtml(queryer, new File("theAboveExampe.html"));

The output type depends on the Queryer's type.  JSON types, including
string, number, true, false and null, have their Java counterparts,
namely String, Integer, Boolean and the null pointer.  A JSON Array is
mapped to a Java ArrayList<Object> and a JSON Object is mapped to
Java's LinkedHashMap<String, Object>.

Working with Jackson
--------------------

JHQL relies on Jackson, a JSON library, for reading JSON-based
expressions. You may also use Jackson to turn a Java value into a JSON
value.

    ObjectMapper objectMapper = new ObjectMapper();
    String jsonString = objectMapper.writeValueAsString(result);
    System.out.println(jsonString);

Note that Jackson, by default, converts java.util.Date into an integer.
Consult Jackson's documents to find out how to change this behavior.

Using Jackson, you can also convert a Map<String, Object> into a
strongly-typed Java object following the JavaBean conventions.

Suppose you have your domain object::

    class Person {
        private String name;
        private int age;
        private Date birthDay;
        // getters and setters ...
    }

You may write your JHQL expression to match this class's property
names::

    {
        "name": "text://some/path",
        "age": "text://.[@id='age']",
        "birthDay": { "_type": "date", ... }
    }

Query to get a Map<String, Object> and then convert into your domain
object::

    Queryer queryer = jhql.makeQueryer("...");
    Object result = jhql.queryHtml(queryer, new File("personInfo.html"));
    
    ObjectMapper objectMapper = new ObjectMapper();
    Person person = objectMapper.convertValue(result, Person.class);

Now you have created your Person object by converting from the
"personInfo.html" web page.

Background
==========

I (Kunshan Wang) am a graduate student working on Data Mining. I
created this JQHL "query language" in order to work with real-world
web pages, specifically the news reports from various news sites.

Although more and more web sites now support the much cleaner RSS or
ATOM formats, parsing HTML is still necessary. It has brought me much
headache to find a proper way to extract useful information from an
HTML file.

I tried regular expressions. They are difficult to use because
**regular expressions are hardly readable** when they are written to
match a piece of HTML source code. You have to pay attention to many
regexp meta-characters that may appear in HTML codes.
You also need to remember whether you have enabled the "DOTALL" mode
and figure out whether you need "greedy" or "lazy" matching. If there
is a bug in the expression, it'll take you longer to find it out than
writing a new expression.

Then I moved to DOM and XPath. I wondered whether XPath actually work
with real-world web pages since ill-formed HTML pages may be "repaired"
into different DOM trees according to different HTML parser
implementations. Fortunately real-world experience told me that XPath
works for "most" pages, which include all the pages I care about. By
using more wildcards (like "//\*[@id='foo']") instead of full paths (
like "/body/p/p/div/span/table/tbody/tr/td/p/a[@id='foo']"), more
errors can be avoided. So **XPath is a reasonable way** to extract
things from real-world HTML pages.

But **it is not practical to write XPath expressions directly in Java**
. XPaths should be compiled before using because of performance. So you
should usually keep XPath objects as global singletons that stay away
from your HTML-extraction procedures. Moreover XPath alone is not
useful because you have to trim the matched text that contains leading
or trailing whitespaces, use regular expressions again to extract part
of the text (for example, you only need a number, but the <p></p> also
contains surrounding words) and then convert the text into other types
(like int or Date). If your desired information is repeated (like the
thread list in a forum page), then you have to use weird for-loops to
iterate over a list of matching nodes and assemble your own ArrayList
of matching records.

Then I find myself end up with a Java source file where XPath
expressions are separated from the main logics and I have to go back
and forth through the sourcecode to find what XPath expressions I used.
On the other hand, methods are filled with ugly boilerplate codes and
the class is filled with random ad-hoc methods which would never be
used again. **The code no longer express my extraction logic.**

Here is a showcase of such chaotic code:
https://github.com/wks/libbyr4j/blob/master/src/com/github/wks/libbyr4j/Byr.java

I feel that **I need a domain-specific language** that is dedicated to
this job.

There are also other "query languages" available. XQuery is a good
candidate. It queries on an XML document and outputs XML. I personally
dislike XML because it is harder for Java to work with XML than to work
with String, Integer, Boolean List and LinkedHashMap.

So I decided to create my own "query language". I considered using XML
as the format of the query expression, but I didn't use it due to my
dislike of XML.

Inspired by MongoDB's query language, I decided to use JSON as the
underlying representation of queries. Then JHQL was born.

Author
======

Kunshan Wang

wks1986@gmail.com
