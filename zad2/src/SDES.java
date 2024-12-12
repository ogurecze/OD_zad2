import java.util.Arrays;

public class SDES {

    private static final int[] IP = {1, 5, 2, 0, 3, 7, 4, 6};
    private static final int[] IP_INV = {3, 0, 2, 4, 6, 1, 7, 5};
    private static final int[] P10 = {2, 4, 1, 6, 3, 9, 0, 8, 7, 5};
    private static final int[] P8 = {5, 2, 6, 3, 7, 4, 9, 8};
    private static final int[] EP = {3, 0, 1, 2, 1, 2, 3, 0};
    private static final int[] P4 = {1, 3, 2, 0};

    private static final int[][] S0 = {
            {1, 0, 3, 2},
            {3, 2, 1, 0},
            {0, 2, 1, 3},
            {3, 1, 3, 2}
    };
    private static final int[][] S1 = {
            {0, 1, 2, 3},
            {2, 0, 1, 3},
            {3, 0, 1, 0},
            {2, 1, 0, 3}
    };

    public static void main(String[] args) {
        String plaintext = "00001111";
        String key = "1111100000";

        String[] keys = generateKeys(key);
        String ciphertext = encrypt(plaintext, keys);
        String decryptedText = decrypt(ciphertext, keys);

        System.out.println("Tekst jawny:      " + plaintext);
        System.out.println("Tekst zaszyfrowany:     " + ciphertext);
        System.out.println("Tekst odszyfrowany: " + decryptedText);
    }

    public static String[] generateKeys(String key) {
        String permutedKey = permute(key, P10);
        String[] splitKeys = split(permutedKey);

        splitKeys[0] = leftShift(splitKeys[0], 1);
        splitKeys[1] = leftShift(splitKeys[1], 1);
        String k1 = permute(splitKeys[0] + splitKeys[1], P8);

        splitKeys[0] = leftShift(splitKeys[0], 2);
        splitKeys[1] = leftShift(splitKeys[1], 2);
        String k2 = permute(splitKeys[0] + splitKeys[1], P8);

        return new String[]{k1, k2};
    }

    public static String encrypt(String plaintext, String[] keys) {
        String permutedText = permute(plaintext, IP);
        String[] splitText = split(permutedText);

        String result = fk(splitText, keys[0]);
        result = swap(result);
        result = fk(split(result), keys[1]);

        return permute(result, IP_INV);
    }

    public static String decrypt(String ciphertext, String[] keys) {
        String permutedText = permute(ciphertext, IP);
        String[] splitText = split(permutedText);

        String result = fk(splitText, keys[1]);
        result = swap(result);
        result = fk(split(result), keys[0]);

        return permute(result, IP_INV);
    }

    private static String fk(String[] splitText, String key) {
        String expandedRight = permute(splitText[1], EP);
        String xorResult = xor(expandedRight, key);
        String substituted = substitute(xorResult);
        String permuted = permute(substituted, P4);

        return xor(splitText[0], permuted) + splitText[1];
    }

    private static String substitute(String input) {
        String left = input.substring(0, 4);
        String right = input.substring(4);

        String substitutedLeft = sBox(left, S0);
        String substitutedRight = sBox(right, S1);

        return substitutedLeft + substitutedRight;
    }

    private static String sBox(String input, int[][] sBox) {
        int row = Integer.parseInt(input.charAt(0) + "" + input.charAt(3), 2);
        int col = Integer.parseInt(input.charAt(1) + "" + input.charAt(2), 2);
        int value = sBox[row][col];

        return String.format("%2s", Integer.toBinaryString(value)).replace(' ', '0');
    }

    private static String permute(String input, int[] permutation) {
        StringBuilder result = new StringBuilder();
        for (int index : permutation) {
            result.append(input.charAt(index));
        }
        return result.toString();
    }

    private static String[] split(String input) {
        int mid = input.length() / 2;
        return new String[]{input.substring(0, mid), input.substring(mid)};
    }

    private static String leftShift(String input, int shifts) {
        return input.substring(shifts) + input.substring(0, shifts);
    }

    private static String xor(String input1, String input2) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < input1.length(); i++) {
            result.append(input1.charAt(i) ^ input2.charAt(i));
        }
        return result.toString();
    }

    private static String swap(String input) {
        int mid = input.length() / 2;
        return input.substring(mid) + input.substring(0, mid);
    }
}
