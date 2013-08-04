package ru.andreymarkelov.atlas.plugins.datacollector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.andreymarkelov.atlas.plugins.datacollector.struct.DateRange;

public class RangeUtils {
    public static DateRange getIntersectionRange(DateRange dr1, DateRange dr2) {
        Date minEnd = min(dr1.getTo(), dr2.getTo());
        Date maxStart = max(dr1.getFrom(), dr2.getFrom());
        long res = minEnd.getTime() - maxStart.getTime();
        if (res > 0) {
            return new DateRange(maxStart, minEnd);
        }
        return null;
    }

    public static List<DateRange> intersectRanges(List<DateRange> r1, List<DateRange> r2) {
        List<DateRange> r = new ArrayList<DateRange>();

        for (DateRange dr1 : r1) {
            for (DateRange dr2 : r2) {
                DateRange idr = getIntersectionRange(dr1, dr2);
                if (idr != null) {
                    r.add(idr);
                }
            }
        }

        return r;
    }

    public static boolean isIntersect(DateRange dr1, DateRange dr2) {
        return getIntersectionRange(dr1, dr2) != null;
    }

    private static Date max(Date d1, Date d2) {
        return new Date(Math.max(d1.getTime(), d2.getTime()));
    }

    private static Date min(Date d1, Date d2) {
        return new Date(Math.min(d1.getTime(), d2.getTime()));
    }

    private RangeUtils() {
    }
}
