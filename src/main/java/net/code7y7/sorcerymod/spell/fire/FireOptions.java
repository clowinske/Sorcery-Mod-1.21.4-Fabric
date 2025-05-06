package net.code7y7.sorcerymod.spell.fire;

import net.code7y7.sorcerymod.spell.CrystalOptions;
import net.code7y7.sorcerymod.util.crystal.CrystalData;

public class FireOptions extends CrystalOptions {
    FireBurstOptions fireSprayOptions;
    FireBallOptions fireBallOptions;
    FireballRicochetOptions fireballRicochetOptions;
    FlamingImbueOptions flamingImbueOptions;
    ScorchOptions scorchOptions;
    public FireOptions() {
        super(CrystalData.FIRE);
        fireSprayOptions = new FireBurstOptions();
        spellOptions.put("fire_burst", fireSprayOptions);
        fireBallOptions = new FireBallOptions();
        spellOptions.put("fireball", fireBallOptions);
        fireballRicochetOptions = new FireballRicochetOptions();
        spellOptions.put("fireball_ricochet", fireballRicochetOptions);
        flamingImbueOptions = new FlamingImbueOptions();
        spellOptions.put("flaming_imbue", flamingImbueOptions);
        scorchOptions = new ScorchOptions();
        spellOptions.put("scorch", scorchOptions);
    }
}
