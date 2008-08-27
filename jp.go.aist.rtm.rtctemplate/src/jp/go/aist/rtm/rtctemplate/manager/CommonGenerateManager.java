package jp.go.aist.rtm.rtctemplate.manager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.go.aist.rtm.rtctemplate.generator.GeneratedResult;
import jp.go.aist.rtm.rtctemplate.generator.param.GeneratorParam;
import jp.go.aist.rtm.rtctemplate.generator.param.RtcParam;
import jp.go.aist.rtm.rtctemplate.template.TemplateUtil;

/**
 * ��ʃt�@�C���̏o�͂𐧌䂷��}�l�[�W��
 */
public class CommonGenerateManager implements GenerateManager {

	/**
	 * �t�@�C�����o�͂���
	 * 
	 * @param generatorParam
	 * @return �o�͌��ʂ̃��X�g
	 */
	public List<GeneratedResult> doGenerate(GeneratorParam generatorParam) {
		List<GeneratedResult> result = new ArrayList<GeneratedResult>();

		for (RtcParam rtcParam : generatorParam.getRtcParams()) {
			result = generateReadMe(rtcParam, result);
			result = generateCommonExtend(rtcParam, result);
		}

		return result;
	}
	
	/**
	 * ReadMe�𐶐�����
	 * 
	 * @param rtcParam	�����p�p�����[�^
	 * @param contextMap	���������
	 * @param result	�������ʊi�[��
	 * @return �o�͌��ʂ̃��X�g
	 */
	protected List<GeneratedResult> generateReadMe(RtcParam rtcParam, List<GeneratedResult> result) {
		InputStream ins = null;

		ins = CommonGenerateManager.class.getClassLoader()
			.getResourceAsStream("jp/go/aist/rtm/rtctemplate/template/common/README_src.template");
		result.add(TemplateUtil.createGeneratedResult(ins, "rtcParam", rtcParam, "README." + rtcParam.getName()));

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
