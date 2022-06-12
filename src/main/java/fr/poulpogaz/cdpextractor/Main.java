package fr.poulpogaz.cdpextractor;

import fr.poulpogaz.cdpextractor.api.*;
import fr.poulpogaz.cdpextractor.args.CommandLine;
import fr.poulpogaz.cdpextractor.args.CommandLineException;
import fr.poulpogaz.cdpextractor.args.annotation.Command;
import fr.poulpogaz.cdpextractor.args.annotation.Option;
import fr.poulpogaz.cdpextractor.args.annotation.VaArgs;
import fr.poulpogaz.cdpextractor.utils.Pair;
import fr.poulpogaz.cdpextractor.utils.Utils;
import fr.poulpogaz.cdpextractor.utils.Version;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;

@Command(name = "")
public class Main implements Runnable {

    private static final String VERSION = "1.0";

    public static void main(String[] args) {
        try {
            CommandLine cmd = new CommandLine()
                    .addDefaultConverters()
                    .addConverter(Path.class, new OutPathConverter())
                    .addCommand(new Main());

            cmd.parse(args);
        } catch (CommandLineException e) {
            e.printStackTrace();
        }
    }

    @Option(names = {"-version"})
    private boolean version;

    @Option(names = {"-output", "o"}, argName = "output", converter = OutPathConverter.class)
    private Path out;

    @Option(names = {"-permanent-connection", "p"}, defaultValue = "false")
    private boolean permanentConnection;

    @Option(names = {"-intelligent-copy", "i"})
    private boolean intelligentCopy;

    @Option(names = {"-logout"})
    private boolean logout;

    @VaArgs
    private String[] skip;

    @Override
    public void run() {
        if (version) {
            Version.showVersion();
            return;
        }

        try {
            if (logout) {
                Session session = Session.read();

                if (session != null) {
                    API.logout(session);
                    Files.delete(Session.SESSION_FILE);
                }
                return;
            }


            if (!Files.exists(out)) {
                Files.createDirectories(out);
            }

            Session session = getSession();
            List<CDPRootFolder> folders = API.getRootFolders(session);

            for (CDPRootFolder folder : folders) {
                if (Utils.contains(skip, folder.name())) {
                    continue;
                }

                walk(session, folder, out);
            }

            if (permanentConnection) {
                session.save();
            } else {
                API.logout(session);
            }
        } catch (IOException | CDPException e) {
            e.printStackTrace();
        }
    }

    private void walk(Session session, CDPEntry entry, Path position) throws CDPException, IOException {
        Path descendantPos = download(session, entry, position);

        if (entry.isFolder()) {
            CDPFolder folder = (CDPFolder) entry;

            for (CDPEntry sub : API.getContent(folder, session)) {
                walk(session, sub, descendantPos);
            }
        }
    }

    private Path download(Session session, CDPEntry entry, Path position) throws IOException, CDPException {
        if (entry.isFolder()) {
            Path out = position.resolve(entry.name());

            if (!Files.exists(out)) {
                Files.createDirectory(out);
            }

            return out;
        } else {
            CDPFile file = (CDPFile) entry;

            String fileName = entry.name() + '.' + file.fileType();
            Path out = position.resolve(fileName);

            if (replace(out, file) || !intelligentCopy) {
                System.out.println("Downloading " + fileName);

                InputStream is = API.getInputStream(file, session);
                OutputStream os = new BufferedOutputStream(Files.newOutputStream(out));
                is.transferTo(os);
                os.close();
                is.close();

                FileTime ft = FileTime.from(file.lastModified().toInstant());
                Files.setLastModifiedTime(out, ft);
            } else {
                System.out.println(fileName + " is up to date");
            }

            return null;
        }
    }

    private boolean replace(Path out, CDPFile file) throws IOException {
        if (Files.exists(out)) {
            Instant fileInstant = Files.getLastModifiedTime(out).toInstant();
            Instant cdpInstant = file.lastModified().toInstant();

            return fileInstant.compareTo(cdpInstant) != 0;
        }

        return true;
    }

    private Session getSession() throws IOException, CDPException {
        if (permanentConnection) {
            try {
                Session session = Session.read();

                if (session != null) {
                    return session;
                }
            } catch (IOException e) {
                // ignored
            }
        }

        Pair<String, String> ids = Utils.readInput();

        return API.login(ids.left(), ids.right(), permanentConnection);
    }
}
