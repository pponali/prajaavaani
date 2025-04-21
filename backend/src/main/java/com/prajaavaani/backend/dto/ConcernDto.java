package com.prajaavaani.backend.dto;

import com.prajaavaani.backend.model.GeographicLevel;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class ConcernDto {
    private UUID id;
    private UUID authorId; // Can be null
    private String authorName; // Can be null
    private String text;
    private int upvotes;
    private int downvotes;
    private int netVotes; // Calculated field
    private GeographicLevel geographicLevel;
    private String locationIdentifier;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Add a constructor or static factory method for easy mapping from Entity if needed
}
