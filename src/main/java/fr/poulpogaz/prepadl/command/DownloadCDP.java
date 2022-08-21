package fr.poulpogaz.prepadl.command;

import fr.poulpogaz.prepadl.PrepaDLException;
import fr.poulpogaz.prepadl.cdp.*;
import fr.poulpogaz.prepadl.utils.Input;
import fr.poulpogaz.prepadl.utils.Pair;
import fr.poulpogaz.prepadl.utils.Utils;
import picocli.CommandLine;
import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Command(name = "dl", description = "Download all files from cahier-de-prepa", mixinStandardHelpOptions = true)
public class DownloadCDP extends DownloadCommand {

    @ArgGroup(exclusive = false)
    private MyCDPSession custom;

    @Override
    public void downloadImpl() throws PrepaDLException, IOException, InterruptedException {
        CDPSession session = custom == null ? allSession.getCDPSession() : custom.toCDPSession();
        if (session == null) {
            Pair<String, String> pair = Input.readInput();

            session = API.login(pair.left(), pair.right(), permanentConnection);
        }
        allSession.setCDPSession(session);

        List<CDPRootFolder> folders = API.getRootFolders(session);

        for (CDPRootFolder folder : folders) {
            if (skip != null && Utils.contains(skip, folder.name())) {
                continue;
            }

            walk(session, folder, out);
        }

        if (!permanentConnection) {
            if (allSession.getCDPSession() != null) {
                API.logout(allSession.getCDPSession());
            }

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

            download(fileName, file.lastModified().toInstant(), -1, out, (s) -> API.getInputStream(file, session));

            return null;
        }
    }

    static class MyCDPSession {
        @CommandLine.Option(names = "--cpge")
        private String cpge;

        @CommandLine.Option(names = "--cdp-session")
        private String cdpSession;

        @CommandLine.Option(names = "--cdp-session-perm")
        private String cdpSessionPerm;

        public CDPSession toCDPSession() {
            return new CDPSession(cpge, cdpSession, cdpSessionPerm);
        }
    }
}
