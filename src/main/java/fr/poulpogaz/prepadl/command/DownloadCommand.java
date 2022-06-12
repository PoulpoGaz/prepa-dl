package fr.poulpogaz.prepadl.command;

import fr.poulpogaz.args.api.Option;
import fr.poulpogaz.args.api.VaArgs;
import fr.poulpogaz.args.api.VoidCommand;
import fr.poulpogaz.prepadl.AllSession;
import fr.poulpogaz.prepadl.OutPathConverter;
import fr.poulpogaz.prepadl.PrepaDLException;
import fr.poulpogaz.prepadl.cdp.CDPException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
        } catch (IOException | PrepaDLException e) {
            e.printStackTrace();
        }
    }

    protected abstract void downloadImpl() throws IOException, PrepaDLException;
}
