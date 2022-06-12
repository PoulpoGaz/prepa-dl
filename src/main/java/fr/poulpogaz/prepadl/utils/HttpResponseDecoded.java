package fr.poulpogaz.prepadl.utils;

import javax.net.ssl.SSLSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Optional;

public class HttpResponseDecoded implements HttpResponse<InputStream> {

    private final HttpResponse<InputStream> response;
    private final InputStream decoded;

    public HttpResponseDecoded(HttpResponse<InputStream> response) throws IOException {
        this.response = response;
        this.decoded = Utils.decodeInputStream(response);
    }

    @Override
    public int statusCode() {
        return response.statusCode();
    }

    @Override
    public HttpRequest request() {
        return response.request();
    }

    @Override
    public Optional<HttpResponse<InputStream>> previousResponse() {
        return response.previousResponse();
    }

    @Override
    public HttpHeaders headers() {
        return response.headers();
    }

    @Override
    public InputStream body() {
        return decoded;
    }

    @Override
    public Optional<SSLSession> sslSession() {
        return response.sslSession();
    }

    @Override
    public URI uri() {
        return response.uri();
    }

    @Override
    public HttpClient.Version version() {
        return response.version();
    }

    public HttpResponse<InputStream> getResponse() {
        return response;
    }
}
