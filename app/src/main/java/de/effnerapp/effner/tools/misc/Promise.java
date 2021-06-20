package de.effnerapp.effner.tools.misc;

public interface Promise<T, E> {
    void accept(T t);
    void reject(E e);
}
