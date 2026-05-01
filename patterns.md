# Patrones de Diseño - Technology Microservice (Java 25 WebFlux)

## 1. Router (Endpoints WebFlux)

```java
// com.techms.router.ExampleRouter.java
@Configuration
public class ExampleRouter {

    @Bean
    public RouterFunction<ServerResponse> exampleRoutes(ExampleHandler handler) {
        return route(POST("/api/v1/examples").and(accept(APPLICATION_JSON)), handler::create)
                .andRoute(GET("/api/v1/examples/{id}"), handler::getById)
                .andRoute(GET("/api/v1/examples"), handler::listAll)
                .andRoute(PUT("/api/v1/examples/{id}"), handler::update)
                .andRoute(DELETE("/api/v1/examples/{id}"), handler::delete);
    }
}
```

## 2. Handler (Lógica de Negocio Reactiva)

```java
// com.techms.handler.ExampleHandler.java
@Component
@RequiredArgsConstructor
public class ExampleHandler {

    private final ExampleRepository exampleRepository;
    private final ExampleEventPublisher eventPublisher;

    public Mono<ServerResponse> create(ServerRequest request) {
        return request.bodyToMono(CreateExampleRequest.class)
                .flatMap(req -> exampleRepository.save(toEntity(req)))
                .flatMap(saved -> eventPublisher.publishCreatedEvent(saved)
                        .then(Mono.just(toResponse(saved))))
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED).bodyValue(response));
    }

    public Mono<ServerResponse> getById(ServerRequest request) {
        String id = request.pathVariable("id");
        return exampleRepository.findById(id)
                .map(this::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("Example " + id + " not found")));
    }

    public Mono<ServerResponse> listAll(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("1"));
        int pageSize = Integer.parseInt(request.queryParam("pageSize").orElse("20"));

        PaginationParams pagination = PaginationParams.of(page, pageSize);

        return exampleRepository.findAll(pagination)
                .collectList()
                .zipWith(exampleRepository.count())
                .map(tuple -> PaginatedResponse.of(
                        tuple.getT1().stream().map(this::toResponse).toList(),
                        tuple.getT2(),
                        pagination))
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> update(ServerRequest request) {
        String id = request.pathVariable("id");
        return request.bodyToMono(UpdateExampleRequest.class)
                .flatMap(req -> exampleRepository.findById(id)
                        .switchIfEmpty(Mono.error(new EntityNotFoundException("Example " + id + " not found")))
                        .flatMap(entity -> {
                            entity.setName(req.getName());
                            entity.setDescription(req.getDescription());
                            return exampleRepository.update(entity);
                        }))
                .map(this::toResponse)
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    public Mono<ServerResponse> delete(ServerRequest request) {
        String id = request.pathVariable("id");
        return exampleRepository.delete(id)
                .then(ServerResponse.noContent().build());
    }
}
```

## 3. GlobalExceptionHandler (WebFlux)

```java
// com.techms.exception.GlobalExceptionHandler.java
@Configuration
public class GlobalExceptionHandler {

    @Bean
    public RouterFunction<ServerResponse> exceptionRoutes() {
        return route(containsPath("/api/"), this::handleException);
    }

    private Mono<ServerResponse> handleException(ServerRequest request, Throwable ex) {
        if (ex instanceof EntityNotFoundException) {
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .bodyValue(ErrorResponse.of("NOT_FOUND", ex.getMessage()));
        }
        if (ex instanceof ValidationException) {
            return ServerResponse.status(HttpStatus.BAD_REQUEST)
                    .bodyValue(ErrorResponse.of("VALIDATION_ERROR", ex.getMessage()));
        }
        if (ex instanceof BusinessException) {
            return ServerResponse.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .bodyValue(ErrorResponse.of("BUSINESS_ERROR", ex.getMessage()));
        }
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(ErrorResponse.of("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}

// HandlerExceptionResolver alternative
@Component
public class WebExceptionHandler extends AbstractErrorWebExceptionHandler {

    @Override
    protected Mono<ServerResponse> renderError(WebExchange exchange) {
        Throwable ex = exchange.getAttribute(ERROR_ATTRIBUTE);
        if (ex instanceof EntityNotFoundException) {
            return ServerResponse.status(HttpStatus.NOT_FOUND)
                    .bodyValue(ErrorResponse.of("NOT_FOUND", ex.getMessage()));
        }
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(ErrorResponse.of("INTERNAL_ERROR", "An unexpected error occurred"));
    }
}
```

