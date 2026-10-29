import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * ILP4MSA_Becker_Ropertz.java -- writes the maximum-weight-trace problem for three sequences
 * (Section 2.6.4 of the handout) as an integer linear program in the input format of
 * lp_solve (Section 2.6.5).
 * Sequence Bioinformatics, WS 2026/27, Assignment 3.
 * Author: Remi Becker
 *         Nina Ropertz
 *
 * Usage: java ILP4MSA_Becker_Ropertz <sequences.fasta> <match> <mismatch>  > problem.lp
 *        lp_solve -S3 problem.lp
 *
 * The alignment edge between position i of sequence s and position j of sequence t
 * (s < t; sequences and positions numbered from 1) is the binary variable x<s>_<i>_<t>_<j>,
 * see variable() below. It is weighted by <match> if the two residues are identical and by
 * <mismatch> otherwise. The program prints the objective, one constraint for every simple
 * mixed cycle through two of the sequences, one for every simple mixed cycle through all
 * three, and the declaration of all variables as binary. The counts go to standard error.
 *
 * Fill in the four methods marked "todo". Everything else is provided.
 */
public class ILP4MSA_Becker_Ropertz {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: java ILP4MSA_Becker_Ropertz <sequences.fasta> <match> <mismatch>");
            System.exit(1);
        }
        var seq = readFastA(args[0]);
        if (seq.length != 3) {
            System.err.println("Expected three sequences, found " + seq.length);
            System.exit(1);
        }
        var match = Integer.parseInt(args[1]);
        var mismatch = Integer.parseInt(args[2]);

        var out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(System.out)));
        out.println("/* maximum weight trace of " + seq[0] + ", " + seq[1] + ", " + seq[2]
                + "; match " + match + ", mismatch " + mismatch + " */");
        var variables = printObjective(out, seq, match, mismatch);
        var twoCycles = printTwoSequenceCycles(out, seq);
        var threeCycles = printThreeSequenceCycles(out, seq);
        var declared = printBinary(out, seq);
        out.flush();

        System.err.println("ILP for " + seq[0] + ", " + seq[1] + ", " + seq[2] + " (match " + match + ", mismatch " + mismatch + ")");
        System.err.println("Variables:                       " + variables);
        System.err.println("Constraints, two sequences:      " + twoCycles);
        System.err.println("Constraints, three sequences:    " + threeCycles);
        System.err.println("Variables declared binary:       " + declared);
    }

    /**
     * The name of the variable for the alignment edge between position i of sequence s and
     * position j of sequence t. Sequences and positions are numbered from 1; the sequence
     * with the smaller number comes first, so that every edge has exactly one name.
     */
    static String variable(int s, int i, int t, int j) {
        if (s > t)
            return variable(t, j, s, i);
        return "x" + s + "_" + i + "_" + t + "_" + j;
    }

    /** Prints the objective, one term per edge, and returns the number of variables. */
    static int printObjective(PrintWriter out, String[] seq, int match, int mismatch) {
        var count = 0;
        // todo: print  max: 4 x1_1_2_1 + 1 x1_1_2_2 + ... ;  (the weight of an edge is match
        out.print("max:");
        //first two for loops determine the sequence pairs s and t (1-2, 1-3, 2-3)
        //third for loop -> position i in sequence s, fourth for loop -> position j in sequence t)
        for (int s = 1; s < 3; s++)
            for (int t = s + 1; t <= 3; t++)
                for (int i = 1; i <= seq[s - 1].length(); i++)
                    for (int j = 1; j <= seq[t - 1].length(); j++) {
                        int w = 0;
                        if(seq[s - 1].charAt(i - 1) == seq[t - 1].charAt(j - 1)){
                          w = match;
                        }else{
                            w = -mismatch;
                        }
                        out.print(" " + (w >= 0 ? "+" : "-") + Math.abs(w) + " " + variable(s, i, t, j));
                        count++;
                    }
        out.println(" ;");
        return count;
    }

    /**
     * Prints one constraint x + x' <= 1 for every simple mixed cycle through two of the
     * sequences: two different edges between the same two sequences that cross or share an
     * endpoint (Section 2.6.5). Returns the number of constraints.
     */
    static int printTwoSequenceCycles(PrintWriter out, String[] seq) {
        var count = 0;
        // todo
        //first two loops to determin the sequence pairs s and t
        for(int s = 1; s <3; s++){
            for(int t = s+1; t <=3; t++){
                //lengths of current sequence s and t
                var n = seq[s-1].length();
                var m = seq[t-1].length();

                //case i == j, both edges start at the same position in s
                for(int i = 0; i < n; i++) {
                    for (int l = 0; l < m-1; l++) {
                        for (int k = l + 1; k < m; k++) {
                            out.println(variable(s, i+1, t, l+1) + " + " +variable(s, i+1, t, k+1) + "<= 1;");
                            count++;
                        }
                    }
                }
                //case l == k, both edges start at the same position in t
                for(int l = 0; l <m; l++){
                    for(int i = 0; i < n-1; i++){
                        for(int j = i+1; j <n; j++){
                            out.println(variable(s,i+1, t, l+1) + " + " + variable(s, j+1, t, l+1) + "<= 1;");
                            count++;
                        }
                    }
                }
                //i,k and j, l cross
                for(int i = 0; i < n-1; i++){
                    for(int l = 0; l < m; l++){
                        for(int j = n-1; j > i; j--){
                            for(int k = m-1; k>l; k--){
                                out.println(variable(s, i+1, t, k+1) + " + " + variable(s, j+1, t, l+1) + "<= 1;");
                                count++;
                            }
                        }
                    }
                }
            }
        }
        return count;
    }

    /**
     * Prints one constraint x + x' + x'' <= 2 for every simple mixed cycle through all three
     * sequences (Section 2.6.4): three edges, one between each pair of sequences, that close
     * a cycle with follow edges running forward in each sequence, in either direction around
     * the triangle. Three edges that meet in one column, with no follow edge, are not a
     * mixed cycle. Returns the number of constraints.
     */
    //concept by hand, programming by AI, because...
    static int printThreeSequenceCycles(PrintWriter out, String[] seq) {
        var count = 0;
        var lenA = seq[0].length();
        var lenB = seq[1].length();
        var lenC = seq[2].length();

        // case 1:
        for (int j = 1; j <= lenA; j++)
            for (int i = 1; i < j; i++)
                for (int b = 1; b <= lenB; b++)
                    for (int c = 1; c <= lenC; c++) {
                        out.println(variable(1, j, 2, b) + " + " + variable(2, b, 3, c) + " + " + variable(3, c, 1, i) + " <= 2;");
                        out.println(variable(1, j, 3, c) + " + " + variable(3, c, 2, b) + " + " + variable(2, b, 1, i) + " <= 2;");
                        count += 2;
                    }

        // case 2:
        for (int l = 1; l <= lenB; l++)
            for (int k = 1; k < l; k++)
                for (int a = 1; a <= lenA; a++)
                    for (int c = 1; c <= lenC; c++) {
                        out.println(variable(1, a, 2, k) + " + " + variable(2, l, 3, c) + " + " + variable(3, c, 1, a) + " <= 2;");
                        out.println(variable(1, a, 3, c) + " + " + variable(3, c, 2, k) + " + " + variable(2, l, 1, a) + " <= 2;");
                        count += 2;
                    }

        // case 3:
        for (int q = 1; q <= lenC; q++)
            for (int p = 1; p < q; p++)
                for (int a = 1; a <= lenA; a++)
                    for (int b = 1; b <= lenB; b++) {
                        out.println(variable(1, a, 2, b) + " + " + variable(2, b, 3, p) + " + " + variable(3, q, 1, a) + " <= 2;");
                        out.println(variable(1, a, 3, p) + " + " + variable(3, q, 2, b) + " + " + variable(2, b, 1, a) + " <= 2;");
                        count += 2;
                    }

        // case 4:
        for (int j = 1; j <= lenA; j++)
            for (int i = 1; i < j; i++)
                for (int l = 1; l <= lenB; l++)
                    for (int k = 1; k < l; k++)
                        for (int c = 1; c <= lenC; c++) {
                            out.println(variable(1, j, 2, k) + " + " + variable(2, l, 3, c) + " + " + variable(3, c, 1, i) + " <= 2;");
                            out.println(variable(1, j, 3, c) + " + " + variable(3, c, 2, k) + " + " + variable(2, l, 1, i) + " <= 2;");
                            count += 2;
                        }

        // case 5:
        for (int j = 1; j <= lenA; j++)
            for (int i = 1; i < j; i++)
                for (int q = 1; q <= lenC; q++)
                    for (int p = 1; p < q; p++)
                        for (int b = 1; b <= lenB; b++) {
                            out.println(variable(1, j, 2, b) + " + " + variable(2, b, 3, p) + " + " + variable(3, q, 1, i) + " <= 2;");
                            out.println(variable(1, j, 3, p) + " + " + variable(3, q, 2, b) + " + " + variable(2, b, 1, i) + " <= 2;");
                            count += 2;
                        }

        // case 6:
        for (int l = 1; l <= lenB; l++)
            for (int k = 1; k < l; k++)
                for (int q = 1; q <= lenC; q++)
                    for (int p = 1; p < q; p++)
                        for (int a = 1; a <= lenA; a++) {
                            out.println(variable(1, a, 2, k) + " + " + variable(2, l, 3, p) + " + " + variable(3, q, 1, a) + " <= 2;");
                            out.println(variable(1, a, 3, p) + " + " + variable(3, q, 2, k) + " + " + variable(2, l, 1, a) + " <= 2;");
                            count += 2;
                        }

        // case 7:
        for (int j = 1; j <= lenA; j++)
            for (int i = 1; i < j; i++)
                for (int l = 1; l <= lenB; l++)
                    for (int k = 1; k < l; k++)
                        for (int q = 1; q <= lenC; q++)
                            for (int p = 1; p < q; p++) {
                                out.println(variable(1, j, 2, k) + " + " + variable(2, l, 3, p) + " + " + variable(3, q, 1, i) + " <= 2;");
                                out.println(variable(1, j, 3, p) + " + " + variable(3, q, 2, k) + " + " + variable(2, l, 1, i) + " <= 2;");
                                count += 2;
                            }

        return count;
    }

    /** Prints  bin x1_1_2_1, x1_1_2_2, ... ;  and returns the number of variables declared. */
    static int printBinary(PrintWriter out, String[] seq) {
        var count = 0;
        // todo
        out.print("bin");
        for (int s = 1; s <= 3; s++)
            for (int t = s + 1; t <= 3; t++)
                for (int i = 1; i <= seq[s - 1].length(); i++)
                    for (int j = 1; j <= seq[t - 1].length(); j++) {
                        if(count == 0){
                            out.print(" " + variable(s, i, t, j));
                        }else {
                            out.print((", ") + variable(s, i, t, j));
                        }
                        count++;
                    }
        out.println(" ;");
        return count;
    }

    /** Reads the sequences of a FastA file (names and comments are ignored). */
    static String[] readFastA(String file) throws IOException {
        var sequences = new ArrayList<String>();
        var current = new StringBuilder();
        for (var line : Files.readAllLines(Path.of(file))) {
            if (line.startsWith(">")) {
                if (current.length() > 0)
                    sequences.add(current.toString());
                current.setLength(0);
            } else
                current.append(line.trim().toUpperCase());
        }
        if (current.length() > 0)
            sequences.add(current.toString());
        return sequences.toArray(new String[0]);
    }
}
