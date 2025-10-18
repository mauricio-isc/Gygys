package com.gym.controller;

import com.gym.dto.MembresiaRequest;
import com.gym.dto.MembresiaResponse;
import com.gym.service.MembresiaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static jdk.internal.org.objectweb.asm.util.CheckClassAdapter.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


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
        membresiaResponse = new MembresiaResponse();
        membresiaResponse.setId(1L);
        membresiaResponse.setTipoMembresia("Membresia premium");
        membresiaRequest.setPrecioPagado(new BigDecimal("100.00"));
    }

    @Test
    void testFindAll(){
        //arrange
        List<MembresiaResponse> membresias = Arrays.asList(membresiaResponse);
        when(membresiaService.findAll()).thenReturn(membresias);

        //act
        ResponseEntity<List<MembresiaResponse>> response = membresiaController.findAll();

        //Assert
        AssertEquals(HttpStatus.OK, response.getStatusCode());
        AssertEquals(1, response.getBody().size());
        AssertEquals(membresiaResponse, response.getBody().get(0));
        verify(membresiaService, times(1)).findAll();
    }

    private void AssertEquals() {
    }
}
