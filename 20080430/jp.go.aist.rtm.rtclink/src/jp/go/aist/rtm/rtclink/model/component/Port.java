package jp.go.aist.rtm.rtclink.model.component;

import jp.go.aist.rtm.rtclink.model.core.CorbaWrapperObject;

/**
 * Port��\������N���X
 * @model
 */
public interface Port extends ConnectorSource, ConnectorTarget, CorbaWrapperObject {
	/**
	 * @model containment="true"
	 */
	public PortProfile getPortProfile();

	void setPortProfile(PortProfile value);

	public RTC.Port getCorbaObjectInterface();
}
