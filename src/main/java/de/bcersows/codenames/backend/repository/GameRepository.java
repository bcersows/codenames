package de.bcersows.codenames.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import de.bcersows.codenames.backend.entity.Game;

public interface GameRepository extends JpaRepository<Game, Long> {
    @Query("SELECT g FROM Game g WHERE g.name = :name")
    Optional<Game> findByName(@Param("name") String name);

    @Query("SELECT g FROM Game g WHERE lower(g.name) like lower(concat('%', :searchTerm, '%'))")
    List<Game> search(@Param("searchTerm") String searchTerm);

    // game_words (game_id, words_id)
    @Transactional
    @Modifying
    // @Query("UPDATE GameField SET flipped = TRUE WHERE id = :gameFieldId AND Game_GameField.game_id = :gameId AND Game_GameField.words_id = :gameFieldId")
    @Query("UPDATE GameField SET flipped = TRUE WHERE id = :gameFieldId")
    void updateGameField(/* @Param("gameId") long gameId, */ @Param("gameFieldId") Long gameFieldId);
}
