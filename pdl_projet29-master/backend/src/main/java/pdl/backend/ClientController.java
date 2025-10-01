package pdl.backend;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import pdl.backend.models.MyUserDetails;
import pdl.backend.models.User;

@Controller
public class ClientController {
    @Autowired
    private MyUserDetailsService userService;
    

    @RequestMapping("/user")
    public String user() {
        return "user";
    }

    @RequestMapping("/admin")
    public String admin() {
       
        return "admin";
    }
    @RequestMapping(value = "/login")
    public String login() {
        // custom logic before showing login page...
         
        return "login";
    }
    @RequestMapping(value="/registration", method = RequestMethod.GET)
    public ModelAndView registration(){
        ModelAndView modelAndView = new ModelAndView();
        User user = new User();
        modelAndView.addObject("user", user);
        modelAndView.setViewName("registration");
        return modelAndView;
    }
    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public ModelAndView createNewUser(@Validated User user, BindingResult bindingResult) {
        String [] usersName = user.getUserName().split(",");
        System.out.println(user.getPassword());
        ModelAndView modelAndView = new ModelAndView();
        Optional<User> userExists = userService.userRepository.findByUserName(usersName[0]);
        
        if (userExists.isEmpty() == false ) {
            bindingResult
                    .rejectValue("userName", "error.user",
                            "There is already aa user registered with the user name provided");
        }
        if (bindingResult.hasErrors()) {
            modelAndView.setViewName("registration");
        } else {
            modelAndView.addObject("successMessage", "User has been registered successfully");
            String [] passWords = user.getPassword().split(",");
            String [] names = user.getName().split(",");
            String [] lastNames = user.getLastName().split(",");
            String [] emails = user.getEmail().split(",");
            User newUser = new User();
            newUser.setUserName(usersName[0]);
            newUser.setActive(true);
            newUser.setPassword(passWords[0]);
            newUser.setRoles("ROLE_USER");
            newUser.setEmail(emails[0]);
            newUser.setLastName(lastNames[0]);
            newUser.setName(names[0]);
            userService.userRepository.saveAndFlush(newUser);
            modelAndView.addObject("user", newUser);
            modelAndView.setViewName("registration");

        }
        return modelAndView;
    }

    
  
}

