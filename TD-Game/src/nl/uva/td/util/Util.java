package nl.uva.td.util;

import java.util.List;
import java.util.Random;

public class Util {

    public static final Random RND = new Random();

    public static <T> T randomObject(final List<T> list) {
        return list.get(RND.nextInt(list.size()));
    }

    public static <T> T removeRandomObject(final List<T> list) {
        int position = RND.nextInt(list.size());

        return list.remove(position);
    }

}
