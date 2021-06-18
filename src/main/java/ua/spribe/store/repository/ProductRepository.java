package ua.spribe.store.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.spribe.store.model.Product;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByOrderedIsFalse();

    int countByNameAndOrderedIsFalse(String productName);

    List<Product> findAllByCustomerId(Long customerId);
}

