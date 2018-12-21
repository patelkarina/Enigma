package enigma;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.regex.Pattern;

import static enigma.EnigmaException.*;

/** Enigma simulator.
 *  @author Karina Patel
 */
public final class Main {

    /** Process a sequence of encryptions and decryptions, as
     *  specified by ARGS, where 1 <= ARGS.length <= 3.
     *  ARGS[0] is the name of a configuration file.
     *  ARGS[1] is optional; when present, it names an input file
     *  containing messages.  Otherwise, input comes from the standard
     *  input.  ARGS[2] is optional; when present, it names an output
     *  file for processed messages.  Otherwise, output goes to the
     *  standard output. Exits normally if there are no errors in the input;
     *  otherwise with code 1. */
    public static void main(String... args) {
        try {
            new Main(args).process();
            return;
        } catch (EnigmaException excp) {
            System.err.printf("Error: %s%n", excp.getMessage());
        }
        System.exit(1);
    }

    /** Check ARGS and open the necessary files (see comment on main). */
    Main(String[] args) {
        if (args.length < 1 || args.length > 3) {
            throw error("Only 1, 2, or 3 command-line arguments allowed");
        }

        _config = getInput(args[0]);

        if (args.length > 1) {
            _input = getInput(args[1]);
        } else {
            _input = new Scanner(System.in);
        }

        if (args.length > 2) {
            _output = getOutput(args[2]);
        } else {
            _output = System.out;
        }
    }

