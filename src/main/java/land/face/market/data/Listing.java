package land.face.market.data;

import java.util.UUID;
import land.face.market.data.PlayerMarketState.Category;
import land.face.market.data.PlayerMarketState.FilterFlagA;
import land.face.market.data.PlayerMarketState.FilterFlagB;
import land.face.market.utils.DiscordUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.inventory.ItemStack;

public class Listing {

  private ItemStack itemStack;
  private Category category;
  private FilterFlagA flagA;
  private FilterFlagB flagB;
  private int price;
  private Long listingTime;
  private String sellerName;
  private UUID sellerUuid;
  private UUID listingId;
  private boolean sold;
  @Getter @Setter
  private boolean claimed;
  private boolean expired;

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

  public int getPrice() {
    return price;
  }

  public void setPrice(int price) {
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

  public boolean isSold() {
    return sold;
  }

  public void setSold(boolean sold) {
    this.sold = sold;
  }

  public boolean isExpired() {
    return expired;
  }

  public void setExpired(boolean expired) {
    this.expired = expired;
  }
}
