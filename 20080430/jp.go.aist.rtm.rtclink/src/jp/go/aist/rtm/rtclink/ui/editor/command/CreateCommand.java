package jp.go.aist.rtm.rtclink.ui.editor.command;

import jp.go.aist.rtm.rtclink.model.component.Component;
import jp.go.aist.rtm.rtclink.model.component.SystemDiagram;

import org.eclipse.gef.commands.Command;

/**
 * �V�X�e���_�C�A�O������Rtc��ǉ�����R�}���h
 */
public class CreateCommand extends Command {
	private SystemDiagram parent;

	private Component target;

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void execute() {
		parent.getComponents().add(target);
	}

	/**
	 * �e�ƂȂ�V�X�e���_�C�A�O������ݒ肷��
	 * 
	 * @param parent
	 *            �e�ƂȂ�V�X�e���_�C�A�O����
	 */
	public void setParent(SystemDiagram parent) {
		this.parent = parent;
	}

	/**
	 * �쐬�Ώۂ�Rtc��ݒ肷��
	 * 
	 * @param target
	 *            �쐬�Ώۂ�Rtc
	 */
	public void setTarget(Component target) {
		this.target = target;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void undo() {
		parent.getComponents().remove(target);
	}
}
