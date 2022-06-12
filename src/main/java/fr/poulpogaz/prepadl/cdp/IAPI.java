package fr.poulpogaz.prepadl.cdp;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IAPI {

    default CDPSession login(String mail, String password) throws IOException, CDPException {
        return login(mail, password, false);
    }

    CDPSession login(String mail, String password, boolean permanentConnection) throws IOException, CDPException;

    List<CDPRootFolder> getRootFolders(CDPSession session) throws IOException, CDPException;

    List<CDPEntry> getContent(CDPFolder folder, CDPSession session) throws IOException, CDPException;

    InputStream getInputStream(CDPFile file, CDPSession session) throws IOException, CDPException;

    void logout(CDPSession session) throws IOException, CDPException;
}
