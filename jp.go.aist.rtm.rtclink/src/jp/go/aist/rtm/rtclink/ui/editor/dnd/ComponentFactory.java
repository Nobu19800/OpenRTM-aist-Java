package jp.go.aist.rtm.rtclink.ui.editor.dnd;

import jp.go.aist.rtm.rtclink.factory.CorbaWrapperFactory;
import jp.go.aist.rtm.rtclink.model.component.Component;
import jp.go.aist.rtm.rtclink.model.core.WrapperObject;

import org.eclipse.gef.requests.CreationFactory;

/**
 * �h���b�O���h���b�v���A�R���|�[�l���g���쐬����t�@�N�g��
 */
public class ComponentFactory implements CreationFactory {
	private Component component;

	/**
	 * {@inheritDoc}
	 */
	public Object getNewObject() {
		WrapperObject result = null;
		if (component != null) {
			result = CorbaWrapperFactory.getInstance().copy(component);
		}

		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public Object getObjectType() {
		return Component.class;
	}

	/**
	 * �R���|�[�l���g�̃����[�g�I�u�W�F�N�g��ݒ肷��
	 * 
	 * @param remoteObject
	 *            �R���|�[�l���g�̃����[�g�I�u�W�F�N�g
	 */
	public void setComponent(Component component) {
		this.component = component;
	}
}
