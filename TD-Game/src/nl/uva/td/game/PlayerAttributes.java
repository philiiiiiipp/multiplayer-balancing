package nl.uva.td.game;

public class PlayerAttributes {

    public enum UpgradeType {
        NONE,
        HEALTH,
        MOVEMENT,
        AMOUNT
    }

    private static final double HEALTH_INCREASE_PER_UPDATE = 0.7;

    private static final double MOVEMENT_INCREASE_PER_UPDATE = 0.4;

    private static final double CREEP_AMOUNT_INCREASE_PER_UPDATE = 0.75;

    private int mLives;

    private double mAmountOfCreeps = 1;

    private double mHealth = 1;

    private double mMovement = 1;

    public PlayerAttributes(final int lives) {
        mLives = lives;
    }

    public int getLives() {
        return mLives;
    }

    public void setLives(final int lives) {
        mLives = lives;
    }

    public void upgrade(final UpgradeType type) {
        switch (type) {
        case HEALTH:
            upgradeHealth();
            break;
        case MOVEMENT:
            upgradeMovement();
            break;
        case AMOUNT:
            upgradeAmount();
            break;
        default:
            throw new RuntimeException("Unsupported Upgrade type");
        }
    }

    private void upgradeHealth() {
        mHealth += HEALTH_INCREASE_PER_UPDATE;
    }

    private void upgradeMovement() {
        mMovement += MOVEMENT_INCREASE_PER_UPDATE;
    }

    private void upgradeAmount() {
        mAmountOfCreeps += CREEP_AMOUNT_INCREASE_PER_UPDATE;
    }

    public double getHealth() {
        return mHealth;
    }

    public double getMovement() {
        return mMovement;
    }

    public int getAmountOfCreeps() {
        return (int) mAmountOfCreeps;
    }
}
