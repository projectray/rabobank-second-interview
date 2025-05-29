package nl.rabobank.lostandfound.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import nl.rabobank.lostandfound.model.Claim;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class LostAndFoundControllerIT {

  @Autowired
  private MockMvc mockMvc;
  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void claimItemSuccessTest() throws Exception {
    //setUp
    String lostItems = "ItemName: Laptop\nQuantity: 1\nPlace: Taxi\nItemName: Headphones\nQuantity: 2\nPlace: Railway station\nItemName: Jewels\nQuantity: 4\nPlace: Airport\nItemName: Laptop\nQuantity: 1\nPlace: Airport";
    MockMultipartFile mockFile = new MockMultipartFile("file", "lostItems.txt", "text/plain", lostItems.getBytes());

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
    String lostItems = "ItemName: Laptop\nQuantity: 1\nPlace: Taxi\nItemName: Headphones\nQuantity: 2\nPlace: Railway station\nItemName: Jewels\nQuantity: 4\nPlace: Airport\nItemName: Laptop\nQuantity: 1\nPlace: Airport";
    MockMultipartFile mockFile = new MockMultipartFile("file", "lostItems.txt", "text/plain", lostItems.getBytes());

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
    String lostItems = "ItemName: Laptop\nQuantity: 1\nPlace: Taxi\nItemName: Headphones\nQuantity: 2\nPlace: Railway station\nItemName: Jewels\nQuantity: 4\nPlace: Airport\nItemName: Laptop\nQuantity: 1\nPlace: Airport";
    MockMultipartFile mockFile = new MockMultipartFile("file", "lostItems.txt", "text/plain", lostItems.getBytes());

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
