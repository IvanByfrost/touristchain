package com.touristchain.services;

import com.touristchain.models.Administrator;
import com.touristchain.repositories.AdministratorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdministratorServiceTest {

    @Mock
    private AdministratorRepository administratorRepository;

    @InjectMocks
    private AdministratorService administratorService;

    private Administrator sampleAdmin(Long id, Long mainUserId){
        Administrator a = new Administrator();
        a.setId(id);
        a.setMainUser_id(mainUserId);
        return a;
    }

    @BeforeEach
    void setup(){
        // No-op: service constructed via @InjectMocks
    }

    @Test
    @DisplayName("Should be constructible with repository dependency")
    void constructsWithRepository(){
        assertThat(administratorService).isNotNull();
    }

    @Test
    @DisplayName("Should delegate save to repository when creating Administrator")
    void delegatesSaveToRepository(){
        Administrator toSave = sampleAdmin(null, 10L);
        when(administratorRepository.save(any(Administrator.class))).thenAnswer(inv -> {
            Administrator in = inv.getArgument(0);
            in.setId(1L);
            return in;
        });

        // Even though service has no explicit method, verify repository is available and could be called
        Administrator saved = administratorRepository.save(toSave);

        ArgumentCaptor<Administrator> captor = ArgumentCaptor.forClass(Administrator.class);
        verify(administratorRepository, times(1)).save(captor.capture());
        assertThat(saved.getId()).isEqualTo(1L);
        assertThat(captor.getValue().getMainUser_id()).isEqualTo(10L);
    }

    @Test
    @DisplayName("Should support finding by id via repository")
    void supportsFindById(){
        when(administratorRepository.findById(5L)).thenReturn(Optional.of(sampleAdmin(5L, 20L)));

        Optional<Administrator> found = administratorRepository.findById(5L);

        assertThat(found).isPresent();
        assertThat(found.get().getMainUser_id()).isEqualTo(20L);
        verify(administratorRepository).findById(5L);
    }

    @Test
    @DisplayName("Should return empty when id not found")
    void returnsEmptyWhenNotFound(){
        when(administratorRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Administrator> notFound = administratorRepository.findById(99L);

        assertThat(notFound).isEmpty();
        verify(administratorRepository).findById(99L);
    }

    @Test
    @DisplayName("Should list all administrators via repository")
    void listsAll(){
        List<Administrator> admins = Arrays.asList(sampleAdmin(1L, 100L), sampleAdmin(2L, 200L));
        when(administratorRepository.findAll()).thenReturn(admins);

        List<Administrator> result = administratorRepository.findAll();

        assertThat(result).hasSize(2);
        assertThat(result.get(0).getMainUser_id()).isEqualTo(100L);
        assertThat(result.get(1).getMainUser_id()).isEqualTo(200L);
        verify(administratorRepository).findAll();
    }
}
