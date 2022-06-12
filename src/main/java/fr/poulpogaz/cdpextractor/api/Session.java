package fr.poulpogaz.cdpextractor.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Session {

    public static final Path SESSION_FILE = Path.of(".session");

    private static final Pattern CDP_REGEX =
            Pattern.compile("^(CDP_SESSION=.*); path=/(.*)/; domain=cahier-de-prepa.fr; secure$");


    public static Session read() throws IOException {
        if (!Files.exists(SESSION_FILE)) {
            return null;
        }

        Properties properties = new Properties();

        InputStream is = Files.newInputStream(SESSION_FILE);
        properties.load(is);
        is.close();

        String cpge = properties.getProperty("cpge");
        String cpdSession = properties.getProperty("CDP_SESSION");
        String cpdPerm = properties.getProperty("CDP_PERM_SESSION");

        return new Session(cpge, cpdSession, cpdPerm);
    }


    private final String cpge;
    private String cdpSession;
    private String cdpPerm;

    public Session(String cpge, String cdpSession, String cdpPerm) {
        this.cpge = cpge;
        this.cdpSession = cdpSession;
        this.cdpPerm = cdpPerm;
    }

    public void updateSession(HttpResponse<?> response) {
        for (String h : response.headers().allValues("Set-Cookie")) {
            Matcher m = CDP_REGEX.matcher(h);

            if (m.find() && !m.group(2).equals("connexion")) {
                cdpSession = m.group(1);
            } else if (h.startsWith("CDP_SESSION_PERM")) {
                int end = h.indexOf(';');
                cdpPerm = h.substring(0, end);
            }
        }
    }

    public String getCPGE() {
        return cpge;
    }

    public String getCDPSession() {
        return cdpSession;
    }

    public String getCDPPerm() {
        return cdpPerm;
    }

    public void save() throws IOException {
        Properties properties = new Properties();
        properties.setProperty("cpge", cpge);
        properties.setProperty("CDP_SESSION", cdpSession);
        properties.setProperty("CDP_PERM_SESSION", cdpPerm);

        OutputStream os = Files.newOutputStream(SESSION_FILE);
        properties.store(os, null);
        os.close();
    }

    @Override
    public String toString() {
        return "Session{" +
                "cpge='" + cpge + '\'' +
                ", cdpSession='" + cdpSession + '\'' +
                ", cdpPerm='" + cdpPerm + '\'' +
                '}';
    }
}
