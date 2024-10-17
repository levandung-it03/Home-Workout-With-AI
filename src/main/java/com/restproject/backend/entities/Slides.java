package com.restproject.backend.entities;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "slides")
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Slides {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "image_public_id")
    String imagePublicId;

    @Column(name = "image_url", nullable = false)
    String imageUrl;
}
