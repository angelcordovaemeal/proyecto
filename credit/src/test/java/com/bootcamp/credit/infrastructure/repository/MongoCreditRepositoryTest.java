package com.bootcamp.credit.infrastructure.repository;

import com.bootcamp.credit.domain.model.Credit;
import com.bootcamp.credit.domain.model.PersonalCredit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MongoCreditRepositoryTest {

    @Mock
    private SpringDataCreditRepository springDataRepository;

    @InjectMocks
    private MongoCreditRepository repository;

    private Credit credit;

    @BeforeEach
    void setup() {
        credit = new PersonalCredit();
        credit.setId("c1");
        credit.setCustomerId("cust1");
    }

    @Test
    void shouldSaveCredit() {

        when(springDataRepository.save(credit))
                .thenReturn(credit);

        repository.save(credit)
                .test()
                .assertComplete()
                .assertValue(saved -> saved.getId().equals("c1"));
    }

    @Test
    void shouldFindCreditByIdWhenExists() {

        when(springDataRepository.findById("c1"))
                .thenReturn(Optional.of(credit));

        repository.findById("c1")
                .test()
                .assertComplete()
                .assertValue(found -> found.getId().equals("c1"));
    }

    @Test
    void shouldReturnEmptyWhenCreditNotFoundById() {

        when(springDataRepository.findById("x"))
                .thenReturn(Optional.empty());

        repository.findById("x")
                .test()
                .assertComplete()
                .assertNoValues();
    }

    @Test
    void shouldFindAllCredits() {

        when(springDataRepository.findAll())
                .thenReturn(List.of(credit));

        repository.findAll()
                .toList()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1);
    }

    @Test
    void shouldFindCreditsByCustomerId() {

        when(springDataRepository.findByCustomerId("cust1"))
                .thenReturn(List.of(credit));

        repository.findByCustomerId("cust1")
                .toList()
                .test()
                .assertComplete()
                .assertValue(list -> list.size() == 1)
                .assertValue(list -> list.get(0).getCustomerId().equals("cust1"));
    }

    @Test
    void shouldDeleteCreditById() {

        doNothing().when(springDataRepository).deleteById("c1");

        repository.deleteById("c1")
                .test()
                .assertComplete();

        verify(springDataRepository, times(1)).deleteById("c1");
    }
}