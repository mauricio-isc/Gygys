package com.gym;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Contexto no levanta hasta tener BD real / seguridad")
class ManagamentApplicationTests {

	@Test
	void contextLoads() {
        //pronto implementare pruebas unitarias, para aprender y repazar lo que vi durante mi trayectoria :D
	}

}
