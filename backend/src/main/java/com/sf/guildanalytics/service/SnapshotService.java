package com.sf.guildanalytics.service;

import com.sf.guildanalytics.dto.SnapshotDTO;
import com.sf.guildanalytics.entity.Player;
import com.sf.guildanalytics.entity.PlayerSnapshot;
import com.sf.guildanalytics.repository.PlayerRepository;
import com.sf.guildanalytics.repository.PlayerSnapshotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import jakarta.persistence.EntityNotFoundException;

@Service
public class SnapshotService {

    private final PlayerSnapshotRepository snapshotRepository;
    private final PlayerRepository playerRepository;

    public SnapshotService(PlayerSnapshotRepository snapshotRepository, PlayerRepository playerRepository) {
        this.snapshotRepository = snapshotRepository;
        this.playerRepository = playerRepository;
    }

    public List<SnapshotDTO> getSnapshotsByPlayer(UUID playerId) {
        return snapshotRepository.findByPlayerIdOrderByTimestampDesc(playerId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public SnapshotDTO createSnapshot(SnapshotDTO snapshotDTO) {
        Player player = playerRepository.findById(snapshotDTO.getPlayerId())
                .orElseThrow(() -> new EntityNotFoundException("Player not found"));

        PlayerSnapshot snapshot = new PlayerSnapshot();
        snapshot.setPlayer(player);
        if (snapshotDTO.getTimestamp() != null) {
            snapshot.setTimestamp(snapshotDTO.getTimestamp());
        }
        snapshot.setLevel(snapshotDTO.getLevel());
        snapshot.setStrength(snapshotDTO.getStrength());
        snapshot.setDexterity(snapshotDTO.getDexterity());
        snapshot.setIntelligence(snapshotDTO.getIntelligence());
        snapshot.setConstitution(snapshotDTO.getConstitution());
        snapshot.setHp(snapshotDTO.getHp());
        snapshot.setHonor(snapshotDTO.getHonor());
        snapshot.setDungeonProgress(snapshotDTO.getDungeonProgress());
        snapshot.setFortressLevel(snapshotDTO.getFortressLevel());

        return convertToDTO(snapshotRepository.save(snapshot));
    }

    @Transactional
    public SnapshotDTO updateSnapshot(UUID id, SnapshotDTO snapshotDTO) {
        PlayerSnapshot snapshot = snapshotRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Snapshot not found"));

        if (snapshotDTO.getTimestamp() != null) {
            snapshot.setTimestamp(snapshotDTO.getTimestamp());
        }
        snapshot.setLevel(snapshotDTO.getLevel());
        snapshot.setStrength(snapshotDTO.getStrength());
        snapshot.setDexterity(snapshotDTO.getDexterity());
        snapshot.setIntelligence(snapshotDTO.getIntelligence());
        snapshot.setConstitution(snapshotDTO.getConstitution());
        snapshot.setHp(snapshotDTO.getHp());
        snapshot.setHonor(snapshotDTO.getHonor());
        snapshot.setDungeonProgress(snapshotDTO.getDungeonProgress());
        snapshot.setFortressLevel(snapshotDTO.getFortressLevel());

        return convertToDTO(snapshotRepository.save(snapshot));
    }

    @Transactional
    public void deleteSnapshot(UUID id) {
        if (!snapshotRepository.existsById(id)) {
            throw new EntityNotFoundException("Snapshot not found");
        }
        snapshotRepository.deleteById(id);
    }

    // Example Analytics: Get level progression
    public List<Integer> getLevelHistory(UUID playerId) {
        return snapshotRepository.findByPlayerIdOrderByTimestampDesc(playerId).stream()
                .map(PlayerSnapshot::getLevel)
                .collect(Collectors.toList());
    }

    private SnapshotDTO convertToDTO(PlayerSnapshot snapshot) {
        SnapshotDTO dto = new SnapshotDTO();
        dto.setId(snapshot.getId());
        dto.setPlayerId(snapshot.getPlayer().getId());
        dto.setTimestamp(snapshot.getTimestamp());
        dto.setLevel(snapshot.getLevel());
        dto.setStrength(snapshot.getStrength());
        dto.setDexterity(snapshot.getDexterity());
        dto.setIntelligence(snapshot.getIntelligence());
        dto.setConstitution(snapshot.getConstitution());
        dto.setHp(snapshot.getHp());
        dto.setHonor(snapshot.getHonor());
        dto.setDungeonProgress(snapshot.getDungeonProgress());
        dto.setFortressLevel(snapshot.getFortressLevel());
        return dto;
    }
}
