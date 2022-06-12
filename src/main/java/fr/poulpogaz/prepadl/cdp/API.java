package fr.poulpogaz.prepadl.cdp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class API {

    private static final IAPI api = new APIImpl();

    public static CDPSession login(String mail, String password) throws IOException, CDPException {
        return api.login(mail, password);
    }

    public static CDPSession login(String mail, String password, boolean permanentConnection) throws IOException, CDPException {
        return api.login(mail, password, permanentConnection);
    }

    public static List<CDPRootFolder> getRootFolders(CDPSession session) throws IOException, CDPException {
        return api.getRootFolders(session);
    }

    public static List<CDPEntry> getContent(CDPFolder folder, CDPSession session) throws IOException, CDPException {
        return api.getContent(folder, session);
    }

    public static InputStream getInputStream(CDPFile file, CDPSession session) throws IOException, CDPException {
        return api.getInputStream(file, session);
    }

    public static void logout(CDPSession session) throws IOException, CDPException {
        api.logout(session);
    }
}
