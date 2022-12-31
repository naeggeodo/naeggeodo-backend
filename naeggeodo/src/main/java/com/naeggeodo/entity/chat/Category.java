package com.naeggeodo.entity.chat;

import lombok.Getter;

@Getter
public enum Category {
    ALL("https://res.cloudinary.com/naeggeodo/image/upload/v1655798239/default/allDefault_uhmnbe.svg"),
    CHICKEN("https://res.cloudinary.com/naeggeodo/image/upload/v1655798239/default/chickenDefault_dzwvd5.svg"),
    PIZZA("https://res.cloudinary.com/naeggeodo/image/upload/v1655798241/default/pizzaDefault_avlmik.svg"),
    FASTFOOD("https://res.cloudinary.com/naeggeodo/image/upload/v1655823879/default/hamburgerDefault_mkyf7d.svg"),
    DESSERT("https://res.cloudinary.com/naeggeodo/image/upload/v1655798240/default/dessertDefault_apngiz.svg"),
    JAPANESE("https://res.cloudinary.com/naeggeodo/image/upload/v1655798243/default/sushiDefault_qx8bwe.svg"),
    CHINESE("https://res.cloudinary.com/naeggeodo/image/upload/v1655798239/default/chineseDefault_pew8ro.svg"),
    KOREAN("https://res.cloudinary.com/naeggeodo/image/upload/v1655798239/default/koreaDefault_ja7ibh.svg"),
    SNACKS("https://res.cloudinary.com/naeggeodo/image/upload/v1655798241/default/ramenDefault_jnhgw8.svg"),
    STEW("https://res.cloudinary.com/naeggeodo/image/upload/v1655798242/default/soupDefault_lpfgcb.svg"),
    WESTERN("https://res.cloudinary.com/naeggeodo/image/upload/v1655798243/default/spagettiDefault_arpmqg.svg"),
    GRILLED_MEAT("https://res.cloudinary.com/naeggeodo/image/upload/v1655798241/default/meatDefault_teiofj.svg"),
    PORK_FEET("https://res.cloudinary.com/naeggeodo/image/upload/v1655798241/default/porkDefault_ufal2f.svg");

    private final String defaultImagePath;

    Category(String defaultImagePath) {
        this.defaultImagePath = defaultImagePath;
    }
}
