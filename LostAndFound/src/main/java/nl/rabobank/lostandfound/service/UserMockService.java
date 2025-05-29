package nl.rabobank.lostandfound.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserMockService {
  private Map<Long, String> users = new HashMap<>();

  public UserMockService() {
    users.put(1L, "Ray");
    users.put(2L, "Amy");
  }

  public String getUserName(Long userId) {
    return users.getOrDefault(userId, "Unknown User");
  }
}
