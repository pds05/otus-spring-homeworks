package ru.otus.hw.security;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.otus.hw.models.User;
import ru.otus.hw.repositories.UserRepository;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service("userDetailsService")
@AllArgsConstructor
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("User not found: {}", username);
            throw new UsernameNotFoundException("User " + username + " not found");
        }

        final List<SimpleGrantedAuthority> grantedAuthorities = user.getUserAuthorities().stream()
                .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.getAuthority())).toList();
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(grantedAuthorities).build();
    }

    public boolean save(User user) {
        User saved = userRepository.findByUsername(user.getUsername());
        if (Objects.nonNull(saved)) return false;

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return true;
    }
}
