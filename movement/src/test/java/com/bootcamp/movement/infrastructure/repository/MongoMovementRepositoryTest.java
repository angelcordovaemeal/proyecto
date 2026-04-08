package com.bootcamp.movement.infrastructure.repository;

import com.bootcamp.movement.domain.model.Movement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoMovementRepositoryTest {

    @Mock
    private SpringDataMovementRepository springDataRepository;

    @InjectMocks
    private MongoMovementRepository repository;

    private Movement movement;

    @BeforeEach
    void setup() {
        movement = new Movement();
        movement.setId("m1");
        movement.setProductId("p1");
        movement.setAmount(100.0);
    }

    @Test
    void shouldSaveMovementAndSetCreatedAt() {

        when(springDataRepository.save(any(Movement.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        repository.save(movement)
                .test()
                .assertComplete()
                .assertValue(saved -> saved.getCreatedAt() != null);
    }

    @Test
    void shouldFindMovementsByProductId() {

        when(springDataRepository.findByProductId("p1"))
                .thenReturn(List.of(movement));

        repository.findByProductId("p1")
                .toList()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1)
                .assertValue(list -> list.get(0).getProductId().equals("p1"));
    }

    @Test
    void shouldFindAllMovements() {

        when(springDataRepository.findAll())
                .thenReturn(List.of(movement));

        repository.findAll()
                .toList()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1);
    }

    @Test
    void shouldDeleteMovementById() {

        doNothing().when(springDataRepository).deleteById("m1");

        repository.deleteById("m1")
                .test()
                .assertComplete();

        verify(springDataRepository, times(1)).deleteById("m1");
    }
}