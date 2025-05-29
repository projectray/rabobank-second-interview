package nl.rabobank.lostandfound.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class LostItem {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String itemName;
  private Integer quantity;
  private String place;

  @OneToMany(mappedBy = "lostItem", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Claim> claims;
}
