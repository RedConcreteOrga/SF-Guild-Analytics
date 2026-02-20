package com.sf.guildanalytics.repository;

import com.sf.guildanalytics.entity.PlayerSnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PlayerSnapshotRepository extends JpaRepository<PlayerSnapshot, UUID> {
    List<PlayerSnapshot> findByPlayerIdOrderByTimestampDesc(UUID playerId);
}
