package fr.poulpogaz.prepadl.cdp;

import fr.poulpogaz.prepadl.Session;

import java.io.IOException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CDPSession implements Session {

    private static final Pattern CDP_REGEX =
            Pattern.compile("^(CDP_SESSION=.*); path=/(.*)/; domain=cahier-de-prepa.fr; secure$");


    private final String cpge;
    private String cdpSession;
    private String cdpPerm;

    public CDPSession(String cpge, String cdpSession, String cdpPerm) {
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

    @Override
    public HttpRequest.Builder setHeader(HttpRequest.Builder request) {
        request.header("Cookie", cdpSession);

        if (cdpPerm != null) {
            request.header("Cookie", cdpPerm);
        }

        return request;
    }

    @Override
    public boolean isValid() {
        return cpge != null && cdpSession != null;
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

}
