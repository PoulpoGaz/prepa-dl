package fr.poulpogaz.prepadl.command;

import fr.poulpogaz.prepadl.AllSession;
import fr.poulpogaz.prepadl.cdp.API;
import fr.poulpogaz.prepadl.cdp.CDPException;
import picocli.CommandLine;

import java.io.IOException;

@CommandLine.Command(name = "logout", description = "Logout from all platforms")
public class Logout implements Runnable {

    @Override
    public void run() {
        try {
            AllSession sessions = AllSession.INSTANCE;

            if (sessions.getCDPSession() != null) {
                API.logout(sessions.getCDPSession());

                System.out.println("You have been successfully logged out");
            } else {
                System.out.println("You are not connected");
            }

            sessions.setCDPSession(null);
            sessions.setALSession(null);
        } catch (IOException | CDPException e) {
            e.printStackTrace();
        }
    }
}
