package land.face.market.data;

public class PlayerMarketState {

  private SortStyle sortStyle;
  private Category selectedCategory;
  private FilterFlagA filterA;
  private FilterFlagB filterB;
  private int page = 1;

  public Category getSelectedCategory() {
    return selectedCategory;
  }

  public void setSelectedCategory(Category selectedCategory) {
    this.selectedCategory = selectedCategory;
  }

  public SortStyle getSortStyle() {
    return sortStyle;
  }

  public void setSortStyle(SortStyle sortStyle) {
    this.sortStyle = sortStyle;
  }

  public FilterFlagB getFilterB() {
    return filterB;
  }

  public void setFilterB(FilterFlagB filterB) {
    this.filterB = filterB;
  }

  public FilterFlagA getFilterA() {
    return filterA;
  }

  public void setFilterA(FilterFlagA filterA) {
    this.filterA = filterA;
  }

  public int getPage() {
    return page;
  }

  public void setPage(int page) {
    this.page = page;
  }

  public static final Category[] CATEGORIES = Category.values();

  public enum Category {
    CATEGORY_1,
    CATEGORY_2,
    CATEGORY_3,
    CATEGORY_4,
    CATEGORY_5
  }

  public static final FilterFlagA[] FILTER_AS = FilterFlagA.values();

  public enum FilterFlagA {
    ALL,
    FLAG_1,
    FLAG_2,
    FLAG_3,
    FLAG_4,
    FLAG_5,
    FLAG_6,
    FLAG_7,
    FLAG_8,
    FLAG_9,
    FLAG_10
  }

  public static final FilterFlagB[] FILTER_BS = FilterFlagB.values();

  public enum FilterFlagB {
    ALL,
    FLAG_1,
    FLAG_2,
    FLAG_3,
    FLAG_4,
    FLAG_5,
    FLAG_6,
    FLAG_7,
    FLAG_8,
    FLAG_9,
    FLAG_10
  }

  public static final SortStyle[] SORT_STYLES = SortStyle.values();

  public enum SortStyle {
    TIME_ASCENDING,
    TIME_DESCENDING,
    LEVEL_ASCENDING,
    LEVEL_DESCENDING,
    PRICE_ASCENDING,
    PRICE_DESCENDING,
    RARITY_ASCENDING,
    RARITY_DESCENDING,
  }
}
