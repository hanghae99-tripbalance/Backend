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
    private int id;
    private String title;
    private String contents;
    private String url;
    private String blogName;
    private String thumbnail;
    public void setId(int id) {
        this.id = id;
    }
}
