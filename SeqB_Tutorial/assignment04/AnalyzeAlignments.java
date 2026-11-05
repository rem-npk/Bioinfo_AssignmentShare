import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.util.stream.*;

/**
 * Auswertung der MAFFT-Alignments (Aufgabe 2b und 2c).
 * Aufruf: java AnalyzeAlignments [Ordner mit *.aln.fasta]   (Standard: "aln")
 * Schreibt table.tsv und gibt die Zusammenfassung auf der Konsole aus.
 */
public class AnalyzeAlignments {

    static Map<String, String> read(Path p) throws IOException {
        Map<String, StringBuilder> tmp = new LinkedHashMap<>();
        StringBuilder cur = null;
        byte[] bytes = Files.readAllBytes(p);
        java.nio.charset.Charset cs = StandardCharsets.UTF_8;
        if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFF && (bytes[1] & 0xFF) == 0xFE) {
            cs = StandardCharsets.UTF_16LE;
        } else if (bytes.length >= 2 && (bytes[0] & 0xFF) == 0xFE && (bytes[1] & 0xFF) == 0xFF) {
            cs = StandardCharsets.UTF_16BE;
        }
        String text = new String(bytes, cs).replace("\uFEFF", "");
        for (String line : text.split("\\R")) {
            line = line.trim();
            if (line.startsWith(">")) {
                cur = new StringBuilder();
                tmp.put(line.substring(1), cur);
            } else if (cur != null) {
                cur.append(line);
            }
        }
        Map<String, String> res = new LinkedHashMap<>();
        for (Map.Entry<String, StringBuilder> e : tmp.entrySet()) {
            res.put(e.getKey(), e.getValue().toString().toUpperCase());
        }
        return res;
    }

    /** Laengen aller inneren Gaps (ohne Gaps am Anfang/Ende einer Sequenz) -> Anzahl. */
    static TreeMap<Integer, Integer> gapRuns(Collection<String> seqs) {
        TreeMap<Integer, Integer> counts = new TreeMap<>();
        Pattern gap = Pattern.compile("-+");
        for (String s : seqs) {
            String inner = s.replaceAll("^-+|-+$", "");
            Matcher m = gap.matcher(inner);
            while (m.find()) {
                counts.merge(m.end() - m.start(), 1, Integer::sum);
            }
        }
        return counts;
    }

    public static void main(String[] args) throws IOException {
        Path dir = Paths.get(args.length > 0 ? args[0] : "aln");

        List<Path> files;
        try (Stream<Path> st = Files.list(dir)) {
            files = st.filter(p -> p.getFileName().toString().endsWith(".aln.fasta"))
                      .sorted()
                      .collect(Collectors.toList());
        }
        if (files.isEmpty()) {
            System.err.println("Keine *.aln.fasta in " + dir.toAbsolutePath()
                    + " gefunden. Working directory pruefen!");
            return;
        }

        List<String[]> rows = new ArrayList<>();   // gene, nSeq, length, gapCols
        Map<String, TreeMap<Integer, Integer>> runs = new HashMap<>();
        int noGap = 0;
        String shortest = null, longest = null;
        int minLen = Integer.MAX_VALUE, maxLen = -1;

        for (Path f : files) {
            String gene = f.getFileName().toString().replace(".aln.fasta", "");
            Map<String, String> seqs = read(f);
            if (seqs.isEmpty()) {
                System.err.println("WARNUNG: keine Sequenzen in " + f.getFileName()
                        + " (Datei leer oder falsche Kodierung, z.B. UTF-16) - uebersprungen");
                continue;
            }
            int n = seqs.size();
            int len = seqs.values().iterator().next().length();

            int gapCols = 0;
            for (int i = 0; i < len; i++) {
                for (String s : seqs.values()) {
                    if (s.charAt(i) == '-') {
                        gapCols++;
                        break;
                    }
                }
            }
            if (gapCols == 0) noGap++;
            if (len < minLen) { minLen = len; shortest = gene; }
            if (len > maxLen) { maxLen = len; longest = gene; }

            rows.add(new String[]{gene, String.valueOf(n), String.valueOf(len), String.valueOf(gapCols)});
            runs.put(gene, gapRuns(seqs.values()));
        }

        try (PrintWriter out = new PrintWriter("table.tsv", "UTF-8")) {
            out.println("gene\tn_seq\taln_length\tgap_columns");
            for (String[] r : rows) {
                out.println(String.join("\t", r));
            }
        }

        System.out.println("Anzahl Gene: " + rows.size());
        System.out.println("Kuerzestes Alignment: " + shortest + " (" + minLen + ")");
        System.out.println("Laengstes Alignment:  " + longest + " (" + maxLen + ")");
        System.out.println("Gene ohne Gap: " + noGap);

        for (String g : new String[]{"rbcL", "matK"}) {
            System.out.println("\n" + g + " Gap-Laengen (Laenge=Anzahl): " + runs.get(g));
        }

        // ycf1: welche Sequenzen sind auffaellig?
        System.out.println("\nycf1 (Sequenz | Laenge ohne Gaps | Gap-Zeichen)");
        Map<String, String> ycf1 = read(dir.resolve("ycf1.aln.fasta"));
        for (Map.Entry<String, String> e : ycf1.entrySet()) {
            String s = e.getValue();
            int gaps = 0;
            for (int i = 0; i < s.length(); i++) if (s.charAt(i) == '-') gaps++;
            String name = e.getKey().length() > 50 ? e.getKey().substring(0, 50) : e.getKey();
            System.out.println(name + " | " + (s.length() - gaps) + " | " + gaps);
        }
    }
}
