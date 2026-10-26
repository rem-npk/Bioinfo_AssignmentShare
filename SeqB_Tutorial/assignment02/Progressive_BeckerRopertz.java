package assignment02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Progressive_BeckerRopertz.java -- sum-of-pairs scoring, profile alignment and progressive
 * alignment along a given guide tree, with a linear gap penalty.
 * Sequence Bioinformatics, WS 2026/27, Assignment 2.
 * Author: Becker, Ropertz
 *
 * Usage: java Progressive_BeckerRopertz align <sequences.fasta> <tree.txt> <matrix.txt> <gap>
 *        java Progressive_BeckerRopertz score <alignment.fasta> <matrix.txt> <gap>
 *
 * Scoring (Section 2.2.1 of the handout): two residues score s(a,b) from the matrix,
 * a residue against a gap scores -d, two gaps score 0. A profile alignment maximizes the
 * cross-terms, the pairs of symbols drawn one from each profile (Section 2.4.1); a column
 * with k residues set against a gap column inserted into the other profile, of l rows,
 * costs k*l*d. The score of the merged profile is then SP(P) + SP(Q) + cross-terms, so the
 * final SP score equals the sum of the merge scores. Break ties in the traceback diagonal
 * first, then a column of the first profile against a gap, then a column of the second
 * profile against a gap.
 *
 * Fill in the three methods marked "todo". Everything else (input, output, the progressive
 * loop over the merges) is provided.
 */

public class Progressive_BeckerRopertz{

    public static void main(String[] args) throws IOException {
        if (args.length == 5 && args[0].equals("align")) {
            var sequences = readFastA(args[1]);
            var merges = readMerges(args[2]);
            var matrix = readMatrix(args[3]);
            var d = Integer.parseInt(args[4]);
            align(sequences, merges, matrix, d);
        } else if (args.length == 4 && args[0].equals("score")) {
            var alignment = readFastA(args[1]);
            var matrix = readMatrix(args[2]);
            var d = Integer.parseInt(args[3]);
            var rows = new ArrayList<String>();
            for (var record : alignment)
                rows.add(record.sequence());
            for (var row : rows)
                if (row.length() != rows.get(0).length())
                    throw new IOException("Rows of different lengths: not an alignment");
            System.out.println("Progressive_BeckerRopertz score " + args[1] + " " + args[2] + " gap=" + d);
            System.out.println(rows.size() + " sequences, " + rows.get(0).length() + " columns");
            System.out.println("SP score: " + spScore(rows, matrix, d));
        } else {
            throw new IOException("Usage: java Progressive_BeckerRopertz align <sequences.fasta> <tree.txt> <matrix.txt> <gap>\n"
                    + "       java Progressive_BeckerRopertz score <alignment.fasta> <matrix.txt> <gap>");
        }
    }

    /** A profile: the names of its sequences and their aligned rows, all of the same length. */
    public record Profile(List<String> names, List<String> rows) {}

    /** Progressive alignment along the merges of the tree file (Algorithm 2.1 of the handout). */
    public static void align(List<FastARecord> sequences, List<String[]> merges, Map<String, Integer> matrix, int d) {
        System.out.println("Progressive_BeckerRopertz align, " + sequences.size() + " sequences, " + merges.size() + " merges, gap=" + d);
        var pool = new HashMap<String, Profile>();
        for (var record : sequences)
            pool.put(record.header(), new Profile(List.of(record.header()), List.of(record.sequence())));
        var total = 0;
        Profile last = null;
        for (var merge : merges) {
            var p = pool.remove(merge[1]);
            var q = pool.remove(merge[2]);
            if (p == null || q == null)
                throw new IllegalArgumentException("Merge " + merge[0] + ": unknown or already used profile " + (p == null ? merge[1] : merge[2]));
            var result = profileAlign(p, q, matrix, d);
            pool.put("m" + merge[0], result.profile());
            last = result.profile();
            total += result.score();
            System.out.println("merge " + merge[0] + ": " + merge[1] + " + " + merge[2] + ": cross-term score " + result.score()
                    + ", profile of " + last.rows().size() + " x " + last.rows().get(0).length());
        }
        if (pool.size() != 1)
            throw new IllegalArgumentException("The tree does not merge all sequences into one profile: " + pool.keySet());
        System.out.println("Alignment:");
        for (var k = 0; k < last.rows().size(); k++)
            System.out.printf("%-14s %s%n", last.names().get(k), last.rows().get(k));
        System.out.println("SP score: " + spScore(last.rows(), matrix, d) + "   (sum of the merge scores: " + total + ")");
    }

