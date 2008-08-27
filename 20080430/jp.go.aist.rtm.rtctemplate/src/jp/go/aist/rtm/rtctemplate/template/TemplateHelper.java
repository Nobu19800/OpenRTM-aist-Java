package jp.go.aist.rtm.rtctemplate.template;

import java.io.File;

/**
 * �e���v���[�g���o�͂���ۂɎg�p�����w���p�[
 */
public class TemplateHelper {

	/**
	 * �x�[�X�����擾����
	 * 
	 * @param fullName
	 * @return
	 */
	public static String getBasename(String fullName) {
		String[] split = fullName.split("::");
		return split[split.length - 1];
	}

	/**
	 * �t�@�C�������擾����
	 * 
	 * @param fullPath
	 * @return
	 */
	public static String getFileName(String fullPath) {
		File file = new File(fullPath);
		return file.getName();
	}
	/**
	 * �g���q�����t�@�C�������擾����
	 * 
	 * @param fullPath
	 * @return
	 */
	public static String getFilenameNoExt(String fullPath) {
		String fileName = getFileName(fullPath);

		if(fileName == null) return "";
		
		int index = fileName.lastIndexOf('.');
		if(index>0 && index<fileName.length()-1) {
			return fileName.substring(0,index);
		}
		return "";
	}
}
