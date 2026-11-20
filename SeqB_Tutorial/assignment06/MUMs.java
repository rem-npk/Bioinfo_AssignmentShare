//Written by Claude

import java.io.*;
import java.util.*;

public class MUMs {

    // Suffix tree node: label of the edge leading to it as a pair of positions (1-based)
    static class Node {
        int start, end;
        int suffix;                            // suffix number if leaf, -1 otherwise
        ArrayList<Node> children = new ArrayList<>();

        Node(int start, int end, int suffix) {
            this.start = start;
            this.end = end;
            this.suffix = suffix;
        }
    }

    static String x;          // X = S#T$
    static int n;             // length of X
    static int lenS;          // length of S
    static int minLen;        // L
    static ArrayList<int[]> mums = new ArrayList<>();   // {pos in S, pos in T, length}

    static char at(int p) {
        return x.charAt(p - 1);
    }

    static Node findChild(Node v, char c) {
        for (Node w : v.children) {
            if (at(w.start) == c) return w;
        }
        return null;
    }

    // naive insertion of suffix i (Algorithm 11.2)
    static void insert(Node root, int i) {
        Node v = root;
        int pos = i;
        while (true) {
            Node child = findChild(v, at(pos));
            if (child == null) {
                v.children.add(new Node(pos, n, i));
                return;
            }
            int k = child.start;
            while (k <= child.end && at(k) == at(pos)) {
                k++;
                pos++;
            }
            if (k > child.end) {
                v = child;
            } else {
                Node mid = new Node(child.start, k - 1, -1);
                child.start = k;
                v.children.set(v.children.indexOf(child), mid);
                mid.children.add(child);
                mid.children.add(new Node(pos, n, i));
                return;
            }
        }
    }

    // Returns {number of leaves from S, number of leaves from T, one suffix from S, one suffix from T}
    // for the subtree of v. depth = string depth of v.
    static int[] visit(Node v, int depth) {
        if (v.suffix >= 0) {
            if (v.suffix <= lenS) return new int[]{1, 0, v.suffix, 0};
            if (v.suffix > lenS + 1) return new int[]{0, 1, 0, v.suffix};
            return new int[]{0, 0, 0, 0};      // the suffix starting at # (belongs to neither)
        }
        int[] r = new int[]{0, 0, 0, 0};
        for (Node w : v.children) {
            int[] c = visit(w, depth + (w.end - w.start + 1));
            r[0] += c[0];
            r[1] += c[1];
            if (c[0] > 0) r[2] = c[2];
            if (c[1] > 0) r[3] = c[3];
        }
        // exactly one leaf from S and one from T below v: candidate for a MUM
        if (depth >= minLen && r[0] == 1 && r[1] == 1) {
            int i = r[2];
            int j = r[3];
            // left maximal: letters before the two occurrences differ (or one is at the start)
            if (i == 1 || at(i - 1) != at(j - 1)) {
                mums.add(new int[]{i, j - lenS - 1, depth});
            }
        }
        return r;
    }

    // argument is either a FastA file or the sequence itself
    static String readInput(String arg) throws IOException {
        File f = new File(arg);
        if (!f.isFile()) return arg;
        StringBuilder sb = new StringBuilder();
        BufferedReader in = new BufferedReader(new FileReader(f));
        String line;
        while ((line = in.readLine()) != null) {
            if (line.startsWith(">")) continue;
            sb.append(line.trim());
        }
        in.close();
        return sb.toString();
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 3) {
            System.out.println("usage: java MUMs <S> <T> <L>");
            return;
        }
        String s = readInput(args[0]);
        String t = readInput(args[1]);
        minLen = Integer.parseInt(args[2]);

        lenS = s.length();
        x = s + "#" + t + "$";
        n = x.length();

        Node root = new Node(0, 0, -1);
        for (int i = 1; i <= n; i++) {
            insert(root, i);
        }

        visit(root, 0);

        // sort by position in S
        Collections.sort(mums, new Comparator<int[]>() {
            public int compare(int[] a, int[] b) {
                return a[0] - b[0];
            }
        });

        for (int[] m : mums) {
            System.out.println(m[0] + " " + m[1] + " " + m[2] + " "
                    + s.substring(m[0] - 1, m[0] - 1 + m[2]));
        }
        System.out.println("MUMs: " + mums.size());
    }
}