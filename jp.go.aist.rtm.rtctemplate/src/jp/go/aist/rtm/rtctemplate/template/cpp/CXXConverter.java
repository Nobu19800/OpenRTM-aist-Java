package jp.go.aist.rtm.rtctemplate.template.cpp;

import java.io.File;

import jp.go.aist.rtm.rtctemplate.generator.param.GeneratorParam;
import jp.go.aist.rtm.rtctemplate.generator.param.idl.ServiceClassParam;

/**
 * CXX�\�[�X���o�͂���ۂɎg�p����郆�[�e�B���e�B
 */
public class CXXConverter {

	/**
	 * �T�[�r�X�X�^�u�����擾����
	 * 
	 * @param serviceClassParam
	 *            ServiceClassParam
	 * @return
	 */
	public static StringBuffer getSvcStubName(
			ServiceClassParam serviceClassParam) {
		StringBuffer result = new StringBuffer();

		result.append(serviceClassParam.getName());
		if (serviceClassParam.getParent().getServiceStubSuffix() != null
				&& !serviceClassParam.getParent().getServiceStubSuffix()
						.equals(""))
			result.append(serviceClassParam.getParent().getServiceStubSuffix());
		else
			result.append(GeneratorParam.DEFAULT_SVC_STUB_SUFFIX);
		return result;
	}

	/**
	 * �T�[�r�X�X�P���g�������擾����
	 * 
	 * @param serviceClassParam
	 *            ServiceClassParam
	 * @return
	 */
	public static StringBuffer getSvcSkelName(
			ServiceClassParam serviceClassParam) {
		StringBuffer result = new StringBuffer();

		result.append(serviceClassParam.getName());
		if (serviceClassParam.getParent().getServiceSkelSuffix() != null
				&& !serviceClassParam.getParent().getServiceSkelSuffix()
						.equals(""))
			result.append(serviceClassParam.getParent().getServiceSkelSuffix());
		else
			result.append(GeneratorParam.DEFAULT_SVC_SKEL_SUFFIX);
		return result;
	}

	/**
	 * �T�[�r�X�C���v�������擾����
	 * 
	 * @param serviceClassParam
	 *            ServiceClassParam
	 * @return
	 */
	public static StringBuffer getSvcImplName(
			ServiceClassParam serviceClassParam) {
		StringBuffer result = new StringBuffer();

		result.append(serviceClassParam.getName());
		if (!serviceClassParam.getParent().getServiceImplSuffix().equals(""))
			result.append(serviceClassParam.getParent().getServiceImplSuffix());
		else
			result.append(GeneratorParam.DEFAULT_SVC_IMPL_SUFFIX);
		return result;
	}

	/**
	 * CPP�^����CORBA�^�ւ̃}�b�s���O���`����
	 * 
	 * @param strcpp
	 *            CPP�^
	 * @return CORBA�^
	 */
	public static String convCpp2CORBA(String strcpp) {
		String result = null;
		if (strcpp.equals("longlong"))
			result = "CORBA::LongLong";
		else if (strcpp.equals("long"))
			result = "CORBA::Long";
		else if (strcpp.equals("unsignedlong"))
			result = "CORBA::ULong";
		else if (strcpp.equals("unsignedlonglong"))
			result = "CORBA::ULongLong";
		else if (strcpp.equals("short"))
			result = "CORBA::Short";
		else if (strcpp.equals("unsignedshort"))
			result = "CORBA::UShort";
		else if (strcpp.equals("float"))
			result = "CORBA::Float";
		else if (strcpp.equals("double"))
			result = "CORBA::Double";
		else if (strcpp.equals("longdouble"))
			result = "CORBA::LongDouble";
		else if (strcpp.equals("char"))
			result = "CORBA::Char";
		else if (strcpp.equals("wchar"))
			result = "CORBA::WChar";
		else if (strcpp.equals("octet"))
			result = "CORBA::Octet";
		else if (strcpp.equals("boolean"))
			result = "CORBA::Boolean";
		else if (strcpp.equals("string"))
			result = "char*";
		else if (strcpp.equals("wstring"))
			result = "CORBA::WChar*";
		else if (strcpp.equals("any"))
			result = "CORBA::Any*";
		else if (strcpp.equals("void"))
			result = "void";
		else
			result = strcpp + "*";

		return result;
	}

	/**
	 * CPP�^����CORBA�^�ւ̃}�b�s���O���`����(�����p)
	 * 
	 * @param strType
	 *            CPP�^
	 * @param strDir
	 *            ���o��
	 * @return CORBA�^
	 */
	public static String convCpp2CORBAforArg(String strType, String strDir) {
		String result = null;
		
		result = convCpp2CORBA(strType);
		
		if(strType.equals("string")) {
			if(strDir.equals("in"))
				result = "const char*";
			else if(strDir.equals("out"))
				result = "CORBA::String_out";
			else if(strDir.equals("inout"))
				result = "char*&";
		} else if(strType.equals("wstring")) {
			if(strDir.equals("in"))
				result = "const CORBA::WChar*";
			else if(strDir.equals("out"))
				result = "CORBA::WString_out";
			else if(strDir.equals("inout"))
				result = "CORBA::WChar*&";
		} else if(strType.equals("any")) {
			if(strDir.equals("in"))
				result = "const CORBA::Any&";
			else if(strDir.equals("out"))
				result = "CORBA::Any_OUT_arg";
			else if(strDir.equals("inout"))
				result = "CORBA::Any&";
		} else {
			if(strDir.equals("out") || strDir.equals("inout"))
				result = result + "&";
		}

		return result;
	}

	/**
	 * �x�[�X�����擾����
	 * 
	 * @param fullName
	 * @return
	 */
	public static String getBasename(String fullName) {
		String[] split = fullName.split("\\.");
		return split[split.length - 1];
	}

	/**
	 * �p�b�P�[�W�̋�؂蕶�����u.�v����u::�v�ɕύX����
	 * @param fullName
	 * @return
	 */
	public static String convertDelimiter(String fullName) {
		return fullName.replaceAll("\\.", "::");
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
