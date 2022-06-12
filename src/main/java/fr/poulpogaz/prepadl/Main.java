package fr.poulpogaz.prepadl;

import fr.poulpogaz.args.CommandLine;
import fr.poulpogaz.args.CommandLineBuilder;
import fr.poulpogaz.args.utils.CommandLineException;
import fr.poulpogaz.prepadl.command.DownloadAL;
import fr.poulpogaz.prepadl.command.DownloadCDP;
import fr.poulpogaz.prepadl.command.Logout;
import fr.poulpogaz.prepadl.utils.Version;

import java.io.IOException;
import java.nio.file.Path;

public class Main  {

    public static void main(String[] args) {
        Path dir = Path.of("");

        try {
            AllSession.INSTANCE.load(dir);

            CommandLine cli = new CommandLineBuilder()
                    .addDefaultConverters()
                    .addCommand(new Version())
                    .addCommand(new Logout())
                    .addCommand(new DownloadCDP())
                    .addCommand(new DownloadAL())
                    .build();

            cli.execute(args);

            AllSession.INSTANCE.save(dir);
        } catch (CommandLineException | IOException e) {
            e.printStackTrace();
        }
    }
}
