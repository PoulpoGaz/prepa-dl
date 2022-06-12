package fr.poulpogaz.cdpextractor.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface IAPI {

    default Session login(String mail, String password) throws IOException, CDPException {
        return login(mail, password, false);
    }

    Session login(String mail, String password, boolean permanentConnection) throws IOException, CDPException;

    List<CDPRootFolder> getRootFolders(Session session) throws IOException, CDPException;

    List<CDPEntry> getContent(CDPFolder folder, Session session) throws IOException, CDPException;

    InputStream getInputStream(CDPFile file, Session session) throws IOException, CDPException;

    void logout(Session session) throws IOException, CDPException;
}
