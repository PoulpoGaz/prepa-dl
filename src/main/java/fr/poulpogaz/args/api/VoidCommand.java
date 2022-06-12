package fr.poulpogaz.args.api;

/**
 * @author PoulpoGaz
 */
public interface VoidCommand extends Runnable, Command<Void> {

    @Override
    default Void execute() {
        run();
        return null;
    }
}