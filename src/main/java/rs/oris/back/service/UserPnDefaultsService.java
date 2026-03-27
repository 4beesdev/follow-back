package rs.oris.back.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import rs.oris.back.controller.wrapper.Response;
import rs.oris.back.domain.PN;
import rs.oris.back.domain.User;
import rs.oris.back.domain.UserPnDefaults;
import rs.oris.back.domain.dto.UserPnDefaultsDTO;
import rs.oris.back.repository.UserPnDefaultsRepository;

import java.util.Optional;

@Service
public class UserPnDefaultsService {

    @Autowired
    private UserPnDefaultsRepository userPnDefaultsRepository;

    public Response<UserPnDefaultsDTO> getForUser(User user) {
        Optional<UserPnDefaults> optionalDefaults = userPnDefaultsRepository.findByUserUserId(user.getUserId());
        return new Response<>(toDto(optionalDefaults.orElse(null)));
    }

    public Response<UserPnDefaultsDTO> updateForUser(User user, UserPnDefaultsDTO dto) {
        UserPnDefaults userPnDefaults = userPnDefaultsRepository.findByUserUserId(user.getUserId()).orElseGet(() -> {
            UserPnDefaults created = new UserPnDefaults();
            created.setUser(user);
            return created;
        });

        userPnDefaults.setCompanyOwner(normalize(dto.getCompanyOwner()));
        userPnDefaults.setCompanyAddress(normalize(dto.getCompanyAddress()));
        userPnDefaults.setPlaceOfIssue(normalize(dto.getPlaceOfIssue()));
        userPnDefaults.setTransportType(normalize(dto.getTransportType()));
        userPnDefaults.setGarageAddress(normalize(dto.getGarageAddress()));

        UserPnDefaults saved = userPnDefaultsRepository.save(userPnDefaults);
        return new Response<>(toDto(saved));
    }

    public void applyDefaultsToPnIfMissing(PN pn, User user) {
        Optional<UserPnDefaults> optionalDefaults = userPnDefaultsRepository.findByUserUserId(user.getUserId());
        if (!optionalDefaults.isPresent()) {
            return;
        }

        UserPnDefaults defaults = optionalDefaults.get();

        if (!StringUtils.hasText(pn.getCompanyOwner()) && StringUtils.hasText(defaults.getCompanyOwner())) {
            pn.setCompanyOwner(defaults.getCompanyOwner());
        }
        if (!StringUtils.hasText(pn.getCompanyAddress()) && StringUtils.hasText(defaults.getCompanyAddress())) {
            pn.setCompanyAddress(defaults.getCompanyAddress());
        }
        if (!StringUtils.hasText(pn.getPlaceOfIssue()) && StringUtils.hasText(defaults.getPlaceOfIssue())) {
            pn.setPlaceOfIssue(defaults.getPlaceOfIssue());
        }
        if (!StringUtils.hasText(pn.getTransportType()) && StringUtils.hasText(defaults.getTransportType())) {
            pn.setTransportType(defaults.getTransportType());
        }
        if (!StringUtils.hasText(pn.getGarageAddress()) && StringUtils.hasText(defaults.getGarageAddress())) {
            pn.setGarageAddress(defaults.getGarageAddress());
        }
    }

    private UserPnDefaultsDTO toDto(UserPnDefaults userPnDefaults) {
        if (userPnDefaults == null) {
            return new UserPnDefaultsDTO();
        }

        return new UserPnDefaultsDTO(
                userPnDefaults.getCompanyOwner(),
                userPnDefaults.getCompanyAddress(),
                userPnDefaults.getPlaceOfIssue(),
                userPnDefaults.getTransportType(),
                userPnDefaults.getGarageAddress()
        );
    }

    private String normalize(String value) {
        if (!StringUtils.hasText(value)) {
            return null;
        }
        return value.trim();
    }
}
