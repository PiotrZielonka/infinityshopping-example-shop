package infinityshopping.online.app.repository;

import infinityshopping.online.app.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@SuppressWarnings("unused")
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

  @Query(value = "SELECT * FROM PRODUCT WHERE PRODUCT_CATEGORY_ENUM = 'Probiotics'",
      nativeQuery = true)
  Page<Product> getAllProductsByCategoryProbiotics(Pageable pageable);

  @Query(value = "SELECT * FROM PRODUCT WHERE PRODUCT_CATEGORY_ENUM = 'Vitamins'",
      nativeQuery = true)
  Page<Product> getAllProductsByCategoryVitamins(Pageable pageable);

  @Query(value = "SELECT * FROM PRODUCT WHERE PRODUCT_CATEGORY_ENUM = 'Minerals'",
      nativeQuery = true)
  Page<Product> getAllProductsByCategoryMinerals(Pageable pageable);

  @Query(value = "SELECT * FROM PRODUCT WHERE PRODUCT_CATEGORY_ENUM = 'Aloes'",
      nativeQuery = true)
  Page<Product> getAllProductsByCategoryAloes(Pageable pageable);

  @Query(value = "SELECT * FROM PRODUCT WHERE PRODUCT_CATEGORY_ENUM = 'Collagen'",
      nativeQuery = true)
  Page<Product> getAllProductsByCategoryCollagen(Pageable pageable);
}
