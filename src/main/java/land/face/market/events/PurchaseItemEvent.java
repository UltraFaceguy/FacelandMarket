package land.face.market.events;

import land.face.market.data.Listing;
import org.bukkit.entity.Player;

public class PurchaseItemEvent extends MarketEvent {

  private final Player buyer;
  private final Listing listing;

  public PurchaseItemEvent(Player buyer, Listing listing) {
    this.buyer = buyer;
    this.listing = listing;
  }

  public Player getBuyer() {
    return buyer;
  }

  public Listing getListing() {
    return listing;
  }
}