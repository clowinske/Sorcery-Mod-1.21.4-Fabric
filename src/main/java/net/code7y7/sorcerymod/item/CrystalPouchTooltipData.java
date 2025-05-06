package net.code7y7.sorcerymod.item;

import net.code7y7.sorcerymod.component.CrystalPouchContentsComponent;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.item.tooltip.TooltipData;

public record CrystalPouchTooltipData(CrystalPouchContentsComponent contents) implements TooltipData {
    public CrystalPouchTooltipData(CrystalPouchContentsComponent contents) {
        this.contents = contents;
    }

    public CrystalPouchContentsComponent contents() {
        return this.contents;
    }
}
