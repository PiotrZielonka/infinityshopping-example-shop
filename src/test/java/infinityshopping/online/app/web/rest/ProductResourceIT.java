package infinityshopping.online.app.web.rest;

import static infinityshopping.online.app.web.rest.TestUtil.sameNumber;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import infinityshopping.online.app.IntegrationTest;
import infinityshopping.online.app.domain.Product;
import infinityshopping.online.app.domain.enumeration.ProductCategoryEnum;
import infinityshopping.online.app.repository.ProductRepository;
import infinityshopping.online.app.security.AuthoritiesConstants;
import infinityshopping.online.app.service.AddVat;
import infinityshopping.online.app.service.dto.ProductDTO;
import infinityshopping.online.app.service.mapper.ProductMapper;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Base64Utils;

@IntegrationTest
@AutoConfigureMockMvc
class ProductResourceIT implements AddVat {

  private static Random random = new Random();
  private static AtomicLong count = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

  private static final ProductCategoryEnum DEFAULT_PRODUCT_CATEGORY_ENUM
      = ProductCategoryEnum.Vitamins;
  private static final ProductCategoryEnum UPDATED_PRODUCT_CATEGORY_ENUM
      = ProductCategoryEnum.Minerals;
  private static final ProductCategoryEnum PRODUCT_CATEGORY_ENUM_Vitamins
      = ProductCategoryEnum.Vitamins;
  private static final ProductCategoryEnum PRODUCT_CATEGORY_ENUM_Minerals
      = ProductCategoryEnum.Minerals;
  private static final ProductCategoryEnum PRODUCT_CATEGORY_ENUM_Aloes
      = ProductCategoryEnum.Aloes;
  private static final ProductCategoryEnum PRODUCT_CATEGORY_ENUM_Collagen
      = ProductCategoryEnum.Collagen;
  private static final ProductCategoryEnum PRODUCT_CATEGORY_ENUM_Probiotics
      = ProductCategoryEnum.Probiotics;

  private static final String DEFAULT_NAME = "AAAAAAAAAA";
  private static final String UPDATED_NAME = "BBBBBBBBBB";

  private static BigDecimal DEFAULT_QUANTITY = BigDecimal.ONE;
  private static BigDecimal UPDATED_QUANTITY = new BigDecimal(random.nextInt(100 - 1) + 1);

  private static BigDecimal DEFAULT_PRICE_NET = new BigDecimal("200.53");
  private static BigDecimal UPDATED_PRICE_NET = new BigDecimal("310.78");

  private static BigDecimal DEFAULT_VAT
      = new BigDecimal(random.nextInt(30 - 5) + 5);
  private static BigDecimal UPDATED_VAT
      = new BigDecimal(random.nextInt(30 - 5) + 5);

  private BigDecimal defaultProperPriceGross = addVat(DEFAULT_PRICE_NET, DEFAULT_VAT);
  private BigDecimal updatedProperPriceGross = addVat(UPDATED_PRICE_NET, UPDATED_VAT);
  private static BigDecimal DEFAULT_FAKE_PRICE_GROSS = BigDecimal.ZERO;

  private static BigDecimal DEFAULT_STOCK = BigDecimal.ZERO;
  private static BigDecimal UPDATED_STOCK = BigDecimal.ONE;

  private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
  private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

  private static final Instant DEFAULT_CREATE_TIME = Instant.ofEpochMilli(0L);
  private static final Instant UPDATED_CREATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

  private static final Instant DEFAULT_UPDATE_TIME = Instant.ofEpochMilli(0L);
  private static final Instant UPDATED_UPDATE_TIME = Instant.now().truncatedTo(ChronoUnit.MILLIS);

  private static final byte[] DEFAULT_IMAGE = TestUtil.createByteArray(1, "0");
  private static final byte[] UPDATED_IMAGE = TestUtil.createByteArray(1, "1");
  private static final String DEFAULT_IMAGE_CONTENT_TYPE = "image/jpg";
  private static final String UPDATED_IMAGE_CONTENT_TYPE = "image/png";

