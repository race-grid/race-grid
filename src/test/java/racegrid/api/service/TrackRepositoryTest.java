package racegrid.api.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import racegrid.api.model.ExactVector;
import racegrid.api.model.RaceTrack;
import racegrid.api.model.Vector;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class TrackRepositoryTest {

    TrackRepository repository = new TrackRepository(new ObjectMapper());

    @Test
    public void loadTrack() throws IOException {
        String fileName = "test-race-track.json";
        Path filePath = filePath(fileName);

        RaceTrack track = repository.loadTrackFromFile(filePath);

        assertEquals(10, track.height());
        assertEquals(20, track.width());
        assertEquals(new ExactVector(1.5, 2.5), track.goalLine().from());
        assertEquals(new ExactVector(3.5, 4.5), track.goalLine().to());
        assertEquals(new ExactVector(10, 20), track.walls().get(0).from());
        assertEquals(new ExactVector(30, 40), track.walls().get(0).to());
        assertEquals(new Vector(100, 200), track.startPositions().get(0));
    }

    private Path filePath(String fileName) {
        URL url = getClass().getClassLoader().getResource(fileName);
        return Paths.get(url.getFile());
    }
}
