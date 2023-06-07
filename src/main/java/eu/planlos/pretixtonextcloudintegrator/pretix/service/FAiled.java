package eu.planlos.pretixtonextcloudintegrator.pretix.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FAiled {

//    public void saveAll(List<ItemDTO> itemDTOList) {
//        List<Item> itemList = itemDTOList.stream().map(itemDTO -> {
//            ItemCategory itemCategory = itemCategoryService.loadOrFetch(itemDTO.category());
//            return convert(itemDTO, itemCategory);
//        }).collect(Collectors.toList());
//        itemRepository.saveAll(itemList);
//    }
//
//    public Item loadOrFetch(Long itemId) {
//
//        // Get from DB
//        Optional<Item> itemOptional = itemRepository.findById(itemId);
//        if(itemOptional.isPresent()) {
//            log.info("Loaded item from db: {}", itemId);
//            return itemOptional.get();
//        }
//
//        // or fetch from Pretix
//        return fetchFromPretix(itemId);
//    }
//
//    public void fetchAll() {
//        List<ItemDTO> itemDTOList = pretixApiItemService.queryAllItems();
//        List<Item> itemList = itemDTOList.stream()
//                .map(itemDTO -> convert(itemDTO, itemCategoryService.loadOrFetch(itemDTO.category())))
//                .collect(Collectors.toList());
//        itemRepository.saveAll(itemList);
//    }
//
//    private Item fetchFromPretix(Long itemId) {
//        ItemDTO itemDTO = pretixApiItemService.queryItem(itemId);
//        ItemCategory itemCategory = itemCategoryService.loadOrFetch(itemDTO.category());
//        Item item = convert(itemDTO, itemCategory);
//        return itemRepository.save(item);
//    }
}
