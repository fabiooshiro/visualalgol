package visualalgol.ferramenta;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;

import visualalgol.entidades.CondicaoFim;
import visualalgol.entidades.CondicaoIf;
import visualalgol.entidades.InstrucaoGenerica;
import visualalgol.entidades.Linha;

public class CondicaoIfFerramenta extends Ferramenta {

	@Override
	public void mouseClicked(MouseEvent e) {
		if (getInstrucaoEm(e.getX(), e.getY()) != null) return;
		
		// pegar a linha em x y do mouse
		Linha linha = getLinhaEm(e.getX(), e.getY());
		if(linha!=null){
			if(linha.getPontoTemporario()!=null){//Criar antes deste ponto
				InstrucaoGenerica destino = linha.getDestino(); 
				linha.getListPontos().remove(linha.getPontoTemporario());
				int x = linha.getPontoTemporario().x;//alinhar o x com o ponto inferior
				int y = e.getY();
				criarIf(linha, destino,x , y,linha.getPontoTemporario());
			}else{//criar antes do destino
				InstrucaoGenerica destino = linha.getDestino(); 
				int x = destino.getX();//alinhar o x com o destino
				int y = e.getY();
				criarIf(linha, destino, x, y,null);
			}
		}
	}
	private void criarIf(Linha linha, InstrucaoGenerica destino, int x, int y, Point point) {
		//Criar a intrucao if
		CondicaoIf condicaoIf = new CondicaoIf();
		condicaoIf.setX(x);//alinhar o x com o destino
		condicaoIf.setY(y);
		condicaoIf.setW(100);
		condicaoIf.setH(60);
		condicaoIf.setCor(new Color(0xff, 0xf0, 0xf0).getRGB());
		getAlgoritmo().getListComando().add(condicaoIf);
		condicaoIf.setAlgoritmo(getAlgoritmo());
		setArrastando(condicaoIf);
		//indicar a linha de entrada
		condicaoIf.setLinhaEntrada(linha);
		
		//alterar o destino da linha original
		linha.setDestino(condicaoIf);
		
		//criar o end if
		CondicaoFim condicaoFim = new CondicaoFim();
		condicaoFim.setX(x);//alinhar o x com o destino
		condicaoFim.setY(destino.getY()-30);//proximo do destino
		condicaoFim.setW(10);
		condicaoFim.setH(10);
		condicaoFim.setCor(new Color(0xff, 0xf0, 0xf0).getRGB());
		getAlgoritmo().getListComando().add(condicaoFim);
		condicaoFim.setAlgoritmo(getAlgoritmo());
		
		Linha linhaVerdadeira = new Linha();
		{//criar a linha para o true
			linhaVerdadeira.setOrigem(condicaoIf);
			linhaVerdadeira.setDestino(condicaoFim);
			getAlgoritmo().getListLinha().add(linhaVerdadeira);
			
			condicaoIf.setLinhaVerdadeira(linhaVerdadeira);
		}
		Linha linhaFalsa = new Linha();
		{//criar a linha para o false
			linhaFalsa.setOrigem(condicaoIf);
			{//criar o desvio do false
				linhaFalsa.getListPontos().add(new Point(x+150,y));
				linhaFalsa.getListPontos().add(new Point(x+150,condicaoFim.getY()));
			}
			linhaFalsa.setDestino(condicaoFim);
			getAlgoritmo().getListLinha().add(linhaFalsa);
			
			condicaoIf.setLinhaFalsa(linhaFalsa);
		}
		//ligar o end if ao comando posterior, criando uma linha
		Linha fimSeAoDestino = new Linha();
		fimSeAoDestino.setOrigem(condicaoFim);
		if(point!=null){//colocar o ponto removido no proximo
			fimSeAoDestino.getListPontos().add(point);
		}
		fimSeAoDestino.setDestino(destino);
		getAlgoritmo().getListLinha().add(fimSeAoDestino);
		condicaoFim.setLinhaSaida(fimSeAoDestino);
		condicaoFim.getListLinhaEntrada().add(linhaVerdadeira);
		condicaoFim.getListLinhaEntrada().add(linhaFalsa);
	}
	private void implementacaoAntiga(MouseEvent e){
		if (getInstrucaoEm(e.getX(), e.getY()) == null) {
			CondicaoIf condicaoIf = new CondicaoIf();
			condicaoIf.setX(e.getX());
			condicaoIf.setY(e.getY());
			condicaoIf.setW(100);
			condicaoIf.setH(60);
			condicaoIf.setCor(new Color(0xff, 0xf0, 0xf0).getRGB());
			getAlgoritmo().getListComando().add(condicaoIf);
			condicaoIf.setAlgoritmo(getAlgoritmo());
			setArrastando(condicaoIf);
		}
	}

}