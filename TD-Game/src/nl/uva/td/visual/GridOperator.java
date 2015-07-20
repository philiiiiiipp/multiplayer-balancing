package nl.uva.td.visual;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.IntBinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * @author jpereda
 */
public class GridOperator {

    // public static final int DEFAULT_GRID_SIZE = 5;

    private final int gridSizeX;
    private final int gridSizeY;
    private final List<Integer> traversalX;
    private final List<Integer> traversalY;

    // public GridOperator() {
    // this(DEFAULT_GRID_SIZE);
    // }

    public GridOperator(final int gridSizeX, final int gridSizeY) {
        this.gridSizeX = gridSizeX;
        this.gridSizeY = gridSizeY;
        this.traversalX = IntStream.range(0, gridSizeX).boxed().collect(Collectors.toList());
        this.traversalY = IntStream.range(0, gridSizeY).boxed().collect(Collectors.toList());
    }

    public void sortGrid(final Direction direction) {
        Collections.sort(traversalX, direction.equals(Direction.RIGHT) ? Collections.reverseOrder()
                : Integer::compareTo);
        Collections
                .sort(traversalY, direction.equals(Direction.DOWN) ? Collections.reverseOrder() : Integer::compareTo);
    }

    public int traverseGrid(final IntBinaryOperator func) {
        AtomicInteger at = new AtomicInteger();
        traversalX.forEach(t_x -> {
            traversalY.forEach(t_y -> {
                at.addAndGet(func.applyAsInt(t_x, t_y));
            });
        });

        return at.get();
    }

    public int getGridSizeX() {
        return gridSizeX;
    }

    public int getGridSizeY() {
        return gridSizeY;
    }

    public boolean isValidLocation(final Location loc) {
        return loc.getX() >= 0 && loc.getX() < gridSizeX && loc.getY() >= 0 && loc.getY() < gridSizeY;
    }

}
