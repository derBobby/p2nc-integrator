package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.Addon;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Product;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.ProductType;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.Ticket;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.ItemCategoryDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.ItemDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.AddonRepository;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.ProductTypeRepository;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.TicketRepository;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiItemCategoryService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class ProductService {

    private final AddonRepository addonRepository;
    private final TicketRepository ticketRepository;
    private final ProductTypeRepository productTypeRepository;

    private final PretixApiItemCategoryService pretixApiItemCategoryService;
    private final PretixApiItemService pretixApiItemService;

    public ProductService(PretixApiItemCategoryService pretixApiItemCategoryService, PretixApiItemService pretixApiItemService,
                          AddonRepository addonRepository, TicketRepository ticketRepository, ProductTypeRepository productTypeRepository) {
        this.pretixApiItemCategoryService = pretixApiItemCategoryService;
        this.pretixApiItemService = pretixApiItemService;
        this.addonRepository = addonRepository;
        this.ticketRepository = ticketRepository;
        this.productTypeRepository = productTypeRepository;
    }

    /*
     *   Fetching
     */

    public void fetchAll() {
        fetchAllProductTypes();
        fetchAllProducts();
    }

    private void fetchAllProductTypes() {
        List<ItemCategoryDTO> itemCategoryDTOList = pretixApiItemCategoryService.queryAllItemCategories();
        List<ProductType> productTypeList = itemCategoryDTOList.stream().map(this::convert).toList();
        productTypeRepository.saveAll(productTypeList);
    }

    private ProductType fetchProductType(Long pretixId) {
        ItemCategoryDTO itemCategoryDTO = pretixApiItemCategoryService.queryItemCategory(pretixId);
        ProductType productType = convert(itemCategoryDTO);
        return productTypeRepository.save(productType);
    }

    private void fetchAllProducts() {
        List<ItemDTO> itemDTOList = pretixApiItemService.queryAllItems();
        List<Product> productList = itemDTOList.stream().map(this::convert).toList();
        saveProducts(productList);
    }

    private Product fetchProduct(Long pretixId) {
        ItemDTO itemDTO = pretixApiItemService.queryItem(pretixId);
        Product product = convert(itemDTO);
        return saveProduct(product);
    }

    /*
     * Retrieving
     */
    public ProductType loadOrFetchProductType(Long pretixId) {

        // Get from DB
        Optional<ProductType> productType = productTypeRepository.findByPretixId(pretixId);
        if(productType.isPresent()) {
            log.info("Loaded product type from db: {} ", pretixId);
            return productType.get();
        }

        // or fetch from Pretix
        return fetchProductType(pretixId);
    }

    public Ticket loadOrFetchTicket(Long pretixId) {

        // Get from DB
        Optional<Ticket> ticket = ticketRepository.findByPretixId(pretixId);
        if(ticket.isPresent()) {
            log.info("Loaded ticket from db: {} ", pretixId);
            return ticket.get();
        }

        // or fetch from Pretix
        return (Ticket) fetchProduct(pretixId);
    }

    public Addon loadOrFetchAddon(Long pretixId) {

        // Get from DB
        Optional<Addon> addon = addonRepository.findByPretixId(pretixId);
        if(addon.isPresent()) {
            log.info("Loaded ticket from db: {} ", pretixId);
            return addon.get();
        }

        // or fetch from Pretix
        return (Addon) fetchProduct(pretixId);
    }

    /*
     * Saving
     */

    private void saveProducts(List<Product> productList) {
        productList.forEach(this::saveProduct);
    }

    private Product saveProduct(Product product) {
        if (product.getClass().equals(Addon.class)) {
            return addonRepository.save((Addon) product);
        }

        if (product.getClass().equals(Ticket.class)) {
            return ticketRepository.save((Ticket) product);
        }

        throw new InvalidParameterException();
    }

    /*
     * Converter
     */
    private ProductType convert(ItemCategoryDTO itemCategoryDTO) {
        return new ProductType(itemCategoryDTO.id(), itemCategoryDTO.is_addon(), itemCategoryDTO.name().get("de-informal"));
    }

//    private Item convert(ItemDTO itemDTO, ItemCategory itemCategory) {
//        return new Item(
//                itemDTO.id(),
//                itemDTO.name().get("de-informal"),
//                itemCategory
//        );
//    }
}