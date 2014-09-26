package br.com.caelum.brutauth.verifier;

import java.lang.annotation.Annotation;
import java.util.List;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import br.com.caelum.brutauth.auth.rules.BrutauthRule;
import br.com.caelum.brutauth.auth.rules.DefaultBrutauthRuleProducer;
import br.com.caelum.brutauth.interceptors.BrutauthClassOrMethod;

/**
 * Verify every brutauth annotations for a BrutauthClassOrMethod.
 * This is called two times for each request: One for the controller class and other for the controller method.
 * If no annotations of BrutauthClassOrMethod can be verified, the rule annotated with @DefaultRule will be used.
 * @author Leonardo Wolter
 */
public class BrutauthRulesVerifiers {

	private final Instance<BrutauthRulesVerifier> verifiers;
	private final SingleRuleVerifier singleVerifier;
	private DefaultBrutauthRuleProducer defaultRuleProvider;

	@Inject
	public BrutauthRulesVerifiers(Instance<BrutauthRulesVerifier> verifiers, DefaultBrutauthRuleProducer defaultRuleProvider,
			SingleRuleVerifier singleVerifier) {
		this.verifiers = verifiers;
		this.defaultRuleProvider = defaultRuleProvider;
		this.singleVerifier = singleVerifier;
	}

	/**
	 * @deprecated CDI eyes only
	 */
	protected BrutauthRulesVerifiers() {
		this(null, null, null);
	}
	
	public boolean verify(BrutauthClassOrMethod type) {
		List<Annotation> annotations = type.getAnnotations();
		for (BrutauthRulesVerifier verifier: verifiers) {
			for (Annotation annotation : annotations) {
				if(verifier.canVerify(annotation.annotationType())){
					if(!verifier.rulesOfTypeAllows(type)) return false;
				}
			}
		}
		BrutauthRule defaultRule = defaultRuleProvider.getInstance();
		boolean userDefinedDefaultRule = defaultRule != null;
		if(userDefinedDefaultRule) return singleVerifier.verify(defaultRule, null);
		return true;
	}
	
}
