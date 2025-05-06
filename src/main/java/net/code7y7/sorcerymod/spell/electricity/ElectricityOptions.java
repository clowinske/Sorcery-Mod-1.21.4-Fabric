package net.code7y7.sorcerymod.spell.electricity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.code7y7.sorcerymod.spell.AbilityOptions;
import net.code7y7.sorcerymod.spell.CrystalOptions;
import net.code7y7.sorcerymod.util.crystal.CrystalData;


import java.util.Map;

public class ElectricityOptions extends CrystalOptions {

    BlinkOptions blinkOptions;
    LightningOptions lightningOptions;
    LightningBoltOptions lightningBoltOptions;
    ElectricImbueOptions electricImbueOptions;
    ChainLightningBoltOptions chainLightningBoltOptions;
    ChainLightningOptions chainLightningOptions;

    public ElectricityOptions(){
        super(CrystalData.ELECTRICITY);
        this.blinkOptions = new BlinkOptions();
        spellOptions.put("blink", blinkOptions);
        this.lightningOptions = new LightningOptions();
        spellOptions.put("lightning", lightningOptions);
        this.lightningBoltOptions = new LightningBoltOptions();
        spellOptions.put("lightning_bolt", lightningBoltOptions);
        this.electricImbueOptions = new ElectricImbueOptions();
        spellOptions.put("electric_imbue", electricImbueOptions);
        this.chainLightningBoltOptions = new ChainLightningBoltOptions();
        spellOptions.put("chain_lightning_bolt", chainLightningBoltOptions);
        this.chainLightningOptions = new ChainLightningOptions();
        spellOptions.put("chain_lightning", chainLightningOptions);
    }

    public BlinkOptions getBlinkOptions() {
        return blinkOptions;
    }
    public ChainLightningBoltOptions getChainLightningBoltOptions() {
        return chainLightningBoltOptions;
    }
    public ChainLightningOptions getChainLightningOptions() {
        return chainLightningOptions;
    }
}
