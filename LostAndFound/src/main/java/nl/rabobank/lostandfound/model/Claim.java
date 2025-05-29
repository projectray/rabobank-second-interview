package nl.rabobank.lostandfound.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Claim {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotNull(message = "UserId cannot be null")
  private Long userId;
  @NotNull(message = "ItemId cannot be null")
  private Long itemId;
  @NotNull(message = "Quantity of the claimed item cannot be null")
  private Integer claimedQuantity;

  @ManyToOne
  @JoinColumn(name = "lost_item_id")
  private LostItem lostItem;

  private String userName;
}
