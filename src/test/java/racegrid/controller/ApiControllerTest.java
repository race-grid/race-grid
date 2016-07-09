package racegrid.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import racegrid.model.GameEntry;
import racegrid.model.GameState;
import racegrid.model.Id;
import racegrid.model.NewUserResponse;
import racegrid.model.Player;
import racegrid.model.PlayerGameState;
import racegrid.model.User;
import racegrid.model.Vector;
import racegrid.service.GameRepository;
import racegrid.service.Engine;
import racegrid.service.TrackRepository;
import racegrid.service.UserRepository;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {ApiControllerTest.class})
@WebAppConfiguration
@EnableAutoConfiguration
public class ApiControllerTest {

    private static ObjectMapper objectMapper = new ObjectMapper().registerModule(new Jdk8Module());
    private Engine engine;

    private static final String NAME = "Mickey";
    private static final String NAME_2 = "Donald";

    private ApiControllerTestClient client;


    @Before
    public void setup() {
        UserRepository userRepository = new UserRepository();
        GameRepository gameRepository = new GameRepository(userRepository);
        TrackRepository trackRepository = new TrackRepository(new ObjectMapper());
        Engine tmp = new Engine(trackRepository, userRepository, gameRepository);
        engine = spy(tmp);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(new ApiController(engine)).build();
        client = new ApiControllerTestClient(mockMvc, objectMapper);
    }

    @Test
    public void getGames() throws Exception {
        GameEntry game1 = new GameEntry(Id.of("ABC"), Collections.emptyList());
        GameEntry game2 = new GameEntry(Id.of("DEF"), Collections.emptyList());
        when(engine.getGames()).thenReturn(Arrays.asList(game1, game2).stream());

        GameEntry[] games = client.getGames();
        assertEquals(game1.id(), games[0].id());
        assertEquals(game2.id(), games[1].id());
    }

    @Test
    public void newUser() throws Exception {
        NewUserResponse response = client.newUser(NAME);
        assertTrue(response.isCreatedNewUser());
        assertEquals(NAME, response.getUser().name());
    }

    @Test
    public void getUsers() throws Exception {
        User u1 = new User(NAME, Id.of("A"));
        User u2 = new User(NAME_2, Id.of("B"));
        when(engine.getUsers()).thenReturn(Arrays.asList(u1, u2).stream());
        List<String> users = client.getUsers();
        assertTrue(users.contains(NAME));
        assertTrue(users.contains(NAME_2));
        assertEquals(2, users.size());
    }

    @Test
    public void newGameVsAi() {
        NewUserResponse newUserResponse = client.newUser(NAME);
        UUID userHash = newUserResponse.getUserHash();
        Id userId = newUserResponse.getUser().id();
        Id gameId = client.newGameVsAi(userId, userHash, 1);
        assertNotNull(gameId);
    }

    @Test
    public void getPossibleMoves() {
        NewUserResponse newUserResponse = client.newUser(NAME);
        UUID userHash = newUserResponse.getUserHash();
        Id userId = newUserResponse.getUser().id();
        Id gameId = client.newGameVsAi(userId, userHash, 1);
        HashMap<String, Optional<Vector>> moves = client.getPossibleMoves(gameId, userId, userHash);
        assertNotNull(moves);
    }

    @Test
    public void makeMove() throws JsonProcessingException {
        UUID userHash = UUID.randomUUID();
        Id userId = Id.of("B");
        Id gameId = Id.of("A");
        Vector destination = new Vector(1, 2);


        Map<Id, PlayerGameState> playerStates = new HashMap<>();
        List<Vector> positionHistory = Arrays.asList(new Vector(0, 0), new Vector(1, 2));
        playerStates.put(userId, new PlayerGameState(new Player("NAME", userId), positionHistory, false));
        GameState expectedState = new GameState(userId, playerStates, 10l);
        doReturn(expectedState).when(engine).userMakeMove(any(), any(), any());

        GameState state = client.makeMove(gameId, userId, userHash, destination);

        assertNotNull(state);
        verify(engine).userMakeMove(eq(gameId), any(), eq(destination));
    }


}