## 4. Custom Exceptions

```java
// com.techms.exception.custom exceptions
public class BaseApplicationException extends RuntimeException {
    private final String code;
    private final String message;

    public BaseApplicationException(String message, String code) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public String getCode() { return code; }
    @Override
    public String getMessage() { return message; }
}

public class EntityNotFoundException extends BaseApplicationException {
    public EntityNotFoundException(String message) {
        super(message, "NOT_FOUND");
    }
}

public class ValidationException extends BaseApplicationException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
}

public class BusinessException extends BaseApplicationException {
    public BusinessException(String message) {
        super(message, "BUSINESS_ERROR");
    }
}

public class DuplicateEntityException extends BaseApplicationException {
    public DuplicateEntityException(String message) {
        super(message, "DUPLICATE_ENTITY");
    }
}

public class KafkaProducerException extends BaseApplicationException {
    public KafkaProducerException(String message) {
        super(message, "KAFKA_PRODUCER_ERROR");
    }
}

public class UnauthorizedException extends BaseApplicationException {
    public UnauthorizedException(String message) {
        super(message, "UNAUTHORIZED");
    }
}
```

## 5. DTOs Genéricos

```java
// com.techms.dto.request.CreateExampleRequest.java
public record CreateExampleRequest(
        @NotBlank String name,
        String description
) {}

// com.techms.dto.request.UpdateExampleRequest.java
public record UpdateExampleRequest(
        @NotBlank String name,
        String description
) {}

// com.techms.dto.response.ExampleResponse.java
public record ExampleResponse(
        String id,
        String name,
        String description,
        Instant createdAt,
        Instant updatedAt
) {}

// com.techms.dto.response.PaginatedResponse.java
public record PaginatedResponse<T>(
        List<T> items,
        long total,
        int page,
        int pageSize,
        int totalPages
) {
    public static <T> PaginatedResponse<T> of(List<T> items, long total, PaginationParams pagination) {
        int totalPages = (int) Math.ceil((double) total / pagination.pageSize());
        return new PaginatedResponse<>(items, total, pagination.page(), pagination.pageSize(), totalPages);
    }
}

// com.techms.dto.common.PaginationParams.java
public record PaginationParams(int page, int pageSize) {
    public static PaginationParams of(int page, int pageSize) {
        return new PaginationParams(page, pageSize);
    }

    public int offset() { return (page - 1) * pageSize; }
    public int limit() { return pageSize; }
}

// com.techms.dto.response.ApiResponse.java
public record ApiResponse<T>(boolean success, T data, String error) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }
    public static <T> ApiResponse<T> fail(String error) {
        return new ApiResponse<>(false, null, error);
    }
}
```

## 6. Kafka Consumer (Recibir Eventos - WebFlux)

```java
// com.techms.kafka.consumer.KafkaConsumerConfig.java
@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ReceiverOptions<String, Map<String, Object>> receiverOptions() {
        ReceiverOptions<String, Map<String, Object>> options = ReceiverOptions.create();
        return options.subscription(Collections.singleton("auth.login.events"))
                .withKeyDeserializer(new StringDeserializer())
                .withValueDeserializer(new JsonDeserializer<>(Map.class));
    }

    @Bean
    public KafkaReceiver<String, Map<String, Object>> kafkaReceiver(ReceiverOptions<String, Map<String, Object>> options) {
        return KafkaReceiver.create(options);
    }
}

// com.techms.kafka.consumer.LoginEventConsumer.java
@Service
public class LoginEventConsumer {

    private final AuthService authService;

    public LoginEventConsumer(KafkaReceiver<String, Map<String, Object>> kafkaReceiver, AuthService authService) {
        this.authService = authService;
        kafkaReceiver.receive()
                .doOnNext(record -> processRecord(record))
                .subscribe();
    }

    private void processRecord(ReceiverRecord<String, Map<String, Object>> record) {
        Map<String, Object> value = record.value();
        LoginEventPayload payload = mapToPayload(value);
        authService.processLoginEvent(payload).block();
        record.receiverOffset().acknowledge();
    }

    private LoginEventPayload mapToPayload(Map<String, Object> map) {
        return new LoginEventPayload(
                (String) map.get("eventId"),
                (String) map.get("eventType"),
                (String) map.get("userId"),
                (String) map.get("email"),
                (String) map.get("identityProvider"),
                (Map<String, Object>) map.get("identityClaims"),
                Instant.now()
        );
    }
}
```

