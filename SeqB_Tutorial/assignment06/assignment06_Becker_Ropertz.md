
Assignment06 WS 26/27
Remi Becker, Nina Ropertz

# Part A: Suffix tree and maximal unique matches in Java

## 1 The naive suffix tree


## 2 Maximal unique matches


# Part B: The suffix array and the LCP array by hand

## 3 The two arrays, and searching with them



## 4 Kasai's algorithm, and back to the tree



## 5 MUMs in the generalized suffix array

S= ggtact T = ctatac X = S#T$
a)

$X = [ggtact\#ctatac\$]$ 

lexicographic order: $ < # < a < c < g < t

| k   | SA[k] | from | LCP[k] | suffix $X_{SA[k]}$ |
| --- | ----- | ---- | ------ | ------------------ |
| 1   | 14    |      | -      | $                  |
| 2   | 7     |      | 0      | $\#ctatac$$        |
| 3   | 12    | T,5  | 0      | ac$                |
| 4   | 4     | S,4  | 2      | act#ctatac$        |
| 5   | 10    | T,3  | 1      | atac$              |
| 6   | 13    | T,6  | 0      | c$                 |
| 7   | 5     | S,5  | 1      | ct#ctatac$         |
| 8   | 8     | T,1  | 2      | ctatac$            |
| 9   | 1     | S,1  | 0      | ggtact#ctatac$     |
| 10  | 2     | S,2  | 1      | gtact#ctatac$      |
| 11  | 6     | S,6  | 0      | t#ctatac$          |
| 12  | 11    | T,4  | 1      | tac$               |
| 13  | 3     | S,3  | 3      | tact#ctatac$       |
| 14  | 9     | T,2  | 2      | tatac$             |



b)



