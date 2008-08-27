package jp.go.aist.rtm.rtclink.ui.editor.command;

import jp.go.aist.rtm.rtclink.model.core.ModelElement;
import jp.go.aist.rtm.rtclink.ui.util.Draw2dUtil;

import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.gef.commands.Command;

/**
 * ����i�ʒu�A�傫���j�̕ύX���s���R�}���h
 */
public class ChangeConstraintCommand extends Command {
	private ModelElement model;

	private Rectangle constraint;

	private Rectangle oldConstraint;

	private ClearLineConstraintCommand clearLineConstraintCommand = new ClearLineConstraintCommand();

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void execute() {
		model.setConstraint(Draw2dUtil.toRtcLinkRectangle(constraint));

		clearLineConstraintCommand.setModel(model);
		clearLineConstraintCommand.execute();
	}

	/**
	 * ����i�ʒu�A�傫���j��ݒ肷��
	 * 
	 * @param constraint
	 *            ����
	 */
	public void setConstraint(Rectangle constraint) {
		this.constraint = constraint;
	}

	/**
	 * �ύX�Ώۂ̃��f����ݒ�
	 * 
	 * @param model
	 *            �ύX�Ώۂ̃��f��
	 */
	public void setModel(ModelElement model) {
		this.model = model;
		this.oldConstraint = Draw2dUtil
				.toDraw2dRectangle(model.getConstraint());
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void undo() {
		clearLineConstraintCommand.undo();

		model.setConstraint(Draw2dUtil.toRtcLinkRectangle(oldConstraint));
	}
}
