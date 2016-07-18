package racegrid.api.service;

import org.springframework.stereotype.Service;
import racegrid.api.model.GameEntry;
import racegrid.api.model.Id;
import racegrid.api.model.RacegridError;
import racegrid.api.model.RacegridException;
import racegrid.api.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GameRepository {
    private List<GameEntry> games = new ArrayList<>();

    public GameEntry newGame(List<User> users) {
        assertNoUsersAreInGame(users);
        Id id = Id.generateUnique();
        GameEntry newGame = new GameEntry(id, users);
        games.add(newGame);
        return newGame;
    }

    private void assertNoUsersAreInGame(List<User> users) {
        boolean isSomeUserAlreadyInGame = users.stream().anyMatch(u -> isUserInGame(u.id()));
        if (isSomeUserAlreadyInGame) {
            throw new RacegridException(RacegridError.USER_IN_GAME, "User is already in a game!");
        }
    }

    public boolean isUserInGame(Id userId) {
        return games.stream()
                .anyMatch(g -> g.users().stream()
                        .anyMatch(u -> u.id().equals(userId)));
    }

    public Stream<GameEntry> getGames() {
        return games.stream();
    }

    public Optional<GameEntry> gameById(Id id) {
        return games.stream()
                .filter(g -> g.id().equals(id))
                .findFirst();
    }
}
