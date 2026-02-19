package com.sf.guildanalytics.service;

import com.sf.guildanalytics.dto.GuildDTO;
import com.sf.guildanalytics.entity.Guild;
import com.sf.guildanalytics.entity.User;
import com.sf.guildanalytics.repository.GuildRepository;
import com.sf.guildanalytics.repository.PlayerRepository;
import com.sf.guildanalytics.repository.PlayerSnapshotRepository;
import com.sf.guildanalytics.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
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
    private final UserRepository userRepository;

    public GuildService(GuildRepository guildRepository, PlayerRepository playerRepository,
            PlayerSnapshotRepository playerSnapshotRepository, UserRepository userRepository) {
        this.guildRepository = guildRepository;
        this.playerRepository = playerRepository;
        this.playerSnapshotRepository = playerSnapshotRepository;
        this.userRepository = userRepository;
    }

    private User requireUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private void checkOwnership(Guild guild, User user) {
        boolean isAdmin = "ADMIN".equals(user.getRole());
        boolean isOwner = guild.getOwnerId() != null && guild.getOwnerId().equals(user.getId());
        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Du bist nicht der Besitzer dieser Gilde.");
        }
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
    public GuildDTO createGuild(GuildDTO guildDTO, String username) {
        User owner = requireUser(username);
        Guild guild = new Guild();
        guild.setName(guildDTO.getName());
        guild.setServer(guildDTO.getServer());
        guild.setFaction(guildDTO.getFaction());
        guild.setImageUrl(guildDTO.getImageUrl());
        guild.setOwnerId(owner.getId());
        return convertToDTO(guildRepository.save(guild));
    }

    @Transactional
    public GuildDTO updateGuild(UUID id, GuildDTO guildDTO, String username) {
        User currentUser = requireUser(username);
        Guild guild = guildRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guild not found"));
        checkOwnership(guild, currentUser);
        guild.setName(guildDTO.getName());
        guild.setServer(guildDTO.getServer());
        guild.setFaction(guildDTO.getFaction());
        guild.setImageUrl(guildDTO.getImageUrl());
        return convertToDTO(guildRepository.save(guild));
    }

    @Transactional
    public void deleteGuild(UUID id, String username) {
        User currentUser = requireUser(username);
        Guild guild = guildRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guild not found"));
        checkOwnership(guild, currentUser);
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
        dto.setOwnerId(guild.getOwnerId());

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
