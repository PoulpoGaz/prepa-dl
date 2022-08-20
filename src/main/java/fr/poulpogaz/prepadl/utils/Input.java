package fr.poulpogaz.prepadl.utils;

import java.io.Console;
import java.util.Scanner;

public class Input {

    private static final Reader reader;

    static {
        Console console = System.console();

        if (console == null) {
            System.out.println("echoing is disabled, your password will be visible!");

            reader = new SystemReader();
        } else {
            reader = new ConsoleReader(console);
        }
    }

    public static Pair<String, String> readInput() {
        String mail = reader.readString("Mail: ");
        String password = reader.readPassword("Password: ");

        return new Pair<>(mail, password);
    }

    public static String readPassword() {
        return reader.readPassword("Password: ");
    }

    public static Reader getReader() {
        return reader;
    }

    public interface Reader {

        String readString(String fmt, Object... args);

        String readPassword(String fmt, Object... args);
    }

    private static class ConsoleReader implements Reader {

        private final Console console;

        public ConsoleReader(Console console) {
            this.console = console;
        }

        @Override
        public String readString(String fmt, Object... args) {
            return console.readLine(fmt, args);
        }

        @Override
        public String readPassword(String fmt, Object... args) {
            return String.valueOf(console.readPassword(fmt, args));
        }
    }

    private static class SystemReader implements Reader {

        private final Scanner scanner = new Scanner(System.in);;

        @Override
        public String readString(String fmt, Object... args) {
            System.out.printf(fmt, args);
            return scanner.nextLine();
        }

        @Override
        public String readPassword(String fmt, Object... args) {
            System.out.printf(fmt, args);
            return scanner.nextLine();
        }
    }
}