## 7. Kafka Producer (Envío de Eventos - WebFlux)

```java
// com.techms.kafka.producer.KafkaProducerConfig.java
@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public SenderOptions<String, Object> senderOptions() {
        SenderOptions<String, Object> options = SenderOptions.create();
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return options.withProducerProperties(props);
    }

    @Bean
    public KafkaSender<String, Object> kafkaSender(SenderOptions<String, Object> senderOptions) {
        return KafkaSender.create(senderOptions);
    }
}

// com.techms.kafka.producer.EventPublisher.java
@Service
public class EventPublisher {

    private final KafkaSender<String, Object> kafkaSender;

    public EventPublisher(KafkaSender<String, Object> kafkaSender) {
        this.kafkaSender = kafkaSender;
    }

    public Mono<Void> publish(String topic, String eventType, Object payload) {
        Map<String, Object> message = new HashMap<>();
        message.put("event_type", eventType);
        message.put("payload", payload);
        message.put("timestamp", Instant.now());

        SenderRecord<String, Object, String> record = SenderRecord.create(topic, null, UUID.randomUUID().toString(), message);

        return kafkaSender.send(Mono.just(record)).then();
    }
}
```

## 8. Flujo de Autenticación con Spring Security WebFlux

```java
// com.techms.security.config.SecurityConfig.java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain filterChain(ServerHttpSecurity http, JwtTokenProvider jwtTokenProvider) {
        return http
                .csrf(ServerCsrfToken::disable)
                .httpBasic(ServerHttpBasic::disable)
                .formLogin(ServerFormLogin::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(ServerWebExchangeSession::STATELESS))
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/auth/**", "/health").permitAll()
                        .anyExchange().authenticated())
                .addFilterAt(new JwtAuthenticationFilter(jwtTokenProvider), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

// com.techms.security.filter.JwtAuthenticationFilter.java
@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getPath().value();
        if (path.startsWith("/api/v1/auth")) {
            return chain.filter(exchange);
        }

        String token = extractToken(exchange.getRequest());

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication auth = jwtTokenProvider.getAuthentication(token);
            return chain.filter(exchange).contextWrite(ReactorSecurityContextHolder.withAuthentication(auth));
        }

        return chain.filter(exchange);
    }

    private String extractToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

// com.techms.security.provider.JwtTokenProvider.java
@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;
    private final SecretKey secretKey;

    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Instant now = Instant.now();
        Instant expiry = now.plus(Duration.ofHours(24));

        return Jwts.builder()
                .subject(username)
                .claim("roles", authentication.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();
        String username = claims.getSubject();
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }
}

// com.techms.security.service.AuthService.java
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EventPublisher eventPublisher;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public Mono<AuthResponse> login(LoginRequest request) {
        return userRepository.findByEmail(request.getEmail())
                .switchIfEmpty(Mono.error(new UnauthorizedException("Invalid credentials")))
                .filter(user -> passwordEncoder.matches(request.getPassword(), user.getPassword()))
                .switchIfEmpty(Mono.error(new UnauthorizedException("Invalid credentials")))
                .flatMap(user -> {
                    Authentication auth = new UsernamePasswordAuthenticationToken(
                            user.getEmail(), null, user.getAuthorities());
                    String token = jwtTokenProvider.generateToken(auth);
                    return publishLoginEvent(user, "LOCAL", Map.of())
                            .thenReturn(new AuthResponse(token, user.getEmail(), user.getRoles()));
                });
    }

    public Mono<Void> processLoginEvent(LoginEventPayload payload) {
        return userRepository.findByEmail(payload.getEmail())
                .switchIfEmpty(createUserFromIdentity(payload))
                .flatMap(user -> {
                    user.updateLastLogin();
                    return userRepository.save(user);
                })
                .then();
    }

    private Mono<UserEntity> createUserFromIdentity(LoginEventPayload payload) {
        return userRepository.save(UserEntity.builder()
                .email(payload.getEmail())
                .externalId(payload.getUserId())
                .identityProvider(payload.getIdentityProvider())
                .identityClaims(payload.getIdentityClaims())
                .build());
    }

    private Mono<Void> publishLoginEvent(UserEntity user, String provider, Map<String, Object> claims) {
        return eventPublisher.publish("auth.login.events", "user.login", Map.of(
                "eventId", UUID.randomUUID().toString(),
                "userId", user.getId(),
                "email", user.getEmail(),
                "timestamp", Instant.now()
        ));
    }
}

// LoginRequest.java / AuthResponse.java
public record LoginRequest(
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}

public record AuthResponse(String token, String email, List<String> roles) {}

// LoginEventPayload.java
public record LoginEventPayload(
        String eventId,
        String eventType,
        String userId,
        String email,
        String identityProvider,
        Map<String, Object> identityClaims,
        Instant timestamp
) {}
```

