import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Align_RemiBecker_NinaRopertz.java -- global, local and overlap alignment with a linear gap penalty.
 * Sequence Bioinformatics, WS 2026/27, Assignment 1.
 * Author: Katharina (Remi) Becker and Nina Ropertz
 *
 * Usage: java Align_RemiBecker_NinaRopertz <file> <mode> <match> <mismatch> <gap>
 *   file      FastA file with exactly two sequences
 *   mode      global | local | overlap
 *   match     match score, positive
 *   mismatch  mismatch score, negative
 *   gap       linear gap penalty d, positive (subtracted for every position aligned to a gap)
 *
 * Fill in the three methods marked "todo". Everything else (input, output) is provided.
 */
public class Align_RemiBecker_NinaRopertz {

    private enum TracebackDirection {
        DIAG,
        UP,
        LEFT,
        END
    }

    /** An alignment: its score and the two rows, with '-' for gaps. */
    public record Alignment(int score, String xAligned, String yAligned) {}

    public static void main(String[] args) throws IOException {
        if (args.length != 5)
            throw new IOException("Usage: Align_RemiBecker_NinaRopertz <file> <mode> <match> <mismatch> <gap>");
        var file = args[0];
        var mode = args[1];
        var match = Integer.parseInt(args[2]);
        var mismatch = Integer.parseInt(args[3]);
        var gap = Integer.parseInt(args[4]);
        if (!mode.equals("global") && !mode.equals("local") && !mode.equals("overlap"))
            throw new IOException("Illegal mode " + mode + ": must be global, local or overlap");
        // done: todo: check that match and gap are positive and mismatch is negative; throw an exception if not
        if(match <= 0 || mismatch >= 0 || gap <= 0) {
            throw new IOException("Illegal match/mismatch/gap: " + match + "/" + mismatch + "/" + gap + ". Match/Gap must be positive and mismatch negative.");
        }
        var sequences = readFastA(file);
        if (sequences.size() != 2)
            throw new IOException("File must contain exactly two sequences, found " + sequences.size());
        var x = sequences.get(0).sequence();
        var y = sequences.get(1).sequence();

        System.out.println("Align_RemiBecker_NinaRopertz " + mode + " match=" + match + " mismatch=" + mismatch + " gap=" + gap);
        System.out.println("X: " + sequences.get(0).header() + " (" + x.length() + " letters)");
        System.out.println("Y: " + sequences.get(1).header() + " (" + y.length() + " letters)");

        var start = System.currentTimeMillis();
        Alignment alignment = switch (mode) {
            case "global" -> alignGlobal(x, y, match, mismatch, gap);
            case "local" -> alignLocal(x, y, match, mismatch, gap);
            default -> alignOverlap(x, y, match, mismatch, gap);
        };
        var millis = System.currentTimeMillis() - start;

        System.out.println("Score: " + alignment.score() + "   (" + millis + " ms)");
        var length = alignment.xAligned().length();
        for (var a = 0; a < length; a += 60) {
            var b = Math.min(length, a + 60);
            System.out.println(alignment.xAligned().substring(a, b));
            System.out.println(alignment.yAligned().substring(a, b));
            System.out.println();
        }
    }

