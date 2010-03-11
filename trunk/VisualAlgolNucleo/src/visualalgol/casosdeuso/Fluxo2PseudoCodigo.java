package visualalgol.casosdeuso;

import java.util.ArrayList;
import java.util.List;

import visualalgol.entidades.Comando;
import visualalgol.entidades.CondicaoFim;
import visualalgol.entidades.CondicaoIf;
import visualalgol.entidades.Fim;
import visualalgol.entidades.Inicio;
import visualalgol.entidades.InstrucaoGenerica;
import visualalgol.swing.MainFrame;

/**
 * Conversor de fluxo para pseudo codigo
 * TODO definir o estilo do pseudo codigo
 */
public class Fluxo2PseudoCodigo extends CasoDeUso {

	private MainFrame mainFrame;

	/**
	 * Contador de tabs para edentar o codigo
	 */
	private int nTabs = 0;

	/**
	 * Navegar pelos nodes iniciando do Inicio, vamos navegar sempre pelas
	 * linhas
	 */
	@Override
	public void executar(MainFrame mainFrame) {
		this.mainFrame = mainFrame;
		// detecta os loops
		navegarPeloGrafo(false);
		// imprime o codigo na tela
		navegarPeloGrafo(true);
	}

	/**
	 * Trata do print dentro da tela do Usuario, cuida dos tabs tamb&eacute;m
	 * 
	 * @param string
	 *            codigo para colocar na tela
	 */
	private void print(String string) {
		String texto = mainFrame.getTelaPseudoCodigo().getText();
		StringBuilder tabs = new StringBuilder();
		for (int i = 0; i < nTabs; i++)
			tabs.append('\t');
		mainFrame.getTelaPseudoCodigo().setText(texto + "\n" + tabs.toString() + string);
	}

	/**
	 * Navega pelas instrucoes inseridas dentro do fluxograma
	 * 
	 * @param printMode
	 *            se estiver com true ira jogar o print na tela do usuario
	 */
	private void navegarPeloGrafo(boolean printMode) {
		List<CondicaoIf> pilhaCondicao = new ArrayList<CondicaoIf>();
		Inicio inicio = mainFrame.getAlgoritmo().getComandoInicial();
		InstrucaoGenerica instrucao, proximaInstrucao = inicio.getLinhaSaida().getDestino();
		// Zerar
		for (InstrucaoGenerica aux : mainFrame.getAlgoritmo().getListComando()) {
			aux.setVisitado(false);
		}
		while (true) {
			// para deixar mais claro. Ao final do loop existe o
			// instrucao.setVisitado(true);
			instrucao = proximaInstrucao;
			if (instrucao == null) {
				break;
			} else if (instrucao instanceof CondicaoIf) {
				CondicaoIf condicao = (CondicaoIf) instrucao;
				if (printMode) {
					// modo para dar saida no pseudo codigo
					if (condicao.isLoop()) {
						if (!condicao.isVisitado()) {
							print("while(" + condicao.getPseudoCodigo() + "){ ");
							nTabs++;
						} else {
							nTabs--;
							print("}//fim do loop ");
						}
					} else {
						print("if(" + condicao.getPseudoCodigo() + "){");
						nTabs++;
					}
				}
				// pode ser um if ou um loop
				if (condicao.isVisitado()) {
					// se ja foi visitado entao so pode ser um loop
					// desempilha
					condicao = pilhaCondicao.remove(pilhaCondicao.size() - 1);
					condicao.setLoop(true);
					// andar pelo false
					proximaInstrucao = condicao.getLinhaFalsa().getDestino();
				} else {
					// nao foi visitado, indefinido
					// empilha
					pilhaCondicao.add(condicao);
					// andar pelo true
					proximaInstrucao = condicao.getLinhaVerdadeira().getDestino();
				}
			} else if (instrucao instanceof Comando) {
				// Comando
				Comando comando = (Comando) instrucao;
				proximaInstrucao = comando.getLinhaSaida().getDestino();
				if (printMode) {
					if (comando.getPseudoCodigo() != null) {
						print(comando.getPseudoCodigo() + ";");
					} else {
						print("comando qualquer;");
					}
				}
			} else if (instrucao instanceof CondicaoFim) {
				// Fim de Condicao, vulgo end if
				if (!instrucao.isVisitado()) {
					// desempilha
					CondicaoIf condicao = pilhaCondicao.remove(pilhaCondicao.size() - 1);
					condicao.setLoop(false);
					// andar pelo false
					proximaInstrucao = condicao.getLinhaFalsa().getDestino();
					if (printMode) {
						nTabs--;
						print("}else{");
						nTabs++;
					}
				} else {
					CondicaoFim condicaoFim = (CondicaoFim) instrucao;
					proximaInstrucao = condicaoFim.getLinhaSaida().getDestino();
					if (printMode) {
						nTabs--;
						print("}//fim de condicao");
					}
				}
			} else if (instrucao instanceof Fim) {
				break;
			}
			instrucao.setVisitado(true);
		}

	}
}