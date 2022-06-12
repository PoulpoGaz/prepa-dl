package fr.poulpogaz.cdpextractor.utils;

import java.net.URI;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpHeaders {

    private final Map<String, List<String>> headers = new HashMap<>();

    public HttpHeaders() {

    }

    public HttpHeaders setHeader(String header, String value) {
        List<String> list = Utils.getOrCreate(headers, header, ArrayList::new);
        list.clear();
        list.add(value);
        return this;
    }

    public HttpHeaders header(String header, String value) {
        List<String> list = Utils.getOrCreate(headers, header, ArrayList::new);
        list.add(value);
        return this;
    }

    public HttpHeaders headers(String... headers) {
        if (headers.length % 2 != 0) {
            throw new IllegalStateException("Wrong number of headers (%d)".formatted(headers.length));
        }

        for (int i = 0; i < headers.length; i += 2) {
            header(headers[i], headers[i + 1]);
        }

        return this;
    }

    public void apply(HttpRequest.Builder builder) {
        for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
            for (String headerValue : entry.getValue()) {
                builder.header(entry.getKey(), headerValue);
            }
        }
    }
    
    public HttpRequest.Builder request(String url) {
        HttpRequest.Builder builder = HttpRequest.newBuilder(URI.create(url));
        apply(builder);

        return builder;
    }
}
