package com.lucas.helpdesk.domain.enums;

public enum Status {
	ABERTO(0, "ABERTO"), ANDAMENTO(1, "ANDAMENTO"), ENCERRADO(2, "ENCERRADO");
	
	public Integer codigo;
	public String descricao;
	
	public Integer getCodigo() {
		return codigo;
	}
	public String getDescricao() {
		return descricao;
	}
	
	private Status(Integer codigo, String descricao) {
		this.codigo = codigo;
		this.descricao = descricao;
	}	
	
	public static Status toEnum(Integer cod) {
		if (cod == null)
			return null;
		
		for (Status x : Status.values()) {
			if (cod.equals(x.getCodigo())) {
				return x;
			}
		}
		
		throw new IllegalArgumentException("Status inválido");
	} 
	
}
