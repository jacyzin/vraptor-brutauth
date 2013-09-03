package br.com.caelum.brutauth.reflection.methodsearchers;

import static br.com.caelum.brutauth.reflection.methodsearchers.Parameter.build;

import java.lang.reflect.Method;

import br.com.caelum.brutauth.auth.rules.CustomBrutauthRule;
import br.com.caelum.brutauth.reflection.Arguments;
import br.com.caelum.brutauth.reflection.BrutauthMethod;
import br.com.caelum.brutauth.reflection.paranamer.ParanamerParser;
import br.com.caelum.vraptor.ioc.Component;

@Component
public class AdaptedMethodSearcher implements MethodSearcher {

	private final DefaultMethodSearcher defaultMethodSearcher;

	private final ArgumentMatcher argumentMatcher;
	
	public AdaptedMethodSearcher(DefaultMethodSearcher defaultMethodSearcher, ArgumentMatcher argumentMatcher) {
		this.defaultMethodSearcher = defaultMethodSearcher;
		this.argumentMatcher = argumentMatcher;
	}
	
	@Override
	public BrutauthMethod search(CustomBrutauthRule ruleToSearch, Argument... withArgs) {
		try {
			Method defaultMethod = defaultMethodSearcher.getMethod(ruleToSearch);
			String[] parameterNames = ParanamerParser.paramsFor(defaultMethod);
			Class<?>[] classes = defaultMethod.getParameterTypes();
			Arguments argumentsThatMatchToTypes = argumentMatcher.getArgumentsThatMatchToParameters(build(parameterNames, classes), withArgs);

			return new BrutauthMethod(argumentsThatMatchToTypes.toValuesOnly(), defaultMethod, ruleToSearch);
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	
}