    /** Global alignment (Needleman-Wunsch), Section 1.8 of the handout. */
    public static Alignment alignGlobal(String x, String y, int match, int mismatch, int d) {
        // done: todo: fill the matrix F with F(i,0) = -i*d and F(0,j) = -j*d, the recurrence of
        // Section 1.8.1, then trace back from (n,m) to (0,0) as in Section 1.8.2
        int xLength = x.length();
        int yLength = y.length();
        int[][] globalMatrix = new int[xLength + 1][yLength + 1];
        TracebackDirection[][] tracebackMatrix = new TracebackDirection[xLength+1][yLength+1];
        //initializing Matrix with i/j * gap value and tracebackMatrix to ensure ending at (0,0)
        for (int i = 0; i <= xLength; i++) {
            globalMatrix[i][0] = -i*d;
            tracebackMatrix[i][0] = TracebackDirection.UP;
        }
        for  (int j = 0; j <= yLength; j++) {
            globalMatrix[0][j] = -j*d;
            tracebackMatrix[0][j] = TracebackDirection.LEFT;
        }
        tracebackMatrix[0][0] = TracebackDirection.END;


        //filling Matrix
        for (int i = 1; i <= xLength; i++) {
            for (int j = 1; j <= yLength; j++) {
                int diag = globalMatrix[i-1][j-1];
                int left = globalMatrix[i][j-1];
                int up = globalMatrix[i-1][j];
                char charX = x.charAt(i-1);
                char charY = y.charAt(j-1);
                // calculate possible scores
                int matchCost = diag + ((charX == charY)?match:mismatch);  // match or mismatch
                int deletionCost = up - d;
                int insertionCost = left - d;
                int maxScore = Math.max(matchCost, Math.max(deletionCost, insertionCost));
                // fill matrix position
                globalMatrix[i][j] = maxScore;
                // fill traceback
                if (maxScore == deletionCost) {
                    tracebackMatrix[i][j] = TracebackDirection.UP;
                }
                if (maxScore == insertionCost) {
                    tracebackMatrix[i][j] = TracebackDirection.LEFT;
                }
                if (maxScore == matchCost) {
                    tracebackMatrix[i][j] = TracebackDirection.DIAG;
                }
            }
        }
        StringBuilder alignedX = new StringBuilder();
        StringBuilder alignedY = new StringBuilder();
        // traceback start at (n,m) in global alignment
        int posX = xLength;
        int posY = yLength;
        while (tracebackMatrix[posX][posY] != TracebackDirection.END) { //traceback ends at position (0,0)
            switch (tracebackMatrix[posX][posY]) {
                case DIAG:
                    alignedX.append(x.charAt(posX-1));
                    alignedY.append(y.charAt(posY-1));
                    posX--;
                    posY--;
                    break;
                case LEFT:
                    alignedX.append("-");
                    alignedY.append(y.charAt(posY-1));
                    posY--;
                    break;
                case UP:
                    alignedX.append(x.charAt(posX-1));
                    alignedY.append("-");
                    posX--;
                    break;
            }
        }
        String finX = String.valueOf(alignedX.reverse());
        String finY = String.valueOf(alignedY.reverse());
        return new Alignment(globalMatrix[xLength][yLength], finX, finY);
    }

    /** Local alignment (Smith-Waterman), Section 1.9 of the handout. */
    public static Alignment alignLocal(String x, String y, int match, int mismatch, int d) {
        // done: todo: zero boundary, the recurrence with the extra 0 option, start the traceback
        // at the largest entry and stop at the first 0
        int xLength = x.length();
        int yLength = y.length();
        int[][] localMatrix = new int[xLength + 1][yLength + 1];
        TracebackDirection[][] tracebackMatrix = new TracebackDirection[xLength+1][yLength+1];
        //initializing Matrix with 0 and Traceback with END
        for (int i = 0; i <= xLength; i++) {
            localMatrix[i][0] = 0;
            tracebackMatrix[i][0] = TracebackDirection.END;
        }
        for  (int j = 0; j <= yLength; j++) {
            localMatrix[0][j] = 0;
            tracebackMatrix[0][j] = TracebackDirection.END;
        }

        //filling Matrix
        for (int i = 1; i <= xLength; i++) {
            for (int j = 1; j <= yLength; j++) {
                int diag = localMatrix[i-1][j-1];
                int left = localMatrix[i][j-1];
                int up = localMatrix[i-1][j];
                char charX = x.charAt(i-1);
                char charY = y.charAt(j-1);
                // calculate possible scores
                int matchCost = diag + ((charX == charY)?match:mismatch);  // match or mismatch
                int deletionCost = up - d;
                int insertionCost = left - d;
                int maxScore = Math.max(0, Math.max(matchCost, Math.max(deletionCost, insertionCost)));
                // fill matrix position
                localMatrix[i][j] = maxScore;
                // fill traceback
                if (maxScore == 0) {
                    tracebackMatrix[i][j] = TracebackDirection.END;
                }
                if (maxScore == deletionCost) {
                    tracebackMatrix[i][j] = TracebackDirection.UP;
                }
                if (maxScore == insertionCost) {
                    tracebackMatrix[i][j] = TracebackDirection.LEFT;
                }
                if (maxScore == matchCost) {
                    tracebackMatrix[i][j] = TracebackDirection.DIAG;
                }
            }
        }
        StringBuilder alignedX = new StringBuilder();
        StringBuilder alignedY = new StringBuilder();
        // find max entry in localMatrix to set staring point of local alignment traceback
        int maxX = 0;
        int maxY = 0;
        int currentMax = 0;
        for(int i = 1; i <= xLength; i++) {
            for(int j = 1; j <= yLength; j++) {
                if(localMatrix[i][j] >= currentMax) {
                    maxX = i;
                    maxY = j;
                    currentMax = localMatrix[i][j];
                }
            }
        }
        while (tracebackMatrix[maxX][maxY] != TracebackDirection.END) {  //traceback ends at entry END meaning at first matrix entry value 0
            switch (tracebackMatrix[maxX][maxY]) {
                case DIAG:
                    alignedX.append(x.charAt(maxX-1));
                    alignedY.append(y.charAt(maxY-1));
                    maxX--;
                    maxY--;
                    break;
                case LEFT:
                    alignedX.append("-");
                    alignedY.append(y.charAt(maxY-1));
                    maxY--;
                    break;
                case UP:
                    alignedX.append(x.charAt(maxX-1));
                    alignedY.append("-");
                    maxX--;
                    break;
            }
        }
        String finX = String.valueOf(alignedX.reverse());
        String finY = String.valueOf(alignedY.reverse());
        return new Alignment(currentMax, finX, finY);
    }

