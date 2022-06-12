package fr.poulpogaz.cdpextractor.utils;

public class Version {

    public static final String VERSION = "1.0";

    public static void showVersion() {
        System.out.println("CDPExtractor version: " + VERSION);

        String jvm = System.getProperty("java.vm.name");
        String javaVersion = System.getProperty("java.version");
        String vendor = System.getProperty("java.version");

        System.out.printf("Java version: %s, vendor: %s, jvm: %s%n", javaVersion, vendor, jvm);
    }
}
