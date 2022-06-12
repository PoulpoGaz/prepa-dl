package fr.poulpogaz.prepadl.utils;

import fr.poulpogaz.args.api.VoidCommand;

public class Version implements VoidCommand {

    public static final String VERSION = "1.1-dev";

    public static void showVersion() {
        System.out.println("prepa-dl version: " + VERSION);

        String jvm = System.getProperty("java.vm.name");
        String javaVersion = System.getProperty("java.version");
        String vendor = System.getProperty("java.version");

        System.out.printf("Java version: %s, vendor: %s, jvm: %s%n", javaVersion, vendor, jvm);
    }

    @Override
    public String getName() {
        return "version";
    }

    @Override
    public String getUsage() {
        return "Show version";
    }

    @Override
    public boolean addHelp() {
        return false;
    }

    @Override
    public void run() {
        showVersion();
    }
}
