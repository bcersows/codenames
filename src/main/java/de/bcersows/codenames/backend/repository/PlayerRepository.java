package de.bcersows.codenames.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.bcersows.codenames.backend.entity.Player;

public interface PlayerRepository extends JpaRepository<Player, Long> {
}
