package de.mq.iot.rule.support;

import de.mq.iot.model.LocaleAware;
import de.mq.iot.model.Subject;


public interface RuleDefinitionModel extends Subject<RuleDefinitionModel.Events, RuleDefinitionModel> , LocaleAware {
		
		enum Events {
			AssignState,
			ChangeLocale;
		}
		
		
		

}
