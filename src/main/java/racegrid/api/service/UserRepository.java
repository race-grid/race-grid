package racegrid.api.service;


import org.springframework.stereotype.Service;
import racegrid.api.model.Id;
import racegrid.api.model.NewUserResponse;
import racegrid.api.model.User;
import racegrid.api.model.UserAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Service
public class UserRepository {

    private static final int MIN_NAME_LENGTH = 1;
    private static final int MAX_NAME_LENGTH = 30;

    private Map<Id, UUID> hashes = new HashMap<>();
    private List<User> users = new ArrayList<>();

    public NewUserResponse newUser(String name) {
        if (!isValidName(name)) {
            return NewUserResponse.fail();
        }
        User newUser = new User(name, Id.generateUnique());
        UUID hash = UUID.randomUUID();
        users.add(newUser);
        hashes.put(newUser.id(), hash);
        return NewUserResponse.success(newUser, hash);
    }

    private boolean isValidName(String name) {
        //TODO check if too many spaces
        boolean nameTaken = users.stream().anyMatch(p -> p.name().equals(name));
        boolean validLength = name.length() >= MIN_NAME_LENGTH && name.length() <= MAX_NAME_LENGTH;
        return validLength && !nameTaken;
    }

    public Stream<User> getUsers() {
        return users.stream();
    }

    public boolean authenticateUser(UserAuth auth) {

        if(false){
            return true; //TODO
        }

        return hashes.containsKey(auth.id())
                && hashes.get(auth.id()).equals(auth.hash());
    }

    public Optional<User> userById(Id id) {
        return users.stream()
                .filter(u -> u.id().equals(id))
                .findFirst();
    }
}