## 9. Repositorio Reactivo (R2DBC)

```java
// com.techms.repository.ExampleRepository.java
@Repository
public class ExampleRepository {

    private final DatabaseClient databaseClient;

    public ExampleRepository(DatabaseClient databaseClient) {
        this.databaseClient = databaseClient;
    }

    public Mono<ExampleEntity> save(ExampleEntity entity) {
        return databaseClient.insert().into("examples")
                .value("id", entity.getId())
                .value("name", entity.getName())
                .value("description", entity.getDescription())
                .value("created_at", entity.getCreatedAt())
                .value("updated_at", entity.getUpdatedAt())
                .then()
                .thenReturn(entity);
    }

    public Mono<ExampleEntity> findById(String id) {
        return databaseClient.select().from("examples")
                .matching(where("id").is(id))
                .fetch()
                .one()
                .map(row -> ExampleEntity.builder()
                        .id(row.get("id", String.class))
                        .name(row.get("name", String.class))
                        .description(row.get("description", String.class))
                        .createdAt(row.get("created_at", Instant.class))
                        .updatedAt(row.get("updated_at", Instant.class))
                        .build());
    }

    public Flux<ExampleEntity> findAll(PaginationParams pagination) {
        return databaseClient.select().from("examples")
                .fetch()
                .offset(pagination.offset())
                .limit(pagination.limit())
                .all()
                .map(row -> ExampleEntity.builder()
                        .id(row.get("id", String.class))
                        .name(row.get("name", String.class))
                        .description(row.get("description", String.class))
                        .createdAt(row.get("created_at", Instant.class))
                        .updatedAt(row.get("updated_at", Instant.class))
                        .build());
    }

    public Mono<ExampleEntity> update(ExampleEntity entity) {
        return databaseClient.update().table("examples")
                .matching(where("id").is(entity.getId()))
                .value("name", entity.getName())
                .value("description", entity.getDescription())
                .value("updated_at", Instant.now())
                .then()
                .thenReturn(entity);
    }

    public Mono<Void> delete(String id) {
        return databaseClient.delete().from("examples")
                .matching(where("id").is(id))
                .then();
    }

    public Mono<Long> count() {
        return databaseClient.select().from("examples")
                .fetch()
                .count();
    }
}
```

## 10. Test Unitarios (WebFlux)

