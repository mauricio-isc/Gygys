package com.gym.controller;

import com.gym.dto.MiembroRequest;
import com.gym.dto.MiembroResponse;
import com.gym.entity.Miembro;
import com.gym.service.MiembroService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MiembroControllerTest {

    @Mock
    private MiembroService miembroService;

    @InjectMocks
    private MiembroController miembroController;

    private MiembroResponse miembroResponse;

    @BeforeEach
    void setUp() {
        miembroResponse = new MiembroResponse();
        miembroResponse.setId(1L);
        miembroResponse.setNombre("Mauricio");
        miembroResponse.setApellido("Sanchez");
        miembroResponse.setEmail("mau@developer.com");
        miembroResponse.setTelefono("4548539832");
        miembroResponse.setDocumentoIdentidad("3242324234");
        miembroResponse.setGenero("Masculino");
        miembroResponse.setFechaNacimiento(LocalDate.of(2003, 1, 15));
        miembroResponse.setDireccion("Ciudad de Mexico");
    }

    @Test
    void testFindAllWithPagination() {
        // arrange
        List<MiembroResponse> miembrosList = Arrays.asList(miembroResponse);
        Page<MiembroResponse> page = new PageImpl<>(miembrosList);
        when(miembroService.findAll(Pageable.unpaged())).thenReturn(page);

        // act
        ResponseEntity<Page<MiembroResponse>> response = miembroController.findAll(Pageable.unpaged());

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().getContent().size());
        assertEquals("Mauricio", response.getBody().getContent().get(0).getNombre());
    }

    @Test
    void testSearchWithPagination(){

        //arrange
        String searchQuery = "Mauricio";
        List<MiembroResponse> miembroList = Arrays.asList(miembroResponse);
        Page<MiembroResponse> page = new PageImpl<>(miembroList);
        when(miembroService.search(searchQuery, Pageable.unpaged())).thenReturn(page);

        //act
        ResponseEntity<Page<MiembroResponse>> response = miembroController.search(searchQuery, Pageable.unpaged());

        //assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1L, response.getBody().getContent().size());
        assertEquals("Mauricio", response.getBody().getContent().get(0).getNombre());
    }

    @Test
    void testFindId(){

        //arrange
        Long searchId = 1L;
        MiembroResponse expectedResponse = new MiembroResponse();
        expectedResponse.setId(searchId);
        expectedResponse.setNombre("Mauricio Sanchez");

        when(miembroService.findById(searchId)).thenReturn(expectedResponse);

        //act
        ResponseEntity<MiembroResponse> response = miembroController.findById(searchId);

        //assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(searchId, response.getBody().getId());
        verify(miembroService, times(1)).findById(searchId);
    }

    @Test
    void createMember(){

        //arrange
        MiembroRequest request = new MiembroRequest();
        request.setNombre("Mauricio");
        request.setApellido("Sanchez");
        request.setEmail("Mauri@developer.com");
        request.setFechaNacimiento(LocalDate.of(2003, 01,15));

        MiembroResponse expectedResponse = new MiembroResponse();
        expectedResponse.setId(1L);
        expectedResponse.setNombre("Mauricio");
        expectedResponse.setApellido("Sanchez");
        expectedResponse.setEmail("Mauri@developer.com");
        expectedResponse.setFechaNacimiento(LocalDate.of(2003, 01,15));

        when(miembroService.create(any(MiembroRequest.class))).thenReturn(expectedResponse);

        //act
        ResponseEntity<MiembroResponse> response = miembroController.create(request);
        //assert
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().getId());
        assertEquals("Mauricio", response.getBody().getNombre());
        assertEquals("Sanchez", response.getBody().getApellido());
        assertEquals("Mauri@developer.com", response.getBody().getEmail());
        assertEquals(LocalDate.of(2003,01,15), response.getBody().getFechaNacimiento());

        verify(miembroService, times(1)).create(request);
    }

    @Test
    void updateMember(){

        //arrange
        Long id = 1L;

    }
}
