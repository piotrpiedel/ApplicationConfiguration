package com.piedel.piotr.configuration.service.domain.clientwithversion;

import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Supplier;

@Service
public class ClientWithVersionService {

    private final ClientWithVersionRepository clientWithVersionRepository;

    public ClientWithVersionService(ClientWithVersionRepository clientWithVersionRepository) {
        this.clientWithVersionRepository = clientWithVersionRepository;
    }

    public Optional<ClientVersion> findClientWithVersion(String client, String version) {
        return clientWithVersionRepository
                .findByClientAndVersion(client, version);
    }

    public ClientVersion findOrCreate(String client, String version) {
        Optional<ClientVersion> clientVersion = clientWithVersionRepository
                .findByClientAndVersion(client, version);

        return clientVersion
                .orElseGet(saveClientWithVersion(client, version));
    }

    private Supplier<ClientVersion> saveClientWithVersion(String client, String version) {
        return () -> saveClientWithVersion(ClientVersion.builder()
                .client(client)
                .version(version)
                .build());
    }

    public ClientVersion saveClientWithVersion(ClientVersion clientVersion) {
        return clientWithVersionRepository.save(clientVersion);
    }
}
