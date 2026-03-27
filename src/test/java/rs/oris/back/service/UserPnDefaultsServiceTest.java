package rs.oris.back.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.util.ReflectionTestUtils;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.PN;
import rs.oris.back.domain.User;
import rs.oris.back.domain.UserPnDefaults;
import rs.oris.back.domain.dto.UserPnDefaultsDTO;
import rs.oris.back.repository.UserPnDefaultsRepository;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class UserPnDefaultsServiceTest {

    @Mock
    private UserPnDefaultsRepository userPnDefaultsRepository;

    private UserPnDefaultsService userPnDefaultsService;

    @Before
    public void setUp() {
        userPnDefaultsService = new UserPnDefaultsService();
        ReflectionTestUtils.setField(userPnDefaultsService, "userPnDefaultsRepository", userPnDefaultsRepository);
    }

    @Test
    public void getForUser_returnsEmptyDtoWhenDefaultsDoNotExist() {
        User user = user(1);
        when(userPnDefaultsRepository.findByUserUserId(1)).thenReturn(Optional.empty());

        Response<UserPnDefaultsDTO> response = userPnDefaultsService.getForUser(user);

        assertNull(response.getData().getCompanyOwner());
        assertNull(response.getData().getCompanyAddress());
        assertNull(response.getData().getPlaceOfIssue());
        assertNull(response.getData().getTransportType());
        assertNull(response.getData().getGarageAddress());
    }

    @Test
    public void updateForUser_createsDefaultsAndNormalizesValues() {
        User user = user(2);
        UserPnDefaultsDTO request = new UserPnDefaultsDTO("  Firma  ", "   ", "  Nis ", "  Teretni  ", null);

        when(userPnDefaultsRepository.findByUserUserId(2)).thenReturn(Optional.empty());
        when(userPnDefaultsRepository.save(any(UserPnDefaults.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Response<UserPnDefaultsDTO> response = userPnDefaultsService.updateForUser(user, request);

        assertEquals("Firma", response.getData().getCompanyOwner());
        assertNull(response.getData().getCompanyAddress());
        assertEquals("Nis", response.getData().getPlaceOfIssue());
        assertEquals("Teretni", response.getData().getTransportType());
        assertNull(response.getData().getGarageAddress());
    }

    @Test
    public void applyDefaultsToPnIfMissing_fillsOnlyBlankFields() {
        User user = user(3);
        PN pn = new PN();
        pn.setCompanyOwner(null);
        pn.setCompanyAddress("Rucna adresa");
        pn.setPlaceOfIssue("   ");
        pn.setTransportType("Specijalni");
        pn.setGarageAddress(null);

        UserPnDefaults defaults = new UserPnDefaults();
        defaults.setCompanyOwner("Default firma");
        defaults.setCompanyAddress("Default adresa");
        defaults.setPlaceOfIssue("Beograd");
        defaults.setTransportType("Teretni");
        defaults.setGarageAddress("Garaza 9");

        when(userPnDefaultsRepository.findByUserUserId(3)).thenReturn(Optional.of(defaults));

        userPnDefaultsService.applyDefaultsToPnIfMissing(pn, user);

        assertEquals("Default firma", pn.getCompanyOwner());
        assertEquals("Rucna adresa", pn.getCompanyAddress());
        assertEquals("Beograd", pn.getPlaceOfIssue());
        assertEquals("Specijalni", pn.getTransportType());
        assertEquals("Garaza 9", pn.getGarageAddress());
    }

    private User user(int userId) {
        User user = new User();
        user.setUserId(userId);
        return user;
    }
}
