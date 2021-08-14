package com.example.taskmanager.service;


import com.example.taskmanager.model.Todo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import javassist.NotFoundException;
import com.example.taskmanager.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.taskmanager.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    public List<User> getUsers(){
        return this.userRepository.findAll();
    }
    public User addUser(User user){
       try{
            this.getUserByUsername(user.getUsername());
            throw new Exception("User with this username already exists");
       }catch(Exception e){
           user.setPassword(passwordEncoder.encode(user.getPassword()));
           return this.userRepository.save(user);
       }

    }
    public User getUserById(long userId) throws NotFoundException{
        return this.userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("User with this id not found"));
    }

    public User getUserByUsername(String username) throws NotFoundException{
        return this.userRepository.findUserByUsername(username)
                .orElseThrow(()-> new NotFoundException("User with this id not found"));
    }

    public User editUserInfo(User newUser) throws NotFoundException{
        User userToEdit = this.userRepository.findById(newUser.getId())
                .orElseThrow(()-> new NotFoundException("user not found"));
        userToEdit.setUsername(newUser.getUsername());
        userToEdit.setSurname(newUser.getSurname());
        userToEdit.setName(newUser.getName());
        return userToEdit;
    }
    public User editUserRole(long userId,String role) throws NotFoundException{
        User userToEdit = this.userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("user not found"));
        userToEdit.setRole("ROLE_ "+role);
        return userToEdit;
    }


    public User applyPatchToUser(JsonPatch patch, User targetUser) throws JsonPatchException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode patched = patch.apply(objectMapper.convertValue(targetUser, JsonNode.class));
        return objectMapper.treeToValue(patched, User.class);
    }

    public User updateUserPassword(long userId,String oldPassword, String newPassword) throws NotFoundException {
        User userToEdit = this.userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("user not found"));
        if(passwordEncoder.matches(userToEdit.getPassword(),oldPassword)){
            userToEdit.setPassword(passwordEncoder.encode(newPassword));
            return userToEdit;
        }else{
            throw new NotFoundException("user old password is wrong");
        }
    }
    public User deleteUser(long userId) throws NotFoundException{
        User userToDelete = this.userRepository.findById(userId)
                .orElseThrow(()-> new NotFoundException("user not found"));
        userRepository.deleteById(userId);
        return userToDelete;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

           User user = this.userRepository.findUserByUsername(username)
                   .orElseThrow(()->  new UsernameNotFoundException("User not found"));
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(user.getRole()));
        return new org.springframework.security.core.userdetails.User(user.getUsername(),user.getPassword(),authorities);

    }
}
