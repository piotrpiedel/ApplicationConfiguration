package com.piedel.piotr.configuration.service.domain.clientwithversion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientWithVersionRepository extends JpaRepository<ClientVersion, Long> {

    Optional<ClientVersion> findByClientAndVersion(String client, String version);
}
