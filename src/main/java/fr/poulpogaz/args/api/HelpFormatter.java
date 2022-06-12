package fr.poulpogaz.args.api;

import fr.poulpogaz.args.utils.CommandLineException;
import fr.poulpogaz.args.utils.INode;

/**
 * Produce help string when the
 * user doesn't properly use a command
 * @author PoulpoGaz
 */
public interface HelpFormatter {

    String commandHelp(CommandLineException error,
                       CommandDescriber command);

    String generalHelp(INode<CommandDescriber> commands,
                       String[] args,
                       boolean unrecognizedCommand);
}
