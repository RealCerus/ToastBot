/*
 * Copyright (c) 2019 Cerus
 * File created at 18.04.19 16:51
 * Last modification: 18.04.19 16:51
 * All rights reserved.
 */

package de.cerus.toastbot.util;

@FunctionalInterface
public interface TriConsumer<A, B, C> {

    void accept(A a, B b, C c);

}
