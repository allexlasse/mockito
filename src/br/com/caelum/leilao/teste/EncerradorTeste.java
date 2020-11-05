package br.com.caelum.leilao.teste;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.inOrder;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.mockito.InOrder;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.EnviadorDeEmail;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.servico.EncerradorDeLeilao;

public class EncerradorTeste {


	@Test
	public void encerrarLeiloesComUmaSemana() {
		Calendar antigo = Calendar.getInstance();
		antigo.set(1999, 1, 20);
		
		Leilao l1 = new CriadorDeLeilao().para("TV").naData(antigo).constroi();
		Leilao l2 = new CriadorDeLeilao().para("PS5").naData(antigo).constroi();
		List<Leilao> leiloesAntigos = Arrays.asList(l1,l2);
		
		LeilaoDao daoMock = mock(LeilaoDao.class);
		EnviadorDeEmail carteiroMock = mock(EnviadorDeEmail.class);
		
		when(daoMock.correntes()).thenReturn(leiloesAntigos);
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoMock, carteiroMock);
		encerrador.encerra();
		
		assertEquals(2, encerrador.getTotalEncerrados());
		assertTrue(l1.isEncerrado());
		assertTrue(l2.isEncerrado());
		
	}
	
	@Test
	public void testaLeilaoVazio() {
		
		LeilaoDao daoMock = mock(LeilaoDao.class);
		EnviadorDeEmail carteiroMock = mock(EnviadorDeEmail.class);
		
		//Como o mockito retorna certos parametros por padrão, não é preciso retornar a lista vazia
		//when(daoMock.correntes()).thenReturn(new ArrayList<Leilao>());
		
		/*
		  Por padrão: 
		    -Se o método retorna um inteiro, double, ou um tipo primitivo qualquer, ele retornará 0
			-Se o método retorna uma lista, o Mockito retornará uma lista vazia
			-Se o método retorna uma outra classe qualquer, o Mockito retorna null.
		 */
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoMock, carteiroMock);
		encerrador.encerra();
		
		assertEquals(0, encerrador.getTotalEncerrados());
	}
	
	@Test
	public void verificaAtualiza() {
		//Configura a data de encerramento do leilão
		Calendar antigo = Calendar.getInstance();
		antigo.set(1999, 1, 20);
		
		//Cria um novo leilão
		Leilao l1 = new CriadorDeLeilao().para("TV").naData(antigo).constroi();
		
		//Cria o mock do LeilaoDAO
		LeilaoDao daoMock = mock(LeilaoDao.class);
		EnviadorDeEmail carteiroMock = mock(EnviadorDeEmail.class);
		
		//"Ensina" o mock a agir como o DAO, retornando uma lista de leilões
		when(daoMock.correntes()).thenReturn(Arrays.asList(l1));
		
		//Passa o mock para o encerrador como um objeto dao em seu construtor, para encerrar seus leilões ativos
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoMock, carteiroMock);
		encerrador.encerra();
		
		//para verificar a atualização dos leilões, use-se o metodo verify, que vai falhar caso o metodo Atualiza
		//do LeilaoDao não seja chamado
		// -> verify(daoMock).atualiza(l1);
		
		//Para verificar ainda a quantidade de vezes que o metodo deve ser chamado, pode-se usar:
		verify(daoMock, times(1)).atualiza(l1);
		
		/*Ainda podemos passar atLeastOnce(), atLeast(numero) e atMost(numero) para o verify(). Também é possível usar 
		  o metodo never() para garantir que um metodo nunca é chamado.
		 */
	}
	
	@Test
	public void verificaOrdemDosMetodos() {
		
		Calendar antigo = Calendar.getInstance();
		antigo.set(1999, 1, 20);
		Leilao l1 = new CriadorDeLeilao().para("TV").naData(antigo).constroi();
		
		LeilaoDao daoMock = mock(LeilaoDao.class);
		EnviadorDeEmail carteiroMock = mock(EnviadorDeEmail.class);
		
		when(daoMock.correntes()).thenReturn(Arrays.asList(l1));
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoMock, carteiroMock);
		encerrador.encerra();
		
		InOrder inOrder = inOrder(daoMock, carteiroMock);
		inOrder.verify(daoMock, times(1)).atualiza(l1);
		inOrder.verify(carteiroMock, times(1)).envia(l1);
		
		
	}
}
