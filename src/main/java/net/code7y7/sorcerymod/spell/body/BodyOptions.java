package net.code7y7.sorcerymod.spell.body;

import net.code7y7.sorcerymod.spell.CrystalOptions;
import net.code7y7.sorcerymod.util.crystal.CrystalData;

public class BodyOptions extends CrystalOptions {
    AdrenalineRushOptions adrenalineRushOptions;
    BodyMasteryOptions bodyMasteryOptions;
    BloodSapOptions bloodSapOptions;
    BloodSapMasteryOptions bloodSapMasteryOptions;
    VitalitySurplusOptions vitalitySurplusOptions;
    SanguineBondOptions sanguineBondOptions;
    IroncladMetabolismOptions ironcladMetabolismOptions;
    public BodyOptions() {
        super(CrystalData.BODY);
        adrenalineRushOptions = new AdrenalineRushOptions();
        spellOptions.put("adrenaline_rush", adrenalineRushOptions);
        bodyMasteryOptions = new BodyMasteryOptions();
        spellOptions.put("body_mastery", bodyMasteryOptions);
        bloodSapOptions = new BloodSapOptions();
        spellOptions.put("blood_sap", bloodSapOptions);
        bloodSapMasteryOptions = new BloodSapMasteryOptions();
        spellOptions.put("blood_sap_mastery", bloodSapMasteryOptions);
        vitalitySurplusOptions = new VitalitySurplusOptions();
        spellOptions.put("vitality_surplus", vitalitySurplusOptions);
        sanguineBondOptions = new SanguineBondOptions();
        spellOptions.put("sanguine_bond", sanguineBondOptions);
        ironcladMetabolismOptions = new IroncladMetabolismOptions();
        spellOptions.put("ironclad_metabolism", ironcladMetabolismOptions);
    }
}
