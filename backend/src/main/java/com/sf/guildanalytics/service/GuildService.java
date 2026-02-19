package com.sf.guildanalytics.service;

import com.sf.guildanalytics.dto.GuildDTO;
import com.sf.guildanalytics.entity.Guild;
import com.sf.guildanalytics.repository.GuildRepository;
import com.sf.guildanalytics.repository.PlayerRepository;
import com.sf.guildanalytics.repository.PlayerSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GuildService {

    private final GuildRepository guildRepository;
    private final PlayerRepository playerRepository;
    private final PlayerSnapshotRepository playerSnapshotRepository;

    public GuildService(GuildRepository guildRepository, PlayerRepository playerRepository,
            PlayerSnapshotRepository playerSnapshotRepository) {
        this.guildRepository = guildRepository;
        this.playerRepository = playerRepository;
        this.playerSnapshotRepository = playerSnapshotRepository;
    }

    public List<GuildDTO> getAllGuilds() {
        return guildRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public GuildDTO getGuildById(UUID id) {
        return guildRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Guild not found"));
    }

    @Transactional
    public GuildDTO createGuild(GuildDTO guildDTO) {
        Guild guild = new Guild();
        guild.setName(guildDTO.getName());
        guild.setServer(guildDTO.getServer());
        guild.setFaction(guildDTO.getFaction());
        guild.setImageUrl(guildDTO.getImageUrl());
        return convertToDTO(guildRepository.save(guild));
    }

    @Transactional
    public GuildDTO updateGuild(UUID id, GuildDTO guildDTO) {
        Guild guild = guildRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guild not found"));
        guild.setName(guildDTO.getName());
        guild.setServer(guildDTO.getServer());
        guild.setFaction(guildDTO.getFaction());
        guild.setImageUrl(guildDTO.getImageUrl());
        return convertToDTO(guildRepository.save(guild));
    }

    @Transactional
    public void deleteGuild(UUID id) {
        guildRepository.deleteById(id);
    }

    private GuildDTO convertToDTO(Guild guild) {
        GuildDTO dto = new GuildDTO();
        dto.setId(guild.getId());
        dto.setName(guild.getName());
        dto.setServer(guild.getServer());
        dto.setFaction(guild.getFaction());
        dto.setCreatedAt(guild.getCreatedAt());
        dto.setImageUrl(guild.getImageUrl());

        List<com.sf.guildanalytics.entity.Player> players = playerRepository.findByGuildId(guild.getId());
        dto.setPlayerCount(players.size());

        if (!players.isEmpty()) {
            double totalLevel = 0;
            int count = 0;
            for (com.sf.guildanalytics.entity.Player player : players) {
                List<com.sf.guildanalytics.entity.PlayerSnapshot> snapshots = playerSnapshotRepository
                        .findByPlayerIdOrderByTimestampDesc(player.getId());
                if (!snapshots.isEmpty()) {
                    totalLevel += snapshots.get(0).getLevel();
                    count++;
                }
            }
            if (count > 0) {
                dto.setAvgLevel(Math.round((totalLevel / count) * 10.0) / 10.0);
            }
        }

        return dto;
    }
}
