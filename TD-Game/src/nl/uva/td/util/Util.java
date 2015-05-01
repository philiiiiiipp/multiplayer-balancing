package nl.uva.td.util;

import java.util.List;
import java.util.Random;
import java.util.Set;

public class Util {

    public static final Random RND = new Random();

    public static <T> T randomObject(final List<T> list) {
        return list.get(RND.nextInt(list.size()));
    }

    public static <T> T removeRandomObject(final List<T> list) {
        int position = RND.nextInt(list.size());

        return list.remove(position);
    }

    public static void print(final List<Object> list) {
        for (Object o : list) {
            System.out.print(o);
        }
    }

    public static void print(final Set<Object> list) {
        for (Object o : list) {
            System.out.print(o);
        }
    }
}
