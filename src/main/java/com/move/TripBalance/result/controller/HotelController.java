package com.move.TripBalance.result.controller;

import com.move.TripBalance.result.service.HotelService;
import com.move.TripBalance.result.Product;
import com.move.TripBalance.result.SearchKeyword;
import com.move.TripBalance.result.repository.SearchKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/tb")
@RestController
@RequiredArgsConstructor
public class HotelController {
    @Autowired
    private SearchKeywordRepository searchKeywordRepository;

    @GetMapping("/hotel/{keyword}")
    @ResponseBody
    public List<Product> crawHotel(@PathVariable String keyword) {
        System.out.println(keyword);
        List<Product> products = new HotelService().startAllCraw(keyword);
        SearchKeyword searchKeywordEntity =
                searchKeywordRepository.findByKeyword(keyword);
        for(Product product:products) {
            product.setKeyword(searchKeywordEntity);
        }
        System.out.println(products);
        return products;
    }
}

