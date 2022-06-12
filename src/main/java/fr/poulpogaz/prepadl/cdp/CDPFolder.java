package fr.poulpogaz.prepadl.cdp;

import org.jsoup.nodes.Element;

import java.util.Objects;

public class CDPFolder implements CDPEntry {

    protected final String cpge;
    protected final String name;
    protected final String request;
    protected final boolean isLocked;

    public CDPFolder(String cpge, String name, String request, boolean isLocked) {
        this.request = request;
        this.cpge = cpge;
        this.name = name;
        this.isLocked = isLocked;
    }

    public CDPFolder(String cpge, Element element) {
        this.cpge = cpge;
        this.isLocked = element.selectFirst(".icon-minilock") != null;
        this.name = element.selectFirst(".nom").html();

        if (!isLocked) {
            this.request = "docs" + element.selectFirst("a").attr("href");
        } else {
            this.request = null;
        }
    }

    @Override
    public String cpge() {
        return cpge;
    }

    @Override
    public String request() {
        return request;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public boolean isFile() {
        return false;
    }

    @Override
    public boolean isFolder() {
        return true;
    }

    @Override
    public boolean isRootFolder() {
        return false;
    }

    @Override
    public boolean isLocked() {
        return isLocked;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CDPFolder) obj;
        return Objects.equals(this.request, that.request) &&
                Objects.equals(this.cpge, that.cpge) &&
                Objects.equals(this.name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, cpge, name);
    }

    @Override
    public String toString() {
        return "CDPFolder[" +
                "request=" + request + ", " +
                "cpge=" + cpge + ", " +
                "name=" + name + ']';
    }

}
