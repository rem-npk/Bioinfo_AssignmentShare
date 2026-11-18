//Code written by Claude
import java.io.*;
import java.util.*;

public class SuffixTree {

    // An edge label is stored as a pair of positions into the text (1-based, inclusive).
    // Every node stores the label of the edge leading to it.
    static class Node {
        int start, end;
        int suffix = -1;                       // suffix number if leaf, -1 otherwise
        ArrayList<Node> children = new ArrayList<>();

        Node(int start, int end, int suffix) {
            this.start = start;
            this.end = end;
            this.suffix = suffix;
        }
    }

    static String t;              // text including the terminator $
    static int n;                 // length of t
    static int matched = 0;       // sum of all h
    static int internalNodes = 0;
    static int leaves = 0;

    // letter at position p (1-based)
    static char at(int p) {
        return t.charAt(p - 1);
    }

    // child of v whose edge label starts with letter c, or null
    static Node findChild(Node v, char c) {
        for (Node w : v.children) {
            if (at(w.start) == c) return w;
        }
        return null;
    }

    // insert suffix i (Algorithm 11.2)
    static void insert(Node root, int i) {
        Node v = root;
        int pos = i;                           // next letter of the suffix to match
        while (true) {
            Node child = findChild(v, at(pos));
            if (child == null) {
                // no edge starts with this letter: new leaf
                v.children.add(new Node(pos, n, i));
                return;
            }
            // walk down along the edge as long as the letters agree
            int k = child.start;
            while (k <= child.end && at(k) == at(pos)) {
                k++;
                pos++;
                matched++;
            }
            if (k > child.end) {
                // whole edge matched, continue at the child
                v = child;
            } else {
                // mismatch inside the edge: split it
                Node mid = new Node(child.start, k - 1, -1);
                child.start = k;
                v.children.set(v.children.indexOf(child), mid);
                mid.children.add(child);
                mid.children.add(new Node(pos, n, i));
                return;
            }
        }
    }

    // depth first output, $ first, then alphabetical ($ is smaller than letters in ASCII)
    static void print(Node v, int depth) {
        ArrayList<Node> kids = new ArrayList<>(v.children);
        Collections.sort(kids, new Comparator<Node>() {
            public int compare(Node a, Node b) {
                return at(a.start) - at(b.start);
            }
        });
        for (Node w : kids) {
            String indent = "";
            for (int d = 0; d < depth; d++) indent += "  ";
            String line = indent + "[" + w.start + ".." + w.end + "] "
                    + t.substring(w.start - 1, w.end);
            if (w.suffix >= 0) {
                line += " (" + w.suffix + ")";
                leaves++;
            } else {
                internalNodes++;
            }
            System.out.println(line);
            print(w, depth + 1);
        }
    }

    // argument is either a FastA file or the text itself
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
        if (args.length != 1) {
            System.out.println("usage: java SuffixTree <string or FastA file>");
            return;
        }
        t = readInput(args[0]) + "$";
        n = t.length();

        Node root = new Node(0, 0, -1);
        for (int i = 1; i <= n; i++) {
            insert(root, i);
        }

        System.out.println("T = " + t);
        internalNodes = 1;                     // the root
        print(root, 0);
        System.out.println("internal nodes: " + internalNodes + ", leaves: " + leaves
                + ", letters matched: " + matched);
    }
}
