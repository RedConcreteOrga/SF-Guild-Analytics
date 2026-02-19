package com.sf.guildanalytics.controller;

import com.sf.guildanalytics.dto.PlayerDTO;
import com.sf.guildanalytics.service.PlayerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    @GetMapping("/guild/{guildId}")
    public List<PlayerDTO> getPlayersByGuild(@PathVariable UUID guildId) {
        return playerService.getPlayersByGuild(guildId);
    }

    @GetMapping("/{id}")
    public PlayerDTO getPlayerById(@PathVariable UUID id) {
        return playerService.getPlayerById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GUILD_LEAD')")
    public PlayerDTO createPlayer(@RequestBody PlayerDTO playerDTO) {
        return playerService.createPlayer(playerDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUILD_LEAD')")
    public PlayerDTO updatePlayer(@PathVariable UUID id, @RequestBody PlayerDTO playerDTO) {
        return playerService.updatePlayer(id, playerDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'GUILD_LEAD')")
    public ResponseEntity<?> deletePlayer(@PathVariable UUID id) {
        playerService.deletePlayer(id);
        return ResponseEntity.ok().build();
    }
}
