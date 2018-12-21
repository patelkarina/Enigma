package enigma;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;

/** The suite of all JUnit tests for the Permutation class.
 *  @author
 */
public class PermutationTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private Permutation perm;
    private String alpha = UPPER_STRING;

    /** Check that perm has an alphabet whose size is that of
     *  FROMALPHA and TOALPHA and that maps each character of
     *  FROMALPHA to the corresponding character of FROMALPHA, and
     *  vice-versa. TESTID is used in error messages. */
    private void checkPerm(String testId,
                           String fromAlpha, String toAlpha) {
        int N = fromAlpha.length();
        assertEquals(testId + " (wrong length)", N, perm.size());
        for (int i = 0; i < N; i += 1) {
            char c = fromAlpha.charAt(i), e = toAlpha.charAt(i);
            assertEquals(msg(testId, "wrong translation of '%c'", c),
                         e, perm.permute(c));
            assertEquals(msg(testId, "wrong inverse of '%c'", e),
                         c, perm.invert(e));
            int ci = alpha.indexOf(c), ei = alpha.indexOf(e);
            assertEquals(msg(testId, "wrong translation of %d", ci),
                         ei, perm.permute(ci));
            assertEquals(msg(testId, "wrong inverse of %d", ei),
                         ci, perm.invert(ei));
        }
    }

    /* ***** TESTS ***** */

    @Test
    public void checkIdTransform() {
        perm = new Permutation("", UPPER);
        checkPerm("identity", UPPER_STRING, UPPER_STRING);
    }
    @Test
    public void testPerm() {
        Permutation p = new Permutation(NAVALA.get("I"), UPPER);
        String a1 = "A";
        String e2 = "E";
        char a = a1.charAt(0);
        char e = e2.charAt(0);
        assertEquals(e, p.permute(a));
        char n = "N".charAt(0);
        char w = "W".charAt(0);
        assertEquals(w, p.permute(n));
        assertEquals(14, p.permute(12));
        assertEquals(25, p.permute(9));

    }

    @Test
        public void testInvertChar() {
        Permutation p2 = new Permutation("(PNH) "
                + "(ABDFIKLZYXW) (JC)", new CharacterRange('A', 'Z'));
        assertEquals(p2.invert('B'), 'A');
        assertEquals(p2.invert('G'), 'G');
    }

    @Test
    public void testPermuteChar() {
        Permutation p3 = new Permutation("(PNH) "
                + "(ABDFIKLZYXW) (JC)", new CharacterRange('A', 'Z'));
        assertEquals(p3.permute('B'), 'D');
        assertEquals(p3.permute('G'), 'G');
    }

}