    public record Result(Profile profile, int score) {}

    /** Profile alignment by dynamic programming on the columns (Section 2.4.1): cross-terms only. */
    public static Result profileAlign(Profile p, Profile q, Map<String, Integer> matrix, int d) {
        // todo: dynamic programming on the columns of p (index i, across) and q (index j, down),
        // Section 2.4.1 of the handout: F(i,j) is the best cross-term score of the first i columns
        // of p against the first j columns of q; a diagonal step adds crossScore of the two columns,
        // a step that sets a column of p against a gap column inserted into q costs
        // residues(column) * q.rows().size() * d, and symmetrically for a column of q.
        // Then trace back (ties: diagonal, then p-column against a gap, then q-column against a gap)
        // and build the merged profile: the rows of p first, then those of q, all of the new length.


        int length_p = columns(p.rows()).length;
        int length_q = columns(q.rows()).length;

        int[][] profile_matrix = new int[length_p+1][length_q+1];

        for(int i = 1; i <= length_p; i++){
            profile_matrix[i][0] = profile_matrix[i-1][0] - residues(columns(p.rows())[i-1])* q.rows().size() * d;
        }

        for(int j = 1; j <= length_p + 1; j++){
            profile_matrix[0][j] = profile_matrix[0][j-1] - residues(columns(q.rows())[j-1])* p.rows().size() * d;
        }


        for(int i = 1; i <= length_p; i++ ){
            for(int j = 1; j <= length_q; j++){

                int diag_score = profile_matrix[i-1][j-1] + crossScore(columns(p.rows())[i-1],columns(q.rows())[j-1],matrix, d);
                int top_score = profile_matrix[i-1][j] - residues(columns(p.rows())[j-1]) * q.rows().size() * d;
                int left_score = profile_matrix[i][j-1] - residues(columns(q.rows())[i-1]) * p.rows().size() * d;

                profile_matrix[i][j] = Math.max(diag_score, Math.max(left_score, top_score));

            }
        }
        int i = length_p;
        int j = length_q;

        var newP = new ArrayList<StringBuilder>();
        for(int k = 0; k < p.rows().size(); k++){
            newP.add(new StringBuilder());
        }

        var newQ = new ArrayList<StringBuilder>();
        for( int l = 0; l < q.rows().size(); l++){
            newQ.add(new StringBuilder());
        }

        while(i > 0 || j > 0){

                if(i > 0 && j > 0 && profile_matrix[i-1][j-1] == profile_matrix[i][j] - crossScore(columns(p.rows())[i-1],columns(q.rows())[j-1],matrix, d)){
                    for(int n = 0; n < p.rows().size(); n++){
                        newP.get(n).append((columns(p.rows())[i-1]).charAt(n));
                    }
                    for(int m = 0; m < q.rows().size(); m++){
                        newQ.get(m).append((columns(q.rows())[j-1]).charAt(m));
                    }
                    i--;
                    j--;
                }else if(i>0 && profile_matrix[i][j] == profile_matrix[i-1][j] - residues(columns(p.rows())[j-1]) * q.rows().size() * d){
                    for(int n = 0; n < p.rows().size(); n++){
                        newP.get(n).append((columns(p.rows())[i-1]).charAt(n));
                    }
                    for(int m = 0; m < q.rows().size(); m++){
                        newQ.get(m).append("-");
                    }
                    i--;
                }else{
                    for(int n = 0; n < q.rows().size(); n++){
                        newQ.get(n).append((columns(q.rows())[j-1]).charAt(n));
                    }
                    for(int m = 0; m < p.rows().size(); m++){
                        newP.get(m).append("-");
                    }
                    j--;
                }

        }


        var names = new ArrayList<String>(p.names());
        names.addAll(q.names());
        var rows = new ArrayList<String>();
        for(var sb:newP){
            rows.add(sb.reverse().toString());
        }
        for(var sb:newQ){
            rows.add(sb.reverse().toString());
        }



        return new Result(new Profile(names, rows), profile_matrix[length_p][length_q]);

    }

