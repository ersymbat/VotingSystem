package narxoz.kz.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import narxoz.kz.model.Candidate;

@Getter
@Setter

public class CandidateWithVotesDTO {
    private Candidate candidate;
    private long voteCount;

    public void setCandidate(Candidate candidate) {
        this.candidate = candidate;
    }

    public Candidate getCandidate() {
        return candidate;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Long voteCount) {
        this.voteCount = voteCount;
    }

    public CandidateWithVotesDTO() {
    }

    public CandidateWithVotesDTO(Candidate candidate, long voteCount) {
        this.candidate = candidate;
        this.voteCount = voteCount;
    }
}