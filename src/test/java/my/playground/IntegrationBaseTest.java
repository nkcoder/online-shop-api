package my.playground;

import io.restassured.RestAssured;
import my.playground.infrastructure.jwt.JwtUtil;
import my.playground.persistence.UserRepository;
import my.playground.persistence.entity.UserEntity;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDateTime;
import java.util.List;

/**
 * References: <br/>
 * <a href="https://testcontainers.com/guides/testing-spring-boot-rest-api-using-testcontainers/">Getting started with Testcontainers in a Java Spring Boot Project</a>
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class IntegrationBaseTest {

  private static final PostgreSQLContainer<?> SQL_CONTAINER = new PostgreSQLContainer<>(
      "postgres:15-alpine");

  @LocalServerPort
  public Integer port;

  @Autowired
  public UserRepository userRepository;

  @Autowired
  protected JwtUtil jwtUtil;

  @Autowired
  protected PasswordEncoder passwordEncoder;

  protected Long userId;

  @BeforeAll
  public static void beforeAll() {
    SQL_CONTAINER.start();
  }

  @AfterAll
  public static void afterAll() {
    SQL_CONTAINER.stop();
  }

  @DynamicPropertySource
  public static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", SQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", SQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", SQL_CONTAINER::getPassword);
  }

  @BeforeEach
  public void setup() {
    RestAssured.baseURI = "http://localhost";
    RestAssured.port = port;

    // For JWT authorization and authentication
    userId = userRepository.save(
        new UserEntity("test0User", "test0@email.com", passwordEncoder.encode("test0Pass!"), LocalDateTime.now())
    ).getId();
  }

  @AfterEach
  public void teardown() {
    if (userId != null) {
      userRepository.deleteById(userId);
    }
  }

  protected String generateJwtToken() {
    UserDetails userDetails = new org.springframework.security.core.userdetails.User(
        "test0User",
        passwordEncoder.encode("test0Pass!"),
        List.of()
    );
    return "Bearer " + jwtUtil.generateToken(userDetails);
  }
}
