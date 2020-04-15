package land.face.market.data;

import java.util.UUID;
import land.face.market.data.PlayerMarketState.Category;
import land.face.market.data.PlayerMarketState.FilterFlagA;
import land.face.market.data.PlayerMarketState.FilterFlagB;
import org.bukkit.inventory.ItemStack;

public class Listing {

  private ItemStack itemStack;
  private Category category;
  private FilterFlagA flagA;
  private FilterFlagB flagB;
  private double price;
  private Long listingTime;
  private String sellerName;
  private UUID sellerUuid;
  private UUID listingId;
  public ItemStack getItemStack() {
    return itemStack;
  }

  public void setItemStack(ItemStack itemStack) {
    this.itemStack = itemStack;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public FilterFlagA getFlagA() {
    return flagA;
  }

  public void setFlagA(FilterFlagA flagA) {
    this.flagA = flagA;
  }

  public FilterFlagB getFlagB() {
    return flagB;
  }

  public void setFlagB(FilterFlagB flagB) {
    this.flagB = flagB;
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public Long getListingTime() {
    return listingTime;
  }

  public void setListingTime(Long listingTime) {
    this.listingTime = listingTime;
  }

  public String getSellerName() {
    return sellerName;
  }

  public void setSellerName(String sellerName) {
    this.sellerName = sellerName;
  }

  public UUID getSellerUuid() {
    return sellerUuid;
  }

  public void setSellerUuid(UUID sellerUuid) {
    this.sellerUuid = sellerUuid;
  }

  public UUID getListingId() {
    return listingId;
  }

  public void setListingId(UUID listingId) {
    this.listingId = listingId;
  }
}
