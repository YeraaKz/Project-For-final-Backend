package com.example.demo.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "sneakers")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Sneaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "brand")
    private String brand;

    @Column(name = "imageURL")
    private String imageURL;

    @Column(name = "price")
    private double price;

    @ManyToMany(mappedBy = "sneakers")
    private List<User> users;

}
