package com.workxlife.authentication_service.security;

import com.workxlife.authentication_service.entity.Role;
import com.workxlife.authentication_service.entity.User;
import com.workxlife.authentication_service.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsernameFindsByUsername() {
        User user = new User(1L, "john", "password", "john@example.com", Set.of(new Role(1L, "ROLE_USER")));
        when(userRepository.findByUsername("john")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("john");

        assertThat(userDetails.getUsername()).isEqualTo("john");
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_USER");
        verify(userRepository).findByUsername("john");
        verify(userRepository, never()).findByEmail(anyString());
    }

    @Test
    void loadUserByUsernameFallsBackToEmail() {
        User user = new User(2L, "jane", "password", "jane@example.com", Set.of());
        when(userRepository.findByUsername("jane@example.com")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("jane@example.com")).thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("jane@example.com");

        assertThat(userDetails.getUsername()).isEqualTo("jane");
        verify(userRepository).findByUsername("jane@example.com");
        verify(userRepository).findByEmail("jane@example.com");
    }

    @Test
    void loadUserByUsernameThrowsWhenUserMissing() {
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customUserDetailsService.loadUserByUsername("missing"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("missing");
    }
}
