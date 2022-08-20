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
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

public class ALIterator {

    private static final String ROOT = "https://anthonylick.com/";

    private final Iterator<Element> iterator;

    private String currentGroup = null;
    private Iterator<Element> groupIterator;

    private ALEntry next;

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

    private void nextGroup() {
        groupIterator = null;
        currentGroup = null;

        while (iterator.hasNext() && groupIterator == null) {
            Element e = iterator.next();

            if (e.is("h4")) {
                currentGroup = removeTrail(e.text());
            } else if (e.is("ul")) {
                groupIterator = e.children().iterator();
            }
        }
    }

    private String getSubCategoryName(Element li) {
        StringBuilder name = new StringBuilder();

        for (int i = 0; i < li.childNodeSize(); i++) {
            Node node = li.childNode(i);

            String toAppend = null;
            if (node instanceof TextNode text) {
                toAppend = text.text();
            } else if (node instanceof Element element && element.is("a")) {
                toAppend = element.text();
            }

            if (toAppend != null && !toAppend.isBlank()) {
                String withoutTrail = removeTrail(toAppend);

                if (withoutTrail.startsWith(" (")) {
                    break;
                } else {
                    name.append(withoutTrail);

                    if (withoutTrail.length() < toAppend.length()) {
                        break;
                    }
                }
            }
        }

        if (name.isEmpty()) {
            return null;
        }

        return name.toString();
    }

    private void fetchNext() {
        next = null;

        while ((iterator.hasNext() || groupIterator.hasNext()) && next == null) {
            if (groupIterator == null || !groupIterator.hasNext()) {
                nextGroup();
                continue;
            }

            Element next = groupIterator.next();

            if (next.is("li")) {
                String subName = getSubCategoryName(next);

                List<NamedUrl> urls = new ArrayList<>();
                for (Element e : next.select("a")) {
                    urls.add(new NamedUrl(e.attr("href"), removeTrail(e.text())));
                }

                this.next = new ALEntry(currentGroup, subName, urls);
            }
        }
    }

    public boolean hasNext() {
        if (next == null) {
            fetchNext();
        }

        return next != null;
    }

    public ALEntry next() throws ALException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        ALEntry curr = next;
        next = null;
        return curr;
    }

    private String removeTrail(String str) {
        return str.replaceFirst("[ :(]*$", "");
    }
}
