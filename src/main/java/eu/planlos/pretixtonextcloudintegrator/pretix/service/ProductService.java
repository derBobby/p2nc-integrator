package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.PretixId;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Product;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.ProductType;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.ItemCategoryDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.ItemDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.ProductRepository;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.ProductTypeRepository;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiItemCategoryService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiItemService;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductTypeRepository productTypeRepository;

    private final PretixApiItemCategoryService pretixApiItemCategoryService;
    private final PretixApiItemService pretixApiItemService;

    public ProductService(PretixApiItemCategoryService pretixApiItemCategoryService, PretixApiItemService pretixApiItemService, ProductRepository productRepository, ProductTypeRepository productTypeRepository) {
        this.pretixApiItemCategoryService = pretixApiItemCategoryService;
        this.pretixApiItemService = pretixApiItemService;
        this.productRepository = productRepository;
        this.productTypeRepository = productTypeRepository;
    }

    /*
     *   Fetching
     */

    public void fetchAll(String event) {
        fetchAllProductTypes(event);
        fetchAllProducts(event);
    }

    private void fetchAllProductTypes(String event) {
        List<ItemCategoryDTO> itemCategoryDTOList = pretixApiItemCategoryService.queryAllItemCategories(event);
        List<ProductType> productTypeList = itemCategoryDTOList.stream().map(this::convert).toList();
        productTypeRepository.saveAll(productTypeList);
    }

    private ProductType fetchProductType(String event, PretixId pretixId) {
        ItemCategoryDTO itemCategoryDTO = pretixApiItemCategoryService.queryItemCategory(event, pretixId);
        ProductType productType = convert(itemCategoryDTO);
        return productTypeRepository.save(productType);
    }

    private void fetchAllProducts(String event) {
        List<ItemDTO> itemDTOList = pretixApiItemService.queryAllItems(event);
        List<Product> productList = itemDTOList.stream().map(itemDTO -> convert(event, itemDTO)).flatMap(List::stream).toList();
        saveProducts(productList);
    }

    private Optional<Product> fetchProduct(@NotNull String event, @NotNull PretixId pretixId, @NotNull PretixId pretixVariationId) {
        ItemDTO itemDTO = pretixApiItemService.queryItem(event, pretixId);
        List<Product> productList = convert(event, itemDTO);
        return saveProducts(productList).stream().filter(streamedProduct -> {
            if (streamedProduct.getPretixVariationId() == null || streamedProduct.getPretixVariationId().getValue() == null && pretixVariationId == null) {
                return true;
            }
            return Objects.equals(streamedProduct.getPretixVariationId(), pretixVariationId);
        }).findFirst();
    }

    /*
     * Retrieving
     */

    public ProductType loadOrFetchProductType(String event, PretixId pretixId) {

        // Get from DB
        Optional<ProductType> productType = productTypeRepository.findByPretixId(pretixId);
        if (productType.isPresent()) {
            log.info("Loaded product type from db: {} ", pretixId);
            return productType.get();
        }

        // or fetch from Pretix
        return fetchProductType(event, pretixId);
    }

    public Product loadOrFetchProduct(String event, PretixId pretixId, PretixId pretixVariationId) {

        Optional<Product> product;

        // Get from DB
        product = productRepository.findByPretixIdAndPretixVariationId(pretixId, pretixVariationId);
        if (product.isPresent()) {
            log.info("Loaded product from db: {} ", pretixId);
            return product.get();
        }

        product = fetchProduct(event, pretixId, pretixVariationId);
        if (product.isPresent()) {
            return product.get();
        }

        throw new RuntimeException("Product not found for pretixId: " + pretixId + " and variation: " + pretixVariationId);
    }

    /*
     * Saving
     */

    private List<Product> saveProducts(List<Product> productList) {
        return productList.stream().map(this::saveProduct).toList();
    }

    private Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    /*
     * Converter
     */
    private ProductType convert(ItemCategoryDTO itemCategoryDTO) {
        return new ProductType(new PretixId(itemCategoryDTO.id()), itemCategoryDTO.is_addon(), itemCategoryDTO.getName());
    }

    private List<Product> convert(String event, ItemDTO itemDTO) {
        ProductType productType = loadOrFetchProductType(event, new PretixId(itemDTO.category()));

        String baseName = itemDTO.getName();

        // No variations
        if (itemDTO.variations().isEmpty()) {
            return List.of(new Product(new PretixId(itemDTO.id()), baseName, productType));
        }

        // Variations
        return itemDTO.variations().stream().map(itemVariationDTO -> {
            log.debug("IDs for item are {} - {}", itemDTO.id(), itemVariationDTO.id());
            String fullName = String.join(" - ", baseName, itemVariationDTO.getName());
            return new Product(new PretixId(itemDTO.id()), new PretixId(itemVariationDTO.id()), fullName, productType);
        }).toList();
    }
}