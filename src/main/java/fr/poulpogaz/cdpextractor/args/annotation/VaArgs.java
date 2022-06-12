package fr.poulpogaz.cdpextractor.args.annotation;

import fr.poulpogaz.cdpextractor.args.TypeConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface VaArgs {

    Class<? extends TypeConverter<?>>[] converter() default {};
}