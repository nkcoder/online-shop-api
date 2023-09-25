package my.playground.persistence.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Table("users")
@RequiredArgsConstructor
@Getter
public class UserEntity {
  private final String userName;
  private final String email;
  private final String password;
  private final LocalDateTime dateRegistered;
  @Id
  @Setter
  private Long id;
}
