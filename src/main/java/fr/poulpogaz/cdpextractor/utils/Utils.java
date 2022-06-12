package fr.poulpogaz.cdpextractor.utils;

import java.io.Console;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static Pair<String, String> readInput() {
        Console console = System.console();

        // get mail and password
        String mail;
        String password;

        if (console == null) {
            System.out.println("echoing is disabled, your password will be visible!");

            Scanner scanner = new Scanner(System.in);
            System.out.print("Mail: ");
            mail = scanner.nextLine();

            System.out.print("Password: ");
            password = scanner.nextLine();

        } else {
            mail = console.readLine("Mail: ");
            password = String.valueOf(console.readPassword("Password: "));
        }

        return new Pair<>(mail, password);
    }



    public static Date parseDateDMY(String date) {
        return parseDate(date, "dd/MM/yyyy");
    }

    public static Date parseDate(String date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat f = new SimpleDateFormat(format);

        try {
            return f.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static int parseSize(String sizeString) {
        String[] parts = sizeString.split("&nbsp;");

        int size = Integer.parseInt(parts[0]);

        switch (parts[1]) {
            case "ko" -> size *= 1024;
            case "Mo" -> size *= 1024 * 1024;
            case "Go" -> size *= 1024 * 1024 * 1024;
            default -> {}
        }

        return size;
    }

    public static String getRegexGroup(String text, String regex) {
        Matcher matcher = Pattern.compile(regex).matcher(text);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }



    public static <K, V> V getOrCreate(Map<K, V> map, K key, Supplier<V> defaultValue) {
        V value = map.get(key);

        if (value == null) {
            value = defaultValue.get();
            map.put(key, value);
        }

        return value;
    }

    public static <T> boolean contains(T[] array, T o) {
        if (o == null) {
            for (T t : array) {
                if (t == null) {
                    return true;
                }
            }
        } else {
            for (T t : array) {
                if (o.equals(t)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static <T> T find(Collection<T> list, Predicate<T> predicate) {
        return list.stream()
                .filter(predicate)
                .findFirst()
                .orElse(null);
    }

    public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
            Function<? super T, ? extends U> keyExtractor)
    {
        Objects.requireNonNull(keyExtractor);
        return (c1, c2) -> {
            U u1 = keyExtractor.apply(c1);
            U u2 = keyExtractor.apply(c2);

            if (u1 == null && u2 == null) {
                return 0;
            } else if (u1 == null) {
                return -1;
            } else if (u2 == null) {
                return 1;
            } else {
                return u1.compareTo(u2);
            }
        };
    }
}
