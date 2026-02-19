package com.sf.guildanalytics.repository;

import com.sf.guildanalytics.entity.Guild;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GuildRepository extends JpaRepository<Guild, UUID> {
}
