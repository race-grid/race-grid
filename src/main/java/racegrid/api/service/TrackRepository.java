package racegrid.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import racegrid.api.model.RaceTrack;
import racegrid.api.model.RacegridError;
import racegrid.api.model.RacegridException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

@Service
public class TrackRepository {

    private final ObjectMapper objectMapper;

    @Autowired
    public TrackRepository(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public RaceTrack loadTrackFromFile(String resourceName) {
        try {
            InputStream inputStream = getClass().getResourceAsStream(resourceName);
            String json = textFromInputStream(inputStream);
            return objectMapper.readValue(json, RaceTrack.class);
        } catch (IOException e) {
            throw new RacegridException(RacegridError.INTERNAL, "Could not load track from file: " + resourceName);
        }
    }

    private String textFromInputStream(InputStream inputStream){
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
    }
}
