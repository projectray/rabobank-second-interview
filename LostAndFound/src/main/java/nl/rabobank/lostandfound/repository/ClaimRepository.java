package nl.rabobank.lostandfound.repository;

import nl.rabobank.lostandfound.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
  @Query("SELECT c FROM Claim c WHERE c.userId = :userId")
  List<Claim> findClaimsByUserId(@Param("userId") long userId);
}
