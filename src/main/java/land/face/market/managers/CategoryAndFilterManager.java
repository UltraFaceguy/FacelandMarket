package land.face.market.managers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import land.face.market.FacelandMarketPlugin;
import land.face.market.data.CategoryContainer;
import land.face.market.data.PlayerMarketState.Category;
import land.face.market.data.PlayerMarketState.FilterFlagA;
import land.face.market.data.PlayerMarketState.FilterFlagB;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;

public class CategoryAndFilterManager {

  private FacelandMarketPlugin plugin;

  private Map<Category, CategoryContainer> categoryData = new HashMap<>();

  public CategoryAndFilterManager(FacelandMarketPlugin plugin) {
    this.plugin = plugin;

    for (Category c : Category.values()) {
      CategoryContainer categoryContainer = new CategoryContainer();
      categoryContainer.setName(
          plugin.getSettings().getString("config.basic-categories." + c + ".name", c.toString()));
      Set<Material> materials = new HashSet<>();
      for (String s : plugin.getSettings()
          .getStringList("config.basic-categories." + c + ".materials")) {
        try {
          materials.add(Material.valueOf(s));
        } catch (Exception e) {
          Bukkit.getLogger().warning("Tried to load invalid material " + s + " for " + c);
        }
      }
      categoryContainer.setFallbackMaterials(materials);
      Map<FilterFlagA, String> filterNamesA = new HashMap<>();
      for (FilterFlagA f : FilterFlagA.values()) {
        String name = plugin.getSettings()
            .getString("config.basic-categories." + c + ".filterNamesA." + f, "");
        if (StringUtils.isNotBlank(name)) {
          filterNamesA.put(f, name);
        }
      }
      categoryContainer.setFilterNamesA(filterNamesA);

      Map<FilterFlagB, String> filterNamesB = new HashMap<>();
      for (FilterFlagB f : FilterFlagB.values()) {
        String name = plugin.getSettings()
            .getString("config.basic-categories." + c + ".filterNamesB." + f, "");
        if (StringUtils.isNotBlank(name)) {
          filterNamesB.put(f, name);
        }
      }
      categoryContainer.setFilterNamesB(filterNamesB);

      categoryData.put(c, categoryContainer);
    }
  }

  public Map<Category, CategoryContainer> getCategoryData() {
    return categoryData;
  }
}
