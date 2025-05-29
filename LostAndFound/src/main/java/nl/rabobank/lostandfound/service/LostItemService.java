package nl.rabobank.lostandfound.service;

import nl.rabobank.lostandfound.model.LostItem;
import nl.rabobank.lostandfound.repository.LostItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LostItemService {
  @Autowired
  private LostItemRepository lostItemRepository;

  public LostItem saveLostItem(LostItem lostItem) {
    return lostItemRepository.save(lostItem);
  }

  public List<LostItem> getAllLostItems() {
    return lostItemRepository.findAll();
  }
}
