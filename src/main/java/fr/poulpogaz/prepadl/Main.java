package fr.poulpogaz.prepadl;

import fr.poulpogaz.prepadl.command.DownloadAL;
import fr.poulpogaz.prepadl.command.DownloadCDP;
import fr.poulpogaz.prepadl.command.Logout;
import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Path;

@CommandLine.Command(name = "prepa-dl",
        mixinStandardHelpOptions = true,
        version = {"prepa-dl: 1.1-dev",
                "JVM: ${java.version} (${java.vendor} ${java.vm.name} ${java.vm.version})",
                "OS: ${os.name} ${os.version} ${os.arch}"})
public class Main  {

    public static void main(String[] args) {
        Path dir = Path.of("");

        try {
            AllSession.INSTANCE.load(dir);

            CommandLine cli = new CommandLine(new Main());
            cli.addSubcommand(new DownloadAL());
            cli.addSubcommand(new DownloadCDP());
            cli.addSubcommand(new Logout());

            cli.execute(args);

            AllSession.INSTANCE.save(dir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
