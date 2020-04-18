package de.bcersows.codenames.helper;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.lang.NonNull;

import de.bcersows.codenames.backend.entity.GameField;
import de.bcersows.codenames.backend.entity.PlayerTeam;

/**
 * @author bcersows
 */
public final class GameHelper {
    public static final int FIELD_AMOUNT_START_TEAM = 9;
    public static final int FIELD_AMOUNT_OTHER_TEAM = 8;

    private GameHelper() {
        // static class
    }

    @NonNull
    public static List<GameField> createGameField(@NonNull final PlayerTeam startTeam, @NonNull final List<String> words) {
        final PlayerTeam otherTeam = PlayerTeam.BLUE == startTeam ? PlayerTeam.RED : PlayerTeam.BLUE;

        final Random random = new Random();

        final List<Integer> fieldNumbers = IntStream.range(0, words.size()).boxed().collect(Collectors.toList());

        final int killer = random.nextInt(words.size());
        fieldNumbers.remove((Integer) killer);

        // shuffle before getting the start team fields
        Collections.shuffle(fieldNumbers, random);
        final Set<Integer> startTeamFields = fieldNumbers.stream().limit(FIELD_AMOUNT_START_TEAM).collect(Collectors.toSet());
        fieldNumbers.removeAll(startTeamFields);

        // shuffle before getting the start team fields
        Collections.shuffle(fieldNumbers, random);
        final Set<Integer> otherTeamFields = fieldNumbers.stream().limit(FIELD_AMOUNT_OTHER_TEAM).collect(Collectors.toSet());

        // create the fields
        final List<GameField> gameFields = words.stream().map(GameField::new).collect(Collectors.toList());
        // set the killer and the teams
        gameFields.get(killer).setKiller(true);
        startTeamFields.stream().map(gameFields::get).forEach(gameField -> gameField.setTeam(startTeam));
        otherTeamFields.stream().map(gameFields::get).forEach(gameField -> gameField.setTeam(otherTeam));

        return gameFields;
    }
}
