package jp.go.aist.rtm.rtclink.ui.views.propertysheetview;

import jp.go.aist.rtm.rtclink.model.component.Component;

/**
 * �R���|�[�l���g�̃��b�p�N���X
 */
public class ComponentWrapper {
	private Component component;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param component
	 *            �h���C�����f��
	 */
	public ComponentWrapper(Component component) {
		this.component = component;
	}

	/**
	 * �R���|�[�l���g���擾����
	 * 
	 * @return �R���|�[�l���g
	 */
	public Component getComponent() {
		return component;
	}

	/**
	 * �R���|�[�l���g��ݒ肷��
	 * 
	 * @param component
	 *            �R���|�[�l���g
	 */
	public void setComponent(Component component) {
		this.component = component;
	}
}
