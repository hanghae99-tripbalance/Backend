package com.move.TripBalance.result;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Blog {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;
    private String title;
    private String contents;
    private String url;
    private String blogName;
    private String thumbnail;
    public void setId(Long id) {
        this.id = id;
    }
}