    /** Overlap alignment, Section 1.10 of the handout. */
    public static Alignment alignOverlap(String x, String y, int match, int mismatch, int d) {
        // todo: zero boundary, the Needleman-Wunsch recurrence, start the traceback at the
        // best entry of the last row or last column and stop at the top or left edge
        int xLength = x.length();
        int yLength = y.length();
        int[][] globalMatrix = new int[xLength + 1][yLength + 1];
        TracebackDirection[][] tracebackMatrix = new TracebackDirection[xLength+1][yLength+1];
        //initializing Matrix and Traceback with 0 and END
        for (int i = 0; i <= xLength; i++) {
            globalMatrix[i][0] = 0;
            tracebackMatrix[i][0] = TracebackDirection.END;
        }
        for  (int j = 0; j <= yLength; j++) {
            globalMatrix[0][j] = 0;
            tracebackMatrix[0][j] = TracebackDirection.END;
        }

        //filling Matrix
        for (int i = 1; i <= xLength; i++) {
            for (int j = 1; j <= yLength; j++) {
                int diag = globalMatrix[i-1][j-1];
                int left = globalMatrix[i][j-1];
                int up = globalMatrix[i-1][j];
                char charX = x.charAt(i-1);
                char charY = y.charAt(j-1);
                // calculate possible scores
                int matchCost = diag + ((charX == charY)?match:mismatch);  // match or mismatch
                int deletionCost = up - d;
                int insertionCost = left - d;
                int maxScore = Math.max(matchCost, Math.max(deletionCost, insertionCost));
                // fill matrix position
                globalMatrix[i][j] = maxScore;
                // fill traceback
                if (maxScore == deletionCost) {
                    tracebackMatrix[i][j] = TracebackDirection.UP;
                }
                if (maxScore == insertionCost) {
                    tracebackMatrix[i][j] = TracebackDirection.LEFT;
                }
                if (maxScore == matchCost) {
                    tracebackMatrix[i][j] = TracebackDirection.DIAG;
                }
            }
        }
        StringBuilder alignedX = new StringBuilder();
        StringBuilder alignedY = new StringBuilder();
        // finding starting position of traceback as maximum of bottom or right edge of matrix
        int posX = 0;
        int posY = 0;
        int currentMax = 0;
        for(int i = 1; i <= xLength; i++) {
            if(globalMatrix[i][yLength] >= currentMax) {
                posX = i;
                posY = yLength;
                currentMax = globalMatrix[i][yLength];
            }
        }
        for(int j = 1; j <= yLength; j++) {
            if(globalMatrix[xLength][j] >= currentMax) {
                posX = xLength;
                posY = j;
                currentMax = globalMatrix[xLength][j];
            }
        }
        while (tracebackMatrix[posX][posY] != TracebackDirection.END) { // traceback ends at left or top edge of matrix as traceback matrix was initialized with END
            switch (tracebackMatrix[posX][posY]) {
                case DIAG:
                    alignedX.append(x.charAt(posX-1));
                    alignedY.append(y.charAt(posY-1));
                    posX--;
                    posY--;
                    break;
                case LEFT:
                    alignedX.append("-");
                    alignedY.append(y.charAt(posY-1));
                    posY--;
                    break;
                case UP:
                    alignedX.append(x.charAt(posX-1));
                    alignedY.append("-");
                    posX--;
                    break;
            }
        }
        String finX = String.valueOf(alignedX.reverse());
        String finY = String.valueOf(alignedY.reverse());
        return new Alignment(currentMax, finX, finY);
    }

    /** A FastA record: the header line (without '>') and the sequence (no line breaks). */
    public record FastARecord(String header, String sequence) {}

    /** Reads all records of a FastA file. */
    public static List<FastARecord> readFastA(String fileName) throws IOException {
        var records = new ArrayList<FastARecord>();
        String header = null;
        var sequence = new StringBuilder();
        for (var line : Files.readAllLines(Path.of(fileName))) {
            if (line.startsWith(">")) {
                if (header != null)
                    records.add(new FastARecord(header, sequence.toString()));
                header = line.substring(1).trim();
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