```java
// com.techms.handler.ExampleHandlerTest.java
@ExtendWith(MockitoExtension.class)
class ExampleHandlerTest {

    @Mock
    private ExampleRepository exampleRepository;

    @Mock
    private ExampleEventPublisher eventPublisher;

    private ExampleHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ExampleHandler(exampleRepository, eventPublisher);
    }

    @Test
    void create_withValidRequest_returnsCreatedResponse() {
        CreateExampleRequest request = new CreateExampleRequest("Test", "Description");
        ExampleEntity savedEntity = ExampleEntity.builder()
                .id("uuid-123")
                .name("Test")
                .description("Description")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(exampleRepository.save(any(ExampleEntity.class))).thenReturn(Mono.just(savedEntity));
        when(eventPublisher.publishCreatedEvent(any())).thenReturn(Mono.empty());

        StepVerifier.create(handler.create(mockServerRequest(request)))
                .expectNextMatches(response -> response.id().equals("uuid-123"))
                .verifyComplete();

        verify(exampleRepository).save(any(ExampleEntity.class));
    }

    @Test
    void getById_whenNotFound_returnsError() {
        when(exampleRepository.findById("nonexistent")).thenReturn(Mono.empty());

        StepVerifier.create(handler.getById(mockServerRequest("nonexistent")))
                .expectError(EntityNotFoundException.class)
                .verify();
    }

    @Test
    void listAll_withPagination_returnsPaginatedResponse() {
        PaginationParams pagination = PaginationParams.of(1, 10);
        List<ExampleEntity> entities = List.of(
                ExampleEntity.builder().id("1").name("First").build(),
                ExampleEntity.builder().id("2").name("Second").build()
        );

        when(exampleRepository.findAll(pagination)).thenReturn(Flux.fromIterable(entities));
        when(exampleRepository.count()).thenReturn(Mono.just(2L));

        StepVerifier.create(handler.listAll(mockServerRequest(pagination)))
                .expectNextMatches(response -> response.items().size() == 2 && response.page() == 1)
                .verifyComplete();
    }

    private ServerRequest mockServerRequest(CreateExampleRequest body) {
        ServerRequest request = mock(ServerRequest.class);
        when(request.bodyToMono(CreateExampleRequest.class)).thenReturn(Mono.just(body));
        return request;
    }

    private ServerRequest mockServerRequest(String id) {
        ServerRequest request = mock(ServerRequest.class);
        when(request.pathVariable("id")).thenReturn(id);
        return request;
    }

    private ServerRequest mockServerRequest(PaginationParams pagination) {
        ServerRequest request = mock(ServerRequest.class);
        when(request.queryParam("page")).thenReturn(Optional.of(String.valueOf(pagination.page())));
        when(request.queryParam("pageSize")).thenReturn(Optional.of(String.valueOf(pagination.pageSize())));
        return request;
    }
}

// com.techms.security.JwtTokenProviderTest.java
@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {

    @Mock
    private UserDetailsService userDetailsService;

    private JwtTokenProvider jwtTokenProvider;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        jwtTokenProvider = new JwtTokenProvider(userDetailsService, secretKey);
    }

    @Test
    void generateToken_withValidAuthentication_returnsToken() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user@example.com");
        when(userDetails.getAuthorities()).thenReturn(List.of(new SimpleGrantedAuthority("ROLE_USER")));
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        String token = jwtTokenProvider.generateToken(auth);

        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals("user@example.com", jwtTokenProvider.getAuthentication(token).getName());
    }

    @Test
    void validateToken_withInvalidToken_returnsFalse() {
        assertFalse(jwtTokenProvider.validateToken("invalid.token.here"));
    }
}

// com.techms.security.AuthServiceTest.java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, eventPublisher, jwtTokenProvider, passwordEncoder);
    }

    @Test
    void login_withValidCredentials_returnsToken() {
        LoginRequest request = new LoginRequest("user@example.com", "password123");
        UserEntity user = UserEntity.builder()
                .id("user-123")
                .email("user@example.com")
                .password("encoded-password")
                .roles(List.of("ROLE_USER"))
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("password123", "encoded-password")).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(Authentication.class))).thenReturn("jwt-token");
        when(eventPublisher.publish(any(), any(), any())).thenReturn(Mono.empty());

        StepVerifier.create(authService.login(request))
                .expectNextMatches(response -> response.token().equals("jwt-token"))
                .verifyComplete();
    }

    @Test
    void login_withInvalidPassword_returnsUnauthorized() {
        LoginRequest request = new LoginRequest("user@example.com", "wrongpassword");
        UserEntity user = UserEntity.builder()
                .email("user@example.com")
                .password("encoded-password")
                .build();

        when(userRepository.findByEmail("user@example.com")).thenReturn(Mono.just(user));
        when(passwordEncoder.matches("wrongpassword", "encoded-password")).thenReturn(false);

        StepVerifier.create(authService.login(request))
                .expectError(UnauthorizedException.class)
                .verify();
    }
}
```

## 11. Test de Integración (WebFlux)

```java
// com.techms.integration.ExampleIntegrationTest.java
@SpringBootTest
@Testcontainers
@AutoConfigureWebFlux
class ExampleIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("techms_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://" + postgres.getHost() + ":" + postgres.getMappedPort(5432) + "/techms_test");
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);
    }

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void createExample_withValidData_returns201() {
        CreateExampleRequest request = new CreateExampleRequest("Test", "Description");

        webTestClient.post().uri("/api/v1/examples")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .header("Authorization", "Bearer " + getAuthToken())
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Test")
                .jsonPath("$.description").isEqualTo("Description");
    }

    @Test
    void createExample_withInvalidData_returns400() {
        CreateExampleRequest request = new CreateExampleRequest("", null);

        webTestClient.post().uri("/api/v1/examples")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    void getExample_withValidId_returns200() {
        String exampleId = createTestExample();

        webTestClient.get().uri("/api/v1/examples/{id}", exampleId)
                .header("Authorization", "Bearer " + getAuthToken())
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo(exampleId);
    }

    private String getAuthToken() {
        return "test-jwt-token";
    }

    private String createTestExample() {
        return "test-id-123";
    }
}

// com.techms.integration.KafkaIntegrationTest.java
@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"auth.login.events"})
class KafkaIntegrationTest {

    @Autowired
    private KafkaSender<String, Object> kafkaSender;

    @Autowired
    private AuthService authService;

    @Test
    void loginEvent_isConsumed_andProcessed() throws InterruptedException {
        LoginEventPayload payload = new LoginEventPayload(
                "event-123",
                "user.login",
                "user-456",
                "test@example.com",
                "GOOGLE",
                Map.of("sub", "google-id"),
                Instant.now()
        );

        CountDownLatch latch = new CountDownLatch(1);
        when(authService.processLoginEvent(any())).thenReturn(Mono.empty().doOnSuccess(v -> latch.countDown()));

        Map<String, Object> message = Map.of(
                "event_type", "user.login",
                "payload", payload
        );

        kafkaSender.send(Mono.just(SenderRecord.create("auth.login.events", null, "key", message)))
                .block();

        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}
```

