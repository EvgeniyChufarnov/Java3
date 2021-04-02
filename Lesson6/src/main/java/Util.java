public class Util {
    public static int[] getNumbersAfterLast4(int[] inputArray) {
        int positionOf4 = -1;

        for (int i = inputArray.length - 1; i >= 0; i--) {
            if (inputArray[i] == 4) {
                positionOf4 = i;
                break;
            }
        }

        if (positionOf4 == -1)
            throw new RuntimeException("Input array in getNumbersAfterLast4 has no 4 in it");

        int[] result = new int[(inputArray.length - 1) - positionOf4];

        for (int i = 0; i < result.length; i++) {
            result[i] = inputArray[++positionOf4];
        }

        return result;
    }

    public static boolean has1And4Only(int[] inputArray) {
        boolean has1 = false;
        boolean has4 = false;

        for (int i = 0; i < inputArray.length; i++) {
            if (inputArray[i] == 1) {
                has1 = true;
            } else if (inputArray[i] == 4) {
                has4 = true;
            } else {
                return false;
            }
        }

        return has1 && has4;
    }
}
