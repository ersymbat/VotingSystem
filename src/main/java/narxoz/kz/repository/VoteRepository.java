package narxoz.kz.repository;

import narxoz.kz.auth.Users;
import narxoz.kz.model.Candidate;
import narxoz.kz.model.Election;
import narxoz.kz.model.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {
    boolean existsByUserAndElection(Users user, Election election);
    long countByCandidateAndElection(Candidate candidate,Election election);
}
