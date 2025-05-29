package narxoz.kz.repository;

import narxoz.kz.model.Election;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ElectionRepository extends JpaRepository<Election, Long> {
    List<Election> findAllByTitleContainsIgnoreCase(String title);
    List<Election> findAllByOrderByStartDateAsc();
    List<Election> findAllByOrderByStartDateDesc();
    Election findAllById(Long id);
}
