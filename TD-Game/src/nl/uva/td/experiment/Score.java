package nl.uva.td.experiment;

public class Score {

    private final double mPoints;

    public Score(final double points) {
        mPoints = points;
    }

    /**
     * Get the points for the last run
     * 
     * @return The points archeived
     */
    public double getPoints() {
        return mPoints;
    }

}
