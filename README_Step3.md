
### Useful Links:

* [Parse Tree](http://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/ParseTree.html)<br>An interface to access the tree of ```RuleContext``` objects created during a parse that makes the data structure look like a simple parse tree.<br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;The payload is either a ```Token``` or a ```RuleContext``` object <br><br>
* [Parse Tree Listener](http://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/ParseTreeListener.html)<br>
```java
public interface ParseTreeListener
```
This interface describes the minimal core of methods triggered by ParseTreeWalker. <br>
E.g.,
```java
ParseTreeWalker walker = new ParseTreeWalker();
walker.walk(myParseTreeListener, myParseTree);

// In the place of the myParseTree I am not sure what needs to be used,
// but what I understand from the source code pattern is that we can use
// the output of the microParser.program() (in Micro.java) and also I
// have created the Micro468Listener.java which implements
// MicroBaseListener (which implements ParseTreeListener) an instance of
// the Micro468Listener can be used in the place of myParseTreeListener
```
The above triggers events in your listener If you want to trigger events in multiple listeners during a single tree walk



* [Parse Tree Walker](http://www.antlr.org/api/Java/org/antlr/v4/runtime/tree/ParseTreeWalker.html#walk)
