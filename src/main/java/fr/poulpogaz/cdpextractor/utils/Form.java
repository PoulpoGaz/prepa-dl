package fr.poulpogaz.cdpextractor.utils;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;

public class Form {

    private final List<Pair<String, String>> values;

    public Form() {
        values = new ArrayList<>();
    }

    public Form add(String key, String value) {
        values.add(new Pair<>(key, value));

        return this;
    }

    public HttpRequest.Builder createRequest(String root) {
        String b = build();

        if (b.isEmpty()) {
            return HttpRequest.newBuilder(URI.create(root));
        } else {
            return HttpRequest.newBuilder(URI.create(root + '?' + b));
        }
    }

    public String build() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < values.size() - 1; i++) {
            Pair<String, String> p = values.get(i);

            builder.append(p.left())
                    .append('=')
                    .append(p.right())
                    .append('&');
        }

        if (values.size() > 0) {
            Pair<String, String> p = values.get(values.size() - 1);

            builder.append(p.left())
                    .append('=')
                    .append(p.right());
        }

        return builder.toString();
    }
}
