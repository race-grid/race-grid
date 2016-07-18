package racegrid.api.service;

import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import racegrid.api.model.RacegridException;
import racegrid.api.model.GameEntry;
import racegrid.api.model.Id;
import racegrid.api.model.User;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@RunWith(JUnit4.class)
public class GameRepositoryTest extends TestCase {

    private GameRepository gameRepository;
    private UserRepository userRepository;

    @Before
    public void setup() {
        RacegridProps props = new RacegridProps();
        userRepository = new UserRepository(props);
        gameRepository = new GameRepository();
    }

    @Test
    public void newGame() {
        User p1 = newUser("X");
        User p2 = newUser("Y");
        GameEntry newGame = gameRepository.newGame(Arrays.asList(p1, p2));
        assertNotNull(newGame);
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
        GameEntry game = gameRepository.newGame(Collections.singletonList(newUser("X")));
        assertEquals(Optional.of(game), gameRepository.gameById(game.id()));
    }

    private User newUser(String name) {
        return userRepository.newUser(name).getUser();
    }
}