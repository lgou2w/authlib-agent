package yushijinhun.authlibagent.model;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@XmlRootElement
@Entity
public class AccessRule implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String DEFAULT_RULE_KEY = "default";

	private String host;
	private AccessPolicy policy;

	@XmlElement
	@Id
	@Column(nullable = false, unique = true)
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@XmlElement
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	public AccessPolicy getPolicy() {
		return policy;
	}

	public void setPolicy(AccessPolicy policy) {
		this.policy = policy;
	}

	@Override
	public int hashCode() {
		return Objects.hash(host);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AccessRule) {
			AccessRule another = (AccessRule) obj;
			return Objects.equals(getHost(), another.getHost());
		}
		return false;
	}

}
