package land.face.market.data;

import org.bukkit.inventory.ItemStack;

public class SoldListing {

  private ItemStack itemStack;
  private double amount;

  public ItemStack getItemStack() {
    return itemStack;
  }

  public void setItemStack(ItemStack itemStack) {
    this.itemStack = itemStack;
  }

  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }

  public static SoldListing fromListing(Listing listing) {
    SoldListing soldListing = new SoldListing();
    soldListing.setItemStack(listing.getItemStack().clone());
    soldListing.setAmount(listing.getPrice());
    return soldListing;
  }
}
