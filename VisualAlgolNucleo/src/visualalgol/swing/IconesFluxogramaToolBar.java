package visualalgol.swing;

import javax.swing.JButton;
import javax.swing.JToolBar;

public class IconesFluxogramaToolBar extends JToolBar{
	private static final long serialVersionUID = 1L;
	private JButton btnCondicao;
	private JButton btnComando;
	private JButton btnFimDecisao;
	
	public IconesFluxogramaToolBar() {
		//instanciando...
		btnCondicao = new JButton("Condi��o");
		btnComando = new JButton("Comando");
		btnFimDecisao = new JButton("Fim da Condi��o");
		
		//layout...
		this.add(btnCondicao);
		this.add(btnComando);
		this.add(btnFimDecisao);
	}

	/**
	 * @return the btnCondicao
	 */
	public JButton getBtnCondicao() {
		return btnCondicao;
	}

	/**
	 * @param btnCondicao the btnCondicao to set
	 */
	public void setBtnCondicao(JButton btnCondicao) {
		this.btnCondicao = btnCondicao;
	}

	/**
	 * @return the btnComando
	 */
	public JButton getBtnComando() {
		return btnComando;
	}

	/**
	 * @param btnComando the btnComando to set
	 */
	public void setBtnComando(JButton btnComando) {
		this.btnComando = btnComando;
	}

	/**
	 * @return the btnFimDecisao
	 */
	public JButton getBtnFimDecisao() {
		return btnFimDecisao;
	}

	/**
	 * @param btnFimDecisao the btnFimDecisao to set
	 */
	public void setBtnFimDecisao(JButton btnFimDecisao) {
		this.btnFimDecisao = btnFimDecisao;
	}
	
}