package racegrid.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Collision {
    private final ExactVector exactLocation;
    private final Vector resultingPosition;
    private final CollisionType type;

    @JsonCreator
    public Collision(@JsonProperty("exactLocation") ExactVector exactLocation,
                     @JsonProperty("resultingPosition") Vector resultingPosition,
                     @JsonProperty("type") CollisionType type) {
        this.exactLocation = exactLocation;
        this.resultingPosition = resultingPosition;
        this.type = type;
    }

    public static Collision withWall(ExactVector exactLocation, Vector resultingPosition){
        return new Collision(exactLocation, resultingPosition, CollisionType.WITH_WALL);
    }

    @JsonProperty
    public ExactVector exactLocation() {
        return exactLocation;
    }

    @JsonProperty
    public Vector resultingPosition() {
        return resultingPosition;
    }

    @JsonProperty
    public CollisionType type() {
        return type;
    }
}
