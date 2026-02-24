package SmartFoodStreet_Backend.repository;

import SmartFoodStreet_Backend.entity.Account;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {

    boolean existsByUserName(String userName);

    Optional<Account> findByUserName(String userName);

    @EntityGraph(attributePaths = {"roles", "roles.permissions"})
    Optional<Account> findWithRolesByUserName(String userName);

    @EntityGraph(attributePaths = {"roles"})
    Optional<Account> findWithRolesById(Integer id);
}
