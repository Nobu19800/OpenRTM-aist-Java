package jp.go.aist.rtm.rtclink.ui.editor.editpart;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

import jp.go.aist.rtm.rtclink.manager.PreferenceManager;
import jp.go.aist.rtm.rtclink.model.component.Component;
import jp.go.aist.rtm.rtclink.model.component.ComponentPackage;
import jp.go.aist.rtm.rtclink.model.component.ExecutionContext;
import jp.go.aist.rtm.rtclink.model.component.LifeCycleState;
import jp.go.aist.rtm.rtclink.model.core.CorePackage;
import jp.go.aist.rtm.rtclink.ui.editor.action.ChangeComponentDirectionAction;
import jp.go.aist.rtm.rtclink.ui.editor.editpolicy.ChangeDirectionEditPolicy;
import jp.go.aist.rtm.rtclink.ui.editor.editpolicy.ComponentComponentEditPolicy;
import jp.go.aist.rtm.rtclink.ui.editor.editpolicy.EditPolicyConstraint;
import jp.go.aist.rtm.rtclink.ui.editor.figure.ComponentLayout;
import jp.go.aist.rtm.rtclink.ui.util.Draw2dUtil;

import org.eclipse.draw2d.ColorConstants;
import org.eclipse.draw2d.Figure;
import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Label;
import org.eclipse.draw2d.MouseEvent;
import org.eclipse.draw2d.MouseListener;
import org.eclipse.draw2d.Panel;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.editparts.AbstractGraphicalEditPart;
import org.eclipse.gef.ui.actions.ActionRegistry;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.PlatformUI;

/**
 * �R���|�[�l���g��EditPart
 * <p>
 * GEF�̎d�l�ł͎q����EditPart�͐e��EditPart�Ɋ܂܂�Ȃ���΂Ȃ�Ȃ����A�|�[�g���R���|�[�l���g����͂ݏo���ĕ\�����Ȃ���΂Ȃ�Ȃ��B
 * ����𖞂����Ȃ���A�ꌩ�R���|�[�l���g�̊O�Ƀ|�[�g���o�Ă���悤�Ɍ����邽�߂ɁA�R���|�[�l���g�̃{�f�B�̃h���[�C���O�͈̔͂����߂邱�ƂŎ������Ă��邽�߁A����Ȏ����ɂȂ��Ă���B
 */
public class ComponentEditPart extends AbstractEditPart {

	/**
	 * �R���|�[�l���g�̎���ƃR���|�[�l���g�̃{�f�B�܂ł̃X�y�[�X
	 */
	public static final int SPACE = 7;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	private ComponentFloatingLabel componentLabel;

