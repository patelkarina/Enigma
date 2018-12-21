package enigma;

import static enigma.EnigmaException.*;

/** Represents a permutation of a range of integers starting at 0 corresponding
 *  to the characters of an alphabet.
 *  @author Karina Patel
 */
class Permutation {

    /** Set this Permutation to that specified by CYCLES, a string in the
     *  form "(cccc) (cc) ..." where the c's are characters in ALPHABET, which
     *  is interpreted as a permutation in cycle notation.  Characters in the
     *  alphabet that are not included in any cycle map to themselves.
     *  Whitespace is ignored. */
    Permutation(String cycles, Alphabet alphabet) {
        _alphabet = alphabet;
        _isDerangement = true;
        _forwardMapping = new char[_alphabet.size()];
        _backwardMapping = new char[_alphabet.size()];

        String[] split = cycles.split("[\\s\\t\\n]+");


        for (String cycle : split) {
            addCycle(cycle);
        }


        for (int i = 0; i < _forwardMapping.length; i++) {
            if (_forwardMapping[i] == 0) {
                _forwardMapping[i] = _alphabet.toChar(i);

                _backwardMapping[i] = _alphabet.toChar(i);
                _isDerangement = false;

            }
        }
    }


    /** Add the cycle c0->c1->...->cm->c0 to the permutation, where CYCLE is
     *  c0c1...cm. */
    private void addCycle(String cycle) {
        if (cycle == null || cycle.length() == 0) {
            return;
        }


        cycle = cycle.substring(1, cycle.length() - 1);
        String cycleFormatted = cycle + cycle.charAt(0);


        if (cycleFormatted.length() == 2) {
            _isDerangement = false;
        }

        for (int i = 0; i < cycleFormatted.length() - 1; i++) {
            char from = cycleFormatted.charAt(i);
            char to = cycleFormatted.charAt(i + 1);

            _forwardMapping[_alphabet.toInt(from)] = to;
            _backwardMapping[_alphabet.toInt(to)] = from;
        }
    }


    /** Return the value of P modulo the size of this permutation. */
    final int wrap(int p) {
        int r = p % size();
        if (r < 0) {
            r += size();
        }
        return r;
    }

    /** Returns the size of the alphabet I permute. */
    int size() {
        return _alphabet.size();
    }

    /** Return the result of applying this permutation to P modulo the
     *  alphabet size. */
    int permute(int p) {
        return _alphabet.toInt(_forwardMapping[wrap(p)]);
    }

    /** Return the result of applying the inverse of this permutation
     *  to  C modulo the alphabet size. */
    int invert(int c) {
        return _alphabet.toInt(_backwardMapping[wrap(c)]);
    }

    /** Return the result of applying this permutation to the index of P
     *  in ALPHABET, and converting the result to a character of ALPHABET. */
    char permute(char p) {
        return _alphabet.toChar(permute(_alphabet.toInt(p)));
    }

    /** Return the result of applying the inverse of this permutation to C. */
    int invert(char c) {
        return _alphabet.toChar(invert(_alphabet.toInt(c)));
    }

    /** Return the alphabet used to initialize this Permutation. */
    Alphabet alphabet() {
        return _alphabet;
    }

    /** Return true iff this permutation is a derangement (i.e., a
     *  permutation for which no value maps to itself). */
    boolean derangement() {
        return _isDerangement;
    }

    /** Alphabet of this permutation. */
    private Alphabet _alphabet;

    /** String cycle for this permutation. */
    private String _cycles;

    /** Forward mapping of this permutation. */
    private char [] _forwardMapping;

    /** Reverse mapping for this permutation.  */
    private char [] _backwardMapping;

    /** Whether the permutation is a derangement.  */
    private boolean _isDerangement;


}
