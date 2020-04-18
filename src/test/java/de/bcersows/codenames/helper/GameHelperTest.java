package de.bcersows.codenames.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import de.bcersows.codenames.backend.entity.GameField;
import de.bcersows.codenames.backend.entity.PlayerTeam;

public class GameHelperTest {

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateGameField() throws Exception {
        final PlayerTeam startTeam = PlayerTeam.BLUE;
        final PlayerTeam otherTeam = PlayerTeam.RED;
        final List<String> words = Arrays.asList("slip", "bribery", "tuber", "loss", "ballet", "terracotta", "login", "rosemary", "car", "waterwheel", "album",
                "incidence", "mansion", "hermit", "debris", "killing", "sweatshop", "degradation", "yak", "partner", "desktop", "carrot", "fennel", "gaming",
                "stamina");
        assertEquals("Word count matches.", 25, words.size());

        // test one
        List<GameField> gameFields = GameHelper.createGameField(startTeam, words);

        assertNotNull("Created result.", gameFields);
        testGameField(startTeam, otherTeam, gameFields);

        // test two with other team
        gameFields = GameHelper.createGameField(otherTeam, words);

        assertNotNull("Created result.", gameFields);
        testGameField(otherTeam, startTeam, gameFields);
    }

    /**
     * Test the created field.
     */
    private void testGameField(final PlayerTeam startTeam, final PlayerTeam otherTeam, final List<GameField> gameFields) {
        final Map<PlayerTeam, Long> counts = gameFields.stream().filter(field -> null != field.getTeam()).map(GameField::getTeam)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        assertEquals("Count for start team matches.", 9, counts.get(startTeam).intValue());
        assertEquals("Count for other team matches.", 8, counts.get(otherTeam).intValue());

        assertEquals("There's only one killer field.", 1, gameFields.stream().filter(field -> null == field.getTeam() && field.isKiller()).count());
    }

}
