package net.code7y7.sorcerymod.item.weapon;

import net.code7y7.sorcerymod.perk.PerkRegistry;
import net.code7y7.sorcerymod.perk.WeaponPerk;
import net.code7y7.sorcerymod.perk.elemental.ElementalPerk;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SwordItem;
import net.minecraft.item.ToolMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public abstract class WeaponItem extends SwordItem {
    private final WeaponType weaponType;
    private final List<WeaponPerk> perks;
    public WeaponItem(WeaponType type, ToolMaterial material, float attackDamage, float attackSpeed, Settings settings) {
        super(material, attackDamage, attackSpeed, settings);
        this.weaponType = type;
        this.perks = new ArrayList<>();
    }

    public void addPerk(WeaponPerk perk){
        if(perk.isCompatible(this)){
            perks.add(perk);
        }
    }

    public List<WeaponPerk> getPerks(){
        return perks;
    }

    public void clearPerks(){
        perks.clear();
    }

    public void onAttack(PlayerEntity player, Entity target){
        getPerks().forEach(perk -> perk.onAttack(player, target));
    }

    public void generateRandomPerks() {
        clearPerks();
        Random random = new Random();

        //first perk
        List<WeaponPerk> generalPool = PerkRegistry.getGeneralPerks();
        addRandomPerkFromPool(generalPool, random);

        //second perk
        List<WeaponPerk> weaponSpecificPool = PerkRegistry.getWeaponSpecificPerks(weaponType);
        addRandomPerkFromPool(weaponSpecificPool, random);

        //third perk, 15% chance
        if(random.nextFloat() <= 0.15f) {
            List<ElementalPerk> elementalPool = PerkRegistry.getElementalPerks();
            addRandomPerkFromPool(elementalPool, random);
        } else {
            addRandomPerkFromPool(weaponSpecificPool, random);
        }
    }

    private void addRandomPerkFromPool(List<? extends WeaponPerk> pool, Random random) {
        if(!pool.isEmpty()) {
            List<WeaponPerk> validPerks = pool.stream()
                    .filter(perk -> perk.isCompatible(this))
                    .collect(Collectors.toList());

            if(!validPerks.isEmpty()) {
                WeaponPerk selected = validPerks.get(random.nextInt(validPerks.size()));
                this.addPerk(selected);
            }
        }
    }
}
