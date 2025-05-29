package narxoz.kz.controller;

import narxoz.kz.auth.UserRepository;
import narxoz.kz.auth.Users;
import narxoz.kz.model.Candidate;
import narxoz.kz.model.Election;
import narxoz.kz.model.Vote;
import narxoz.kz.repository.CandidateRepository;
import narxoz.kz.repository.ElectionRepository;
import narxoz.kz.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/vote")
public class VoteController {

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private ElectionRepository electionRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public String vote(@RequestParam Long electionId,
                       @RequestParam Long candidateId,
                       Principal principal,
                       RedirectAttributes redirectAttributes) {

        Users user = userRepository.findAllByEmail(principal.getName());
        Election election = electionRepository.findById(electionId).orElse(null);
        Candidate candidate = candidateRepository.findById(candidateId).orElse(null);

        if (election == null || candidate == null) {
            redirectAttributes.addFlashAttribute("error", "Неверные данные.");
            return "redirect:/election/home";
        }

        if (voteRepository.existsByUserAndElection(user, election)) {
            redirectAttributes.addFlashAttribute("error", "Вы уже голосовали на этих выборах.");
            return "redirect:/election/details?id=" + electionId;
        }

        Vote vote = new Vote();
        vote.setUser(user);
        vote.setElection(election);
        vote.setCandidate(candidate);
        voteRepository.save(vote);

        redirectAttributes.addFlashAttribute("success", "Ваш голос успешно учтён.");
        return "redirect:/election/details?id=" + electionId;
    }

}
