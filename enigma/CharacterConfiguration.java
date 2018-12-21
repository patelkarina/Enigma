package enigma;

import java.util.HashMap;

import static enigma.EnigmaException.*;

/** An alphabet consisting of Unicode characters in a certain range in
 *  order.
 * @author Karina Patel
 *  */

class CharacterConfiguration extends Alphabet {

    /** An alphabet consisting of all characters between
     * FIRST and LAST, inclusive.
     * @param characters the characters used
     **/

    CharacterConfiguration(String characters) {
        if (characters == null || characters.length() == 0) {
            throw error("Empty set of characters");
        }

        _charToInt = new HashMap<>();
        _intToChar = new HashMap<>();

        for (int i = 0; i < characters.length(); i++) {
            if (_charToInt.containsKey(Character.
                    toUpperCase(characters.charAt(i)))) {
                throw error("Duplicate value in characters string.");
            }

            _charToInt.put(Character.toUpperCase(characters.charAt(i)), i);
            _intToChar.put(i, Character.toUpperCase(characters.charAt(i)));
        }
    }

    @Override
    int size() {
        return _charToInt.size();
    }

    @Override
    boolean contains(char ch) {
        return _charToInt.containsKey(ch);
    }

    @Override
    char toChar(int index) {
        Character result = _intToChar.get(index);
        if (result == null) {
            throw error("Character index out of range");
        }

        return result;
    }

    @Override
    int toInt(char ch) {
        Integer result = _charToInt.get(ch);
        if (result == null) {
            throw error("Character out of range");
        }

        return result;
    }

    /** Bimap of characters in this Alphabet. */
    private HashMap<Character, Integer> _charToInt;

    /** Bimap of characters in this Alphabet. */
    private HashMap<Integer, Character> _intToChar;

}
