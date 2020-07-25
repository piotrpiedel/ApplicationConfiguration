package com.piedel.piotr.configuration.service.infrastructure.startup;

import com.piedel.piotr.configuration.service.domain.user.Role;
import com.piedel.piotr.configuration.service.domain.user.RoleRepository;
import com.piedel.piotr.configuration.service.domain.user.User;
import com.piedel.piotr.configuration.service.domain.user.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements ApplicationRunner {

    private final BCryptPasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public DataLoader(
            UserRepository userRepository, RoleRepository roleRepository,
            BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        Role adminRole = Role.builder().name("ADMIN").build();
        Role userRole = Role.builder().name("USER").build();
        roleRepository.save(adminRole);
        roleRepository.save(userRole);
        User adminFirst = User.builder()
                .userName("admin")
                .password(passwordEncoder.encode("admin1"))
                .role(adminRole).build();
        User userFirst = User.builder()
                .userName("user")
                .password(passwordEncoder.encode("user1"))
                .role(userRole).build();

        userRepository.save(adminFirst);
        userRepository.save(userFirst);
    }
}
