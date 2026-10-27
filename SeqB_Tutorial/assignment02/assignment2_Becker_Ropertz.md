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



**2 Compare with an alignment tool**

a)
```java
Progressive_BeckerRopertz score clustalo-WW12.aln-fasta blosum62.txt gap=4
12 sequences, 23 columns
SP score: 3707
```

b)
```java
Progressive_BeckerRopertz score clustalw-ww12.fasta blosum62.txt gap=4
12 sequences, 23 columns
SP score: 3729
```

the following structure:
">" $\Rightarrow$ Header
1st line below $\Rightarrow$ Clustal W 
2nd line below $\Rightarrow$ our progressive alignment

```java
>O54971  
PLPPGWE-KRGRVYFVNWEDPRS  
PLPPGWEKR-GRVYFVNWEDPRS - column 8 to 10 mismatch, gap placement  
>O43165  
GLPSGWEERKGRTYYVNWTRPIM  
GLPSGWEERKGRTYYVNWTRPIM - match  
>NED4_HUMAN  
PLPPGWEERGR-IFYINWEDPRL  
PLPPGWEER-GRIFYINWEDPRL - column 10 t0 12 mismatch, gap placement  
>O14326  
PLPSGWEMRL------YWDDPRL  
PLPSGWEMRL----Y--WDDPRL - column 15 and 17 mismatch, gap placemen  
>O43165_2  
FLPPGWEMRIG-RPFFIWEDPRL  
FLPPGWEMRIGRPFFI-WEDPRL - column 12 to 17 mismatch, gap placement  
>PIN1_HUMAN  
KLPPGWEKRMGRVYYFNWERPSG  
KLPPGWEKRMGRVYYFNWERPSG - match  
>NED4_HUMAN_1  
PLPPGWEERQGRTYYVNWKRPTP  
PLPPGWEERQGRTYYVNWKRPTP - match  
>O75853  
PLPPGWE-VRGRIYPRL------  
PLPPGWEVR-GRIY------PRL - column 8 - 10 and 15 - 17  21 - 23 mismatch, gap placement  
>PUB1_SCHPO_2  
RLPPGWE-RRGRTYY--WIRPNL  
RLPPGW-ERRGRTYY--WIRPNL - column 7 and 8 mismatch, gap placement  
>YA65_CHICK  
PLPPGWEMAKGQRYFLNWQDPRK  
PLPPGWEMAKGQRYFLNWQDPRK - match  
>I83196_2  
GLPPGWEEKQGRSYY--WSKPTM  
GLPPGWEEKQGRSYY--WSKPTM - match  
>YA65_MOUSE  
PLPDGWEQAMGEVYYINWLDPRL  
PLPDGWEQAMGEVYYINWLDPRL - match
```

This comparison shows that columns 7 to 11, 12 to 17 and 21 to 23 have mismatches. This matches 9 columns exactly and 14 that differ overall. The differences mostly concentrated in the middle of the domain, columns 7 to 17. 

The alignments differ in:
- PUB1_SCHPO_2  at columns 7 and 8
- O75853 at columns 8 to 10, 15 to 17 and 21 to 23
- O43165_2 at columns 12 and 17
- O14326 at columns 15 and 17
- NED4_HUMAN at column 10 and 12
- O54971 at column 8 to 10

c)
sp-score ClustalW: 3729
sp-score Clustal Omega: 3707
sp-score own alignment: 4373

Under the sum-of-pairs objective our own alignment scores highest with 4373.
The sp-score is only a model, it depends on the chosen matrix and gap penalty. 
It does not take the actual structure into account, which is illustrated by our own alignment having the highest sp-score despite fewer than 50% of ClustalW's 23 columns are reproduced. 
Two features make the tools depart from this objective. By using distances along a guide tree (e.g. by Neighbor-Joining), the most similar sequences are aligned first and then the rest is merged one after the other. With that, early decisions on gap placements are never reconsidered and the final alignment is not an optimization of the sum-of-pairs score, which means a higher scoring alignment can exist.
The sum-of-pairs score also treats every pair equally. So closely related sequences dominate the scoring, the scaling defect. 
This is countered by turning the edge weights into sequence weights and with that down-weighing closely related sequences, which results in a weighted rather than a plain sp-score.

**3 The sum-of-pairs score**


**4 Three sequences by dynamic programming**
a)
computing a score of the displayed alignment
scoring with blosum62
residue against a gap -4
two gaps 0

$$
\begin{array}{l|ccccccc|l} & A & - & G & D & - & E & - \\ 
& A & S & G & -& - & E & - \\ 
& A & - & - & D & S & E & E \\ \hline 
\text{score} & 12 & -8 & -2 & -2 & -8 & 15 & -8 & -1\end{array}
$$
b)
$$
\begin{array}{l|ccccc|l} & A & G & - & D & E \\ 
& A & S & - &G & E \\ 
& A  & D & S & E & E \\ \hline 
\text{score} & 12 & -1 & -8 & -1  &  15 & 17\end{array}
$$
c)
$$
\begin{array}{l|ccccc|l} & A & - & G & D & E \\ 
& A & - & S &G & E \\ 
& A  & D & S & E & E \\ \hline 
\text{score} & 12 & -8 & 4 & -1  &  15 & 22\end{array}
$$

d)
The three-dimensional matrix for these three sequences have at least $(5+1)*(4+1)*(4+1) = 150$ cells.
An interior cells has $2^3 -1 = 7$ predecessors.
For ten sequences of length 100 there would be $101^10 = 1.1 * 10^{20}$ cells and $2^{10} -1 = 1023$ predecessors.

Because of this exponential growth a heuristic is used practice instead of the exact alignment. 

**5 Profile alignment by hand**

Exercise 2.5:

a)
two identical residues +1
two different residues -1
residue against gap -1
gap against gap 0
$$
\begin{array}{|c|c|c|c|}
\hline
 F & 0 & AA & C- & TC \\ \hline
0 & 0\cellcolor{red} & -4 & -6 & -10\\ \hline
AA & -4 &\cellcolor{red} 4 & 2 & -2\\ \hline
A- & -6 & 2 &\cellcolor{red} 1 & -2\\ \hline
CG & -10 & -2 & 0 & \cellcolor{red} -1 \\ \hline
\end{array}   
$$
b)
$$
\begin{array}{l|ccc} P_1 & A & C & T \\ P_2 & A & - & C \\ \hline Q_1 & A & A & C \\ Q_2 & A & - & G \end{array}
$$
The traceback runs along the diagonal of the matrix. There were no two similar scores for a cell from the diagonal, the left score or the top score, which makes the traceback unique. 
