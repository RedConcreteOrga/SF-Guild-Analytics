package com.sf.guildanalytics.controller;

import com.sf.guildanalytics.dto.SnapshotDTO;
import com.sf.guildanalytics.service.SnapshotService;
import org.springframework.http.ResponseEntity;
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
    public SnapshotDTO createSnapshot(@RequestBody SnapshotDTO snapshotDTO) {
        return snapshotService.createSnapshot(snapshotDTO);
    }

    @PutMapping("/{id}")
    public SnapshotDTO updateSnapshot(@PathVariable UUID id, @RequestBody SnapshotDTO snapshotDTO) {
        return snapshotService.updateSnapshot(id, snapshotDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSnapshot(@PathVariable UUID id) {
        snapshotService.deleteSnapshot(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/player/{playerId}/analytics/level-history")
    public List<Integer> getLevelHistory(@PathVariable UUID playerId) {
        return snapshotService.getLevelHistory(playerId);
    }
}
