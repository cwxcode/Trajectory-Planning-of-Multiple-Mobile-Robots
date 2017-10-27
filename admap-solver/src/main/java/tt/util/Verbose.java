package tt.util;

public class Verbose {

    private static boolean verbose;

    public static void print(String text) {
        if (verbose)
            System.out.print(text);
    }

    public static void println(String text) {
        if (verbose)
            System.out.println(text);
    }

    public static void printf(String text, Object... params) {
        if (verbose)
            System.out.printf(text, params);
    }

    public static boolean isVerbose() {
        return verbose;
    }

    public static void setVerbose(boolean verbose) {
        Verbose.verbose = verbose;
    }
}
