
Assignment06 WS 26/27
Remi Becker, Nina Ropertz

# Part A: Suffix tree and maximal unique matches in Java

## 1 The naive suffix tree


## 2 Maximal unique matches


# Part B: The suffix array and the LCP array by hand

## 3 The two arrays, and searching with them

a)
T = tgcacgca$ n = 9 

lexicographic order: $ < # < a < c < g < t

| k   | SA[k] | LCP[k] | suffix $X_{SA[k]}$ | turning node     |
| --- | ----- | ------ | ------------------ | ---------------- |
| 1   | 9     | -      | $                  | no previous leaf |
| 2   | 8     | 0      | a$                 | root             |
| 3   | 4     | 1      | acgca$             | node a           |
| 4   | 7     | 0      | ca$                | root             |
| 5   | 3     | 2      | cacgca$            | node ca          |
| 6   | 5     | 1      | cgca$              | node c           |
| 7   | 6     | 0      | gca$               | root             |
| 8   | 2     | 3      | gcacgca$           | node gca         |
| 9   | 1     | 0      | tgcacgca$          | root             |
b)






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

| k   | rows       | prefix, h | (1) S/T?          | (2) h> LCP[k-1], LCP[k+1]?        | letters in front | (3)                                                           |
| --- | ---------- | --------- | ----------------- | --------------------------------- | ---------------- | ------------------------------------------------------------- |
| 4   | T,5 / S,4  | ac, 2     | $\checkmark$      | $\checkmark$ (2 > 0, 2 > 1)       | t / t            | $\unicode{x2717}$ two letters in front identical              |
| 5   | S,4 / T,3  | at, 1     | $\checkmark$      | $\unicode{x2717}$ (1 < 2, 1 > 2)  |                  |                                                               |
| 7   | T,6 / S, 5 | c, 1      | $\checkmark$      | $\unicode{x2717}$ (1 > 0, 1 < 2)  |                  |                                                               |
| 8   | S,5 / T,1  | ct, 2     | $\checkmark$      | $\checkmark$ (2 > 1, 2 > 0)       | a, #             | $\checkmark$ two letters differ, one is start of the sequence |
| 10  | S,1 / S,2  | g, 1      | $\unicode{x2717}$ | $\checkmark$ (1 > 0, 1 > 0)       |                  |                                                               |
| 12  | S,6 / T,4  | t, 1      | $\checkmark$      | $\unicode{x2717}$ ( 1 > 0, 1 < 3) |                  |                                                               |
| 13  | T,4 / S,3  | tac, 3    | $\checkmark$      | $\checkmark$ (3 > 1, 3 > 2)       | t / g            | $\checkmark$ two letters in front differ                      |
| 14  | S,3 / T,4  | ta, 2     | $\checkmark$      | $\unicode{x2717}$ ( 2 < 3)        |                  |                                                               |

maximal unique matches: 
1. ct at position S,5 and T,1
2. tac at position S,3 and T,4

ta is excluded on the premise that it fails condition 2 as show in the table above. Three suffixes start with ta: S,3, T,4 and T,2. This means ta occurs one in S and twice in T, which means it is not unique in T. It is therefore not a unique match even though it occurs in both sequences. This was tested by condition 2.