  private static final String ENTITY_API_URL = "/api/products";
  private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";
  private static final String ENTITY_API_URL_ID_BY_ID = ENTITY_API_URL + "/byid/{id}";
  private static final String ENTITY_API_URL_ALL = ENTITY_API_URL + "/all";
  private static final String ENTITY_API_URL_ALL_IMAGE_NAME_PRICE_GROSS
      = ENTITY_API_URL + "/all/imageNamePriceGross";



  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductMapper productMapper;

  @Autowired
  private EntityManager em;

  @Autowired
  private MockMvc restProductMockMvc;

  private Product product;

  public static Product createEntity(EntityManager em) {
    Product product = new Product()
        .productCategoryEnum(DEFAULT_PRODUCT_CATEGORY_ENUM)
        .name(DEFAULT_NAME)
        .quantity(DEFAULT_QUANTITY)
        .priceNet(DEFAULT_PRICE_NET)
        .vat(DEFAULT_VAT)
        .priceGross(DEFAULT_FAKE_PRICE_GROSS)
        .stock(DEFAULT_STOCK)
        .description(DEFAULT_DESCRIPTION)
        .createTime(DEFAULT_CREATE_TIME)
        .updateTime(DEFAULT_UPDATE_TIME)
        .image(DEFAULT_IMAGE)
        .imageContentType(DEFAULT_IMAGE_CONTENT_TYPE);
    return product;
  }

  @BeforeEach
  public void initTest() {
    product = createEntity(em);
  }

  @Test
  @Transactional
  @WithMockUser
  void createProductByUserShouldThrowStatusForbidden() throws Exception {
    int databaseSizeBeforeCreate = productRepository.findAll().size();

    // Create the Product
    ProductDTO productDto = productMapper.toDto(product);

    restProductMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isForbidden());

    // Validate the Product in the database
    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  void createProductByAnyoneShouldThrowStatusUnauthorized() throws Exception {
    int databaseSizeBeforeCreate = productRepository.findAll().size();

    // Create the Product
    ProductDTO productDto = productMapper.toDto(product);

    restProductMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isUnauthorized());

    // Validate the Product in the database
    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void createProductAndSetProperPriceGrossAutomatic() throws Exception {
    final int databaseSizeBeforeCreate = productRepository.findAll().size();

    // Create the Product
    ProductDTO productDto = productMapper.toDto(product);
    productDto.setPriceGross(null);
    productDto.setCreateTime(null);
    productDto.setUpdateTime(null);

    restProductMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isCreated());

