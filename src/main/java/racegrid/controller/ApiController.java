package racegrid.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import racegrid.Util;
import racegrid.model.Collision;
import racegrid.model.GameEntry;
import racegrid.model.GameSettings;
import racegrid.model.GameState;
import racegrid.model.Id;
import racegrid.model.NewUserResponse;
import racegrid.model.RacegridException;
import racegrid.model.User;
import racegrid.model.UserAuth;
import racegrid.model.Vector;
import racegrid.service.Engine;

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

    @RequestMapping(value = "users", method = RequestMethod.POST)
    public NewUserResponse newUser(@RequestParam String name) {
        return engine.newUser(name);
    }

    @RequestMapping(value = "users", method = RequestMethod.GET)
    public List<String> getUsers() {
        return engine.getUsers()
                .map(User::name)
                .collect(Collectors.toList());
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
        System.err.println(msg + " (" + url + ")");
        return response;
    }
}
