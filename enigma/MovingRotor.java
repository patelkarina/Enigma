package enigma;

import static enigma.EnigmaException.*;

/** Class that represents a rotating rotor in the enigma machine.
 *  @author Karina Patel
 */
class MovingRotor extends Rotor {

    /** A rotor named NAME whose permutation in its default setting is
     *  PERM, and whose notches are at the positions indicated in NOTCHES.
     *  The Rotor is initally in its 0 setting (first character of its
     *  alphabet).
     */
    MovingRotor(String name, Permutation perm, String notches) {
        super(name, perm);
        _permutation = perm;
        _notches = notches;



    }

    @Override
    boolean rotates() {
        return true;
    }

    @Override
    boolean atNotch() {
        for (int i = 0; i < _notches.length(); i++) {
            char x = _notches.charAt(i);
            if (x == alphabet().toChar(setting())) {
                return true;
            }
        }
        return false;
    }


    @Override
    void advance() {
        int advance = _permutation.wrap(setting() + 1);
        set(advance);
    }

    /** Indicates the position of notches. */
    private String _notches;

    /** The permutation implemented by this rotor in its default setting. */
    private Permutation _permutation;

    /** Alphabet of this permutation.*/
    private Alphabet _alphabet;



}


