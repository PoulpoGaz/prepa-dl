package fr.poulpogaz.prepadl;

import fr.poulpogaz.prepadl.anthonylick.ALSession;
import fr.poulpogaz.prepadl.cdp.CDPSession;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class AllSession {

    public static final Path SESSION_FILE = Path.of(".session");
    public static final AllSession INSTANCE = new AllSession();

    private CDPSession cdpSession;
    private ALSession alSession;

    private AllSession() {
    }

    public void save(Path directory) throws IOException {
        Path out = directory.resolve(SESSION_FILE);

        if (cdpSession != null || alSession != null) {
            ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(out, StandardOpenOption.CREATE));

            oos.writeObject(cdpSession);
            oos.writeObject(alSession);

            oos.close();
        }
    }

    public void load(Path directory) throws IOException {
        Path in = directory.resolve(SESSION_FILE);

        if (Files.exists(in)) {

            try {
                ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(in));

                cdpSession = (CDPSession) ois.readObject();
                alSession = (ALSession) ois.readObject();

                ois.close();
            } catch (ClassNotFoundException | ClassCastException e) {
                throw new IOException(e);
            }
        } else {
            cdpSession = null;
            alSession = null;
        }
    }

    public CDPSession getCDPSession() {
        return cdpSession;
    }

    public void setCDPSession(CDPSession cdpSession) {
        this.cdpSession = cdpSession;
    }

    public ALSession getALSession() {
        return alSession;
    }

    public void setALSession(ALSession alSession) {
        this.alSession = alSession;
    }
}
