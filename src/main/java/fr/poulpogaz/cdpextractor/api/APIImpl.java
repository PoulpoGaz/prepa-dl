package fr.poulpogaz.cdpextractor.api;

import fr.poulpogaz.cdpextractor.utils.Form;
import fr.poulpogaz.json.JsonException;
import fr.poulpogaz.json.tree.JsonObject;
import fr.poulpogaz.json.tree.JsonTreeReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.print.Doc;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class APIImpl implements IAPI {

    private static final Pattern CDP_REGEX =
            Pattern.compile("^(CDP_SESSION=.*); path=/(.*)/; domain=cahier-de-prepa.fr; secure$");

    private static final String USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36 OPR/85.0.4341.47";

    private final HttpClient CLIENT = HttpClient.newHttpClient();

    private final String ROOT = "https://cahier-de-prepa.fr";

    @Override
    public Session login(String mail, String password, boolean permanentConnection) throws IOException, CDPException {
        try {
            return loginImpl(mail, password, permanentConnection);
        } catch (InterruptedException | JsonException e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<CDPRootFolder> getRootFolders(Session session) throws IOException, CDPException {
        try {
            return getRootFoldersImpl(session);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    @Override
    public List<CDPEntry> getContent(CDPFolder folder, Session session) throws IOException, CDPException {
        try {
            return getContentImpl(folder, session);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    @Override
    public InputStream getInputStream(CDPFile file, Session session) throws IOException, CDPException {
        try {
            return getInputStreamImpl(file, session);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void logout(Session session) throws IOException, CDPException {
        try {
            logoutImpl(session);
        } catch (InterruptedException e) {
            throw new IOException(e);
        }
    }

    // *******************
    // * IMPLEMENTATIONS *
    // *******************

    protected Session loginImpl(String mail, String password, boolean permanentConnection)
            throws IOException, CDPException, InterruptedException, JsonException {

        Form form = new Form().add("mail", mail)
                .add("motdepasse", password)
                .add("connexion", "1");

        if (permanentConnection) {
            form.add("permconn",  "1");
        }

        // construct request
        HttpRequest request = form.createRequest(url("/connexion/ajax.php"))
                .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                //.setHeader("User-Agent", USER_AGENT)
                .build();

        HttpResponse<InputStream> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

        // check error
        JsonObject object = (JsonObject) JsonTreeReader.read(response.body());
        if (!object.getAsString("etat").equals("ok")) {
            String message = object.getAsString("message");

            throw new CDPException(message);
        }

        // get CDP_SESSION
        String cdpSession = null;
        String cdpPerm = null;
        String cpge = null;
        for (String h : response.headers().allValues("Set-Cookie")) {
            Matcher m = CDP_REGEX.matcher(h);

            if (m.find() && !m.group(2).equals("connexion")) {
                cdpSession = m.group(1);
                cpge = m.group(2);
            } else if (h.startsWith("CDP_SESSION_PERM")) {
                int end = h.indexOf(';');
                cdpPerm = h.substring(0, end);
            }
        }

        if (cpge == null) {
            throw new CDPException("Failed to get cpge");
        }
        if (cdpSession == null) {
            throw new CDPException("Failed to get CDP_SESSION");
        }

        return new Session(cpge, cdpSession, cdpPerm);
    }

    protected List<CDPRootFolder> getRootFoldersImpl(Session session) throws IOException, CDPException, InterruptedException {
        URI uri = uri("/%s/docs", session.getCPGE());
        Document document = getDocument(uri, session);
        Elements elements = document.select("#menu > a:not([href^=docs?rep])[href^=docs]");

        List<CDPRootFolder> folders = new ArrayList<>();
        for (Element e : elements) {
            String folderName = getFolderName(e);
            String req = e.attr("href");

            folders.add(new CDPRootFolder(session.getCPGE(), folderName, req));
        }

        return folders;
    }

    private String getFolderName(Element element) {
        Element e = element;

        while ((e = e.previousElementSibling()) != null) {
            if (e.is("h3")) {
                return e.html();
            }
        }

        return "Général";
    }

    protected List<CDPEntry> getContentImpl(CDPFolder folder, Session session) throws IOException, CDPException, InterruptedException {
        URI uri = uri("/%s/%s", session.getCPGE(), folder.request());
        Document document = getDocument(uri, session);

        List<CDPEntry> entries = new ArrayList<>();

        // get documents
        Elements docs = document.select(".doc");
        for (Element element : docs) {
            entries.add(new CDPFile(session.getCPGE(), element));
        }

        // get folders
        Elements reps = document.select(".rep");
        for (Element element : reps) {
            entries.add(new CDPFolder(session.getCPGE(), element));
        }

        return entries;
    }

    protected InputStream getInputStreamImpl(CDPFile file, Session session) throws IOException, CDPException, InterruptedException {
        URI uri = uri("/%s/%s", session.getCPGE(), file.request());

        HttpRequest request = setHeader(HttpRequest.newBuilder(uri), session).build();

        return CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream())
                .body();
    }

    protected void logoutImpl(Session session) throws IOException, CDPException, InterruptedException {
        URI uri = uri("/%s/", session.getCPGE());
        Document document = getDocument(uri, session);
        String csrfToken = document.body().attr("data-csrf-token");


        Form form = new Form().add("csrf-token", csrfToken)
                .add("action", "deconnexion");

        // construct request
        HttpRequest request = form.createRequest(url("/%s/ajax.php", session.getCPGE()))
                .setHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
                .build();

        CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
    }

    private String url(String end, String... format) {
        if (format.length > 0) {
            return ROOT + end.formatted((Object[]) format);
        } else {
            return ROOT + end;
        }
    }

    private URI uri(String end, String... format) {
        return URI.create(url(end, format));
    }

    private Document getDocument(URI uri, Session session) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri);
        setHeader(builder, session);

        HttpRequest request = builder.build();
        HttpResponse<InputStream> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

        session.updateSession(response);

        return Jsoup.parse(response.body(), "UTF-8", ROOT);
    }

    private HttpRequest.Builder setHeader(HttpRequest.Builder builder, Session session) {
        builder.header("Cookie", session.getCDPSession())
                ;//.setHeader("User-Agent", USER_AGENT);

        if (session.getCDPPerm() != null) {
            builder.header("Cookie", session.getCDPPerm());
        }

        return builder;
    }
}
