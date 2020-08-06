package de.mq.iot.support;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.util.ClassUtils;

enum CsvType {
	Synonym("de.mq.iot.synonym.support.SynonymImpl"), 
	User("de.mq.iot.authentication.support.UserAuthenticationImpl", "authorities"), 
	GaussDay("de.mq.iot.calendar.support.GaussDayImpl", "dayGroup"),
	ResourceIdentifier("de.mq.iot.resource.support.ResourceIdentifierImpl", "parameters"),
	RulesDefinition("de.mq.iot.rule.support.RulesDefinitionImpl", "inputData", "optionalRules");

	private final Class<?> clazz;

	
	private final Collection<Field> fields;

	private CsvType(final String clazz, final String ...nonSimpleFields) {
		this.clazz = ClassUtils.resolveClassName(clazz, CsvImportServiceImpl.class.getClassLoader());
		fields = fields(this.clazz, nonSimpleFields);
		
	}

	final List<Field> fields(final Class<?> clazz, final String... nonSimpleFields) {
		final List<Field> fields = new ArrayList<>();
	    Class<?> target=clazz;
	    while(target != null) {
		   fields.addAll( Arrays.asList(target.getDeclaredFields()).stream().filter(field -> !Modifier.isStatic(field.getModifiers())).filter(field -> BeanUtils.isSimpleValueType(field.getType())|| Arrays.asList(nonSimpleFields).contains(field.getName())).collect(Collectors.toList()));
	       target=target.getSuperclass();
	    }
	    return Collections.unmodifiableList(fields);
	}

	final Collection<Field> fields() {
		return Collections.unmodifiableCollection(fields);
	}
	
	final Class<?> target() {
		return clazz;
	}
}