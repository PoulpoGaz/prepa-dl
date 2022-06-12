package fr.poulpogaz.prepadl;

import fr.poulpogaz.args.api.TypeConverter;
import fr.poulpogaz.args.utils.TypeException;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class OutPathConverter implements TypeConverter<Path> {

    @Override
    public Path convert(String value) throws TypeException {
        if (value == null) {
            return Path.of("");
        }

        try {
            Path p = Path.of(value).toAbsolutePath();

            if (Files.exists(p) && !Files.isDirectory(p)) {
                throw new TypeException("Not a directory: " + value);
            }

            return p;
        } catch (InvalidPathException e) {
            throw new TypeException(e);
        }
    }
}
