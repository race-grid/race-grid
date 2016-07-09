package racegrid.api.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Line {
    private final ExactVector from;
    private final ExactVector to;

    @JsonCreator
    public Line(@JsonProperty("from") ExactVector from,
                @JsonProperty("to") ExactVector to) {
        this.from = from;
        this.to = to;
    }

    @JsonProperty
    public ExactVector from() {
        return from;
    }

    @JsonProperty
    public ExactVector to() {
        return to;
    }
}
