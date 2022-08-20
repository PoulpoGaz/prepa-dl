package fr.poulpogaz.prepadl.command;

import fr.poulpogaz.prepadl.PrepaDLException;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamFactory {

    InputStream createInputStream(String url) throws IOException, InterruptedException, PrepaDLException;
}
