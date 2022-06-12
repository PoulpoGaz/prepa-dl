package fr.poulpogaz.cdpextractor.args;

public interface TypeConverter<T> {

    T convert(String value) throws TypeException;
}