    // Validate the Product in the database
    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeCreate + 1);
    Product testProduct = productList.get(productList.size() - 1);
    assertThat(testProduct.getProductCategoryEnum()).isEqualTo(DEFAULT_PRODUCT_CATEGORY_ENUM);
    assertThat(testProduct.getName()).isEqualTo(DEFAULT_NAME);
    assertThat(testProduct.getQuantity()).isEqualTo(BigDecimal.ONE);
    assertThat(testProduct.getPriceNet()).isEqualTo(DEFAULT_PRICE_NET);
    assertThat(testProduct.getVat()).isEqualTo(DEFAULT_VAT);
    assertThat(testProduct.getPriceGross()).isEqualTo(defaultProperPriceGross);
    assertThat(testProduct.getStock()).isEqualTo(DEFAULT_STOCK);
    assertThat(testProduct.getDescription()).isEqualTo(DEFAULT_DESCRIPTION);
    assertThat(testProduct.getImage()).isEqualTo(DEFAULT_IMAGE);
    assertThat(testProduct.getImageContentType()).isEqualTo(DEFAULT_IMAGE_CONTENT_TYPE);
    assertNotNull(testProduct.getCreateTime());
    assertNotNull(testProduct.getUpdateTime());
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void createProductWithExistingId() throws Exception {
    // Create the Product with an existing ID
    product.setId(1L);
    ProductDTO productDto = productMapper.toDto(product);

    int databaseSizeBeforeCreate = productRepository.findAll().size();

    // An entity with an existing ID cannot be created, so this API call must fail
    restProductMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isBadRequest());

    // Validate the Product in the database
    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeCreate);
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void checkCategoryIsRequired() throws Exception {
    int databaseSizeBeforeTest = productRepository.findAll().size();
    // set the field null
    product.setProductCategoryEnum(null);

    // Create the Product, which fails.
    ProductDTO productDto = productMapper.toDto(product);

    restProductMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isBadRequest());

    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void checkNameIsRequired() throws Exception {
    int databaseSizeBeforeTest = productRepository.findAll().size();
    // set the field null
    product.setName(null);

    // Create the Product, which fails.
    ProductDTO productDto = productMapper.toDto(product);

    restProductMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isBadRequest());

    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void checkPriceNetIsRequired() throws Exception {
    int databaseSizeBeforeTest = productRepository.findAll().size();
    // set the field null
    product.setPriceNet(null);

    // Create the Product, which fails.
    ProductDTO productDto = productMapper.toDto(product);

    restProductMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isBadRequest());

    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void checkVatIsRequired() throws Exception {
    int databaseSizeBeforeTest = productRepository.findAll().size();
    // set the field null
    product.setVat(null);

    // Create the Product, which fails.
    ProductDTO productDto = productMapper.toDto(product);

    restProductMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isBadRequest());

    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void checkStockIsRequired() throws Exception {
    int databaseSizeBeforeTest = productRepository.findAll().size();
    // set the field null
    product.setStock(null);

    // Create the Product, which fails.
    ProductDTO productDto = productMapper.toDto(product);

    restProductMockMvc.perform(post(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isBadRequest());

    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeTest);
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void getAllProducts() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    productRepository.saveAndFlush(product);

    // Get all the productList
    restProductMockMvc.perform(get(ENTITY_API_URL_ALL + "?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
        .andExpect(jsonPath("$.[*].productCategoryEnum").value(
            hasItem(DEFAULT_PRODUCT_CATEGORY_ENUM.toString())))
        .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
        .andExpect(jsonPath("$.[*].quantity").value(hasItem(sameNumber(DEFAULT_QUANTITY))))
        .andExpect(jsonPath("$.[*].priceNet").value(hasItem(sameNumber(DEFAULT_PRICE_NET))))
        .andExpect(jsonPath("$.[*].vat").value(hasItem(sameNumber(DEFAULT_VAT))))
        .andExpect(
            jsonPath("$.[*].priceGross").value(hasItem(sameNumber(defaultProperPriceGross))))
        .andExpect(jsonPath("$.[*].stock").value(hasItem(sameNumber(DEFAULT_STOCK))))
        .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION.toString())))
        .andExpect(jsonPath("$.[*].createTime").exists())
        .andExpect(jsonPath("$.[*].updateTime").exists())
        .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
        .andExpect(
            jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
  }

  @Test
  @Transactional
  @WithMockUser(username = "user", password = "user", authorities = AuthoritiesConstants.USER)
  void getAllProductsForProductManagementByUserShouldThrowStatusForbidden() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    productRepository.saveAndFlush(product);

    // Get all the productList
    restProductMockMvc.perform(get(ENTITY_API_URL_ALL + "?sort=id,desc"))
        .andExpect(status().isForbidden());
  }

  @Test
  @Transactional
  void getAllProductsForProductManagementByAnyoneShouldThrowStatusUnauthorized() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    productRepository.saveAndFlush(product);

    // Get all the productList
    restProductMockMvc.perform(get(ENTITY_API_URL_ALL + "?sort=id,desc"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Transactional
  void getAllProductsOnlyWithImageNamePriceGross() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    productRepository.saveAndFlush(product);

    // Get all the productList
    restProductMockMvc
        .perform(get(ENTITY_API_URL_ALL_IMAGE_NAME_PRICE_GROSS + "?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
        .andExpect(jsonPath("$.[*].productCategoryEnum").doesNotExist())
        .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
        .andExpect(jsonPath("$.[*].quantity").doesNotExist())
        .andExpect(jsonPath("$.[*].priceNet").doesNotExist())
        .andExpect(jsonPath("$.[*].vat").doesNotExist())
        .andExpect(
            jsonPath("$.[*].priceGross").value(hasItem(sameNumber(defaultProperPriceGross))))
        .andExpect(jsonPath("$.[*].stock").doesNotExist())
        .andExpect(jsonPath("$.[*].description").doesNotExist())
        .andExpect(jsonPath("$.[*].createTime").doesNotExist())
        .andExpect(jsonPath("$.[*].updateTime").doesNotExist())
        .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
        .andExpect(
            jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
  }

  @Test
  @Transactional
  public void getAllProductsByCategoryProbiotics() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    product.setProductCategoryEnum(PRODUCT_CATEGORY_ENUM_Probiotics);
    productRepository.saveAndFlush(product);

    // Get all the productListByCategoryProbiotics
    restProductMockMvc.perform(get("/api/category-probiotics?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(
            jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
        .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
        .andExpect(
            jsonPath("$.[*].priceGross").value(hasItem(sameNumber(defaultProperPriceGross))))
        .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
        .andExpect(
            jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
  }

  @Test
  @Transactional
  public void getAllProductsByCategoryProbioticsShouldNotGetAnotherEntity() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    product.setProductCategoryEnum(PRODUCT_CATEGORY_ENUM_Vitamins);
    productRepository.saveAndFlush(product);

    // Get all the productListByCategoryProbiotics
    restProductMockMvc.perform(get("/api/category-probiotics?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").doesNotExist())
        .andExpect(jsonPath("$.[*].productCategoryEnum").doesNotExist())
        .andExpect(jsonPath("$.[*].name").doesNotExist())
        .andExpect(jsonPath("$.[*].quantity").doesNotExist())
        .andExpect(jsonPath("$.[*].priceNet").doesNotExist())
        .andExpect(jsonPath("$.[*].vat").doesNotExist())
        .andExpect(jsonPath("$.[*].priceGross").doesNotExist())
        .andExpect(jsonPath("$.[*].stock").doesNotExist())
        .andExpect(jsonPath("$.[*].description").doesNotExist())
        .andExpect(jsonPath("$.[*].createTime").doesNotExist())
        .andExpect(jsonPath("$.[*].updateTime").doesNotExist())
        .andExpect(jsonPath("$.[*].imageContentType").doesNotExist())
        .andExpect(jsonPath("$.[*].image").doesNotExist());
  }

  @Test
  @Transactional
  public void getAllProductsByCategoryVitamins() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    product.setProductCategoryEnum(PRODUCT_CATEGORY_ENUM_Vitamins);
    productRepository.saveAndFlush(product);

    // Get all the productListByCategoryVitamins
    restProductMockMvc.perform(get("/api/category-vitamins?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(
            jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
        .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
        .andExpect(
            jsonPath("$.[*].priceGross").value(hasItem(sameNumber(defaultProperPriceGross))))
        .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
        .andExpect(
            jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
  }

  @Test
  @Transactional
  public void getAllProductsByCategoryVitaminsShouldNotGetAnotherEntity() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    product.setProductCategoryEnum(PRODUCT_CATEGORY_ENUM_Probiotics);
    productRepository.saveAndFlush(product);

    // Get all the productListByCategoryProbiotics
    restProductMockMvc.perform(get("/api/category-vitamins?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").doesNotExist())
        .andExpect(jsonPath("$.[*].productCategoryEnum").doesNotExist())
        .andExpect(jsonPath("$.[*].name").doesNotExist())
        .andExpect(jsonPath("$.[*].quantity").doesNotExist())
        .andExpect(jsonPath("$.[*].priceNet").doesNotExist())
        .andExpect(jsonPath("$.[*].vat").doesNotExist())
        .andExpect(jsonPath("$.[*].priceGross").doesNotExist())
        .andExpect(jsonPath("$.[*].stock").doesNotExist())
        .andExpect(jsonPath("$.[*].description").doesNotExist())
        .andExpect(jsonPath("$.[*].createTime").doesNotExist())
        .andExpect(jsonPath("$.[*].updateTime").doesNotExist())
        .andExpect(jsonPath("$.[*].imageContentType").doesNotExist())
        .andExpect(jsonPath("$.[*].image").doesNotExist());
  }

  @Test
  @Transactional
  public void getAllProductsByCategoryMinerals() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    product.setProductCategoryEnum(PRODUCT_CATEGORY_ENUM_Minerals);
    productRepository.saveAndFlush(product);

    // Get all the productListByCategoryMinerals
    restProductMockMvc.perform(get("/api/category-minerals?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(
            jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
        .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
        .andExpect(
            jsonPath("$.[*].priceGross").value(hasItem(sameNumber(defaultProperPriceGross))))
        .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
        .andExpect(
            jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
  }

  @Test
  @Transactional
  public void getAllProductsByCategoryMineralsShouldNotGetAnotherEntity() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    product.setProductCategoryEnum(PRODUCT_CATEGORY_ENUM_Probiotics);
    productRepository.saveAndFlush(product);

    // Get all the productListByCategoryMinerals
    restProductMockMvc.perform(get("/api/category-minerals?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").doesNotExist())
        .andExpect(jsonPath("$.[*].productCategoryEnum").doesNotExist())
        .andExpect(jsonPath("$.[*].name").doesNotExist())
        .andExpect(jsonPath("$.[*].quantity").doesNotExist())
        .andExpect(jsonPath("$.[*].priceNet").doesNotExist())
        .andExpect(jsonPath("$.[*].vat").doesNotExist())
        .andExpect(jsonPath("$.[*].priceGross").doesNotExist())
        .andExpect(jsonPath("$.[*].stock").doesNotExist())
        .andExpect(jsonPath("$.[*].description").doesNotExist())
        .andExpect(jsonPath("$.[*].createTime").doesNotExist())
        .andExpect(jsonPath("$.[*].updateTime").doesNotExist())
        .andExpect(jsonPath("$.[*].imageContentType").doesNotExist())
        .andExpect(jsonPath("$.[*].image").doesNotExist());
  }

  @Test
  @Transactional
  public void getAllProductsByCategoryAloes() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    product.setProductCategoryEnum(PRODUCT_CATEGORY_ENUM_Aloes);
    productRepository.saveAndFlush(product);

    // Get all the productListByCategoryAloes
    restProductMockMvc.perform(get("/api/category-aloes?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
        .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
        .andExpect(
            jsonPath("$.[*].priceGross").value(hasItem(sameNumber(defaultProperPriceGross))))
        .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
        .andExpect(
            jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
  }

  @Test
  @Transactional
  public void getAllProductsByCategoryAloesShouldNotGetAnotherEntity() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    product.setProductCategoryEnum(PRODUCT_CATEGORY_ENUM_Probiotics);
    productRepository.saveAndFlush(product);

    // Get all the productListByCategoryAloes
    restProductMockMvc.perform(get("/api/category-aloes?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").doesNotExist())
        .andExpect(jsonPath("$.[*].productCategoryEnum").doesNotExist())
        .andExpect(jsonPath("$.[*].name").doesNotExist())
        .andExpect(jsonPath("$.[*].quantity").doesNotExist())
        .andExpect(jsonPath("$.[*].priceNet").doesNotExist())
        .andExpect(jsonPath("$.[*].vat").doesNotExist())
        .andExpect(jsonPath("$.[*].priceGross").doesNotExist())
        .andExpect(jsonPath("$.[*].stock").doesNotExist())
        .andExpect(jsonPath("$.[*].description").doesNotExist())
        .andExpect(jsonPath("$.[*].createTime").doesNotExist())
        .andExpect(jsonPath("$.[*].updateTime").doesNotExist())
        .andExpect(jsonPath("$.[*].imageContentType").doesNotExist())
        .andExpect(jsonPath("$.[*].image").doesNotExist());
  }

  @Test
  @Transactional
  public void getAllProductsByCategoryCollagen() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    product.setProductCategoryEnum(PRODUCT_CATEGORY_ENUM_Collagen);
    productRepository.saveAndFlush(product);

    // Get all the productListByCategoryCollagen
    restProductMockMvc.perform(get("/api/category-collagen?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(
            jsonPath("$.[*].id").value(hasItem(product.getId().intValue())))
        .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
        .andExpect(
            jsonPath("$.[*].priceGross").value(hasItem(sameNumber(defaultProperPriceGross))))
        .andExpect(jsonPath("$.[*].imageContentType").value(hasItem(DEFAULT_IMAGE_CONTENT_TYPE)))
        .andExpect(
            jsonPath("$.[*].image").value(hasItem(Base64Utils.encodeToString(DEFAULT_IMAGE))));
  }

  @Test
  @Transactional
  public void getAllProductsByCategoryCollagenShouldNotGetAnotherEntity() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    product.setProductCategoryEnum(PRODUCT_CATEGORY_ENUM_Probiotics);
    productRepository.saveAndFlush(product);

    // Get all the productListByCategoryCollagen
    restProductMockMvc.perform(get("/api/category-collagen?sort=id,desc"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.[*].id").doesNotExist())
        .andExpect(jsonPath("$.[*].productCategoryEnum").doesNotExist())
        .andExpect(jsonPath("$.[*].name").doesNotExist())
        .andExpect(jsonPath("$.[*].quantity").doesNotExist())
        .andExpect(jsonPath("$.[*].priceNet").doesNotExist())
        .andExpect(jsonPath("$.[*].vat").doesNotExist())
        .andExpect(jsonPath("$.[*].priceGross").doesNotExist())
        .andExpect(jsonPath("$.[*].stock").doesNotExist())
        .andExpect(jsonPath("$.[*].description").doesNotExist())
        .andExpect(jsonPath("$.[*].createTime").doesNotExist())
        .andExpect(jsonPath("$.[*].updateTime").doesNotExist())
        .andExpect(jsonPath("$.[*].imageContentType").doesNotExist())
        .andExpect(jsonPath("$.[*].image").doesNotExist());
  }

  @Test
  @Transactional
  void getProduct() throws Exception {
    // Initialize the database
    product.setPriceGross(defaultProperPriceGross);
    productRepository.saveAndFlush(product);

    // Get the product
    restProductMockMvc
        .perform(get(ENTITY_API_URL_ID_BY_ID, product.getId()))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id").value(product.getId().intValue()))
        .andExpect(
            jsonPath("$.productCategoryEnum").value(DEFAULT_PRODUCT_CATEGORY_ENUM.toString()))
        .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
        .andExpect(jsonPath("$.quantity").value(sameNumber(DEFAULT_QUANTITY)))
        .andExpect(jsonPath("$.priceNet").value(sameNumber(DEFAULT_PRICE_NET)))
        .andExpect(jsonPath("$.vat").value(sameNumber(DEFAULT_VAT)))
        .andExpect(jsonPath("$.priceGross").value(sameNumber(defaultProperPriceGross)))
        .andExpect(jsonPath("$.stock").value(sameNumber(DEFAULT_STOCK)))
        .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION.toString()))
        .andExpect(jsonPath("$.createTime").exists())
        .andExpect(jsonPath("$.updateTime").exists())
        .andExpect(jsonPath("$.imageContentType").value(DEFAULT_IMAGE_CONTENT_TYPE))
        .andExpect(jsonPath("$.image").value(Base64Utils.encodeToString(DEFAULT_IMAGE)));
  }

  @Test
  @Transactional
  void getNonExistingProduct() throws Exception {
    // Get the product
    restProductMockMvc.perform(get(ENTITY_API_URL_ID_BY_ID, Long.MAX_VALUE))
        .andExpect(status().isNotFound());
  }

  @Test
  @Transactional
  @WithMockUser
  void putNewProductByUserShouldThrowStatusForbidden() throws Exception {
    // Initialize the database
    productRepository.saveAndFlush(product);

    final int databaseSizeBeforeUpdate = productRepository.findAll().size();

    // Update the product
    ProductDTO productDto = productMapper.toDto(
        productRepository.findById(product.getId()).get());
    productDto.setProductCategoryEnum(UPDATED_PRODUCT_CATEGORY_ENUM);
    productDto.setName(UPDATED_NAME);
    productDto.setQuantity(UPDATED_QUANTITY);
    productDto.setPriceNet(UPDATED_PRICE_NET);
    productDto.setVat(UPDATED_VAT);
    productDto.setPriceGross(null);
    productDto.setStock(UPDATED_STOCK);
    productDto.setDescription(UPDATED_DESCRIPTION);
    productDto.setCreateTime(UPDATED_CREATE_TIME);
    productDto.setUpdateTime(UPDATED_UPDATE_TIME);
    productDto.setImage(UPDATED_IMAGE);
    productDto.setImageContentType(UPDATED_IMAGE_CONTENT_TYPE);

    // Update the Product
    restProductMockMvc.perform(put(ENTITY_API_URL_ID, productDto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @Transactional
  void putNewProductByAnyoneShouldThrowStatusUnauthorized() throws Exception {
    // Initialize the database
    productRepository.saveAndFlush(product);

    final int databaseSizeBeforeUpdate = productRepository.findAll().size();

    // Update the product
    ProductDTO productDto = productMapper.toDto(
        productRepository.findById(product.getId()).get());
    productDto.setProductCategoryEnum(UPDATED_PRODUCT_CATEGORY_ENUM);
    productDto.setName(UPDATED_NAME);
    productDto.setQuantity(UPDATED_QUANTITY);
    productDto.setPriceNet(UPDATED_PRICE_NET);
    productDto.setVat(UPDATED_VAT);
    productDto.setPriceGross(null);
    productDto.setStock(UPDATED_STOCK);
    productDto.setDescription(UPDATED_DESCRIPTION);
    productDto.setCreateTime(UPDATED_CREATE_TIME);
    productDto.setUpdateTime(UPDATED_UPDATE_TIME);
    productDto.setImage(UPDATED_IMAGE);
    productDto.setImageContentType(UPDATED_IMAGE_CONTENT_TYPE);

    // Update the Product
    restProductMockMvc.perform(put(ENTITY_API_URL_ID, productDto.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void putNewProduct() throws Exception {
    // Initialize the database
    productRepository.saveAndFlush(product);

    final int databaseSizeBeforeUpdate = productRepository.findAll().size();

    // Update the product
    ProductDTO productDto = productMapper.toDto(
        productRepository.findById(product.getId()).get());
    productDto.setProductCategoryEnum(UPDATED_PRODUCT_CATEGORY_ENUM);
    productDto.setName(UPDATED_NAME);
    productDto.setQuantity(UPDATED_QUANTITY);
    productDto.setPriceNet(UPDATED_PRICE_NET);
    productDto.setVat(UPDATED_VAT);
    productDto.setPriceGross(null);
    productDto.setStock(UPDATED_STOCK);
    productDto.setDescription(UPDATED_DESCRIPTION);
    productDto.setCreateTime(UPDATED_CREATE_TIME);
    productDto.setUpdateTime(UPDATED_UPDATE_TIME);
    productDto.setImage(UPDATED_IMAGE);
    productDto.setImageContentType(UPDATED_IMAGE_CONTENT_TYPE);

    restProductMockMvc.perform(put(ENTITY_API_URL_ID, productDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isOk());

    // Validate the Product in the database
    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeUpdate);
    Product testProduct = productList.get(productList.size() - 1);
    assertThat(testProduct.getProductCategoryEnum()).isEqualTo(UPDATED_PRODUCT_CATEGORY_ENUM);
    assertThat(testProduct.getName()).isEqualTo(UPDATED_NAME);
    assertThat(testProduct.getQuantity()).isEqualTo(DEFAULT_QUANTITY);
    assertThat(testProduct.getPriceNet()).isEqualTo(UPDATED_PRICE_NET);
    assertThat(testProduct.getVat()).isEqualTo(UPDATED_VAT);
    assertThat(testProduct.getPriceGross()).isEqualTo(updatedProperPriceGross);
    assertThat(testProduct.getStock()).isEqualTo(UPDATED_STOCK);
    assertThat(testProduct.getDescription()).isEqualTo(UPDATED_DESCRIPTION);
    assertNotNull(testProduct.getCreateTime());
    assertNotNull(testProduct.getUpdateTime());
    assertThat(testProduct.getImage()).isEqualTo(UPDATED_IMAGE);
    assertThat(testProduct.getImageContentType()).isEqualTo(UPDATED_IMAGE_CONTENT_TYPE);
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void putNonExistingProduct() throws Exception {
    int databaseSizeBeforeUpdate = productRepository.findAll().size();
    product.setId(count.incrementAndGet());

    // Create the Product
    ProductDTO productDto = productMapper.toDto(product);

    // If the entity doesn't have an ID, it will throw BadRequestAlertException
    restProductMockMvc.perform(put(ENTITY_API_URL_ID, productDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isBadRequest());

    // Validate the Product in the database
    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void putWithIdMismatchProduct() throws Exception {
    int databaseSizeBeforeUpdate = productRepository.findAll().size();
    product.setId(count.incrementAndGet());

    // Create the Product
    ProductDTO productDto = productMapper.toDto(product);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restProductMockMvc.perform(put(ENTITY_API_URL_ID, count.incrementAndGet())
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isBadRequest());

    // Validate the Product in the database
    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void putWithMissingIdPathParamProduct() throws Exception {
    int databaseSizeBeforeUpdate = productRepository.findAll().size();
    product.setId(count.incrementAndGet());

    // Create the Product
    ProductDTO productDto = productMapper.toDto(product);

    // If url ID doesn't match entity ID, it will throw BadRequestAlertException
    restProductMockMvc.perform(put(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isMethodNotAllowed());

    // Validate the Product in the database
    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeUpdate);
  }

  @Test
  @Transactional
  @WithMockUser
  void deleteProductByUserShouldThrowStatusForbidden() throws Exception {
    int databaseSizeBeforeDelete = productRepository.findAll().size();

    // Create the Product
    ProductDTO productDto = productMapper.toDto(product);

    restProductMockMvc.perform(delete(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isForbidden());

    // Validate the Product in the database
    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeDelete);
  }

  @Test
  @Transactional
  void deleteProductByAnyoneShouldThrowStatusUnauthorized() throws Exception {
    int databaseSizeBeforeDelete = productRepository.findAll().size();

    // Create the Product
    ProductDTO productDto = productMapper.toDto(product);
    restProductMockMvc.perform(delete(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(productDto)))
        .andExpect(status().isUnauthorized());

    // Validate the Product in the database
    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeDelete);
  }

  @Test
  @Transactional
  @WithMockUser(username = "admin", password = "admin", authorities = AuthoritiesConstants.ADMIN)
  void deleteProduct() throws Exception {
    // Initialize the database
    productRepository.saveAndFlush(product);

    int databaseSizeBeforeDelete = productRepository.findAll().size();

    // Delete the product
    restProductMockMvc.perform(delete(ENTITY_API_URL_ID, product.getId())
            .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());

    // Validate the database contains one less item
    List<Product> productList = productRepository.findAll();
    assertThat(productList).hasSize(databaseSizeBeforeDelete - 1);
  }
}
