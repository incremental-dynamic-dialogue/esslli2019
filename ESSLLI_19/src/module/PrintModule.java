package module;

import inpro.incremental.IUModule;
import inpro.incremental.unit.EditMessage;
import inpro.incremental.unit.IU;

import java.util.Collection;
import java.util.List;

public class PrintModule extends IUModule {
	
	
	@Override
	protected void leftBufferUpdate(Collection<? extends IU> ius,
			List<? extends EditMessage<? extends IU>> edits) {
		
		
		for (EditMessage<? extends IU> edit : edits) {
				System.out.println(edit);

		}
		
	}

}
