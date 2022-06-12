package fr.poulpogaz.prepadl.cdp;

public interface CDPEntry {

    String cpge();

    String name();

    String request();

    boolean isFile();

    boolean isFolder();

    boolean isRootFolder();

    boolean isLocked();
}
