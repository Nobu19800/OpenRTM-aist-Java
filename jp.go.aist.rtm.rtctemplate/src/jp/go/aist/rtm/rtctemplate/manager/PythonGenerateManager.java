package jp.go.aist.rtm.rtctemplate.manager;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.go.aist.rtm.rtctemplate.generator.GeneratedResult;
import jp.go.aist.rtm.rtctemplate.generator.param.GeneratorParam;
import jp.go.aist.rtm.rtctemplate.generator.param.RtcParam;
import jp.go.aist.rtm.rtctemplate.generator.param.idl.IdlFileParam;
import jp.go.aist.rtm.rtctemplate.generator.param.idl.ServiceClassParam;
import jp.go.aist.rtm.rtctemplate.template.TemplateHelper;
import jp.go.aist.rtm.rtctemplate.template.TemplateUtil;
import jp.go.aist.rtm.rtctemplate.template.python.PythonConverter;

/**
 * CXX�t�@�C���̏o�͂𐧌䂷��}�l�[�W��
 */
public class PythonGenerateManager implements GenerateManager {

	/**
	 * �t�@�C�����o�͂���
	 * 
	 * @param generatorParam
	 * @return �o�͌��ʂ̃��X�g
	 */
	public List<GeneratedResult> doGenerate(GeneratorParam generatorParam) {
		InputStream ins = null;
		boolean blnHit = false;

		List<GeneratedResult> result = new ArrayList<GeneratedResult>();
		List<String> allIdlPathes = new ArrayList<String>();
		List<IdlFileParam> allIdlFileParams = new ArrayList<IdlFileParam>();
		List<IdlFileParam> providerIdlFileParams = new ArrayList<IdlFileParam>();

		for (RtcParam rtcParam : generatorParam.getRtcParams()) {
			if (rtcParam.isLanguageExist(RtcParam.LANG_PYTHON)) {
				result = generatePythonSource(rtcParam, result);
				result = generateCommonExtend(rtcParam, result);

				for( String idlFile : rtcParam.getProviderIdlPathes() ) {
					if( !allIdlPathes.contains(idlFile) )
						allIdlPathes.add(idlFile);
						allIdlFileParams.add(new IdlFileParam(idlFile, generatorParam));
				}
				providerIdlFileParams = new ArrayList<IdlFileParam>(allIdlFileParams);
				for( String idlFile : rtcParam.getConsumerIdlPathes() ) {
					if( !allIdlPathes.contains(idlFile) )
						allIdlPathes.add(idlFile);
						allIdlFileParams.add(new IdlFileParam(idlFile, generatorParam));
				}
				blnHit = true;
			}
		}
		if( blnHit) {
			//IDL�t�@�C�����ɋL�q����Ă���ServiceClassParam��ݒ肷��
			for( IdlFileParam idlFileParam : allIdlFileParams ) {
				for (ServiceClassParam serviceClassParam : generatorParam.getServiceClassParams()) {
					if( idlFileParam.getIdlPath().equals(serviceClassParam.getIdlPath()) )
						idlFileParam.addServiceClassParams(serviceClassParam);
				}
			}
			Map contextMap = new HashMap();
			for (IdlFileParam idlFileParam : allIdlFileParams) {
				result = generateSVCIDLSource(idlFileParam, contextMap, result);
			}
			if( allIdlFileParams.size() > 0 ) {
				contextMap = new HashMap();
				result = generateGlobalInitSource(allIdlFileParams, contextMap, result, generatorParam.getOutputDirectory());
				result = generateGlobalPOAInitSource(allIdlFileParams, contextMap, result, generatorParam.getOutputDirectory());
			}
			for (RtcParam rtcParam : generatorParam.getRtcParams()) {
				for (IdlFileParam idlFileParam : providerIdlFileParams) {
						contextMap = new HashMap();
						result = generateSVCIDLExampleSource(idlFileParam, contextMap, result);
				}
			}
	
			try {
				if( ins != null) ins.close();
			} catch (Exception e) {
				throw new RuntimeException(e); // system error
			}
		}

		return result;
	}
	
