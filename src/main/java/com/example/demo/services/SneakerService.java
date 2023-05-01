package com.example.demo.services;

import com.example.demo.models.Sneaker;
import com.example.demo.models.User;
import com.example.demo.repositories.SneakerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SneakerService {

    private final SneakerRepository repository;

    @Autowired
    public SneakerService(SneakerRepository repository) {
        this.repository = repository;
    }

    public Sneaker findById(Long id) {
        return repository.findById(id).orElse(null);
    }

    public List<Sneaker> findAll() {
        return repository.findAll();
    }

    @Transactional
    public void addSneaker(Sneaker sneaker) {
        repository.save(sneaker);
    }

    public void buySneaker(User user, Sneaker sneaker) {
        // TODO
    }

    @Transactional
    public void save(Sneaker sneaker) {
        repository.save(sneaker);
    }
}
