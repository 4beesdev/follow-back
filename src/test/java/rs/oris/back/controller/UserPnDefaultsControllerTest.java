package rs.oris.back.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.User;
import rs.oris.back.domain.dto.UserPnDefaultsDTO;
import rs.oris.back.service.UserPnDefaultsService;
import rs.oris.back.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserPnDefaultsControllerTest {

    @Mock
    private UserPnDefaultsService userPnDefaultsService;
    @Mock
    private UserService userService;

    private UserPnDefaultsController controller;

    @Before
    public void setUp() {
        controller = new UserPnDefaultsController();
        ReflectionTestUtils.setField(controller, "userPnDefaultsService", userPnDefaultsService);
        ReflectionTestUtils.setField(controller, "userService", userService);
    }

    @Test
    public void getMyPnDefaults_resolvesUserFromAuthorizationHeader() throws Exception {
        User user = new User();
        user.setUserId(7);
        user.setUsername("pn-user");

        Response<UserPnDefaultsDTO> expected = new Response<>(new UserPnDefaultsDTO("Firma", "Adresa", "Novi Sad", "Teretni", "Garaza 1"));

        when(userService.findByUsername("pn-user")).thenReturn(user);
        when(userPnDefaultsService.getForUser(user)).thenReturn(expected);

        Response<UserPnDefaultsDTO> actual = controller.getMyPnDefaults(authHeader("pn-user"));

        assertSame(expected, actual);
    }

    @Test
    public void updateMyPnDefaults_updatesDefaultsForResolvedUser() throws Exception {
        User user = new User();
        user.setUserId(8);
        user.setUsername("pn-admin");

        UserPnDefaultsDTO request = new UserPnDefaultsDTO("Firma 2", "Adresa 2", "Beograd", "Putnicki", "Garaza 2");
        Response<UserPnDefaultsDTO> expected = new Response<>(request);

        when(userService.findByUsername("pn-admin")).thenReturn(user);
        when(userPnDefaultsService.updateForUser(user, request)).thenReturn(expected);

        Response<UserPnDefaultsDTO> actual = controller.updateMyPnDefaults(authHeader("pn-admin"), request);

        assertSame(expected, actual);
    }

    private String authHeader(String username) {
        String payload = "{\"sub\":\"" + username + "\",\"exp\":1}";
        String encoded = Base64.getEncoder().encodeToString(payload.getBytes(StandardCharsets.UTF_8));
        return "header." + encoded + ".signature";
    }
}
