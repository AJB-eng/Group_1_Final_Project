package model;

/**
 * Pure from-scratch utility methods for primitive array manipulation, parsing, and formatting.
 * Implemented using manual loops and primitive arrays only.
 */
public final class ArrayUtils {

    private static long lcgSeed = System.nanoTime() ^ 0x5DEECE66DL;

    private ArrayUtils() {
    }

    /**
     * Deep-copies a 1D int array using a manual loop.
     */
    public static int[] copyArray(int[] source) {
        if (source == null) {
            return new int[0];
        }
        int[] copy = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            copy[i] = source[i];
        }
        return copy;
    }

    /**
     * Deep-copies a 2D int array using manual nested loops.
     */
    public static int[][] copy2DArray(int[][] source) {
        if (source == null) {
            return null;
        }
        int[][] copy = new int[source.length][];
        for (int i = 0; i < source.length; i++) {
            copy[i] = copyArray(source[i]);
        }
        return copy;
    }

    /**
     * Creates a range array [start, start + 1, ..., endInclusive].
     */
    public static int[] rangeIndices(int start, int endInclusive) {
        if (endInclusive < start) {
            return new int[0];
        }
        int len = endInclusive - start + 1;
        int[] result = new int[len];
        for (int i = 0; i < len; i++) {
            result[i] = start + i;
        }
        return result;
    }

    /**
     * Checks whether a target value is present in an array using a manual linear scan.
     */
    public static boolean contains(int[] arr, int target) {
        if (arr == null) {
            return false;
        }
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == target) {
                return true;
            }
        }
        return false;
    }

    /**
     * Formats an int[] into a bracketed string "[a, b, c]" using a manual loop.
     */
    public static String arrayToString(int[] arr) {
        if (arr == null) {
            return "[]";
        }
        if (arr.length == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Formats an int[] into a comma-separated string "a, b, c" for input fields.
     */
    public static String arrayToCsv(int[] arr) {
        if (arr == null || arr.length == 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

    /**
     * Formats an int[] as a linked chain "a -> b -> c -> NULL".
     */
    public static String arrayToLinkedChain(int[] arr) {
        if (arr == null || arr.length == 0) {
            return "HEAD -> NULL (Empty List)";
        }
        StringBuilder sb = new StringBuilder("HEAD -> ");
        for (int i = 0; i < arr.length; i++) {
            sb.append("[").append(arr[i]).append("] -> ");
        }
        sb.append("NULL");
        return sb.toString();
    }

    /**
     * Parses comma, whitespace, semicolon, or newline separated integers from raw text
     * using a manually resized primitive int[] buffer.
     */
    public static int[] parseIntArray(String rawText) {
        if (rawText == null || rawText.trim().isEmpty()) {
            return new int[0];
        }
        int capacity = 16;
        int[] temp = new int[capacity];
        int count = 0;

        int n = rawText.length();
        int i = 0;
        while (i < n) {
            char c = rawText.charAt(i);
            // Skip delimiters
            if (c == ',' || c == ';' || c == ' ' || c == '\t' || c == '\n' || c == '\r' || c == '[' || c == ']') {
                i++;
                continue;
            }
            // Parse token
            int start = i;
            if (c == '-' || c == '+') {
                i++;
            }
            boolean hasDigits = false;
            while (i < n) {
                char d = rawText.charAt(i);
                if (d >= '0' && d <= '9') {
                    hasDigits = true;
                    i++;
                } else {
                    break;
                }
            }
            if (hasDigits) {
                String token = rawText.substring(start, i);
                try {
                    int val = Integer.parseInt(token);
                    if (count >= capacity) {
                        capacity *= 2;
                        int[] next = new int[capacity];
                        for (int k = 0; k < count; k++) {
                            next[k] = temp[k];
                        }
                        temp = next;
                    }
                    temp[count++] = val;
                } catch (NumberFormatException ignored) {
                    // Ignore malformed numbers out of 32-bit range
                }
            } else {
                // Skip non-numeric character
                i++;
            }
        }

        int[] result = new int[count];
        for (int k = 0; k < count; k++) {
            result[k] = temp[k];
        }
        return result;
    }

    /**
     * Generates an array of pseudo-random integers in [minVal, maxVal] using a custom
     * Linear Congruential Generator (LCG) implemented from scratch.
     */
    public static int[] generateRandomArray(int count, int minVal, int maxVal) {
        if (count <= 0) {
            return new int[0];
        }
        int[] result = new int[count];
        int range = (maxVal - minVal) + 1;
        if (range <= 0) {
            range = 90;
        }
        lcgSeed ^= System.nanoTime();
        for (int i = 0; i < count; i++) {
            lcgSeed = (lcgSeed * 6364136223846793005L + 1442695040888963407L);
            long positive = lcgSeed & 0x7FFFFFFFFFFFFFFFL;
            result[i] = minVal + (int) (positive % range);
        }
        return result;
    }
}
