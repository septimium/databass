package com.databass.coreservice.repository;

import com.databass.coreservice.model.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SongRepository extends JpaRepository<Song, Long> {
    List<Song> findAllByOrderByCreatedAtDesc();

    List<Song> findByUserUsernameOrderByCreatedAtDesc(String username);

    List<Song> findByUserUsernameAndIsPublicTrueOrderByCreatedAtDesc(String username);
}
