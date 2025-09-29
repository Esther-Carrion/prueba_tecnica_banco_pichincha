package com.pichincha.accounts.application.service;

import com.pichincha.accounts.application.port.output.ClientRepository;
import com.pichincha.accounts.domain.Client;
import com.pichincha.accounts.domain.enums.Gender;
import com.pichincha.accounts.domain.exception.ClientNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

	@Mock
	private ClientRepository clientRepository;

	@InjectMocks
	private ClientService clientService;

	private UUID clientId;

	@BeforeEach
	void setUp() {
		clientId = UUID.randomUUID();
	}

	private Client baseClient() {
		Client c = new Client();
		c.setId(clientId);
		c.setName("Ana");
		c.setIdentification("0102030405");
		c.setGender(Gender.FEMENINO);
		return c;
	}

	@Test
	void shouldCreateClientAndSetDefaultStateAndGenerateClientIdWhenIdentificationIsUnique() {
		Client toCreate = baseClient();
		toCreate.setClientId(null); // fuerza generación

		when(clientRepository.existsByIdentification("0102030405")).thenReturn(false);
		// Lazo de generación de clientId: simular no existe
		when(clientRepository.existsByClientId(any())).thenReturn(false);
		when(clientRepository.save(any(Client.class))).thenAnswer(i -> {
			Client saved = i.getArgument(0);
			saved.setId(UUID.randomUUID());
			return saved;
		});

		Client saved = clientService.createClient(toCreate);

		assertThat(saved.getId()).isNotNull();
		assertThat(saved.getClientId()).isNotBlank();
		assertThat(saved.getState()).isTrue();
		verify(clientRepository).save(any(Client.class));
	}

	@Test
	void shouldThrowWhenIdentificationAlreadyExists() {
		Client toCreate = baseClient();
		when(clientRepository.existsByIdentification("0102030405")).thenReturn(true);

		assertThatThrownBy(() -> clientService.createClient(toCreate))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Ya existe un cliente con la identificación");
	}

	@Test
	void shouldThrowWhenClientIdAlreadyExists() {
		Client toCreate = baseClient();
		toCreate.setClientId("C-123");

		when(clientRepository.existsByIdentification("0102030405")).thenReturn(false);
		when(clientRepository.existsByClientId("C-123")).thenReturn(true);

		assertThatThrownBy(() -> clientService.createClient(toCreate))
				.isInstanceOf(RuntimeException.class)
				.hasMessageContaining("Ya existe un cliente con el ID");
	}

	@Test
	void shouldUpdateMutableFieldsWhenClientExists() {
		UUID id = UUID.randomUUID();
		Client existing = baseClient();
		existing.setId(id);
		existing.setClientId("C-1");
		existing.setState(true);

		when(clientRepository.findById(id)).thenReturn(Optional.of(existing));
		when(clientRepository.save(any(Client.class))).thenAnswer(i -> i.getArgument(0));

		Client request = new Client();
		request.setName("Ana María");
		request.setGender(Gender.FEMENINO);
		request.setAge(30);
		request.setIdentification("1112223334");
		request.setPhone("0999999999");
		request.setAddress("Av. Siempre Viva");
		request.setPassword("secret");
		request.setState(false);

		Client updated = clientService.updateClient(id, request);

		assertThat(updated.getName()).isEqualTo("Ana María");
		assertThat(updated.getAge()).isEqualTo(30);
		assertThat(updated.getIdentification()).isEqualTo("1112223334");
		assertThat(updated.getPhone()).isEqualTo("0999999999");
		assertThat(updated.getAddress()).isEqualTo("Av. Siempre Viva");
		assertThat(updated.getPassword()).isEqualTo("secret");
		assertThat(updated.getState()).isFalse();
		verify(clientRepository).save(any(Client.class));
	}

	@Test
	void shouldThrowClientNotFoundWhenUpdatingNonExistingClient() {
		UUID id = UUID.randomUUID();
		when(clientRepository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> clientService.updateClient(id, new Client()))
				.isInstanceOf(ClientNotFoundException.class)
				.hasMessageContaining("Cliente no encontrado");
	}

	@Test
	void shouldDeleteClientWhenExists() {
		UUID id = UUID.randomUUID();
		when(clientRepository.findById(id)).thenReturn(Optional.of(baseClient()));

		clientService.deleteClient(id);
		verify(clientRepository).deleteById(id);
	}

	@Test
	void shouldThrowClientNotFoundWhenDeletingNonExistingClient() {
		UUID id = UUID.randomUUID();
		when(clientRepository.findById(id)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> clientService.deleteClient(id))
				.isInstanceOf(ClientNotFoundException.class)
				.hasMessageContaining("Cliente no encontrado");
	}

	@Test
	void shouldReturnClientOptionalWhenFindByIdExists() {
		UUID id = UUID.randomUUID();
		when(clientRepository.findById(id)).thenReturn(Optional.of(baseClient()));

		Optional<Client> result = clientService.findById(id);
		assertThat(result).isPresent();
		verify(clientRepository).findById(id);
	}

	@Test
	void shouldReturnEmptyOptionalWhenFindByIdNotExists() {
		UUID id = UUID.randomUUID();
		when(clientRepository.findById(id)).thenReturn(Optional.empty());

		Optional<Client> result = clientService.findById(id);
		assertThat(result).isEmpty();
		verify(clientRepository).findById(id);
	}

	@Test
	void shouldReturnClientOptionalWhenFindByClientIdExists() {
		when(clientRepository.findByClientId("C-1")).thenReturn(Optional.of(baseClient()));

		Optional<Client> result = clientService.findByClientId("C-1");
		assertThat(result).isPresent();
		verify(clientRepository).findByClientId("C-1");
	}

	@Test
	void shouldReturnEmptyOptionalWhenFindByClientIdNotExists() {
		when(clientRepository.findByClientId("C-404")).thenReturn(Optional.empty());

		Optional<Client> result = clientService.findByClientId("C-404");
		assertThat(result).isEmpty();
		verify(clientRepository).findByClientId("C-404");
	}

	@Test
	void shouldDelegateFindAllWhenInvoked() {
		when(clientRepository.findAll()).thenReturn(List.of());
		assertThat(clientService.findAll()).isEmpty();
		verify(clientRepository).findAll();
	}

	@Test
	void shouldReturnTrueWhenExistsByClientIdIsTrue() {
		when(clientRepository.existsByClientId("C-1")).thenReturn(true);
		assertThat(clientService.existsByClientId("C-1")).isTrue();
		verify(clientRepository).existsByClientId("C-1");
	}

	@Test
	void shouldReturnFalseWhenExistsByClientIdIsFalse() {
		when(clientRepository.existsByClientId("C-2")).thenReturn(false);
		assertThat(clientService.existsByClientId("C-2")).isFalse();
		verify(clientRepository).existsByClientId("C-2");
	}

	@Test
	void shouldReturnTrueWhenExistsByIdentificationIsTrue() {
		when(clientRepository.existsByIdentification("0102030405")).thenReturn(true);
		assertThat(clientService.existsByIdentification("0102030405")).isTrue();
		verify(clientRepository).existsByIdentification("0102030405");
	}

	@Test
	void shouldReturnFalseWhenExistsByIdentificationIsFalse() {
		when(clientRepository.existsByIdentification("0102030405")).thenReturn(false);
		assertThat(clientService.existsByIdentification("0102030405")).isFalse();
		verify(clientRepository).existsByIdentification("0102030405");
	}
}
