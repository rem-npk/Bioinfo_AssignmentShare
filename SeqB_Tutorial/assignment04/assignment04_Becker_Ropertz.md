### Assignment 4

WS26/27
Remi Becker, Nina Ropertz

#### 1: Biological Background
- What are the data?
  78 protein-coding plastome genes
- Which plant families and genera do the 17 genomes represent?
  3 families with a total of 8 genera:
    - Nymphaeaceae (5 genera)
    - Cambombaceae (2 genera)
    - Hydatellaceae (1 genus)
- Which of them is the outgroup?
  Hydatellaceae
- What is the main question?
  Evaluating evolutionary relationships within Nymphaeales, especially to re-assess the monophyly of Nymphaeaceae by assessing whether the results of phylogenetic inference performed individual genes are congruent across genes and those performed on the multiple alignment congruent under different data partitioning schemes.
- Why does the genus *Nuphar* matter for it?
  The phylogenetic position of the genus *Nuphar* is so far unresolved and would affect the taxanomic status of Nymphaeaceae as a monophyletic group. Determining this would help in the effort to understand the evolutionary history of early-diverging angiosperms.

#### 2: Multiple sequence alignment
a) 
We used MAFFT 7.526 with the following parameters <mark style="background: #FFB8EBA6;">--auto --thread 4</mark>. The programm was initiated with one call per gene in a loop over all 78 FASTA-files. The strategy is chosen depending on the size of the data. According to the log-files, L-INS-i was used for 74 genes while FFT-NS-i was used for the four genes (rpoB, rpoC2, ycf1, ycf2). These four genes have the longest alignment lengths (3234, 4311, 6860, 7721), which suggests MAFFT switched to a faster strategy for the longest alignments. The gap costs are the standard values (opening 1.53, extending 0).

MAFFT is a heuristic progressive method with iterative refinement. Based on pairwise alignments a UPGMA-guide-tree is computed for L-INS-i. For FFT-NS-i, MAFFT derives the distances from shared k-mer counts instead of full pairwise alignments. The sequences are initially aligned along the tree, and subsequently the alignment is iteratively refined by means of splitting into subgroups and realigned as long as the score improves.

- L-INS-i additionally uses local pairwise alignments as additional information and is consistency based with that. It is the most accurate but slowest version.

- FFT-NS-i starts with a fast progressive alignment. The pairwise alignment is sped up by a Fast-Fourier-Transformation and later refined iteratively. It does not use lokal pair information but is much faster and more suitable for long sequences.

Both are heuristics, because the exact multiple alignment is NP-hard. MAFFT is an independent tool, but its core is the progressive alignment of Section 2.4, with the same weakness in early errors bound into it. This problem is addressed by the iterative refinement and the consistency information in L-INS-i. In the book, MAFFT is listed under other tools

commands used in Windows PowerShell:
```powershell
# creating folder aln for the alignment
mkdir aln

# saving the path to maaft.bat in the variable $mafft
$mafft = "C:\path\to\mafft-7.526-win64-signed\mafft-win\mafft.bat"

# alignment (run in the folder with the FASTA-files)
Get-ChildItem *.fasta | Where-Object { $_.Name -notlike "*.aln.fasta" } | ForEach-Object {      
>>      $g = $_.BaseName
>>      cmd /c "`"$mafft`" --auto --thread 4 $($_.Name) > aln\$g.aln.fasta 2> aln\$g.mafft.log"
>>    }

# which strategy did MAFFT choose?
Select-String -Path aln\*.mafft.log -Pattern "L-INS-i|FFT-NS|E-INS-i|G-INS-i" | Group-Object Line | Select Count, Name

# which genes got FFT-NS-i?
Select-String -Path aln\*.mafft.log -Pattern "FFT-NS-i" | ForEach-Object { $_.Filename -replace '\.mafft\.log$','' }

# which alignments are the longest? (run in the folder that contains the table.tsv file)
 Import-Csv table.tsv -Delimiter "`t" | Sort-Object { [int]$_.aln_length } -Descending | Select-Object -First 8
```
 
 
b)
The table.tsv file contains a table with the number of sequences, the length of the alignment and the number of columns that contain a gap, for every gene. 

Number of genes: 78
shortest alignment: petL (93)
longest alignment:  ycf2 (7721)
Gene without gap: 27

c)

#### 3: visualization of multiple sequence alignments (1 point)



#### 4: Phylogenetic trees (2 points)



#### 5: Consensus tree (1 points)



#### 6: Consensus network (2 point)



#### 7: Sankoff's algorithm by hand (1 points)

Cost matrix M:
$$
\begin{array}{c|cccc}
\text{cost} & A & C & G & T\\ \hline
A & 0 & 2 & 1 & 2\\
C & 2 & 0 & 2 & 1\\
G & 1 & 2 & 0 & 2\\
T & 2 & 1 & 2 & 0\\
\end{array}
$$

<div style="text-align: center;"> <img src="Sankoff_algorithm_chart.png" width="256"> </div>

$$S_\alpha (v) = \min_\beta (S_\beta(u) + m(\alpha, \beta)) + \min_\beta (S_\beta(w) + m(\alpha,\beta))$$

child v, $S(v) = (2,1,2,4)$:
$$
\begin{array}{ccc}
\alpha & \text{calculation } (\beta = (A, C, G, T)) & \text{minimum}\\ \hline
A & (2+0), (1+2), (2+1), (4 +2)  & 2 \\
C & (2+2), (1+0), (2+2), (4+1) & 1 \\
G & (2+1), (1+2), (2+0), (4+2) & 2 \\
T & (2+2), (1+1), (2+2), (4+0) & 2 \\
\end{array}
$$


child w, $S(w) = (3,2,1,3)$:
$$
\begin{array}{ccc}
\alpha & \text{calculation }(\beta = (A,C,G,T)) & \text{minimum}\\ \hline
A & (3+0), (2+2), (1+1), (3+2) & 2 \\
C & (3+2), (2+0), (1+2), (3+1) & 2 \\
G & (3+1), (2+2), (1+0), (3+2) & 1 \\
T & (3+2), (2+1), (1+2), (3+0) & 3 \\
\end{array}
$$
Sum: $S(u) = (2+2) , (1+2), (2+1) , (2+3) = (4,3,3,5)$

$\Rightarrow$ the minimal overall costs are 3, assumed for u = C and u = G

$$
\begin{array}{ccc}
\alpha (\text{state of }u) & \text{child } v \text{ chooses }\beta & \text{child } w \text{ chooses }\beta \\ \hline
A & \text{A } (2+0) & \text{G } (1+1)\\
C & \text{C } ( 1+0) & \text{C }(2+0) \\
G & \text{G } (2+0) & \text{G }(1 + 0) \\
T & \text{C }(1+1) & \text{C, G or T } (3) \\
\end{array}
$$

traceback:
$$
\begin{array}{cccccc}
\text{u} & v\text{ chooses }\beta & \text{costs} & w\text{ chooses }\beta & \text{costs} & \text{sum} \\ \hline
C & C & 1+0 = 1 & C & 2+0 = 2 & 3 \\
G & G & 2+0 = 2 & G & 1+0 = 1 & 3 
\end{array}
$$

This means there are two equally optimal reconstructions (u,v,w) = (C, C, C) and (G, G, G).
There are no mutations on the edges of both, the costs are completely in the leaf vectors S(v) and S(w). 