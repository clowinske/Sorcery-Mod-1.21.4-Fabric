package componentTest;

import net.code7y7.sorcerymod.component.CrystalTier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

public class CrystalTierTest {

    @Test
    void WhenLevelIsMaxLevel_ReturnString() {
        CrystalTier crystalTier = new CrystalTier(CrystalTier.MAX_LEVEL);
        assertEquals("CrystalTier{level=" + CrystalTier.MAX_LEVEL + "}", crystalTier.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {CrystalTier.MAX_LEVEL, 0})
    void ValidLevels_Success(int level) {
        CrystalTier crystalTier = new CrystalTier(level);
        assertEquals(level, crystalTier.getValue());
    }

    @ParameterizedTest
    @ValueSource(ints = {CrystalTier.MAX_LEVEL + 1, -1})
    void InvalidLevels_ThrowIllegalArgumentException(int invalidLevel) {
        assertThrows(IllegalArgumentException.class, () -> new CrystalTier(invalidLevel));
    }
}