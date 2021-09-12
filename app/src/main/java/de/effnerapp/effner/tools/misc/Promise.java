/*
 * Developed by Sebastian Müller and Luis Bros.
 * Last updated: 12.09.21, 19:48.
 * Copyright (c) 2021 EffnerApp.
 *
 */

package de.effnerapp.effner.tools.misc;

public interface Promise<T, E> {
    void accept(T t);
    void reject(E e);
}
