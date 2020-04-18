package de.bcersows.codenames.backend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;

import de.bcersows.codenames.backend.entity.Game;
import de.bcersows.codenames.backend.entity.GameField;
import de.bcersows.codenames.backend.entity.Player;
import de.bcersows.codenames.backend.entity.PlayerTeam;
import de.bcersows.codenames.backend.repository.GameRepository;
import de.bcersows.codenames.exception.InvalidWordsException;
import de.bcersows.codenames.helper.GameHelper;

@Service
public class GameService {
    private static final Logger LOG = LoggerFactory.getLogger(GameService.class);

    /** Repository to access the stored games. **/
    private final GameRepository gameRepository;

    @Autowired
    public GameService(final GameRepository gameRepository) {
        this.gameRepository = gameRepository;

    }

    public Optional<Game> find(@NonNull final String name) {
        return this.gameRepository.findByName(name);
    }

    public List<Game> findAll() {
        return gameRepository.findAll();
    }

    public List<Game> findAll(final String filterText) {
        if (filterText == null || filterText.isEmpty()) {
            return gameRepository.findAll();
        } else {
            return gameRepository.search(filterText);
        }
    }

    public long count() {
        return gameRepository.count();
    }

    public long countPlayers() {
        return this.gameRepository.findAll().stream().mapToLong(game -> game.getPlayers().size()).sum();
    }

    public void delete(@NonNull final Game game) {
        gameRepository.delete(game);
    }

    /**
     * Create a game with the given name, if it does not exists. Returns if successfully created.
     * 
     * @throws InvalidWordsException
     *             if the words could not be loaded
     **/
    public boolean create(@NonNull final String gameName, @NonNull final String languageName) throws InvalidWordsException {
        if (
        // if the name is valid
        isGameNameValid(gameName)
                // and there is no game by that name already
                && !this.gameRepository.findByName(gameName).isPresent()) {
            // create the game
            // TODO codenames: improve? https://www.baeldung.com/spring-load-resource-as-string
            final String resourceName = "words/nouns_" + languageName + ".txt";
            final ResourceLoader resourceLoader = new DefaultResourceLoader();
            final Resource resource = resourceLoader.getResource("classpath:" + resourceName);

            LOG.warn("File name: {}, resource file: {}", resourceName, resource);
            try (Stream<String> stream = Files.lines(Paths.get(resource.getFile().getPath()))) {
                final ArrayList<String> words = stream.collect(Collectors.toCollection(ArrayList::new));
                Collections.shuffle(words);

                final List<String> filteredWords = words.subList(0, 25);

                LOG.debug("Words ({}): {}.", filteredWords.size(), filteredWords);
                // TODO codenames store words in game
                final List<GameField> fields = GameHelper.createGameField(PlayerTeam.BLUE, filteredWords);

                final Game newGame = new Game(gameName, PlayerTeam.BLUE, fields);

                // and store it
                this.gameRepository.save(newGame);

                return true;
            } catch (final NullPointerException | IOException e) {
                throw new InvalidWordsException("Could not load the words.", e);
            }
        }

        return false;
    }

    public void save(@Nullable final Game game) {
        if (game == null) {
            LOG.error("Game is null. Are you sure you have connected your form to the application?");
            return;
        }
        this.gameRepository.save(game);
    }

    /**
     * Update the game field of the given game.
     * 
     * @param game
     * @param gameField
     */
    public void updateGameField(final long gameId, @NonNull final GameField gameField) {
        this.gameRepository.updateGameField(/* gameId, */gameField.getId());
    }

    /**
     * Update the current turn value for the given game.
     * 
     * @param gameId
     * @return
     */
    public PlayerTeam updateGameTurn(final long gameId) {
        final AtomicReference<PlayerTeam> playerTeam = new AtomicReference<>(PlayerTeam.BLUE);
        this.gameRepository.findById(gameId).ifPresent(game -> {
            game.setCurrentTeam(game.getCurrentTeam().nextTeam());
            final PlayerTeam nextTeam = this.gameRepository.save(game).getCurrentTeam();
            playerTeam.set(nextTeam);
        });

        /// TODO codenames do not just return blue
        return playerTeam.get();
    }

    /** Populate data for testing. **/
    @PostConstruct
    public void populateTestData() {
        if (gameRepository.count() == 0) {
            final Random r = new Random(0);
            gameRepository.saveAll(Stream.of("opphers", "frankensteins", "work").map(gameName -> {
                final Game game = new Game(gameName, PlayerTeam.BLUE,
                        GameHelper.createGameField(PlayerTeam.BLUE,
                                Arrays.asList("slip", "bribery", "tuber", "loss", "ballet", "terracotta", "login", "rosemary", "car", "waterwheel", "album",
                                        "incidence", "mansion", "hermit", "debris", "killing", "sweatshop", "degradation", "yak", "partner", "desktop",
                                        "carrot", "fennel", "gaming", "stamina")));

                final int amountOfPlayers = r.nextInt(6);
                final List<Player> players = new ArrayList<>(amountOfPlayers);

                for (int i = 0; i < amountOfPlayers; i++) {
                    final PlayerTeam playerTeam = r.nextBoolean() ? PlayerTeam.RED : PlayerTeam.BLUE;
                    players.add(new Player("player" + i, playerTeam));
                }

                game.setPlayers(players);

                return game;
            }).collect(Collectors.toList()));
        }
    }

    /** If the given game name is valid. **/
    public static boolean isGameNameValid(@Nullable final String gameName) {
        return null != gameName && gameName.matches("\\w{5,20}");
    }
}
