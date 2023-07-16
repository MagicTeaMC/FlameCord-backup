package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated = true)
abstract class GwtFluentFutureCatchingSpecialization<V> extends AbstractFuture<V> {}
