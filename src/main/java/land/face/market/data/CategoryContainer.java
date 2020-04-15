package land.face.market.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import land.face.market.data.PlayerMarketState.FilterFlagA;
import land.face.market.data.PlayerMarketState.FilterFlagB;
import org.bukkit.Material;

public class CategoryContainer {

  private String name;
  private Set<Material> fallbackMaterials;
  private Map<FilterFlagA, String> filterNamesA = new HashMap<>();
  private Map<FilterFlagB, String> filterNamesB = new HashMap<>();

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Set<Material> getFallbackMaterials() {
    return fallbackMaterials;
  }

  public void setFallbackMaterials(Set<Material> fallbackMaterials) {
    this.fallbackMaterials = fallbackMaterials;
  }

  public Map<FilterFlagA, String> getFilterNamesA() {
    return filterNamesA;
  }

  public void setFilterNamesA(
      Map<FilterFlagA, String> filterNamesA) {
    this.filterNamesA = filterNamesA;
  }

  public Map<FilterFlagB, String> getFilterNamesB() {
    return filterNamesB;
  }

  public void setFilterNamesB(
      Map<FilterFlagB, String> filterNamesB) {
    this.filterNamesB = filterNamesB;
  }
}
