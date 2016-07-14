package racegrid.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import racegrid.api.model.RaceTrack;
import racegrid.api.model.RacegridError;
import racegrid.api.model.RacegridException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class TrackRepository {

    private final ObjectMapper objectMapper;
    private final Charset charset = Charset.forName("UTF-8");

    @Autowired
    public TrackRepository(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public RaceTrack loadTrackFromFile(Path filePath) {
        try {
            String json = readTextFile(filePath);
            return objectMapper.readValue(json, RaceTrack.class);
        } catch (IOException e) {
            throw new RacegridException(RacegridError.INTERNAL, "Could not load track from file: " + filePath);
        }
    }

    private String readTextFile(Path filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(filePath);
        return new String(bytes, charset);
    }
}