    /** The columns of an alignment, each as a string of its symbols, top to bottom. */
    public static String[] columns(List<String> rows) {
        var L = rows.get(0).length();
        var cols = new String[L];
        for (var c = 0; c < L; c++) {
            var sb = new StringBuilder();
            for (var row : rows) sb.append(row.charAt(c));
            cols[c] = sb.toString();
        }
        return cols;
    }

    public static int residues(String column) {
        var k = 0;
        for (var c : column.toCharArray())
            if (c != '-') k++;
        return k;
    }

    /** Score of one pair of symbols: s(a,b), or -d for a residue against a gap, or 0 for two gaps. */
    public static int pairScore(char a, char b, Map<String, Integer> matrix, int d) {
        // todo: Section 2.2.1: two residues score matrix.get("" + a + b), a residue against a gap
        // scores -d, two gaps score 0

        if(a == '-' && b == '-'){
            return 0;
        }
        if(a == '-' || b == '-'){
            return -d;
        }
        return matrix.get("" + a + b);
    }

    /** The cross-terms of two columns: every pair of one symbol of the first and one of the second. */
    public static int crossScore(String colP, String colQ, Map<String, Integer> matrix, int d) {
        var score = 0;
        for (var a : colP.toCharArray())
            for (var b : colQ.toCharArray())
                score += pairScore(a, b, matrix, d);
        return score;
    }

    /** The sum-of-pairs score of an alignment (Definition 2.2 of the handout). */
    public static int spScore(List<String> rows, Map<String, Integer> matrix, int d) {
        // todo: Definition 2.2: sum over all columns and, within each column, over all pairs of
        // rows p < q, of pairScore(column.charAt(p), column.charAt(q), matrix, d)
        var score = 0;
        for(var i:columns(rows)){
            for(var j = 0; j < i.length(); j++){
                for(var n = j+1; n < i.length(); n++){
                    score += pairScore(i.charAt(j), i.charAt(n), matrix, d);
                }
            }
        }
        return score;
    }

    /** Reads a substitution matrix: a header line of letters, then one line per letter with its scores. */
    public static Map<String, Integer> readMatrix(String fileName) throws IOException {
        var matrix = new HashMap<String, Integer>();
        String[] header = null;
        for (var line : Files.readAllLines(Path.of(fileName))) {
            if (line.isBlank() || line.startsWith("#")) continue;
            var parts = line.trim().split("\\s+");
            if (header == null) {
                header = parts;
            } else {
                for (var k = 0; k < header.length; k++)
                    matrix.put(parts[0] + header[k], Integer.parseInt(parts[k + 1]));
            }
        }
        return matrix;
    }

    /** Reads the merges of a guide tree: one line per merge, "k A B", where A and B name sequences or earlier merges, "m3". */
    public static List<String[]> readMerges(String fileName) throws IOException {
        var merges = new ArrayList<String[]>();
        for (var line : Files.readAllLines(Path.of(fileName))) {
            if (line.isBlank()) continue;
            var parts = line.trim().split("\\s+");
            if (parts.length != 3)
                throw new IOException("Expected 'k A B' in the tree file, found: " + line);
            merges.add(parts);
        }
        return merges;
    }

    /** A FastA record: the header line (without '>', first word only) and the sequence. */
    public record FastARecord(String header, String sequence) {}

    public static List<FastARecord> readFastA(String fileName) throws IOException {
        var records = new ArrayList<FastARecord>();
        String header = null;
        var sequence = new StringBuilder();
        for (var line : Files.readAllLines(Path.of(fileName))) {
            if (line.startsWith(">")) {
                if (header != null)
                    records.add(new FastARecord(header, sequence.toString()));
                header = line.substring(1).trim().split("\\s+")[0];
                sequence.setLength(0);
            } else {
                sequence.append(line.trim().toUpperCase());
            }
        }
        if (header != null)
            records.add(new FastARecord(header, sequence.toString()));
        return records;
    }
}
