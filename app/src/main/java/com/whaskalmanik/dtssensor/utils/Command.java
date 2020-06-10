package com.whaskalmanik.dtssensor.utils;
import java.util.function.Function;

public interface Command extends Function<Void, Void> {
    @Override
    default Void apply(Void aVoid) {
        apply();
        return null;
    }
    void apply();
}
