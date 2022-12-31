package com.naeggeodo.entity.chat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CategoryTest {
    @Test
    @DisplayName("카테고리 기본 이미지값 전체 검증")
    void defaultImagePathTest(){
        String all = Category.ALL.getDefaultImagePath();
        String chicken = Category.CHICKEN.getDefaultImagePath();
        String pizza = Category.PIZZA.getDefaultImagePath();
        String fastFood = Category.FASTFOOD.getDefaultImagePath();
        String dessert = Category.DESSERT.getDefaultImagePath();
        String japanese = Category.JAPANESE.getDefaultImagePath();
        String chinese = Category.CHINESE.getDefaultImagePath();
        String korean = Category.KOREAN.getDefaultImagePath();
        String snacks = Category.SNACKS.getDefaultImagePath();
        String stew = Category.STEW.getDefaultImagePath();
        String western = Category.WESTERN.getDefaultImagePath();
        String grilledMeat = Category.GRILLED_MEAT.getDefaultImagePath();
        String porkFeet = Category.PORK_FEET.getDefaultImagePath();

        assertThat(all).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798239/default/allDefault_uhmnbe.svg");
        assertThat(chicken).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798239/default/chickenDefault_dzwvd5.svg");
        assertThat(pizza).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798241/default/pizzaDefault_avlmik.svg");
        assertThat(fastFood).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655823879/default/hamburgerDefault_mkyf7d.svg");
        assertThat(dessert).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798240/default/dessertDefault_apngiz.svg");
        assertThat(japanese).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798243/default/sushiDefault_qx8bwe.svg");
        assertThat(chinese).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798239/default/chineseDefault_pew8ro.svg");
        assertThat(korean).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798239/default/koreaDefault_ja7ibh.svg");
        assertThat(snacks).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798241/default/ramenDefault_jnhgw8.svg");
        assertThat(stew).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798242/default/soupDefault_lpfgcb.svg");
        assertThat(western).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798243/default/spagettiDefault_arpmqg.svg");
        assertThat(grilledMeat).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798241/default/meatDefault_teiofj.svg");
        assertThat(porkFeet).isEqualTo("https://res.cloudinary.com/naeggeodo/image/upload/v1655798241/default/porkDefault_ufal2f.svg");
    }
}