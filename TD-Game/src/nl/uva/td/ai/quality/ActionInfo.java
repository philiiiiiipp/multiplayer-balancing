package nl.uva.td.ai.quality;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;

public class ActionInfo {

    private final static NumberFormat sNumForm = NumberFormat.getPercentInstance();

    private final static DecimalFormat sDecimalFormat = new DecimalFormat("#.###");

    public final int action;

    public final boolean isHuman;

    public int winCounter;

    public int drawCounter;

    public int loseCounter;

    public double score;

    public ActionInfo(final int action, final Race.Type type) {
        this.action = action;
        this.isHuman = type == Race.Type.HUMAN;
    }

    @Override
    public String toString() {
        double total = total();

        int norm = 13;
        int norm2 = 10;
        return f(new Decision(this.action, Race.getRaceForType(getType())).toString(), 4) + " " + this.action + "  \t"
                + f("Win: " + this.winCounter, norm) + f(" Draw: " + this.drawCounter, norm)
                + f(" Lose: " + this.loseCounter, norm)
                + f(" \tWin: " + sNumForm.format(this.winCounter / total), norm2)
                + f(" Draw: " + sNumForm.format(this.drawCounter / total), norm2)
                + f(" Lose: " + sNumForm.format(this.loseCounter / total), norm2) + "   \t" + getType() + " Score: "
        + sDecimalFormat.format(this.score) + " Norm: "
        + sDecimalFormat.format((this.score / (winCounter + loseCounter + drawCounter)));
    }

    private String f(final String s, final int preferedLength) {
        String result = s;
        while (result.length() < preferedLength) {
            result += " ";
        }

        return result;
    }

    public double winProb() {
        return (winCounter / (double) total());
    }

    public double drawProb() {
        return (drawCounter / (double) total());
    }

    public double loseProb() {
        return (loseCounter / (double) total());
    }

    public int total() {
        return this.drawCounter + this.winCounter + this.loseCounter;
    }

    public Race.Type getType() {
        if (this.isHuman) {
            return Race.Type.HUMAN;
        } else {
            return Race.Type.ALIEN;
        }
    }

}
