package nl.rabobank.lostandfound.service;

import nl.rabobank.lostandfound.model.LostItem;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class UploadClaimFileService {
  public List<LostItem> parseFile(MultipartFile file) throws IOException {
    List<LostItem> lostItems = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
      String line;
      LostItem currentItem = null;
      while ((line = reader.readLine()) != null) {
        String trim = line.substring(line.indexOf(":") + 1).trim();
        if (line.trim().startsWith("ItemName:")) {
          if (currentItem != null) {
            lostItems.add(currentItem);
          }
          currentItem = new LostItem();
          currentItem.setItemName(trim);
        } else if (line.trim().startsWith("Quantity:") && currentItem != null ) {
          currentItem.setQuantity(Integer.parseInt(trim));
        } else if (line.trim().startsWith("Place:") && currentItem != null) {
          currentItem.setPlace(trim);
        }
      }
      if (currentItem != null) {
        lostItems.add(currentItem);
      }
    }
    return lostItems;
  }
}
