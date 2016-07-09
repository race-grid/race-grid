package racegrid.api.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public class Id {
    private static int idCounter = 1;

    private final String id;

    public static Id generateUnique(){
        final Id id = new Id(String.valueOf(idCounter));
        idCounter ++;
        return id;
    }

    private Id(final String id) {
        this.id = id;
    }

    @JsonCreator
    public static Id of(String id){
        return new Id(id);
    }

    @Override
    public boolean equals(Object o){
        return (o instanceof Id) && id.equals(((Id) o).id);
    }

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    @Override
    @JsonValue
    public String toString(){
        return id;
    }
}
