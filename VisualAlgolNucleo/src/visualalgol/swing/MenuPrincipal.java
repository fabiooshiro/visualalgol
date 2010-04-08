package visualalgol.swing;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import visualalgol.entidades.ArquivoRecente;

public class MenuPrincipal extends JMenuBar{
	private static final long serialVersionUID = 1L;

	private JMenuItem salvarMenuItem;
	private JMenuItem abrirMenuItem;

	private AbrirRecenteListener abrirRecenteListener;
	private JMenuItem verPseudoCodigo;
	private JMenuItem rodar;
	private JMenuItem sairMenuItem;
	
	private ArquivoRecente arquivoRecente;
	private JMenu arquivo; 
	private JMenu recentes;
	private JMenu compilar;
	private JMenuItem novo;
	private JMenu codigo;
	public MenuPrincipal() {
		//instancia
		salvarMenuItem = new JMenuItem("Salvar");
		abrirMenuItem = new JMenuItem("Abrir");
		sairMenuItem= new JMenuItem("Sair");
		codigo = new JMenu("Codigo");
		verPseudoCodigo = new JMenuItem("Ver Pseudo Codigo");
		rodar = new JMenuItem("Executar");
		arquivo = new JMenu("Arquivo");
		recentes = new JMenu("Recentes");
		compilar = new JMenu("Compilar");
		novo = new JMenuItem("Novo");
		
		
		
		novo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
		novo.setMnemonic('N');
		
		//configuracao
		salvarMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		abrirMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		rodar.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F9,0));
		//layout
		arquivo.add(abrirMenuItem);
		arquivo.add(novo);
		arquivo.add(recentes);
		arquivo.add(salvarMenuItem);
		arquivo.add(sairMenuItem);
		this.add(arquivo);
		
		//codigo.add(verPseudoCodigo);
		this.add(codigo);
		compilar.add(rodar);
		this.add(compilar);
	}
	
	private void criarRecentes(){
		List<String> paths = arquivoRecente.getPaths();
		for(int i=0;i<paths.size();i++){
			File file = new File(paths.get(i));
			if(file.exists()){
				JMenuItem recente = new JMenuItem(file.getName());
				recentes.add(recente);
				recente.setActionCommand(paths.get(i));
				recente.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						abrirRecenteListener.abrirArquivoRecente(e.getActionCommand());
					}
				});
			}
		}
	}
	
	/**
	 * @return the salvarMenuItem
	 */
	public JMenuItem getSalvarMenuItem() {
		return salvarMenuItem;
	}
	/**
	 * @param salvarMenuItem the salvarMenuItem to set
	 */
	public void setSalvarMenuItem(JMenuItem salvarMenuItem) {
		this.salvarMenuItem = salvarMenuItem;
	}
	/**
	 * @return the abrirMenuItem
	 */
	public JMenuItem getAbrirMenuItem() {
		return abrirMenuItem;
	}
	/**
	 * @param abrirMenuItem the abrirMenuItem to set
	 */
	public void setAbrirMenuItem(JMenuItem abrirMenuItem) {
		this.abrirMenuItem = abrirMenuItem;
	}
	
	public JMenuItem getVerPseudoCodigo() {
		return verPseudoCodigo;
	}
	public void setVerPseudoCodigo(JMenuItem verPseudoCodigo) {
		this.verPseudoCodigo = verPseudoCodigo;
	}
	public ArquivoRecente getArquivoRecente() {
		return arquivoRecente;
	}
	public void setArquivoRecente(ArquivoRecente arquivoRecente) {
		this.arquivoRecente = arquivoRecente;
		criarRecentes();
	}
	public JMenuItem getSairMenuItem() {
		return sairMenuItem;
	}
	

	/**
	 * @return the abrirRecenteListener
	 */
	public AbrirRecenteListener getAbrirRecenteListener() {
		return abrirRecenteListener;
	}

	/**
	 * @param abrirRecenteListener the abrirRecenteListener to set
	 */
	public void setAbrirRecenteListener(AbrirRecenteListener abrirRecenteListener) {
		this.abrirRecenteListener = abrirRecenteListener;
	}

	/**
	 * Run F9
	 * @return the rodar
	 */
	public JMenuItem getRodar() {
		return rodar;
	}
	public JMenuItem getNovo() {
		return novo;
	}
	public JMenu getCodigo() {
		return codigo;
	}
}