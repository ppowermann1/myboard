package com.board.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "boards")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board {

    @Id
    @Column(length = 50)
    private String id; // e.g. 'free', 'anonymous', 'jobs'

    @Column(nullable = false, length = 100)
    private String name; // e.g. '자유게시판', '익명게시판', '구인구직'

    @Column(length = 255)
    private String description;
}
