package com.saas.saas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SaasApplicationTests {

	@org.springframework.beans.factory.annotation.Autowired
	private com.saas.saas.repository.UserRepository userRepository;

	@jakarta.persistence.PersistenceContext
	private jakarta.persistence.EntityManager entityManager;

	@Test
	void contextLoads() {
	}

	@org.springframework.transaction.annotation.Transactional
	@org.springframework.test.annotation.Rollback(false)
	@Test
	void createTenant121() {
		entityManager.createNativeQuery("INSERT INTO tenants(id, name) VALUES (121, 'Tenant 121') ON CONFLICT (id) DO NOTHING;")
				.executeUpdate();
		System.out.println("TENANT 121 CREATED OR ALREADY EXISTS!");
	}

	@org.springframework.beans.factory.annotation.Autowired
	private org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder passwordEncoder;

	@Test
	void verifyAdarshUser() {
		userRepository.findByEmail("953623104017@ritrjpm.ac.in").ifPresent(user -> {
			user.setEmailVerified(true);
			userRepository.save(user);
			System.out.println("VERIFIED ADARSH USER IN DATABASE!");
		});
	}

	@Test
	void listAllUsers() {
		userRepository.findAll().forEach(user -> {
			System.out.println("USER_ROW: ID=" + user.getId() + ", Email=" + user.getEmail() + ", Role=" + user.getRole() + ", TenantID=" + (user.getTenant() != null ? user.getTenant().getId() : "null"));
		});
	}

	@Test
	void resetAdminPassword() {
		userRepository.findByEmail("admin@unified.com").ifPresent(user -> {
			user.setPassword(passwordEncoder.encode("Balaji@2005"));
			user.setEmailVerified(true);
			userRepository.save(user);
			System.out.println("RESET ADMIN PASSWORD TO Balaji@2005!");
		});
	}
}
