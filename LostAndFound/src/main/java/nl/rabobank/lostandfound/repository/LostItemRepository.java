package nl.rabobank.lostandfound.repository;

import nl.rabobank.lostandfound.model.LostItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LostItemRepository extends JpaRepository<LostItem, Long> {
}
