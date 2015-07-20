package nl.uva.td.ai.quality;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import nl.uva.td.game.agent.Decision;
import nl.uva.td.game.faction.Race;

public class ActionInfo {

    private final int action;

    private final boolean isHuman;

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
        double total = this.drawCounter + this.winCounter + this.loseCounter;

        NumberFormat form = NumberFormat.getPercentInstance();
        DecimalFormat df = new DecimalFormat("#.###");

        int norm = 13;
        int norm2 = 10;
        return f(new Decision(this.action, Race.getRaceForType(getType())).toString(), 4) + " " + this.action + "  \t"
        + f("Win: " + this.winCounter, norm) + f(" Draw: " + this.drawCounter, norm)
        + f(" Lose: " + this.loseCounter, norm) + f(" \tWin: " + form.format(this.winCounter / total), norm2)
        + f(" Draw: " + form.format(this.drawCounter / total), norm2)
        + f(" Lose: " + form.format(this.loseCounter / total), norm2) + "   \t" + getType() + " Score: "
                + df.format(this.score) + " Norm: "
                + df.format((this.score / (winCounter + loseCounter + drawCounter)));
    }

    private String f(final String s, final int preferedLength) {
        String result = s;
        while (result.length() < preferedLength) {
            result += " ";
        }

        return result;
    }

    public Race.Type getType() {
        if (this.isHuman) {
            return Race.Type.HUMAN;
        } else {
            return Race.Type.ALIEN;
        }
    }

}
