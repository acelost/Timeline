package com.acelost.android.timeline.util;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

import static com.acelost.android.timeline.Preconditions.checkNotNull;

public final class TimeUnitUtil {

    @NonNull
    public static TimeUnit minUnit(@NonNull final TimeUnit unit1, @NonNull final TimeUnit unit2) {
        final long unit1Count = unit1.convert(1, TimeUnit.DAYS);
        final long unit2Count = unit2.convert(1, TimeUnit.DAYS);
        return unit1Count > unit2Count ? unit1 : unit2;
    }

    public static boolean before(final long value, @NonNull final TimeUnit valueUnits,
                                 final long other, @NonNull final TimeUnit otherUnits) {
        checkNotNull(valueUnits);
        checkNotNull(otherUnits);
        final TimeUnit minUnit = minUnit(valueUnits, otherUnits);
        return minUnit.convert(value, valueUnits) < minUnit.convert(other, otherUnits);
    }

    public static boolean after(final long value, @NonNull final TimeUnit valueUnits,
                                final long other, @NonNull final TimeUnit otherUnits) {
        checkNotNull(valueUnits);
        checkNotNull(otherUnits);
        final TimeUnit minUnit = minUnit(valueUnits, otherUnits);
        return minUnit.convert(value, valueUnits) > minUnit.convert(other, otherUnits);
    }

}