	/**
	 * Python�R�[�h�𐶐�����
	 * 
	 * @param rtcParam	�����p�p�����[�^
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generatePythonSource(RtcParam rtcParam, List<GeneratedResult> result) {
		InputStream ins = null;

		Map contextMap = new HashMap();
		contextMap.put("rtcParam", rtcParam);
		contextMap.put("tmpltHelper", new TemplateHelper());
		contextMap.put("pyConv", new PythonConverter());

		ins = PythonGenerateManager.class.getClassLoader()
			.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/python/Py_src.template");
		result.add(TemplateUtil.createGeneratedResult(ins, contextMap, rtcParam.getName() + ".py"));

		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}
	
	/**
	 * IDL�ɑΉ�����Service�t�@�C���𐶐�����
	 * 
	 * @param serviceParam	�����Ώۂ̃T�[�r�X�p�����[�^
	 * @param contextMap	���������
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generateSVCIDLSource(IdlFileParam idlFileParam, Map contextMap, List<GeneratedResult> result) {
		InputStream ins = null;

		contextMap.put("pyConv", new PythonConverter());
		contextMap.put("tmpltHelper", new TemplateHelper());
		contextMap.put("idlFileParam", idlFileParam);

		ins = PythonGenerateManager.class.getClassLoader()	
				.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/python/Py_SVC_IDL_src.template");
		result.add(TemplateUtil.createGeneratedResult(ins, contextMap, 
				new TemplateHelper().getFilenameNoExt(idlFileParam.getIdlPath()) + "_idl.py"));

		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}
	
	/**
	 * IDL�ɑΉ�����ServiceSample�t�@�C���𐶐�����
	 * 
	 * @param idlFileParm	�����Ώۂ�IDL�t�@�C���p�����[�^
	 * @param contextMap	���������
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generateSVCIDLExampleSource(IdlFileParam idlFileParam, Map contextMap, List<GeneratedResult> result) {
		InputStream ins = null;

		contextMap.put("pyConv", new PythonConverter());

		ins = PythonGenerateManager.class.getClassLoader()	
				.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/python/Py_SVC_IDL_Example_src.template");
		contextMap.put("idlFileParam", idlFileParam);
		result.add(TemplateUtil.createGeneratedResult(ins, contextMap, 
									idlFileParam.getIdlFileNoExt() + "_idl_example.py"));
		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}
	
	/**
	 * Global IDL�pinit�t�@�C���𐶐�����
	 * 
	 * @param idlFileParm	�����Ώۂ�IDL�t�@�C���p�����[�^
	 * @param contextMap	���������
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generateGlobalInitSource(List<IdlFileParam> idlFileParams, Map contextMap, List<GeneratedResult> result, String outDir) {
		InputStream ins = null;

		contextMap.put("idlFileParams", idlFileParams);
		contextMap.put("tmpltHelper", new TemplateHelper());

		ins = PythonGenerateManager.class.getClassLoader()	
				.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/python/Py_Global_Init_src.template");
		File targetDirectory = new File(outDir + "\\_GlobalIDL");
		if( !targetDirectory.isDirectory() ) {
			targetDirectory.mkdir();
        }
		result.add(TemplateUtil.createGeneratedResult(ins, contextMap, "\\_GlobalIDL\\__init__.py"));

		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}
	
	/**
	 * Global IDL POA�pinit�t�@�C���𐶐�����
	 * 
	 * @param idlFileParm	�����Ώۂ�IDL�t�@�C���p�����[�^
	 * @param contextMap	���������
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generateGlobalPOAInitSource(List<IdlFileParam> idlFileParams, Map contextMap, List<GeneratedResult> result, String outDir) {
		InputStream ins = null;

		contextMap.put("idlFileParams", idlFileParams);
		contextMap.put("tmpltHelper", new TemplateHelper());

		ins = PythonGenerateManager.class.getClassLoader()	
				.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/python/Py_Global_POA_Init_src.template");
		File targetDirectory = new File(outDir + "\\_GlobalIDL__POA");
		if( !targetDirectory.isDirectory() ) {
			targetDirectory.mkdir();
        }
		result.add(TemplateUtil.createGeneratedResult(ins, contextMap, "\\_GlobalIDL__POA\\__init__.py"));

		try {
			if( ins != null) ins.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // system error
		}

		return result;
	}
	
	protected List<GeneratedResult> generateCommonExtend(RtcParam rtcParam, List<GeneratedResult> result) {
		return result;		
	}
}
