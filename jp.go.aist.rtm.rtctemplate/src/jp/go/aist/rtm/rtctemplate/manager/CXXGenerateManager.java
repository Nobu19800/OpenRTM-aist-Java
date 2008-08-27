package jp.go.aist.rtm.rtctemplate.manager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.go.aist.rtm.rtctemplate.generator.GeneratedResult;
import jp.go.aist.rtm.rtctemplate.generator.param.GeneratorParam;
import jp.go.aist.rtm.rtctemplate.generator.param.RtcParam;
import jp.go.aist.rtm.rtctemplate.generator.param.ServiceReferenceParam;
import jp.go.aist.rtm.rtctemplate.generator.param.idl.IdlFileParam;
import jp.go.aist.rtm.rtctemplate.generator.param.idl.ServiceClassParam;
import jp.go.aist.rtm.rtctemplate.template.TemplateUtil;
import jp.go.aist.rtm.rtctemplate.template.cpp.CXXConverter;

/**
 * CXX�t�@�C���̏o�͂𐧌䂷��}�l�[�W��
 */
public class CXXGenerateManager implements GenerateManager {

	/**
	 * �t�@�C�����o�͂���
	 * 
	 * @param generatorParam	�����p�p�����[�^
	 * @return �o�͌��ʂ̃��X�g
	 */
	public List<GeneratedResult> doGenerate(GeneratorParam generatorParam) {
		InputStream ins = null;

		List<String> allIdlPathes = new ArrayList<String>();
		List<IdlFileParam> allIdlFileParams = new ArrayList<IdlFileParam>();

		List<GeneratedResult> result = new ArrayList<GeneratedResult>();
		for (RtcParam rtcParam : generatorParam.getRtcParams()) {
			Map contextMap = new HashMap();
			contextMap.put("rtcParam", rtcParam);
			contextMap.put("cXXConv", new CXXConverter());
			if (rtcParam.isLanguageExist(RtcParam.LANG_CPP)) {
				result = generateCompSource(rtcParam, result);

				if (rtcParam.getName() != null) {
					result = generateMakeFile(rtcParam, contextMap, result);
					result = generateRTCHeader(rtcParam, contextMap, result);
					result = generateRTCSource(rtcParam, result);
					result = generateRTCExtend(rtcParam, result);

					for( String idlFile : rtcParam.getProviderIdlPathes() ) {
						if( !allIdlPathes.contains(idlFile) )
							allIdlPathes.add(idlFile);
							allIdlFileParams.add(new IdlFileParam(idlFile, generatorParam));
					}
				}
			}
		}

		//IDL�t�@�C�����ɋL�q����Ă���ServiceClassParam��ݒ肷��
		for( IdlFileParam idlFileParam : allIdlFileParams ) {
			for (ServiceClassParam serviceClassParam : generatorParam.getServiceClassParams()) {
				if( idlFileParam.getIdlPath().equals(serviceClassParam.getIdlPath()) )
					idlFileParam.addServiceClassParams(serviceClassParam);
			}
		}

		//Provider�ɎQ�Ƃ���Ă���ServiceClassParam���쐬����
		Set<ServiceClassParam> providerRefenencedServiceClassParam = new HashSet<ServiceClassParam>();
		for (RtcParam param : generatorParam.getRtcParams()) {
			List<ServiceReferenceParam> serviceReferenceParamList = new ArrayList<ServiceReferenceParam>();
			serviceReferenceParamList.addAll(param.getProviderReferences());

			for (ServiceReferenceParam serviceReferenceParam : serviceReferenceParamList) {
				ServiceClassParam find = null;
				for (ServiceClassParam serviceClassParam : generatorParam
						.getServiceClassParams()) {
					if (serviceReferenceParam.getType().equals(
							serviceClassParam.getName())) {
						find = serviceClassParam;
						break;
					}
				}

				if (find != null) {
					providerRefenencedServiceClassParam.add(find);
				}
			}
		}

		for (IdlFileParam idlFileParm : allIdlFileParams) {
			Map contextMap = new HashMap();
			contextMap.put("idlFileParam", idlFileParm);
			contextMap.put("cXXConv", new CXXConverter());
			
			result = generateSVCHeader(idlFileParm, contextMap, result);
			result = generateSVCSource(idlFileParm, contextMap, result);
		}

		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}
	
