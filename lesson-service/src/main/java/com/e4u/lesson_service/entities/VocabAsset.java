package com.e4u.lesson_service.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "vocab_assets")
public class VocabAsset extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID assetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "word_id", nullable = false)
    private UserVocabInstance wordInstance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AssetType assetType;

    @Column(nullable = false)
    private String url;

    private String altText;

    private Integer sortOrder;

    public enum AssetType {
        IMAGE,
        AUDIO,
        VIDEO,
        TEXT
    }
}
