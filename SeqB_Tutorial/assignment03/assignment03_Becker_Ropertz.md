
**1 Three sequences by integer linear programming**

```java
java ILP4MSA_Becker_Ropertz.java  book.fasta 4 1 | Out-File -Encoding ascii problem.lp
ILP for TCAG, TCCTA, CTAG (match 4, mismatch 1)
Variables:                       56
Constraints, two sequences:      344
Constraints, three sequences:    2840
Variables declared binary:       56
```

lp_solve output:
Value of objective function: 36.00000000

Actual values of the variables:
x1_1_2_1                        1
x1_1_2_2                        0
x1_1_2_3                        0
x1_1_2_4                        0
x1_1_2_5                        0
x1_2_2_1                        0
x1_2_2_2                        0
x1_2_2_3                        1
x1_2_2_4                        0
x1_2_2_5                        0
x1_3_2_1                        0
x1_3_2_2                        0
x1_3_2_3                        0
x1_3_2_4                        0
x1_3_2_5                        1
x1_4_2_1                        0
x1_4_2_2                        0
x1_4_2_3                        0
x1_4_2_4                        0
x1_4_2_5                        0
x1_1_3_1                        0
x1_1_3_2                        0
x1_1_3_3                        0
x1_1_3_4                        0
x1_2_3_1                        1
x1_2_3_2                        0
x1_2_3_3                        0
x1_2_3_4                        0
x1_3_3_1                        0
x1_3_3_2                        0
x1_3_3_3                        1
x1_3_3_4                        0
x1_4_3_1                        0
x1_4_3_2                        0
x1_4_3_3                        0
x1_4_3_4                        1
x2_1_3_1                        0
x2_1_3_2                        0
x2_1_3_3                        0
x2_1_3_4                        0
x2_2_3_1                        0
x2_2_3_2                        0
x2_2_3_3                        0
x2_2_3_4                        0
x2_3_3_1                        1
x2_3_3_2                        0
x2_3_3_3                        0
x2_3_3_4                        0
x2_4_3_1                        0
x2_4_3_2                        1
x2_4_3_3                        0
x2_4_3_4                        0
x2_5_3_1                        0
x2_5_3_2                        0
x2_5_3_3                        1
x2_5_3_4                        0

```java
java ILP4MSA_Becker_Ropertz.java  long.fasta 4 1 | Out-File -Encoding ascii problem2.lp    
ILP for GATTCA, GTTCA, ATCAG (match 4, mismatch 1)
Variables:                       85
Constraints, two sequences:      770
Constraints, three sequences:    9150
Variables declared binary:       85
```