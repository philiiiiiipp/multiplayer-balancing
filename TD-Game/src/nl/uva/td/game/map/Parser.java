package nl.uva.td.game.map;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Parser {

    public static final String START_FIELD = "S";

    public static final String END_FIELD = "E";

    public static GameField parse() {
        return parseFile("Standard");
    }

    /**
     * O decodes a tower-placement field X is a way field where the creeps walk S is the start field
     * for the creeps E is the end field for the creeps
     */
    public static GameField parseFile(final String path) {
        try {
            return parse(new String(Files.readAllBytes(Paths.get(path))));
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Only game fields with equal row length are allowed
     *
     * @param gameFieldString
     *            The game field string to be parsed, null will use the default game field
     * @return The parsed game field object
     */
    public static GameField parse(final String gameFieldString) {
        if (gameFieldString == null) {
            return parse();
        }

        String[] splittedField = gameFieldString.split("\n");

        List<TowerField> towerFields = new ArrayList<TowerField>();
        List<CreepField> creepFields = new ArrayList<CreepField>();
        CreepField startField = null, endField = null;
        Field[][] gameField = new Field[splittedField.length][splittedField[0].length()];

        int row = 0;
        for (String rowEntry : splittedField) {
            for (int column = 0; column < rowEntry.length(); ++column) {
                String fieldID = String.valueOf(rowEntry.charAt(column));

                switch (fieldID) {

                case TowerField.ID:
                    TowerField towerField = new TowerField(row, column);
                    towerFields.add(towerField);
                    gameField[row][column] = towerField;
                    break;

                case CreepField.ID:
                    CreepField creepField = new CreepField(CreepField.Type.NONE, row, column);
                    creepFields.add(creepField);
                    gameField[row][column] = creepField;
                    break;

                case START_FIELD:
                    creepFields.add(startField = new CreepField(CreepField.Type.START, row, column));
                    gameField[row][column] = startField;
                    break;

                case END_FIELD:
                    creepFields.add(endField = new CreepField(CreepField.Type.END, row, column));
                    gameField[row][column] = endField;
                    break;

                default:
                    throw new RuntimeException("Could not parse \"" + fieldID + "\" unknown ID");
                }
            }

            row++;
        }

        if (startField == null || endField == null) {
            throw new RuntimeException(
                    "This map does either not have a starting field or an end field, forgot to specify it? S marks start and E marks end");
        }

        // Make the field connections
        for (row = 0; row < gameField.length; row++) {
            int columnLength = gameField[row].length;

            for (int column = 0; column < columnLength; column++) {
                Field currentField = gameField[row][column];

                if (hasNorthField(row)) currentField.setNorth(gameField[row - 1][column]);

                if (hasEastField(column, columnLength)) currentField.setEast(gameField[row][column + 1]);

                if (hasSouthField(row, gameField.length)) currentField.setSouth(gameField[row + 1][column]);

                if (hasWestField(column, columnLength)) currentField.setWest(gameField[row][column - 1]);
            }
        }

        // Put together the path for the creeps to walk
        CreepField currentField = startField, lastField = startField;
        while (currentField != null) {

            if (isNextCreepField(currentField.getNorth(), lastField)) {
                currentField.setNextField((CreepField) currentField.getNorth());

            } else if (isNextCreepField(currentField.getEast(), lastField)) {
                currentField.setNextField((CreepField) currentField.getEast());

            } else if (isNextCreepField(currentField.getSouth(), lastField)) {
                currentField.setNextField((CreepField) currentField.getSouth());

            } else if (isNextCreepField(currentField.getWest(), lastField)) {
                currentField.setNextField((CreepField) currentField.getWest());
            }

            lastField = currentField;
            currentField = currentField.getNextField();
        }

        return new GameField(startField, endField, towerFields, creepFields, gameField);
    }

    private static boolean hasNorthField(final int row) {
        return row > 0;
    }

    private static boolean hasEastField(final int column, final int columnLength) {
        return column < columnLength - 1;
    }

    private static boolean hasSouthField(final int row, final int rowLength) {
        return row < rowLength - 1;
    }

    private static boolean hasWestField(final int column, final int columnLength) {
        return column > 0;
    }

    private static boolean isNextCreepField(final Field field, final Field lastField) {
        return field instanceof CreepField && field != lastField;
    }
}
