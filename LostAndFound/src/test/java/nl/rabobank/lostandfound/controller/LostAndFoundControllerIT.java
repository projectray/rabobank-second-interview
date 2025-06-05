package nl.rabobank.lostandfound.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.rabobank.lostandfound.model.Claim;
import nl.rabobank.lostandfound.model.User;
import nl.rabobank.lostandfound.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("dev")
@SpringBootTest
@AutoConfigureMockMvc
class LostAndFoundControllerIT {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private UserRepository userRepository;


  @BeforeEach
  void setUp() {
    // Create a test user with admin role
    if (userRepository.findByUsername("admin").isEmpty()) {
      User admin = new User();
      admin.setUsername("admin");
      admin.setPassword("password"); // Noop password encoder for testing
      admin.setRole("ADMIN");
      userRepository.save(admin);
    }
  }

  @Test
  void claimItemSuccessTest() throws Exception {
    //setUp
    File file = ResourceUtils.getFile("classpath:lost_items.txt");
    byte[] fileContent = Files.readAllBytes(file.toPath());
    MockMultipartFile mockFile = new MockMultipartFile("file", "lost_items.txt", "text/plain", fileContent);

    mockMvc.perform(multipart("/api/admin/upload-items").file(mockFile)
        .with(httpBasic("admin", "password"))
        .contentType(MediaType.MULTIPART_FORM_DATA))
      .andExpect(status().isOk())
      .andReturn();

    Claim claim = new Claim();
    claim.setUserId(1L);
    claim.setItemId(1L);
    claim.setClaimedQuantity(1);

    String json = objectMapper.writeValueAsString(claim);
    MvcResult result = mockMvc.perform(post("/api/claim-item").content(json).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isOk())
      .andReturn();

    //Assert that the claim was successful
    assertEquals("Claim report successfully.", result.getResponse().getContentAsString());
  }

  @Test
  void claimItemFailedTest() throws Exception {
    //setUp
    File file = ResourceUtils.getFile("classpath:lost_items.txt");
    byte[] fileContent = Files.readAllBytes(file.toPath());
    MockMultipartFile mockFile = new MockMultipartFile("file", "lost_items.txt", "text/plain", fileContent);

    mockMvc.perform(multipart("/api/admin/upload-items").file(mockFile)
        .with(httpBasic("admin", "password"))
        .contentType(MediaType.MULTIPART_FORM_DATA))
      .andExpect(status().isOk())
      .andReturn();

    Claim claim = new Claim();
    claim.setUserId(1L);
    claim.setItemId(1L);
    claim.setClaimedQuantity(3);

    String json = objectMapper.writeValueAsString(claim);
    MvcResult result = mockMvc.perform(post("/api/claim-item").content(json).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andReturn();

    //Assert that the claim was failed
    assertEquals("The claim quantity is greater than the quantity of the item in stock", result.getResponse().getContentAsString());
  }

  @Test
  void authenticationFailedTest() throws Exception {
    File file = ResourceUtils.getFile("classpath:lost_items.txt");
    byte[] fileContent = Files.readAllBytes(file.toPath());
    MockMultipartFile mockFile = new MockMultipartFile("file", "lost_items.txt", "text/plain", fileContent);

    mockMvc.perform(multipart("/api/admin/upload-items").file(mockFile)
        .contentType(MediaType.MULTIPART_FORM_DATA))
      .andExpect(status().is4xxClientError());
  }

  @Test
  void claimItemInValidRequestBodyTest() throws Exception {
    String json = objectMapper.writeValueAsString(new Claim());
    MvcResult result = mockMvc.perform(post("/api/claim-item").content(json).contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest())
      .andReturn();

    //Assert that the claim was failed
    assertTrue(result.getResponse().getContentAsString().contains("UserId cannot be null"));
    assertTrue(result.getResponse().getContentAsString().contains("ItemId cannot be null"));
    assertTrue(result.getResponse().getContentAsString().contains("Quantity of the claimed item cannot be null"));
  }
}
