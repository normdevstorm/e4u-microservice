package com.renting.item_service;

import com.renting.item_service.model.response.auth.UserModelFromJwtPayload;
import com.renting.item_service.service.JwtService;
import io.github.cdimascio.dotenv.Dotenv;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.UUID;

@Slf4j
@SpringBootTest
class ItemServiceApplicationTests {
	@Autowired
	private JwtService jwtService;

	@Test
	void contextLoads() {
//		Dotenv dotenv = Dotenv.configure().filename("../../../.env").load();
//		dotenv.entries().forEach((entry) -> {
//			System.setProperty(entry.getKey(), entry.getValue());
//		});

		String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6IjAzYTI4MTc0LTYzNjEtNDM2NC1iMTc0LTU2ODhjNDY0Y2UyZCIsInJvbGUiOiJPV05FUiIsImF1dGhvcml0aWVzIjpbeyJhdXRob3JpdHkiOiJpdGVtOnVwZGF0ZSJ9LHsiYXV0aG9yaXR5IjoiaXRlbTpkZWxldGUifSx7ImF1dGhvcml0eSI6Iml0ZW06cmVhZCJ9LHsiYXV0aG9yaXR5IjoiaXRlbTpjcmVhdGUifSx7ImF1dGhvcml0eSI6IlJPTEVfT1dORVIifV0sInZlcnNpb24iOjQsInVzZXJuYW1lIjoibm9ybV9vd25lcl8xIiwic3ViIjoibm9ybV9vd25lcl8xIiwiaWF0IjoxNzU2NTY2MjYwLCJleHAiOjE3NTY1NjcxNjB9.vguZAsJgYO4rmch_jXopGTxx2-w2Vp5Vz_IcSded5HR4oq5cZPFmGep1ichhUoiuVbXuSCcRlSzmGbzoC9FDNT6qPmFKH-H9BCi3KpF7w3v8y1MeCSqWMAE_qh4otsPrlZKpTlKcCOtb9N4bJ_jNzKaGwZkGbYNcx36z3ydciEw1jcj4Z_NmOdzQWxZuTIcJNWJeMMb58AGvDrOj-zoD0N4t0TQmIPpa_FDyvCw67I-Eu5syKuAKXosGV_Pb3U2HnqpC6dvcLMl32dS0feclXCY-9hWxZcJFPltfy8ufQurNGy4CBLc00NbyuPPNM77UZWxU-xOZtaWEB-hyHDFGEw";
		Claims claims = jwtService.extractClaim(token);
		UserModelFromJwtPayload user = new UserModelFromJwtPayload();
		user.setId(UUID.fromString(claims.get("id", String.class)));
		user.setUsername(claims.get("username", String.class));
		user.setAuthorities(claims.get("authorities", List.class));
		user.setRole(claims.get("role", String.class));
		log.info(user.getId().toString());
	}

}
