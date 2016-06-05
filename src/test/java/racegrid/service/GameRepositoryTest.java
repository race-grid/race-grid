package racegrid.service;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import racegrid.model.RacegridException;
import racegrid.model.GameEntry;
import racegrid.model.Id;
import racegrid.model.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@RunWith(JUnit4.class)
public class GameRepositoryTest extends TestCase {

    private GameRepository gameRepository;
    private UserRepository userRepository;

    @Before
    public void setup() {
        userRepository = new UserRepository();
        gameRepository = new GameRepository(userRepository);
    }

    @Test
    public void newGame() {
        User p1 = newUser("X");
        User p2 = newUser("Y");
        GameEntry newGame = gameRepository.newGame(Arrays.asList(p1, p2));
        assertNotNull(newGame);
    }

    @Test(expected = RacegridException.class)
    public void newGame_shouldNotAllowNonExistantUsers() {
        User p1 = new User("X", Id.of("BAD_ID"));
        gameRepository.newGame(Collections.singletonList(p1));
    }

    @Test(expected = RacegridException.class)
    public void newGame_shouldNotAllowSameUserInDifferentGames() {
        User p1 = newUser("X");
        gameRepository.newGame(Collections.singletonList(p1));
        gameRepository.newGame(Collections.singletonList(p1));
    }

    @Test
    public void getGames() {
        assertEquals(0, gameRepository.getGames().count());
        User p1 = newUser("X");
        User p2 = newUser("Y");
        gameRepository.newGame(Arrays.asList(p1, p2));
        assertEquals(1, gameRepository.getGames().count());
    }

    @Test
    public void gameById() {
        GameEntry game = gameRepository.newGame(Arrays.asList(newUser("X")));
        assertEquals(Optional.of(game), gameRepository.gameById(game.id()));
    }

    private User newUser(String name) {
        return userRepository.newUser(name).getUser();
    }
}