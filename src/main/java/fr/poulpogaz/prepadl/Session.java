package fr.poulpogaz.prepadl;

import java.io.Serializable;
import java.net.http.HttpRequest;

public interface Session extends Serializable {

    HttpRequest.Builder setHeader(HttpRequest.Builder request) throws PrepaDLException;

    boolean isValid();
}
