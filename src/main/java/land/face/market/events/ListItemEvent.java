package land.face.market.events;

import land.face.market.data.Listing;
import org.bukkit.entity.Player;

public class ListItemEvent extends MarketCancellableEvent {

  private final Player seller;
  private final Listing listing;

  public ListItemEvent(Player seller, Listing listing) {
    this.seller = seller;
    this.listing = listing;
  }

  public Player getSeller() {
    return seller;
  }

  public Listing getListing() {
    return listing;
  }
}