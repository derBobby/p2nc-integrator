package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import eu.planlos.pretixtonextcloudintegrator.pretix.model.*;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.ItemCategoryDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.model.dto.single.ItemDTO;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.AddonRepository;
import eu.planlos.pretixtonextcloudintegrator.pretix.repository.TicketRepository;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiItemCategoryService;
import eu.planlos.pretixtonextcloudintegrator.pretix.service.api.PretixApiItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProductService {

    private final AddonRepository addonRepository;
    private final TicketRepository ticketRepository;

    private final PretixApiItemCategoryService pretixApiItemCategoryService;
    private final PretixApiItemService pretixApiItemService;

    public ProductService(PretixApiItemCategoryService pretixApiItemCategoryService, PretixApiItemService pretixApiItemService,
                          AddonRepository addonRepository, TicketRepository ticketRepository) {
        this.pretixApiItemCategoryService = pretixApiItemCategoryService;
        this.pretixApiItemService = pretixApiItemService;
        this.addonRepository = addonRepository;
        this.ticketRepository = ticketRepository;
    }

//    public ItemCategory loadOrFetch(Long itemCategoryId) {
//
//        // Get from DB
//        Optional<Category> itemCategoryOptional = itemCategoryRepository.findById(itemCategoryId);
//        if(itemCategoryOptional.isPresent()) {
//            log.info("Loaded item category from db: {} ", itemCategoryId);
//            return itemCategoryOptional.get();
//        }
//
//        // or fetch from Pretix
//        return fetchFromPretix(itemCategoryId);
//    }

    public void fetchAll() {
        List<ItemCategoryDTO> itemCategoryDTOList = pretixApiItemCategoryService.queryAllItemCategories();
        List<Product> productList = itemCategoryDTOList.stream().map(this::convert).collect(Collectors.toList());
        productList.forEach(this::saveProduct);
    }

    private void saveProduct(Product product) {
        if (product.getClass().equals(Addon.class)) {
            addonRepository.save((Addon) product);
            return;
        }

        if (product.getClass().equals(Ticket.class)) {
            ticketRepository.save((Ticket) product);
            return;
        }

        throw new InvalidParameterException();
    }

//    private ItemCategory fetchFromPretix(Long itemCategoryId) {
//        ItemCategoryDTO itemCategoryDTO = pretixApiItemCategoryService.queryItemCategory(itemCategoryId);
//        ItemCategory itemCategory = convert(itemCategoryDTO);
//        return itemCategoryRepository.save(itemCategory);
//    }


    private Product convert(ItemCategoryDTO itemCategoryDTO) {

        if(itemCategoryDTO.is_addon()) {
            return new Addon(
                    itemCategoryDTO.id(),
                    itemCategoryDTO.name().get("de-informal"));
        }

        if(! itemCategoryDTO.is_addon()) {
            return new Ticket();
        }

        throw new IllegalArgumentException("");
    }

//    private Item convert(ItemDTO itemDTO, ItemCategory itemCategory) {
//        return new Item(
//                itemDTO.id(),
//                itemDTO.name().get("de-informal"),
//                itemCategory
//        );
//    }
}
