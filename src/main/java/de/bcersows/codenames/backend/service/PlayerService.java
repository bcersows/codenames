package de.bcersows.codenames.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import de.bcersows.codenames.backend.entity.Player;
import de.bcersows.codenames.backend.repository.PlayerRepository;

@Service
public class PlayerService {
    private final PlayerRepository playerRepository;

    public PlayerService(final PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public List<Player> findAll() {
        return playerRepository.findAll();
    }

}