    /** Return a Scanner reading from the file named NAME. */
    private Scanner getInput(String name) {
        try {
            return new Scanner(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Return a PrintStream writing to the file named NAME. */
    private PrintStream getOutput(String name) {
        try {
            return new PrintStream(new File(name));
        } catch (IOException excp) {
            throw error("could not open %s", name);
        }
    }

    /** Configure an Enigma machine from the contents of configuration
     *  file _config and apply it to the messages in _input, sending the
     *  results to _output. */
    private void process() {

        Machine enigma = readConfig();
        String setting = _input.nextLine();
        setUp(enigma, setting);


        while (_input.hasNextLine()) {
            String next = _input.nextLine().trim();
            if (next.isEmpty()) {
                _output.println();
            } else if (next.indexOf('*') >= 0) {
                setUp(enigma, next);
            } else {
                next = next.replaceAll(" ", "");
                next = next.toUpperCase();
                printMessageLine(enigma.convert(next));
            }
        }
    }

    /** Return an Enigma machine configured from the contents of configuration
     *  file _config. */
    private Machine readConfig() {
        try {

            String alphabetString;
            if (_config.hasNext(Pattern.compile("[\\w-]+"))) {
                alphabetString = _config.next();
                if (alphabetString.indexOf('-') >= 0) {
                    char C1 = alphabetString.charAt(0);
                    char C2 = alphabetString.charAt(alphabetString.length()
                            - 1);

                    if (C1 > C2) {
                        throw error("Improperly formatted "
                                + "config for initial alphabet: C1 > C2.");
                    }
                    _alphabet = new CharacterRange(C1, C2);
                } else {
                    _alphabet = new CharacterConfiguration(alphabetString);
                }
            } else {
                throw error("Improperly formatted "
                        + "config for initial alphabet.");
            }


            int numRotors;
            if (_config.hasNextInt()) {
                numRotors = _config.nextInt();
            } else {
                throw error("Improperly formatted "
                        + "config for number of rotors (S).");
            }


            int pawls;
            if (_config.hasNextInt()) {
                pawls = _config.nextInt();
            } else {
                throw error("Improperly formatted "
                        + "config for number of pawls (P).");
            }


            ArrayList<Rotor> allRotors = new ArrayList<>();
            while (_config.hasNext()) {
                allRotors.add(readRotor());
            }

            return new Machine(_alphabet, numRotors, pawls, allRotors);
        } catch (NoSuchElementException excp) {
            throw error("configuration file truncated");
        }
    }

    /** Return a rotor, reading its description from _config. */
    private Rotor readRotor() {
        try {

            String name;
            if (_config.hasNext(Pattern.compile("[^\\s()]+"))) {
                name = _config.next().toUpperCase();
            } else {
                throw error("Improperly formatted "
                        + "config for rotor name %s", _config.next());
            }


            String type;
            if (_config.hasNext(Pattern.compile("[RNM]\\w*"))) {
                type = _config.next();
            } else {
                throw error("Improperly formatted "
                        + "config for rotor type %s", _config.next());
            }


            String cycles = "";
            while (_config.hasNext(Pattern.compile("\\(\\w+\\)"))
                    || _config.hasNext(Pattern.compile("\\([\\w()]+\\)"))) {
                String cycle = _config.next();
                cycle = cycle.replace(")(", ") (");

                cycles = cycles + " " + cycle;
            }


            Permutation permutation = new Permutation(cycles, _alphabet);

            Rotor r;
            if (type.charAt(0) == 'R') {
                r = new Reflector(name, permutation);
            } else if (type.charAt(0) == 'N') {
                r = new FixedRotor(name, permutation);
            } else if (type.charAt(0) == 'M') {
                r = new MovingRotor(name, permutation, type.substring(1));
            } else {
                throw error("Rotor type %s not recognized", type);
            }

            return r;
        } catch (NoSuchElementException excp) {
            throw error("bad rotor description");
        }
    }

    /** Set M according to the specification given on SETTINGS,
     *  which must have the format specified in the assignment. */
    private void setUp(Machine M, String settings) {
        String[] settingsSplit = settings.toUpperCase().split("[\\s\\t\\n]+");


        if (settingsSplit.length == 0) {
            throw error("No setting detected.");
        }

        if (settingsSplit.length - 1 < M.numRotors()) {
            throw new EnigmaException("Length of settings string, "
                    + "doesn't match number of settable rotors");
        }

        if (!settingsSplit[0].equals("*")) {
            throw error("No valid setting detected. Must start with a *.");
        }

        if (settingsSplit.length < M.numRotors() + 2) {
            throw error("No valid setting detected. Too few elements.");
        }


        int index = 0;
        String[] rotors = new String[M.numRotors()];
        while (index < M.numRotors() && settingsSplit[index + 1].
                matches(Pattern.compile("[^\\s()]+").pattern())) {
            String name = settingsSplit[index + 1];
            rotors[index] = name;
            index++;
        }
        if (index != M.numRotors()) {
            throw error("Improperly formatted rotor names in settings.");
        }


        index = M.numRotors() + 1;
        String setting = settingsSplit[index];
        if (!setting.matches(Pattern.compile("\\w+").pattern())) {
            throw error("Improperly formatted rotor setting in settings.");
        }


        index = M.numRotors() + 2;
        String cycles = "";
        while (index < settingsSplit.length
                && (settingsSplit[index].
                        matches(Pattern.compile("\\(\\w+\\)").pattern())
                        || settingsSplit[index].matches(Pattern.
                        compile("\\([\\w()]+\\)").pattern()))) {
            String cycle = settingsSplit[index];
            cycle = cycle.replace(")(", ") (");
            cycles = cycles + " " + cycle;
            index++;
        }


        M.insertRotors(rotors);
        M.setRotors(setting);
        M.setPlugboard(new Permutation(cycles, _alphabet));
    }

    /** Print MSG in groups of five (except that the last group may
     *  have fewer letters). */
    private void printMessageLine(String msg) {
        if (msg == null || msg.length() == 0) {
            throw error("Final output message is null or empty.");
        } else {
            msg = msg.replaceAll(" ", "");
            String temp = "";
            while (msg.length() >= 6) {
                temp += msg.substring(0, 5) + " ";
                msg = msg.substring(5);
            }
            temp += msg;
            _output.println(temp);
        }
    }

    /** Alphabet used in this machine. */
    private Alphabet _alphabet;

    /** Source of input messages. */
    private Scanner _input;

    /** Source of machine configuration. */
    private Scanner _config;

    /** File for encoded/decoded messages. */
    private PrintStream _output;
}
