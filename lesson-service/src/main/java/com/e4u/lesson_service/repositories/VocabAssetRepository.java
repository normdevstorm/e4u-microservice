package com.e4u.lesson_service.repositories;

import com.e4u.lesson_service.entities.VocabAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VocabAssetRepository extends JpaRepository<VocabAsset, UUID> {
    List<VocabAsset> findByWordInstanceId(UUID wordInstanceId);
    List<VocabAsset> findByWordInstanceIdOrderBySortOrderAsc(UUID wordInstanceId);
    List<VocabAsset> findByAssetType(VocabAsset.AssetType assetType);
    List<VocabAsset> findByWordInstanceIdAndAssetType(UUID wordInstanceId, VocabAsset.AssetType assetType);
}
