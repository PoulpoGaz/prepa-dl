package fr.poulpogaz.prepadl.anthonylick;

import fr.poulpogaz.prepadl.PrepaDLException;
import fr.poulpogaz.prepadl.Session;
import fr.poulpogaz.prepadl.utils.Utils;

import java.io.IOException;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

public class ALSession implements Session {

    private static final URI login = URI.create("https://anthonylick.com/wp-login.php?action=postpass");
    private static final String postDate = "post_password=%s&Submit=Enter";

    private final String password;
    private HttpCookie cookie;

    public ALSession(String password) {
        this.password = password;
    }

    public void login() throws IOException, InterruptedException {
        if (cookie != null && !cookie.hasExpired()) {
            return;
        }

        cookie = null;
        CookieStore store = Utils.COOKIE_STORE;

        for (HttpCookie c : store.get(login)) {
            store.remove(login, c);
        }

        HttpRequest req = HttpRequest.newBuilder()
                .uri(login)
                .setHeader("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString(postDate.formatted(password)))
                .build();

        HttpResponse<Void> rep = Utils.CLIENT.send(req, HttpResponse.BodyHandlers.discarding());

        if (rep.statusCode() == 302 || rep.statusCode() >= 200 && rep.statusCode() < 300) {
            List<HttpCookie> cookies = store.get(login);

            for (HttpCookie c : cookies) {
                System.out.println(c);
                if (c.getName().startsWith("wp-postpass")) {
                    cookie = c;

                    break;
                }
            }
        }
    }

    @Override
    public HttpRequest.Builder setHeader(HttpRequest.Builder request) throws PrepaDLException {
        if (cookie == null) {
            throw new ALException("Not logged");
        }
        if (cookie.hasExpired()) {
            throw new ALException("Cookie has expired");
        }

        request.header("Cookie", cookie.getName() + "=" + cookie.getValue());

        return request;
    }

    @Override
    public boolean isValid() {
        return cookie != null && !cookie.hasExpired();
    }
}
