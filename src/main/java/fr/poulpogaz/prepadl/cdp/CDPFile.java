package fr.poulpogaz.prepadl.cdp;

import fr.poulpogaz.prepadl.utils.Utils;
import org.jsoup.nodes.Element;

import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CDPFile implements CDPEntry {

    private static final Pattern ATTRIBUTES_REGEX = Pattern.compile("^\\((.+), (.+), (.+)\\)$");

    private final String cpge;
    private final String name;
    private final String request;
    private final boolean isLocked;

    private final String fileType;
    private final Date lastModified;
    private final int size;

    public CDPFile(String cpge, String name, String request, boolean isLocked, String fileType, Date lastModified, int size) {
        this.cpge = cpge;
        this.name = name;
        this.request = request;
        this.isLocked = isLocked;
        this.fileType = fileType;
        this.lastModified = lastModified;
        this.size = size;
    }

    public CDPFile(String cpge, Element element) throws CDPException {
        this.cpge = cpge;
        this.name = element.selectFirst(".nom").html();
        this.isLocked = element.selectFirst(".icon-minilock") != null;

        if (!isLocked) {
            this.request = element.selectFirst("a").attr("href");
        } else {
            this.request = null;
        }

        String attributes = element.selectFirst(".docdonnees").html();
        Matcher matcher = ATTRIBUTES_REGEX.matcher(attributes);
        if (matcher.find()) {
            fileType = matcher.group(1);
            lastModified = Utils.parseDateDMY(matcher.group(2));
            size = Utils.parseSize(matcher.group(3));

            if (lastModified == null) {
                throw new CDPException("Can't parse date");
            }
        } else {
            throw new CDPException("Can't parse attributes: " + attributes);
        }
    }

    public String cpge() {
        return cpge;
    }

    public String name() {
        return name;
    }

    public String request() {
        return request;
    }

    public String fileType() {
        return fileType;
    }

    public Date lastModified() {
        return lastModified;
    }

    public int size() {
        return size;
    }

    @Override
    public boolean isFile() {
        return true;
    }

    @Override
    public boolean isFolder() {
        return false;
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
        var that = (CDPFile) obj;
        return Objects.equals(this.request, that.request) &&
                Objects.equals(this.cpge, that.cpge) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.fileType, that.fileType) &&
                Objects.equals(this.lastModified, that.lastModified) &&
                this.size == that.size;
    }

    @Override
    public int hashCode() {
        return Objects.hash(request, cpge, name, fileType, lastModified, size);
    }

    @Override
    public String toString() {
        return "CDPFile[" +
                "request=" + request + ", " +
                "cpge=" + cpge + ", " +
                "name=" + name + ", " +
                "type=" + fileType + ", " +
                "lastModified=" + lastModified + ", " +
                "size=" + size + ']';
    }

}
