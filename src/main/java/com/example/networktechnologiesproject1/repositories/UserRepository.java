package com.example.networktechnologiesproject1.repositories;

import com.example.networktechnologiesproject1.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
}
