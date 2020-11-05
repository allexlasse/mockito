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
		
		//Como o mockito retorna certos parametros por padr�o, n�o � preciso retornar a lista vazia
		//when(daoMock.correntes()).thenReturn(new ArrayList<Leilao>());
		
		/*
		  Por padr�o: 
		    -Se o m�todo retorna um inteiro, double, ou um tipo primitivo qualquer, ele retornar� 0
			-Se o m�todo retorna uma lista, o Mockito retornar� uma lista vazia
			-Se o m�todo retorna uma outra classe qualquer, o Mockito retorna null.
		 */
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoMock, carteiroMock);
		encerrador.encerra();
		
		assertEquals(0, encerrador.getTotalEncerrados());
	}
	
	@Test
	public void verificaAtualiza() {
		//Configura a data de encerramento do leil�o
		Calendar antigo = Calendar.getInstance();
		antigo.set(1999, 1, 20);
		
		//Cria um novo leil�o
		Leilao l1 = new CriadorDeLeilao().para("TV").naData(antigo).constroi();
		
		//Cria o mock do LeilaoDAO
		LeilaoDao daoMock = mock(LeilaoDao.class);
		EnviadorDeEmail carteiroMock = mock(EnviadorDeEmail.class);
		
		//"Ensina" o mock a agir como o DAO, retornando uma lista de leil�es
		when(daoMock.correntes()).thenReturn(Arrays.asList(l1));
		
		//Passa o mock para o encerrador como um objeto dao em seu construtor, para encerrar seus leil�es ativos
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoMock, carteiroMock);
		encerrador.encerra();
		
		//para verificar a atualiza��o dos leil�es, use-se o metodo verify, que vai falhar caso o metodo Atualiza
		//do LeilaoDao n�o seja chamado
		// -> verify(daoMock).atualiza(l1);
		
		//Para verificar ainda a quantidade de vezes que o metodo deve ser chamado, pode-se usar:
		verify(daoMock, times(1)).atualiza(l1);
		
		/*Ainda podemos passar atLeastOnce(), atLeast(numero) e atMost(numero) para o verify(). Tamb�m � poss�vel usar 
		  o metodo never() para garantir que um metodo nunca � chamado.
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
