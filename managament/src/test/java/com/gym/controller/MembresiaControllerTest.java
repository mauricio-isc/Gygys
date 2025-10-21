package com.gym.controller;

import com.gym.dto.MembresiaRequest;
import com.gym.dto.MembresiaResponse;
import com.gym.entity.Membresia;
import com.gym.service.MembresiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MembresiaControllerTest {

    @Mock
    private MembresiaService membresiaService;

    @InjectMocks
    private MembresiaController membresiaController;

    private MembresiaResponse membresiaResponse;
    private MembresiaRequest membresiaRequest;

    @BeforeEach
    void setUp(){
    membresiaRequest = createMembresiaRequest();
    membresiaResponse = createMembresiaResponse();
    }

    private MembresiaRequest createMembresiaRequest(){
        MembresiaRequest request = new MembresiaRequest();
        request.setTipoMembresiaId(1L);
        request.setMiembroId(1L);
        request.setPrecioPagado(new BigDecimal("100.00"));
        request.setNotas("Prefiere ir por las tardes");

        return request;
    }


    private MembresiaResponse createMembresiaResponse(){
        MembresiaResponse response = new MembresiaResponse();
        response.setId(1L);
        response.setTipoMembresia("Premium");
        response.setTipoMembresiaId(1L);
        response.setEstado("Activa");
        return response;
    }

    @Test
    void testFindAll(){
        //arrange
        List<MembresiaResponse> membresias = Arrays.asList(membresiaResponse);
        when(membresiaService.findAll()).thenReturn(membresias);

        //act
        ResponseEntity<List<MembresiaResponse>> response = membresiaController.findAll();

        //Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(membresiaResponse, response.getBody().get(0));
        verify(membresiaService, times(1)).findAll();
    }

    @Test
    void testFindById(){
        //arrange
        when(membresiaService.findById(1L)).thenReturn(membresiaResponse);

        //act
        ResponseEntity<MembresiaResponse> response = membresiaController.findById(1L);

        //assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(membresiaResponse, response.getBody());
        verify(membresiaService, times(1)).findById(1L);
    }

    @Test
    void TestActivateMembership(){
        //arrange
        Long miembroId = 1L;
        Long tipoMembresiaId= 2L;
        BigDecimal precioPagado = new BigDecimal("150.00");

        when(membresiaService.activateMembership(miembroId, tipoMembresiaId, precioPagado))
                .thenReturn(membresiaResponse);

        //act
        ResponseEntity<MembresiaResponse> response = membresiaController
                .activateMembership(miembroId, tipoMembresiaId, precioPagado);

        //assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(membresiaResponse, response.getBody());
        verify(membresiaService, times(1))
                .activateMembership(miembroId, tipoMembresiaId, precioPagado);
    }

    @Test
    void testFindExpiringMembership(){
        //arrange
        List<MembresiaResponse> membresias = Arrays.asList(membresiaResponse);
        when(membresiaService.findExpiringMemberships()).thenReturn(membresias);

        //act
        ResponseEntity<List<MembresiaResponse>> response = membresiaController.findExpiringMemberships();

        //assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        verify(membresiaService, times(1)).findExpiringMemberships();
    }

    @Test
    void testGetMembershipStats(){
        long activeMemberships = 1L;
        long expiredMemberships = 1L;
        long expiringMemberships = 1L;
        //arrange
        when(membresiaService.countActiveMemberships()).thenReturn(activeMemberships);
        when(membresiaService.countExpiredMemberships()).thenReturn(expiredMemberships);
        when(membresiaService.countExpiringMemberships()).thenReturn(expiringMemberships);

        //act
        ResponseEntity<MembresiaController.MembershipStats> response = membresiaController.getMembershipStats();

        //assert
        assertEquals(HttpStatus.OK, response.getStatusCode());

        MembresiaController.MembershipStats stats = response.getBody();
        assertNotNull(stats);
        assertEquals(activeMemberships, stats.getActiveMemberships());
        assertEquals(expiredMemberships, stats.getExpiredMemberships());
        assertEquals(expiringMemberships, stats.getExpiringMemberships());

        verify(membresiaService, times(1)).countActiveMemberships();
        verify(membresiaService, times(1)).countExpiredMemberships();
        verify(membresiaService, times(1)).countExpiringMemberships();
    }

    @Test
    void testUpdateMembershipStatus(){
        //act
        ResponseEntity<Void> response = membresiaController.updateMembershipStatus();

        //assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNull(response.getBody());
        verify(membresiaService, times(1)).updateMembershipStatus();
    }

    @Test
    void testMembershipStatsSettersAndGetters(){

        long active = 15L;
        long expired = 8L;
        long expiring = 2L;

        //arrange
        MembresiaController.MembershipStats stats = new MembresiaController.MembershipStats();

        //Act
        stats.setActiveMemberships(active);
        stats.setExpiredMemberships(expired);
        stats.setExpiringMemberships(expiring);

        //assert
        assertEquals(active, stats.getActiveMemberships());
        assertEquals(expired, stats.getExpiredMemberships());
        assertEquals(expiring, stats.getExpiringMemberships());
    }

}
