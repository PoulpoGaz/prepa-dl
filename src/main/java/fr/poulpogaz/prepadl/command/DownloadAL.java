package fr.poulpogaz.prepadl.command;

import fr.poulpogaz.prepadl.PrepaDLException;
import fr.poulpogaz.prepadl.anthonylick.ALEntry;
import fr.poulpogaz.prepadl.anthonylick.ALIterator;
import fr.poulpogaz.prepadl.anthonylick.ALSession;
import fr.poulpogaz.prepadl.utils.NamedUrl;
import fr.poulpogaz.prepadl.utils.Utils;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DownloadAL extends DownloadCommand {

    @Override
    public String getName() {
        return "dl-info";
    }

    @Override
    public String getUsage() {
        return "Download all files from https://anthonylick.com/";
    }

    @Override
    public boolean addHelp() {
        return false;
    }

    @Override
    public void downloadImpl() throws IOException, PrepaDLException, InterruptedException {
        ALSession session = login();
        ALIterator iterator = new ALIterator(session, "https://anthonylick.com/mp2i/");

        ALEntry last = null;

        while (iterator.hasNext()) {
            ALEntry next = iterator.next();

            Path dir = out.resolve(next.group());

            if (last == null || !last.group().equals(next.group())) {
                if (!Files.exists(dir)) {
                    Files.createDirectories(dir);
                }
            }

            download(next, dir);

            last = next;
        }
    }

    private ALSession login() throws IOException, InterruptedException {
        ALSession session = allSession.getALSession();

        if (session == null || !session.isValid()) {
            String password = "janson2sailly"; //Input.readPassword();

            session = new ALSession(password);
            session.login();
        }

        return session;
    }

    private void download(ALEntry entry, Path dir) throws IOException, InterruptedException, PrepaDLException {
        List<NamedUrl> urls = entry.urls();

        if (urls.size() > 1) {
            String firstName = urls.get(0).text();

            Path subDir;
            if (firstName.equals(entry.name())) {
                subDir = dir.resolve(entry.name());;
            } else {
                subDir = dir.resolve(entry.name() + " - " + firstName);
            }

            for (NamedUrl url : urls) {
                download(url.url(), subDir, null);
            }

        } else {
            NamedUrl url = urls.get(0);

            download(url.url(), dir, url.text());
        }
    }

    private void download(String url, Path dirOut, String trail) throws IOException, InterruptedException, PrepaDLException {
        int dot = url.lastIndexOf('.');
        int slash = url.lastIndexOf('/');

        if (dot < slash) { // found a webpage
            return;
        }

        String name = URLDecoder.decode(url.substring(slash + 1, dot), StandardCharsets.UTF_8);
        String extension = url.substring(dot + 1);

        if (!Files.exists(dirOut)) {
            Files.createDirectories(dirOut);
        }

        Path out;
        if (trail == null || trail.isBlank() || trail.equalsIgnoreCase(name)) {
            out = dirOut.resolve("%s.%s".formatted(name, extension));
        } else {
            out = dirOut.resolve("%s - %s.%s".formatted(name, trail, extension));
        }

        HttpRequest headRequest = HttpRequest.newBuilder(URI.create(url))
                .HEAD()
                .build();

        HttpResponse<Void> headRep = Utils.CLIENT.send(headRequest, HttpResponse.BodyHandlers.discarding());
        long size = headRep.headers().firstValueAsLong("Content-Length").orElse(-1);

        Instant instant = headRep.headers().firstValue("Last-Modified")
                .map(s -> DateTimeFormatter.RFC_1123_DATE_TIME.parse(s, Instant::from))
                .orElse(null);

        download(url, instant, size, out, null);
    }
}
