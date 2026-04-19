package com.siga.registry

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class RegistryApplicationTests {

    @Test
    fun contextLoads() {
        // Test fails to compile/load context because RegistryApplication is missing
        // and @EnableEurekaServer is missing
    }

}
