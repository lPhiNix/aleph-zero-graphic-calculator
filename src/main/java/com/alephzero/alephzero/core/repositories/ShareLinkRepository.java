package com.alephzero.alephzero.core.repositories;

import com.alephzero.alephzero.core.models.ShareLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShareLinkRepository extends JpaRepository<ShareLink, Integer> {
}