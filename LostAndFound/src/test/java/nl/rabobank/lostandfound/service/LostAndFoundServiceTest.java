package nl.rabobank.lostandfound.service;

import nl.rabobank.lostandfound.execption.BadRequestException;
import nl.rabobank.lostandfound.model.Claim;
import nl.rabobank.lostandfound.model.LostItem;
import nl.rabobank.lostandfound.repository.ClaimRepository;
import nl.rabobank.lostandfound.repository.LostItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LostAndFoundServiceTest {
  @Mock
  private LostItemRepository lostItemRepository;
  @Mock
  private ClaimRepository claimRepository;
  @Mock
  private UploadClaimFileService uploadClaimFileService;
  @Mock
  private UserMockService userService;

  @InjectMocks
  private LostAndFoundService lostAndFoundService;

  @Test
  void uploadLostItemFileTest() throws IOException {
    List<LostItem> lostItems = new ArrayList<>();
    LostItem item = new LostItem();
    item.setItemName("Laptop");
    item.setQuantity(1);
    item.setPlace("Taxi");
    lostItems.add(item);

    when(uploadClaimFileService.parseFile(any())).thenReturn(lostItems);
    when(lostItemRepository.saveAll(lostItems)).thenReturn(lostItems);

    List<LostItem> result = lostAndFoundService.uploadLostItems(mock(MultipartFile.class));

    assertEquals(lostItems, result);
    verify(lostItemRepository).saveAll(lostItems);
  }

  @Test
  void successClaimTest() throws BadRequestException {
    LostItem item = new LostItem();
    item.setId(1L);
    item.setItemName("Laptop");
    item.setQuantity(4);
    when(lostItemRepository.findById(any())).thenReturn(Optional.of(item));

    Claim claim = new Claim();
    claim.setUserId(1L);
    claim.setItemId(1L);
    claim.setClaimedQuantity(1);
    when(userService.getUserName(1L)).thenReturn("Ray");
    when(claimRepository.save(any())).thenReturn(claim);

    String result = lostAndFoundService.claimItem(1L, 1L, 1);
    assertEquals("Claim report successfully.", result);
    assertEquals(3, item.getQuantity());
    verify(claimRepository).save(any(Claim.class));
  }

  @Test
  void failedClaimDueToNotItemNotExistTest() {
    when(lostItemRepository.findById(1L)).thenReturn(Optional.empty());

    Exception ex = assertThrows(BadRequestException.class, () -> lostAndFoundService.claimItem(1L, 1L, 3));
    assertEquals("The claim item is not existing", ex.getMessage());
  }

  @Test
  void failedClaimDueToNotEnoughQuantityTest() {
    LostItem item = new LostItem();
    item.setId(1L);
    item.setItemName("Laptop");
    item.setQuantity(2);
    when(lostItemRepository.findById(1L)).thenReturn(Optional.of(item));

    Exception ex = assertThrows(BadRequestException.class, () -> lostAndFoundService.claimItem(1L, 1L, 3));
    assertEquals("The claim quantity is greater than the quantity of the item in stock", ex.getMessage());
  }
}
