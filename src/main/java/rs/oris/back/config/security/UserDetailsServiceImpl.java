package rs.oris.back.config.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import rs.oris.back.repository.UserRepository;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Custom query za vadjenje korisnika iz baze jer se tabela s korisnicima u bazi
     * razlikuje od standardne tabele koju spring ocekuje
     *
     * @param username
     * @return
     * @throws UsernameNotFoundException
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        rs.oris.back.domain.User userJadran = userRepository.findByUsername(username);

        if (userJadran == null) {
            throw new UsernameNotFoundException(username);
        }
        System.out.println("ovde------"+userJadran.getUsername() + " -- " + userJadran.getPassword());

        User user = new User(userJadran.getUsername(), userJadran.getPassword(), emptyList());
        System.out.println("ovde------"+user);
        return user;
    }

}