	/**
	 * Standalone component�𐶐�����
	 * 
	 * @param rtcParam	�����p�p�����[�^
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generateCompSource(RtcParam rtcParam, List<GeneratedResult> result) {
		InputStream ins = null;

		ins = CXXGenerateManager.class.getClassLoader()	
			.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/cpp/CXX_Comp_src.template");
		result.add(TemplateUtil.createGeneratedResult(ins, "rtcParam", rtcParam, rtcParam.getName() + "Comp.cpp"));

		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}
	
	/**
	 * Makefile�𐶐�����
	 * 
	 * @param rtcParam	�����p�p�����[�^
	 * @param contextMap	���������
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generateMakeFile(RtcParam rtcParam, Map contextMap, List<GeneratedResult> result) {
		InputStream ins = null;

		ins = CXXGenerateManager.class.getClassLoader()	
			.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/cpp/CXX_Makefile_src.template");
		result.add(TemplateUtil.createGeneratedResult(ins, contextMap, "Makefile." + rtcParam.getName()));

		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}
	
	/**
	 * RTC�̃w�b�_�E�t�@�C���𐶐�����
	 * 
	 * @param rtcParam	�����p�p�����[�^
	 * @param contextMap	���������
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generateRTCHeader(RtcParam rtcParam, Map contextMap, List<GeneratedResult> result) {
		InputStream ins = null;

		ins = CXXGenerateManager.class.getClassLoader()	
			.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/cpp/CXX_RTC_Header_src.template");
		result.add(TemplateUtil.createGeneratedResult(ins, contextMap, rtcParam.getName() + ".h"));

		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}
	
	/**
	 * RTC�̃\�[�X�E�t�@�C���𐶐�����
	 * 
	 * @param rtcParam	�����p�p�����[�^
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generateRTCSource(RtcParam rtcParam, List<GeneratedResult> result) {
		InputStream ins = null;

		ins = CXXGenerateManager.class.getClassLoader()	
			.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/cpp/CXX_RTC_Source_src.template");
		result.add(TemplateUtil.createGeneratedResult(ins, "rtcParam", rtcParam, rtcParam.getName() + ".cpp"));

		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}
	
	protected List<GeneratedResult> generateRTCExtend(RtcParam rtcParam, List<GeneratedResult> result) {
		return result;		
	}
	/**
	 * Service implementation header�𐶐�����
	 * 
	 * @param idlFileParm	�����Ώۂ�IDL�t�@�C���p�����[�^
	 * @param contextMap	���������
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generateSVCHeader(IdlFileParam idlFileParm, Map contextMap, List<GeneratedResult> result) {
		InputStream ins = null;

		ins = CXXGenerateManager.class.getClassLoader()	
			.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/cpp/CXX_SVC_Header_src.template");
		result.add(TemplateUtil.createGeneratedResult(ins, contextMap, 
								new CXXConverter().getBasename(idlFileParm.getIdlFileNoExt())
									+ idlFileParm.getParent().getServiceImplSuffix() + ".h"));

		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}

	/**
	 * Service implementation code�𐶐�����
	 * 
	 * @param idlFileParm	�����Ώۂ�IDL�t�@�C���p�����[�^
	 * @param contextMap	���������
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generateSVCSource(IdlFileParam idlFileParm, Map contextMap, List<GeneratedResult> result) {
		InputStream ins = null;

		ins = CXXGenerateManager.class.getClassLoader()	
			.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/cpp/CXX_SVC_Source_src.template");
		result.add(TemplateUtil.createGeneratedResult(ins, contextMap, 
								new CXXConverter().getBasename(idlFileParm.getIdlFileNoExt())
									+ idlFileParm.getParent().getServiceImplSuffix() + ".cpp"));

		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}
	protected List<GeneratedResult> generateSVCExtend(IdlFileParam idlFileParm, Map contextMap, List<GeneratedResult> result) {
		return result;		
	}
}
