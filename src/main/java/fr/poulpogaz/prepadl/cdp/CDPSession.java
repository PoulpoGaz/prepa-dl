package fr.poulpogaz.prepadl.cdp;

import fr.poulpogaz.prepadl.Session;
import fr.poulpogaz.prepadl.utils.Utils;

import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class CDPSession implements Session {

    public static CDPSession createFromCookieStore() throws CDPException {
        List<HttpCookie> cookies = Utils.COOKIE_STORE.get(uri);

        String cpge = null;
        String cdpSession = null;
        String cdpPerm = null;
        for (HttpCookie cookie : cookies) {
            if (cookie.hasExpired()) {
                continue;
            }

            if (cookie.getName().equalsIgnoreCase("CDP_SESSION")) {
                cpge = cookie.getPath();
                cpge = cpge.substring(1, cpge.length() - 1); // remove first and last /

                cdpSession = cookie.getValue();
            } else if (cookie.getName().equalsIgnoreCase("CDP_SESSION_PERM")) {
                cdpPerm = cookie.getValue();
            }
        }

        if (cpge == null) {
            throw new CDPException("Failed to get cpge");
        }
        if (cdpSession == null) {
            throw new CDPException("Failed to get CDP_SESSION");
        }

        return new CDPSession(cpge, cdpSession, cdpPerm);
    }

    private static final URI uri = URI.create("https://cahier-de-prepa.fr/");


    private final String cpge;
    private String cdpSession;
    private String cdpPerm;

    public CDPSession(String cpge, String cdpSession, String cdpPerm) {
        this.cpge = cpge;
        this.cdpSession = cdpSession;
        this.cdpPerm = cdpPerm;
    }

    public void updateSession(HttpResponse<?> response) {
        List<HttpCookie> cookies = Utils.COOKIE_STORE.get(uri);

        for (HttpCookie cookie : cookies) {
            if (cookie.hasExpired()) {
                continue;
            }
            if (cookie.getName().equalsIgnoreCase("CDP_SESSION")) {
                cdpSession = cookie.getName() + "=" + cookie.getValue();
            } else if (cookie.getName().equalsIgnoreCase("CDP_SESSION_PERM")) {
                cdpPerm = cookie.getName() + "=" + cookie.getValue();
            }
        }
    }

    @Override
    public HttpRequest.Builder setHeader(HttpRequest.Builder request) {
        request.header("Cookie", "CDP_SESSION=" + cdpSession);

        if (cdpPerm != null) {
            request.header("Cookie", "CDP_SESSION_PERM=" + cdpPerm);
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
