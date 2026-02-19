package com.sf.guildanalytics.controller;

import com.sf.guildanalytics.dto.SnapshotDTO;
import com.sf.guildanalytics.service.SnapshotService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/snapshots")
public class SnapshotController {

    private final SnapshotService snapshotService;

    public SnapshotController(SnapshotService snapshotService) {
        this.snapshotService = snapshotService;
    }

    @GetMapping("/player/{playerId}")
    public List<SnapshotDTO> getSnapshotsByPlayer(@PathVariable UUID playerId) {
        return snapshotService.getSnapshotsByPlayer(playerId);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'GUILD_LEAD')")
    public SnapshotDTO createSnapshot(@RequestBody SnapshotDTO snapshotDTO) {
        return snapshotService.createSnapshot(snapshotDTO);
    }

    @GetMapping("/player/{playerId}/analytics/level-history")
    public List<Integer> getLevelHistory(@PathVariable UUID playerId) {
        return snapshotService.getLevelHistory(playerId);
    }
}
