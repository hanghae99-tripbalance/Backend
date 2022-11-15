package com.move.TripBalance.result.service;

import java.util.ArrayList;

import com.move.TripBalance.result.Product;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;


import java.util.List;

@Getter
@Setter
@Service
@RequiredArgsConstructor
public class HotelService {

    public List<Product> startAllCraw(String keyword) {

        List<Product> products = new ArrayList<>();
        String url = "https://www.goodchoice.kr/product/result?keyword=" + keyword;
        try {
            Document doc = Jsoup.connect(url).get();
            Elements stockTableBody = doc.select("div.list_wrap li");

            for (int i =0; i < 4; i++) {
                Product product = new Product();

                String text;
                String img;
                text = stockTableBody.get(i).select("p.pic img.lazy").attr("alt");

                img = stockTableBody.get(i).select("p.pic img.lazy").attr("data-original");

                product.setTitle(text);
                product.setImg(img);
                products.add(product);

                System.out.println(products);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return products;
    }
}

