package fr.poulpogaz.prepadl.utils;

import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;
import org.apache.commons.compress.compressors.deflate.DeflateCompressorInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Utils {

    public static final String USER_AGENT =
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/99.0.4844.84 Safari/537.36 OPR/85.0.4341.47";

    public static final CookieManager COOKIE_MANAGER = new CookieManager();
    public static final CookieStore COOKIE_STORE = COOKIE_MANAGER.getCookieStore();

    public static final HttpClient CLIENT = HttpClient.newBuilder()
            .cookieHandler(COOKIE_MANAGER)
            .build();

    public static Date parseDateDMY(String date) {
        return parseDate(date, "dd/MM/yyyy");
    }

    public static Date parseDate(String date, String format) {
        if (date == null) {
            return null;
        }
        SimpleDateFormat f = new SimpleDateFormat(format);

        try {
            return f.parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    public static int parseSize(String sizeString) {
        String[] parts = sizeString.split("&nbsp;");

        int size = Integer.parseInt(parts[0]);

        switch (parts[1]) {
            case "ko" -> size *= 1024;
            case "Mo" -> size *= 1024 * 1024;
            case "Go" -> size *= 1024 * 1024 * 1024;
            default -> {}
        }

        return size;
    }

    public static <T> boolean contains(T[] array, T o) {
        if (o == null) {
            for (T t : array) {
                if (t == null) {
                    return true;
                }
            }
        } else {
            for (T t : array) {
                if (o.equals(t)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static InputStream decodeInputStream(HttpResponse<InputStream> rep) throws IOException {
        String encoding = rep.headers().firstValue("content-encoding").orElse(null);
        InputStream is = rep.body();

        if (encoding == null) {
            return is;
        } else if (encoding.equals("gzip")) {
            return new GZIPInputStream(is);
        } else if (encoding.equals("br")) {
            return new BrotliCompressorInputStream(is);
        } else if (encoding.equals("deflate")) {
            return new DeflateCompressorInputStream(is);
        } else {
            return null;
        }
    }

    public static HttpResponse<InputStream> send(HttpRequest request) throws IOException, InterruptedException {
        HttpResponse<InputStream> rep = CLIENT.send(request, HttpResponse.BodyHandlers.ofInputStream());

        return new HttpResponseDecoded(rep);
    }
}
