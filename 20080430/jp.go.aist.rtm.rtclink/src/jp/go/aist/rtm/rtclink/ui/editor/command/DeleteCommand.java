package jp.go.aist.rtm.rtclink.ui.editor.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.go.aist.rtm.rtclink.model.component.Component;
import jp.go.aist.rtm.rtclink.model.component.Connector;
import jp.go.aist.rtm.rtclink.model.component.Port;
import jp.go.aist.rtm.rtclink.model.component.SystemDiagram;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.gef.commands.Command;

/**
 * Rtc���폜����폜�R�}���h
 */
public class DeleteCommand extends Command {
	private SystemDiagram parent;

	private Component target;

	private List<Connector> saveSourceConnectors = new ArrayList<Connector>();

	private List<Connector> saveTargetConnectors = new ArrayList<Connector>();

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void execute() {
		for (Iterator iter = target.eAllContents(); iter.hasNext();) {
			EObject element = (EObject) iter.next();
			if (element instanceof Port) {
				saveTargetConnectors.addAll(((Port) element)
						.getTargetConnectors());
				saveSourceConnectors.addAll(((Port) element)
						.getSourceConnectors());
			}
		}

		for (Connector source : saveSourceConnectors) {
			source.dettachSource();
			source.dettachTarget();
		}

		for (Connector target : saveTargetConnectors) {
			target.dettachSource();
			target.dettachTarget();
		}

		parent.getComponents().remove(target);
	}

	/**
	 * �e�̃V�X�e���_�C�A�O������ݒ肷��
	 * 
	 * @param parent
	 *            �e�̃V�X�e���_�C�A�O����
	 */
	public void setParent(SystemDiagram parent) {
		this.parent = parent;
	}

	/**
	 * �ύX�Ώۂ̃R���|�[�l���g
	 * 
	 * @param target
	 *            �R���|�[�l���g
	 */
	public void setTarget(Component target) {
		this.target = target;
	}

	@Override
	/**
	 * {@inheritDocs}
	 */
	public void undo() {
		for (Connector source : saveSourceConnectors) {
			source.attachSource();
			source.attachTarget();
		}

		for (Connector target : saveTargetConnectors) {
			target.attachSource();
			target.attachTarget();
		}

		parent.getComponents().add(target);

		saveSourceConnectors.clear();
		saveTargetConnectors.clear();
	}
}
