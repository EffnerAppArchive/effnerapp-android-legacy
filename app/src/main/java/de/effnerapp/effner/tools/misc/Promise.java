/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 20.06.21, 20:06.
 * Copyright (c) 2021 EffnerApp.
 */

package de.effnerapp.effner.tools.misc;

public interface Promise<T, E> {
    void accept(T t);
    void reject(E e);
}
