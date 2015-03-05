package nl.uva.td.game;

public class StepResult {

    /** Describes the extra salary earned this round */
    private double mExtraSalary = 0;

    /**
     * Adds extra salary increase
     * 
     * @param salaryIncrease
     *            The salary to be increased by
     */
    public void addExtraSalary(final double salaryIncrease) {
        mExtraSalary += salaryIncrease;
    }

    /**
     * Get the extra salary increase earned this round
     * 
     * @return The extra salary increase
     */
    public double getExtraSalary() {
        return mExtraSalary;
    }

}
