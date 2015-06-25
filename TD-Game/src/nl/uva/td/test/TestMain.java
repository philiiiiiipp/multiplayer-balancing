package nl.uva.td.test;

import java.util.ArrayList;

public class TestMain {

    public static void main(final String[] args) {
        ArrayList<Integer> a = new ArrayList<Integer>();
        a.add(0);
        a.add(1);

        for (Integer t : a) {
            System.out.print(t + " ");
        }
        System.out.println();
    }
}
