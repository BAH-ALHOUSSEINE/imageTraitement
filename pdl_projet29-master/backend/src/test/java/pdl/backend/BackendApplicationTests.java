package pdl.backend;

import static org.assertj.core.api.Assertions.assertThat;
import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@SpringBootTest
class BackendApplicationTests {
/*	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ImageDao imageDao;
	@Autowired
	private ImageController controller;
	@Autowired
	private WebApplicationContext webApplicationContext;
*/
	@Test
	void contextLoads() {
		//assertThat(controller).isNotNull();
	}

/*	@Test
	public void testGetImage() throws Exception { // test of get image
		Optional<Image> img = this.imageDao.retrieve(0);
		if (img.isPresent()) {
			assertThat(img.get().getId()).isEqualTo(0);
			mockMvc.perform(get("/images/{id}", 0)).andExpect(status().isCreated());
		}

		mockMvc.perform(get("/images/{id}", 10000)).andExpect(status().isNotFound());
		mockMvc.perform(get("/images/{id}", -1)).andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteImage() throws Exception { // test of delete image
		Optional<Image> img = this.imageDao.retrieve(0);
		if (img.isPresent()) {
			assertThat(img.get().getId()).isEqualTo(0);
			mockMvc.perform(delete("/images/{id}", 0)).andExpect(status().isOk());
		}
	}*/

	/*
	 * @Test public void whenFileUploaded_thenVerifyStatus() // test of multiparte
	 * file Upload throws Exception { MockMultipartFile file = new
	 * MockMultipartFile( "file", "hello.txt", MediaType.TEXT_PLAIN_VALUE,
	 * "Hello, World!".getBytes() );
	 * 
	 * MockMvc mockMvc =
	 * MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
	 * mockMvc.perform(multipart("/upload").file(file)) .andExpect(status().isOk());
	 * }
	 
	@BeforeAll
	public static void reset() {
		// reset Image class static counter
		ReflectionTestUtils.setField(Image.class, "count", Long.valueOf(0));
	}*/

}