## 12. Validaciones con Jakarta Validation (WebFlux)

```java
// Validación en Handler
public Mono<ServerResponse> create(ServerRequest request) {
    return request.bodyToMono(CreateExampleRequest.class)
            .validate()
            .flatMap(validRequest -> exampleRepository.save(toEntity(validRequest)))
            .flatMap(saved -> ServerResponse.status(HttpStatus.CREATED).bodyValue(toResponse(saved)))
            .onError(ConstraintViolationException.class, ex -> ServerResponse.badRequest()
                    .bodyValue(ErrorResponse.of("VALIDATION_ERROR", ex.getMessage())));
}

// Custom Validation Mono
public <T> Mono<T> validate(T request) {
    return Mono.defer(() -> {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<T>> violations = validator.validate(request);

        if (!violations.isEmpty()) {
            String errors = violations.stream()
                    .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                    .collect(Collectors.joining(", "));
            return Mono.error(new ValidationException(errors));
        }
        return Mono.just(request);
    });
}
```

## 13. Middleware / WebFilter (WebFlux)

```java
// com.techms.middleware.LoggingWebFilter.java
@Component
public class LoggingWebFilter implements WebFilter {

    private final Logger logger = LoggerFactory.getLogger(LoggingWebFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        long startTime = System.currentTimeMillis();
        String method = exchange.getRequest().getMethod().name();
        String path = exchange.getRequest().getPath().value();

        logger.info("{} {} started", method, path);

        return chain.filter(exchange)
                .doOnSuccess(aVoid -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logger.info("{} {} completed with status {} in {}ms",
                            method, path, exchange.getResponse().getStatusCode(), duration);
                })
                .onErrorResume(ex -> {
                    long duration = System.currentTimeMillis() - startTime;
                    logger.error("{} {} failed after {}ms: {}", method, path, duration, ex.getMessage());
                    return Mono.error(ex);
                });
    }
}
```

## 14. Estructura de Proyecto Sugerida

```
src/main/java/com/techms/
├── TechMsApplication.java
├── router/
│   └── ExampleRouter.java
├── handler/
│   └── ExampleHandler.java
├── service/
│   └── AuthService.java
├── repository/
│   └── ExampleRepository.java
├── entity/
│   └── ExampleEntity.java
├── dto/
│   ├── request/
│   │   └── CreateExampleRequest.java
│   ├── response/
│   │   └── ExampleResponse.java
│   └── common/
│       └── PaginationParams.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── custom/
│       ├── BaseApplicationException.java
│       ├── EntityNotFoundException.java
│       └── ValidationException.java
├── kafka/
│   ├── consumer/
│   │   ├── KafkaConsumerConfig.java
│   │   └── LoginEventConsumer.java
│   └── producer/
│       ├── KafkaProducerConfig.java
│       └── EventPublisher.java
├── security/
│   ├── config/
│   │   └── SecurityConfig.java
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java
│   ├── provider/
│   │   └── JwtTokenProvider.java
│   └── dto/
│       ├── LoginRequest.java
│       └── AuthResponse.java
├── middleware/
│   └── LoggingWebFilter.java
└── config/
    └── AppConfig.java

src/test/java/com/techms/
├── handler/
│   └── ExampleHandlerTest.java
├── service/
│   └── AuthServiceTest.java
├── security/
│   └── JwtTokenProviderTest.java
└── integration/
    ├── ExampleIntegrationTest.java
    └── KafkaIntegrationTest.java
```