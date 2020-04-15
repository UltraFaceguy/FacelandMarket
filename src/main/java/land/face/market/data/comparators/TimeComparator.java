package land.face.market.data.comparators;

import java.util.Comparator;
import land.face.market.data.Listing;

public class TimeComparator implements Comparator<Listing> {

  public int compare(Listing listing1, Listing listing2) {
    return Long.compare(listing1.getListingTime(), listing2.getListingTime());
  }
}
