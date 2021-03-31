import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class UtilTest {
    @Test
    void getNumbersAfterLast4() {
        Assertions.assertArrayEquals(new int[]{1, 7}, Util.getNumbersAfterLast4(new int[]{1, 2, 4, 4, 2, 3, 4, 1, 7}));
        Assertions.assertArrayEquals(new int[]{}, Util.getNumbersAfterLast4(new int[]{4}));
        Assertions.assertArrayEquals(new int[]{2, 1, 7}, Util.getNumbersAfterLast4(new int[]{4, 2, 1, 7}));
    }

    @Test
    void getNumbersAfterLast4Exception() {
        Assertions.assertThrows(RuntimeException.class, () -> Util.getNumbersAfterLast4(new int[]{1}));
    }

    @Test
    void has1And4Only() {
        Assertions.assertTrue(Util.has1And4Only(new int[]{1, 1, 1, 4, 4, 1, 4, 4}));
        Assertions.assertFalse(Util.has1And4Only(new int[]{1, 1, 1}));
        Assertions.assertFalse(Util.has1And4Only(new int[]{1, 1, 1, 4, 4, 7, 4, 4}));
        Assertions.assertTrue(Util.has1And4Only(new int[]{1, 4}));
        Assertions.assertFalse(Util.has1And4Only(new int[]{}));
    }
}