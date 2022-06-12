package fr.poulpogaz.prepadl.command;

import fr.poulpogaz.prepadl.PrepaDLException;
import fr.poulpogaz.prepadl.anthonylick.ALEntry;
import fr.poulpogaz.prepadl.anthonylick.ALIterator;
import fr.poulpogaz.prepadl.anthonylick.ALSession;
import fr.poulpogaz.prepadl.utils.Input;
import fr.poulpogaz.prepadl.utils.NamedUrl;
import fr.poulpogaz.prepadl.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
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
    public void downloadImpl() throws IOException, PrepaDLException {
        try {
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



        } catch (InterruptedException e) {
            throw new IOException(e);
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

    private void download(ALEntry entry, Path dir) throws IOException, InterruptedException {
        List<NamedUrl> urls = entry.urls();

        if (urls.size() > 1) {
            Path subDir = dir.resolve(entry.name());

            for (NamedUrl url : urls) {
                download(url.url(), subDir, null);
            }

        } else {
            NamedUrl url = urls.get(0);

            download(url.url(), dir, url.text());
        }
    }

    private void download(String url, Path dirOut, String overrideName) throws IOException, InterruptedException {
        if (!Files.exists(dirOut)) {
            Files.createDirectories(dirOut);
        }

        Path out;

        if (overrideName != null) {
            out = dirOut.resolve(overrideName);
        } else {
            int i = url.lastIndexOf('/');
            String name = url.substring(i + 1);

            out = dirOut.resolve(name);
        }

        System.out.println("Downloading: " + url + " - " + out);

        HttpRequest req = HttpRequest.newBuilder(URI.create(url)).build();

        HttpResponse<InputStream> rep = Utils.CLIENT.send(req, HttpResponse.BodyHandlers.ofInputStream());
        InputStream is = rep.body();

        OutputStream os = new BufferedOutputStream(Files.newOutputStream(out));
        is.transferTo(os);
        os.close();
        is.close();
    }
}
