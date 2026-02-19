package com.sf.guildanalytics.service;

import com.sf.guildanalytics.dto.PlayerDTO;
import com.sf.guildanalytics.entity.Guild;
import com.sf.guildanalytics.entity.Player;
import com.sf.guildanalytics.repository.GuildRepository;
import com.sf.guildanalytics.repository.PlayerRepository;
import com.sf.guildanalytics.repository.PlayerSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;
    private final GuildRepository guildRepository;
    private final PlayerSnapshotRepository playerSnapshotRepository;

    public PlayerService(PlayerRepository playerRepository, GuildRepository guildRepository,
            PlayerSnapshotRepository playerSnapshotRepository) {
        this.playerRepository = playerRepository;
        this.guildRepository = guildRepository;
        this.playerSnapshotRepository = playerSnapshotRepository;
    }

    public List<PlayerDTO> getPlayersByGuild(UUID guildId) {
        return playerRepository.findByGuildId(guildId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PlayerDTO getPlayerById(UUID id) {
        return playerRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Player not found"));
    }

    @Transactional
    public PlayerDTO createPlayer(PlayerDTO playerDTO) {
        Guild guild = guildRepository.findById(playerDTO.getGuildId())
                .orElseThrow(() -> new RuntimeException("Guild not found"));

        Player player = new Player();
        player.setName(playerDTO.getName());
        player.setPlayerClass(playerDTO.getPlayerClass());
        player.setJoinDate(playerDTO.getJoinDate());
        player.setActive(playerDTO.isActive());
        player.setGuild(guild);

        return convertToDTO(playerRepository.save(player));
    }

    @Transactional
    public PlayerDTO updatePlayer(UUID id, PlayerDTO playerDTO) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Player not found"));

        player.setName(playerDTO.getName());
        player.setPlayerClass(playerDTO.getPlayerClass());
        player.setActive(playerDTO.isActive());
        // Custom logic: keep join date or update it? Keep for now.

        return convertToDTO(playerRepository.save(player));
    }

    @Transactional
    public void deletePlayer(UUID id) {
        playerRepository.deleteById(id);
    }

    private PlayerDTO convertToDTO(Player player) {
        PlayerDTO dto = new PlayerDTO();
        dto.setId(player.getId());
        dto.setName(player.getName());
        dto.setPlayerClass(player.getPlayerClass());
        dto.setJoinDate(player.getJoinDate());
        dto.setActive(player.isActive());
        dto.setGuildId(player.getGuild().getId());

        List<com.sf.guildanalytics.entity.PlayerSnapshot> snapshots = playerSnapshotRepository
                .findByPlayerIdOrderByTimestampDesc(player.getId());
        if (!snapshots.isEmpty()) {
            dto.setLevel(snapshots.get(0).getLevel());
        }

        return dto;
    }
}
