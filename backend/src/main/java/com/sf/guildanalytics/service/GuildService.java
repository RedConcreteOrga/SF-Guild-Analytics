package com.sf.guildanalytics.service;

import com.sf.guildanalytics.dto.GuildDTO;
import com.sf.guildanalytics.entity.Guild;
import com.sf.guildanalytics.repository.GuildRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GuildService {

    private final GuildRepository guildRepository;

    public GuildService(GuildRepository guildRepository) {
        this.guildRepository = guildRepository;
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
        return convertToDTO(guildRepository.save(guild));
    }

    @Transactional
    public GuildDTO updateGuild(UUID id, GuildDTO guildDTO) {
        Guild guild = guildRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Guild not found"));
        guild.setName(guildDTO.getName());
        guild.setServer(guildDTO.getServer());
        guild.setFaction(guildDTO.getFaction());
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
        return dto;
    }
}
