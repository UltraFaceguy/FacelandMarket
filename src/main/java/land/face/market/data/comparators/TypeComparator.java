package land.face.market.data.comparators;

import java.util.Comparator;
import land.face.market.data.Listing;

public class TypeComparator implements Comparator<Listing> {

  public int compare(Listing listing1, Listing listing2) {
    return listing1.getItemStack().getType().toString().compareTo(listing2.getItemStack().getType().toString());
  }
}
