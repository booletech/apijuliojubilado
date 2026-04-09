package br.edu.infnet.JulioJubiladoapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
		"feature.remoteCalls.enabled=false",
		"spring.datasource.url=jdbc:h2:mem:juliojubilado;MODE=MSSQLServer;DB_CLOSE_DELAY=-1",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@ActiveProfiles("test")
class JulioJubiladoapiApplicationTests {

	@Test
	void contextLoads() {
	}

}
