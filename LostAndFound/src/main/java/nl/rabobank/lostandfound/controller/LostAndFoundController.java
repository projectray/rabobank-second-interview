package nl.rabobank.lostandfound.controller;

import jakarta.validation.Valid;
import nl.rabobank.lostandfound.execption.BadRequestException;
import nl.rabobank.lostandfound.model.Claim;
import nl.rabobank.lostandfound.model.LostItem;
import nl.rabobank.lostandfound.service.LostAndFoundService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LostAndFoundController {

  static Logger logger = LoggerFactory.getLogger(LostAndFoundController.class);
  private final LostAndFoundService lostAndFoundService;

  public LostAndFoundController(LostAndFoundService lostAndFoundService) {
    this.lostAndFoundService = lostAndFoundService;
  }

  @PostMapping("/admin/upload-items")
  public ResponseEntity<List<LostItem>> upload(@RequestParam("file") MultipartFile file) {
    try {
      return ResponseEntity.ok(lostAndFoundService.uploadLostItems(file));
    } catch (IOException e) {
      logger.error(String.format("Cause {}, message {}", e.getCause(), e.getMessage()));
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }

  @GetMapping("/lost-items")
  public ResponseEntity<List<LostItem>> getAllLostItems() {
    return ResponseEntity.ok(lostAndFoundService.getAllLostItems());
  }

  @PostMapping("/claim-item")
  public ResponseEntity<String> claimItem(@Valid @RequestBody Claim claim) {
    try {
      return ResponseEntity.ok(lostAndFoundService.claimItem(claim.getUserId(), claim.getItemId(), claim.getClaimedQuantity()));
    }catch (BadRequestException e){
      logger.error(String.format("Cause {}, message {}", e.getCause(), e.getMessage()));
      return ResponseEntity.badRequest().body(e.getMessage());
    }
  }

  @GetMapping("/admin/check-claims")
  public ResponseEntity<List<Claim>> getAllClaims(){
    return ResponseEntity.ok(lostAndFoundService.getAllClaims());
  }

  @GetMapping("/admin/check-user-claims")
  public ResponseEntity<List<Claim>> getAllClaimsByUserID(@RequestParam long userId){
    return ResponseEntity.ok(lostAndFoundService.getClaimsByUserId(userId));
  }
}
