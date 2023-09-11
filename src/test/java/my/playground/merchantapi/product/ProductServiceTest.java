package my.playground.merchantapi.product;

import static my.playground.merchantapi.product.ProductMockFactory.newProductEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import my.playground.merchantapi.entity.ProductEntity;
import my.playground.merchantapi.repository.ProductRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class ProductServiceTest {

  private static ProductRepository productRepository;

  private static ProductService productService;

  @BeforeAll
  public static void setup() {
    productRepository = mock(ProductRepository.class);
    productService = new ProductService(productRepository);
  }

  @Test
  public void shouldReturnListOfProducts() {
    List<ProductEntity> productEntities = List.of(
        newProductEntity("product1", "description1"),
        newProductEntity("product2", "description2")
    );
    when(productRepository.findAll()).thenReturn(productEntities);

    List<Product> products = productService.getAllProducts();
    assertEquals(2, products.size());
  }
}
