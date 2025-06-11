package com.alephzero.alephzero.db.repositories;

import com.alephzero.alephzero.db.models.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareLinkRepository extends JpaRepository<ShareLink, Integer> {
}