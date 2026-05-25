package es.upm.proyecto.jade.recomendador.models;

public enum AgentModel {
	
	INTERFACE("UserInterface"),
	RECOMMENDER("Recommender"),
	DATA("DataRecover"),
	REVIEWER("ReviewRecover"),
	DESCONOCIDO("Desconocido");
	
	private final String value;
	
	AgentModel(String value){
		this.value = value;
	}
	
	public String getValue(){
		return this.value;
	}
	
	public static AgentModel getEnum(String value) {
		switch (value) {
		case "UserInterface":
			return INTERFACE;
		case "DataRecover":
			return DATA;
		case "ReviewRecover":
			return REVIEWER;
		case "Recommender":
			return RECOMMENDER;
		default:
			return DESCONOCIDO;
		}
	}
	
}
