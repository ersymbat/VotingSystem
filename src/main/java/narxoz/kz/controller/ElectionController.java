package narxoz.kz.controller;
import jakarta.transaction.Transactional;
import narxoz.kz.auth.UserRepository;
import narxoz.kz.auth.Users;
import narxoz.kz.dto.CandidateWithVotesDTO;
import narxoz.kz.model.Candidate;
import narxoz.kz.model.Election;
import narxoz.kz.model.Vote;
import narxoz.kz.repository.CandidateRepository;
import narxoz.kz.repository.ElectionRepository;
import narxoz.kz.repository.VoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/election")
public class ElectionController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private VoteRepository voteRepository;
    @Autowired
   private ElectionRepository electionRepository;
    @Autowired
    private CandidateRepository candidateRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/add-election")
    public String openAddElection(Model model){
        List<Candidate> candidates = candidateRepository.findAll();
        model.addAttribute("candidates", candidates);
        return "add-election";
    }
    @PostMapping("/add-election")
    public String addElection(
                         @RequestParam("title") String title,
                         @RequestParam("description") String description,
                         @RequestParam("startDate")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime startDate,
                         @RequestParam("endDate")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
                         @RequestParam("candidates") List<Candidate> candidates,
                         @RequestParam(value = "isActive",defaultValue = "false") boolean isActive){
       Election updElection = new Election();
       updElection.setCandidates(candidates);
       updElection.setActive(isActive);
       updElection.setTitle(title);
       updElection.setDescription(description);
       updElection.setStartDate(startDate);
       updElection.setEndDate(endDate);
       electionRepository.save(updElection);
        StringBuilder report = new StringBuilder();
        report.append("Добавлены новые выборы!\n");
        report.append("──────────────────────────────\n\n");
        report.append("\n"+updElection.getTitle() +" \n");
        report.append("──────────────────────────────\n\n");
        report.append("\n"+updElection.getDescription() +" \n");
        report.append("──────────────────────────────\n\n");
        report.append("\n"+updElection.getStartDate() +" \n");
        report.append("──────────────────────────────\n\n");
        report.append("\n"+updElection.getEndDate() +" \n");


        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("yersymbat.sagatbekov@narxoz.kz");
        message.setSubject("Новые выборы! " +LocalDate.now());
        message.setText(report.toString());

        javaMailSender.send(message);
        return "redirect:home";
    }
    @GetMapping("/add-candidate")
    public String openAddCandidate(Model model){
        model.addAttribute("candidate", new Candidate());
       return "add-candidate";

    }
    @PostMapping("/add")
    public String saveCandidate(@ModelAttribute Candidate candidate) {
        candidateRepository.save(candidate);
        StringBuilder report = new StringBuilder();
        report.append("Добавлен новый кандидат!\n");
        report.append("──────────────────────────────\n\n");
        report.append("\n"+candidate.getName() +" \n");
        report.append("──────────────────────────────\n\n");
        report.append("\n"+candidate.getDescription() +" \n");
        report.append("──────────────────────────────\n\n");
        report.append("\n"+candidate.getVoteCount() +" \n");
        report.append("──────────────────────────────\n\n");


        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("yersymbat.sagatbekov@narxoz.kz");
        message.setSubject("Новый кандидат! " +LocalDate.now());
        message.setText(report.toString());

        javaMailSender.send(message);
        return "redirect:/election/home";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/home")
    public String openHome(Model model,
                           @RequestParam(required = false, name = "search") String search,
                           @RequestParam(required = false, name = "sort") String sort) {

        List<Election> elections;

        if (search != null) {
            elections = electionRepository.findAllByTitleContainsIgnoreCase(search);
        } else if ("asc".equals(sort)) {
            elections = electionRepository.findAllByOrderByStartDateAsc();
        } else if ("desc".equals(sort)) {
            elections = electionRepository.findAllByOrderByStartDateDesc();
        } else {
            elections = electionRepository.findAll();
        }

        model.addAttribute("elections", elections);


        return "home";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/details")
    public String openDetails(@RequestParam("id") Long id,
                              Model model,
                              Principal principal) {
        Election election = electionRepository.findById(id).orElse(null);
        if (election == null) {
            return "redirect:/elections/home";
        }

        String email = principal.getName();
        Users user = userRepository.findAllByEmail(email);

        model.addAttribute("alreadyVoted", voteRepository.existsByUserAndElection(user, election));
        model.addAttribute("election", election);

        // Кандидаты с голосами только по текущим выборам
        List<CandidateWithVotesDTO> candidatesWithVotes = new ArrayList<>();
        for (Candidate candidate : election.getCandidates()) {
            long count = voteRepository.countByCandidateAndElection(candidate, election);
            candidatesWithVotes.add(new CandidateWithVotesDTO(candidate, count));
        }

        model.addAttribute("candidatesWithVotes", candidatesWithVotes);

        return "details";
    }
    @Transactional
    @PostMapping("/deleteElection")
    public String deleteElection(@RequestParam("id") Long id){
        StringBuilder report = new StringBuilder();
        report.append("Удалены выборы!\n");
        report.append("──────────────────────────────\n\n");
        report.append("\n"+electionRepository.findAllById(id).getTitle() +" \n");
        report.append("──────────────────────────────\n\n");
        report.append("\n"+electionRepository.findAllById(id).getDescription() +" \n");
        report.append("──────────────────────────────\n\n");
        report.append("\n"+electionRepository.findAllById(id).getStartDate() +" \n");
        report.append("──────────────────────────────\n\n");
        report.append("\n"+electionRepository.findAllById(id).getEndDate() +" \n");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo("yersymbat.sagatbekov@narxoz.kz");
        message.setSubject("Удалены выборы! " +LocalDate.now());
        message.setText(report.toString());

        javaMailSender.send(message);
        electionRepository.deleteById(id);
        return "redirect:/election/home";
    }

    @GetMapping("/login")
    public String openLogin(){
        return "login";
    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/updElection")
    public String updElection(@RequestParam("id") Long id, Model model){
        Election election = electionRepository.findAllById(id);
        List<Candidate> candidates = candidateRepository.findAll();
        model.addAttribute("election", election);
        model.addAttribute("candidates", candidates);
        return "updElection";
    }
    @PostMapping("/update")
    public String updateElection(@ModelAttribute Election election) {
        Election updElection = election;
      updElection.setStartDate(electionRepository.findAllById(election.getId()).getStartDate());

       electionRepository.save(updElection);
        return "redirect:/election/home";
    }
    @GetMapping("/register")
    public String registerPage(Model model) {
        Users user = new Users();
        model.addAttribute("user", user);
        return "register";
    }
    @PostMapping("/register")
    public String register(@ModelAttribute Users user) {
        String rawPassword = user.getPassword();
        String hashedPassword = passwordEncoder.encode(rawPassword);
        user.setPassword(hashedPassword);
        userRepository.save(user);
        StringBuilder report = new StringBuilder();
        report.append("Рады видеть вас на нашей платформе!\n");
        report.append("──────────────────────────────\n\n");
        report.append("\n📅 Спасибо за регистрацию. Хорошего дня!\n");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(user.getEmail());
        message.setSubject("Благодарим вас!");
        message.setText(report.toString());

        javaMailSender.send(message);
        return "redirect:/election/login";
    }

}
