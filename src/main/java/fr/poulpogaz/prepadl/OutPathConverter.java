package fr.poulpogaz.prepadl;

import picocli.CommandLine;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;

public class OutPathConverter implements CommandLine.ITypeConverter<Path> {

    @Override
    public Path convert(String value) {
        if (value == null) {
            return Path.of("");
        }

        try {
            Path p = Path.of(value).toAbsolutePath();

            if (Files.exists(p) && !Files.isDirectory(p)) {
                throw new CommandLine.TypeConversionException("Not a directory: " + value);
            }

            return p;
        } catch (InvalidPathException e) {
            throw new CommandLine.TypeConversionException(e.getMessage());
        }
    }
}
