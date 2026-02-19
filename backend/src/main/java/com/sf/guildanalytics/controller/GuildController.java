package com.sf.guildanalytics.controller;

import com.sf.guildanalytics.dto.GuildDTO;
import com.sf.guildanalytics.service.GuildService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/guilds")
public class GuildController {

    private final GuildService guildService;

    public GuildController(GuildService guildService) {
        this.guildService = guildService;
    }

    @GetMapping
    public List<GuildDTO> getAllGuilds() {
        return guildService.getAllGuilds();
    }

    @GetMapping("/{id}")
    public GuildDTO getGuildById(@PathVariable UUID id) {
        return guildService.getGuildById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GUILD_LEAD')")
    public GuildDTO createGuild(@RequestBody GuildDTO guildDTO) {
        return guildService.createGuild(guildDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUILD_LEAD')")
    public GuildDTO updateGuild(@PathVariable UUID id, @RequestBody GuildDTO guildDTO) {
        return guildService.updateGuild(id, guildDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteGuild(@PathVariable UUID id) {
        guildService.deleteGuild(id);
        return ResponseEntity.ok().build();
    }
}
