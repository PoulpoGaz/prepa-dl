package fr.poulpogaz.prepadl.command;

import fr.poulpogaz.prepadl.PrepaDLException;
import fr.poulpogaz.prepadl.cdp.*;
import fr.poulpogaz.prepadl.utils.Input;
import fr.poulpogaz.prepadl.utils.Pair;
import fr.poulpogaz.prepadl.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.List;

public class DownloadCDP extends DownloadCommand {

    @Override
    public String getName() {
        return "dl";
    }

    @Override
    public String getUsage() {
        return "Download all files from cahier-de-prepa";
    }

    @Override
    public boolean addHelp() {
        return false;
    }

    @Override
    public void downloadImpl() throws PrepaDLException, IOException, InterruptedException {
        CDPSession session = allSession.getCDPSession();
        if (session == null) {
            Pair<String, String> pair = Input.readInput();

            session = API.login(pair.left(), pair.right(), permanentConnection);
        }

        List<CDPRootFolder> folders = API.getRootFolders(session);

        for (CDPRootFolder folder : folders) {
            if (Utils.contains(skip, folder.name())) {
                continue;
            }

            walk(session, folder, out);
        }

        if (!permanentConnection) {
            API.logout(session);
            allSession.setCDPSession(null);
        }
    }

    private void walk(CDPSession session, CDPEntry entry, Path position) throws PrepaDLException, IOException, InterruptedException {
        Path descendantPos = download(session, entry, position);

        if (entry.isFolder()) {
            CDPFolder folder = (CDPFolder) entry;

            for (CDPEntry sub : API.getContent(folder, session)) {
                walk(session, sub, descendantPos);
            }
        }
    }

    private Path download(CDPSession session, CDPEntry entry, Path position) throws IOException, PrepaDLException, InterruptedException {
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

            download(null, file.lastModified().toInstant(), -1, out, (s) -> API.getInputStream(file, session));

            /*if (!intelligentCopy || replace(out, file)) {
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
            }*/

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
}
