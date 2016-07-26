package racegrid.api.game;

import racegrid.api.Geometry;
import racegrid.api.model.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class Game {
    private final MutableGameBoard board;
    private Id activePlayerId;

    public Game(MutableGameBoard board, Id activePlayerId) {
        assertValidActivePlayer(board, activePlayerId);
        this.board = board;
        this.activePlayerId = activePlayerId;
    }

    private void assertValidActivePlayer(GameBoard board, Id activePlayerId) {
        boolean boardContainsActivePlayer = board.getPlayers()
                .anyMatch(p -> p.id().equals(activePlayerId));
        if (!boardContainsActivePlayer) {
            throw new IllegalArgumentException("Board doesn't contain active player " + activePlayerId);
        }
    }

    public void makeMove(Id playerId, Vector destination) {
        assertPlayersTurn(playerId);
        assertIsValidMove(playerId, destination);
        board.makeMove(playerId, destination);
        setNextPlayersTurn();
    }

    public Id getActivePlayerId() {
        return activePlayerId;
    }

    public GameBoard getBoard() {
        return board;
    }

    private void assertPlayersTurn(Id playerId) {
        if (!activePlayerId.equals(playerId)) {
            throw new RacegridException(RacegridError.NOT_PLAYERS_TURN, "Not player's turn. Cannot make a move!");
        }
    }

    /**
     * @return valid moves mapping to resulting collision position (if collision)
     */
    private Map<Vector, Optional<Collision>> getCollisionDataForGivenMoves(Id playerId, List<Vector> moves) {
        Vector from = board.getPlayerCurrentPosition(playerId);
        CollisionHandler collisionHandler = board.getCollisionHandler();
        RaceTrack track = board.getRaceTrack();
        return moves.stream().collect(Collectors.toMap(
                to -> to,
                to -> collisionHandler.collisionBetween(track, from, to)
        ));
    }

    private Id nextPlayerId(Id current) {
        List<Id> ids = board.getPlayerIds().collect(Collectors.toList());
        int index = ids.indexOf(current);
        return ids.get((index + 1) % ids.size());
    }

    private List<Vector> possibleNextMovesBasedOnVelocity(Id id) {
        Vector currentPos = board.getPlayerCurrentPosition(id);
        Vector prevPos = board.getPlayerPreviousPosition(id);
        Vector velocity = currentPos.minus(prevPos);
        Vector nextPosition = currentPos.plus(velocity);
        return Geometry.withSurrounding(nextPosition);
    }

    public Map<Vector, Optional<Collision>> getValidMovesWithCollisionData(Id playerId) {
        List<Vector> moves = possibleNextMovesBasedOnVelocity(playerId);
        return getCollisionDataForGivenMoves(playerId, moves);
    }

    private void assertIsValidMove(Id playerId, Vector destination) {
        List<Vector> valid = possibleNextMovesBasedOnVelocity(playerId);
        boolean isValid = valid.contains(destination);
        if (!isValid) {
            throw new RacegridException(RacegridError.INVALID_MOVE, "Invalid move by player " + playerId + ": " + destination);
        }
    }

    public void setNextPlayersTurn() {
        activePlayerId = nextPlayerId(activePlayerId);
    }
}
