package fr.poulpogaz.prepadl.cdp;

public class CDPRootFolder extends CDPFolder {

    public CDPRootFolder(String cpge, String name, String request) {
        super(cpge, name, request, false);
    }

    @Override
    public boolean isRootFolder() {
        return true;
    }

    @Override
    public String toString() {
        return "CDPRootFolder[" +
                "request=" + request + ", " +
                "cpge=" + cpge + ", " +
                "name=" + name + ']';
    }
}
