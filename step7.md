# Step 7

### Action Items

*   Make sure to correct the extra register usage for local variables parameters
```
    
    \\ from 
    STORE$TYPE$ $-2 !T$No$
    $COMP_OP$ $-1 !T$No$ label$NUMBER$
    
    \\ to
    $COMP_OP$ $-1 $-2 label$NUMBER
    
```

*   Eliminate a lot of unnecessary dead code
    *  Also refactor the code after the eliminating dead and redundant code