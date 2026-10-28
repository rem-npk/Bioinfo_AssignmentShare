import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * ILP4MSA_YOUR_NAME.java -- writes the maximum-weight-trace problem for three sequences
 * (Section 2.6.4 of the handout) as an integer linear program in the input format of
 * lp_solve (Section 2.6.5).
 * Sequence Bioinformatics, WS 2026/27, Assignment 3.
 * Author: YOUR_NAME
 *
 * Usage: java ILP4MSA_YOUR_NAME <sequences.fasta> <match> <mismatch>  > problem.lp
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
public class ILP4MSA_YOUR_NAME {

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.err.println("Usage: java ILP4MSA_YOUR_NAME <sequences.fasta> <match> <mismatch>");
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
        // if the two residues are identical and mismatch otherwise; a line break between
        // terms is allowed, a long line is not a problem either)
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
        return count;
    }

    /**
     * Prints one constraint x + x' + x'' <= 2 for every simple mixed cycle through all three
     * sequences (Section 2.6.4): three edges, one between each pair of sequences, that close
     * a cycle with follow edges running forward in each sequence, in either direction around
     * the triangle. Three edges that meet in one column, with no follow edge, are not a
     * mixed cycle. Returns the number of constraints.
     */
    static int printThreeSequenceCycles(PrintWriter out, String[] seq) {
        var count = 0;
        // todo
        return count;
    }

    /** Prints  bin x1_1_2_1, x1_1_2_2, ... ;  and returns the number of variables declared. */
    static int printBinary(PrintWriter out, String[] seq) {
        var count = 0;
        // todo
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
