package land.face.market.data.comparators;

import java.util.Comparator;
import land.face.market.data.Listing;

public class LevelComparator implements Comparator<Listing> {

  public int compare(Listing listing1, Listing listing2) {
    return Integer.compare(listing1.getFlagA().ordinal(), listing2.getFlagA().ordinal());
  }
}
