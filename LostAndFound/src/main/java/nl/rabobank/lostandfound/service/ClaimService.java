package nl.rabobank.lostandfound.service;

import nl.rabobank.lostandfound.repository.ClaimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClaimService {
  @Autowired
  private ClaimRepository claimRepository;
}
