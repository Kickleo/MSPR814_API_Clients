package epsi.mspr814.kls.client.repository;

import epsi.mspr814.kls.client.model.Role;
import epsi.mspr814.kls.client.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(RoleName roleName);
}