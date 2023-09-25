package my.playground.user;

import lombok.RequiredArgsConstructor;
import my.playground.persistence.UserRepository;
import my.playground.persistence.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public User register(UserRegistrationReq registrationReq) {
    UserEntity userEntity = new UserEntity(registrationReq.userName(), registrationReq.email(),
        passwordEncoder.encode(registrationReq.password()), LocalDateTime.now());
    UserEntity userSaved = userRepository.save(userEntity);
    return new User(userSaved.getId(), userSaved.getUserName(), userSaved.getEmail(),
        userSaved.getPassword(), userSaved.getDateRegistered());
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository.findByUserName(username).map(
            userEntity -> new org.springframework.security.core.userdetails.User(
                userEntity.getUserName(), userEntity.getPassword(), List.of()))
        .orElseThrow(() -> new UsernameNotFoundException("user not exist"));
  }

  public User updateUser(Long userId, UserUpdateReq updateReq) {
    UserEntity newEntity = userRepository.findById(userId).map(existingUser -> {
      UserEntity newUser = new UserEntity(updateReq.userName(), updateReq.email(),
          updateReq.password(), existingUser.getDateRegistered());
      newUser.setId(userId);
      return newUser;
    }).orElseThrow(() -> new UserNotFoundException("User doesn't exist: " + userId));

    UserEntity updatedEntity = userRepository.save(newEntity);
    return new User(userId, updatedEntity.getUserName(), updatedEntity.getEmail(), null,
        updatedEntity.getDateRegistered());
  }
}

