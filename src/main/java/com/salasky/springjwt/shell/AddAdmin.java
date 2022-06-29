package com.salasky.springjwt.shell;

import com.salasky.springjwt.models.ERole;
import com.salasky.springjwt.models.Role;
import com.salasky.springjwt.models.User;
import com.salasky.springjwt.models.payload.response.MessageResponse;
import com.salasky.springjwt.repository.RoleRepository;
import com.salasky.springjwt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import static com.salasky.springjwt.models.ERole.*;

@ShellComponent
public class AddAdmin {


    private PasswordEncoder encoder;
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    @Autowired
    public AddAdmin(PasswordEncoder encoder, RoleRepository roleRepository, UserRepository userRepository) {
        this.encoder = encoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    @ShellMethod(value = "Для добавления администратора,ввведите команду newadmin с параметрами\n" +
            "                        username password email", key = "newadmin")
    public String newadmin(String username,String password,@ShellOption(defaultValue="admin@gmail.com") String email) {


        if (userRepository.existsByUsername(username)){
            return "Username уже используется!";
        }

        if (userRepository.existsByEmail(email)) {
            return "Email уже используется!";
        }


        User user = new User(username, email, encoder.encode(password));

        Role userRole =roleRepository.findByName(ROLE_USER).get();
        Role modRole = roleRepository.findByName(ROLE_MODERATOR).get();
        Role adminRole = roleRepository.findByName(ROLE_ADMIN).get();

        Set<Role> roles=new HashSet<>();
        roles.add(userRole);roles.add(modRole);roles.add(adminRole);
        user.setRoles(roles);
        var saveuser=userRepository.save(user);
        return "Добавлен администратор с username "+saveuser.getUsername();

    }
}





