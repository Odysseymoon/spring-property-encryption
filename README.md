# Spring Boot Encryption Template
---

### Framework
- Java 1.8+
- Spring Boot 2.2.x
- Spring WebFlux
- Spring Data JPA
- H2 Database
- jasypt-spring-boot-starter [Link](https://github.com/ulisesbocchio/jasypt-spring-boot)
- Bouncy Castle Encryption Library [Link](https://bouncycastle.org/documentation.html)

### How To Use

#### Add Maven Dependency

- `pom.xml`
```xml
<!-- Encryption Library -->
<dependency>
    <groupId>com.github.ulisesbocchio</groupId>
    <artifactId>jasypt-spring-boot-starter</artifactId>
    <version>3.0.2</version>
</dependency>
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.64</version>
</dependency>
```

#### Define Encryptor Bean

```java
@Bean("jasyptStringEncryptor")
public StringEncryptor stringEncryptor(Environment environment) {
    PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
    SimpleStringPBEConfig config = new SimpleStringPBEConfig();
    config.setPassword(environment.getProperty("jasypt.encryptor.password", "MyPassword"));  //Change Password
    config.setAlgorithm("PBEWithSHA1AndDESede");
    config.setKeyObtentionIterations("1000");
    config.setPoolSize("1");
    config.setProvider(new BouncyCastleProvider());
    config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
    config.setStringOutputType("base64");
    encryptor.setConfig(config);
    return encryptor;
}
```

#### Test Encryption

```java
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class EncryptionApplicationTest {

    @Autowired
    private StringEncryptor jasyptStringEncryptor;

    @Test
    public void _0_testInit() {
        assertThat(jasyptStringEncryptor).isNotNull();
    }

    @Test
    public void _1_encrypt_decrypt() {

        String orgText = "testValue";

        String encText = jasyptStringEncryptor.encrypt(orgText);

        log.info("##### encText : {}", encText);

        String decText = jasyptStringEncryptor.decrypt(encText);

        Assertions.assertThat(decText).isEqualTo(orgText);
    }

}
```

#### Using Encryption at Properties

- `application.yaml`
```yaml
spring:
  profiles: local
  datasource:
    url: jdbc:h2:mem:testDB;mode=MySQL;DB_CLOSE_DELAY=-1
    username: ENC(bn7gNC9SEFcbStq4dDtVuw==)               #Encrypted Property
    password: ENC(px9ud7xYJVB4/MW8CSH7kw==)               #Encrypted Property
    driver-class-name: org.h2.Driver

my:
  test:
    encValue: ENC(lXghc3bqo998+jeOxIB2S6LhMtHZZZ75)       #Encrypted Property
```

#### Using Encryption at JPA Converter

- `StringEncryptConverter`
```java
@Converter
public class StringEncryptConverter implements AttributeConverter<String, String> {

    private static StringEncryptor stringEncryptor;

    @Autowired
    @Qualifier("jasyptStringEncryptor")
    public void setStringEncryptor(StringEncryptor encryptor) {
        StringEncryptConverter.stringEncryptor = encryptor;
    }

    @Override
    public String convertToDatabaseColumn(String entityString) {

        return Optional.ofNullable(entityString)
                       .filter(s -> !s.isEmpty())
                       .map(StringEncryptConverter.stringEncryptor::encrypt)
                       .orElse("");
    }

    @Override
    public String convertToEntityAttribute(String dbString) {

        return Optional.ofNullable(dbString)
                       .filter(s -> !s.isEmpty())
                       .map(StringEncryptConverter.stringEncryptor::decrypt)
                       .orElse("");
    }
}
```

- Entity Usage `User.java`
```java
@Entity
@Table(name = "user")
@Data
@ToString(exclude = {"password"})
@EqualsAndHashCode(of = {"userId"})
@NoArgsConstructor
@AllArgsConstructor
public class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo;

    private String userId;

    @JsonIgnore
    @Convert(converter = StringEncryptConverter.class)  //Encrypt data when save and decrypt data when load
    private String password;

}
```

