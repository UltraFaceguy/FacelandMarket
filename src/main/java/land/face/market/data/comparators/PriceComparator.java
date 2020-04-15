package land.face.market.data.comparators;

import java.util.Comparator;
import land.face.market.data.Listing;

public class PriceComparator implements Comparator<Listing> {

  public int compare(Listing listing1, Listing listing2) {
    return Double.compare(listing1.getPrice(), listing2.getPrice());
  }
}
