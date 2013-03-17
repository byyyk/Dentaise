package pl.edu.agh.mkulpa.dentaise.server.dto;

public interface DtoDsTransformer<TDS, TDTO> {

	public void copyDtoDataToDs(TDTO dto, TDS ds);
	public TDTO transformDsIntoDto(TDS ds);
	
}
