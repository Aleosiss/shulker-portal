from nbt import nbt

level_dat = "run/world/level.dat"


def terralith_biome(_biome):
    # print("biome: " + str(_biome["biome"]))
    return not str(_biome["biome"]).startswith("terralith:")


def terralith_recipe(_recipe):
    # print("recipe: " + str(_recipe))
    return not str(recipe).startswith("terralith:")


def terralith(_datapack):
    return not str(_datapack) == "terralith"


nbt = nbt.NBTFile(level_dat, 'rb')
for dimension in nbt["Data"]["WorldGenSettings"]["dimensions"].tags:  # This loop will show us each entry
    print("dimension: " + str(dimension["type"]))
    if "biomes" in dimension["generator"]["biome_source"]:
        biomes = dimension["generator"]["biome_source"]["biomes"]
        biomes[:] = [biome for biome in biomes if terralith_biome(biome)]

recipes = nbt["Data"]["Player"]["recipeBook"]["recipes"].tags
for recipe in recipes:
    recipes[:] = [recipe for recipe in recipes if terralith_recipe(recipe)]

toBeDisplayed = nbt["Data"]["Player"]["recipeBook"]["toBeDisplayed"].tags
for recipe in toBeDisplayed:
    toBeDisplayed[:] = [recipe for recipe in toBeDisplayed if terralith_recipe(recipe)]

datapacks = nbt["Data"]["DataPacks"]["Enabled"].tags
for datapack in datapacks:
    datapacks[:] = [datapack for datapack in datapacks if terralith(datapack)]

nbt.write_file(level_dat)
