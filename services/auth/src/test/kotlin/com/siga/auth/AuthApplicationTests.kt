package com.siga.auth

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class AuthApplicationTests {

    @Test
    fun contextLoads() {
        // Assert correct Application context loaded with JPA and Eureka
    }
}