	/**
	 * �R���X�g���N�^
	 * 
	 * @param actionRegistry
	 *            ActionRegistry
	 */
	public ComponentEditPart(ActionRegistry actionRegistry) {
		super(actionRegistry);
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	protected IFigure createFigure() {
		Component component = getModel();

		Figure result = new Panel() {

			@Override
			/**
			 * {@inheritDoc}
			 */
			protected boolean useLocalCoordinates() {
				return true;
			}

			@Override
			/**
			 * {@inheritDoc}
			 * <p>
			 * �R���|�[�l���g�̊O�Ƀ|�[�g���o�Ă���悤�Ɍ����邽�߂ɁA�R���|�[�l���g�̃{�f�B�̃h���[�C���O�͈̔͂����߂Ă���
			 */
			protected void paintFigure(Graphics graphics) {
				if (isOpaque()) {
					Rectangle bound = new Rectangle(getBounds());
					graphics.fillRectangle(bound.expand(-SPACE, -SPACE));

					Color saveForegroundColor = graphics.getForegroundColor();
					// graphics.setForegroundColor(ColorConstants.black);
					graphics.drawRectangle(bound);
					graphics.setForegroundColor(saveForegroundColor);
				}
			}

			@Override
			/**
			 * {@inheritDoc}
			 * <p>
			 * �R���|�[�l���g�̊O�Ƀ|�[�g���o�Ă���悤�Ɍ����邽�߁A�����
			 */
			protected void paintBorder(Graphics graphics) {
				// void
			}

			@Override
			/**
			 * {@inheritDoc}
			 * <p>
			 * �R���|�[�l���g�̐��񂪕ύX����邽�тɁA���x�����ړ�������B
			 * �i�Ӗ��̕������炷��΂��܂�悭�Ȃ����A�t�@�C�����ɕ��Ă���̂ł����Ɏ����ɂ����j
			 */
			public void setBounds(Rectangle rect) {
				super.setBounds(rect);

				Rectangle newComponentLabelRectangle = componentLabel
						.getTextBounds().getCopy();
				newComponentLabelRectangle.x = rect.getCenter().x
						- componentLabel.getTextBounds().width / 2;
				newComponentLabelRectangle.y = rect.getBottom().y;
				
				componentLabel.setBounds(newComponentLabelRectangle);

				propertyChangeSupport.firePropertyChange("Bounds", null, rect);
			}

		};

		result.addMouseListener(new MouseListener.Stub() {
			@Override
			/**
			 * {@inheritDoc}
			 * <p>
			 * �R���|�[�l���g���E�N���b�N�i+Shift�j���āA������ϊ�����@�\�̎���
			 */
			public void mousePressed(MouseEvent me) {
				if (me.button == 2) { // right click
					IAction action;
					if (me.getState() == MouseEvent.SHIFT) {
						action = getActionRegistry()
								.getAction(
										ChangeComponentDirectionAction.VERTICAL_DIRECTION_ACTION_ID);
					} else {
						action = getActionRegistry()
								.getAction(
										ChangeComponentDirectionAction.HORIZON_DIRECTION_ACTION_ID);
					}

					action.run();
				}
			}
		});

		ComponentLayout layout = new ComponentLayout(getModel());
		result.setLayoutManager(layout);

		result.setBackgroundColor(ColorConstants.orange);

		// ���ӁFComponentLabel�̐e��SystemDiagram
		componentLabel = new ComponentFloatingLabel(
				((AbstractGraphicalEditPart) getParent()).getFigure());
		componentLabel.setText(getModel().getInstanceNameL());
		componentLabel.setSize(30, 10);

		return result;
	}

	/**
	 * �ݒ�}�l�[�W�����Ď����郊�X�i
	 */
	PropertyChangeListener preferenceChangeListener = new PropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent evt) {
			refreshConponent();
		}
	};

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void activate() {
		super.activate();

		PreferenceManager.getInstance().addPropertyChangeListener(
				preferenceChangeListener);
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public void deactivate() {
		componentLabel.deactivate();
		super.deactivate();

		PreferenceManager.getInstance().removePropertyChangeListener(
				preferenceChangeListener);
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	protected void createEditPolicies() {
		installEditPolicy(EditPolicy.COMPONENT_ROLE,
				new ComponentComponentEditPolicy());

		installEditPolicy(EditPolicyConstraint.CHANGE_DIRECTION_ROLE,
				new ChangeDirectionEditPolicy());
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	protected void refreshVisuals() {
		getFigure().setBackgroundColor(getNewBodyColor());

		getFigure().setForegroundColor(getNewBorderColor());

		Rectangle modelRec = Draw2dUtil.toDraw2dRectangle(getModel()
				.getConstraint());

		Rectangle newRectangle = modelRec.getCopy();

		((GraphicalEditPart) getParent()).setLayoutConstraint(this,
				getFigure(), newRectangle);
	}

	/**
	 * �ŐV�̃{�[�_�[���̐F���擾����
	 * 
	 * @return
	 */
	public Color getNewBorderColor() {
		Color exexucitonContextColor = PreferenceManager.getInstance()
				.getColor(PreferenceManager.COLOR_RTC_STATE_UNKNOWN);
		if (getModel().getExecutionContextState() == ExecutionContext.STATE_RUNNING) {
			exexucitonContextColor = PreferenceManager.getInstance().getColor(
					PreferenceManager.COLOR_RTC_EXECUTION_CONTEXT_RUNNING);
		} else if (getModel().getExecutionContextState() == ExecutionContext.STATE_STOPPED) {
			exexucitonContextColor = PreferenceManager.getInstance().getColor(
					PreferenceManager.COLOR_RTC_EXECUTION_CONTEXT_STOPPED);
		} else if (getModel().getExecutionContextState() == ExecutionContext.STATE_UNKNOWN) {
			exexucitonContextColor = PreferenceManager.getInstance().getColor(
					PreferenceManager.COLOR_RTC_STATE_UNKNOWN);
		}

		return exexucitonContextColor;
	}

	/**
	 * �ŐV�̃{�f�B���̐F���擾����
	 * 
	 * @return
	 */
	public Color getNewBodyColor() {
		Color stateColor = PreferenceManager.getInstance().getColor(
				PreferenceManager.COLOR_RTC_STATE_UNKNOWN);
		if (getModel().getComponentState() == LifeCycleState.RTC_ACTIVE) {
			stateColor = PreferenceManager.getInstance().getColor(
					PreferenceManager.COLOR_RTC_STATE_ACTIVE);
		} else if (getModel().getComponentState() == LifeCycleState.RTC_CREATED) {
			stateColor = PreferenceManager.getInstance().getColor(
					PreferenceManager.COLOR_RTC_STATE_CREATED);
		} else if (getModel().getComponentState() == LifeCycleState.RTC_ERROR) {
			stateColor = PreferenceManager.getInstance().getColor(
					PreferenceManager.COLOR_RTC_STATE_ERROR);
		} else if (getModel().getComponentState() == LifeCycleState.RTC_INACTIVE) {
			stateColor = PreferenceManager.getInstance().getColor(
					PreferenceManager.COLOR_RTC_STATE_INACTIVE);
		} else if (getModel().getComponentState() == LifeCycleState.RTC_UNKNOWN) {
			stateColor = PreferenceManager.getInstance().getColor(
					PreferenceManager.COLOR_RTC_STATE_UNKNOWN);
		}

		return stateColor;
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	public Component getModel() {
		return (Component) super.getModel();
	}

	/**
	 * {@inheritDoc}
	 */
	public void notifyChanged(Notification notification) {
		if (CorePackage.eINSTANCE.getModelElement_Constraint().equals(
				notification.getFeature())
				|| ComponentPackage.eINSTANCE.getComponent_ComponentState()
						.equals(notification.getFeature())
				|| ComponentPackage.eINSTANCE
						.getComponent_ExecutionContextState().equals(
								notification.getFeature())
				|| ComponentPackage.eINSTANCE.getComponent_OutportDirection()
						.equals(notification.getFeature())) {
			refreshConponent();
		}
	}

	public void refreshConponent() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (isActive()) {
					refresh();
					refreshChildren();
					getFigure().invalidate();
				}
			}
		});
	}

	@Override
	/**
	 * {@inheritDoc}
	 */
	protected List getModelChildren() {
		List result = new ArrayList();
		result.addAll(getModel().getInports());
		result.addAll(getModel().getOutports());
		result.addAll(getModel().getServiceports());

		return result;
	}

	/**
	 * �R���|�[�l���gFigure�̕ύX�̒ʒm���s�����X�i��o�^����
	 * 
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * �R���|�[�l���gFigure�̕ύX�̒ʒm���s�����X�i���폜����
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/**
	 * �V�X�e���_�C�A�O�����̃R���|�[�l���g�ɕ\������郉�x��
	 */
	public class ComponentFloatingLabel extends Label {

		/**
		 * �R���X�g���N�^
		 * 
		 * @param parentFigure
		 *            �e�t�B�M���A
		 */
		public ComponentFloatingLabel(IFigure parentFigure) {
			setParent(parentFigure);
			parentFigure.add(this);
		}

		/**
		 * �폜����ꍇ�ɌĂяo����邱�Ƃ��Ӑ}����
		 */
		public void deactivate() {
			getParent().remove(this);
		}

		@Override
		/**
		 * {@inheritDoc}
		 */
		public boolean isFocusTraversable() {
			return false;
		}

		@Override
		/**
		 * {@inheritDoc}
		 */
		public boolean isRequestFocusEnabled() {
			return false;
		}

		@Override
		/**
		 * {@inheritDoc}
		 */
		protected boolean isMouseEventTarget() {
			return false;
		}

	}

}
