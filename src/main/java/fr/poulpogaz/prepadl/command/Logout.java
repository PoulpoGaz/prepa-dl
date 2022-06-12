package fr.poulpogaz.prepadl.command;

import fr.poulpogaz.args.api.VoidCommand;
import fr.poulpogaz.prepadl.AllSession;
import fr.poulpogaz.prepadl.cdp.API;
import fr.poulpogaz.prepadl.cdp.CDPException;

import java.io.IOException;

public class Logout implements VoidCommand {

    @Override
    public String getName() {
        return "logout";
    }

    @Override
    public String getUsage() {
        return "Logout from all platforms";
    }

    @Override
    public boolean addHelp() {
        return true;
    }

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
