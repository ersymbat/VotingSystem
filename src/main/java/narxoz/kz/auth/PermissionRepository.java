package narxoz.kz.auth;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface PermissionRepository extends JpaRepository<Permission, Long> {

}
