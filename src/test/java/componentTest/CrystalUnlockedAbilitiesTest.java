package componentTest;

import org.junit.jupiter.api.Test;
import net.code7y7.sorcerymod.component.CrystalUnlockedAbilities;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class CrystalUnlockedAbilitiesTest {
    @Test
    void validateSize_shouldReturnError_whenListSizeExceedsMaximumAllowed() {
        List<String> largeList = List.of("Ability1", "Ability2", "Ability3", "Ability4", "Ability5",
                "Ability6", "Ability7", "Ability8", "Ability9", "Ability10", "Ability11", "Ability12", "Ability13");
        try {
            new CrystalUnlockedAbilities(largeList);
            fail("Expected an exception to be thrown");
        } catch (IllegalArgumentException e) {
            assertEquals("List must not contain more than 12 strings.", e.getMessage());
        }
    }

   /* @Test
    void validateSize_shouldReturnSuccess_whenListSizeIsEqualToMaxAllowed() {
        List<String> list = List.of("ability1", "ability2", "ability3", "ability4", "ability5", "ability6", "ability7", "ability8", "ability9", "ability10", "ability11", "ability12");

        DataResult<List<String>> result = CrystalUnlockedAbilities.validateSize(list);

        assertEquals(DataResult.Status.SUCCESS, result.getStatus());
        assertEquals(list, result.getResult().orElse(null));
    }

    @Test
    void validateSize_shouldReturnSuccess_whenListSizeIsLessThanOrEqualToMaxSize() {
        List<String> testList = List.of("Ability1", "Ability2", "Ability3");
        DataResult<List<String>> result = CrystalUnlockedAbilities.validateSize(testList);

        assertEquals(DataResult.Status.SUCCESS, result.getStatus());
    }*/

}