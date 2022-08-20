package fr.poulpogaz.prepadl.utils;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;
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

        System.out.println(root + "?" + b);

        if (b.isEmpty()) {
            return HttpRequest.newBuilder(URI.create(root));
        } else {
            return HttpRequest.newBuilder(URI.create(root + '?' + b));
        }
    }

    public String build() {
        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < values.size() - 1; i++) {
            add(builder, values.get(i));

            builder.append('&');
        }

        if (values.size() > 0) {
            add(builder, values.get(values.size() - 1));
        }

        return builder.toString();
    }

    private void add(StringBuilder sb, Pair<String, String> pair) {
        sb.append(encode(pair.left()))
                .append('=')
                .append(encode(pair.right()));
    }

    private String encode(String str) {
        return URLEncoder.encode(str, StandardCharsets.UTF_8);
    }
}
