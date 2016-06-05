package racegrid.game;


import racegrid.model.PlayerGameState;
import racegrid.model.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsciiBoard {

    public static String boardToString(GameBoard board) {
        Map<Vector, List<Character>> grid = gridFromBoard(board);
        return stringFromGrid(grid);
    }

    private static Map<Vector, List<Character>> gridFromBoard(GameBoard board) {
        Map<Vector, List<Character>> grid = new HashMap<>();

        char[] playerSymbols = new char[]{
                'x', 'o', '3', '4', '5', '6', '7', '8', '9'
        };

        ArrayList<PlayerGameState> states = new ArrayList<>(board.getPlayerStates().values());
        for (int i = 0; i < states.size(); i++) {
            PlayerGameState state = states.get(i);
            Character symbol = playerSymbols[i];
            state.positionHistory().forEach(pos -> {
                List<Character> occupiedBy = grid.computeIfAbsent(pos, vec -> new ArrayList<>());
                occupiedBy.add(symbol);
            });
        }
        return grid;
    }

    private static String stringFromGrid(Map<Vector, List<Character>> grid) {
        StringBuilder str = new StringBuilder();

        int min = -10;
        int max = 10;

        str.append("|");
        for (int x = min; x < max; x++) {
            str.append("-");
        }
        str.append("|\n");
        for (int y = min; y < max; y++) {
            str.append("|");
            for (int x = min; x < max; x++) {
                Vector cell = new Vector(x, y);
                if (grid.containsKey(cell)) {
                    Character symbol = grid.get(cell).get(0);
                    str.append(symbol);
                } else {
                    str.append(" ");
                }
            }
            str.append("|\n");
        }
        str.append("|");
        for (int x = min; x < max; x++) {
            str.append("-");
        }
        str.append("|\n");
        return str.toString();
    }
}
