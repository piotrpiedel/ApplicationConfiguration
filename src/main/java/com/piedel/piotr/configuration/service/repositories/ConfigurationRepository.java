package com.piedel.piotr.configuration.service.repositories;

import com.piedel.piotr.configuration.service.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public interface ConfigurationRepository extends JpaRepository<Configuration, Long> {

    List<Configuration> findAllByClientVersionId(long clientId);

    // select configuration which is newer than given acquired configuration (given Etag)
    @Query("select conf from Configuration conf where conf.creationDate > :creation_date and conf.clientVersion.id = :client_id")
    List<Configuration> findAllByClientVersionIdAfterGivenAcquisition(@Param("client_id") long clientId,
                                                                      @Param("creation_date") Timestamp lastAcquiredConfigurationETag);
}
