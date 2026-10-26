**Assignment 2**

WS26/27 
Remi Becker, Nina Ropertz 

**1 Sum-of-pairs score and progressive alignment**

a)
Test 1:
```java
Progressive_BeckerRopertz score exercise-2-2.fasta .\blosum62.txt gap=4
4 sequences, 5 columns
SP score: 23
```

Test 2:
```java
Progressive_BeckerRopertz score exercise-2-3.fasta blosum62.txt gap=4
3 sequences, 7 columns
SP score: -1
```

c)
Test 1:
```java
Progressive_BeckerRopertz align, 3 sequences, 2 merges, gap=1
merge 1: X + Z: cross-term score -1, profile of 2 x 3
merge 2: m1 + Y: cross-term score -2, profile of 3 x 3
Alignment:
X              TAG
Z              CAA
Y              CTA
SP score: -3   (sum of the merge scores: -3)
```

Test 2:
```java
Progressive_BeckerRopertz align, 3 sequences, 2 merges, gap=1
merge 1: Y + Z: cross-term score 1, profile of 2 x 3
merge 2: m1 + X: cross-term score -2, profile of 3 x 4
Alignment:
Y              CTA-
Z              CAA-
X              -TAG
SP score: -1   (sum of the merge scores: -1)
```

Test 3:
```java
Progressive_BeckerRopertz align, 12 sequences, 11 merges, gap=4
merge 1: O54971 + O75853: cross-term score 57, profile of 2 x 22
merge 2: O14326 + O43165_2: cross-term score 58, profile of 2 x 22
merge 3: m2 + YA65_CHICK: cross-term score 125, profile of 3 x 23
merge 4: m3 + YA65_MOUSE: cross-term score 211, profile of 4 x 23
merge 5: m4 + NED4_HUMAN: cross-term score 328, profile of 5 x 23
merge 6: m5 + m1: cross-term score 640, profile of 7 x 23
merge 7: O43165 + I83196_2: cross-term score 89, profile of 2 x 23
merge 8: m7 + NED4_HUMAN_1: cross-term score 191, profile of 3 x 23
merge 9: m8 + PUB1_SCHPO_2: cross-term score 238, profile of 4 x 23
merge 10: m9 + PIN1_HUMAN: cross-term score 326, profile of 5 x 23
merge 11: m6 + m10: cross-term score 2110, profile of 12 x 23
Alignment:
O14326         PLPSGWEMRL----Y--WDDPRL
O43165_2       FLPPGWEMRIGRPFFI-WEDPRL
YA65_CHICK     PLPPGWEMAKGQRYFLNWQDPRK
YA65_MOUSE     PLPDGWEQAMGEVYYINWLDPRL
NED4_HUMAN     PLPPGWEER-GRIFYINWEDPRL
O54971         PLPPGWEKR-GRVYFVNWEDPRS
O75853         PLPPGWEVR-GRIY------PRL
O43165         GLPSGWEERKGRTYYVNWTRPIM
I83196_2       GLPPGWEEKQGRSYY--WSKPTM
NED4_HUMAN_1   PLPPGWEERQGRTYYVNWKRPTP
PUB1_SCHPO_2   RLPPGW-ERRGRTYY--WIRPNL
PIN1_HUMAN     KLPPGWEKRMGRVYYFNWERPSG
SP score: 4373   (sum of the merge scores: 4373)
```

d)







