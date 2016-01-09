package yushijinhun.authlibagent.web;

import java.rmi.RemoteException;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.Signature;
import java.security.interfaces.RSAPrivateKey;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import org.springframework.stereotype.Component;
import yushijinhun.authlibagent.api.web.SignatureKeyChangeCallback;
import yushijinhun.authlibagent.api.web.WebBackend;
import static yushijinhun.authlibagent.commons.RandomUtils.*;

@Component("signature_service")
public class SignatureServiceImpl implements SignatureService {

	@Resource(name = "backend")
	private WebBackend backend;

	private volatile RSAPrivateKey key;
	private SignatureKeyChangeCallback keyListener;

	@Override
	public byte[] sign(byte[] data) throws GeneralSecurityException {
		if (key == null) {
			throw new InvalidKeyException("no key to sign with");
		}
		Signature signature = Signature.getInstance("SHA1withRSA");
		signature.initSign(key, getSecureRandom());
		signature.update(data);
		return signature.sign();
	}

	@PostConstruct
	private void registerKeyListener() throws RemoteException {
		keyListener = new SignatureKeyChangeCallback() {

			@Override
			public void call(RSAPrivateKey newKey) throws RemoteException {
				key = newKey;
			}
		};
		backend.addSignatureKeyListener(keyListener);
		key = backend.getSignatureKey();
	}

	@PreDestroy
	private void unregisterKeyListener() throws RemoteException {
		backend.removeSignatureKeyListener(keyListener);
		keyListener = null;
		key = null;
	}

}