package racegrid.api.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import racegrid.api.model.GameEntry;
import racegrid.api.model.GameState;
import racegrid.api.model.Id;
import racegrid.api.model.NewUserResponse;
import racegrid.api.model.Vector;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


public class ApiControllerTestClient {

    private final static String API_PATH = "/api";

    private final MockMvc client;
    private final ObjectMapper objectMapper;

    public ApiControllerTestClient(MockMvc client, ObjectMapper objectMapper) {
        this.client = client;
        this.objectMapper = objectMapper;
    }

    public GameState makeMove(Id gameId, Id userId, UUID userHash, Vector destination) {
        try {
            String requestJson = objectMapper.writeValueAsString(makeMoveRequest(gameId, userId, userHash, destination));
            MockHttpServletRequestBuilder request = post(API_PATH + "/make-move")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestJson);
            return callAndParse(request, GameState.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public GameEntry[] getGames() {
        return callAndParse(get(API_PATH + "/games"), GameEntry[].class);
    }

    private MakeMoveRequest makeMoveRequest(Id gameId, Id userId, UUID userHash, Vector destination) {
        MakeMoveRequest request = new MakeMoveRequest();
        request.setGameId(gameId);
        request.setUserId(userId);
        request.setUserHash(userHash);
        request.setDestination(destination);
        return request;
    }

    public Id newGameVsAi(Id userId, UUID userHash, int numOpponents) {
        MockHttpServletRequestBuilder request = post(API_PATH + "/new-game-vs-ai" +
                "?userId=" + userId +
                "&userHash=" + userHash +
                "&numOpponents=" + numOpponents);
        return callAndParse(request, Id.class);
    }

    public NewUserResponse newUser(String name) {
        MvcResult result = call(post(API_PATH + "/create-user?name=" + name));
        return parse(result, NewUserResponse.class);
    }

    public List<String> getUsers() {
        String[] users = callAndParse(get(API_PATH + "/users"), String[].class);
        return Arrays.asList(users);
    }

    private <T> T callAndParse(RequestBuilder request, Class<T> clazz) {
        MvcResult result = call(request);
        return parse(result, clazz);
    }

    public HashMap<String, Optional<Vector>> getPossibleMoves(Id gameId, Id userId, UUID userHash) {
        MockHttpServletRequestBuilder request = get(API_PATH + "/possible-moves" +
                "?gameId=" + gameId +
                "&userId=" + userId +
                "&userHash=" + userHash);
        TypeReference<HashMap<String, Optional<Vector>>> type =
                new TypeReference<HashMap<String, Optional<Vector>>>() {
                };
        return callAndParse(request, type);
    }

    private <T> T callAndParse(RequestBuilder request, TypeReference<T> clazz) {
        MvcResult result = call(request);
        return parse(result, clazz);
    }


    private MvcResult call(RequestBuilder request) {
        try {
            return client.perform(request)
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T parse(MvcResult result, Class<T> clazz) {
        try {
            String resultString = result.getResponse().getContentAsString();
            return objectMapper.readValue(resultString, clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private <T> T parse(MvcResult result, TypeReference<T> type) {
        try {
            String resultString = result.getResponse().getContentAsString();
            return objectMapper.readValue(resultString, type);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Id createLobby(Id userId, UUID userHash) {
        MockHttpServletRequestBuilder request = post(API_PATH + "/create-lobby?userId=" + userId + "&userHash=" + userHash);
        Id lobbyId = callAndParse(request, Id.class);
        return lobbyId;
    }

    public void inviteToLobby(Id userId, UUID userHash, Id otherUserId) {
        MockHttpServletRequestBuilder request = post(API_PATH + "/invite-to-lobby?userId=" + userId +
                "&userHash=" + userHash + "&otherUserId=" + otherUserId);
        call(request);
    }
}
