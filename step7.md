# Step 7

## Action Items

*   Make sure to correct the extra register usage for local variables parameters
```

    \\ from 
    STORE$TYPE$ $-2 !T$No$
    $COMP_OP$ $-1 !T$No$ label$NUMBER$
    
    \\ to
    $COMP_OP$ $-1 $-2 label$NUMBER

```

- [ ]  Eliminate a lot of unnecessary dead code
    - [ ]  Also refactor the code after the eliminating dead and redundant code

### Creating a Control Flow Graph

- [x]   Add unlnk and ret whenever we exit the function
- [x]   Create a list of all IR Code (IR Nodes)

- [x]   Create the successor and predecessor of each IR Node in the IR Node List
    - [x]  First, do the Predecessor
    - [x]  Second, do the Successor

- [ ]   Create GEN and KILL sets
    - [ ]  PUSH (GEN and No KILL)
    - [ ]  POP (KILL and No GEN)
    - [ ]  WRITE (GEN and No KILL)
    - [ ]  READ (KILL and No GEN)
    - [ ]  CALL 
        - [ ]   All Global Variables in GEN Set, Parameters used in the Function Call
        - [ ]   KILL is empty

- [ ]   Define IN (Live-in) and OUT (Live-Out)
    - [ ]   Initialize all the OUT sets for RETURN IR Nodes to all Global Variables
    - [ ]   LIVE-OUT = LIVE-IN(S_Node_1) U LIVE-IN(S_Node_2) U .... U LIVE-IN(S_Node_n)
    - [ ]   LIVE-IN = LIVE-OUT(Same Node) U GEN - KILL
    
- [ ] WorkList Algorithm

- [ ] Register Allocation Algorithm