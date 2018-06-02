package com.example.dell.serverkoreanfood.Model;

/**
 * Created by dell on 21/01/2018.
 */

public class Food {


    private String Name, Image ,Descripition,Price, Discount, MenuId;


    public Food() {
    }


    public Food(String name, String image, String descripition, String price, String discount, String menuId) {
        Name = name;
        Image = image;
        Descripition = descripition;
        Price = price;
        Discount = discount;
        MenuId = menuId;
    }


    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getDescripition() {
        return Descripition;
    }

    public void setDescripition(String descripition) {
        Descripition = descripition;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getDiscount() {
        return Discount;
    }

    public void setDiscount(String discount) {
        Discount = discount;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }
}
