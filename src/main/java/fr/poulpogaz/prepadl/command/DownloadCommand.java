package fr.poulpogaz.prepadl.command;

import fr.poulpogaz.args.api.Option;
import fr.poulpogaz.args.api.VaArgs;
import fr.poulpogaz.args.api.VoidCommand;
import fr.poulpogaz.prepadl.AllSession;
import fr.poulpogaz.prepadl.OutPathConverter;
import fr.poulpogaz.prepadl.PrepaDLException;
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
import java.nio.file.attribute.FileTime;
import java.time.Instant;

public abstract class DownloadCommand implements VoidCommand  {

    protected AllSession allSession = AllSession.INSTANCE;

    @Option(names = {"-output", "o"}, argName = "output", hasArgument = true, converter = OutPathConverter.class)
    protected Path out;

    @Option(names = {"-permanent-connection", "p"}, defaultValue = "false")
    protected boolean permanentConnection;

    @Option(names = {"-intelligent-copy", "i"}, defaultValue = "false")
    protected boolean intelligentCopy;

    @VaArgs
    protected String[] skip;

    @Override
    public void run() {
        try {
            if (!Files.exists(out)) {
                Files.createDirectories(out);
            }

            downloadImpl();
        } catch (IOException | PrepaDLException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    protected abstract void downloadImpl() throws IOException, PrepaDLException, InterruptedException;

    protected void download(String url,
                            Instant lastModifiedTime, long size,
                            Path dest,
                            InputStreamFactory inputStreamFactory)
            throws IOException, InterruptedException, PrepaDLException {
        if (!intelligentCopy || canReplace(dest, lastModifiedTime, size)) {
            System.out.println("Downloading " + dest.getFileName());

            InputStream is;
            if (inputStreamFactory != null) {
                is = inputStreamFactory.createInputStream(url);
            } else {
                HttpRequest req = HttpRequest.newBuilder(URI.create(url)).build();

                HttpResponse<InputStream> rep = Utils.CLIENT.send(req, HttpResponse.BodyHandlers.ofInputStream());
                is = rep.body();
            }

            OutputStream os = new BufferedOutputStream(Files.newOutputStream(dest));
            is.transferTo(os);
            os.close();
            is.close();

            if (lastModifiedTime != null) {
                Files.setLastModifiedTime(dest, FileTime.from(lastModifiedTime));
            }
        } else {
            System.out.println(dest.getFileName() + " is up to date");
        }
    }

    private boolean canReplace(Path dest, Instant lastModifiedTime, long size) throws IOException {
        if (Files.exists(dest)) {
            long localSize = Files.size(dest);
            Instant localLastModifiedTime = Files.getLastModifiedTime(dest).toInstant();

            if (size >= 0 && localSize != size) {
                return false;
            }

            return !localLastModifiedTime.equals(lastModifiedTime);
        }

        return true;
    }
}
