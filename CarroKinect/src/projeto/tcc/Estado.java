package projeto.tcc;

public enum Estado {
	// Alguns desses valores estão diferentes do que tem em CarroKinect
	VIRA_ESQUERDA("Esquerda",4),
	VIRA_DIREITA("Direita", 6),
	MOVE_FRENTE("Frente", 8),
	MOVE_TRAS("Tras", 2),
	PARA("Parar",0),
	PARA_RODA_DIANTEIRA("Parar Rodas Dianteiras",5);

	private String descricao;
	private int valorArduino;

	Estado(String descricao, int valorArduino){
		this.setDescricao(descricao);
		this.setValorArduino(valorArduino);
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getValorArduino() {
		return valorArduino;
	}

	public void setValorArduino(int valorArduino) {
		this.valorArduino = valorArduino;
	}


}
