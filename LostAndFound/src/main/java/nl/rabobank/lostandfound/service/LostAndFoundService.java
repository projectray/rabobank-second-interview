package nl.rabobank.lostandfound.service;

import jakarta.transaction.Transactional;
import nl.rabobank.lostandfound.execption.BadRequestException;
import nl.rabobank.lostandfound.model.Claim;
import nl.rabobank.lostandfound.model.LostItem;
import nl.rabobank.lostandfound.repository.ClaimRepository;
import nl.rabobank.lostandfound.repository.LostItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class LostAndFoundService {

  private final LostItemRepository lostItemRepository;
  private final ClaimRepository claimRepository;
  private final UploadClaimFileService uploadClaimFileService;
  private final UserMockService userMockService;

  public LostAndFoundService(LostItemRepository lostItemRepository, ClaimRepository claimRepository,
                             UploadClaimFileService uploadClaimFileService, UserMockService userMockService) {
    this.lostItemRepository = lostItemRepository;
    this.claimRepository = claimRepository;
    this.uploadClaimFileService = uploadClaimFileService;
    this.userMockService = userMockService;
  }

  @Transactional
  public List<LostItem> uploadLostItems(MultipartFile file) throws IOException {
    List<LostItem> lostItems = uploadClaimFileService.parseFile(file);
    return lostItemRepository.saveAll(lostItems);
  }

  public List<LostItem> getAllLostItems() {
    return lostItemRepository.findAll();
  }

  @Transactional
  public String claimItem(Long userId, Long itemId, int quantity) throws BadRequestException {
    LostItem item = lostItemRepository.findById(itemId).orElse(null);
    if(item != null) {
      if (item.getQuantity() >= quantity) {
        Claim claim = new Claim();
        claim.setUserId(userId);
        claim.setClaimedQuantity(quantity);
        claim.setItemId(itemId);
        item.setQuantity(item.getQuantity() - quantity);// update item quantity after claim
        claim.setUserName(userMockService.getUserName(userId));
        claimRepository.save(claim);
        return "Claim report successfully.";
      }
      throw new BadRequestException("The claim quantity is greater than the quantity of the item in stock");
    }
    throw new BadRequestException("The claim item is not existing");
  }

  public List<Claim> getAllClaims() {
    return claimRepository.findAll();
  }

  public List<Claim> getClaimsByUserId(long userId) {
    return claimRepository.findClaimsByUserId(userId);
  }
}
