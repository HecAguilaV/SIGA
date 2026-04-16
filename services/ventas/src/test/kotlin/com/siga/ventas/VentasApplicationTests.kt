package com.siga.ventas

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class VentasApplicationTests {

    @Test
    fun contextLoads() {
        // Assert context loads with Feign, Eureka, Security and JPA correctly
    }
}
