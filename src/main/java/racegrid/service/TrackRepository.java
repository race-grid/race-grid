package racegrid.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import racegrid.model.RaceTrack;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;

public class TrackRepository {

    private final ObjectMapper objectMapper;
    private final Charset charset = Charset.forName("UTF-8");

    @Autowired
    public TrackRepository(ObjectMapper objectMapper){
        this.objectMapper = objectMapper;
    }

    public RaceTrack loadTrackFromFile(Path filePath) throws IOException {
        String json = readTextFile(filePath);
        return objectMapper.readValue(json, RaceTrack.class);
    }

    private String readTextFile(Path filePath) throws IOException {
        byte[] bytes = Files.readAllBytes(filePath);
        return new String(bytes, charset);
    }
}
