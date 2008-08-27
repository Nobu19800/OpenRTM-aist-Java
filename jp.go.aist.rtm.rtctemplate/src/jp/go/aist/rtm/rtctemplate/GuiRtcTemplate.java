package jp.go.aist.rtm.rtctemplate;

import java.util.HashMap;

import jp.go.aist.rtm.rtctemplate.Generator.MergeHandler;
import jp.go.aist.rtm.rtctemplate.generator.GeneratedResult;
import jp.go.aist.rtm.rtctemplate.generator.param.GeneratorParam;
import jp.go.aist.rtm.rtctemplate.generator.parser.MergeBlockParser;
import jp.go.aist.rtm.rtctemplate.manager.CXXGenerateManager;
import jp.go.aist.rtm.rtctemplate.manager.CommonGenerateManager;
import jp.go.aist.rtm.rtctemplate.manager.GenerateManager;
import jp.go.aist.rtm.rtctemplate.manager.PythonGenerateManager;
import jp.go.aist.rtm.rtctemplate.ui.compare.CompareResultDialog;
import jp.go.aist.rtm.rtctemplate.ui.compare.CompareTarget;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

/**
 * GUI��RtcTemplate�����s����ۂ̃��C���ƂȂ�N���X
 */
public class GuiRtcTemplate {

	Generator generator = new Generator();
	/**
	 * �W�F�l���[�g�E�}�l�[�W����ǉ�����
	 * 
	 * @param genManager�@�����Ώۂ̃W�F�l���[�g�E�}�l�[�W��
	 */
	public void addGenerateManager(String managerKey, GenerateManager genManager) {
		generator.addGenerateManager(managerKey, genManager);
	}
	/**
	 * �W�F�l���[�g�E�}�l�[�W�����N���A����
	 */
	public void clearGenerateManager() {
		generator.clearGenerateManager();
	}
	/**
	 * �W�F�l���[�g���s���A�t�@�C���o�͂��s��
	 * 
	 * @param generatorParam
	 *            �p�����[�^
	 */
	public void doGenerateWrite(GeneratorParam generatorParam) {

//		Generator generator = new Generator();

		try {
			generator.doGenerateWrite(generatorParam, new MergeHandler() {
				public int getSelectedProcess(GeneratedResult generatedResult,
						String originalFileContents) {
					return compareByDialog(generatedResult,
							originalFileContents);
				}
			});

			MessageDialog.openInformation(PlatformUI.getWorkbench()
					.getDisplay().getActiveShell(), "Information",
					"Generate success.");
		} catch (Exception e) {
			MessageDialog.openError(PlatformUI.getWorkbench().getDisplay()
					.getActiveShell(), "Error", e.getMessage());
		}
	}

	private int compareByDialog(GeneratedResult generatedResult,
			String originalFileContents) {
		CompareTarget target = new CompareTarget();
		target.setTargetName(generatedResult.getName());
		target.setOriginalSrc(originalFileContents);
		target.setGenerateSrc(generatedResult.getCode());
		target.setCanMerge(MergeBlockParser.parse(generatedResult.getCode())
				.equals(MergeBlockParser.parse(originalFileContents)) == false);

		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
		Shell shell = window.getShell();

		return new CompareResultDialog(shell, target).open();
	}

}
