package moon.odyssey;

import org.assertj.core.api.Assertions;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import lombok.extern.slf4j.Slf4j;


@RunWith(SpringRunner.class)
@AutoConfigureWebTestClient
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@Slf4j
public class UserControllerTest {

    @Autowired
    private WebTestClient testClient;

    @Test
    public void _0_init() {
        Assertions.assertThat(testClient).isNotNull();
    }

    @Test
    public void _1_getAllUsers_should_return_UserList() {

        testClient
            .get()
            .uri("/api/user")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBodyList(User.class)
            .consumeWith(result ->
                    result.getResponseBody()
                        .stream()
                        .forEach(user -> {
                            log.info("##### {}", user);
                            Assertions.assertThat(user).isInstanceOf(User.class);

                        })
            )
            ;
    }

    @Test
    public void _2_getUser_should_return_User() {

        testClient
            .get()
            .uri("/api/user/{USERID}"
                , "testUser"
            )
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(User.class)
            .consumeWith(result -> {
                log.info("##### {}", result.getResponseBody());
                Assertions.assertThat(result.getResponseBody())
                          .isInstanceOf(User.class);
                }
            )
            ;
    }

    @Test
    public void _3_getUnknownUser_should_return_400() {

        testClient
            .get()
            .uri("/api/user/{USERID}"
                , "unknownUser"
            )
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isBadRequest()
            .expectBody(String.class)
            .consumeWith(result -> log.info("##### {}", result.getResponseBody()))
            ;
    }

    @Test
    public void _4_getEncValue_should_return_decryptValue() {

        testClient
            .get()
            .uri("/api/user/encValue")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk()
            .expectBody(String.class)
            .consumeWith(result -> {
                log.info("##### {}", result.getResponseBody());
                Assertions.assertThat(result.getResponseBody())
                          .isEqualTo("testValue");
            })
            ;

    }

}