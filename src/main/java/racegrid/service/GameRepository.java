package racegrid.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import racegrid.model.RacegridException;
import racegrid.model.GameEntry;
import racegrid.model.Id;
import racegrid.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class GameRepository {
    private List<GameEntry> games = new ArrayList<>();
    private final UserRepository userRepository;

    @Autowired
    public GameRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public GameEntry newGame(List<User> users) {
        assertUsersExist(users);
        assertNoUsersAreInGame(users);
        Id id = Id.generateUnique();
        GameEntry newGame = new GameEntry(id, users);
        games.add(newGame);
        return newGame;
    }

    private void assertNoUsersAreInGame(List<User> users) {
        boolean isSomeUserAlreadyInGame = users.stream().anyMatch(this::isUserInGame);
        if (isSomeUserAlreadyInGame) {
            throw new RacegridException("User is already in a game!");
        }
    }

    private void assertUsersExist(List<User> users) {
        users.forEach(p -> {
            if (!userRepository.getUsers().anyMatch(p2 -> p2.equals(p))) {
                throw new RacegridException("User doesn't exist: " + p);
            }
        });
    }

    private boolean isUserInGame(User p) {
        return games.stream()
                .anyMatch(g -> g.users().contains(p));
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
