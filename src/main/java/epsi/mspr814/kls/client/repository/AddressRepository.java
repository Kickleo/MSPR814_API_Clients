package epsi.mspr814.kls.client.repository;

import epsi.mspr814.kls.client.model.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {
}
