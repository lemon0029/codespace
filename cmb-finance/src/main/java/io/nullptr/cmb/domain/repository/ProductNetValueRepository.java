package io.nullptr.cmb.domain.repository;

import io.nullptr.cmb.domain.ProductNetValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Repository
public interface ProductNetValueRepository extends JpaRepository<ProductNetValue, Long> {

    List<ProductNetValue> findAllByInnerCode(String innerCode);

    List<ProductNetValue> findAllByInnerCodeInAndDateBetween(Collection<String> innerCodes,
                                                             LocalDate dateAfter,
                                                             LocalDate dateBefore);

    void deleteAllByInnerCode(String innerCode);
}
