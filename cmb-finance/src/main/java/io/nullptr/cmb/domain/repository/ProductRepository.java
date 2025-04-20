package io.nullptr.cmb.domain.repository;

import io.nullptr.cmb.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByInnerCode(String innerCode);

    List<Product> findAllByProductTag(Integer productTag);
}
