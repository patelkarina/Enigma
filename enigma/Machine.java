package enigma;

import java.util.ArrayList;
import java.util.Collection;

import static enigma.EnigmaException.*;

/** Class that represents a complete enigma machine.
 *  @author Karina Patel
 */
class Machine {

    /**
     * A new Enigma machine with alphabet ALPHA, 1 < NUMROTORS rotor slots,
     * and 0 <= PAWLS < NUMROTORS pawls.  ALLROTORS contains all the
     * available rotors.
     */
    Machine(Alphabet alpha, int numRotors, int pawls,
            Collection<Rotor> allRotors) {
        _alphabet = alpha;
        _numRotors = numRotors;
        _pawls = pawls;
        _allRotors = allRotors;
        _plugboard = new Permutation("", _alphabet);
        _rotors = new Rotor[_numRotors];

    }

    /**
     * Return the number of rotor slots I have.
     */
    int numRotors() {

        return _numRotors;
    }

    /**
     * Return the number pawls (and thus rotating rotors) I have.
     */
    int numPawls() {
        return _pawls;
    }

    /**
     * Set my rotor slots to the rotors named ROTORS from my set of
     * available rotors (ROTORS[0] names the reflector).
     * Initially, all rotors are set at their 0 setting.
     */
    void insertRotors(String[] rotors) {
        int i = 0;
        ArrayList<String> names = new ArrayList<>();
        for (String name : rotors) {
            for (Rotor r : _allRotors) {
                if (r.name().equals(name)) {
                    _rotors[i] = r;
                    r.set(0);
                    i++;
                    if (names.contains(name)) {
                        throw new EnigmaException("Two of the "
                                + "same name occurred.");
                    } else {
                        names.add(name);
                    }
                }
            }
        }
        if (_rotors.length != rotors.length) {
            throw new EnigmaException("Misnaming");
        }
    }


    /** Set my rotors according to SETTING, which must be a string of
     *  numRotors()-1 upper-case letters. The first letter refers to the
     *  leftmost rotor setting (not counting the reflector).  */
    void setRotors(String setting) {
        if (setting.length() != _numRotors - 1) {
            throw new EnigmaException("Length of settings string, "
                    + "doesn't match number of settable rotors");
        }
        if (!_rotors[0].reflecting()) {
            throw new EnigmaException("Reflector isn't in place");
        }

        for (int i = 1; i <= setting.length(); i++) {
            _rotors[i].set(setting.charAt(i - 1));
        }
    }

    /** Set the plugboard to PLUGBOARD. */
    void setPlugboard(Permutation plugboard) {
        _plugboard = plugboard;
    }

    /** Returns the result of converting the input character C (as an
     *  index in the range 0..alphabet size - 1), after first advancing
     *  the machine. */

    int convert(int c) {

        boolean[] advance = new boolean[numRotors()];
        for (boolean b : advance) {
            b = false;
        }
        for (int i = 0; i < numRotors(); i += 1) {
            if (!_rotors[i].rotates()) {
                advance[i] = false;
            } else if (i == numRotors() - 1) {
                advance[i] = true;
            } else if (_rotors[i + 1].atNotch()) {
                advance[i] = true;
                advance[i + 1] = true;
            }
        }
        for (int i = 0; i < numRotors(); i += 1) {
            if (advance[i]) {
                _rotors[i].advance();
            }
        }

        c = _plugboard.permute(c);
        for (int j = _rotors.length - 1; j >= 0; j--) {
            c = _rotors[j].convertForward(c);
        }

        for (int i = 1; i < _rotors.length; i++) {
            c = _rotors[i].convertBackward(c);
        }

        c = _plugboard.permute(c);
        return c;

    }


    /** Returns the encoding/decoding of MSG, updating the state of
     *  the rotors accordingly. */
    String convert(String msg) {
        String convert =  "";
        for (int i = 0; i < msg.length(); i++) {
            convert += _alphabet.toChar(
                    convert(_alphabet.toInt(msg.charAt(i))));
        }
        return convert;
    }

    /** Returns all the rotors used. */
    Rotor [] getRotors() {
        return this._rotors;
    }

    /** Common alphabet of my rotors. */
    private final Alphabet _alphabet;

    /** The number of rotors. */
    private int _numRotors;

    /** The number of pawls. */
    private int _pawls;

    /** All the available rotors. */
    private Rotor [] _rotors;

    /** Plugboard. */
    private Permutation _plugboard;

    /** Collection of the available rotors. */
    private Collection<Rotor> _allRotors;

}
