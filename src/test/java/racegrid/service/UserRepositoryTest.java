package racegrid.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import racegrid.model.Id;
import racegrid.model.NewUserResponse;
import racegrid.model.User;
import racegrid.model.UserAuth;

import java.util.Optional;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class UserRepositoryTest {

    private UserRepository service;

    private final String NAME = "X";
    private final String NAME_2 = "Y";

    @Before
    public void setup() {
        service = new UserRepository();
    }

    @Test
    public void newUser() {
        NewUserResponse result = service.newUser(NAME);
        assertTrue(result.isCreatedNewUser());
        assertNotNull(result.getUserHash());
    }

    @Test
    public void newUser_shouldNotAllowDuplicateNames() {
        service.newUser(NAME);
        NewUserResponse secondUser = service.newUser(NAME);
        assertFalse(secondUser.isCreatedNewUser());
    }

    @Test
    public void newUser_shouldNotAllowShortName() {
        String shortName = "";
        NewUserResponse player = service.newUser(shortName);
        assertFalse(player.isCreatedNewUser());
    }

    @Test
    public void newUser_shouldNotAllowLongName() {
        String longName = "asdflkjlkjwlkejflkwajeflkjwfjawelfkjlwkfjlakwjelfjkweflkawjef";
        NewUserResponse player = service.newUser(longName);
        assertFalse(player.isCreatedNewUser());
    }

    @Test
    public void getUsers() {
        assertTrue(service.getUsers().count() == 0);
        service.newUser(NAME);
        service.newUser(NAME_2);
        assertEquals(2, service.getUsers().count());
    }

    @Test
    public void userByName() {
        NewUserResponse newUserResponse = service.newUser(NAME);
        User user = newUserResponse.getUser();
        assertEquals(Optional.of(user), service.userById(user.id()));
    }

    @Test
    public void userByName_shouldReturnEmptyForUnknownName(){
        Optional<User> optional = service.userById(Id.of("BAD_ID"));
        assertEquals(Optional.empty(), optional);
    }

    @Test
    public void authorizeUser() {
        NewUserResponse response = service.newUser(NAME);
        assertTrue(service.authorizeUser(new UserAuth(response.getUser().id(), response.getUserHash())));
    }

    @Test
    public void authorizeUser_shouldEnforceCorrectHash() {
        UUID userHash = service.newUser(NAME).getUserHash();
        assertFalse(service.authorizeUser(new UserAuth(Id.of("BAD_ID"), userHash)));
    }
}