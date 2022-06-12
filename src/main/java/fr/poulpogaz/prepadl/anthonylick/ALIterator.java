package fr.poulpogaz.prepadl.anthonylick;

import fr.poulpogaz.prepadl.PrepaDLException;
import fr.poulpogaz.prepadl.utils.NamedUrl;
import fr.poulpogaz.prepadl.utils.Utils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ALIterator {

    private static final String ROOT = "https://anthonylick.com/";

    private final Iterator<Element> iterator;

    private String currentGroup = null;
    private Iterator<Element> inner;

    public ALIterator(ALSession session, String url) throws PrepaDLException, IOException, InterruptedException {
        if (!url.startsWith("https://anthonylick.com/")) {
            throw new ALException("Invalid url");
        }

        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url));
        session.setHeader(builder);

        HttpResponse<InputStream> response =
                Utils.CLIENT.send(builder.build(), HttpResponse.BodyHandlers.ofInputStream());

        Document doc = Jsoup.parse(response.body(), "UTF-8", ROOT);

        Files.writeString(Path.of("out.html"), doc.html(), StandardOpenOption.CREATE);

        iterator = doc.select(".entry-content > *").iterator();
    }

    public boolean hasNext() {
        return iterator.hasNext() || (inner != null && inner.hasNext());
    }

    public ALEntry next() throws ALException {
        nextGroup();

        Element e = inner.next();

        String name = getName(e);

        List<NamedUrl> url = new ArrayList<>();
        for (Element a : e.select("a")) {
            url.add(new NamedUrl(a.attr("href"), a.text()));
        }

        ALEntry entry = new ALEntry(currentGroup, name, Collections.unmodifiableList(url));

        if (!inner.hasNext()) {
            inner = null;
            currentGroup = null;
        }

        return entry;
    }

    private void nextGroup() throws ALException {
        if (currentGroup == null) {
            Element e;
            do {
                e = iterator.next();

                if (e.is("h4")) {
                    currentGroup = e.text();
                } else {
                    throw new ALException("Not h4");
                }

                e = iterator.next();

            } while (!e.is("ul"));

            inner = e.select("ul > li").iterator();
        }
    }

    private String getName(Element element) {
        List<Node> nodes = element.childNodes();

        for (Node node : nodes) {
            String text;

            if (node instanceof TextNode t) {
                text = t.text();
            } else if (node instanceof Element e) {
                text = e.text();
            } else {
                continue;
            }

            if (!text.isBlank()) {
                return text;
            }
        }

        return null;
    }
}
