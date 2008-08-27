package jp.go.aist.rtm.rtclink.model.nameservice;

import org.eclipse.emf.common.util.EList;
import org.omg.CosNaming.NamingContextExt;

/**
 * �l�[�~���O�R���e�N�X�g��\������N���X
 * 
 * @model
 */
public interface NamingContextNode extends Node {

	public NamingContextExt getCorbaObjectInterface();

	/**
	 * @model type="jp.go.aist.rtm.rtclink.model.nameservice.Node"
	 *        containment="true" resolveProxies="false"
	 * @return
	 */
	public EList getNodes();

	/**
	 * @model changeable="false" transient="true" volatile="true"
	 */
	public boolean isZombie();

}