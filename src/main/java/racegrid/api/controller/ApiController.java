package racegrid.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import racegrid.api.Util;
import racegrid.api.model.Collision;
import racegrid.api.model.GameEntry;
import racegrid.api.model.GameSettings;
import racegrid.api.model.GameState;
import racegrid.api.model.Id;
import racegrid.api.model.NewUserResponse;
import racegrid.api.model.RacegridException;
import racegrid.api.model.User;
import racegrid.api.model.UserAuth;
import racegrid.api.model.Vector;
import racegrid.api.service.Engine;
import racegrid.model.Lobby;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/")
@CrossOrigin //TODO
public class ApiController {

    private final Engine engine;

    @Autowired
    public ApiController(Engine engine) {
        this.engine = engine;
    }

    @RequestMapping(value = "games", method = RequestMethod.GET)
    public List<GameEntry> getGames() {
        return engine.getGames()
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "create-user", method = RequestMethod.POST)
    public NewUserResponse createUser(@RequestParam String name) {
        return engine.newUser(name);
    }

    @RequestMapping(value = "remove-user", method = RequestMethod.POST)
    public void removeUser(@RequestParam Id userId, @RequestParam UUID userHash) {
        engine.removeUser(new UserAuth(userId, userHash));
    }

    @RequestMapping(value = "users", method = RequestMethod.GET)
    public List<String> getUsers() {
        return engine.getUsers()
                .map(User::name)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "lobbies/{lobbyId}", method = RequestMethod.GET)
    public Lobby getLobby(@PathVariable Id lobbyId) {
        return engine.lobbyById(lobbyId);
    }

    @RequestMapping(value = "lobbies", method = RequestMethod.GET)
    public List<Lobby> getLobbies() {
        return engine.getLobbies()
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "create-lobby", method = RequestMethod.POST)
    public Id createLobby(@RequestParam Id userId, @RequestParam UUID userHash) {
        return engine.createLobby(new UserAuth(userId, userHash));
    }

    @RequestMapping(value = "invite-to-lobby", method = RequestMethod.POST)
    public void inviteToLobby(@RequestParam Id userId, @RequestParam UUID userHash, @RequestParam Id otherUserId) {
        engine.inviteToLobby(new UserAuth(userId, userHash), otherUserId);
    }

    @RequestMapping(value = "new-game-vs-ai", method = RequestMethod.POST)
    public Id newGameVsAi(@RequestParam Id userId, @RequestParam UUID userHash, @RequestParam int numOpponents) {
        GameSettings settings = new GameSettings(5);
        return engine.newTimedGameVsAi(new UserAuth(userId, userHash), numOpponents, settings);
    }

    @RequestMapping(value = "new-slow-game-vs-ai", method = RequestMethod.POST)
    public Id newSlowGameVsAi(@RequestParam Id userId, @RequestParam UUID userHash, @RequestParam int numOpponents) {
        GameSettings settings = new GameSettings(5);
        return engine.newSlowGameVsAi(new UserAuth(userId, userHash), numOpponents);
    }

    @RequestMapping(value = "possible-moves", method = RequestMethod.GET)
    public Map<String, Optional<Collision>> possibleMoves(@RequestParam Id gameId, @RequestParam Id userId, @RequestParam UUID userHash) {
        Map<Vector, Optional<Collision>> moves = engine.getValidMovesWithCollisionData(gameId, new UserAuth(userId, userHash));
        return Util.mapKeys(moves, Vector::toString);
    }

    @RequestMapping(value = "make-move", method = RequestMethod.POST)
    public GameState makeMove(@RequestBody MakeMoveRequest request) {
        return engine.userMakeMove(
                request.getGameId(),
                new UserAuth(request.getUserId(), request.getUserHash()),
                request.getDestination()
        );
    }

    @ExceptionHandler(RacegridException.class)
    public ErrorResponse handleError(HttpServletRequest req, RacegridException exception) {
        String url = req.getRequestURL().toString();
        String msg = exception.getMessage();
        ErrorResponse response = new ErrorResponse();
        response.setUrl(url);
        response.setMessage(msg);
        response.setError(exception.getError());
        System.err.println(msg + " (" + url + ")");
        return response;
    }
}
