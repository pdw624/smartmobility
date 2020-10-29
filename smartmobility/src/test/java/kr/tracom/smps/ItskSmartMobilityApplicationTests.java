package kr.tracom.smps;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class ItskSmartMobilityApplicationTests {
	
	@Autowired
	private MockMvc mockMvc;

	@Test
	public void selectActionList() throws Exception {
		mockMvc.perform(get("/api/v1/action"))
				.andExpect(status().isOk())
				.andDo(print());
	}
}
