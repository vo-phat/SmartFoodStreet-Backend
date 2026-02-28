package SmartFoodStreet_Backend.configuration;

import SmartFoodStreet_Backend.entity.Account;
import SmartFoodStreet_Backend.entity.Role;
import SmartFoodStreet_Backend.repository.AccountRepository;
import SmartFoodStreet_Backend.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;
import java.util.Set;

@Slf4j
@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {
    RoleRepository roleRepository;

    @Bean
    ApplicationRunner applicationRunner(AccountRepository accountRepository, RoleRepository roleRepository) {

        return args -> {

            Role adminRole = roleRepository.findByName("ADMIN")
                    .orElseGet(() -> {
                        Role role = Role.builder()
                                .name("ADMIN")
                                .description("Has full system access including account and permission management")
                                .build();
                        return roleRepository.save(role);
                    });

            Role vendorRole = roleRepository.findByName("VENDOR")
                    .orElseGet(() -> {
                        Role role = Role.builder()
                                .name("VENDOR")
                                .description("Can manage daily operations but cannot manage system configuration")
                                .build();
                        return roleRepository.save(role);
                    });

            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(10);

            if (accountRepository.findByUserName("admin").isEmpty()) {

                Account adminAccount = Account.builder()
                        .userName("admin")
                        .password(encoder.encode("admin"))
                        .fullName("Administrator")
                        .email("administrator@sv.sgu.edu.vn")
                        .isActive(true)
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .roles(Set.of(adminRole))
                        .build();

                accountRepository.save(adminAccount);

                log.warn("Admin account created with default password: admin");
            }

            if (accountRepository.findByUserName("vendor").isEmpty()) {

                Account vendorAccount = Account.builder()
                        .userName("vendor")
                        .password(encoder.encode("vendor"))
                        .fullName("Vendor")
                        .email("vendor@sv.sgu.edu.vn")
                        .isActive(true)
                        .createdAt(new Timestamp(System.currentTimeMillis()))
                        .roles(Set.of(vendorRole))
                        .build();

                accountRepository.save(vendorAccount);

                log.warn("Vendor account created with default password: vendor");
            }
        };
    }
}
