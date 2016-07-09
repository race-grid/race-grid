package racegrid.api.game;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import racegrid.api.model.Vector;

import java.util.HashMap;

import static org.junit.Assert.*;

public class ModelTest {

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testVectorKey() throws JsonProcessingException {
        Vector vector = new Vector(0, 1);
        HashMap<Vector, Integer> map = new HashMap<>();
        map.put(vector, 0);
        String s = objectMapper.writeValueAsString(map);
        String expected = "{\"(0,1)\":0}";
        assertEquals(expected, s);

    }
}
