package moon.odyssey;

import org.assertj.core.api.Assertions;
import org.jasypt.encryption.StringEncryptor;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import lombok.extern.slf4j.Slf4j;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;

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