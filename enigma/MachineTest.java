package enigma;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;

import static enigma.TestUtils.*;


/** The suite of all JUnit tests for the Machine class.
 *  @author Karina Patel
 */
public class MachineTest {

    /** Testing time limit. */
    @Rule
    public Timeout globalTimeout = Timeout.seconds(5);

    /* ***** TESTING UTILITIES ***** */

    private ArrayList<Rotor> testRotors = R;
    private Machine machine;
    private String [] rots = {"B", "BETA", "III", "IV", "I"};

    private void setUPM(Alphabet a, int numR, int numP,
                   Collection<Rotor> allRotors) {
        machine = new Machine(a, numR, numP, allRotors);
    }
    @Test
    public void testInsertRotors() {
        setUPM(UPPER, 5, 3, testRotors);
        machine.insertRotors(rots);
        assertEquals("B",  machine.getRotors()[0].name());
        assertEquals("BETA", machine.getRotors()[1].name());
        assertEquals("I", machine.getRotors()[4].name());
        assertEquals("IV", machine.getRotors()[3].name());
        assertEquals("III", machine.getRotors()[2].name());

    }

    @Test
    public void testSetRotors() {
        setUPM(UPPER, 5, 3, testRotors);
        machine.insertRotors(rots);
        machine.setRotors("AXLE");
        assertEquals(0, machine.getRotors()[1].setting());
        assertEquals(23, machine.getRotors()[2].setting());
        assertEquals(11, machine.getRotors()[3].setting());
        assertEquals(4, machine.getRotors()[4].setting());


    }

    @Test
    public void testConvert() {
        setUPM(UPPER, 5, 3, testRotors);
        machine.insertRotors(rots);
        machine.setRotors("AXLE");
        Permutation p2 = new Permutation("(HQ) (EX) (IP) (TR) "
                + "(BY)", UPPER);
        machine.setPlugboard(p2);
        assertEquals(3, machine.convert(4));
        assertEquals(24, machine.convert(8));

    }


    @Test
    public void testDoubleStep() {
        Alphabet ac = new CharacterRange('A', 'D');
        Rotor one = new Reflector("R1", new Permutation("(AC) (BD)", ac));
        Rotor two = new MovingRotor("R2", new Permutation("(ABCD)", ac),
                "C");
        Rotor three = new MovingRotor("R3", new Permutation("(ABCD)", ac),
                "C");
        Rotor four = new MovingRotor("R4", new Permutation("(ABCD)", ac),
                "C");
        String setting = "AAA";
        Rotor[] machineRotors = {one, two, three, four};
        String[] rotors = {"R1", "R2", "R3", "R4"};
        Machine mach = new Machine(ac, 4, 3,
                new ArrayList<>(Arrays.asList(machineRotors)));
        mach.insertRotors(rotors);
        mach.setRotors(setting);
        assertEquals("AAAA", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AAAB", getSetting(ac, machineRotors));
        mach.convert('a');
        assertEquals("AAAC", getSetting(ac, machineRotors));


    }

    /** Helper method to get the String representation
     * of the current Rotor settings */
    private String getSetting(Alphabet alph, Rotor[] machineRotors) {
        String currSetting = "";
        for (Rotor r : machineRotors) {
            currSetting += alph.toChar(r.setting());
        }
        return currSetting;
    }


}
